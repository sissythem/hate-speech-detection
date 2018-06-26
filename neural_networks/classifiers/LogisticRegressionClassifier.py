from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix


def classify(train_labels, train_features, test_labels, test_features):
    log_reg = LogisticRegression()
    log_reg.fit(train_features, train_labels)
    res_prob = log_reg.predict_proba(test_features)
    predicted_labels = np.argmax(res_prob, axis=1)
    return confusion_matrix(test_labels, predicted_labels)