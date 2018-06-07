package gr.di.hatespeech.runners;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import gr.di.hatespeech.classifiers.KNNClassifier;
import gr.di.hatespeech.classifiers.LogisticRegressionClassifier;
import gr.di.hatespeech.classifiers.NaiveBayesClassifier;
import gr.di.hatespeech.dataexporters.FeatureExporter;
import gr.di.hatespeech.entities.ClassificationEvaluation;
import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.features.InstanceGenerator;
import gr.di.hatespeech.features.NgramGraphFeatureExtractor;
import gr.di.hatespeech.utils.Utils;
import weka.core.Instances;

public class FoldRunner implements Runnable {
	private static String startingMessageLog = "[" + FoldRunner.class.getSimpleName() + "] ";
	private String pathToInstances;
	private int foldNumber;
	private Properties config;
	private NgramGraphFeatureExtractor ngramGraphFeatureExtractor;

	private Map<Integer, List<Text>> totalFolds = new HashMap<>();
	private Map<Integer, List<ClassificationEvaluation>> totalEvaluations = new HashMap<>();

	/** Instances **/
	private Instances trainingInstances;
	private Instances testInstances;

	/** Training and test features & labels to create Instances **/
	private List<Map<String, Double>> trainingFeatures;
	private List<String> trainingLabels;
	private List<Map<String, Double>> testingFeatures;
	private List<String> testLabels;

	private List<Feature> existingFeatures = new ArrayList<>();
	private List<TextFeature> existingTextFeatures = new ArrayList<>();
	
	private NaiveBayesClassifier naiveBayes = new NaiveBayesClassifier();
	private LogisticRegressionClassifier logisticRegressionClassifier = new LogisticRegressionClassifier();
	private KNNClassifier knnClassifier = new KNNClassifier();

	public FoldRunner(int foldNumber, Properties config, List<Feature> existingFeatures,
			List<TextFeature> existingTextFeatures, Map<Integer,List<Text>> totalFolds, String pathToInstances) {
		this.foldNumber = foldNumber;
		this.config = config;
		this.existingFeatures = existingFeatures;
		this.existingTextFeatures = existingTextFeatures;
		this.totalFolds = totalFolds;
		this.pathToInstances = pathToInstances;
	}

	@Override
	public void run() {
		runFold(foldNumber);
	}

	private void runFold(int foldNumber) {
		Utils.FILE_LOGGER.info(startingMessageLog + "### Running fold " + foldNumber);
		LocalDateTime start = Utils.tic();
		// get Instances
		trainingInstances = null;
		testInstances = null;

		if (config.getProperty(Utils.INSTANCES).equals("new")) {
			// get training and test texts
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating new instances");
			Map<Integer, List<Text>> tempFolds = new HashMap<>();
			tempFolds.putAll(totalFolds);
			List<Text> testTexts = tempFolds.get(foldNumber);
			tempFolds.remove(foldNumber);
			List<Text> trainingTexts = getTrainingTexts(tempFolds);
			generateNewInstances(foldNumber, testTexts, trainingTexts);
		} else {
			readInstancesFromFile(foldNumber);
		}
		// classify
		List<ClassificationEvaluation> evaluations = classify(trainingInstances, testInstances);
		totalEvaluations.put(foldNumber, evaluations);
		long seconds = Utils.toc(start);
		Utils.FILE_LOGGER.info(startingMessageLog + " ## Time needded in seconds to execute fold no " + foldNumber + " is: " + seconds);
	}

	/**
	 * Get training texts from remaining folds
	 * @return
	 */
	private List<Text> getTrainingTexts(Map<Integer, List<Text>> tempFolds) {
		// get training texts
		List<Text> trainingTexts = new ArrayList<>();
		for (Integer key : tempFolds.keySet()) {
			trainingTexts.addAll(tempFolds.get(key));
		}
		return trainingTexts;
	}

