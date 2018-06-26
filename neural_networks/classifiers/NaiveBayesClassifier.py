from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    print("Running Naive Bayes classifier")
    gnb = GaussianNB()
    print("Training Naive Bayes classifier")
    gnb.fit(train_features, train_labels)
    print("Testing Naive Bayes classifier")
    predicted_labels = gnb.predict(test_features)
    print("Got predicted labels")
    return confusion_matrix(test_labels, predicted_labels)
