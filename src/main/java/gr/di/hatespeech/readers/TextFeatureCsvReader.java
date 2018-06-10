package gr.di.hatespeech.readers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.utils.Utils;

public class TextFeatureCsvReader extends CsvReader<List<TextFeature>> {
	private static String startingMessageLog = "[" + TextFeatureCsvReader.class.getSimpleName() + "] ";
	protected List<TextFeature> textFeatures = new ArrayList<>();
	protected TweetCsvReader tweetCsvReader = new TweetCsvReader();
	protected List<Text> texts;
	protected List<Feature> features = new ArrayList<>();
	
	public TextFeatureCsvReader() {
		super();
		texts = tweetCsvReader.readData(Utils.TWEET_CSV_PATH);
	}
	
	 /** Reads data from a file and creates a List of TextFeature objects
	 * @param fileName, the name of the file to read
	 * @return Text
	 */
	@Override
	public List<TextFeature> readData(String fileName) {
		String[] headers = {"id", "text_id", "feature_id"};
		Iterable<CSVRecord> csvRecords = getCsvRecords(headers, fileName);
		if (csvRecords != null) {
			textFeatures = new ArrayList<>();
			for (CSVRecord record : csvRecords) {
				TextFeature textFeature = createTextFeatureFromLine(record);
				if(textFeature!=null) {
					textFeatures.add(textFeature);
				}
			}
		}
		return textFeatures;
	 }
	
	private TextFeature createTextFeatureFromLine(CSVRecord record) {
		try {
			TextFeature textFeature = new TextFeature();
			textFeature.setId(Long.parseLong(record.get("id")));
			Long textId = Long.parseLong(record.get("text_id"));
			Long featureId = Long.parseLong(record.get("feature_id"));
			Text text = getText(textId);
			Feature feature = getFeature(featureId);
			textFeature.setText(text);
			textFeature.setFeature(feature);
			Utils.FILE_LOGGER.info(startingMessageLog + textFeature.getId() + " " + textFeature.getText().getId() + " " + textFeature.getFeature().getId());
			return textFeature;
		} catch (NumberFormatException e) {
			Utils.FILE_LOGGER.error(e.getMessage(), e);
			Utils.FILE_LOGGER.error(startingMessageLog + "######### TextFeature that could not be parsed=> id = " + record.get("id") + ", textId = "
					+ record.get("text_id") + ", featureId = " + record.get("feature_id"));
			return null;
		}
	}

	private Text getText(Long textId) {
		return texts.stream().filter(text -> text.getId().equals(textId)).findFirst().get();
	}

	private Feature getFeature(Long featureId) {
		return features.stream().filter(feature -> feature.getId().equals(featureId)).findFirst().get();
	}
}
