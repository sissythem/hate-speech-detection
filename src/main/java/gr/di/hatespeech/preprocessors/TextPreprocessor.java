package gr.di.hatespeech.preprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.CsvReader;
import gr.di.hatespeech.utils.Utils;

/**
 * This class represents a preprocessor for Text objects. A stopwords list is
 * genarated based on a given csv file. In order those words to be removed, the
 * boolean variable removeStopWords should be set to true.
 * @author sissy
 */
public class TextPreprocessor {
	private static String startingMessageLog = "[" + TextPreprocessor.class.getSimpleName() + "] ";
	protected Set<String> stopwords;
	protected Boolean removeStopWords;

	/**
	 * TextPreprocessor constructor. Stores the choice to remove or not the
	 * stopwords from a text
	 * @param removeStopWords
	 */
	public TextPreprocessor(Boolean removeStopWords, String fileName) {
		super();
		this.removeStopWords = removeStopWords;
		generateStopwordsList(fileName);
	}

	/**
	 * Gets a list of Text objects as input and removes stopwords, urls etc from its
	 * content and then returns an updated list
	 * @param texts, the list of texts to be preprocessed
	 * @return cleanedText, a List<Text> containing the cleaned texts
	 */
	public List<Text> preprocessTexts(List<Text> texts) {
		List<Text> cleanedTexts = new ArrayList<>();
		texts.stream().forEach(text -> {
			text = preprocessText(text);
			cleanedTexts.add(text);
		});
		return cleanedTexts;
	}

	public Text preprocessText(Text text) {
		text = clearData(text);
		if (removeStopWords) {
			text = removeStopWords(text);
		}
		return text;
	}

	/**
	 * Clears a given text. Removes RT, mentions, URLs and unecessary spaces
	 * @param text
	 */
	protected Text clearData(Text text) {
		String message = text.getBody();
		Utils.FILE_LOGGER.info(startingMessageLog + "Original message: " + message);
		message = removeTwitterSpecificChars(message);
		message = replaceMultipleSpacesWithASingleOne(message);
		message = removeURLsAndLowerCaseMessage(message);
		message = removeGarbage(message);
		message = replaceMultipleSpacesWithASingleOne(message);
		text.setPrepMessage(message.trim());
		Utils.FILE_LOGGER.info(startingMessageLog + "Cleaned message: " + message);
		return text;
	}

	protected String removeTwitterSpecificChars(String message) {
		message = message.replaceAll(" RT ", " ");
		message = message.replaceAll("^RT ", " ");
		message = message.replaceAll("@[^\\s]+ ", "");
		message = message.replaceAll("#[^\\s]+", " ");
		return message;
	}

	protected String removeURLsAndLowerCaseMessage(String message) {
		message = message.replaceAll("www[^\\s]+", " ");
		message = message.replaceAll("http[^\\s]+", " ");
		message = message.replaceAll("https.[^\\s]+", " ");
		message = message.toLowerCase();
		return message;
	}

	protected String removeGarbage(String message) {
		message = message.replaceAll(",", " ");
		message = message.replaceAll("!", " ");
		message = message.replaceAll("\\.", " ");
		message = message.replaceAll("\\?", " ");
		message = message.replaceAll(";", " ");
		message = message.replaceAll("&amp", " ");
		message = message.replaceAll("&", " ");
		message = message.replaceAll("[0-9]+", " ");
		message = message.replaceAll("&#[0-9]+", " ");
		message = message.replaceAll("\"", "");
		return message;
	}

	protected String replaceMultipleSpacesWithASingleOne(String message) {
		message = message.replaceAll("\n", " ");
		message = message.replaceAll("[ ]+", " ");
		return message;
	}

	/**
	 * Removes stop words from a given text, based on an english stopwords list
	 * @param text
	 */
	public Text removeStopWords(Text text) {
		Utils.FILE_LOGGER.info(startingMessageLog + "Checking text for stopwords: " + text.getPrepMessage());
		List<String> words = Arrays.asList(text.getPrepMessage().split(" "));
		List<String> remainingWords = new ArrayList<>();
		words.stream().forEach(word -> {
			if (!stopwords.contains(word)) {
				remainingWords.add(word);
			} else {
				Utils.FILE_LOGGER.info(startingMessageLog + "Stopword found: " + word);
			}
		});
		if (!CollectionUtils.isEmpty(remainingWords)) {
			text.setPrepMessage(String.join(" ", remainingWords));
		}
		return text;
	}

	/**
	 * Reads the stopwords list from a csv file
	 */
	protected void generateStopwordsList(String fileName) {
		stopwords = new HashSet<>();
		String[] headers = { Utils.STOPWORDS };
		Iterable<CSVRecord> records = CsvReader.getCsvRecords(headers, fileName);
		if (records != null) {
			for (CSVRecord record : records) {
				stopwords.add(record.get(Utils.STOPWORDS));
			}
		}
	}

	public Set<String> getStopwords() {
		return stopwords;
	}

	public void setStopwords(Set<String> stopwords) {
		this.stopwords = stopwords;
	}

}
