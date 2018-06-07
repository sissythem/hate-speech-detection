package gr.di.hatespeech.features;

import java.util.HashMap;
import java.util.Map;

import gr.di.hatespeech.entities.Text;

/**
 * This is a basic representation of a vector feature extractor
 * having a map field representing the extracted features
 * @author sissy
 *
 */
public abstract class BaseVectorFeatureExtractor implements FeatureExtractor<Map<String, Double>> {
	protected Map<String, Double> features;
	protected String prefix;
	
	/**
	 * Default constructor initializing features map
	 */
	public BaseVectorFeatureExtractor(String prefix) {
		super();
		this.features = new HashMap<>();
		this.prefix = prefix;
	}
	
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		return null;
	}
	
	public Map<String, Double> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Double> features) {
		this.features = features;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
