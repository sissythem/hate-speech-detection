package gr.di.hatespeech.utils;

public class Logger {

	org.apache.log4j.Logger logger;

	public Logger(org.apache.log4j.Logger logger) {
		this.logger = logger;
	}

	public void error(String msg, Exception e) {
		logger.error(msg);
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void error(String msg) {
		logger.error(msg);
	}

	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

	public void error(StackTraceElement[] stackTrace) {
		logger.error(stackTrace);
	}

	public void warn(String msg) {
		logger.warn(msg);
	}
	
	public void debug(String msg) {
		logger.debug(msg);
	}
}
