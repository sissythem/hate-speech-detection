package gr.di.hatespeech.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.events.CalculatorListener;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.GraphUtils;
import gr.di.hatespeech.utils.Utils;

/**NgramGraphFeatureExtractor
 * Implementation of BaseGraphFeatureExtractor. This class can be used in order
 * to extract features for both DocumentNGramGraph and DocumentWordGraph
 * 
 * @author sissy
 */
public class NgramGraphFeatureExtractor extends BaseGraphFeatureExtractor {
	private static String startingMessageLog = "[" + NgramGraphFeatureExtractor.class.getSimpleName() + "] ";

	/**
	 * Default constructor
	 */
	public NgramGraphFeatureExtractor() {
		
	}
	
	/**
	 * NgramGraphFeatureExtractor constructor. The text collection should contain
	 * 90% of all the training instances, which will be used to construct the class
	 * graphs.
	 * @param texts, list of training texts
	 * @param type, DocumentNGramGraph type (word or ngram)
	 * @param dataset, -1 for including both datasets, the other options are 0 or 1 to include only one dataset
	 */
	public NgramGraphFeatureExtractor(List<Text> texts, String type, int dataset) {
		super(type, dataset);

		switch(dataset) {
			case -1:
				Utils.FILE_LOGGER.info(startingMessageLog + "Extracting graphs for training instances");
				exportTrainingInstances(texts);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating Hate Speech class graph");
				hateSpeechClassGraph = GraphUtils.mergeGraphs(trainingInstancesHate, type);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
				cleanClassGraph = GraphUtils.mergeGraphs(trainingInstancesClean, type);
				break;
			case 0:
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating Racism class graph");
				racismClassGraph = GraphUtils.mergeGraphs(trainingInstancesRacism, type);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating Sexism class graph");
				sexismClassGraph = GraphUtils.mergeGraphs(trainingInstancesSexism, type);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
				cleanClassGraph = GraphUtils.mergeGraphs(trainingInstancesClean, type);
				break;
			case 1:
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating Hate Speech class graph");
				hateSpeechClassGraph = GraphUtils.mergeGraphs(trainingInstancesHate, type);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating Offensive language class graph");
				offensiveClassGraph = GraphUtils.mergeGraphs(trainingInstancesOffensive, type);
				Utils.FILE_LOGGER.info(startingMessageLog + "Generating graph for clean text instances");
				cleanClassGraph = GraphUtils.mergeGraphs(trainingInstancesClean, type);
				break;
		}
	}

	/**
	 * Method to extract NgramGraph features. Creates the DocumentNGramGraph (or
	 * DocumentWordGraph) of a given text and compares it with the two class graphs.
	 * @param text, the text to be compared with class graphs
	 * @return features, a Map with a text identification and the highest similarity
	 *         with one of the class graphs
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		features = new HashMap<>();

		DocumentNGramGraph textGraph = GraphUtils.getTextGraph(text, type);
		NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
		CalculatorListener<GraphSimilarity, GraphSimilarity> calc = getCalculatorListener();

		switch(dataset) {
			case -1:
				GraphSimilarity hateSimilarity = ngc.getSimilarityBetween(hateSpeechClassGraph, textGraph);
				GraphSimilarity cleanSimilarity = ngc.getSimilarityBetween(cleanClassGraph, textGraph);
				hateSimilarity.setCalculator(calc);
				cleanSimilarity.setCalculator(calc);

				features.put(hatePrefix, hateSimilarity.getOverallSimilarity());
				features.put(cleanPrefix, cleanSimilarity.getOverallSimilarity());
				break;
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
				GraphSimilarity hateSimilarity1 = ngc.getSimilarityBetween(hateSpeechClassGraph, textGraph);
				GraphSimilarity offensiveSimilarity = ngc.getSimilarityBetween(offensiveClassGraph, textGraph);
				GraphSimilarity clean1Similarity = ngc.getSimilarityBetween(cleanClassGraph, textGraph);
				hateSimilarity1.setCalculator(calc);
				offensiveSimilarity.setCalculator(calc);
				clean1Similarity.setCalculator(calc);
				features.put(hatePrefix, hateSimilarity1.getOverallSimilarity());
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
	 * @return CalculatorListener to override OverallSimilarity method
	 */
	private CalculatorListener<GraphSimilarity, GraphSimilarity> getCalculatorListener() {
		return (oCaller, oCalculationParams) -> {
			 return oCalculationParams.SizeSimilarity == 0.0 ? 0.0 :
					oCalculationParams.ValueSimilarity / oCalculationParams.SizeSimilarity;
		};
	}

}
