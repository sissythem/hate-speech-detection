from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    """
    Function used by Classifier for NN classifier option in config file
    :param data: the configuration dictionary
    :param num_classes: the distinct labels (binary or multi-class classification problem)
    :param train_labels: the labels of the training instances
    :param train_features: the features of the training instances
    :param test_labels: the labels of the testing instances
    :param test_features: the features of the testing instances
    :return: the confusion matrix of this classification task
    """

    classifier = train_classifier(train_labels, train_features)
    conf_matrix = test_classifier(classifier, test_labels, test_features)
    return conf_matrix


def train_classifier(train_labels, train_features):
    """
    Function to train the MLP Classifier
    :param train_labels: the labels of each training instance
    :param train_features: the features of the training instances
    :return: the trained classifier
    """
    # Neural Network using scikit-learn library
    print("Training MLP classifier from scikit-learn")
    # create new classifier
    mlp_classifier = MLPClassifier()
    # Train the classifier and return it
    return mlp_classifier.fit(train_features, train_labels)


def test_classifier(classifier, test_labels, test_features):
    """
    Function for testing the classifier
    :param classifier: the classifier to be trained
    :param test_labels: the labels of the testing instances
    :param test_features: the features of the testing instances
    :return:
    """
    # Test the classifier using scikit-learn and return the confusion matrix
    print("Testing scikit-learn MLP classifier")
    predicted_labels = classifier.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)