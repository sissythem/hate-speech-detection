package gr.di.hatespeech.classifiers;

import java.util.Random;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

/**
 * This class extends BaseClassifier abstract class implementing a
 * NaiveBayes classifier.
 * Uses the NaiveBayes classifier from Weka.
 * @author sissy
 */
public class NaiveBayesClassifier extends BaseClassifier {
	private static String startingMessageLog = "[" + NaiveBayesClassifier.class.getSimpleName() + "] ";
	private NaiveBayes naiveBayes;

	/**
	 * Default constructor. Initialization of weka NaiveBayes classifier
	 */
	public NaiveBayesClassifier()
	{
		super(Utils.NAIVE_BAYES_CLASSIFIER);
	}

	public NaiveBayesClassifier(int folds, int runs) {
		super(Utils.NAIVE_BAYES_CLASSIFIER, folds, runs);
	}

	/**
	 * Classifies given Instances using the Naive Bayes classifier from Weka.
	 * Constructs a List with the classifier's responses for each Instance
	 * @param trainingInstances, an Instances object (weka library)
	 * @return evaluation, Classification evaluation with a list of all evaluations
	 * and the classifier's name
	 */
	@Override
	public ClassificationEvaluation crossValidate(Instances trainingInstances) {
		this.naiveBayes = new NaiveBayes();
		initEvaluation(Utils.NAIVE_BAYES_CLASSIFIER);
		trainingInstances.randomize(new Random());
		try {
			naiveBayes.buildClassifier(trainingInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Training NaiveBayes classifier");
			for (int i = 1; i <= runs; i++) {
				evaluateCrossValidation(trainingInstances, i, naiveBayes);
			}
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}
	
	/**
	 * NaiveBayes classification. Builds the Naive Bayes classifier with the training instances
	 * and evaluates the classifier with the test Instances
	 * @param trainingInstances, instances to train the classifier
	 * @param testInstances, instances to test the classifier
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		this.naiveBayes = new NaiveBayes();
		initEvaluation(Utils.NAIVE_BAYES_CLASSIFIER);
		trainingInstances.randomize(new Random());
		testInstances.randomize(new Random());
		evaluateClassification(trainingInstances, testInstances, naiveBayes);
		return evaluation;
	}

}
