package si.vv.pokebola.compiladoido;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private void logConfigPhase(String phaseName){
		System.out.println("\t <<<<" + phaseName);
		System.out.flush();
	}
	private void logEndConfigPhase(String phaseName){
		System.out.println("\t >>>> " + phaseName + "\n");
	}
	
	public Compiladoido(String[] args) {
		
		logConfigPhase("args");
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
			}
		}
		logEndConfigPhase("args");
		
		try {
			InputStream inStream;
			File propsFile;

			propsFile = new File(CONFIG_FILE_PATH);
			inStream = new FileInputStream(propsFile);

			Properties configProperties = new Properties();
			configProperties.load(inStream);

			// / implementação de EL para properties
			Pattern pattern = Pattern.compile("(?i)\\$\\{(.+?)\\}");

			for (Entry<Object, Object> prop : configProperties.entrySet()) {
				String propString = prop.getValue().toString();

				Matcher matcher = pattern.matcher(propString);
				while (matcher.find()) {
					// Extract the text between the two title elements
					String keyString = matcher.group(1);

					propString = (String) System.getProperties().get(keyString.trim());

					propString = prop.getValue().toString()
							.replaceAll("\\$\\{" + keyString + "\\}", propString);
					prop.setValue(propString);
				}
				
				configProperties.put(prop.getKey(), propString);
			}

			properties = new Properties(System.getProperties());
			properties.putAll(configProperties);

			System.setProperties(properties);
			
			// display new properties
			
			logConfigPhase("System.properties");
			System.getProperties().list(System.out);
			logEndConfigPhase("System.properties");

		} catch (IOException ioException) {
			System.err.println("config not read");
		}

		sourceFileName = properties.getProperty(SOURCE_FILE_PROP);

		logConfigPhase("logger setup");
		logger = LogManager.getLogger();
		logger.debug("Logger created");
		logEndConfigPhase("logger setup");
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