	/**
	 * Get existing instances from file
	 * @param i
	 */
	private void readInstancesFromFile(int i) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading instances from file in path " + pathToInstances);
		InstanceGenerator instanceGenerator = new InstanceGenerator();
		trainingInstances = instanceGenerator.readInstancesFromFile(pathToInstances, i, Utils.TRAIN_INSTANCES_FILE);
		testInstances = instanceGenerator.readInstancesFromFile(pathToInstances, i, Utils.TEST_INSTACES_FILE);
	}

	/**
	 * Initialization of all ArrayList containing features and the relevant labels
	 * for the training and test instances
	 */
	private void initContainers() {
		trainingFeatures = new ArrayList<>();
		trainingLabels = new ArrayList<>();
		testingFeatures = new ArrayList<>();
		testLabels = new ArrayList<>();
	}

	/**
	 * Generate instances from scratch
	 * @param i
	 * @param testTexts
	 * @param trainingTexts
	 */
	private void generateNewInstances(int i, List<Text> testTexts, List<Text> trainingTexts) {
		initContainers();
		// get vector and graph features
		generateFeatures(trainingTexts, testTexts);
		testInstances = getInstances(testingFeatures, testLabels, i, Utils.TEST_INSTACES_FILE);
		trainingInstances = getInstances(trainingFeatures, trainingLabels, i, Utils.TRAIN_INSTANCES_FILE);
	}

	/**
	 * Generate graph and vector features
	 * @param trainingTexts
	 * @param testTexts
	 */
	private void generateFeatures(List<Text> trainingTexts, List<Text> testTexts) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph features: " + config.getProperty(Utils.GRAPH_FEATURES));
		if (Boolean.parseBoolean(config.getProperty(Utils.GRAPH_FEATURES))) {
			List<Text> trainingForClassGraph = getTrainingDataForGraph(trainingTexts);
			ngramGraphFeatureExtractor = new NgramGraphFeatureExtractor(trainingForClassGraph, config.getProperty(Utils.GRAPH_TYPE));
		} else {
			ngramGraphFeatureExtractor = new NgramGraphFeatureExtractor();	
		}
		String vectorFeaturesConfig = config.getProperty(Utils.VECTOR_FEATURES);
		Utils.FILE_LOGGER.info(startingMessageLog + "Vector instances to create: " + vectorFeaturesConfig);
		trainingTexts.stream().forEach(text -> {
			Map<String,Double> feats = getFeatures(ngramGraphFeatureExtractor, text, vectorFeaturesConfig);
			trainingFeatures.add(feats);
			trainingLabels.add(text.getLabel());
		});
		testTexts.stream().forEach(text -> {
			Map<String,Double> feats = getFeatures(ngramGraphFeatureExtractor, text, vectorFeaturesConfig);
			testingFeatures.add(feats);
			testLabels.add(text.getLabel());
		});
	}

	/**
	 * Get a 90% of training instances, to create the two class Graphs
	 * @param trainingTexts
	 * @return
	 */
	private List<Text> getTrainingDataForGraph(List<Text> trainingTexts) {
		List<Text> trainingForClassGraph = new ArrayList<>();
		Utils.FILE_LOGGER.info(startingMessageLog + "Number of training texts: " + trainingTexts.size());
		trainingForClassGraph.addAll(trainingTexts);
		trainingForClassGraph = trainingForClassGraph.stream().limit(trainingForClassGraph.size() * 90 / 100)
				.collect(Collectors.toList());
		Utils.FILE_LOGGER.info(startingMessageLog + "Number of training texts to create class graph: " + trainingForClassGraph.size());
		return trainingForClassGraph;
	}

	/**
	 * Generate all features (vector and graph) for training texts
	 * @param ngramGraphFeatureExtractor
	 * @param text
	 */
	public Map<String, Double> getFeatures(NgramGraphFeatureExtractor ngramGraphFeatureExtractor, Text text,
			String featuresConfig) {
		Map<String, Double> textFeatures = new HashMap<>();
		// get vector features
		switch (featuresConfig) {
		case "existing":
			textFeatures.putAll(getExistingVectorFeatures(config.getProperty(Utils.FEATURES_KIND), text));
			break;
		case "new":
			FeatureExporter featureExporter = new FeatureExporter();
			textFeatures.putAll(featureExporter.getVectorFeatures(text));
			break;
		case "none":
			break;
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.GRAPH_FEATURES))) {
			// get graph features
			textFeatures.putAll(ngramGraphFeatureExtractor.extractFeatures(text));
		}
		return textFeatures;
	}

	/**
	 * Get already generated vector features for a specific text
	 * @param kind
	 * @param text
	 */
	public Map<String, Double> getExistingVectorFeatures(String kind, Text text) {
		Map<String, Double> textFeatures = initVectorMap();
		if (config.getProperty(Utils.DATASOURCE).equals("database")) {
			List<TextFeature> temp = existingTextFeatures.stream().filter(tf -> tf.getText().getId().equals(text.getId())).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(temp)) {
				temp.stream().forEach(tf -> {
					textFeatures.put(tf.getFeature().getDescription(), tf.getValue());
				});
			}
		} else if (config.getProperty(Utils.DATASOURCE).equals("csv")) {
			if (kind.equals("all")) {
				existingTextFeatures = existingTextFeatures.stream()
						.filter(textFeature -> textFeature.getText().equals(text)).collect(Collectors.toList());
			} else {
				existingTextFeatures = existingTextFeatures.stream()
						.filter(textFeature -> textFeature.getText().equals(text)
								&& textFeature.getFeature().getDescription().equals(kind))
						.collect(Collectors.toList());
			}
		}
		return textFeatures;
	}
	
	private Map<String,Double> initVectorMap() {
		Map<String,Double> vectorFeatures = new HashMap<>();
		existingFeatures.stream().forEach(feature -> {
			vectorFeatures.put(feature.getDescription(), 0.0);
		});
		return vectorFeatures;
	}

	/**
	 * Get Weka Instances
	 * @param allFeatures
	 * @param allLabels
	 * @param folderNumber
	 * @param filename
	 * @return
	 */
	private Instances getInstances(List<Map<String, Double>> allFeatures, List<String> allLabels, int folderNumber,
			String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating new instances for texts no: " + allLabels.size());
		InstanceGenerator instanceGenerator = new InstanceGenerator();
		instanceGenerator.generateInstances(allFeatures, allLabels,
				Boolean.parseBoolean(config.getProperty(Utils.INSTANCES_TO_FILE)), pathToInstances, folderNumber, filename);
		return instanceGenerator.getInstances();
	}

	/**
	 * Train classifiers with trainingInstances and evaluate them with the
	 * testInstances
	 * @param trainingInstances
	 * @param testInstances
	 * @return list of ClassificationEvaluation
	 */
	private List<ClassificationEvaluation> classify(Instances trainingInstances, Instances testInstances) {
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
			classificationEvaluation.writeClassificationEvalToFile(classificationEvaluation.getTrainEval(),pathToInstances, foldNumber, Utils.TRAIN_INSTANCES_FILE);
			classificationEvaluation.writeClassificationEvalToFile(classificationEvaluation.getTestEval(),pathToInstances, foldNumber, Utils.TEST_INSTACES_FILE);
		});
		return evaluations;
	}

}
