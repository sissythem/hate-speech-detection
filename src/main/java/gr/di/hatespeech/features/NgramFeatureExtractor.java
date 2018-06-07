package gr.di.hatespeech.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;
import weka.core.tokenizers.NGramTokenizer;

/**
 * Ngram feature extractor extending the Base Vector Feature Extractor
 * @author sissy
 */
public class NgramFeatureExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + NgramFeatureExtractor.class.getSimpleName() + "] ";
	protected Map<String,Double> allTokens = new HashMap<>();

	/**
	 * Default constructor
	 */
	public NgramFeatureExtractor(String prefix) {
		super(prefix);
		loadNGrams();
		Utils.FILE_LOGGER.info("Total ngrams: " + allTokens.keySet().size());
		Utils.FILE_LOGGER.info("############");
	}

	/**
	 * Extracts ngram features from a given text using NGramTokenizer from Weka
	 * @param text, is a Text object
	 * @return features, a Map<String,Double> object with the ngram features of the
	 * text
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting ngrams for text" + text.getId());
		List<String> elements = getNgramTokens(text);
		features = allTokens;
		elements.stream().forEach(element -> {
			String key = prefix + element;
			if (features.containsKey(key)) {
				features.put(key, features.get(key) + 1);
				Utils.FILE_LOGGER.debug(startingMessageLog + "Token appears in text: " + features.get(key));
			}
		});
		return features;
	}

	/**
	 * Generates a List with the ngrams from a Text object
	 * @param text, is a Text object
	 * @return elements, is a List<String> with the ngrams of the text
	 */
	protected List<String> getNgramTokens(Text text) {
		Utils.FILE_LOGGER.debug(startingMessageLog + "Creating new tokenizer for text: " + text.getPrepMessage());
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(3);
		Utils.FILE_LOGGER.debug(startingMessageLog + "Tokenizing text");
		tokenizer.tokenize(text.getPrepMessage());
		List<String> elements = new ArrayList<>();
		Utils.FILE_LOGGER.debug(startingMessageLog + "Iterating tokens and adding them to list");
		String token = tokenizer.nextElement();
		while (!StringUtils.isBlank(token)) {
			Utils.FILE_LOGGER.debug(startingMessageLog + "Adding element {" + token + "}");
			elements.add(token);
			token = tokenizer.nextElement();
			Utils.FILE_LOGGER.debug(startingMessageLog + "Getting new element {" + token + "}");
		}
		return elements;
	}

	/**
	 * Produce all NGrams from the texts on startup
	 */
	@SuppressWarnings("unchecked")
	private void loadNGrams() {
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classloader.getResourceAsStream(Utils.NGRAM_SER);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			allTokens.putAll((HashMap<String,Double>)ois.readObject());
			ois.close();
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
