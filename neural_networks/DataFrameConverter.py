from os.path import join
from arff2pandas import a2p
import numpy


def convert_arff_to_dataframe(data, fold):
    path_to_instances = data["path_to_instances"]
    dataset_folder = data["dataset_folder"]
    feature_folder = data["feature_folder"]
    train_file = "train.arff"
    test_file = "test.arff"
    path_to_test_file = join(path_to_instances, dataset_folder, feature_folder, fold, test_file)
    path_to_train_file = join(path_to_instances, dataset_folder, feature_folder, fold, train_file)

    with open(path_to_test_file) as f:
        test_df = a2p.load(f)
    with open(path_to_train_file) as f:
        train_df = a2p.load(f)
    return train_df, test_df


def get_features_labels_arrays(df):
    array = numpy.asarray(df)
    features, labels_str = array[:, 1:], array[:, 0]
    # write down the class-index correspondence to a file!
    labels_dict, labels_str = labels_string_to_int(labels_str)
    labels = labels_str.astype(numpy.int32)
    features = features.astype(numpy.float32)
    return labels, features, labels_dict


def labels_string_to_int(labels_str):
    labels_set = set(labels_str)
    labels_dict = {}
    for i, label in enumerate(labels_set):
        labels_dict[label] = i
        labels_str[labels_str == label] = i
    return labels_dict, labels_str