package gr.di.hatespeech.classifiers;

import java.util.Random;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

/**
 * LogisticRegression classifier extends the BaseClassifier
 * and uses Logistic implementation from Weka in order to classify
 * text instances.
 * @author sissy
 */
public class LogisticRegressionClassifier extends BaseClassifier {
	private static String startingMessageLog = "[" + LogisticRegressionClassifier.class.getSimpleName() + "] ";
	protected Logistic lrClassifier;
	
	/**
	 * Default constructor
	 */
	public LogisticRegressionClassifier()
	{
		super(Utils.LOGISTIC_REGRESSION_CLASSIFIER);
	}

	public LogisticRegressionClassifier(int folds, int runs) {
		super(Utils.LOGISTIC_REGRESSION_CLASSIFIER, folds, runs);
	}
	
	/**
	 * Classifies given Instances using the Logistic classifier from Weka.
	 * Constructs a List with the classifier's responses for each Instance
	 * @param trainingInstances, an Instances object (weka library)
	 * @return evaluation, Classification evaluation with a list of all evaluations
	 * and the classifier's name
	 */
	@Override
	public ClassificationEvaluation crossValidate(Instances trainingInstances) {
		this.lrClassifier = new Logistic();
		initEvaluation(Utils.LOGISTIC_REGRESSION_CLASSIFIER);
		trainingInstances.randomize(new Random());
		try {
			lrClassifier.buildClassifier(trainingInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Training LogisticRegression classifier");
			for(int i=1; i<=runs;i++) {
				evaluateCrossValidation(trainingInstances, i, lrClassifier);
			}
		} catch(Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}
	
	/**
	 * Logistic regression classification. Trains the classifier with the trainingInstances
	 * and evaluates the model with the testInstances.
	 * @param trainingInstances
	 * @param testInstances
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		this.lrClassifier = new Logistic();
		initEvaluation(Utils.LOGISTIC_REGRESSION_CLASSIFIER);
		trainingInstances.randomize(new Random());
		testInstances.randomize(new Random());
		evaluateClassification(trainingInstances, testInstances, lrClassifier);
		return evaluation;
	}

}
