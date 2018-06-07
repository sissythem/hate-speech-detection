package gr.di.hatespeech.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader implementation for txt files
 * @author sissy
 */
public class TxtReader implements Reader<List<String>> {
	protected List<String> txtLines;

	public TxtReader() {
		super();
		this.txtLines = new ArrayList<>();
	}

	/**
	 * Reads data from a txt file and returns a list with 
	 * the lines of the file
	 * @param fileName, a String object with the name of the file
	 * @return txtLines, is a List<String> with the lines of a txt file
	 */
	@Override
	public List<String> readData(String fileName) {
		try {
			BufferedReader in = getBufferedReader(fileName);
			return readLines(in);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> readLines(BufferedReader in) throws IOException {
		String line = null;
		while ((line = in.readLine()) != null) {
			txtLines.add(line);
		}
		return txtLines;
	}

	public BufferedReader getBufferedReader(String fileName) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classloader.getResourceAsStream(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		return in;
	}

}
