package gr.di.hatespeech.dataexporters;

import com.opencsv.CSVWriter;

/**
 * This class represents an object with options for csv writing based on the
 * options required in OpenCsv library for CSVWriter
 * @author sissy
 */
public class CsvOptions {
	protected char separator;
	protected char quoteCharacter;
	protected char escapeCharacter;
	protected String lineEnd;

	/**
	 * Default constructor setting the default options in a similar way as in
	 * OpenCsv library
	 */
	public CsvOptions() {
		this.separator = CSVWriter.DEFAULT_SEPARATOR;
		this.quoteCharacter = CSVWriter.NO_QUOTE_CHARACTER;
		this.escapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		this.lineEnd = CSVWriter.DEFAULT_LINE_END;
	}

	/**
	 * Constructor for providing the required options in order to export data to a
	 * csv file
	 * @param separator
	 * @param quoteCharacter
	 * @param escapeCharacter
	 * @param lineEnd
	 */
	public CsvOptions(char separator, char quoteCharacter, char escapeCharacter, String lineEnd) {
		super();
		this.separator = separator;
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		this.lineEnd = lineEnd;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	public char getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setEscapeCharacter(char escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public String getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

}
