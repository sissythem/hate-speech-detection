package gr.di.hatespeech.readers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.utils.Utils;

public class FeatureCsvReader extends CsvReader<List<Feature>> {
	private static String startingMessageLog = "[" + FeatureCsvReader.class.getSimpleName() + "] ";
	protected List<Feature> features = new ArrayList<>();
	
	/**
	 * Reads data from a file and creates a List of Feature objects
	 * @param fileName, the name of the file to read
	 * @return Text
	 */
	@Override
	public List<Feature> readData(String fileName) {
		String[] headers = {"id", "description", "kind"};
		Iterable<CSVRecord> csvRecords = getCsvRecords(headers, fileName);
		if (csvRecords != null) {
			features = new ArrayList<>();
			for (CSVRecord record : csvRecords) {
				Feature feature = createFeatureFromLine(record);
				if(feature!=null) {
					features.add(feature);
				}
			}
		}
		return features;
	}

	private Feature createFeatureFromLine(CSVRecord record) {
		try {
			Feature feature = new Feature();
			feature.setId(Long.parseLong(record.get("id")));
			feature.setDescription(record.get("description"));
			feature.setKind(record.get("kind"));
			Utils.FILE_LOGGER.info(startingMessageLog + feature.getId() + " " + feature.getDescription() + " " + feature.getKind());
			return feature;
		} catch (NumberFormatException e) {
			Utils.FILE_LOGGER.error(e.getMessage(), e);
			Utils.FILE_LOGGER.error(startingMessageLog + "######### Feature that could not be parsed=> id = " + record.get("id") + ", description = "
					+ record.get("description") + ", kind = " + record.get("kind"));
			return null;
		}
		
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
}
