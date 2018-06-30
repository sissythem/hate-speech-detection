package gr.di.hatespeech.dataexporters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.opencsv.CSVWriter;
import gr.di.hatespeech.entities.Feature;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.entities.TextFeature;
import gr.di.hatespeech.repositories.FeatureRepository;
import gr.di.hatespeech.repositories.TextFeatureRepository;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Class to export TextFeatures object to a database or to a CSV
 * @author sissy
 */
public class FeatureExporter extends AbstractDataExporter<Feature> {

	private static String startingMessageLog = "[" + FeatureExporter.class.getSimpleName() + "] ";
	private CSVWriter csvWriter;

	public FeatureExporter() {
		super();
	}

	/**
	 * Method to export Feature objects in csv format
	 * @param data, a list with the Features to be exported
	 * @param headerRecord, the header of the CSV
	 * @param fileName, the file name
	 * @param options, specified options to be used for the CSV creation
	 */
	@Override
	public void exportDataToCsv(List<Feature> data, String[] headerRecord, String fileName, CsvOptions options) {
		initCsvWriter(fileName, options);
		addHeaderLineToCsv(headerRecord);
		data.forEach(feature -> writeFeatureToCsv(feature));
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
	 * @param feature, the feature to be exported
	 */
	private void writeFeatureToCsv(Feature feature) {
		String[] featureArray = new String[3];
		featureArray[0] = feature.getId().toString();
		featureArray[1] = feature.getDescription();
		featureArray[2] = feature.getKind();
		csvWriter.writeNext(featureArray);
	}

	/**
	 * Creates header for the CSV file
	 * @param headerRecord, an Array with the header values
	 */
	private void addHeaderLineToCsv(String[] headerRecord) {
		if(headerRecord==null) {
			headerRecord = new String[6];
			headerRecord[0] = Utils.ID;
			headerRecord[1] = Utils.DESCRIPTION;
			headerRecord[2] = Utils.KIND;
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
	 * Exports a list of Text objects in a csv format
	 * with Apache Commons Csv library
	 */
	public void exportDataToCsv(List<Feature> features, String[] headers, String fileName) {
		try {
			FileWriter out = new FileWriter(fileName);
			try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
				      .withHeader(headers))) {
				    	features.forEach(feature -> {
							try {
								addKind(feature);
								printer.printRecord(feature.getId(), feature.getDescription(), feature.getKind());
							} catch (IOException e) {
								Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(),e);
							}
						});
				    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exports all the features into the database. The Feature object
	 * is used for each feature used in the project (for each text the project should
	 * calculate the relevant value)
	 */
	public void exportDataToDatabase() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts().stream().limit(1).collect(Collectors.toList());
		Text text = texts.get(0);
		Map<String, Double> f = getVectorFeatures(text);
		Set<String> features = f.keySet();
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		features.forEach(feature -> exportFeatureToDatabase(em, feature));
		em.close();
		factory.close();
	}

	/**
	 * Create the specific feature and store it to database
	 * @param em, the EntityManager
	 * @param feature, feature's description
	 */
	public void exportFeatureToDatabase(EntityManager em, String feature) {
		Feature feat = new Feature();
		feat.setDescription(feature);
		addKind(feat);
		em.getTransaction().begin();
		em.persist(feat);
		em.getTransaction().commit();
	}

	/**
	 * Add the feature's kind at the Feature object
	 * @param feature, the Feature object
	 */
	public void addKind(Feature feature) {
		if(feature.getDescription().contains(Utils.BOW_KEY_PREFIX)) {
			feature.setKind("bow");
		} else if(feature.getDescription().contains(Utils.CHAR_NGRAM_KEY_PREFIX)) {
			feature.setKind("charngram");
		} else if(feature.getDescription().contains(Utils.NGRAM_KEY_PREFIX)) {
			feature.setKind("ngram");
		} else if(feature.getDescription().contains(Utils.SPELLING_KEY_PREFIX)) {
			feature.setKind("spelling");
		} else if(feature.getDescription().contains(Utils.SYNTAX_KEY_PREFIX)) {
			feature.setKind("syntax");
		} else if(feature.getDescription().contains(Utils.SENTIMENT_KEY_PREFIX)) {
			feature.setKind("sentiment");
		} else if(feature.getDescription().contains(Utils.WORD2VEC_KEY_PREFIX)) {
			feature.setKind("word2vec");
		}
		
	}

}
