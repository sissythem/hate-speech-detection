from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix
from keras.models import Sequential
from keras.layers import Dense, Activation, Dropout
from keras.layers.noise import GaussianNoise
import keras
import numpy as np


def classify(data, num_classes, train_labels, train_features, test_labels, test_features):
    """
    Function used by Classifier for NN classifier option in config file
    :param data: the configuration dictionary
    :param num_classes: the distinct labels (binary or multi-class classification problem)
    :param train_labels: the labels of the training instances
    :param train_features: the features of the training instances
    :param test_labels: the labels of the testing instances
    :param test_features: the features of the testing instances
    :return: the confusion matrix of this classification task
    """
    classifier = train_classifier(data, num_classes, train_labels, train_features)
    conf_matrix = test_classifier(classifier, data, test_labels, test_features)
    return conf_matrix


def train_classifier(data, num_classes, train_labels, train_features):
    """
    Function to train the neural network
    :param data: the config dictionary
    :param num_classes: the distinct number of labels (binary/multi-class)
    :param train_labels: the labels of the training instances
    :param train_features: the features of the training instances
    :return: the MLP classifier
    """
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        # Neural Network using scikit-learn library
        print("Training MLP classifier from scikit-learn")
        # create new classifier
        mlp_classifier = MLPClassifier()
        # Train the classifier and return it
        return mlp_classifier.fit(train_features, train_labels)
    elif nn_library == "keras":
        # Neural Network model using keras
        data = data["keras"]
        print("Creating Sequential model in keras")
        # Create new model with the specified params in the config file
        data_dimension = train_features.shape[1]
        epochs = data["epochs"]
        batch_size=data["batch_size"]
        model = Sequential([
            Dense(data["hidden_layers_size"], input_shape=(data_dimension,), name="first_dense"),
            Activation('relu', name="first_activation"),
            Dense(data["hidden_layers_size"], activation='relu', name="second_dense"),
            Dropout(data["dropout"]),
            Dense(data["hidden_layers_size"], activation='relu', name="third_dense"),
            GaussianNoise(data["gaussian_noise"]),
            Dense(num_classes, name="Dense_to_numclasses"),
            Activation('softmax', name="classification_activation"),
        ])
        # Train the model and return it
        model.compile(optimizer='rmsprop',
                      loss='categorical_crossentropy',
                      metrics=['accuracy'])
        # Convert labels to categorical one-hot encoding
        one_hot_labels = keras.utils.to_categorical(train_labels, num_classes=num_classes)
        model.fit(train_features, one_hot_labels, epochs=epochs, batch_size=batch_size)
        return model


def test_classifier(classifier, data, test_labels, test_features):
    """
    Test the given classifier with the given test data
    :param classifier: the trained classifier
    :param data: the configuration dictionary
    :param test_labels: the labels of the testing instances
    :param test_features: the features of the testing instances
    :return: the confusion matrix
    """
    nn_library = data["nn_library"]
    predicted_labels = []
    if nn_library == "scikit-learn":
        # Test the classifier using scikit-learn and return the confusion matrix
        print("Testing scikit-learn MLP classifier")
        predicted_labels = classifier.predict(test_features)
    elif nn_library == "keras":
        # Test the model using keras and return the confusion matrix
        print("Testing model from keras")
        result = classifier.predict(test_features)
        for i in range(result.shape[0]):
            predicted_labels.append(np.argmax(result[i]))
    return confusion_matrix(test_labels, predicted_labels)
