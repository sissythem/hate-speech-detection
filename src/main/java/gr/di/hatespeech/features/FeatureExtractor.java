package gr.di.hatespeech.features;

import gr.di.hatespeech.entities.Text;

/**
 * Interface that represents a feature extractor with a method that needs to be implemented
 * @author sissy
 */
public interface FeatureExtractor<T> {
/**
 * This method gets a Text as input and returns an object with the extracted features
 * @param text
 * @return 
 */
	T extractFeatures(Text text);
}
