import classifiers.LogisticRegressionClassifier as lr
import classifiers.RandomForestClassifier as rf
import classifiers.NaiveBayesClassifier as nb
import classifiers.NNClassifier as nn
import classifiers.KNNClassifier as knn


def classify(data, classifier, num_classes, train_labels, train_features, test_labels, test_features):
    """
    Function used by FoldRunner to execute classification based on the current classifier
    :param data: the configuration dictionary
    :param classifier: current classifier (from the classifiers list in the configuration file)
    :param num_classes: the number of distinct labels (binary or multiclass classification)
    :param train_labels: the labels of all train instances
    :param train_features: the features of all train instances
    :param test_labels: the labels of all test instances
    :param test_features: the features of all test instances
    :return: the confusion matrix of the classification
    """
    if classifier == "NN":
        return nn.classify(data, num_classes, train_labels, train_features, test_labels, test_features)
    elif classifier == "KNN":
        return knn.classify(data, train_labels, train_features, test_labels, test_features)
    elif classifier == "NaiveBayes":
        return nb.classify(train_labels, train_features, test_labels, test_features)
    elif classifier == "RandomForest":
        return rf.classify(train_labels, train_features, test_labels, test_features)
    elif classifier == "LogisticRegression":
        return lr.classify(train_labels, train_features, test_labels, test_features)