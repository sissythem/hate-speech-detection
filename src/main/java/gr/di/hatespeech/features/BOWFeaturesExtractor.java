package gr.di.hatespeech.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.CsvReader;
import gr.di.hatespeech.utils.Utils;

/**
 * Extends BaseVectorFeatureExtractor for BagOfWords features
 */
public class BOWFeaturesExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + BOWFeaturesExtractor.class.getSimpleName() + "] ";
	protected List<String> hateKeywords= new ArrayList<>();
	
	/**
	 * Default constructor generating hate keywords
	 * from HateBase keywords.
	 */
	public BOWFeaturesExtractor(String fileName, String prefix) {
		super(prefix);
		getKeywordsFromCsv(fileName);
	}

	/**
	 * Extracts features from a given text, returning a HashMap with keys keywords
	 * from HateBase and the number of times that these keywords appear in the text
	 * as value
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		addKeywordsToMap();
		List<String> words = Arrays.asList(text.getPrepMessage().split(" "));
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting bow features for text " + text.getId());
		words.forEach(word -> checkForHateWord(word));
		return features;
	}

	/**
	 * Checks if a word is included in the list with the hate words
	 * Hate words are added in the features map and counts the number
	 * of times each hate word occurs.
	 * @param word
	 */
	protected void checkForHateWord(String word) {
		if (features.containsKey(prefix + word)) {
			Utils.FILE_LOGGER.info(startingMessageLog + "Hate word found: " + word);
			features.put(prefix + word, features.get(prefix + word) + 1);
		}
	}

	/**
	 * Adds all hate words in the features map
	 */
	protected void addKeywordsToMap() {
		features = new HashMap<>();
		hateKeywords.stream().forEach(keyword->features.put(prefix+keyword, 0.0));
	}

	/**
	 * Reads the hate words from the csv file
	 * with the HateBase keywords for HateSpeech
	 */
	protected void getKeywordsFromCsv(String fileName) {
		// adds all hate keywords in the map
		String[] headers = { Utils.HATE_WORD};
		Iterable<CSVRecord> records = CsvReader.getCsvRecords(headers, fileName);
		if(records!=null) {
			for(CSVRecord record : records) {
				hateKeywords.add(record.get(Utils.HATE_WORD).toLowerCase());
			}
		}
	}

}
