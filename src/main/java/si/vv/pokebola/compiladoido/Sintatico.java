package si.vv.pokebola.compiladoido;

import java.util.List;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.Token;

public class Sintatico {
	
	private final static String LOG_LEVEL_PROP = Sintatico.class.getCanonicalName()+".logLevel";
	private Logger logger;
	
	private Lexico lexico;
	
	public Sintatico(Lexico lexico){
		initLogger();
		this.lexico = lexico;
	}
	
	private void initLogger(){
		Compiladoido compiladoido = Compiladoido.getInstance();
		
		logger = compiladoido.getLogger(Lexico.class, LOG_LEVEL_PROP);	
	}
	
	public void parse(){
		AutomatoSintaticoPascal automato = new AutomatoSintaticoPascal(this, lexico);
		try {
			automato.run();
		} catch (AutomatoException e){
			e.log();
			logger.severe("Automato didn't run fine :( ");
			throw new RuntimeException(e);
		}
	}
	
	public List<Token> getExpression(){
		return null;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
}
