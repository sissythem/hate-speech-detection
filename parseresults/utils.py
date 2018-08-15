from os.path import join

instances_path = "/home/sissy/Documents/Professional/University/UOA/Graduate/Thesis/hate-speech-detection/instances/"
dataset = "multilabel1"

graph_path = "graph"
best_path = "best"
all_path = "all"
vector_all_path = "vector/all"
bow_path = "vector/bow"
charngrams_path = "vector/charngrams"
ngrams_path = "vector/ngrams"
sentiment_path = "vector/sentiment"
spelling_path = "vector/spelling"
syntax_path = "vector/syntax"
word2vec_path = "vector/word2vec"

knn_file = "Result_test_KNN.txt"
lr_file = "Result_test_LogisticRegression.txt"
nb_file = "Result_test_NaiveBayes.txt"
nn_keras_file = "Result_test_NN_keras.txt"
nn_scikit_file = "Result_test_NN_scikit-learn.txt"
rf_file = "Result_test_RandomForest.txt"


def create_path_list():
    path_list = []
    path_list.append(join(instances_path, dataset, graph_path))
    path_list.append(join(instances_path, dataset, best_path))
    path_list.append(join(instances_path, dataset, all_path))
    path_list.append(join(instances_path, dataset, vector_all_path))
    path_list.append(join(instances_path, dataset, bow_path))
    path_list.append(join(instances_path, dataset, charngrams_path))
    path_list.append(join(instances_path, dataset, ngrams_path))
    path_list.append(join(instances_path, dataset, sentiment_path))
    path_list.append(join(instances_path, dataset, spelling_path))
    path_list.append(join(instances_path, dataset, syntax_path))
    path_list.append(join(instances_path, dataset, word2vec_path))
    return path_list


def add_files_to_list():
    files_list = []
    files_list.append(knn_file)
    files_list.append(lr_file)
    files_list.append(nb_file)
    files_list.append(nn_keras_file)
    files_list.append(nn_scikit_file)
    files_list.append(rf_file)
    return files_list
