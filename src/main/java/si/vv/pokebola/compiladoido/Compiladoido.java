package si.vv.pokebola.compiladoido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.util.LogFormatter;
import si.vv.pokebola.compiladoido.util.LogHandler;

public class Compiladoido {

	private static final String CONFIG_FILE_PATH = "config.properties";
	private static final String SOURCE_FILE_PROP = "source_file";
	
	private static final String ERRORS_DIR_PROP = "errors_dir";
	private static String errorsDirPath;
	
	private FileHandler errorFileHandler;

	private Logger logger;
	private Properties properties;

	private Lexico lexico;
	private String sourceFileName;

	private static Compiladoido instance;

	private Compiladoido() {
		System.out.println("Init logger");
		logger = Logger.getLogger(Compiladoido.class.getName());
		logger.setLevel(Level.ALL);
		
		Handler[] handlers = logger.getHandlers();
		System.out.println("setting " + handlers.length + " logger handlers");
		if (handlers.length == 0) {
			Handler handler = new LogHandler();
			handler.setFormatter(new LogFormatter());
			logger.addHandler(handler);
		} else {
			for (Handler handler : handlers) {
				if (handler instanceof ConsoleHandler) {
					handler.setFormatter(new LogFormatter());
				}
			}
		}
		System.out.println("testing logger");
		logger.config("Logger created");

		System.out.println("end logger setup");
	}

	public void instanceMain(String[] args) {
		logger.config("args");
		for (int i = 0; i < args.length; i++) {
			logger.config(args[i]);
		}
		logger.config(">>>args");

		logger.fine("geting config");
		try {
			InputStream inStream;
			File propsFile;

			propsFile = new File(CONFIG_FILE_PATH);
			inStream = new FileInputStream(propsFile);

			properties = new Properties();
			properties.load(inStream);
		} catch (FileNotFoundException fileNotFoundException) {
			logger.severe("config not found");
		} catch (IOException ioException) {
			logger.warning("config not found");
		}

		sourceFileName = properties.getProperty(SOURCE_FILE_PROP);
		errorsDirPath = properties.getProperty(ERRORS_DIR_PROP);
		
		logger.fine("ending");
	}
	
	private FileHandler getErrorFileHandler() {
		if (errorFileHandler == null){
			try {
				// This block configure the logger with handler and formatter
				final File file = new File(errorsDirPath + File.separator + "compiladoido.log");
				if (file.exists()) {
					file.delete();
				}
	
				errorFileHandler = new FileHandler(file.getPath(), true);
				errorFileHandler.setFormatter(new LogFormatter());
			} catch (final SecurityException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return errorFileHandler;
	}

	public static void main(String[] args) {
		Compiladoido compiladoido = new Compiladoido();
		compiladoido.instanceMain(args);
	}

	public Properties getProperties() {
		return properties;
	}

	public Lexico getLexico() {
		if (lexico == null) {
			try {
				logger.fine("geting source code");
				lexico = new Lexico(sourceFileName);
			} catch (IOException ioException) {
				logger.log(Level.WARNING, "trouble open source file",
						ioException);
			}
		}
		return lexico;
	}

	public static Compiladoido getInstance() {
		if (instance == null) {
			instance = new Compiladoido();
		}
		return instance;
	}

	public Logger getLogger(Class<?> klass, String logLevelProp) {
		Logger logger;

		this.logger.config("Setting up logger for " + klass.getName());

		logger = Logger.getLogger(klass.getName());

		logger.setLevel(Level.ALL);
		try {
			if (logLevelProp != null){
				String logLevel = this.getProperties().get(logLevelProp).toString();
				if (logLevel != null) {
					logger.setLevel(Level.parse(logLevel));
				}
			}
		} catch (Exception exception) {
		}
		
		logger.addHandler(getErrorFileHandler());
		Handler handler = new LogHandler();
		handler.setFormatter(new LogFormatter());
		logger.addHandler(handler);

		Handler[] handlers = logger.getHandlers();
		for (Handler handler1 : handlers) {
			if (handler1 instanceof ConsoleHandler) {
				handler1.setFormatter(new LogFormatter());
			}
		}
		
		return logger;
	}

	public Logger getLogger(Class<?> klass) {
		return this.getLogger(klass, null);
	}

}
