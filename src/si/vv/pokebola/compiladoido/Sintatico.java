package si.vv.pokebola.compiladoido;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.Token;

public class Sintatico {
	
	private final static String LOG_LEVEL_PROP = Sintatico.class.getCanonicalName()+".logLevel";
	private Logger logger;
	
	private Lexico lexico;
	
	public Sintatico(Lexico lexico) throws IOException {
		initLogger();
		this.lexico = lexico;
	}
	
	private void initLogger(){
		logger = Logger.getLogger(Sintatico.class.getName());
		String logLevel = Compiladoido.getInstance().getProperties().getProperty(LOG_LEVEL_PROP); 
		logger.setLevel(Level.parse(logLevel));		
	}
	
	public void parse(){
		
	}
	
	public List<Token> getExpression(){
		return null;
	}
	
}
