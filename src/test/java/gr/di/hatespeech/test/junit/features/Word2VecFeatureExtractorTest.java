package gr.di.hatespeech.test.junit.features;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.features.Word2VecFeatureExtractor;
import gr.di.hatespeech.utils.Utils;

public class Word2VecFeatureExtractorTest {

	private Word2VecFeatureExtractor word2vecExtractor = new Word2VecFeatureExtractor("avg",
			Utils.WORD2VEC_KEY_PREFIX, Utils.WORD2VEC_SMALL_SER);

	@Test
	public void testWord2VecFeatureExtraction() {
		Text text = new Text();
		text.setId(1L);
		text.setDataset(0);
		text.setLabel("Clean");
		text.setOldLabel("Clean");
		text.setTweetId("4565676788787");
		text.setBody("This is the tweet for test test");
		text.setPrepMessage("This is the tweet for test test");
		Map<String, Double> features = word2vecExtractor.extractFeatures(text);
		System.out.println("Map size " + features.values().size());
		features.keySet().stream().forEach(key -> System.out.println(key + " " + features.get(key)));
		Integer actualv = features.size();
		Integer expectedv = new Integer(50);
		assertEquals(expectedv, actualv);
	}

}
