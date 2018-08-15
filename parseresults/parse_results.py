import utils
from os.path import join
import pandas as pd
import matplotlib.pyplot as plt

choices = ["csv", "latex", "plot"]
multirow = True
output = choices[2]


def parse_results():
    path_list = utils.create_path_list()
    files_list = utils.add_files_to_list()
    results = {}
    for feature in path_list:
        # find feature from path name
        features = feature.split("/")
        feature_name = features[-1]
        if features[-2] == "vector" and features[-1] == "all":
            feature_name = "vector"
        results[feature_name] = {}
        for file in files_list:
            file_name_parts = file.split("_")
            algorithm = file_name_parts[2]
            if algorithm == "NN":
                kind = file_name_parts[3]
                if output == "csv":
                    algorithm = "NN" + kind
                elif output == "latex":
                    algorithm = "NN" + "\\_" + kind
            algorithm = algorithm.split(".")[0]
            results[feature_name][algorithm] = []
            file_path = join(feature, file)
            with open(file_path) as f:
                lines = [line for line in f]
            for idx in range(len(lines)):
                if lines[idx].startswith("Macro F-Measure"):
                    line = lines[idx].split(":")
                    macro_f = line[1].strip()
                    idx = idx + 1
                    line = lines[idx].split(":")
                    micro_f = line[1].strip()
                    results[feature_name][algorithm].append(macro_f)
                    results[feature_name][algorithm].append(micro_f)
                    break
    return results


def separate_results(results):
    results_all = {}
    results_best = {}
    results_ngrams = {}
    results_s = {}

    for feature in results:
        if feature == "all" or feature == "vector" or feature == "best":
            results_all[feature] = results[feature]
        elif feature == "graph" or feature == "bow" or feature == "word2vec":
            results_best[feature] = results[feature]
        elif feature == "sentiment" or feature == "syntax" or feature == "spelling":
            results_s[feature] = results[feature]
        elif feature == "ngrams" or feature == "charngrams":
            results_ngrams[feature] = results[feature]
    return results_all, results_best, results_s, results_ngrams


def get_feature_results(feature,results):
    new_results = {}
    for feat in results:
        if feat == feature:
            new_results[feat] = results[feat]
    return new_results


def write_latex_table(results):
    print("\\begin{table}[H]")
    print("\\begin{tabular}")
    print("{ | l | l | l | l |}")
    print("\\hline")
    print("Features & Algorithms & Macro F & Micro F \\\\ \hline")
    for feature in results:
        count = 1
        for algorithm in results[feature]:
            macro_f = float(results[feature][algorithm][0])
            micro_f = float(results[feature][algorithm][1])
            if multirow:
                if count == 1:
                    print("\\multirow{6}{*}{", feature, "} & ", algorithm, " & ", "%2.3f" % macro_f, " & ", "%2.3f" % micro_f, "\\\\" )
                elif count == len(results[feature]):
                    print(" & ", algorithm, " & ", "%2.3f" % macro_f, " & ", "%2.3f" % micro_f, "\\\\", "\\hline")
                else:
                    print(feature, " & ", algorithm, " & ", "%2.3f" % macro_f, " & ", "%2.3f" % micro_f, "\\\\")
                count = count + 1
            else:
                print(feature, " & ", algorithm, " & ", "%2.3f" % macro_f, " & ", "%2.3f" % micro_f, "\\\\", "\\hline")
    print("\end{tabular}")
    print("\end{table}")


def write_to_csv(results, filename, header, typef):
    with open(filename, 'w', newline='') as csvfile:
        csvfile.write(header)
        for feature in results:
            for algorithm in results[feature]:
                if typef == "micro":
                    favg = float(results[feature][algorithm][1])
                elif typef == "macro":
                    favg = float(results[feature][algorithm][0])
                csvfile.write(feature + "," + algorithm + "," + "%2.3f" % favg + "\n")


def visualize_results(results, filename):
    results_list=[]
    headers = ["algorithm", "feature", "microf"]
    for feature in results:
        for algorithm in results[feature]:
            macro_f = float(results[feature][algorithm][0])
            micro_f = float(results[feature][algorithm][1])
            result_list = [algorithm, feature, micro_f]
            results_list.append(result_list)
    df = pd.DataFrame(results_list, columns=headers)
    print(df)
    df.pivot("feature", "algorithm", "microf").plot(kind='bar')
    plt.show()
    plt.savefig(filename + '.png', bbox_inches='tight')


if __name__ == '__main__':
    results = parse_results()
    results_all, results_best, results_s, results_ngrams = separate_results(results)
    if output == "latex":
        write_latex_table(results_all)
        write_latex_table(results_best)
        write_latex_table(results_s)
        write_latex_table(results_ngrams)
    elif output == "csv":
        header_micro = "features,classifiers,microf\n"
        header_macro = "features,classifiers,macrof\n"
        write_to_csv(results, "microf.csv", header_micro, "micro")
        write_to_csv(results, "macrof.csv", header_macro, "macro")
        write_to_csv(results_all, "microfall.csv", header_micro, "micro")
        write_to_csv(results_all, "macrofall.csv", header_macro, "macro")
        write_to_csv(results_best, "microfbest.csv", header_micro, "micro")
        write_to_csv(results_best, "macrofbest.csv", header_macro, "macro")
        write_to_csv(results_s, "microfs.csv", header_micro, "micro")
        write_to_csv(results_s, "macrofs.csv", header_macro, "macro")
        write_to_csv(results_ngrams, "microfngrams.csv", header_micro, "micro")
        write_to_csv(results_ngrams, "macrofngrams.csv", header_macro, "macro")
        results_graph = get_feature_results("graph", results_best)
        results_best_feat = get_feature_results("best", results_all)
        write_to_csv(results_graph, "macrofgraph.csv", header_macro, "macro")
        write_to_csv(results_graph, "microfgraph.csv", header_micro, "micro")
        write_to_csv(results_best_feat, "macrofbestfeat.csv", header_macro, "macro")
        write_to_csv(results_best_feat, "microfbestfeat.csv", header_micro, "micro")
    elif output == "plot":
        visualize_results(results_all, "combinations")
        visualize_results(results_best, "best")
        visualize_results(results_s, "sentimentgrammar")
        visualize_results(results_ngrams, "ngrams")
