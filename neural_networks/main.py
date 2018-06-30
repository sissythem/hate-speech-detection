import json
import FoldRunner as fr
from os.path import join
import numpy
import math


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


def write_average(classifier, results, f):
    # for each classifier specified in the configuration file, write the results in a file
    # the results include average for all folds in micro/marco precision, recall and F-Measure
    macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = numpy.mean(
        results[classifier], axis=0)
    macro_precision = "Macro Precision: " + str(macro_precision) + "\n"
    micro_precision = "Micro Precision: " + str(micro_precision) + "\n"
    macro_recall = "Macro Recall: " + str(macro_recall) + "\n"
    micro_recall = "Micro Recall: " + str(micro_recall) + "\n"
    macro_f = "Macro F-Measure: " + str(macro_f) + "\n"
    micro_f = "Micro F-Measure: " + str(micro_f) + "\n"
    f.write("Average\n")
    f.write("==========\n")
    f.write(macro_precision)
    f.write(micro_precision)
    f.write(macro_recall)
    f.write(micro_recall)
    f.write(macro_f)
    f.write(micro_f)
    f.write("==================\n")


def write_std_dev(results, classifier, folds, f):
    macro_precision_mean, micro_precision_mean, macro_recall_mean, micro_recall_mean, macro_f_mean, micro_f_mean = numpy.mean(
        results[classifier], axis=0)
    sum_macro_precision = 0
    sum_macro_recall = 0
    sum_macro_f =0
    sum_micro_precision = 0
    sum_micro_recall = 0
    sum_micro_f = 0
    for tuple_res in results[classifier]:
        macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = tuple_res
        sum_macro_precision = sum_macro_precision + math.pow((macro_precision - macro_precision_mean), 2)
        sum_macro_recall = sum_macro_recall + math.pow((macro_recall - macro_recall_mean), 2)
        sum_macro_f = sum_macro_f + math.pow((macro_f - macro_f_mean), 2)
        sum_micro_precision = sum_micro_precision + math.pow((micro_precision - micro_precision_mean), 2)
        sum_micro_recall = sum_micro_recall + math.pow((micro_recall - micro_recall_mean), 2)
        sum_micro_f = sum_micro_f + math.pow((micro_f - micro_f_mean), 2)
    std_dev_macro_precision = math.sqrt(sum_macro_precision / (folds - 1))
    std_dev_micro_precision = math.sqrt(sum_micro_precision / (folds - 1))
    std_dev_macro_recall = math.sqrt(sum_macro_recall / (folds - 1))
    std_dev_micro_recall = math.sqrt(sum_micro_recall / (folds - 1))
    std_dev_macro_f = math.sqrt(sum_macro_f / (folds - 1))
    std_dev_micro_f = math.sqrt(sum_micro_f / (folds - 1))
    total_macro_precision = "Macro Precision: " + str(std_dev_macro_precision) + "\n"
    total_micro_precision = "Micro Precision: " + str(std_dev_micro_precision) + "\n"
    total_macro_recall = "Macro Recall: " + str(std_dev_macro_recall) + "\n"
    total_micro_recall = "Micro Recall: " + str(std_dev_micro_recall) + "\n"
    total_macro_f = "Macro F-Measure: " + str(std_dev_macro_f) + "\n"
    total_micro_f = "Micro F-Measure: " + str(std_dev_micro_f) + "\n"
    f.write("Standard Deviation\n")
    f.write("========================\n")
    f.write(total_macro_precision)
    f.write(total_micro_precision)
    f.write(total_macro_recall)
    f.write(total_micro_recall)
    f.write(total_macro_f)
    f.write(total_micro_f)
    f.write("==================\n")
    write_std_error(folds, std_dev_macro_precision, std_dev_micro_precision, std_dev_macro_recall, std_dev_micro_recall, std_dev_macro_f, std_dev_micro_f, f)


def write_std_error(folds, std_dev_macro_precision, std_dev_micro_precision, std_dev_macro_recall, std_dev_micro_recall, std_dev_macro_f, std_dev_micro_f, f):
    error_macro_precision = std_dev_macro_precision / math.sqrt(folds)
    error_micro_precision = std_dev_micro_precision / math.sqrt(folds)
    error_macro_recall = std_dev_macro_recall / math.sqrt(folds)
    error_micro_recall = std_dev_micro_recall / math.sqrt(folds)
    error_macro_f = std_dev_macro_f / math.sqrt(folds)
    error_micro_f = std_dev_micro_f / math.sqrt(folds)
    error_macro_precision = "Macro Precision: " + str(error_macro_precision) + "\n"
    error_micro_precision = "Micro Precision: " + str(error_micro_precision) + "\n"
    error_macro_recall = "Macro Recall: " + str(error_macro_recall) + "\n"
    error_micro_recall = "Micro Recall: " + str(error_micro_recall) + "\n"
    error_macro_f = "Macro F-Measure: " + str(error_macro_f) + "\n"
    error_micro_f = "Micro F-Measure: " + str(error_micro_f) + "\n"
    f.write("Standard Error\n")
    f.write("========================\n")
    f.write(error_macro_precision)
    f.write(error_micro_precision)
    f.write(error_macro_recall)
    f.write(error_micro_recall)
    f.write(error_macro_f)
    f.write(error_micro_f)
    f.write("==================\n")


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

        if classifier == "NN":
            filename = "Result_test_" + classifier + "_" + data["nn_library"] + ".txt"
        else:
            filename = "Result_test_" + classifier + ".txt"
        result_file = join(data["path_to_instances"], data["dataset_folder"], data["feature_folder"], filename)
        with open(result_file, 'w') as f:
            write_average(classifier, results, f)
            write_std_dev(results, classifier, num_folds, f)
