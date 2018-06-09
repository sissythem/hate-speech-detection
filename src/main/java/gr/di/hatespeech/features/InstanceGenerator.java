package gr.di.hatespeech.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.di.hatespeech.utils.Utils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * This class represents a generator of Weka Instances objects
 * @author sissy
 */
public class InstanceGenerator {
	private static String startingMessageLog = "[" + InstanceGenerator.class.getSimpleName() + "] ";

	protected Instances instances;
	protected ArrayList<Attribute> attributes;

	/**
	 * Default constructor
	 */
	public InstanceGenerator() {
		super();
	}

	/**
	 * Generates and returns an Instances object from a given List of Maps
	 * containing vector features and a List with the correct labels (e.g. hate
	 * speech or not) for all the training texts. If variable writeToFile is true
	 * the produced instances are saved in a file of arff format
	 * @param features
	 * @param labels
	 * @param writeToFile
	 * @param folderNumber
	 * @param filename
	 * @return Instances
	 */
	public Instances generateInstances(List<Map<String, Double>> features, List<String> labels, Boolean writeToFile,
			String startPath, int folderNumber, String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating attribtues");
		generateAttributes(features.get(0));
		instances = new Instances("tweets", attributes, 0);
		Utils.FILE_LOGGER.info(startingMessageLog + "Creating instances");
		for (int i = 0; i < features.size(); i++) {
			Instance instance = new DenseInstance(attributes.size());
			instance.setValue(attributes.get(0), labels.get(i));
			Map<String, Double> featureMap = features.get(i);
			attributes.stream().forEach(attribute -> {
				if (featureMap.containsKey(attribute.name())) {
					instance.setValue(attribute, featureMap.get(attribute.name()));
				}
			});
			instances.add(instance);

		}
		instances.setClass(attributes.get(0));
		instances.setClassIndex(0);
		if (writeToFile) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Writing instances to file: " + filename);
			writeToFile(startPath, folderNumber, filename);
		}
		return instances;
	}

	/**
	 * Initializes an ArrayList of attributes based on the given features
	 * @param features
	 */
	protected void generateAttributes(Map<String, Double> features) {
		attributes = new ArrayList<>();
		List<String> classvalues = new ArrayList<>();
		classvalues.add(Utils.HATE_SPEECH_LABEL);
		classvalues.add(Utils.CLEAN_LABEL);
//		classvalues.add(Utils.SEXISM_LABEL);
//		classvalues.add(Utils.RACISM_LABEL);
//		classvalues.add(Utils.OFFENSIVE_LANGUAGE_LABEL);
		Attribute classAttribute = new Attribute("Class", classvalues);
		attributes.add(classAttribute);
		for (String key : features.keySet()) {
			attributes.add(new Attribute(key));
		}
	}
	
	/**
	 * Writes generated Instances in a file with Arff format
	 */
	protected void writeToFile(String startPath, int folderNumber, String filename) {
		try {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(instances);
			saver.setFile(new File(startPath + folderNumber + "/" + filename));
			saver.writeBatch();
		} catch (IOException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
		}
	}
	
	public Instances readInstancesFromFile(String startPath, int folderNumber, String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(startPath + folderNumber + "/" + filename)));
			Instances data = new Instances(reader);
			data.setClassIndex(0);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void mergeAllGeneratedinstances(int foldNum, String filename) {
		for(int i=0;i<foldNum; i++) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging instances in fold " + i);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading graph instances");
			Instances graphInstances = readInstancesFromFile(Utils.PATH_GRAPH_INSTANCES, i, filename);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading bow instances");
			Instances bowInstances = readInstancesFromFile(Utils.PATH_BOW_INSTANCES, i, filename);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading sentiment instances");
			Instances sentimentInstances = readInstancesFromFile(Utils.PATH_SENTIMENT_INSTANCES, i, filename);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading syntax instances");
			Instances syntaxInstances = readInstancesFromFile(Utils.PATH_SYNTAX_INSTANCES, i, filename);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading word2vec instances");
			Instances word2vecInstances = readInstancesFromFile(Utils.PATH_WORD2VEC_INSTANCES, i, filename);
			Utils.FILE_LOGGER.info(startingMessageLog + "Reading spelling instances");
			Instances spellingInstances = readInstancesFromFile(Utils.PATH_SPELLING_INSTANCES, i, filename);
			
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging graph and bow instances");
			Instances temp = Instances.mergeInstances(graphInstances, bowInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging sentiment instances");
			temp = Instances.mergeInstances(temp, sentimentInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging syntax instances");
			temp = Instances.mergeInstances(temp, syntaxInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging spelling instances");
			temp = Instances.mergeInstances(temp, spellingInstances);
			Utils.FILE_LOGGER.info(startingMessageLog + "Merging word2vec instances");
			temp = Instances.mergeInstances(temp, word2vecInstances);
			
			this.instances = temp;
			this.writeToFile(Utils.PATH_ALL_INSTANCES, i, filename);
		}
	}

	public Instances getInstances() {
		return instances;
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

}
