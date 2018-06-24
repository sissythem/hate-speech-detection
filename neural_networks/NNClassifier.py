from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    mlpClassifier = train_classifier(train_labels, train_features)
    conf_matrix = test_classifier(mlpClassifier, test_labels, test_features)
    return conf_matrix


def train_classifier(train_labels, train_features):
    print("Training classifier")
    mlpClassifier = MLPClassifier()
    return mlpClassifier.fit(train_features, train_labels)


def test_classifier(mlpClassifier, test_labels, test_features):
    print("Testing classifier")
    predicted_labels = mlpClassifier.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)