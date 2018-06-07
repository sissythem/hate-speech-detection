package gr.di.hatespeech.parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.dataexporters.TextExporter;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.CsvReader;
import gr.di.hatespeech.utils.Logger;
import gr.di.hatespeech.utils.LoggerFactory;
import gr.di.hatespeech.utils.Utils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * This class is used only once in order to retrieve tweets from Twitter API.
 * Tweet ids are read from a csv file, and then ids are used to fetch tweets.
 * In order to get tweets from the API, twitter4j library is used
 * @author sissy
 */
public class TweetParser implements Parser<List<Text>>{
	private static final Logger logger = LoggerFactory.getLogger(TweetParser.class);
	protected static final Long THREAD_SLEEP_DURATION = 1000l * 60l * 15l;

	protected Twitter twitter = new TwitterFactory().getInstance();
	protected Integer csvLine = 1;
	protected Integer callNo = 1;
	protected List<Text> texts;

	public List<Text> parseData(String fileName) {
		String[] headers = { Utils.TWEET_ID, Utils.LABEL};
		Iterable<CSVRecord> records = CsvReader.getCsvRecords(headers, fileName);
		if(records!=null) {
			texts = new ArrayList<>();
			int i = 0;
			for(CSVRecord record : records) {
				fetchFromTwitterAPI(record);
				i++;
				if(i==20) {
					break;
				}
			}
			TextExporter textExporter = new TextExporter();
			textExporter.exportDataToDatabase(texts);
		}
		
		return texts;
	}
	
	/**
	 * Gets a csv line (having the tweet id and the label)
	 * and fetches the tweet from the Twitter API.
	 * In order to avoid extra calls when call limit is reached
	 * Thread.sleep is used
	 * @param record
	 */
	private void fetchFromTwitterAPI(CSVRecord record) {
		boolean callOK = false;
		while (!callOK) {
			callOK = true;
			try {
				addTweetInList(record);
				callOK = true;
				csvLine++;
				callNo++;
			} catch (TwitterException e) {
				logger.info("Stopped in call number " + callNo);
				logger.error(e.getMessage(), e);
				if (e.getStatusCode() == 429) {
					callOK = false;
					try {
						Thread.sleep(THREAD_SLEEP_DURATION);
						callNo = 1;
						logger.info("Thread awaken");
					} catch (InterruptedException e1) {
						logger.info("Interrupted...\n");
					}
				} else {
					logger.warn("Tweet with id " + record.get("tweet_id") + " could not be found");
				}
			}
		}
	}
	
	/**
	 * Gets a csv record and performs a call to the Twitter API
	 * Using the response a Text object is created and added to the
	 * texts list
	 * @param record
	 * @throws TwitterException
	 */
	private void addTweetInList(CSVRecord record) throws TwitterException {
		logger.info("Line items " + record.size());
		Status status = getStatus(record);
		if (status != null) {
			Text text = new Text();
			String message = status.getText();
			message = message.replaceAll("\n", " ");
			message = message.replaceAll("\"", "");
			text.setBody(message);
			text.setOldLabel(record.get(1));
			if(text.getOldLabel().equals("racism") || text.getOldLabel().equals("sexism")) {
				text.setLabel("HateSpeech");
			} else {
				text.setLabel("Clean");
			}
			text.setTweetId(record.get("tweet_id"));
			logger.info("Adding text: " + text.getBody() + " with class name " + text.getLabel()
					+ " in csv line " + csvLine);
			texts.add(text);
		} else {
			logger.warn("Found null status with id" + record.get("tweet_id"));
		}
	}

	private Status getStatus(CSVRecord record) throws TwitterException {
		try {
			logger.info("Getting status no " + csvLine + "with id: " + record.get("tweet_id"));
			return twitter.showStatus(Long.parseLong(record.get("tweet_id")));
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
