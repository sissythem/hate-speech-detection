from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    """
       Classification with Naive Bayes classifier (scikit-learn library)
       :param train_labels: the labels of the train instances
       :param train_features: the features of the train instances
       :param test_labels: the labels of the test instances
       :param test_features: the features of the test features
       :return: the confusion matrix
   """
    print("Running Naive Bayes classifier")
    gnb = GaussianNB()
    print("Training Naive Bayes classifier")
    gnb.fit(train_features, train_labels)
    print("Testing Naive Bayes classifier")
    predicted_labels = gnb.predict(test_features)
    print("Got predicted labels")
    return confusion_matrix(test_labels, predicted_labels)
