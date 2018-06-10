package gr.di.hatespeech.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.di.hatespeech.utils.GraphUtils;
import org.apache.commons.collections4.CollectionUtils;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * Base feature extractor for Graph Features.
 * Implements FeatureExtractor
 * @author sissy
 */
public abstract class BaseGraphFeatureExtractor implements FeatureExtractor<Map<String, Double>> {
	protected String hatePrefix = Utils.HATE_SPEECH_LABEL;
	protected String cleanPrefix = Utils.CLEAN_LABEL;
	protected String offensivePrefix = Utils.OFFENSIVE_LANGUAGE_LABEL;
	protected String racismPrefix = Utils.RACISM_LABEL;
	protected String sexismPrefix = Utils.SEXISM_LABEL;

	protected String type;
	protected int dataset;
	protected Map<String, Double> features;

	protected List<DocumentNGramGraph> trainingInstancesHate = new ArrayList<>();
	protected List<DocumentNGramGraph> trainingInstancesClean = new ArrayList<>();
	protected List<DocumentNGramGraph> trainingInstancesRacism = new ArrayList<>();
	protected List<DocumentNGramGraph> trainingInstancesSexism = new ArrayList<>();
	protected List<DocumentNGramGraph> trainingInstancesOffensive = new ArrayList<>();

	protected DocumentNGramGraph hateSpeechClassGraph;
	protected DocumentNGramGraph cleanClassGraph;
	protected DocumentNGramGraph offensiveClassGraph;
	protected DocumentNGramGraph racismClassGraph;
	protected DocumentNGramGraph sexismClassGraph;

	public BaseGraphFeatureExtractor() {

	}
	
	/**
	 * BaseGraph constructor. Need to specify the graph type:
	 * either DocumentNGramGraph or DocumentWordGraph
	 * @param type, ngram or word graph
	 * @param dataset, select a specific dataset or both
	 */
	public BaseGraphFeatureExtractor(String type, int dataset) {
		super();
		this.type = type;
		this.dataset = dataset;
	}

	/**
	 * Based on a given list of texts, this method generates a
	 * list of graphs for all training instances. The graphs are
	 * separated in different lists based on their label
	 * @param texts, list of training texts
	 */
	protected void exportTrainingInstances(List<Text> texts) {
		if (!CollectionUtils.isEmpty(texts)) {
			texts.stream().forEach(text -> {
				DocumentNGramGraph textGraph = GraphUtils.getTextGraph(text, type);
				if (text.getOldLabel().equals(hatePrefix)) {
					trainingInstancesHate.add(textGraph);
				} else if (text.getOldLabel().equals(cleanPrefix)) {
					trainingInstancesClean.add(textGraph);
				} else if(text.getOldLabel().equals(racismPrefix)) {
					trainingInstancesRacism.add(textGraph);
				} else if(text.getOldLabel().equals(sexismPrefix)) {
					trainingInstancesSexism.add(textGraph);
				} else if(text.getOldLabel().equals(offensivePrefix)) {
					trainingInstancesOffensive.add(textGraph);
				}
			});
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
