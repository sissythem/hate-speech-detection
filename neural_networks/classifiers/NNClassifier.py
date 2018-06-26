from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix
from keras.models import Sequential
from keras.layers import Dense, Activation
import keras


def classify(data, num_classes, train_labels, train_features, test_labels, test_features):
    classifier = train_classifier(data, num_classes, train_labels, train_features)
    conf_matrix = test_classifier(classifier, data, test_labels, test_features)
    return conf_matrix


def train_classifier(data, num_classes, train_labels, train_features):
    print("Training classifier")
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        mlpClassifier = MLPClassifier()
        return mlpClassifier.fit(train_features, train_labels)
    elif nn_library == "keras":
        model = Sequential([
            Dense(100, input_shape=(784,)),
            Activation('relu'),
            Dense(50),
            Activation('softmax'),
        ])
        if num_classes > 2:
            # For a multi-class classification problem
            model.compile(optimizer='rmsprop',
                          loss='categorical_crossentropy',
                          metrics=['accuracy'])
            # Convert labels to categorical one-hot encoding
            one_hot_labels = keras.utils.to_categorical(train_labels, num_classes=num_classes)

            # Train the model, iterating on the data in batches of 32 samples
            model.fit(train_features, one_hot_labels, epochs=10, batch_size=100)
        elif num_classes == 2:
            # For a binary classification problem
            model.compile(optimizer='rmsprop',
                          loss='binary_crossentropy',
                          metrics=['accuracy'])
            model.fit(train_features, train_labels, epochs=10, batch_size=100)
        return model


def test_classifier(classifier, data, test_labels, test_features):
    print("Testing classifier")
    nn_library = data["nn_library"]
    if nn_library == "scikit-learn":
        predicted_labels = classifier.predict(test_features)
        return confusion_matrix(test_labels, predicted_labels)
    elif nn_library == "keras":
        score = classifier.evaluate(test_features, test_labels, batch_size=100)
