package gr.di.hatespeech.readers;

/**
 * Interface represeting a reader in order to retrieve data
 * either from database or from a file (csv,txt etc)
 * @author sissy
 */
public interface Reader<T> {
	T readData(String source);
}
