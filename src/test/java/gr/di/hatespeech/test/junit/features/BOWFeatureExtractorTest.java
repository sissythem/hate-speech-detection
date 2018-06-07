package gr.di.hatespeech.test.junit.features;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.features.BOWFeaturesExtractor;
import gr.di.hatespeech.utils.Utils;

public class BOWFeatureExtractorTest {
	
	private BOWFeaturesExtractor bowFeaturesExtractor = new BOWFeaturesExtractor(Utils.HATEBASE_CSV_PATH, Utils.BOW_KEY_PREFIX);
	
	@Test
	public void testBOWFeatureExctraction() {
		Text text = new Text(1L, "Asshole nigger nigger", "HateSpeech");
		text.setPrepMessage("Asshole nigger nigger");
		Map<String,Double> features = bowFeaturesExtractor.extractFeatures(text);
		Double expectedCount = 2.0;
		assertEquals(expectedCount.toString(), features.get(Utils.BOW_KEY_PREFIX+"nigger").toString());
	}
}
