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

public class NgramGraphMultiLabelFeatureExtractor extends BaseMultiLabelGraphFeatureExtractor {
	private static String startingMessageLog = "[" + NgramGraphMultiLabelFeatureExtractor.class.getSimpleName() + "] ";
	
	public NgramGraphMultiLabelFeatureExtractor() {
		
	}
	
	public NgramGraphMultiLabelFeatureExtractor(List<Text> texts, String type, int dataset) {
		super(type,dataset);
		Utils.FILE_LOGGER.info(startingMessageLog + "Extracting graphs for training instances");
		exportTrainingInstances(texts);
		switch(dataset) {
		case 0:
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating Racism class graph");
			generateRacismClassGraph();
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating Sexism class graph");
			generateSexismClassGraph();
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
			generateCleanClassGraph();
			break;
		case 1:
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating Hate Speech class graph");
			generateHateSpeechClassGraph();
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating Offensive language class graph");
			generateOffensiveClassGraph();
			Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
			generateCleanClassGraph();
			break;
		}
	}
	
	@Override
	public Map<String,Double> extractFeatures(Text text) {
		features = new HashMap<>();
		DocumentNGramGraph textGraph = getTextGraph(text, type);
		NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
		CalculatorListener<GraphSimilarity, GraphSimilarity> calc = getCalculatorListener();
		switch(dataset) {
		case 0:
			GraphSimilarity racismSimilarity = ngc.getSimilarityBetween(racismClassGraph, textGraph);
			GraphSimilarity sexismSimilarity = ngc.getSimilarityBetween(sexismClassGraph, textGraph);
			GraphSimilarity clean0Similarity = ngc.getSimilarityBetween(cleanClassGraph, textGraph);
			racismSimilarity.setCalculator(calc);
			sexismSimilarity.setCalculator(calc);
			clean0Similarity.setCalculator(calc);
			features.put(racismPrefix, racismSimilarity.getOverallSimilarity());
			features.put(sexismPrefix, sexismSimilarity.getOverallSimilarity());
			features.put(cleanPrefix, clean0Similarity.getOverallSimilarity());
			break;
		case 1:
			GraphSimilarity hateSimilarity = ngc.getSimilarityBetween(hateSpeechClassGraph, textGraph);
			GraphSimilarity offensiveSimilarity = ngc.getSimilarityBetween(offensiveClassGraph, textGraph);
			GraphSimilarity clean1Similarity = ngc.getSimilarityBetween(cleanClassGraph, textGraph);
			hateSimilarity.setCalculator(calc);
			offensiveSimilarity.setCalculator(calc);
			clean1Similarity.setCalculator(calc);
			features.put(hatePrefix, hateSimilarity.getOverallSimilarity());
			features.put(cleanPrefix, clean1Similarity.getOverallSimilarity());
			features.put(offensivePrefix, offensiveSimilarity.getOverallSimilarity());
			break;
		}
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
