import time
import numpy as np
import tensorflow as tf
from softmax_model import inferSparse, variables, loss_train, calAcc


class LibsvmHelper(object):
    MIN_AFTER_DEQUEUE = 10000
    _float_feature = lambda v: tf.train.Feature(float_list=tf.train.FloatList(value=v))
    _int_feature = lambda v: tf.train.Feature(int64_list=tf.train.Int64List(value=v))

    def __init__(self, num_feats, num_class, num_epochs, batch_size, num_threads):
        self.num_feats = num_feats
        self.num_class = num_class
        self.num_epochs = num_epochs
        self.batch_size = batch_size
        self.num_threads = num_threads

    @classmethod
    def trans_libsvm(cls, ipfname, opfname, label_start_zeros=False):
        writer = tf.python_io.TFRecordWriter(opfname)
        for line in open(ipfname):
            l = line.rstrip().split()
            if label_start_zeros:
                label = int(l[0])
            else:
                label = int(l[0]) - 1

            start = 1
            num_features = 0
            if ':' not in l[1]:
                num_features = int(l[1])
                start += 1

            indexes, values = [], []
            for item in l[start:]:
                index, value = item.split(':')
                indexes.append(int(index) - 1)
                values.append(float(value))

            example = tf.train.Example(features=tf.train.Features(feature={
                'label': LibsvmHelper._int_feature([label]),
                'num_features': LibsvmHelper._int_feature([num_features]),
                'index': LibsvmHelper._int_feature(indexes),
                'value': LibsvmHelper._float_feature(values)})
            )

            writer.write(example.SerializeToString())
        writer.close()

    def pipeline(self, filename_queue, graph: tf.Graph):
        with graph.as_default(), tf.name_scope("Pipeline"):
            filename_queue = tf.train.string_input_producer(
                filename_queue, num_epochs=num_epochs)

            reader = tf.TFRecordReader()
            _, serialized_example = reader.read(filename_queue)

            batch_serialized_examples = tf.train.shuffle_batch(
                [serialized_example],
                batch_size=self.batch_size,
                num_threads=self.num_threads,
                capacity=LibsvmHelper.MIN_AFTER_DEQUEUE + (self.num_threads + 1) * self.batch_size,
                # Ensures a minimum amount of shuffling of examples.
                min_after_dequeue=LibsvmHelper.MIN_AFTER_DEQUEUE)

            features = tf.parse_example(
                batch_serialized_examples,
                features={
                    'label': tf.FixedLenFeature([], tf.int64),
                    'num_features': tf.FixedLenFeature([], tf.int64),
                    'index': tf.VarLenFeature(tf.int64),
                    'value': tf.VarLenFeature(tf.float32),
                })

            return features['label'], features['index'], features['value']

    def parser(self, serialized):
        features = tf.parse_single_example(serialized, features={
            'label': tf.FixedLenFeature([], tf.int64),
            'num_features': tf.FixedLenFeature([], tf.int64),
            'index': tf.VarLenFeature(tf.int64),
            'value': tf.VarLenFeature(tf.float32),
        })

        return features['label'], features['index'], features['value']

    def ds_iterator(self, filename_queue):
        with tf.name_scope('Dataset'):
            ds_mapped = tf.data.TFRecordDataset(filename_queue).map(self.parser)
            ds = ds_mapped.repeat(self.num_epochs).shuffle(buffer_size=1024).batch(self.batch_size)
            iterator = ds.make_initializable_iterator()

        return iterator, iterator.initializer


if __name__ == '__main__':
    # trans_libsvm('data/dna.scale.t', 'data/dna.scale.tf.t')
    # trans_libsvm('data/dna.scale.tr', 'data/dna.scale.tf.tr')

    tf_files = ['data/dna.scale.tf.t', 'data/dna.scale.tf.tr']
    num_feats, num_class, num_epochs, batch_size, num_threads = 180, 3, 1000, 128, 2
    libsvmHelper = LibsvmHelper(num_feats, num_class, num_epochs, batch_size, num_threads)
    # label, index, value = libsvmHelper.pipeline(tf_files, tf.get_default_graph())
    iterator, initializer = libsvmHelper.ds_iterator(tf_files)
    label, index, value = iterator.get_next()

    weight, bias, global_steps = variables(num_feats, num_class)
    logits, pred = inferSparse(index, value, weight, bias)
    train_op, loss = loss_train(tf.one_hot(label, num_class, 1, 0), logits, global_steps)

    # The op for initializing the variables.
    init_op = tf.group(tf.global_variables_initializer(),
                       tf.local_variables_initializer(),
                       initializer)

    with tf.Session() as sess:
        sess.run(init_op)
        Writer = tf.summary.FileWriter(logdir='logs', graph=sess.graph)
        while True:
            try:
                _, loss_, gs = sess.run([train_op, loss, global_steps])
                if gs % 100 == 0:
                    print(loss_)
            except tf.errors.OutOfRangeError:
                print('Dataset Training Finished !')
                Writer.close()
                break
            except Exception as e:
                print(e)
                Writer.close()
                break

        # coord = tf.train.Coordinator()
        # threads = tf.train.start_queue_runners(sess=sess, coord=coord)
        # num_epochs, step = 0, 0
        #
        # Writer = tf.summary.FileWriter(logdir='logs', graph=sess.graph)
        #
        # try:
        #     while not coord.should_stop():
        #         start_time = time.time()
        #         # label_val, index_val, value_val = sess.run([label, index, value])
        #         _, loss_, gs = sess.run([train_op, loss, global_steps])
        #
        #         if gs % 100 == 0:
        #             print(loss_)
        #
        #         duration = time.time() - start_time
        #
        #         step += 1
        #         # coord.request_stop()
        #
        # except tf.errors.OutOfRangeError:
        #     print('Done training for %d epochs, %d steps.' % (num_epochs, step))
        # finally:
        #     # When done, ask the threads to stop.
        #     coord.request_stop()
        #
        #     # Wait for threads to finish.
        #     coord.join(threads)
        #     sess.close()
        #     Writer.close()
