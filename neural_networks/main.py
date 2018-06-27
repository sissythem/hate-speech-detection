import json
import FoldRunner as fr
from os.path import join
import numpy


def read_configurations():
    """
    On start up main function reads the configuration file (json), that includes
    the folds number, the path to the arff files, the dataset folder (binary or multi-class problem)
    the specific feature folder to be used, some configurations related to classification algorithms
    (such as the neighbors number for KNN algorithm or parameters for MLP model in keras), a list with
    the classifiers to be used and finally which library to be used for Neural Networks
    :return: a python dictionary with all information from the configuration file
    """
    with open('config.json') as json_data_file:
        data = json.load(json_data_file)
    return data


if __name__ == '__main__':
    # read the configurations
    data = read_configurations()
    num_folds = data["folds"]
    print("Running folds: ", data["path_to_instances"], data["dataset_folder"], "/", data["feature_folder"])
    results = {}
    for i in range(num_folds):
        # iterate to run classification for all folds
        results = fr.run_fold(data, i, results)
    for classifier in data["classifiers"]:
        # for each classifier specified in the configuration file, write the results in a file
        # the results include average for all folds in micro/marco precision, recall and F-Measure
        macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = numpy.mean(results[classifier], axis=0)
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
