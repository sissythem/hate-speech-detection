import json
import FoldRunner as fr
from os.path import join
import numpy
from scipy import stats
import utils


def read_configurations(filename):
    """
    On start up main function reads the configuration file (json), that includes
    the folds number, the path to the arff files, the dataset folder (binary or multi-class problem)
    the specific feature folder to be used, some configurations related to classification algorithms
    (such as the neighbors number for KNN algorithm or parameters for MLP model in keras), a list with
    the classifiers to be used and finally which library to be used for Neural Networks
    :return: a python dictionary with all information from the configuration file
    """
    with open(filename) as json_data_file:
        data = json.load(json_data_file)
    return data


def write_average(results, classifier, f):
    """
    This method writes the average of micro/macro metrics in a file
    :param f: the file to write the results
    :type classifier: a String with classifier's name
    :type results: dictionary with the results for all classifiers
    """
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


def write_std_dev(results, classifier, f):
    """
    This method writes into a files the standard deviation of micro/macro average for precision, recall and F-Measure
    :param results: all the results from the classification task
    :param classifier: the specific classifier to calculate the total results
    :param f: the file to write into
   """
    std_dev_macro_precision, std_dev_micro_precision, std_dev_macro_recall, std_dev_micro_recall, std_dev_macro_f, std_dev_micro_f = numpy.std(
        results[classifier], axis=0)
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


def write_std_error(results, classifier, f):
    """
    This method writes the standard error of micro/macro average of precision, recall and F-Measure metrics in a file
    :param results: the dictionary with the results for all classifiers
    :param classifier: the specific classifier
    :param f: the file to write into
    :return:
    """
    macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = stats.sem(results[classifier], axis=0)
    error_macro_precision = "Macro Precision: " + str(macro_precision) + "\n"
    error_micro_precision = "Micro Precision: " + str(micro_precision) + "\n"
    error_macro_recall = "Macro Recall: " + str(macro_recall) + "\n"
    error_micro_recall = "Micro Recall: " + str(micro_recall) + "\n"
    error_macro_f = "Macro F-Measure: " + str(macro_f) + "\n"
    error_micro_f = "Micro F-Measure: " + str(micro_f) + "\n"
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
    start_time = utils.get_datetime()
    # read the configurations
    data = read_configurations("config.json")
    emailConfig = read_configurations("emailConfig.json")
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
            write_average(results, classifier, f)
            write_std_dev(results, classifier, f)
            write_std_error(results, classifier, f)
    end_time = utils.elapsed_str(start_time, up_to=None)
    print("Time needed: " + end_time)
    utils.send_email(emailConfig, end_time)
