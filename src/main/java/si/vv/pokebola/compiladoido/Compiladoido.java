package si.vv.pokebola.compiladoido;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Compiladoido {

	private static final String CONFIG_FILE_PATH = "config.properties";
	private static final String SOURCE_FILE_PROP = "source_file";

	private static Logger logger;

	private Properties properties;

	private LexicalAutomata lexico;
	private String sourceFileName;

	private static Compiladoido instance;

	private Compiladoido() {
		this(null);
	}

	public Compiladoido(String[] args) {
		System.out.println("args");
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
			}
		}
		System.out.println(">>>args");

		System.out.println("geting config");
		try {
			InputStream inStream;
			File propsFile;

			propsFile = new File(CONFIG_FILE_PATH);
			inStream = new FileInputStream(propsFile);

			Properties configProperties = new Properties();
			configProperties.load(inStream);
			// / implementação de EL para properties
			/*
			for (Entry<Object, Object> prop : configProperties.entrySet()) {
				if (prop.getValue().toString().matches(".*\\Q${\\E.*\\}.*")) {

				}
			}
			*/

			properties = new Properties(System.getProperties());
			properties.putAll(configProperties);

			System.setProperties(properties);

			// display new properties
			System.getProperties().list(System.out);
			
		}catch (IOException ioException) {
			System.err.println("config not read");
		}

		sourceFileName = properties.getProperty(SOURCE_FILE_PROP);

		System.out.println("testing logger");
		logger = LogManager.getLogger();
		logger.debug("Logger created");
		System.out.println("end logger setup");
	}

	public static void main(String[] args) {
		instance = new Compiladoido(args);
	}

	public Properties getProperties() {
		return properties;
	}

	public LexicalAutomata getLexico() {
		if (lexico == null) {
			try {
				logger.info("Loading LexicalAutomata with " + sourceFileName);
				lexico = new LexicalAutomata(sourceFileName);
			} catch (IOException ioException) {
				logger.error("trouble open source file", ioException);
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

}
