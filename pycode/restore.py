import numpy as np
import tensorflow as tf
from dataset import read_libsvm
from softmax_model import placeholder, variables, infer


def restore1(t_labs: np.ndarray, t_data: np.ndarray):
    with tf.Session() as sess:
        saver = tf.train.import_meta_graph('models/softmax.meta')
        graph = sess.graph
        saver.restore(sess, save_path='models/softmax')

        x_data = graph.get_tensor_by_name('Input/x_data:0')
        labels = graph.get_tensor_by_name('Input/labels:0')
        pred = graph.get_tensor_by_name('predict/ArgMax:0')
        acc = graph.get_tensor_by_name('accucy/Mean:0')

        predict, accuy = sess.run([pred, acc], feed_dict={x_data: t_data, labels: t_labs})

        print(accuy)


def restore2(t_labs: np.ndarray, t_data: np.ndarray, num_feats=180, num_class=3):
    x_data, labels = placeholder(num_feats, num_class)
    weight, bias, global_steps = variables(num_feats, num_class)
    logits, pred, acc = infer(x_data, labels, weight, bias)

    with tf.Session() as sess:
        saver = tf.train.Saver()
        saver.restore(sess, save_path='models/softmax')

        predict, accuy = sess.run([pred, acc], feed_dict={x_data: t_data, labels: t_labs})

        print(accuy)


def restore3(t_labs: np.ndarray, t_data: np.ndarray):
    saved_model_dir='softmax_saved'
    signature_key = 'softmax_acc'

    with tf.Session() as sess:
        meta_graph_def = tf.saved_model.loader.load(
            sess, ['simple_softmax'], saved_model_dir)

        signature = meta_graph_def.signature_def[signature_key]
        x_data_name = signature.inputs['x_data'].name
        labels_name = signature.inputs['labels'].name
        acc_name = signature.outputs['acc'].name

        graph = sess.graph
        x_data = graph.get_tensor_by_name(x_data_name)
        labels = graph.get_tensor_by_name(labels_name)
        acc = graph.get_tensor_by_name(acc_name)

        [accuy] = sess.run([acc], feed_dict={x_data: t_data, labels: t_labs})
        print(accuy)


if __name__ == '__main__':
    t_labs, t_data = read_libsvm("data/dna.scale.t", one_hot=True)
    restore3(t_labs, t_data)
