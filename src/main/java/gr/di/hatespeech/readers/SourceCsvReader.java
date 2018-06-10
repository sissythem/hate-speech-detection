package gr.di.hatespeech.readers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import gr.di.hatespeech.dataexporters.TextExporter;
import gr.di.hatespeech.entities.Text;
import gr.di.hatespeech.utils.Logger;
import gr.di.hatespeech.utils.LoggerFactory;
import gr.di.hatespeech.utils.Utils;

/**
 * This class is used only once to retrieve tweets from a csv containing tweets
 * for hate speech and offensive language. Tweets are exported to a database
 * @author sissy
 */
public class SourceCsvReader extends CsvReader<List<Text>> {
	private static final Logger logger = LoggerFactory.getLogger(SourceCsvReader.class);
	protected List<Text> texts = new ArrayList<>();

	/**
	 * Reads data from a CSV file and creates a Text object
	 * @param fileName, the name of the file to read
	 * @return Text
	 */
	@Override
	public List<Text> readData(String fileName) {
		// change the below file name
		List<String[]> records = getCsvRecords(fileName);
		if (!CollectionUtils.isEmpty(records)) {
			texts = new ArrayList<>();
			records.forEach(record -> {
				Text text = createTextFromLine(record);
				if (text != null) {
					texts.add(text);
				}
			});
			TextExporter textExporter = new TextExporter();
			textExporter.exportDataToDatabase(texts);
		}
		return texts;
	}

	public Text createTextFromLine(String[] record) {
		logger.info("Readin text with body " + record[6] + " and label " + record[5]);
		if (record[5] != null && record[6] != null) {
			Text text = new Text();
			text.setDataset(1);
			String message = record[6];
			message = message.replaceAll("\n", " ");
			message = message.replaceAll("\"", "");
			text.setBody(message);
			if (record[5].equals("0") || record[5].equals("1")) {
				text.setLabel(Utils.HATE_SPEECH_LABEL);
				if (record[5].equals("0")) {
					text.setOldLabel(Utils.HATE_SPEECH_LABEL);
				} else {
					text.setOldLabel(Utils.OFFENSIVE_LANGUAGE_LABEL);
				}
			} else if (record[5].equals("2")) {
				text.setLabel(Utils.CLEAN_LABEL);
				text.setOldLabel(Utils.CLEAN_LABEL);
			}
			return text;
		}
		return null;
	}

	public List<Text> getTexts() {
		return texts;
	}

	public void setTexts(List<Text> texts) {
		this.texts = texts;
	}

}
