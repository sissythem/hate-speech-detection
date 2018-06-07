package gr.di.hatespeech.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.similarity.JaccardDistance;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.CsvReader;
import gr.di.hatespeech.utils.Utils;

/**
 * Implementation of VectorFeatureExtractor for Spelling features
 * @author sissy
 */
public class SpellingFeatureExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + SpellingFeatureExtractor.class.getSimpleName() + "] ";
	protected Set<String> englishWords = new TreeSet<>();
	protected List<Double> editDistances;
	protected JaccardDistance jaccardDistance = new JaccardDistance();

	public SpellingFeatureExtractor(String fileName, String prefix) {
		super(prefix);
		getEnglishWordsFromCsvFile(fileName);
	}

	/**
	 * Extracts features from a given text and returns a HashMap with only one
	 * Entry: the body of a comment as key and an average edit distance
	 * @param text
	 * @return features in a HashMap
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		initContainers();
		populateEditDistanceList(text);
		getEditDistanceFeature(text);
		return features;
	}

	private void initContainers() {
		features = new HashMap<>();
		editDistances = new ArrayList<>();
	}

	/**
	 * Parse words from text and for each word get the min edit distance
	 * @param text
	 */
	private void populateEditDistanceList(Text text) {
		List<String> words = Arrays.asList(text.getPrepMessage().split(" "));
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting spelling features for text " + text.getId());
		words.stream().forEach(word -> getEditDistance(word));
	}

	/**
	 * Get edit distance for a given word
	 * @param word
	 */
	private void getEditDistance(String word) {
		Map<String, Double> cache = new ConcurrentHashMap<>();
		Set<String> candidates = preprocessEnglishWords();
		String mostSimilarWord = findMostSimilarWord(word, cache, candidates);
		editDistances.add(jaccardDistance.apply(word, mostSimilarWord));
	}

	private Set<String> preprocessEnglishWords() {
		Set<String> candidates = englishWords.stream().map(String::toLowerCase)
				.map(term -> Arrays.stream(term.split(" ")).sorted().collect(Collectors.joining(" ")))
				.collect(Collectors.toSet());
		return candidates;
	}

	private String findMostSimilarWord(String word, Map<String, Double> cache, Set<String> candidates) {
		if (!candidates.contains(word)) {
			String mostSimilarWord = candidates.parallelStream().map(String::trim)
					// add more mappers if needed
					.filter(s -> !s.equalsIgnoreCase(word))
					// add more filters if needed
					.min((a, b) -> Double.compare(cache.computeIfAbsent(a, k -> jaccardDistance.apply(word, k)),
							cache.computeIfAbsent(b, k -> jaccardDistance.apply(word, k))))
					.get(); // get the closest match
			return mostSimilarWord;
		} else {
			return word;
		}
	}

	private void getEditDistanceFeature(Text text) {
		Double avgEditDistance = getAverageEditDistance();
		features.put(prefix, avgEditDistance);
	}

	protected Double getAverageEditDistance() {
		double sum = 0.0;
		for (Double distance : editDistances) {
			sum = distance + sum;
		}
		return sum / editDistances.size();
	}

	protected void getEnglishWordsFromCsvFile(String fileName) {
		String[] headers = { Utils.WORD};
		Iterable<CSVRecord> records = CsvReader.getCsvRecords(headers, fileName);
		if(records!=null) {
			for(CSVRecord record : records) {
				englishWords.add(record.get(Utils.WORD));
			}
		}
	}

}
