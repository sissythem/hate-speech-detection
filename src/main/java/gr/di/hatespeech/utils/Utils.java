package gr.di.hatespeech.utils;

import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * Utils file with constant string and util methods
 * @author sissy
 */
public class Utils {
	public static final Logger FILE_LOGGER  = LoggerFactory.getLogger("file");
	
	/** Labels**/
	public static final String HATE_SPEECH_LABEL = "HateSpeech";
	public static final String CLEAN_LABEL = "Clean";
	public static final String OFFENSIVE_LANGUAGE_LABEL = "OffensiveLanguage";
	public static final String SEXISM_LABEL = "sexism";
	public static final String RACISM_LABEL = "Racism";
	
	/** Properties **/
	public static final String PARALLEL = "parallel";
	public static final String NUM_FOLDS = "numFolds";
	public static final String RUNS = "runs";
	public static final String START_PATH_TO_INSTANCES = "startPathToInstances";
	public static final String DATASOURCE = "datasource";
	public static final String INSTANCES = "instances";
	public static final String PREPROCESS = "preprocess";
	public static final String STOPWORDS = "stopwords";
	public static final String VECTOR_FEATURES = "vectorFeatures";
	public static final String GRAPH_FEATURES = "graphFeatures";
	public static final String GRAPH_TYPE = "graphType";
	public static final String FEATURES_KIND = "featuresKind";
	public static final String BOW = "bow";
	public static final String WORD2VEC = "word2vec";
	public static final String AGGREGATION_TYPE = "aggregationType";
	public static final String CHAR_NGRAM = "charngram";
	public static final String NGRAM = "ngram";
	public static final String SPELLING = "spelling";
	public static final String SYNTAX = "syntax";
	public static final String INSTANCES_TO_FILE = "instancesToFile";
	public static final String SEND_EMAIL = "sendEmail";
	
	
	/** CSV/Database columns**/
	public static final String ID = "id";
	public static final String BODY = "body";
	public static final String LABEL = "label";
	public static final String OLD_LABEL = "old_label";
	public static final String DATASET = "dataset";
	public static final String TWEET_ID = "tweet_id";
	public static final String PROCESSED_BODY = "processed_body";
	
	public static final String HATE_WORD = "hate_word";
	public static final String WORD = "word";
	
	/** Classifiers **/
	public static final String CLASSIFICATION_TYPE = "classificationType";
	public static final String KNN_CLASSIFIER = "KNN";
	public static final String LOGISTIC_REGRESSION_CLASSIFIER = "LogisticRegression";
	public static final String NAIVE_BAYES_CLASSIFIER = "NaiveBayes";
	public static final String RANDOM_FOREST_CLASSIFIER = "RandomForest";
	
	/** Table names**/
	public static final String TEXT_TABLE = "texts";
	public static final String FEATURE_TABLE = "features";
	public static final String TEXT_FEATURE_TABLE = "texts_features";
	public static final String PERSISTENCE_UNIT_NAME = "hate-speech-detection";
	
	/** Queries **/
	public static final String TEXT_FIND_ALL = "Text.findAll";
	public static final String TEXT_FIND_BY_ID = "Text.findById";
	public static final String TEXT_FIND_BY_LABEL = "Text.findByLabel";
	public static final String TEXT_FIND_BY_OLD_LABEL = "Text.findByOldLabel";
	
	public static final String FEATURE_FIND_ALL = "Feature.findAll";
	public static final String FEATURE_FIND_BY_ID = "Feature.findById";
	public static final String FEATURE_FIND_BY_KIND = "Feature.findByKind";
	public static final String FEATURE_FIND_BY_DESCRIPTION = "Feature.findByDescription";
	
	public static final String TEXT_FEATURE_FIND_ALL = "TextFeature.findAll";
	public static final String TEXT_FEATURE_FIND_BY_ID = "TextFeature.findById";
	public static final String TEXT_FEATURE_FIND_BY_TEXT = "TextFeature.findByText";
	public static final String TEXT_FEATURE_FIND_BY_FEATURE = "TextFeature.findByFeature";
	public static final String TEXT_FEATURE_FIND_BY_TEXT_AND_FEATURE = "TextFeature.findByTextAndFeature";
	public static final String TEXT_FEATURE_FIND_BY_FEATURE_KIND = "TextFeature.findByFeatureKind";
	
