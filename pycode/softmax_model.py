import tensorflow as tf

__all__ = ['placeholder', 'variables', 'inferDense', 'inferSparse']


def placeholder(num_feats: int, num_class: int):
    with tf.name_scope("Input"):
        # name = Input/x_data:0
        x_data = tf.placeholder(tf.float32, shape=(None, num_feats), name='x_data')
        # name = Input/labels:0
        labels = tf.placeholder(tf.float32, shape=(None, num_class), name='labels')

    return x_data, labels


def variables(num_feats: int, num_class: int):
    with tf.variable_scope('variable'):
        weight = tf.get_variable(name='weight', shape=(num_feats, num_class), dtype=tf.float32,
                                 initializer=tf.initializers.truncated_normal())
        bias = tf.get_variable(name='bias', shape=(num_class,), dtype=tf.float32,
                               initializer=tf.initializers.constant(value=0))

        global_steps = tf.Variable(0, trainable=False, name='global_steps')

    return weight, bias, global_steps


def inferDense(x_data, weight: tf.Tensor, bias: tf.Tensor):
    with tf.name_scope('infer'):
        logits = x_data @ weight + bias
        # name: predict/ArgMax:0
        pred = tf.argmax(tf.nn.softmax(logits), axis=1)

    return logits, pred


def inferSparse(index, value, embedding: tf.Tensor, bias: tf.Tensor):
    with tf.name_scope('infer'):
        logits = tf.nn.embedding_lookup_sparse(params=embedding, sp_ids=index, combiner='sum',
                                               sp_weights=value, partition_strategy='mod',
                                               name='lookup_sparse') + bias
        # name: predict/ArgMax:0
        pred = tf.argmax(tf.nn.softmax(logits), axis=1)

    return logits, pred


def loss_train(labels: tf.Tensor, logits: tf.Tensor, global_steps: tf.Tensor):
    loss = tf.reduce_mean(
        tf.nn.softmax_cross_entropy_with_logits_v2(labels=labels, logits=logits, name='entropy')
    )

    train_op = tf.train.AdamOptimizer().minimize(loss, global_step=global_steps)

    return train_op, loss


def calAcc(labels: tf.Tensor, pred: tf.Tensor):
    with tf.name_scope('accucy'):
        # name = accucy:Mean:0
        acc = tf.reduce_mean(tf.cast(
            tf.equal(tf.argmax(labels, axis=1), pred),
            tf.float32)
        )

    return acc
