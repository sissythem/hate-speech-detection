package gr.di.hatespeech.splitters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.TweetCsvReader;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;

/**
 * This class retrieves texts from a datasource and separates them based on
 * their label. Then, creates folds per label and finally merges the texts for
 * both labels per fold.
 * 
 * @author sissy
 */
public class NfoldDatasetSplitter {
	private static String startingMessageLog = "[" + NfoldDatasetSplitter.class.getSimpleName() + "] ";

	protected List<Text> hateSpeechList = new ArrayList<>();
	protected List<Text> nonHateSpeechList = new ArrayList<>();
	 // contains tweets labeled as HateSpeech, separated into folds
	protected Map<Integer, List<Text>> hateSpeechFolds = new HashMap<>();
	// contains tweets labeled as Clean (not hate speech), separated into folds
	protected Map<Integer, List<Text>> nonHateSpeechFolds = new HashMap<>(); 
	// contains tweets for both labels, separed into folds
	protected Map<Integer, List<Text>> totalFolds = new HashMap<>(); 

	public NfoldDatasetSplitter(int numFolds, String datasource) {
		fetchDataFromDatasource(datasource);
		createFolds(numFolds);
		mergeClassFolds(numFolds);
	}

	/**
	 * Merge the two maps (hateSpeechFolds and nonHateSpeechFolds) into one KeySet
	 * size equals numFolds and each entry contains now both clean and hatespeech
	 * texts
	 * 
	 * @param numFolds
	 */
	private void mergeClassFolds(int numFolds) {
		for (int i = 0; i < numFolds; i++) {
			List<Text> texts = new ArrayList<>();
			texts.addAll(hateSpeechFolds.get(i));
			texts.addAll(nonHateSpeechFolds.get(i));
			Collections.shuffle(texts);
			totalFolds.put(i, texts);
		}
	}

	/**
	 * Create two maps, one for each label
	 * 
	 * @param numFolds
	 */
	protected void createFolds(int numFolds) {
		createHateSpeechFolds(numFolds);
		createNonHateSpeechFolds(numFolds);
	}

	/**
	 * Generate HashMap with N folds containing tweets labeled as HateSpeech
	 * 
	 * @param numFolds
	 */
	protected void createHateSpeechFolds(int numFolds) {
		int hateItems = hateSpeechList.size() / numFolds;
		int foldNumber = 0;
		int count = 0;
		List<Text> texts = new ArrayList<>();
		for (int i = 0; i < hateSpeechList.size(); i++) {
			texts.add(hateSpeechList.get(i));
			count++;
			if (count == hateItems) {
				hateSpeechFolds.put(foldNumber, texts);
				texts = new ArrayList<>();
				count = 0;
				foldNumber++;
			}
		}
		if (!CollectionUtils.isEmpty(texts)) {
			hateSpeechFolds.get(foldNumber - 1).addAll(texts);
		}
	}

	/**
	 * Generate HashMap with N folds containing tweets labeled as Clean (non
	 * HateSpeech)
	 * 
	 * @param numFolds
	 */
	protected void createNonHateSpeechFolds(int numFolds) {
		int cleanItems = nonHateSpeechList.size() / numFolds;
		int foldNumber = 0;
		int count = 0;
		List<Text> texts = new ArrayList<>();
		for (int i = 0; i < nonHateSpeechList.size(); i++) {
			texts.add(nonHateSpeechList.get(i));
			count++;
			if (count == cleanItems) {
				nonHateSpeechFolds.put(foldNumber, texts);
				texts = new ArrayList<>();
				count = 0;
				foldNumber++;
			}
		}
		if (!CollectionUtils.isEmpty(texts)) {
			nonHateSpeechFolds.get(foldNumber - 1).addAll(texts);
		}
	}

	/**
	 * Get selected data source from Properties and export the data from either
	 * database or csv
	 * 
	 * @param datasource
	 */
	protected void fetchDataFromDatasource(String datasource) {
		if (datasource.equalsIgnoreCase("database")) {
			fetchDataFromDatabase();
		} else if (datasource.equalsIgnoreCase("csv")) {
			fetchDataFromCsv();
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "No datasource given");
		}
	}

	/**
	 * Export texts from Csv
	 */
	protected void fetchDataFromCsv() {
		TweetCsvReader tweetCsvReader = new TweetCsvReader();
		List<Text> texts = tweetCsvReader.readData(Utils.TWEET_CSV_PATH);
		texts.stream().forEach(text -> {
			if (!StringUtils.isBlank(text.getPrepMessage())) {
				if (text.getLabel().equals(Utils.HATE_SPEECH_LABEL)) {
					hateSpeechList.add(text);
				} else if (text.getLabel().equals(Utils.CLEAN_LABEL)) {
					nonHateSpeechList.add(text);
				} else {
					Utils.FILE_LOGGER.error(startingMessageLog + "Wrong label");
				}
			}
		});
	}

	/**
	 * Fetch texts from database
	 */
	protected void fetchDataFromDatabase() {
		TextRepository textRepo = new TextRepository();
		hateSpeechList = textRepo.findTextsByLabel(Utils.HATE_SPEECH_LABEL).stream().filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
		nonHateSpeechList = textRepo.findTextsByLabel(Utils.CLEAN_LABEL).stream().filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
	}

	public List<Text> getHatespeechList() {
		return hateSpeechList;
	}

	public void setHatespeechList(List<Text> hatespeechList) {
		this.hateSpeechList = hatespeechList;
	}

	public List<Text> getCleanList() {
		return nonHateSpeechList;
	}

	public void setCleanList(List<Text> cleanList) {
		this.nonHateSpeechList = cleanList;
	}

	public Map<Integer, List<Text>> getHateFolds() {
		return hateSpeechFolds;
	}

	public void setHateFolds(Map<Integer, List<Text>> hateFolds) {
		this.hateSpeechFolds = hateFolds;
	}

	public Map<Integer, List<Text>> getCleanFolds() {
		return nonHateSpeechFolds;
	}

	public void setCleanFolds(Map<Integer, List<Text>> cleanFolds) {
		this.nonHateSpeechFolds = cleanFolds;
	}

	public Map<Integer, List<Text>> getTotalFolds() {
		return totalFolds;
	}

	public void setTotalFolds(Map<Integer, List<Text>> totalFolds) {
		this.totalFolds = totalFolds;
	}

}
