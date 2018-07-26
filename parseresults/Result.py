class Result:
    feature_name = ""
    classifier_name = ""
    fold_num = None
    micro_f = None
    macro_f = None

    def __init__(self, feature, classifier, fold, micro, macro):
        self.feature_name = feature
        self.classifier_name = classifier
        self.fold_num = fold
        self.micro_f = micro
        self.macro_f = macro
