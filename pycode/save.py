import os
import shutil
import numpy as np
import tensorflow as tf
from dataset import read_libsvm, Dataset
from softmax_model import placeholder, variables, inferDense, loss_train, calAcc


# https://blog.csdn.net/thriving_fcl/article/details/71423039
def save(save_dir: str, session: tf.Session):
    if os.path.exists(save_dir):
        shutil.rmtree(save_dir)
    sv = tf.train.Saver(write_version=tf.train.SaverDef.V2)
    sv.save(session, save_path=save_dir)


# https://blog.csdn.net/thriving_fcl/article/details/75213361
def save_model(saved_model_dir: str, session: tf.Session,
               x_data: tf.Tensor, labels: tf.Tensor, predict: tf.Tensor, acc: tf.Tensor):
    if os.path.exists(saved_model_dir):
        shutil.rmtree(saved_model_dir)

    builder = tf.saved_model.builder.SavedModelBuilder(saved_model_dir)

    signature_predict = tf.saved_model.signature_def_utils.build_signature_def(
        inputs={'x_data': tf.saved_model.utils.build_tensor_info(x_data),
                'labels': tf.saved_model.utils.build_tensor_info(labels)
                },
        outputs={'predict': tf.saved_model.utils.build_tensor_info(predict)},
        method_name='softmax_predict'
    )

    signature_acc = tf.saved_model.signature_def_utils.build_signature_def(
        inputs={'x_data': tf.saved_model.utils.build_tensor_info(x_data),
                'labels': tf.saved_model.utils.build_tensor_info(labels)
                },
        outputs={'acc': tf.saved_model.utils.build_tensor_info(acc)},
        method_name='softmax_acc'
    )

    builder.add_meta_graph_and_variables(
        session, tags=['simple_softmax'], signature_def_map={
            'softmax_predict': signature_predict,
            'softmax_acc': signature_acc
        })


    builder.save()


def train_save(ds: Dataset, t_labs: np.ndarray, t_data: np.ndarray, num_feats: int, num_class: int):
    x_data, labels = placeholder(num_feats, num_class)
    weight, bias, global_steps = variables(num_feats, num_class)
    logits, predict = inferDense(x_data, weight, bias)
    acc = calAcc(labels, predict)
    train_op, _ = loss_train(labels, logits, global_steps)

    init = tf.global_variables_initializer()

    with tf.Session() as sess:
        sess.run(init)
        writer = tf.summary.FileWriter('logs', sess.graph)

        for y_batch, x_batch in ds:
            _, gs = sess.run(fetches=[train_op, global_steps], feed_dict={
                x_data: x_batch, labels: y_batch
            })

            if gs % 200 == 0:
                acc_val = sess.run(fetches=[acc], feed_dict={
                    x_data: t_data, labels: t_labs
                })

                print(acc_val)
        save(save_dir='models/softmax', session=sess)
        writer.close()


def train_save_model(ds: Dataset, t_labs: np.ndarray, t_data: np.ndarray, num_feats: int, num_class: int):
    x_data, labels = placeholder(num_feats, num_class)
    weight, bias, global_steps = variables(num_feats, num_class)
    logits, predict = inferDense(x_data, weight, bias)
    acc = calAcc(labels, predict)
    train_op, _ = loss_train(labels, logits, global_steps)

    init = tf.global_variables_initializer()

    saved_model_dir = 'saved_model/softmax'
    with tf.Session() as sess:
        sess.run(init)
        writer = tf.summary.FileWriter('logs', sess.graph)

        for y_batch, x_batch in ds:
            _, gs = sess.run(fetches=[train_op, global_steps], feed_dict={
                x_data: x_batch, labels: y_batch
            })

            if gs % 200 == 0:
                acc_val = sess.run(fetches=[acc], feed_dict={
                    x_data: t_data, labels: t_labs
                })

                print(acc_val)
        save_model('softmax_saved', sess, x_data, labels, predict, acc)
        writer.close()


if __name__ == '__main__':
    n_feats, batch_size, n_class = 180, 128, 3
    tr_data = read_libsvm("data/dna.scale.tr")
    test_labs, test_data = read_libsvm("data/dna.scale.t", one_hot=True)
    train_ds = Dataset(tr_data, nclass=n_class, max_epoch=1000, batch_size=batch_size)

    train_save(train_ds, test_labs, test_data, num_feats=n_feats, num_class=n_class)
