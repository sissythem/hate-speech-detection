package gr.di.hatespeech.readers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * Implementation of CsvReader for extracting texts from a file containing
 * Tweets
 * @author sissy
 */
public class TweetCsvReader extends CsvReader<List<Text>> {
	private static String startingMessageLog = "[" + TweetCsvReader.class.getSimpleName() + "] ";
	protected List<Text> texts = new ArrayList<>();

	/**
	 * Reads data from a file and creates a list of Text objects
	 * @param fileName, the name of the file to read
	 * @return Text
	 */
	@Override
	public List<Text> readData(String fileName) {
		String[] headers = { Utils.ID, Utils.BODY, Utils.LABEL, Utils.OLD_LABEL, Utils.DATASET, Utils.TWEET_ID, Utils.PROCESSED_BODY};
		Iterable<CSVRecord> csvRecords = getCsvRecords(headers, fileName);
		if (csvRecords != null) {
			texts = new ArrayList<>();
			for (CSVRecord record : csvRecords) {
				Text text = createTextFromLine(record);
				if (text != null) {
					texts.add(text);
				}
			}
		}
		return texts;
	}

	public Text createTextFromLine(CSVRecord record) {

		try {
			Text text = new Text();
			text.setId(Long.parseLong(record.get("id")));
			text.setBody(record.get("body"));
			text.setLabel(record.get("label"));
			text.setOldLabel(record.get("old_label"));
			text.setDataset(Integer.parseInt(record.get("dataset")));
			text.setPrepMessage(record.get("processed_body"));
			if (text.getDataset().equals(0)) {
				text.setTweetId(record.get("tweet_id"));
			}
			Utils.FILE_LOGGER.info(startingMessageLog + text.getId() + " " + text.getBody() + " " + text.getLabel());
			return text;
		} catch (NumberFormatException e) {
			Utils.FILE_LOGGER.error(e.getMessage(), e);
			Utils.FILE_LOGGER.error(startingMessageLog + "#########Text that could not be parsed=> id = " + record.get("id") + ", body = "
					+ record.get("body") + ", label = " + record.get("label"));
			return null;
		}
		
	}

	public List<Text> getTexts() {
		return texts;
	}

	public void setTexts(List<Text> texts) {
		this.texts = texts;
	}

}
