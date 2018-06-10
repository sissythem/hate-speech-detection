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
import gr.di.hatespeech.utils.EmailSender;
import gr.di.hatespeech.utils.Utils;

/**
 * Main program to execute classification
 * @author sissy
 */
public class HateSpeechDetection {
	private static String startingMessageLog = "[" + HateSpeechDetection.class.getSimpleName() + "] ";
	protected Properties config = new Properties();
	protected Map<Integer, List<Text>> totalFolds = new HashMap<>();
	protected List<Feature> existingFeatures = new ArrayList<>();
	protected List<TextFeature> existingTextFeatures = new ArrayList<>();

	public static void main(String[] args) {
		try {
			Utils.FILE_LOGGER.info(startingMessageLog + "PROGRAM START");
			HateSpeechDetection hsd = new HateSpeechDetection();
			
			// read config.properties
			hsd.readConfigurationFile();
			
			// retrieve data from datasource based on config.properties
			if (hsd.config.getProperty(Utils.INSTANCES).equals("new")
					&& hsd.config.getProperty(Utils.VECTOR_FEATURES).equals("existing")) {
				hsd.generateExistingFeatures();
			}
			// run the folds
			hsd.runFolds();
			Utils.FILE_LOGGER.info(startingMessageLog + "PROGRAM FINISH");
			Utils.FILE_LOGGER.info(startingMessageLog + "==================");
			EmailSender.Send("c.themeli", "Cardu2214!", "c.themeli@gmail.com", "Program finished", "Program finished");
		} catch (Exception e) {
			e.printStackTrace();
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(), e);
		}
	}
	
	/**
	 * Get configurations
	 */
	protected void readConfigurationFile() {
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
	 * Read existing features from database or csv
	 */
	protected void generateExistingFeatures() {
		if (config.getProperty(Utils.DATASOURCE).equals("database")) {
			retrieveFeaturesFromDatabase();
		} else if (config.getProperty(Utils.DATASOURCE).equals("csv")) {
			retrieveFeaturesFromCsv();
		}
	}

	public void retrieveFeaturesFromDatabase() {
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
	}

	public void retrieveFeaturesFromCsv() {
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

	/**
	 * Execute classification
	 * @throws Exception
	 */
	protected void runFolds() throws Exception {
		int numFolds = Integer.parseInt(config.getProperty(Utils.NUM_FOLDS));
		String pathToInstances = getPathToInstances();
		
		if (config.getProperty(Utils.INSTANCES).equals("new")) {
			int dataset = Integer.parseInt(config.getProperty(Utils.DATASET));
			initTotalFolds(numFolds, dataset);
		}
		
		if (Boolean.parseBoolean(config.getProperty(Utils.PARALLEL))) {
			List<FoldRunner> runnables = initThreads(numFolds, pathToInstances);
			Executor exec = Executors.newFixedThreadPool(numFolds);
			for (int i = 0; i < numFolds; i++) {
				exec.execute(runnables.get(i));
			}
		} else {
			for(int i=0;i<numFolds;i++) {
				Utils.FILE_LOGGER.info(startingMessageLog + "Running fold " + i);
				FoldRunner fr = new FoldRunner(i, config, existingFeatures, existingTextFeatures, totalFolds, pathToInstances);
				fr.run();
			}
		}
	}
	
	/**
	 * Get data per fold
	 * @param numFolds
	 */
	protected void initTotalFolds(int numFolds, int dataset) {
		String datasource = config.getProperty(Utils.DATASOURCE);
		NfoldDatasetSplitter nfoldDatasetSplitter = new NfoldDatasetSplitter(numFolds, datasource, dataset);
		totalFolds.putAll(nfoldDatasetSplitter.getTotalFolds());
	}
	
	protected List<FoldRunner> initThreads(int numFolds, String pathToInstances) {
		List<FoldRunner> runnables = new ArrayList<>();
		for(int i=0; i<numFolds;i++) {
			 FoldRunner fr = new FoldRunner(i, config, existingFeatures, existingTextFeatures, totalFolds, pathToInstances);
			 runnables.add(fr);
		}
		return runnables;
	}
	
	protected String getPathToInstances() {
		String pathToInstances = config.getProperty(Utils.START_PATH_TO_INSTANCES);
		if (!config.getProperty(Utils.VECTOR_FEATURES).equals("none")) {
			String featuresKind = config.getProperty(Utils.FEATURES_KIND);
			switch (featuresKind) {
			case "all":
				pathToInstances = pathToInstances + Utils.PATH_ALL_INSTANCES;
				break;
			case "bow":
				pathToInstances = pathToInstances + Utils.PATH_BOW_INSTANCES;
				break;
			case "word2vec":
				pathToInstances = pathToInstances + Utils.PATH_WORD2VEC_INSTANCES;
				break;
			case "sentiment":
				pathToInstances = pathToInstances + Utils.PATH_SENTIMENT_INSTANCES;
				break;
			case "syntax":
				pathToInstances = pathToInstances + Utils.PATH_SYNTAX_INSTANCES;
				break;
			case "spelling":
				pathToInstances = pathToInstances + Utils.PATH_SPELLING_INSTANCES;
				break;
			case "ngram":
				pathToInstances = pathToInstances + Utils.PATH_NGRAM_INSTANCES;
				break;
			case "charngram":
				pathToInstances = pathToInstances + Utils.PATH_CHARNGRAM_INSTANCES;
				break;
			}
		}
		if(Boolean.parseBoolean(config.getProperty(Utils.GRAPH_FEATURES)) && !config.getProperty(Utils.FEATURES_KIND).equals("all")) {
			pathToInstances = pathToInstances + Utils.PATH_GRAPH_INSTANCES;
		}
		return pathToInstances;
	}

}
