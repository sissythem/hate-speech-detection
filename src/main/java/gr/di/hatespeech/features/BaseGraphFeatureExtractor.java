package gr.di.hatespeech.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * Base feature extractor for Graph Features.
 * Implements FeatureExtractor
 * @author sissy
 */
public abstract class BaseGraphFeatureExtractor implements FeatureExtractor<Map<String, Double>> {
	private static String startingMessageLog = "[" + BaseGraphFeatureExtractor.class.getSimpleName() + "] ";
	protected String hatePrefix;
	protected String cleanPrefix;
	protected String type;
	protected Map<String, Double> features;
	protected List<DocumentNGramGraph> trainingInstancesHate = new ArrayList<>();
	protected List<DocumentNGramGraph> trainingInstancesClean = new ArrayList<>();
	protected DocumentNGramGraph hateSpeechClassGraph;
	protected DocumentNGramGraph cleanClassGraph;
	
	public BaseGraphFeatureExtractor() {
		
	}
	
	/**
	 * BaseGraph constructor. Need to specify the graph type:
	 * either DocumentNGramGraph or DocumentWordGraph
	 * @param type
	 */
	public BaseGraphFeatureExtractor(String type) {
		super();
		this.type = type;
		this.hatePrefix = Utils.HATE_SPEECH_LABEL;
		this.cleanPrefix = Utils.CLEAN_LABEL;
		initClassGraphs(type);
	}
	
	/**
	 * Initialization of class graphs based on the graph type
	 * @param type
	 */
	protected void initClassGraphs(String type) {
		if(type.equalsIgnoreCase("ngram")) {
			hateSpeechClassGraph = new DocumentNGramGraph();
			cleanClassGraph = new DocumentNGramGraph();
		} else if(type.equalsIgnoreCase("word")) {
			hateSpeechClassGraph = new DocumentWordGraph();
			cleanClassGraph = new DocumentWordGraph();
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "Invalid graph type was given");
		}
	}
	
	/**
	 * Gets as input a text and the graph type and generates
	 * a graph for the given text
	 * @param text
	 * @param type
	 * @return
	 */
	protected DocumentNGramGraph getTextGraph(Text text, String type) {
		DocumentNGramGraph textGraph = null;
		if(type.equalsIgnoreCase("ngram")) {
			textGraph = new DocumentNGramGraph();
		} else if(type.equalsIgnoreCase("word")) {
			textGraph = new DocumentWordGraph();
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "Invalid graph type was given");
		}
		textGraph.setDataString(text.getPrepMessage());
		return textGraph;
	}
	
	/**
	 * Based on a given list of texts, this method generates a
	 * list of graphs for all training instances. The graphs are
	 * separated in different lists based on their label
	 * @param texts
	 */
	protected void exportTrainingInstances(List<Text> texts) {
		if (!CollectionUtils.isEmpty(texts)) {
			texts.stream().forEach(text -> {
				DocumentNGramGraph textGraph = getTextGraph(text, type);
				if (text.getLabel().equals(Utils.HATE_SPEECH_LABEL)) {
					trainingInstancesHate.add(textGraph);
				} else if (text.getLabel().equals(Utils.CLEAN_LABEL)) {
					trainingInstancesClean.add(textGraph);
				}
			});
		}
	}

	/**
	 * Generates the class graph for HateSpeech label
	 */
	protected void generateHateSpeechClassGraph() {
		if (!CollectionUtils.isEmpty(trainingInstancesHate)) {
			int i = 0;
			for (DocumentNGramGraph instance : trainingInstancesHate) {
				hateSpeechClassGraph.merge(instance, 1.0 / (1.0 + i));
				i++;
			}
		}
	}

	/**
	 * Generates the class graph for clean label
	 */
	protected void generateCleanClassGraph() {
		if (!CollectionUtils.isEmpty(trainingInstancesClean)) {
			int i = 0;
			for (DocumentNGramGraph instance : trainingInstancesClean) {
				cleanClassGraph.merge(instance, 1.0 / (1.0 + i));
				i++;
			}
		}
	}
	
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		features = new HashMap<>();
		return features;
	}
	
	public Map<String, Double> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Double> features) {
		this.features = features;
	}

}
