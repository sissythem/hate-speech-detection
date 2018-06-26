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
        macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = numpy.mean(results[classifier],
                                                                                                    axis=0)
        filename = "Result_avg_" + classifier + ".txt"
        result_file = join(data["path_to_instances"], data["dataset_folder"], data["feature_folder"], filename)
        with open(result_file, 'w') as f:
            macro_precision = "Macro Precision: " + str(macro_precision) + "\n"
            micro_precision = "Micro Precision: " + str(micro_precision) + "\n"
            macro_recall = "Macro Recall: " + str(macro_recall) + "\n"
            micro_recall = "Micro Recall: " + str(micro_recall) + "\n"
            macro_f = "Macro F-Measure: " + str(macro_f) + "\n"
            micro_f = "Micro F-Measure: " + str(micro_f) + "\n"
            f.write(macro_precision)
            f.write(micro_precision)
            f.write(macro_recall)
            f.write(micro_recall)
            f.write(macro_f)
            f.write(micro_f)

