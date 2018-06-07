package gr.di.hatespeech.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.PropertyConfigurator;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.readers.FeatureCsvReader;
import gr.di.hatespeech.readers.TextFeatureCsvReader;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.repositories.TextFeatureRepository;
import gr.di.hatespeech.runners.FoldRunner;
import gr.di.hatespeech.splitters.NfoldDatasetSplitter;
import gr.di.hatespeech.utils.Utils;

/**
 * Main program to execute classification
 * @author sissy
 */
public class HateSpeechDetection {
	private static String startingMessageLog = "[" + HateSpeechDetection.class.getSimpleName() + "] ";
	private Properties config = new Properties();
	private Map<Integer, List<Text>> totalFolds = new HashMap<>();
	private List<Feature> existingFeatures = new ArrayList<>();
	private List<TextFeature> existingTextFeatures = new ArrayList<>();

	public static void main(String[] args) {
		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "### PROGRAM START");
			HateSpeechDetection hsd = new HateSpeechDetection();
			hsd.readConfigurationFile();
			if (hsd.config.getProperty(Utils.INSTANCES).equals("new")
					&& hsd.config.getProperty(Utils.VECTOR_FEATURES).equals("existing")) {
				hsd.generateExistingFeatures();
			}
			hsd.runFolds();
			Utils.FILE_LOGGER.info(startingMessageLog + "### PROGRAM FINISH");
			Utils.FILE_LOGGER.info(startingMessageLog + "======================================================");
		} catch (Exception e) {
			e.printStackTrace();
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(), e);
		}
	}

	/**
	 * Read existing features from database or csv
	 */
	private void generateExistingFeatures() {
		if (config.getProperty(Utils.DATASOURCE).equals("database")) {
			FeatureRepository featureRepo = new FeatureRepository();
			existingFeatures = new ArrayList<>();
			existingTextFeatures = new ArrayList<>();
			if (config.getProperty(Utils.FEATURES_KIND).equals("all")) {
				existingFeatures = featureRepo.findAllFeatures();
			} else {
				existingFeatures = featureRepo.findFeatureByKind(config.getProperty(Utils.FEATURES_KIND));
			}
			existingFeatures.stream().forEach(feature -> {
				TextFeatureRepository tfr = new TextFeatureRepository();
				List<TextFeature> tfs = tfr.findTextFeatureByFeature(feature.getId());
				if(!CollectionUtils.isEmpty(tfs)) {
					existingTextFeatures.addAll(tfs);
				}
			});
		} else if (config.getProperty(Utils.DATASOURCE).equals("csv")) {
			FeatureCsvReader featureReader = new FeatureCsvReader();
			existingFeatures = featureReader.readData(Utils.FEATURES_CSV_PATH);
			if (!config.getProperty(Utils.FEATURES_KIND).equals("all")) {
				existingFeatures = existingFeatures.stream()
						.filter(feature -> feature.getKind().equals(config.getProperty(Utils.FEATURES_KIND)))
						.collect(Collectors.toList());
			}
			TextFeatureCsvReader textFeatureCsvReader = new TextFeatureCsvReader();
			existingTextFeatures = textFeatureCsvReader.readData(Utils.TEXT_FEATURES_CSV_PATH);
		}
	}

	/**
	 * Get configurations
	 */
	private void readConfigurationFile() {
		InputStream in;
		try {
			String propertiesName = Utils.CONFIG_FILE;
			in = getClass().getClassLoader().getResourceAsStream(propertiesName);
			config.load(in);
			PropertyConfigurator.configure(config);
		} catch (IOException | NullPointerException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(), e);
		}
	}

	/**
	 * Get data per fold
	 * @param numFolds
	 */
	private void initTotalFolds(int numFolds) {
		String datasource = config.getProperty(Utils.DATASOURCE);
		NfoldDatasetSplitter nfoldDatasetSplitter = new NfoldDatasetSplitter(numFolds, datasource);
		totalFolds.putAll(nfoldDatasetSplitter.getTotalFolds());
	}

	/**
	 * Execute classification
	 * @throws Exception
	 */
	private void runFolds() throws Exception {
		int numFolds = Integer.parseInt(config.getProperty(Utils.NUM_FOLDS));
		if (config.getProperty(Utils.INSTANCES).equals("new")) {
			initTotalFolds(numFolds);
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.PARALLEL))) {
			List<FoldRunner> runnables = initThreads(numFolds);
			Executor exec = Executors.newFixedThreadPool(numFolds);
			for (int i = 0; i < numFolds; i++) {
				exec.execute(runnables.get(i));
			}
		} else {
			for(int i=0;i<numFolds;i++) {
				Utils.FILE_LOGGER.info(startingMessageLog + "Running fold " + i);
				FoldRunner fr = new FoldRunner(i, config, existingFeatures, existingTextFeatures, totalFolds, Utils.PATH_SENTIMENT_INSTANCES);
				fr.run();
			}
		}
	}
	
	private List<FoldRunner> initThreads(int numFolds) {
		List<FoldRunner> runnables = new ArrayList<>();
		for(int i=0; i<numFolds;i++) {
			 FoldRunner fr = new FoldRunner(i, config, existingFeatures, existingTextFeatures, totalFolds, Utils.PATH_SENTIMENT_INSTANCES);
			 runnables.add(fr);
		}
		return runnables;
	}

}
