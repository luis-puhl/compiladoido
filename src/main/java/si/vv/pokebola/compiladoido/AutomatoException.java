package si.vv.pokebola.compiladoido;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.Symbol;

public class AutomatoException extends Exception {
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

	private void initMessage(Logger logger) {
		this.logger = logger;
		this.level = Level.INFO;
		this.msg = new StringBuilder();
	}

	private void errorSource() {
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

	public AutomatoException(Logger logger, Symbol expected, boolean optional, Symbol got ) {
		this.initMessage(logger);
		this.msg.append(expected.getName());
		if (optional) {
			level = Level.FINE;
			msg.append(" was not used.");
		} else {
			msg.append(" EXPECTED.");
		}

		msg.append("Got ");
		msg.append(got.toString());

		errorSource();
	}

	public AutomatoException(Logger logger, Collection<? extends Symbol> expected,
			boolean optional, Symbol got) {
		this.initMessage(logger);
		for (Symbol symbol : expected) {
			msg.append(symbol.getName());
			msg.append(", ");
		}

		if (optional) {
			level = Level.FINE;
			msg.append(" was not used.");
		} else {
			msg.append(" EXPECTED.");
		}

		msg.append("Got ");
		msg.append(got.toString());

		errorSource();
	}

	public AutomatoException(Logger logger, Symbol expected, Symbol got) {
		this(logger, expected, false, got);
	}

	public AutomatoException(Logger logger, Collection<? extends Symbol> expected, Symbol got) {
		this(logger, expected, false, got);
	}

	public AutomatoException(Logger logger) {
		this.initMessage(logger);
		msg.append("Unimplemented feature");
		this.errorSource();
	}

	public void log() {
		logger.log(level, msg.toString());
	}

}