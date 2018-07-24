# Title     : ANOVA and Tukey's Tests
# Objective : Run ANOVA and Tukey's Tests
# Created by: sissy
# Created on: 7/14/18
macrofile = "./results/singlelabel/macrofall.csv"
microfile = "./results/singlelabel/microfall.csv"
macrodata = read.csv(file=macrofile)
microdata = read.csv(file=microfile)
macro_aov=aov(macrof  ~ features + classifiers, data=macrodata)
micro_aov = aov(microf ~ features + classifiers, data=microdata)
summary(macro_aov)
summary(micro_aov)
HSD.test(micro_aov, "features", group=TRUE)
HSD.test(micro_aov, "classifiers", group=TRUE)
