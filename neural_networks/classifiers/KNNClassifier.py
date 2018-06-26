from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import confusion_matrix


def classify(data, train_labels, train_features, test_labels, test_features):
    print("Running KNN classifier")
    knn = KNeighborsClassifier(n_neighbors=data["neighbors"])
    print("Training KNN classifier")
    knn.fit(train_features, train_labels)
    print("Testing KNN Classifier")
    predicted_labels = knn.predict(test_features)
    return confusion_matrix(test_labels, predicted_labels)