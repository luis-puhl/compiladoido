package si.vv.pokebola.compiladoido;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.Symbol;

public class AutomatoException extends Exception{
	/**
	 * 
	 */
	private  Logger logger;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5688025380424980486L;

	private Level level;
	private StringBuilder msg;
	private boolean optional;
	
	private void initMessage(Logger logger, boolean optional){
		this.logger = logger;
		this.level = Level.WARNING;
		this.msg = new StringBuilder();
		this.optional = optional;
	}
	
	private void finalizeMessage(){
		if (optional){
			level = Level.FINE;
			msg.append(" was not used."); 
		} else {
			msg.append(" EXPECTED.");
		}
		
		StackTraceElement cause = this.getStackTrace()[2];
		msg.append("\n\t at ");
		msg.append(cause.getClassName());
		msg.append(".");
		msg.append(cause.getMethodName());
		msg.append("(");
		msg.append(cause.getFileName());
		msg.append(":");
		msg.append(cause.getLineNumber());
		msg.append(")");
	}
	
	public AutomatoException( Logger logger, Symbol expected, boolean optional) {
		this.initMessage(logger, optional);
		this.msg.append(expected.getName());
		this.finalizeMessage();
	}
	
	public AutomatoException( Logger logger, Collection<? extends Symbol> expected, boolean optional) {
		this.initMessage(logger, optional);
		for (Symbol symbol : expected) {
			msg.append(symbol.getName());
			msg.append(", ");
		}
		this.finalizeMessage();
	}
	
	public AutomatoException( Logger logger, Symbol expected) {
		this(logger, expected, false);
	}

	public AutomatoException( Logger logger, Collection<? extends Symbol> expected) {
		this(logger, expected, false);
	}
	
	public void log(){
		logger.log(level, msg.toString());
	}
	
}