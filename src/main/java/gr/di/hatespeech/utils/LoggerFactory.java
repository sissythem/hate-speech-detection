package gr.di.hatespeech.utils;

/**
 * Factory class to get a Logger instance
 * @author sissy
 */
public class LoggerFactory {
	
	public static Logger getLogger(Class<?> clazz) {
		return new Logger(org.apache.log4j.Logger.getLogger(clazz));
	}
	
	public static Logger getLogger(String value) {
		return new Logger(org.apache.log4j.Logger.getLogger(value));
	}
}

