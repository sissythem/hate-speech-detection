# Title     : ANOVA and Tukey's Tests
# Objective : Run ANOVA and Tukey's Tests
# Created by: sissy
# Created on: 7/14/18
macrofile = "./results/multilabel0/macrofbest.csv"
microfile = "./results/multilabel0/microfbest.csv"
macrodata = read.csv(file=macrofile)
microdata = read.csv(file=microfile)
macro_aov=aov(macrof  ~ features + classifiers, data=macrodata)
micro_aov = aov(microf ~ features + classifiers, data=microdata)
summary(macro_aov)
summary(micro_aov)
TukeyHSD(x=macro_aov, "features", conf.level=0.95)
TukeyHSD(x=micro_aov, "classifiers", conf.level=0.95)
