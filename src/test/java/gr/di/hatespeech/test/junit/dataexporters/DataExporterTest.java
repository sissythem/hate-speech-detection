package gr.di.hatespeech.test.junit.dataexporters;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import gr.di.hatespeech.dataexporters.CsvOptions;
import gr.di.hatespeech.dataexporters.TextExporter;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.readers.CsvReader;

public class DataExporterTest {
	private TextExporter textExporter = new TextExporter();

	private enum CsvLibrary {
		Open, Apache
	}
	
	@Test
	public void testOpenCsvDataExport() {
		List<Text> texts = new ArrayList<>();
		Text text = new Text();
		text.setBody("Hi how are you? I am fine thanks");
		text.setDataset(0);
		text.setId(1L);
		text.setLabel("Clean");
		text.setOldLabel("Clean");
		text.setTweetId("1");
		texts.add(text);
		String[] headerRecord = new String[6];
		headerRecord[0] = "id";
		headerRecord[1] = "body";
		headerRecord[2] = "label";
		headerRecord[3] = "old_label";
		headerRecord[4] = "dataset";
		headerRecord[5] = "tweet_id";
		CsvOptions options = new CsvOptions();
		textExporter.exportDataToCsv(texts, headerRecord, "./src/test/resources/texts.csv", options);
		Text textToBeTested =readFile(headerRecord, CsvLibrary.Open);
		// OpenCsv library does wrong parsing!!
		assertEquals(text.getBody(), textToBeTested.getBody());
		assertEquals(text.getLabel(), textToBeTested.getLabel());
	}

	@Test
	public void textApacheCommonCsvDataExport() {
		List<Text> texts = new ArrayList<>();
		Text text = new Text();
		text.setBody("Hi, how are you? I am fine, thanks");
		text.setDataset(0);
		text.setId(1L);
		text.setLabel("Clean");
		text.setOldLabel("Clean");
		text.setTweetId("1");
		texts.add(text);
		String[] headers = new String[6];
		headers[0] = "id";
		headers[1] = "body";
		headers[2] = "label";
		headers[3] = "old_label";
		headers[4] = "dataset";
		headers[5] = "tweet_id";
		textExporter.exportDataToCsv(texts, headers, "./src/test/resources/texts.csv");
		Text textToBeTested =readFile(headers, CsvLibrary.Apache);
		assertEquals(text.getBody(), textToBeTested.getBody());
		assertEquals(text.getLabel(), textToBeTested.getLabel());

	}

	public Text readFile(String[] headers, CsvLibrary library) {
		try {
			InputStream inputStream = new FileInputStream("./src/test/resources/texts.csv");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			Text textToBeTested = new Text();
			switch(library) {
			case Apache:
				Iterable<CSVRecord> records = CsvReader.getApacheCommonsCsvRecords(headers, inputStreamReader);
				for(CSVRecord record : records) {
					textToBeTested.setId(Long.parseLong(record.get("id")));
					textToBeTested.setBody(record.get("body"));
					textToBeTested.setLabel(record.get("label"));
				}
				break;
			case Open:
				List<String[]> openCsvRecords = CsvReader.getOpenCsvRecords(inputStreamReader);
				openCsvRecords.stream().forEach(record -> {
					if(!record[0].equalsIgnoreCase("id")) {
						textToBeTested.setId(Long.parseLong(record[0]));
						textToBeTested.setBody(record[1]);
						textToBeTested.setLabel(record[2]);
					}
				});
				break;
			}
			return textToBeTested;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
