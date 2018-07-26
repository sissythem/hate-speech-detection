# Title     : ANOVA and Tukey's Tests
# Objective : Run ANOVA and Tukey's Tests
# Created by: sissy
# Created on: 7/14/18
macrofile = "./results/singlelabel/macrof.csv"
microfile = "./results/singlelabel/microf.csv"
macrodata = read.csv(file=macrofile)
microdata = read.csv(file=microfile)
macro_aov=aov(macrof  ~ features + classifiers, data=macrodata)
micro_aov = aov(microf ~ features + classifiers, data=microdata)
summary(macro_aov)
summary(micro_aov)
hsd_features = HSD.test(micro_aov, "features", group=TRUE)
hsd_classifiers = HSD.test(micro_aov, "classifiers", group=TRUE)
print(hsd_features)
print(hsd_classifiers)
