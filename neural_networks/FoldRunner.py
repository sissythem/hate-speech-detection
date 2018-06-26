import DataFrameConverter as dfc
from classifiers import Classifier
from os.path import join


def run_fold(data, i, results):
    print("Running fold: ", i)
    fold = "fold" + str(i)
    print("Reading and converting arff files")
    train_df, test_df = dfc.convert_arff_to_dataframe(data, fold)
    print("Got train and test dataframes")
    print("Creating numpy arrays")
    train_labels, train_features, train_labels_dict = dfc.get_features_labels_arrays(train_df)
    test_labels, test_features, test_labels_dict = dfc.get_features_labels_arrays(test_df)
    num_classes = len(train_labels_dict)
    print("Got labels and features for train and test datasets")
    print("Classifying")
    for classifier in data["classifiers"]:
        confusion_matrix = Classifier.classify(data, classifier, num_classes, train_labels, train_features, test_labels, test_features)
        results[classifier] = write_results_to_file(data, fold, classifier, confusion_matrix, test_labels_dict, results)
    return results


def write_results_to_file(data, fold, classifier, confusion_matrix, test_labels_dict, results):
    macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f = get_measures(confusion_matrix, test_labels_dict)
    measure_tuples = macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f
    results[classifier] = []
    results[classifier].append(measure_tuples)
    result_file = join(data["path_to_instances"], data["dataset_folder"], data["feature_folder"], fold, "Result_test_Neural_Networks.txt")
    with open(result_file, 'w') as f:
        confusion_matrix = "Confusion Matrix: " + str(confusion_matrix) + "\n"
        macro_precision = "Macro Precision: " + str(macro_precision) + "\n"
        micro_precision = "Micro Precision: " + str(micro_precision) + "\n"
        macro_recall = "Macro Recall: " + str(macro_recall) + "\n"
        micro_recall = "Micro Recall: " + str(micro_recall) + "\n"
        macro_f = "Macro F-Measure: " + str(macro_f) + "\n"
        micro_f  = "Micro F-Measure: " + str(micro_f) + "\n"
        f.write(confusion_matrix)
        f.write(macro_precision)
        f.write(micro_precision)
        f.write(macro_recall)
        f.write(micro_recall)
        f.write(macro_f)
        f.write(micro_f)
    return results


def get_measures(confusion_matrix, test_labels_dict):
    # num_classes = len(test_labels_dict)
    total_true_positives = 0
    total_false_positives = 0
    total_false_negatives = 0
    list_precisions = []
    list_recalls = []
    list_fmeasures = []
    for label in test_labels_dict:
        true_positive = confusion_matrix[test_labels_dict[label]][test_labels_dict[label]]
        false_positive = 0
        false_negative = 0
        for other_label in test_labels_dict:
            if other_label != label:
                false_positive = false_positive + confusion_matrix[test_labels_dict[other_label]][test_labels_dict[label]]
                false_negative = false_negative + confusion_matrix[test_labels_dict[label]][test_labels_dict[other_label]]
        print(label, " true_positive ", true_positive, " false_negative ", false_negative, " false_positive ",
              false_positive)
        if (true_positive + false_positive) == 0:
            precision = 0
        else:
            precision = true_positive / (true_positive + false_positive)
        if (true_positive + false_negative) == 0:
            recall = 0
        else:
            recall = true_positive / (true_positive + false_negative)
        if (precision + recall) == 0:
            fmeasure = 0
        else:
            fmeasure = 2 * ((precision * recall) / (precision + recall))

        total_true_positives = total_true_positives + true_positive
        total_false_negatives = total_false_negatives + false_negative
        total_false_positives = total_false_positives + false_positive
        list_precisions.append(precision)
        list_recalls.append(recall)
        list_fmeasures.append(fmeasure)
    macro_precision = sum(list_precisions) / len(list_precisions)
    micro_precision = total_true_positives / (total_true_positives + total_false_positives)
    macro_recall = sum(list_recalls) / len(list_recalls)
    micro_recall = total_true_positives / (total_true_positives + total_false_negatives)
    macro_f = 2 * ((macro_precision * macro_recall) / (macro_precision + macro_recall))
    micro_f = 2 * ((micro_precision * micro_recall) / (micro_precision + micro_recall))

    print("macro_precision: ", macro_precision, " macro_recall: ", macro_recall, " macro_f: ", macro_f,
          " micro_precision: ", micro_precision, " micro_recall: ", micro_recall, " micro_f: ", micro_f)
    return macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f
