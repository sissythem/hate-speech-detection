import utils
from os.path import join
import Result


def parse_results():
    path_list = utils.create_path_list()
    files_list = utils.add_files_to_list()
    results = []
    for feature in path_list:
        # find feature from path name
        features = feature.split("/")
        feature_name = features[-1]
        if features[-2] == "vector" and features[-1] == "all":
            feature_name = "vectorAll"
        for i in range(10):
            path = join(feature, "fold" + str(i))
            for file in files_list:
                file_name_parts = file.split("_")
                algorithm = file_name_parts[2]
                if algorithm == "NN":
                    kind = file_name_parts[3]
                    algorithm = "NN" + kind
                algorithm = algorithm.split(".")[0]
                file_path = join(path, file)
                with open(file_path) as f:
                    lines = [line for line in f]
                for idx in range(len(lines)):
                    if lines[idx].startswith("Macro F-Measure"):
                        line = lines[idx].split(":")
                        macro_f = line[1].strip()
                        idx = idx + 1
                        line = lines[idx].split(":")
                        micro_f = line[1].strip()
                        result = Result.Result(feature_name, algorithm, i, micro_f, macro_f)
                        results.append(result)
                        break
    return results


def write_results_per_fold():
    results = parse_results()
    header_micro = "features,classifiers,microf\n"
    header_macro = "features,classifiers,macrof\n"
    with open("microf.csv", 'w', newline='') as csvfile:
        csvfile.write(header_micro)
        for result in results:
            microf = float(result.micro_f)
            csvfile.write(result.feature_name + "," + result.classifier_name + "," + "%2.3f" % microf + "\n")
    with open("macrof.csv", 'w', newline='') as csvfile:
        csvfile.write(header_macro)
        for result in results:
            macrof = float(result.macro_f)
            csvfile.write(result.feature_name + "," + result.classifier_name + "," + "%2.3f" % macrof + "\n")


if __name__ == '__main__':
    write_results_per_fold()
