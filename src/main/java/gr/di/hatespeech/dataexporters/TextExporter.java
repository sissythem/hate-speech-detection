package gr.di.hatespeech.dataexporters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.opencsv.CSVWriter;

import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.preprocessors.TextPreprocessor;
import gr.di.hatespeech.repositories.TextRepository;
import gr.di.hatespeech.utils.Utils;

/**
 * Implementation of DataExporter for Text objects.
 * Writes Text objects into a csv file or in the database.
 * For csv writing, it uses OpenCsv library or Apache Commons CSV
 * @author sissy
 */
public class TextExporter implements DataExporter<Text> {
	private static String startingMessageLog = "[" + TextExporter.class.getSimpleName() + "] ";
	protected static EntityManagerFactory factory;
	private CSVWriter csvWriter;

	public TextExporter() {

	}

	/**
	 * Exports a list of Text objects in a database
	 */
	@Override
	public void exportDataToDatabase(List<Text> texts) {
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		texts.forEach(text -> {
			em.getTransaction().begin();
			em.persist(text);
			em.getTransaction().commit();
		});
		em.close();
		factory.close();
	}
	
	/**
	 * Exports a list of Text objects in a csv format
	 * with Apache Commons Csv library
	 */
	public void exportDataToCsv(List<Text> texts, String[] headers, String fileName) {
		try {
			FileWriter out = new FileWriter(fileName);
			try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
				      .withHeader(headers))) {
				    	texts.forEach(text -> {
							try {
								printer.printRecord(text.getId(), text.getBody(), text.getLabel(), text.getOldLabel(), text.getDataset(), text.getTweetId());
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
	 * Exports a list of Text objects in a csv format
	 * with OpenCsvLibrary
	 */
	@Override
	public void exportDataToCsv(List<Text> texts, String[] headerRecord, String fileName, CsvOptions options) {

		initCsvWriter(fileName, options);
		addHeaderLineToCsv(headerRecord);
		texts.forEach(text -> writeTweetsToCsv(text));
		closeCsvWriter();
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
	 * Creates header for the CSV file
	 * @param headerRecord, an Array with the header values
	 */
	private void addHeaderLineToCsv(String[] headerRecord) {
		if(headerRecord==null) {
				headerRecord = new String[6];
				headerRecord[0] = Utils.ID;
				headerRecord[1] = Utils.BODY;
				headerRecord[2] = Utils.LABEL;
				headerRecord[3] = Utils.OLD_LABEL;
				headerRecord[4] = Utils.DATASET;
				headerRecord[5] = Utils.TWEET_ID;
		}
		csvWriter.writeNext(headerRecord);
	}

	/**
	 * Creates a String array for a text
	 * @param text, the text to be exported
	 */
	private void writeTweetsToCsv(Text text) {
		String[] textArray = new String[6];
		textArray[0] = text.getId().toString();
		textArray[1] = text.getBody();
		textArray[2] = text.getLabel();
		textArray[3] = text.getOldLabel();
		textArray[4] = text.getDataset().toString();
		textArray[5] = text.getTweetId();
		csvWriter.writeNext(textArray);
	}

	/**
	 * Removes stopwords from all texts using TextPreprocessor
	 */
	public void removeStopWords() {
		TextRepository textRepo = new TextRepository();
		List<Text> texts = textRepo.findAllTexts();
		TextPreprocessor textPreprocessor = new TextPreprocessor(true, Utils.STOPWORDS_CSV_PATH);
		texts = textPreprocessor.preprocessTexts(texts);
		factory = Persistence.createEntityManagerFactory(Utils.PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		texts.forEach(text -> {
			em.getTransaction().begin();
			em.merge(text);
			em.getTransaction().commit();
		});
		em.close();
		factory.close();
	}

}
