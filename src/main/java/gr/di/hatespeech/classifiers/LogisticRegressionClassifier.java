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
	public LogisticRegressionClassifier() {
		super(Utils.LOGISTIC_REGRESSION_CLASSIFIER);
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
				Evaluation eval = new Evaluation(trainingInstances);
				evaluation.addCrossValidationEval(eval);
				eval.crossValidateModel(lrClassifier, trainingInstances, folds, new Random(i));
				Double kappa = eval.kappa();
				Double fMeasure = eval.weightedFMeasure();
				String confusionMatrix = eval.toMatrixString("Confusion matrix: ");
				Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
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
		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "Building classifier");
			lrClassifier.buildClassifier(trainingInstances);
			Evaluation trainEval = new Evaluation(trainingInstances);
			evaluation.setTrainEval(trainEval);
			evaluation.setTrainingPredictions(trainEval.evaluateModel(lrClassifier, trainingInstances));
			for(int instanceIndex =0; instanceIndex < testInstances.numInstances(); instanceIndex++) {
				lrClassifier.classifyInstance(testInstances.get(instanceIndex));
			}
			Utils.FILE_LOGGER.info(startingMessageLog + "Testing classifier");
			Evaluation testEval = new Evaluation(trainingInstances);
			evaluation.setTestPredictions(testEval.evaluateModel(lrClassifier, testInstances));
			evaluation.setTestEval(testEval);
			Double kappa = testEval.kappa();
			Double fMeasure = testEval.weightedFMeasure();
			String confusionMatrix = testEval.toMatrixString("Confusion matrix: ");
			Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
			Utils.FILE_LOGGER.info(startingMessageLog + "Summary: " + testEval.toSummaryString());
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return evaluation;
	}

}
