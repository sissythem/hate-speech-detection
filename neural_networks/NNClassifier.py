from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix
from keras.models import Sequential
from keras.layers import Dense, Activation


def classify(data, train_labels, train_features, test_labels, test_features):
    mlpClassifier = train_classifier(data, train_labels, train_features)
    conf_matrix = test_classifier(mlpClassifier, data, test_labels, test_features)
    return conf_matrix


def train_classifier(data, train_labels, train_features):
    print("Training classifier")
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        mlpClassifier = MLPClassifier()
        return mlpClassifier.fit(train_features, train_labels)
    elif nn_library == "keras":
        model = Sequential([
            Dense(32, input_shape=(784,)),
            Activation('relu'),
            Dense(10),
            Activation('softmax'),
        ])


def test_classifier(mlpClassifier, data, test_labels, test_features):
    print("Testing classifier")
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        predicted_labels = mlpClassifier.predict(test_features)
        return confusion_matrix(test_labels, predicted_labels)
    elif nn_library == "keras":
        pass
