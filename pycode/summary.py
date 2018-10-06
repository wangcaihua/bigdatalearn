import numpy as np
import tensorflow as tf
from dataset import read_libsvm, Dataset
from softmax_model import placeholder, variables, inferDense, loss_train, calAcc


def train_save(ds: Dataset, t_labs: np.ndarray, t_data: np.ndarray, num_feats: int, num_class: int):
    x_data, labels = placeholder(num_feats, num_class)
    weight, bias, global_steps = variables(num_feats, num_class)
    logits, predict = inferDense(x_data, weight, bias)
    acc = calAcc(labels, predict)
    train_op, loss = loss_train(labels, logits, global_steps)
    tf.summary.scalar('acc', acc)
    tf.summary.scalar('loss', loss)
    init = tf.global_variables_initializer()

    merged = tf.summary.merge_all()
    with tf.Session() as sess:
        sess.run(init)
        writer = tf.summary.FileWriter('logs', sess.graph)

        for y_batch, x_batch in ds:
            _, gs = sess.run(fetches=[train_op, global_steps], feed_dict={
                x_data: x_batch, labels: y_batch
            })

            if gs % 200 == 0:
                acc_val, summary = sess.run(fetches=[acc, merged], feed_dict={
                    x_data: t_data, labels: t_labs
                })
                print(type(summary))
                writer.add_summary(summary, gs)

                print(acc_val)
        writer.close()


if __name__ == '__main__':
    n_feats, batch_size, n_class = 180, 128, 3
    tr_data = read_libsvm("data/dna.scale.tr")
    test_labs, test_data = read_libsvm("data/dna.scale.t", one_hot=True)
    train_ds = Dataset(tr_data, nclass=n_class, max_epoch=1000, batch_size=batch_size)

    train_save(train_ds, test_labs, test_data, num_feats=n_feats, num_class=n_class)
