package gr.di.hatespeech.classifiers;

import java.util.Random;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

public class KNNClassifier extends BaseClassifier {
	private static String startingMessageLog = "[" + KNNClassifier.class.getSimpleName() + "] ";
	protected IBk knn;

	public KNNClassifier() {
		super(Utils.KNN_CLASSIFIER);
	}

	/**
	 * Knn cross validation
	 * @param trainingInstances
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation crossValidate(Instances trainingInstances) {
		initEvaluation(Utils.KNN_CLASSIFIER);
		Utils.FILE_LOGGER.info(startingMessageLog + "Training KNN classifier");
		int k = (int) Math.ceil(Math.sqrt(trainingInstances.numInstances()));
		this.knn = new IBk(k);
		trainingInstances.randomize(new Random());
		try {
			knn.buildClassifier(trainingInstances);
			for(int i=1; i<=runs;i++) {
				Evaluation eval = new Evaluation(trainingInstances);
				evaluation.addCrossValidationEval(eval);
				eval.crossValidateModel(knn, trainingInstances, folds, new Random(i));
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
	 * Knn classification. Gets two Instances objects as input, one for training
	 * and one for evaluation
	 * @param trainingInstances
	 * @param testInstances
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		initEvaluation(Utils.KNN_CLASSIFIER);
		int k = 9;
		this.knn = new IBk(k);
		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "Building classifier");
			trainingInstances.randomize(new Random());
			testInstances.randomize(new Random());
			knn.buildClassifier(trainingInstances);
			Evaluation trainEval = new Evaluation(trainingInstances);
			evaluation.setTrainEval(trainEval);
			evaluation.setTrainingPredictions(trainEval.evaluateModel(knn, trainingInstances));
			for(int instanceIndex =0; instanceIndex < testInstances.numInstances(); instanceIndex++) {
				knn.classifyInstance(testInstances.get(instanceIndex));
			}
			Utils.FILE_LOGGER.info(startingMessageLog + "Testing classifier");
			Evaluation testEval = new Evaluation(trainingInstances);
			evaluation.setTestPredictions(testEval.evaluateModel(knn, testInstances));
			evaluation.setTestEval(testEval);
			Double kappa = testEval.kappa();
			Double fMeasure = testEval.weightedFMeasure();
			String confusionMatrix = testEval.toMatrixString("Confusion matrix: ");
			Utils.FILE_LOGGER.info(startingMessageLog + "Kappa - " + kappa + ", fMeasure - " + fMeasure + ", confusionMatrix - " + confusionMatrix);
			Utils.FILE_LOGGER.info(startingMessageLog + "Summary: " + testEval.toSummaryString());
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}

}
