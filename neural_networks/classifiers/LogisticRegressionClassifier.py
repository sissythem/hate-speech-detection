from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix
import numpy as np


def classify(train_labels, train_features, test_labels, test_features):
    print("Running Logistic Regression classifier")
    log_reg = LogisticRegression()
    print("Training Logistic Regression classifier")
    log_reg.fit(train_features, train_labels)
    print("Testing Logistic Regression classifier")
    res_prob = log_reg.predict_proba(test_features)
    predicted_labels = np.argmax(res_prob, axis=1)
    print("Got predicted labels")
    return confusion_matrix(test_labels, predicted_labels)