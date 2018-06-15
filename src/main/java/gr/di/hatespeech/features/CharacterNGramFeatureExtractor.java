package gr.di.hatespeech.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;
import weka.core.tokenizers.CharacterNGramTokenizer;

/**
 * Character ngram extractor extending Base vector Feature extractor
 * @author sissy
 */
public class CharacterNGramFeatureExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + CharacterNGramFeatureExtractor.class.getSimpleName() + "] ";

	public CharacterNGramFeatureExtractor(String prefix) {
		super(prefix);
		loadCharNGrams();
		Utils.FILE_LOGGER.info("Total ngrams: " + allTokens.keySet().size());
		Utils.FILE_LOGGER.info("############");
	}

	/**
	 * Given a Text, the extractor creates the character ngram features based on the
	 * CharacterNGramTokenizer of Weka. It produces all the character ngram features
	 * of the text
	 * @param text, is a Text object
	 * @return features, is a Map<String,Double> containing the character ngram
	 *         features of the text
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting char ngrams for text " + text.getId());
		List<String> elements = getCharacterNgramTokens(text);
		features = allTokens;
		elements.forEach(element -> {
			addNgramFeature(startingMessageLog, element);
		});
		return features;
	}
	
	/**
	 * Generates the character ngrams of a text
	 * @param text,is a Text object
	 * @return elements, all the character ngrams of the text
	 */
	private List<String> getCharacterNgramTokens(Text text) {
		Utils.FILE_LOGGER.debug(startingMessageLog + "Creating new tokenizer for text: " + text.getPrepMessage());
		CharacterNGramTokenizer tokenizer = new CharacterNGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(3);
		Utils.FILE_LOGGER.debug(startingMessageLog + "Tokenizing text");
		tokenizer.tokenize(text.getPrepMessage());
		List<String> elements = addTokens(startingMessageLog, tokenizer);
		return elements;
	}

	/**
	 * Produce all character NGrams from the texts on startup
	 */
	@SuppressWarnings("unchecked")
	private void loadCharNGrams() {
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classloader.getResourceAsStream(Utils.CHAR_NGRAM_SER);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			allTokens.putAll((HashMap<String,Double>)ois.readObject());
			ois.close();
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
