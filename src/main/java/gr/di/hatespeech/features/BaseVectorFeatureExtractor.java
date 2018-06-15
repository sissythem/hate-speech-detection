package gr.di.hatespeech.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import weka.core.tokenizers.Tokenizer;

/**
 * This is a basic representation of a vector feature extractor
 * having a map field representing the extracted features
 * @author sissy
 *
 */
public abstract class BaseVectorFeatureExtractor implements FeatureExtractor<Map<String, Double>> {
	protected Map<String, Double> features;
	protected Map<String,Double> allTokens = new HashMap<>();
	protected String prefix;
	
	/**
	 * Default constructor initializing features map
	 */
	public BaseVectorFeatureExtractor(String prefix) {
		super();
		this.features = new HashMap<>();
		this.prefix = prefix;
	}

	/**
	 * Tokenize a text (ngram or character ngram)
	 * @param startingMessageLog, String with the class name for logging
	 * @param tokenizer, the tokenizer of the text
	 * @return a list with all the ngrams or char ngrams of a text
	 */
	public List<String> addTokens(String startingMessageLog, Tokenizer tokenizer) {
		List<String> elements = new ArrayList<>();
		String token = tokenizer.nextElement();
		Utils.FILE_LOGGER.debug(startingMessageLog + "Iterating tokens and adding them to list");
		while (!StringUtils.isBlank(token)) {
			Utils.FILE_LOGGER.debug(startingMessageLog + "Adding element {" + token + "}");
			elements.add(token);
			token = tokenizer.nextElement();
			Utils.FILE_LOGGER.debug(startingMessageLog + "Getting new element {" + token + "}");
		}
		return elements;
	}

	/**
	 * Add existing text ngram or character ngram feature to the Map
	 * @param startingMessageLog, class name for logging
	 * @param element, ngram or char ngram feature
	 */
	protected void addNgramFeature(String startingMessageLog, String element) {
		String key = prefix + element;
		if (features.containsKey(key)) {
			features.put(key, features.get(key) + 1);
			Utils.FILE_LOGGER.debug(startingMessageLog + "Token appears in text: " + features.get(key));
		}
	}

	@Override
	public Map<String, Double> extractFeatures(Text text) {
		return null;
	}
	
	public Map<String, Double> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Double> features) {
		this.features = features;
	}
	
	public Map<String, Double> getAllTokens() {
		return allTokens;
	}

	public void setAllTokens(Map<String, Double> allTokens) {
		this.allTokens = allTokens;
	}
}
