package gr.di.hatespeech.parsers;

public interface Parser<T> {
	
	T parseData(String sourceFile);

}
