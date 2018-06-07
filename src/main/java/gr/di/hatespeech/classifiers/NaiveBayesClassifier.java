package gr.di.hatespeech.classifiers;

import java.util.Random;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

/**
 * This class extends BaseClassifier abstract class implementing a
 * NaiveBayes classifier.
 * Uses the NaiveBayes classifier from Weka.
 * @author sissy
 */
public class NaiveBayesClassifier extends BaseClassifier {
	private static String startingMessageLog = "[" + NaiveBayesClassifier.class.getSimpleName() + "] ";
	protected NaiveBayes naiveBayes;

	/**
	 * Default constructor. Initialization of weka NaiveBayes classifier
	 */
	public NaiveBayesClassifier() {
		super(Utils.NAIVE_BAYES_CLASSIFIER);
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
		try {
			naiveBayes.buildClassifier(trainingInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Training NaiveBayes classifier");
			for (int i = 1; i <= runs; i++) {
				Evaluation eval = new Evaluation(trainingInstances);
				evaluation.addCrossValidationEval(eval);
				eval.crossValidateModel(naiveBayes, trainingInstances, folds, new Random(i));
				Double kappa = eval.kappa();
				Double fMeasure = eval.weightedFMeasure();
				String confusionMatrix = eval.toMatrixString("Confusion matrix: ");
				Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
			}
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}
	
	/**
	 * NaiveBayes classification. Builds the Naive Bayes classifier with the training instances
	 * and evaluates the classifier with the test Instances
	 * @param trainingInstances
	 * @param testInstances
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		this.naiveBayes = new NaiveBayes();
		initEvaluation(Utils.NAIVE_BAYES_CLASSIFIER);
		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "Building classifier");
			naiveBayes.buildClassifier(trainingInstances);
			Evaluation trainEval = new Evaluation(trainingInstances);
			evaluation.setTrainEval(trainEval);
			evaluation.setTrainingPredictions(trainEval.evaluateModel(naiveBayes, trainingInstances));
			for(int instanceIndex =0; instanceIndex < testInstances.numInstances(); instanceIndex++) {
				naiveBayes.classifyInstance(testInstances.get(instanceIndex));
			}
			Utils.FILE_LOGGER.info(startingMessageLog + "Testing classifier");
			Evaluation testEval = new Evaluation(trainingInstances);
			evaluation.setTestPredictions(testEval.evaluateModel(naiveBayes, testInstances));
			evaluation.setTestEval(testEval);
			Double kappa = testEval.kappa();
			Double fMeasure = testEval.weightedFMeasure();
			String confusionMatrix = testEval.toMatrixString("Confusion matrix: ");
			Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
			Utils.FILE_LOGGER.info(startingMessageLog + "Summary: " + testEval.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}

}
