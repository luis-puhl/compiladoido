package si.vv.pokebola.compiladoido.util;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		if (record.getLevel().intValue() >= Level.INFO.intValue()){
			return highFormat(record);
		}
		
		if (record.getMessage().equals("ENTRY")){
			String message;
			message = "entring " + record.getSourceClassName() + ": " + record.getSourceMethodName();
			record.setMessage(message);
		}
		if (record.getMessage().startsWith("RETURN")){
			String message;
			message = "exiting " + record.getSourceClassName() + ": " + record.getSourceMethodName();
			if (record.getParameters().length >= 1){
				message += ". returning '" + record.getParameters()[0] + "'";
			}
			record.setMessage(message);
		}
		
		return "[" + record.getLoggerName() + "]" +
				record.getLevel() + ": " + record.getMessage()
				+ "\n";
	}
	
	private String highFormat(LogRecord record){
		return "[" + record.getLoggerName() + "]" +
				record.getLevel() + ": " + record.getMessage() +
				" (" + record.getSourceClassName() + " " + record.getSourceMethodName() + ")"
				+ "\n";
	}

}
