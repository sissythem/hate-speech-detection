package gr.di.hatespeech.dataexporters;

import java.util.List;

/**
 * Interface representing a data exporter
 * Data can be exported in two ways: in csv format
 * or stored in a database
 * @author sissy
 * @param <T>
 */
public interface DataExporter<T> {
	void exportDataToCsv(List<T> data, String[] headerRecord, String fileName, CsvOptions options);
	void exportDataToDatabase(List<T> data);
}
