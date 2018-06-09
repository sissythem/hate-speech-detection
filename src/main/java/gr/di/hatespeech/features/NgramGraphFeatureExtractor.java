package gr.di.hatespeech.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.events.CalculatorListener;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**NgramGraphFeatureExtractor
 * Implementation of BaseGraphFeatureExtractor. This class can be used in order
 * to extract features for both DocumentNGramGraph and DocumentWordGraph
 * 
 * @author sissy
 */
public class NgramGraphFeatureExtractor extends BaseGraphFeatureExtractor {
	private static String startingMessageLog = "[" + NgramGraphFeatureExtractor.class.getSimpleName() + "] ";
	
	public NgramGraphFeatureExtractor() {
		
	}
	
	/**
	 * NgramGraphFeatureExtractor constructor. The text collection should contain
	 * 90% of all the training instances, which will be used to construct the class
	 * graphs.
	 * @param texts
	 * @param prefix
	 * @param type
	 */
	public NgramGraphFeatureExtractor(List<Text> texts, String type) {
		super(type);
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting graphs for training instances");
		exportTrainingInstances(texts);
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating Hate Speech class graph");
		generateHateSpeechClassGraph();
		Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
		generateCleanClassGraph();
	}

	/**
	 * Method to extract NgramGraph features. Creates the DocumentNGramGraph (or
	 * DocumentWordGraph) of a given text and compares it with the two class graphs.
	 * @param text
	 * @return features, a Map with a text identification and the highest similarity
	 *         with one of the class graphs
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		features = new HashMap<>();
		DocumentNGramGraph textGraph = getTextGraph(text, type);
		NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
		CalculatorListener<GraphSimilarity, GraphSimilarity> calc = getCalculatorListener();
		GraphSimilarity hateSimilarity = ngc.getSimilarityBetween(hateSpeechClassGraph, textGraph);
		GraphSimilarity cleanSimilarity = ngc.getSimilarityBetween(cleanClassGraph, textGraph);
		hateSimilarity.setCalculator(calc);
		cleanSimilarity.setCalculator(calc);
		features.put(hatePrefix, hateSimilarity.getOverallSimilarity());
		features.put(cleanPrefix, cleanSimilarity.getOverallSimilarity());
		return features;
	}
	
	/**
	 * Create a CalculatorListener to calculate OverallSimilarity 
	 * with: ValueSimilarity / SizeSimilarity and override the default
	 * calculation
	 * @return
	 */
	private CalculatorListener<GraphSimilarity, GraphSimilarity> getCalculatorListener() {
		CalculatorListener<GraphSimilarity, GraphSimilarity> calc = new CalculatorListener<GraphSimilarity, GraphSimilarity>() {
            @Override
            public double Calculate(GraphSimilarity oCaller, GraphSimilarity oCalculationParams) {
                return oCalculationParams.SizeSimilarity == 0.0 ? 0.0 :
                        oCalculationParams.ValueSimilarity / oCalculationParams.SizeSimilarity;
            }
        };
        return calc;
	}

}
