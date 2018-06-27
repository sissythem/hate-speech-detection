package gr.di.hatespeech.dataexporters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.features.BOWFeaturesExtractor;
import gr.di.hatespeech.features.CharacterNGramFeatureExtractor;
import gr.di.hatespeech.features.NgramFeatureExtractor;
import gr.di.hatespeech.features.SpellingFeatureExtractor;
import gr.di.hatespeech.features.SyntaxFeatureExtractor;
import gr.di.hatespeech.features.Word2VecFeatureExtractor;
import gr.di.hatespeech.utils.Utils;

/**
 * Abstract class implementing DataExporter interface
 * Exports data in database or in csv format
 * @param <T>
 */
public abstract class AbstractDataExporter<T> implements DataExporter<T> {
	private static String startingMessageLog = "[" + AbstractDataExporter.class.getSimpleName() + "] ";

	private Properties config;
	protected EntityManagerFactory factory;

	/** Feature extractors **/
	private BOWFeaturesExtractor bowExtractor;
	private Word2VecFeatureExtractor word2vecExtractor;
	private NgramFeatureExtractor ngramFeatureExtractor;
	private CharacterNGramFeatureExtractor charngramFeatureExtractor;
	private SpellingFeatureExtractor spellingFeatureExtractor;
	private SyntaxFeatureExtractor syntaxFeatureExtractor;
	
	public AbstractDataExporter() {
		Utils utils = new Utils();
		config = utils.readConfigurationFile(startingMessageLog, Utils.CONFIG_FILE);
		initFeatureExtractors();
	}

	/**
	 * Initialization of Feature Extractors (i.e. bag of words, word2vec, ngrams etc)
	 */
	private void initFeatureExtractors() {
		bowExtractor = new BOWFeaturesExtractor(Utils.HATEBASE_CSV_PATH, Utils.BOW_KEY_PREFIX);
		word2vecExtractor = new Word2VecFeatureExtractor(config.getProperty(Utils.AGGREGATION_TYPE),
				Utils.WORD2VEC_KEY_PREFIX, Utils.WORD2VEC_SMALL_SER);
		ngramFeatureExtractor = new NgramFeatureExtractor(Utils.NGRAM_KEY_PREFIX);
		charngramFeatureExtractor = new CharacterNGramFeatureExtractor(Utils.CHAR_NGRAM_KEY_PREFIX);
		spellingFeatureExtractor = new SpellingFeatureExtractor(Utils.ENGLISH_DICTIONARY_PATH,
				Utils.SPELLING_KEY_PREFIX);
		syntaxFeatureExtractor = new SyntaxFeatureExtractor(Utils.ENGLISH_PCFG, Utils.SYNTAX_KEY_PREFIX,
				Utils.SENTIMENT_KEY_PREFIX);
	}

	/**
	 * Uses provided configuration to extract Features from a given Text
	 * @param text, the specific text to extract features
	 * @return, a Map with features as keys and their values
	 */
	public Map<String, Double> getVectorFeatures(Text text) {
		Map<String, Double> textFeatures = new HashMap<>();
		if (Boolean.parseBoolean(config.getProperty(Utils.BOW))) {
			textFeatures.putAll(bowExtractor.extractFeatures(text));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.WORD2VEC))) {
			textFeatures.putAll(word2vecExtractor.extractFeatures(text));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.NGRAM))) {
			textFeatures.putAll(ngramFeatureExtractor.extractFeatures(text));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.CHAR_NGRAM))) {
			textFeatures.putAll(charngramFeatureExtractor.extractFeatures(text));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.SPELLING))) {
			textFeatures.putAll(spellingFeatureExtractor.extractFeatures(text));
		}
		if (Boolean.parseBoolean(config.getProperty(Utils.SYNTAX))) {
			textFeatures.putAll(syntaxFeatureExtractor.extractFeatures(text));
		}
		return textFeatures;
	}
	
	@Override
	public void exportDataToCsv(List<T> data, String[] headerRecord, String fileName, CsvOptions options) {
		
	}

	@Override
	public void exportDataToDatabase(List<T> data) {
		
	}

	public EntityManagerFactory getFactory() {
		return factory;
	}

	public void setFactory(EntityManagerFactory factory) {
		this.factory = factory;
	}

}
