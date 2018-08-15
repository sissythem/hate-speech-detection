import os
import numpy
from os.path import join
path="/home/sissy/Documents/Professional/University/UOA/Graduate/Thesis/hate-speech-detection/instances/multilabel0/vector"

def get_confusion_matrix(name):
    # get confusion matrix from raw weka output

    with open(name) as f:
        confm_start = "Confusion matrix"
        is_relevant = False
        relevant = []
        for line in f:
            line = line.strip()
            if not line:
                is_relevant = False
                continue
            if line.startswith(confm_start):
                is_relevant = True
            if is_relevant:
                relevant.append(line.strip())
        print ("\n".join(relevant))


    confm = []
    symbols = []
    symbol_classes = {}
    relevant = relevant[1:]
    for i in range(len(relevant)):
        if i == 0:
            # header
            parts = relevant[i].split()
            assert parts[-3:] == ["<--", "classified", "as"], "Failed to parse conf.matrix: %s" % (str(relevant))
            symbols = parts[:-3]
            numc = len(symbols)
        else:
            parts = relevant[i].split()
            pipe, symbol, eq, classname = parts[-4:]
            assert eq == "=", "Failed to parse equals at conf.matrix: %s, got %s instead" % (str(relevant[i]), eq)
            assert pipe == "|", "Failed to parse pipe at row %s, got %s instead" % (str(relevant[i]), pipe)
            symbol_classes[symbol] = classname
            confm.append(list(map(int, parts[:-4])))

    [print(r) for r in confm]
    return confm, symbol_classes, symbols

def get_measures(confmatrix, symbols, symbol_classes):
    num_classes=len(symbols)
    total_true_positives=0
    total_false_positives=0
    total_false_negatives=0
    list_precisions=[]
    list_recalls=[]
    list_fmeasures=[]
    for i in range(num_classes):
        true_positive=confmatrix[i][i]
        false_positive=0
        false_negative=0
        for j in range(num_classes):
            if(j !=i):
                false_positive = false_positive + confmatrix[j][i] 
                false_negative = false_negative + confmatrix[i][j]
        print(symbols[i], " true_positive ", true_positive, " false_negative ", false_negative, " false_positive ", false_positive)
        if((true_positive + false_positive) == 0):
            precision = 0
        else:
            precision = true_positive / (true_positive + false_positive)
        if((true_positive + false_negative) == 0):
            recall = 0
        else:
            recall = true_positive / (true_positive + false_negative)
        if((precision + recall) == 0):
            fmeasure = 0
        else:
            fmeasure = 2 * ((precision*recall) / (precision + recall))

        total_true_positives = total_true_positives + true_positive
        total_false_negatives = total_false_negatives + false_negative
        total_false_positives = total_false_positives + false_positive
        list_precisions.append(precision)
        list_recalls.append(recall)
        list_fmeasures.append(fmeasure)
    macro_precision = sum(list_precisions) / len(list_precisions)
    micro_precision = total_true_positives / (total_true_positives + total_false_positives)
    macro_recall = sum(list_recalls) / len(list_recalls)
    micro_recall = total_true_positives / (total_true_positives + total_false_negatives)
    macro_f = 2 * ((macro_precision*macro_recall) / (macro_precision + macro_recall)) 
    micro_f = 2 * ((micro_precision*micro_recall) / (micro_precision + micro_recall)) 

    print("macro_precision: ", macro_precision, " macro_recall: ", macro_recall, " macro_f: ", macro_f, " micro_precision: ", micro_precision, " micro_recall: ", micro_recall, " micro_f: ", micro_f)
    return macro_precision, micro_precision, macro_recall, micro_recall, macro_f, micro_f


# exclude features in here
exclude_features = ["all"]
# only process features in here
only_include_features = ["ngrams"]
parseFilesPrefix = "Result_test"
# store F measure results here
results = {}

# for each feature
for features_name in os.listdir(path):
    if features_name in exclude_features: 
        print(features_name,"in exclusion-list continuing")
        continue
    if features_name not in only_include_features:
        print(features_name,"not in only-inclusion-list, continuing")
        continue
    features_folder_path = join(path, features_name)
    print("feat:",features_folder_path)
    # for each fold
    for fold_folder in os.listdir(features_folder_path):
        foldPath = join(features_folder_path, fold_folder)
        print("fold:",foldPath)
        # for each file
        for filename in os.listdir(foldPath):
            filepath = join(foldPath, filename)
            print("file:", filepath)
            # keep files starting with prefix
            if filename.startswith(parseFilesPrefix):
                # get metadata from filename
                _, mode, algorithm = filename.split("_")
                algorithm = algorithm.split(".")[0]
                if(algorithm == "NeuralNetworks"): continue
                # get confusion matrix
                confmatrix, symbol_classes, symbols = get_confusion_matrix(filepath)
                # compute micro/macro F measure
                measures_tuples = get_measures(confmatrix, symbols, symbol_classes)
                # store it in dictionary
                if features_name not in results:
                    results[features_name] = {}
                if algorithm not in results[features_name]:
                    results[features_name][algorithm] = []

                results[features_name][algorithm].append(measures_tuples)

print("Foldwise averages")
# compute means
for feat in results:
    for alg in results[feat]:
        dat = numpy.asarray(list(results[feat][alg]))
        print(feat, alg)
        print(numpy.mean(dat, axis=0))
# write
