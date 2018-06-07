package gr.di.hatespeech.test.junit.readers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.SourceCsvReader;
import gr.di.hatespeech.readers.TweetCsvReader;
import gr.di.hatespeech.utils.Utils;

public class CsvReadersTest {

	private TweetCsvReader tweetReader = new TweetCsvReader();
	private SourceCsvReader sourceReader = new SourceCsvReader();

	@Test
	public void testSourceCsvReader() {
		String[] record = new String[7];
		record[0] = "1";
		record[1] = "1";
		record[2] = "1";
		record[3] = "1";
		record[4] = "2";
		record[5] = "2";
		record[6] = "This is a tweet";
		Text text = sourceReader.createTextFromLine(record);
		assertEquals("Clean", text.getOldLabel());
	}
	
	@Test
	public void testTweetCsvReadData() {
		List<Text> texts = tweetReader.readData(Utils.TWEET_CSV_PATH);
		assertEquals(true, texts.size()>0);
	}
	
}
