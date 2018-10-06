import numpy as np


def read_libsvm(fname, nclass=3, ndim=180, one_hot=False):
    data = []
    for line in open(fname):
        tl = line.strip().split()

        feats = [0.0] * (ndim + 1)
        feats[0] = int(tl[0]) - 1
        for item in tl[1:]:
            key, val = item.split(":")
            feats[int(key)] = float(val)

        data.append(feats)

    tmp = np.array(data, np.float32)
    if one_hot:
        labs = np.zeros(shape=(len(tmp), nclass), dtype=np.int32)
        for i, v in enumerate(tmp[:, 0]):
            labs[i, int(v)] = 1.0
        return labs, tmp[:, 1:]
    else:
        return tmp


class Dataset(object):

    def __init__(self, data, nclass=3, max_epoch=10, batch_size=128, one_hot=True):
        self._data = data
        self.nclass = nclass
        self.one_hot = one_hot
        self.epoch = 0
        self.max_epoch = max_epoch
        self.batch_idx = 0
        self.curr_sample_idx = 0
        self.batch_size = batch_size
        self.__idxs = [i for i in range(len(data))]
        np.random.shuffle(self.__idxs)

    def __iter__(self):
        return self

    def __next__(self):
        if self.curr_sample_idx + self.batch_size > len(self._data):
            if self.epoch >= self.max_epoch:
                raise StopIteration()

            start = 0
            end = self.batch_size

            self.curr_sample_idx = end
            self.epoch += 1
            self.batch_idx = 0
            np.random.shuffle(self.__idxs)

            idxs = self.__idxs[start:end]

            if self.one_hot:
                return self.__mk_one_hot(self._data[idxs, 0]), self._data[idxs, 1:]
            else:
                return self._data[idxs, 0], self._data[idxs, 1:]
        else:
            start = self.curr_sample_idx
            end = self.batch_size + self.curr_sample_idx

            self.curr_sample_idx = end
            self.batch_idx += 1

            idxs = self.__idxs[start:end]

            if self.one_hot:
                return self.__mk_one_hot(self._data[idxs, 0]), self._data[idxs, 1:]
            else:
                return self._data[idxs, 0], self._data[idxs, 1:]

    def __mk_one_hot(self, labs):
        labs_one_hot = np.zeros(shape=(len(labs), self.nclass), dtype=np.int32)
        for i, v in enumerate(labs):
            labs_one_hot[i, int(v)] = 1.0

        return labs_one_hot


if __name__ == '__main__':
    tr_data = read_libsvm("data/dna.scale.tr")
    ds = Dataset(tr_data)

    for label, feat in ds:
        print(ds.epoch, ds.batch_idx, label, feat.shape)
