package si.vv.pokebola.compiladoido.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

	@Override
	public void publish(LogRecord record) {
		String string;
		string = this.getFormatter().format(record);
		if (record.getLevel().intValue() > Level.INFO.intValue()){
			System.err.print(string);
		} else {
			System.out.print(string);
		}
	}

	@Override
	public void flush() {
		System.out.flush();
		System.err.flush();
	}

	@Override
	public void close() throws SecurityException {}

}