	/** Path to instances files **/
	public static final String PATH_ALL_INSTANCES = "all/fold";
	public static final String PATH_GRAPH_INSTANCES = "graph/fold";
	public static final String PATH_VECTOR_ALL_INSTANCES  ="vector/all/fold";
	public static final String PATH_BEST_INSTANCES = "best/fold";
	public static final String PATH_BOW_INSTANCES = "vector/bow/fold";
	public static final String PATH_WORD2VEC_INSTANCES = "vector/word2vec/fold";
	public static final String PATH_SYNTAX_INSTANCES = "vector/syntax/fold";
	public static final String PATH_SPELLING_INSTANCES = "vector/spelling/fold";
	public static final String PATH_SENTIMENT_INSTANCES = "vector/sentiment/fold";
	public static final String PATH_NGRAM_INSTANCES = "vector/ngrams/fold";
	public static final String PATH_CHARNGRAM_INSTANCES = "vector/charngrams/fold";
	
	/** Files **/
	public static final String TEST_INSTACES_FILE = "test";
	public static final String TRAIN_INSTANCES_FILE = "train";
	public static final String CONFIG_FILE = "./config.properties";
	public static final String EMAIL_CONFIG_FILE = "./emailConfig.properties";
	public static final String TWEETS_TO_DOWNLOAD_FILE_PATH = "./datasets/smalldataset.csv";
	public static final String EXISTING_TWEETS_FILE_PATH = "./datasets/labeled_data.csv";
	public static final String TWEET_CSV_PATH = "./datasets/tweets.csv";
	public static final String FEATURES_CSV_PATH = "./datasets/features.csv";
	public static final String TEXT_FEATURES_CSV_PATH = "./datasets/textfeatures.csv";
	public static final String STOPWORDS_CSV_PATH = "./preprocessing/stopwords.csv";
	public static final String HATEBASE_CSV_PATH = "./bow/hatebase_dict.csv";
	public static final String FEATURES_SER = "./features/features.ser";
	public static final String WORD2VEC_SMALL_SER = "./word2vec/glove.6B.50d.ser";
	public static final String WORD2VEC_TWITTER_PART_1_SER = "./word2vec/glove.twitter.50d.part1.ser";
	public static final String WORD2VEC_TWITTER_PART_2_SER = "./word2vec/glove.twitter.50d.part2.ser";
	public static final String NGRAM_SER = "./ngrams/ngrams.ser";
	public static final String CHAR_NGRAM_SER = "./ngrams/charngrams.ser";
	public static final String ENGLISH_DICTIONARY_PATH = "./spelling/dictionary.csv";
	public static final String ENGLISH_PCFG = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	public static final String ENGLISH_FACTORED = "edu/stanford/nlp/models/lexparser/englishFactored.ser.gz";
	
	/** Key Prefixes**/
	public static final String BOW_KEY_PREFIX = "bowfeatures/";
	public static final String WORD2VEC_KEY_PREFIX = "word2vec/";
	public static final String NGRAM_KEY_PREFIX = "ngramfeatures/";
	public static final String CHAR_NGRAM_KEY_PREFIX = "charngramfeatures/";
	public static final String SPELLING_KEY_PREFIX = "spellingfeature/";
	public static final String SYNTAX_KEY_PREFIX = "syntaxfeature/";
	public static final String SENTIMENT_KEY_PREFIX = "sentimentfeature/";
	
	public static LocalDateTime tic() {
		return LocalDateTime.now();
	}
	
	public static long toc(LocalDateTime start) {
		LocalDateTime end = LocalDateTime.now();
		return Duration.between(start, end).toMillis()/1000;
	}

	/**
	 * Get configurations
	 */
	public  Properties readConfigurationFile(String startingMessageLog, String propertiesName) {
		InputStream in;
		Properties config = new Properties();
		try {
			in = getClass().getClassLoader().getResourceAsStream(propertiesName);
			config.load(in);
			PropertyConfigurator.configure(config);
		} catch (IOException | NullPointerException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(), e);
		}
		return config;
	}

}
