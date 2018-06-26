from classifiers import LogisticRegressionClassifier as lr, RandomForestClassifier as rf, NaiveBayesClassifier as nb, \
    NNClassifier as nn, KNNClassifier as knn


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