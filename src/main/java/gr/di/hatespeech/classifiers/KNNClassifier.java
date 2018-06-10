package gr.di.hatespeech.classifiers;

import java.util.Random;

import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

public class KNNClassifier extends BaseClassifier {
	private static String startingMessageLog = "[" + KNNClassifier.class.getSimpleName() + "] ";
	private IBk knn;

	public KNNClassifier() {
		super(Utils.KNN_CLASSIFIER);
	}

	public KNNClassifier(int folds, int runs) {
		super(Utils.KNN_CLASSIFIER, folds, runs);
	}

	/**
	 * Knn cross validation
	 * @param trainingInstances, instances to cross validate KNN classifier
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
				evaluateCrossValidation(trainingInstances, i, knn);
			}
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(e.getMessage(),e);
		}
		return evaluation;
	}
	
	/**
	 * Knn classification. Gets two Instances objects as input, one for training
	 * and one for evaluation
	 * @param trainingInstances, instances to train KNN classifier
	 * @param testInstances, instances to test KNN classifier
	 * @return ClassificationEvaluation result
	 */
	@Override
	public ClassificationEvaluation classify(Instances trainingInstances, Instances testInstances) {
		initEvaluation(Utils.KNN_CLASSIFIER);
		int k = 9;
		this.knn = new IBk(k);
		evaluateClassification(trainingInstances, testInstances, knn);
		return evaluation;
	}

}
