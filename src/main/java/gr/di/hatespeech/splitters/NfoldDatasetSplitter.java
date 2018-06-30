package gr.di.hatespeech.splitters;

import java.util.ArrayList;
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
 * @author sissy
 */
public class NfoldDatasetSplitter {
	private static String startingMessageLog = "[" + NfoldDatasetSplitter.class.getSimpleName() + "] ";

	protected List<Text> hateSpeechList = new ArrayList<>();
	protected List<Text> nonHateSpeechList = new ArrayList<>();
	protected List<Text> offensiveList = new ArrayList<>();
	protected List<Text> racismList = new ArrayList<>();
	protected List<Text> sexismList = new ArrayList<>();
	
	// contains tweets labeled as HateSpeech, separated into folds
	protected Map<Integer, List<Text>> hateSpeechFolds = new HashMap<>();
	// contains tweets labeled as Clean (not hate speech), separated into folds
	protected Map<Integer, List<Text>> nonHateSpeechFolds = new HashMap<>();
	protected Map<Integer, List<Text>> offensiveFolds = new HashMap<>();
	protected Map<Integer, List<Text>> racismFolds = new HashMap<>();
	protected Map<Integer, List<Text>> sexismFolds = new HashMap<>();
	
	// contains tweets for both labels, separed into folds
	protected Map<Integer, List<Text>> totalFolds = new HashMap<>();

	public NfoldDatasetSplitter(int numFolds, String datasource, int dataset) {
		fetchDataFromDatasource(datasource, dataset);
		createFolds(numFolds, dataset);
		mergeClassFolds(numFolds, dataset);
	}

	/**
	 * Merge the two maps (hateSpeechFolds and nonHateSpeechFolds) into one KeySet
	 * size equals numFolds and each entry contains now both clean and hatespeech
	 * texts. If only one of the two datasets is chosen then merge the relevant
	 * folds
	 * @param numFolds
	 */
	protected void mergeClassFolds(int numFolds, int dataset) {
		switch(dataset) {
		case -1:
			for (int i = 0; i < numFolds; i++) {
				List<Text> texts = new ArrayList<>();
				texts.addAll(hateSpeechFolds.get(i));
				texts.addAll(nonHateSpeechFolds.get(i));
				totalFolds.put(i, texts);
			}
			break;
		case 0:
			for (int i = 0; i < numFolds; i++) {
				List<Text> texts = new ArrayList<>();
				texts.addAll(racismFolds.get(i));
				texts.addAll(nonHateSpeechFolds.get(i));
				texts.addAll(sexismFolds.get(i));
				totalFolds.put(i, texts);
			}
			break;
		case 1:
			for (int i = 0; i < numFolds; i++) {
				List<Text> texts = new ArrayList<>();
				texts.addAll(hateSpeechFolds.get(i));
				texts.addAll(nonHateSpeechFolds.get(i));
				texts.addAll(offensiveFolds.get(i));
				totalFolds.put(i, texts);
			}
			break;
		}
		
	}

	/**
	 * Create two maps, one for each label, in case of single label
	 * If a specific dataset is selected create the relevant folds
	 * @param numFolds
	 */
	protected void createFolds(int numFolds, int dataset) {
		switch(dataset) {
		case -1:
			createHateSpeechFolds(numFolds);
			createNonHateSpeechFolds(numFolds);
			break;
		case 0:
			createRacismFolds(numFolds);
			createSexismFolds(numFolds);
			createNonHateSpeechFolds(numFolds);
			break;
		case 1:
			createHateSpeechFolds(numFolds);
			createOffensiveFolds(numFolds);
			createNonHateSpeechFolds(numFolds);
			break;
		}
	}

	/**
	 * Generate HashMap with N folds containing tweets labeled as HateSpeech
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
	 * Generate HashMap with N folds containing tweets labeled as OffensiveLanguage 
	 * @param numFolds
	 */
	protected void createOffensiveFolds(int numFolds) {
		int offensiveItems = offensiveList.size() / numFolds;
		int foldNumber = 0;
		int count = 0;
		List<Text> texts = new ArrayList<>();
		for(int i=0; i< offensiveList.size(); i++) {
			texts.add(offensiveList.get(i));
			count++;
			if(count==offensiveItems) {
				offensiveFolds.put(foldNumber, texts);
				texts = new ArrayList<>();
				count =0;
				foldNumber++;
			}
		}
		if(!CollectionUtils.isEmpty(texts)) {
			offensiveFolds.get(foldNumber -1).addAll(texts);
		}
	}
	
	/**
	 * Generate HashMap with N folds containing tweets labeled as racist 
	 * @param numFolds
	 */
	protected void createRacismFolds(int numFolds) {
		int racismItems = racismList.size() / numFolds;
		int foldNumber = 0;
		int count = 0;
		List<Text> texts = new ArrayList<>();
		for(int i=0; i< racismList.size(); i++) {
			texts.add(racismList.get(i));
			count++;
			if(count==racismItems) {
				racismFolds.put(foldNumber, texts);
				texts = new ArrayList<>();
				count =0;
				foldNumber++;
			}
		}
		if(!CollectionUtils.isEmpty(texts)) {
			racismFolds.get(foldNumber -1).addAll(texts);
		}
	}
	
