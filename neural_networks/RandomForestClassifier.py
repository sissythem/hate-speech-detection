from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    random_forest_classifier = RandomForestClassifier()
    random_forest_classifier.fit(train_features, train_labels)
    predicted_labels = random_forest_classifier.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)
