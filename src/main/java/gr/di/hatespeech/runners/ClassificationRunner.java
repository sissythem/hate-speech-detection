package gr.di.hatespeech.runners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import gr.di.hatespeech.classifiers.KNNClassifier;
import gr.di.hatespeech.classifiers.LogisticRegressionClassifier;
import gr.di.hatespeech.classifiers.NaiveBayesClassifier;
import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.utils.Utils;
import weka.core.Instances;

public class ClassificationRunner {
	private static String startingMessageLog = "[" + ClassificationRunner.class.getSimpleName() + "] ";
	private int foldNumber;
	private Properties config;
	private String pathToInstances;
	private Instances trainingInstances;
	private Instances testInstances;

	private Map<Integer, List<ClassificationEvaluation>> totalEvaluations = new HashMap<>();
	private NaiveBayesClassifier naiveBayes = new NaiveBayesClassifier();
	private LogisticRegressionClassifier logisticRegressionClassifier = new LogisticRegressionClassifier();
	private KNNClassifier knnClassifier = new KNNClassifier();

	public ClassificationRunner(int foldNumber, Properties config, String pathToInstances, Instances trainingInstances,
			Instances testInstances) {
		super();
		this.foldNumber = foldNumber;
		this.config = config;
		this.pathToInstances = pathToInstances;
		this.trainingInstances = trainingInstances;
		this.testInstances = testInstances;
	}

	public void runClassification() {
		// classify
		List<ClassificationEvaluation> evaluations = classify(trainingInstances, testInstances);
		totalEvaluations.put(foldNumber, evaluations);

	}

	/**
	 * Train classifiers with trainingInstances and evaluate them with the
	 * testInstances
	 * 
	 * @param trainingInstances
	 * @param testInstances
	 * @return list of ClassificationEvaluation
	 */
	protected List<ClassificationEvaluation> classify(Instances trainingInstances, Instances testInstances) {
		List<ClassificationEvaluation> evaluations = new ArrayList<>();
		if (Boolean.parseBoolean(config.getProperty(Utils.NAIVE_BAYES_CLASSIFIER))) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Classifying with Naive Bayes");
			evaluations.add(naiveBayes.classify(trainingInstances, testInstances));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.LOGISTIC_REGRESSION_CLASSIFIER))) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Classifying with Logistic Regression");
			evaluations.add(logisticRegressionClassifier.classify(trainingInstances, testInstances));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.KNN_CLASSIFIER))) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Classifying with Knn Classifier");
			evaluations.add(knnClassifier.classify(trainingInstances, testInstances));
		}
		evaluations.stream().forEach(classificationEvaluation -> {
			classificationEvaluation.writeClassificationEvalToFile(classificationEvaluation.getTrainEval(),
					pathToInstances, foldNumber, Utils.TRAIN_INSTANCES_FILE);
			classificationEvaluation.writeClassificationEvalToFile(classificationEvaluation.getTestEval(),
					pathToInstances, foldNumber, Utils.TEST_INSTACES_FILE);
		});
		return evaluations;
	}

}
