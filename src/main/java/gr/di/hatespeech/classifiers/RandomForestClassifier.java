package gr.di.hatespeech.classifiers;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.Random;

/**
 * Class that extends BaseClassifier and implements
 * classification and cross validation methods using
 * Random Forest classifier from Weka library
 */
public class RandomForestClassifier extends BaseClassifier {
    private static String startingMessageLog = "[" + RandomForestClassifier.class.getSimpleName() + "] ";
    private RandomForest rfClassifier;

    public RandomForestClassifier() {
        super(Utils.RANDOM_FOREST_CLASSIFIER);
    }

    public RandomForestClassifier(int folds, int runs) {
        super(Utils.RANDOM_FOREST_CLASSIFIER, folds, runs);
    }

    /**
     * Classifies given Instances using the Random Forest classifier from Weka.
     * Constructs a List with the classifier's responses for each Instance
     * @param trainingInstances, an Instances object (weka library)
     * @return evaluation, Classification evaluation with a list of all evaluations
     * and the classifier's name
     */
    @Override
    public ClassificationEvaluation crossValidate(Instances trainingInstances) {
        this.rfClassifier = new RandomForest();
        initEvaluation(Utils.RANDOM_FOREST_CLASSIFIER);
        trainingInstances.randomize(new Random());
        try {
            rfClassifier.buildClassifier(trainingInstances);
            Utils.FILE_LOGGER.info(startingMessageLog + "Training RandomForest classifier");
            for (int i = 1; i <= runs; i++) {
                evaluateCrossValidation(trainingInstances, i, rfClassifier);
            }
        } catch (Exception e) {
            Utils.FILE_LOGGER.error(e.getMessage(),e);
        }
        return evaluation;
    }

    /**
     * NaiveBayes classification. Builds the RandomForest classifier with the training instances
     * and evaluates the classifier with the test Instances
     * @param trainingInstances, instances to train the classifier
     * @param testInstances, instances to test the classifier
     * @return ClassificationEvaluation result
     */
    @Override
    public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
        this.rfClassifier = new RandomForest();
        initEvaluation(Utils.RANDOM_FOREST_CLASSIFIER);
        trainingInstances.randomize(new Random());
        testInstances.randomize(new Random());
        evaluateClassification(trainingInstances, testInstances, rfClassifier);
        return evaluation;
    }


}
