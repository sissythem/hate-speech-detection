package gr.di.hatespeech.dataexporters;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;

import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;

/**
 * Class to export TextFeatures object to a database or to a CSV
 * @author sissy
 */
public class TextFeatureExporter extends AbstractDataExporter<TextFeature>{

	private static String startingMessageLog = "[" + FeatureExporter.class.getSimpleName() + "] ";
	private CSVWriter csvWriter;

	public TextFeatureExporter() {
		super();
	}

	/**
	 * Method to export TextFeature objects into csv format
	 * @param data, a list with TextFeature objects
	 * @param headerRecord, the header of the csv
	 * @param fileName, the file name
	 * @param options, specific options to be used in csv file
	 */
	@Override
	public void exportDataToCsv(List<TextFeature> data, String[] headerRecord, String fileName, CsvOptions options) {
		initCsvWriter(fileName, options);
		addHeaderLineToCsv(headerRecord);
		data.forEach(feature -> writeTextFeatureToCsv(feature));
		closeCsvWriter();
	}

	/**
	 * Closes the CSV writer
	 */
	private void closeCsvWriter() {
		try {
			this.csvWriter.close();
		} catch (IOException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
		}
	}

	/**
	 * Creates a String array for a text
	 * @param textFeature, the text feature to be exported
	 */
	private void writeTextFeatureToCsv(TextFeature textFeature) {
		String[] textFeatureArray = new String[3];
		textFeatureArray[0] = textFeature.getId().toString();
		textFeatureArray[1] = textFeature.getFeature().getId().toString();
		textFeatureArray[2] = textFeature.getText().getId().toString();
		csvWriter.writeNext(textFeatureArray);
	}

	/**
	 * Creates header for the CSV file
	 * @param headerRecord, an Array with the header values
	 */
	private void addHeaderLineToCsv(String[] headerRecord) {
		if(headerRecord==null) {
			headerRecord = new String[6];
			headerRecord[0] = Utils.ID;
			headerRecord[1] = Utils.FEATURE_ID;
			headerRecord[2] = Utils.TEXT_ID;
		}
		csvWriter.writeNext(headerRecord);
	}

	/**
	 * Inits the CsvWriter providing the file name and a CsvOptions object
	 * for the Text object parsing
	 * @param fileName, a String with the file's name
	 * @param options, OpenCsv options
	 */
	private void initCsvWriter(String fileName, CsvOptions options) {
		try {
			Writer writer = Files.newBufferedWriter(Paths.get(fileName));
			csvWriter = new CSVWriter(writer, options.getSeparator(), options.getQuoteCharacter(),
					options.getEscapeCharacter(), options.getLineEnd());
		} catch (IOException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
		}
	}
	/**
	 * Method to export all TextFeature objects available (based on the texts)
	 * into a database
	 */
	public void exportDataToDatabase() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts().stream().filter(text -> !StringUtils.isBlank(text.getPrepMessage())).collect(Collectors.toList());
		texts.forEach(text -> {
			if (!StringUtils.isBlank(text.getPrepMessage())) {
				Map<String, Double> f = getVectorFeatures(text);
				FeatureRepository featureRepo = new FeatureRepository();
				factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
				EntityManager em = factory.createEntityManager();
				for (Entry<String, Double> entry : f.entrySet()) {
					if(!entry.getValue().equals(0.0)) {
						Feature feature = featureRepo.findFeatureByDescription(entry.getKey());
						TextFeature textFeature = new TextFeature();
						textFeature.setFeature(feature);
						textFeature.setText(text);
						textFeature.setValue(entry.getValue());
						em.getTransaction().begin();
						em.persist(textFeature);
						em.getTransaction().commit();
					}
				}
				em.close();
				factory.close();
			}
		});
	}

}
