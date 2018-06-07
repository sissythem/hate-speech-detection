package gr.di.hatespeech.test.junit.features;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.features.SpellingFeatureExtractor;
import gr.di.hatespeech.utils.Utils;

public class SpellingFeatureExtractorTest {
	
	private SpellingFeatureExtractor spellingFeatureExtractor = new SpellingFeatureExtractor(Utils.ENGLISH_DICTIONARY_PATH, Utils.SPELLING_KEY_PREFIX);
	
	@Test
	public void test() {
		Text text = new Text();
		text.setId(1L);
		text.setDataset(0);
		text.setLabel("Clean");
		text.setOldLabel("Clean");
		text.setTweetId("4565676788787");
		text.setBody("This is the tweet for test test");
		text.setPrepMessage("This is the tweet for test test");
		Map<String,Double> spellingFeatures = spellingFeatureExtractor.extractFeatures(text);
		Double expectedValue = 0.19;
		assertEquals(expectedValue, spellingFeatures.get(Utils.SPELLING_KEY_PREFIX+text.getId()));
	}

}
