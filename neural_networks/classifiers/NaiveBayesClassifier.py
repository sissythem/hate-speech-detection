from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    gnb = GaussianNB()
    gnb.fit(train_features, train_labels)
    predicted_labels = gnb.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)
