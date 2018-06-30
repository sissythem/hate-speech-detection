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
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

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
	 * @param features, the features of all instances
	 * @param labels, the labels of all instances
	 * @param writeToFile, boolean variable defining if instances will be exported in arff format
	 * @param folderNumber, the current fold
	 * @param filename, the name of the file that instances will be exported
	 * @return Instances
	 */
	public Instances generateInstances(List<Map<String, Double>> features, List<String> labels, int dataset, Boolean writeToFile,
			String startPath, int folderNumber, String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating attribtues");
		generateAttributes(features.get(0), dataset);
		instances = new Instances("tweets", attributes, 0);
		Utils.FILE_LOGGER.info(startingMessageLog + "Creating instances");
		for (int i = 0; i < features.size(); i++) {
			Instance instance = new DenseInstance(attributes.size());
			instance.setValue(attributes.get(0), labels.get(i));
			Map<String, Double> featureMap = features.get(i);
			attributes.forEach(attribute -> {
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
			writeToFile(instances, startPath, folderNumber, filename);
		}
		return instances;
	}

	/**
	 * Initializes an ArrayList of attributes based on the given features
	 * @param features, all existing features
	 * @param dataset, specific dataset
	 */
	private void generateAttributes(Map<String, Double> features, int dataset) {
		attributes = new ArrayList<>();
		List<String> classvalues = new ArrayList<>();
		switch(dataset) {
		case -1:
			classvalues.add(Utils.HATE_SPEECH_LABEL);
			classvalues.add(Utils.CLEAN_LABEL);
			break;
		case 0:
			classvalues.add(Utils.RACISM_LABEL);
			classvalues.add(Utils.SEXISM_LABEL);
			classvalues.add(Utils.CLEAN_LABEL);
			break;
		case 1:
			classvalues.add(Utils.HATE_SPEECH_LABEL);
			classvalues.add(Utils.OFFENSIVE_LANGUAGE_LABEL);
			classvalues.add(Utils.CLEAN_LABEL);
			break;
		}
		Attribute classAttribute = new Attribute("Class", classvalues);
		attributes.add(classAttribute);
		for (String key : features.keySet()) {
			attributes.add(new Attribute(key));
		}
	}
	
	/**
	 * Writes generated Instances in a file with Arff format
	 * @param instances, the instances to be exported in arff format
	 * @param startPath, the initial path to the instances
	 * @param folderNumber, the current fold
	 * @param filename, the name of the arff file
	 */
	private void writeToFile(Instances instances, String startPath, int folderNumber, String filename) {
		try {
			ArffSaver saver = new ArffSaver();
			saver.setInstances(instances);
			if(folderNumber == -1) {
				saver.setFile(new File(startPath + "/" + filename ));
			} else {
				saver.setFile(new File(startPath + folderNumber + "/" + filename));
			}
			saver.writeBatch();
		} catch (IOException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
		}
	}

	/**
	 * Reads an arff file containing instances
	 * @param startPath, the initial path to the arff file
	 * @param folderNumber, the current fold
	 * @param filename, the file to be read
	 * @return instances
	 */
	public Instances readInstancesFromFile(String startPath, int folderNumber, String filename) {
		try {
			String completePath = startPath + folderNumber + "/" + filename;
			Utils.FILE_LOGGER.info(startingMessageLog + "Total path: " + completePath);
			BufferedReader reader = new BufferedReader(new FileReader(new File(completePath)));
			Instances data = new Instances(reader);
			data.setClassIndex(0);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Merge all types of instances
	 * @param startPath, the initial path
	 * @param foldNum, the current fold
	 * @param filename, the file name where merged instances will be exported
	 */
	public void mergeAllGeneratedinstances(String startPath, int foldNum, String filename, String mergeKind) {
		try {
			for (int i = 0; i < foldNum; i++) {
				if(mergeKind.equalsIgnoreCase("all")) {
					createAllInstances(startPath,i,filename);
				} else if(mergeKind.equalsIgnoreCase("vectorAll")) {
					createVectorAllInstances(startPath,i,filename);
				} else if(mergeKind.equalsIgnoreCase("best")) {
					createBestInstnaces(startPath, i , filename);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove class attribute from instances. We use class attribute only from the
	 * first instances file
	 * @param instances, instances weka object from which will be removed the class attributes
	 * @return, the new instances object
	 */
	private Instances removeClassAttribute(Instances instances) {
		
		Remove af = new Remove();
	    Instances retI = null;
	    int[] toIgnore = {instances.classIndex()};
	    try {
	      af.setAttributeIndicesArray(toIgnore);
	      af.setInvertSelection(false);
	      af.setInputFormat(instances);
	      retI = Filter.useFilter(instances, af);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	    return retI;
	}

	/**
	 * Merge all instances both graph and vector features
	 * @param startPath, the path towards the specific dataset
	 * @param i, the specific fold
	 * @param filename, the filename to save the instances
	 */
	private void createAllInstances(String startPath, int i, String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging instances in fold " + i);
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading graph instances");
		Instances graphInstances = readInstancesFromFile(startPath + Utils.PATH_GRAPH_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading bow instances");
		Instances bowInstances = readInstancesFromFile(startPath + Utils.PATH_BOW_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading sentiment instances");
		Instances sentimentInstances = readInstancesFromFile(startPath + Utils.PATH_SENTIMENT_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading syntax instances");
		Instances syntaxInstances = readInstancesFromFile(startPath + Utils.PATH_SYNTAX_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading word2vec instances");
		Instances word2vecInstances = readInstancesFromFile(startPath + Utils.PATH_WORD2VEC_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading spelling instances");
		Instances spellingInstances = readInstancesFromFile(startPath + Utils.PATH_SPELLING_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading ngram instances");
		Instances ngramInstances = readInstancesFromFile(startPath + Utils.PATH_NGRAM_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading charngram instances");
		Instances charngramInstances = readInstancesFromFile(startPath + Utils.PATH_CHARNGRAM_INSTANCES, i,
				filename);

		bowInstances = removeClassAttribute(bowInstances);
		sentimentInstances = removeClassAttribute(sentimentInstances);
		syntaxInstances = removeClassAttribute(syntaxInstances);
		word2vecInstances = removeClassAttribute(word2vecInstances);
		spellingInstances = removeClassAttribute(spellingInstances);
		ngramInstances = removeClassAttribute(ngramInstances);
		charngramInstances = removeClassAttribute(charngramInstances);

		Utils.FILE_LOGGER.info(startingMessageLog + "Merging graph and bow instances");
		Instances instances = Instances.mergeInstances(graphInstances, bowInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging sentiment instances");
		instances = Instances.mergeInstances(instances, sentimentInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging syntax instances");
		instances = Instances.mergeInstances(instances, syntaxInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging spelling instances");
		instances = Instances.mergeInstances(instances, spellingInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging word2vec instances");
		instances = Instances.mergeInstances(instances, word2vecInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging ngram instances");
		instances = Instances.mergeInstances(instances, ngramInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging charngram instances");
		instances = Instances.mergeInstances(instances, charngramInstances);
		instances.setClassIndex(0);
		this.writeToFile(instances, startPath + Utils.PATH_ALL_INSTANCES, i, filename);
		this.instances = null;
	}

	/**
	 * Merge all vector features in instances file
	 * @param startPath, the path towards the specific dataset
	 * @param i, the current fold
	 * @param filename, the filename to save the instances
	 */
	private void createVectorAllInstances(String startPath, int i, String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging instances in fold " + i);
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading graph instances");
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading bow instances");
		Instances bowInstances = readInstancesFromFile(startPath + Utils.PATH_BOW_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading sentiment instances");
		Instances sentimentInstances = readInstancesFromFile(startPath + Utils.PATH_SENTIMENT_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading syntax instances");
		Instances syntaxInstances = readInstancesFromFile(startPath + Utils.PATH_SYNTAX_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading word2vec instances");
		Instances word2vecInstances = readInstancesFromFile(startPath + Utils.PATH_WORD2VEC_INSTANCES, i,
				filename);
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading spelling instances");
		Instances spellingInstances = readInstancesFromFile(startPath + Utils.PATH_SPELLING_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading ngram instances");
		Instances ngramInstances = readInstancesFromFile(startPath + Utils.PATH_NGRAM_INSTANCES, i,
				filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading charngram instances");
		Instances charngramInstances = readInstancesFromFile(startPath + Utils.PATH_CHARNGRAM_INSTANCES, i,
				filename);

		sentimentInstances = removeClassAttribute(sentimentInstances);
		syntaxInstances = removeClassAttribute(syntaxInstances);
		word2vecInstances = removeClassAttribute(word2vecInstances);
		spellingInstances = removeClassAttribute(spellingInstances);
		ngramInstances = removeClassAttribute(ngramInstances);
		charngramInstances = removeClassAttribute(charngramInstances);

		Utils.FILE_LOGGER.info(startingMessageLog + "Merging bow with sentiment instances");
		instances = Instances.mergeInstances(bowInstances, sentimentInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging syntax instances");
		instances = Instances.mergeInstances(instances, syntaxInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging spelling instances");
		instances = Instances.mergeInstances(instances, spellingInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging word2vec instances");
		instances = Instances.mergeInstances(instances, word2vecInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging ngram instances");
		instances = Instances.mergeInstances(instances, ngramInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging charngram instances");
		instances = Instances.mergeInstances(instances, charngramInstances);
		instances.setClassIndex(0);
		this.writeToFile(instances, startPath + Utils.PATH_VECTOR_ALL_INSTANCES, i, filename);
		this.instances = null;
	}

	/**
	 * Create Instances weka object with the top features (graph, bow and word2vec)
	 * @param startPath, the path towards the specific path
	 * @param i, the current fold
	 * @param filename, the filename to save the Best Instances
	 */
	private void createBestInstnaces(String startPath, int i, String filename) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging instances in fold " + i);
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading graph instances");
		Instances graphInstances = readInstancesFromFile(startPath + Utils.PATH_GRAPH_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading bow instances");
		Instances bowInstances = readInstancesFromFile(startPath + Utils.PATH_BOW_INSTANCES, i, filename);

		Utils.FILE_LOGGER.info(startingMessageLog + "Reading word2vec instances");
		Instances word2vecInstances = readInstancesFromFile(startPath + Utils.PATH_WORD2VEC_INSTANCES, i,
				filename);

		bowInstances = removeClassAttribute(bowInstances);
		word2vecInstances = removeClassAttribute(word2vecInstances);

		Utils.FILE_LOGGER.info(startingMessageLog + "Merging graph and bow instances");
		Instances instances = Instances.mergeInstances(graphInstances, bowInstances);
		Utils.FILE_LOGGER.info(startingMessageLog + "Merging word2vec instances");
		instances = Instances.mergeInstances(instances, word2vecInstances);
		instances.setClassIndex(0);
		this.writeToFile(instances, startPath + Utils.PATH_BEST_INSTANCES, i, filename);
		this.instances = null;
	}

	public Instances getInstances() {
		return instances;
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

}
