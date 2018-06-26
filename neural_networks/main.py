import json
import FoldRunner as fr
from os.path import join
import numpy


def read_configurations():
    with open('config.json') as json_data_file:
        data = json.load(json_data_file)
    return data


if __name__ == '__main__':
    data = read_configurations()
    num_folds = data["folds"]
    print("Running folds: ", data["path_to_instances"], data["dataset_folder"], "/", data["feature_folder"])
    results = {}
    for i in range(num_folds):
        results = fr.run_fold(data, i, results)
    for classifier in data["classifiers"]:
        dat = numpy.asarray(list(results[classifier]))
        print(numpy.mean(dat, axis=0))
        filename = "Result_avg_" + classifier + ".txt"
        result_file = join(data["path_to_instances"], data["dataset_folder"], data["feature_folder"], filename)
        with open(result_file, 'w') as f:
            f.write(numpy.mean(dat, axis=0))
