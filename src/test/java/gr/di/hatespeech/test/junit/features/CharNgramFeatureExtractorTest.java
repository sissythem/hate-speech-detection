package gr.di.hatespeech.test.junit.features;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.features.CharacterNGramFeatureExtractor;
import gr.di.hatespeech.utils.Utils;

public class CharNgramFeatureExtractorTest {

	private CharacterNGramFeatureExtractor characterNGramFeatureExtractor = new CharacterNGramFeatureExtractor(Utils.CHAR_NGRAM_KEY_PREFIX);
	
	@Test
	public void testCharNgramFeatureExtraction() {
		Text text = new Text();
		text.setId(1L);
		text.setDataset(0); 
		text.setLabel("Clean");
		text.setOldLabel("Clean");
		text.setTweetId("4565676788787");
		text.setBody("This is a tweet to be tested tested");
		text.setPrepMessage("This is a tweet to be tested tested");
		Map<String,Double> features = characterNGramFeatureExtractor.extractFeatures(text);
		features.keySet().stream().forEach(key->System.out.println(key + " " + features.get(key)));
		Double actualValue = features.get("charngramfeatures/hi_");
		Double expectedValue = 0.0;
		assertEquals(expectedValue.toString(), actualValue.toString());
	}

}
