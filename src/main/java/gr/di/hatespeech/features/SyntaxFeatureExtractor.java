package gr.di.hatespeech.features;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.ScoredObject;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * Implementation of VectorFeatureExtractor for Syntax features
 * @author sissy
 */
public class SyntaxFeatureExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + SyntaxFeatureExtractor.class.getSimpleName() + "] ";
	private LexicalizedParser lp;
	private String sentimentPrefix;
	
	public SyntaxFeatureExtractor(String filename, String syntaxPrefix, String sentimentPrefix) {
		super(syntaxPrefix);
		this.sentimentPrefix = sentimentPrefix;
		loadEnglishModel(filename);
	}

	private void loadEnglishModel(String filename) {
		try {
			lp = LexicalizedParser.loadModel(filename);
		} catch (Exception e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
		}
	}

	/**
	 * Extracts syntax features from a given text and returns a HashMap with only
	 * one Entry: the body of a comment as key and an average of the correct syntax
	 * as value
	 * @param text, the text to extract features
	 * @return features in a HashMap
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting syntax feature for text " + text.getId());
		Utils.FILE_LOGGER.info(startingMessageLog + "Text to check: " + text.getPrepMessage());
		LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
		List<HasWord> tokens = new DocumentPreprocessor(new StringReader(text.getPrepMessage())).iterator().next();
		lpq.parse(tokens);
		List<ScoredObject<Tree>> kBest = lpq.getKBestPCFGParses(1);
		features = new HashMap<>();
		Utils.FILE_LOGGER.info(startingMessageLog + "Parsing score for text " + text.getId() + " is: " + kBest.get(0).score());
		features.put(prefix, kBest.get(0).score());
		features.put(sentimentPrefix, findSentiment(text));
		return features;
	}

	/**
	 * Extracts sentiment feature from a given text and returns the double value
	 * of the main sentiment
	 * @param text, the text to extract features
	 * @return sentiment value
	 */
	protected Double findSentiment(Text text) {
		String tweet = text.getPrepMessage();
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        Utils.FILE_LOGGER.info(startingMessageLog + "Extracting sentiment for tweet " + tweet);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (tweet != null && tweet.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}
			}
		}
		Utils.FILE_LOGGER.info(startingMessageLog + "Sentiment estimation " + mainSentiment + " for text label " + text.getLabel());
		return Double.valueOf(mainSentiment);
	}

	/**
	 * Extracts sentiment feature from a given text and returns the double value
	 * of the total sentiment rate
	 * @param text, the text to extract features
	 * @return sentiment rate
	 */
	protected Double getStanfordSentimentRate(Text text) {
		String sentimentText = text.getPrepMessage();
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting sentiment for tweet " + sentimentText);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int totalRate = 0;
        String[] linesArr = sentimentText.split("\\.");
        for (int i = 0; i < linesArr.length; i++) {
            if (linesArr[i] != null) {
                Annotation annotation = pipeline.process(linesArr[i]);
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    int score = RNNCoreAnnotations.getPredictedClass(tree);
                    totalRate = totalRate + (score - 2);
                }
            }
        }
        Utils.FILE_LOGGER.info(startingMessageLog + "Sentiment rate " + totalRate + " for text label " + text.getLabel());
        return Double.valueOf(totalRate);
    }

}
