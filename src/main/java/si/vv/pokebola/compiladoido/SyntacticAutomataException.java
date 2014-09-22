package si.vv.pokebola.compiladoido;

import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.Symbol;

public class SyntacticAutomataException extends Exception {
	/**
	 * 
	 */
	private Logger logger;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5688025380424980486L;

	private Level level;
	private StringBuilder msg;

	private StackTraceElement caller;

	public SyntacticAutomataException(Logger logger, Collection<? extends Symbol> expected,
			Symbol got, StackTraceElement caller) {
		this.initMessage(logger, caller);
		for (Symbol symbol : expected) {
			msg.append(symbol.getName());
			msg.append(", ");
		}

		msg.append(" Expected.");
		msg.append("Got ");
		msg.append(got.toString());

		msg.append(errorSource());
	}

	/**
	 * ONLY represents a UNIMPLEMENTED FEATURE
	 * 
	 * @param logger
	 */
	public SyntacticAutomataException(Logger logger) {
		this.initMessage(logger, null);
		msg.append("Unimplemented feature");
		msg.append(errorSource());
	}

	public void log() {
		logger.log(level, msg.toString());
	}

	private void initMessage(Logger logger, StackTraceElement caller) {
		this.logger = logger;
		this.caller = caller;
		this.level = Level.INFO;
		this.msg = new StringBuilder();
	}

	private String errorSource() {
		if (caller == null){
			return "";
		}
		return "\n\t at " + caller.getClassName() + "." + caller.getMethodName() + "("
				+ caller.getFileName() + ":" + caller.getLineNumber() + ")";
	}

}