package gr.di.hatespeech.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Utils;

/**
 * This class represents a word2vec feature extractor implementing the
 * BaseVectorFeatureExtractor Reads pretrained word2vec values from a csv and
 * stores them in a map From this file, it calculates the vector dimension and
 * requires an aggregation type in order to return the features map
 * 
 * @author sissy
 */
public class Word2VecFeatureExtractor extends BaseVectorFeatureExtractor {
	private static String startingMessageLog = "[" + Word2VecFeatureExtractor.class.getSimpleName() + "] ";
	protected Map<String, List<Double>> pretrainedWord2Vec;
	protected int vectorDimension;
	protected String type;

	/**
	 * Word2VecFeatureExtractor constructor. AggregationType defines the aggregation
	 * of the vector
	 * 
	 * @param type
	 */
	public Word2VecFeatureExtractor(String type, String prefix, String... fileNames) {
		super(prefix);
		this.type = type;
		readPretrainedVectors(fileNames);
		setVectorDimension();
	}

	/**
	 * Reads the pre-trained vectors for some given words and generates a Map with
	 * the words and the vectors
	 */
	protected void readPretrainedVectors(String... fileNames) {
		if (fileNames == null) {
			Utils.FILE_LOGGER.error(startingMessageLog + "No file was given for the pretrained word vectors!!");
			return;
		}
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		List<String> listFileNames = Arrays.asList(fileNames);
		if (CollectionUtils.isEmpty(listFileNames)) {
			Utils.FILE_LOGGER.error(startingMessageLog + "No file was given!");
			return;
		}
		listFileNames.stream().forEach(fileName -> {
			extractPretrainedVectors(classloader, fileName);
		});
	}

	@SuppressWarnings("unchecked")
	private void extractPretrainedVectors(ClassLoader classloader, String fileName) {
		this.pretrainedWord2Vec = new HashMap<>();
		try {
			InputStream inputStream = classloader.getResourceAsStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			pretrainedWord2Vec.putAll((HashMap<String, List<Double>>) ois.readObject());
			ois.close();
			inputStream.close();
		} catch (IOException ioe) {
			Utils.FILE_LOGGER.error(startingMessageLog + "File not found!");
			Utils.FILE_LOGGER.error(startingMessageLog + ioe.getMessage(),ioe);
			return;
		} catch (ClassNotFoundException c) {
			Utils.FILE_LOGGER.error(startingMessageLog + "Class not found");
			Utils.FILE_LOGGER.error(startingMessageLog + c.getMessage(),c);
			return;
		}
	}

	/**
	 * Based on the csv file it stores the vector dimension
	 */
	protected void setVectorDimension() {
		if (!MapUtils.isEmpty(pretrainedWord2Vec)) {
			for (Entry<String, List<Double>> entry : pretrainedWord2Vec.entrySet()) {
				vectorDimension = entry.getValue().size();
				break;
			}
		} else {
			Utils.FILE_LOGGER.error(startingMessageLog + "Pretrained vectors map is empty or null!!");
		}
	}

	/**
	 * Word2Vec extractor overrides the extracFeatures function Gets a Text as input
	 * and returns a Map with the extracted features
	 */
	@Override
	public Map<String, Double> extractFeatures(Text text) {
		features = new HashMap<>();
		if (MapUtils.isEmpty(pretrainedWord2Vec)) {
			Utils.FILE_LOGGER.error(startingMessageLog + "Pretrained vectors map is empty or null!!");
		} else {
			Utils.FILE_LOGGER.info(startingMessageLog + "Extracting word2vec features for text " + text.getId());
			extractFeaturesInternal(text);
		}
		return features;
	}

	private void extractFeaturesInternal(Text text) {
		List<List<Double>> vectors = new ArrayList<>();
		List<String> words = Arrays.asList(text.getPrepMessage().split(" "));
		words.stream().forEach(word -> {
			Utils.FILE_LOGGER.debug(startingMessageLog + "Examining word " + word);
			if (pretrainedWord2Vec.containsKey(word)) {
				vectors.add(pretrainedWord2Vec.get(word));
			}
		});
		aggregateVector(vectors);
	}

	/**
	 * This functions gets the Text represented by vectors and based on the
	 * aggregation type chosen by the user aggregates the given vectors
	 * @param vectors
	 */
	protected void aggregateVector(List<List<Double>> vectors) {
		if (type.equalsIgnoreCase("avg")) {
			averageAggregation(vectors);
		} else if(type.equalsIgnoreCase("max")) {
			maxAggregation(vectors);
		}
	}

	/**
	 * Average aggregation of the list of vectors
	 * @param vectors
	 */
	protected void averageAggregation(List<List<Double>> vectors) {
		for (int j = 0; j < this.vectorDimension; j++) {
			features.put(prefix + "Feature" + j, 0.0);
			double sum = 0.0d;
			for (int i = 0; i < vectors.size(); i++) {
				sum += vectors.get(i).get(j);
			}
			if(vectors.size()>0) {
				features.put(prefix + "Feature" + j, sum / vectors.size());
			}
			
		}
	}

	/**
	 * Max aggregation of the list of vectors
	 * @param vectors
	 */
	protected void maxAggregation(List<List<Double>> vectors) {
		for (int j = 0; j < this.vectorDimension; j++) {
			double max = Double.MIN_VALUE;
			for (int i = 0; i < vectors.size(); i++) {
				max = vectors.get(i).get(j) > max ? vectors.get(i).get(j) : max;
			}
			features.put(prefix + "Feature" + j, max);
		}
	}

	public Map<String, List<Double>> getPretrainedWord2Vec() {
		return pretrainedWord2Vec;
	}

	public void setPretrainedWord2Vec(Map<String, List<Double>> pretrainedWord2Vec) {
		this.pretrainedWord2Vec = pretrainedWord2Vec;
	}

}
