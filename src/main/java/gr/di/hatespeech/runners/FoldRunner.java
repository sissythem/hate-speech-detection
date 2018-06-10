package gr.di.hatespeech.runners;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.features.InstanceGenerator;
import gr.di.hatespeech.utils.Utils;
import weka.core.Instances;

public class FoldRunner implements Runnable {
	private static String startingMessageLog = "[" + FoldRunner.class.getSimpleName() + "] ";
	
	protected int foldNumber;
	protected Properties config;
	protected List<Feature> existingFeatures;
	protected List<TextFeature> existingTextFeatures;
	protected Map<Integer, List<Text>> totalFolds;
	protected String pathToInstances;
	
	/** Instances **/
	protected Instances trainingInstances;
	protected Instances testInstances;

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

	protected void runFold(int foldNumber) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Running fold " + foldNumber);
		LocalDateTime start = Utils.tic();
		if (config.getProperty(Utils.INSTANCES).equals("new")) {
			generateNewInstances(foldNumber);
		} else {
			readInstancesFromFile(foldNumber);
		}
		runClassification(foldNumber);
		long seconds = Utils.toc(start);
		Utils.FILE_LOGGER.info(startingMessageLog + "Time needded in seconds to execute fold no " + foldNumber
				+ " is: " + seconds);
	}

	/**
	 * Create new instances using InstanceClassificationRunner
	 * @param foldNumber
	 */
	protected void generateNewInstances(int foldNumber) {
		InstanceClassificationRunner instancesRunner = new InstanceClassificationRunner(foldNumber, config, existingFeatures, existingTextFeatures, totalFolds, pathToInstances);
		instancesRunner.runGenerateNewInstances();;
		trainingInstances = instancesRunner.getTrainingInstances();
		testInstances = instancesRunner.getTestInstances();
	}

	/**
	 * Get existing instances from file
	 * @param i
	 */
	protected void readInstancesFromFile(int i) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Reading instances from file in path " + pathToInstances);
		InstanceGenerator instanceGenerator = new InstanceGenerator();
		trainingInstances = instanceGenerator.readInstancesFromFile(pathToInstances, i, Utils.TRAIN_INSTANCES_FILE);
		testInstances = instanceGenerator.readInstancesFromFile(pathToInstances, i, Utils.TEST_INSTACES_FILE);
	}
	
	/**
	 * Create Classification runner and provide the train and test instances
	 * @param foldNumber
	 */
	protected void runClassification(int foldNumber) {
		ClassificationRunner classificationRunner = new ClassificationRunner(foldNumber, config, pathToInstances, trainingInstances, testInstances);
		classificationRunner.runClassification();
	}

}
