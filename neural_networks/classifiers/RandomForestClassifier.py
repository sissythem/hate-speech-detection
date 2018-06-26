from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    print("Running Random Forest classifier")
    random_forest_classifier = RandomForestClassifier()
    print("Training Random Forest classifier")
    random_forest_classifier.fit(train_features, train_labels)
    print("Testing Random Forest classifier")
    predicted_labels = random_forest_classifier.predict(test_features)
    print("Got predicted labels")
    return confusion_matrix(test_labels, predicted_labels)
