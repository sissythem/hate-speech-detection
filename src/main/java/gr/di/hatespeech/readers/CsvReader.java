package gr.di.hatespeech.readers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVReader;

import gr.di.hatespeech.utils.Utils;

/**
 * This class represents a base Csv reader implementing FileReader and having a
 * utility function for reading csv files using OpenCsv library
 * @author sissy
 */
public abstract class CsvReader<T> implements Reader<T> {
	private static String startingMessageLog = "[" + CsvReader.class.getSimpleName() + "] ";
	
	@Override
	public T readData(String fileName) {
		return null;
	}

	/**
	 * Reads data from a given file using OpenCsv library and returns a list with a
	 * String array. Each array contains a line with all column data
	 * @param fileName, the name of the file to read
	 * @return records, all csv lines
	 */
	public static Iterable<CSVRecord> getCsvRecords(String[] headers, String fileName) {
		InputStreamReader reader = getInputStreamReader(fileName);
		try {
			return getApacheCommonsCsvRecords(headers, reader);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Iterable<CSVRecord> getApacheCommonsCsvRecords(String[] headers, InputStreamReader reader) throws IOException {
		Iterable<CSVRecord> csvRecords = CSVFormat.DEFAULT.withHeader(headers).withFirstRecordAsHeader()
				.parse(reader);
		return csvRecords;
	}

	public static List<String[]> getCsvRecords(String fileName) {
		InputStreamReader reader = getInputStreamReader(fileName);
		return getOpenCsvRecords(reader);
	}

	public static List<String[]> getOpenCsvRecords(InputStreamReader reader) {
		CSVReader openCsvReader = new CSVReader(reader);
		List<String[]> records;
		try {
			records = openCsvReader.readAll();
			openCsvReader.close();
			return records;
		} catch (IOException e) {
			Utils.FILE_LOGGER.error(startingMessageLog + e.getMessage(), e);
			return null;
		}
	}

	public static InputStreamReader getInputStreamReader(String fileName) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classloader.getResourceAsStream(fileName);
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		return reader;
	}

}
