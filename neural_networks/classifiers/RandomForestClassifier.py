from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    """
       Classification with Random Forest classifier (scikit-learn library)
       :param train_labels: the labels of the train instances
       :param train_features: the features of the train instances
       :param test_labels: the labels of the test instances
       :param test_features: the features of the test features
       :return: the confusion matrix
   """
    print("Running Random Forest classifier")
    random_forest_classifier = RandomForestClassifier()
    print("Training Random Forest classifier")
    random_forest_classifier.fit(train_features, train_labels)
    print("Testing Random Forest classifier")
    predicted_labels = random_forest_classifier.predict(test_features)
    print("Got predicted labels")
    return confusion_matrix(test_labels, predicted_labels)
