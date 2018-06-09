package gr.di.hatespeech.runners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import gr.di.hatespeech.dataexporters.FeatureExporter;
import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.features.InstanceGenerator;
import gr.di.hatespeech.features.NgramGraphFeatureExtractor;
import gr.di.hatespeech.features.NgramGraphMultiLabelFeatureExtractor;
import gr.di.hatespeech.utils.Utils;
import weka.core.Instances;

public class InstanceGeneratorRunner {
	private static String startingMessageLog = "[" + InstanceGeneratorRunner.class.getSimpleName() + "] ";
	private int foldNumber;
	private int dataset;
	private Properties config;
	private List<Feature> existingFeatures;
	private List<TextFeature> existingTextFeatures;
	private Map<Integer,List<Text>> totalFolds;
	private String pathToInstances;
	
	private NgramGraphFeatureExtractor ngramGraphFeatureExtractor;
	private NgramGraphMultiLabelFeatureExtractor ngramGraphMultiLabelFeatureExtractor;
	
	/** Training and test features & labels to create Instances **/
	private List<Map<String, Double>> trainingFeatures;
	private List<String> trainingLabels;
	private List<Map<String, Double>> testingFeatures;
	private List<String> testLabels;
	
	/** Instances **/
	private Instances trainingInstances;
	private Instances testInstances;
	
	public InstanceGeneratorRunner(int foldNumber, Properties config, List<Feature> existingFeatures,
			List<TextFeature> existingTextFeatures, Map<Integer,List<Text>> totalFolds, String pathToInstances) {
		super();
		this.dataset = Integer.parseInt(config.getProperty(Utils.DATASET));
		this.foldNumber = foldNumber;
		this.config = config;
		this.existingFeatures = existingFeatures;
		this.existingTextFeatures = existingTextFeatures;
		this.totalFolds = totalFolds;
		this.pathToInstances = pathToInstances;
	}

	public void runGenerateNewInstances() {
		// get training and test texts
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating new instances");
		Map<Integer, List<Text>> tempFolds = new HashMap<>();
		tempFolds.putAll(totalFolds);
		List<Text> testTexts = tempFolds.get(foldNumber);
		tempFolds.remove(foldNumber);
		List<Text> trainingTexts = getTrainingTexts(tempFolds);
		generateNewInstances(dataset, foldNumber, testTexts, trainingTexts);
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
	 * Initialization of all ArrayList containing features and the relevant labels
	 * for the training and test instances
	 */
	protected void initContainers() {
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
	protected void generateNewInstances(int dataset, int i, List<Text> testTexts, List<Text> trainingTexts) {
		initContainers();
		// get vector and graph features
		generateFeatures(trainingTexts, testTexts, dataset);
		testInstances = getInstances(testingFeatures, testLabels, dataset, i, Utils.TEST_INSTACES_FILE);
		trainingInstances = getInstances(trainingFeatures, trainingLabels, dataset, i, Utils.TRAIN_INSTANCES_FILE);
	}

	/**
	 * Generate graph and vector features
	 * @param trainingTexts
	 * @param testTexts
	 */
	protected void generateFeatures(List<Text> trainingTexts, List<Text> testTexts, int dataset) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph features: " + config.getProperty(Utils.GRAPH_FEATURES));
		initGraphFeatureExtractor(trainingTexts, dataset);
		String vectorFeaturesConfig = config.getProperty(Utils.VECTOR_FEATURES);
		Utils.FILE_LOGGER.info(startingMessageLog + "Vector instances to create: " + vectorFeaturesConfig);
		trainingTexts.stream().forEach(text -> {
			Map<String,Double> feats = getFeatures(ngramGraphFeatureExtractor, text, vectorFeaturesConfig, dataset);
			trainingFeatures.add(feats);
			if(dataset == -1) {
				trainingLabels.add(text.getLabel());
			} else {
				trainingLabels.add(text.getOldLabel());
			}
		});
		testTexts.stream().forEach(text -> {
			Map<String,Double> feats = getFeatures(ngramGraphFeatureExtractor, text, vectorFeaturesConfig, dataset);
			testingFeatures.add(feats);
			if(dataset == -1) {
				testLabels.add(text.getLabel());
			} else {
				testLabels.add(text.getOldLabel());
			}
		});
	}
	
	protected void initGraphFeatureExtractor(List<Text> trainingTexts, int dataset) {
		if (Boolean.parseBoolean(config.getProperty(Utils.GRAPH_FEATURES))) {
			List<Text> trainingForClassGraph = getTrainingDataForGraph(trainingTexts);
			if(dataset == -1) {
				ngramGraphFeatureExtractor = new NgramGraphFeatureExtractor(trainingForClassGraph, config.getProperty(Utils.GRAPH_TYPE));
			} else {
				ngramGraphMultiLabelFeatureExtractor = new NgramGraphMultiLabelFeatureExtractor(trainingForClassGraph, config.getProperty(Utils.GRAPH_TYPE), dataset);
			}
		}
		else {
			if (dataset == -1) {
				ngramGraphFeatureExtractor = new NgramGraphFeatureExtractor();	
			} else {
				ngramGraphMultiLabelFeatureExtractor = new NgramGraphMultiLabelFeatureExtractor();
			}
		}
	}

	/**
	 * Get a 90% of training instances, to create the two class Graphs
	 * @param trainingTexts
	 * @return
	 */
	protected List<Text> getTrainingDataForGraph(List<Text> trainingTexts) {
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
	protected Map<String, Double> getFeatures(NgramGraphFeatureExtractor ngramGraphFeatureExtractor, Text text,
			String featuresConfig, int dataset) {
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
			if (dataset == -1) {
				textFeatures.putAll(ngramGraphFeatureExtractor.extractFeatures(text));
			} else {
				textFeatures.putAll(ngramGraphMultiLabelFeatureExtractor.extractFeatures(text));
			}
		}
		return textFeatures;
	}

	/**
	 * Get already generated vector features for a specific text
	 * @param kind
	 * @param text
	 */
	protected Map<String, Double> getExistingVectorFeatures(String kind, Text text) {
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
	
	protected Map<String,Double> initVectorMap() {
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
	protected Instances getInstances(List<Map<String, Double>> allFeatures, List<String> allLabels, int dataset, int folderNumber,
			String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating new instances for texts no: " + allLabels.size());
		InstanceGenerator instanceGenerator = new InstanceGenerator();
		instanceGenerator.generateInstances(allFeatures, allLabels, dataset,
				Boolean.parseBoolean(config.getProperty(Utils.INSTANCES_TO_FILE)), pathToInstances, folderNumber, filename);
		return instanceGenerator.getInstances();
	}

	public Instances getTrainingInstances() {
		return trainingInstances;
	}

	public void setTrainingInstances(Instances trainingInstances) {
		this.trainingInstances = trainingInstances;
	}

	public Instances getTestInstances() {
		return testInstances;
	}

	public void setTestInstances(Instances testInstances) {
		this.testInstances = testInstances;
	}

}
