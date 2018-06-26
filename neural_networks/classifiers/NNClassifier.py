from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix
from keras.models import Sequential
from keras.layers import Dense, Activation
import keras
import numpy as np


def classify(data, num_classes, train_labels, train_features, test_labels, test_features):
    classifier = train_classifier(data, num_classes, train_labels, train_features)
    conf_matrix = test_classifier(classifier, num_classes, data, test_labels, test_features)
    return conf_matrix


def train_classifier(data, num_classes, train_labels, train_features):
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        print("Training MLP classifier from scikit-learn")
        mlpClassifier = MLPClassifier()
        return mlpClassifier.fit(train_features, train_labels)
    elif nn_library == "keras":
        print("Creating Sequential model in keras")
        data_dimension = train_features.shape[1]
        epochs = data["epochs"]
        batch_size=data["batch_size"]
        model = Sequential([
            Dense(100, input_shape=(data_dimension,), name="first_dense"),
            Activation('relu', name="first_activation"),
            Dense(num_classes, name="Dense_to_numclasses"),
            Activation('softmax', name="classification_activation"),
        ])
        print("Building model for multi-class problem")
        model.compile(optimizer='rmsprop',
                      loss='categorical_crossentropy',
                      metrics=['accuracy'])
        # Convert labels to categorical one-hot encoding
        one_hot_labels = keras.utils.to_categorical(train_labels, num_classes=num_classes)
        model.fit(train_features, one_hot_labels, epochs=epochs, batch_size=batch_size)
        return model


def test_classifier(classifier, num_classes, data, test_labels, test_features):
    nn_library = data["nn_library"]
    predicted_labels = []
    if nn_library == "scikit-learn":
        print("Testing scikit-learn MLP classifier")
        predicted_labels = classifier.predict(test_features)
    elif nn_library == "keras":
        print("Testing model from keras")
        result = classifier.predict(test_features)
        for i in range(result.shape[0]):
            predicted_labels.append(np.argmax(result[i]))
    return confusion_matrix(test_labels, predicted_labels)
