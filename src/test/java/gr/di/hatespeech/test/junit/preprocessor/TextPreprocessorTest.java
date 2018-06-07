package gr.di.hatespeech.test.junit.preprocessor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.preprocessors.TextPreprocessor;
import gr.di.hatespeech.utils.Utils;

public class TextPreprocessorTest {

	private TextPreprocessor textPreprocessor = new TextPreprocessor(true, Utils.STOPWORDS_CSV_PATH);
	
	@Test
	public void testTextPreprocessing() {
		Text text = new Text();
		text.setBody("@ME Hello http://www.google.com RT #hello &amp hi");
		text = textPreprocessor.preprocessText(text);
		assertEquals("hello hi", text.getPrepMessage());
		assertEquals(true, textPreprocessor.getStopwords().size()>0);
	}

}