	/**
	 * Generate HashMap with N folds containing tweets labeled as sexist 
	 * @param numFolds the number of folds to be created
	 */
	protected void createSexismFolds(int numFolds) {
		int sexismItems = sexismList.size() / numFolds;
		int foldNumber = 0;
		int count = 0;
		List<Text> texts = new ArrayList<>();
		for(int i=0; i< sexismList.size(); i++) {
			texts.add(sexismList.get(i));
			count++;
			if(count==sexismItems) {
				sexismFolds.put(foldNumber, texts);
				texts = new ArrayList<>();
				count =0;
				foldNumber++;
			}
		}
		if(!CollectionUtils.isEmpty(texts)) {
			sexismFolds.get(foldNumber -1).addAll(texts);
		}
	}

	/**
	 * Get selected data source from Properties and export the data from either
	 * database or csv
	 * @param datasource either database or csv
	 */
	protected void fetchDataFromDatasource(String datasource, int dataset) {
		if (datasource.equalsIgnoreCase("database")) {
			fetchDataFromDatabase(dataset);
		} else if (datasource.equalsIgnoreCase("csv")) {
			fetchDataFromCsv(dataset);
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "No datasource given");
		}
	}

	/**
	 * Export texts from Csv
	 */
	protected void fetchDataFromCsv(int dataset) {
		TweetCsvReader tweetCsvReader = new TweetCsvReader();
		List<Text> texts = tweetCsvReader.readData(Utils.TWEET_CSV_PATH);
		if(dataset!=-1) {
			texts = texts.stream().filter(text -> text.getDataset().equals(dataset)).collect(Collectors.toList());
		}
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
	 * Fetch texts from database based on the selected dataset
	 */
	protected void fetchDataFromDatabase(int dataset) {
		TextRepository textRepo = new TextRepository();
		switch (dataset) {
		case -1:
			hateSpeechList = textRepo.findTextsByLabel(Utils.HATE_SPEECH_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
			nonHateSpeechList = textRepo.findTextsByLabel(Utils.CLEAN_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
			break;
		case 0:
			racismList = textRepo.findTextsByOldLabel(Utils.RACISM_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(0))
					.collect(Collectors.toList());
			sexismList = textRepo.findTextsByOldLabel(Utils.SEXISM_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(0))
					.collect(Collectors.toList());
			nonHateSpeechList = textRepo.findTextsByOldLabel(Utils.CLEAN_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(0))
					.collect(Collectors.toList());
			break;
		case 1:
			hateSpeechList = textRepo.findTextsByOldLabel(Utils.HATE_SPEECH_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(1))
					.collect(Collectors.toList());
			offensiveList = textRepo.findTextsByOldLabel(Utils.OFFENSIVE_LANGUAGE_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(1))
					.collect(Collectors.toList());
			nonHateSpeechList = textRepo.findTextsByOldLabel(Utils.CLEAN_LABEL).stream()
					.filter(text -> !StringUtils.isBlank(text.getPrepMessage()) && text.getDataset().equals(1))
					.collect(Collectors.toList());
			break;
		}

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

	public static String getStartingMessageLog() {
		return startingMessageLog;
	}

	public static void setStartingMessageLog(String startingMessageLog) {
		NfoldDatasetSplitter.startingMessageLog = startingMessageLog;
	}

	public List<Text> getHateSpeechList() {
		return hateSpeechList;
	}

	public void setHateSpeechList(List<Text> hateSpeechList) {
		this.hateSpeechList = hateSpeechList;
	}

	public List<Text> getNonHateSpeechList() {
		return nonHateSpeechList;
	}

	public void setNonHateSpeechList(List<Text> nonHateSpeechList) {
		this.nonHateSpeechList = nonHateSpeechList;
	}

	public List<Text> getOffensiveList() {
		return offensiveList;
	}

	public void setOffensiveList(List<Text> offensiveList) {
		this.offensiveList = offensiveList;
	}

	public List<Text> getRacismList() {
		return racismList;
	}

	public void setRacismList(List<Text> racismList) {
		this.racismList = racismList;
	}

	public List<Text> getSexismList() {
		return sexismList;
	}

	public void setSexismList(List<Text> sexismList) {
		this.sexismList = sexismList;
	}

	public Map<Integer, List<Text>> getHateSpeechFolds() {
		return hateSpeechFolds;
	}

	public void setHateSpeechFolds(Map<Integer, List<Text>> hateSpeechFolds) {
		this.hateSpeechFolds = hateSpeechFolds;
	}

	public Map<Integer, List<Text>> getNonHateSpeechFolds() {
		return nonHateSpeechFolds;
	}

	public void setNonHateSpeechFolds(Map<Integer, List<Text>> nonHateSpeechFolds) {
		this.nonHateSpeechFolds = nonHateSpeechFolds;
	}

	public Map<Integer, List<Text>> getOffensiveFolds() {
		return offensiveFolds;
	}

	public void setOffensiveFolds(Map<Integer, List<Text>> offensiveFolds) {
		this.offensiveFolds = offensiveFolds;
	}

	public Map<Integer, List<Text>> getRacismFolds() {
		return racismFolds;
	}

	public void setRacismFolds(Map<Integer, List<Text>> racismFolds) {
		this.racismFolds = racismFolds;
	}

	public Map<Integer, List<Text>> getSexismFolds() {
		return sexismFolds;
	}

	public void setSexismFolds(Map<Integer, List<Text>> sexismFolds) {
		this.sexismFolds = sexismFolds;
	}

}
