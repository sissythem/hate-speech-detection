import NNClassifier as nn
import KNNClassifier as knn
import NaiveBayesClassifier as nb
import RandomForestClassifier as rf
import LogisticRegressionClassifier as lr


def classify(data, train_labels, train_features, test_labels, test_features):
    classifier = data["classifier"]
    if classifier == "NN":
        return nn.classify(data, train_labels, train_features, test_labels, test_features)
    elif classifier == "KNN":
        return knn.classify(data, train_labels, train_features, test_labels, test_features)
    elif classifier == "NaiveBayes":
        return nb.classify(train_labels, train_features, test_labels, test_features)
    elif classifier == "RandomForest":
        return rf.classify(train_labels, train_features, test_labels, test_features)
    elif classifier == "LogisticRegression":
        return lr.classify(train_labels, train_features, test_labels, test_features)