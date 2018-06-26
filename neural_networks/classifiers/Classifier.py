import classifiers.LogisticRegressionClassifier as lr
import classifiers.RandomForestClassifier as rf
import classifiers.NaiveBayesClassifier as nb
import classifiers.NNClassifier as nn
import classifiers.KNNClassifier as knn


def classify(data, classifier, num_classes, train_labels, train_features, test_labels, test_features):
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