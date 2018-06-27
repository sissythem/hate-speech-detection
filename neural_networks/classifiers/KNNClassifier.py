from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import confusion_matrix


def classify(data, train_labels, train_features, test_labels, test_features):
    """
    Classification with KNN classifier (scikit-learn library)
    :param data: the configuration dictionary
    :param train_labels: the labels of the train instances
    :param train_features: the features of the train instances
    :param test_labels: the labels of the test instances
    :param test_features: the features of the test features
    :return: the confusion matrix
    """
    print("Running KNN classifier")
    # creates new KNN classifier
    knn = KNeighborsClassifier(n_neighbors=data["neighbors"])
    print("Training KNN classifier")
    # Train the classifier
    knn.fit(train_features, train_labels)
    print("Testing KNN Classifier")
    # Test the classifier and get the confusion matrix
    predicted_labels = knn.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)