package si.vv.pokebola.compiladoido;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LexicoTest {

	Compiladoido compiladoido;
	private Logger logger;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		String[] args;
		args = new String[0];
		
		Compiladoido compiladoido = Compiladoido.getInstance();
		compiladoido.instanceMain(args);
		
		logger = compiladoido.getLogger(LexicoTest.class);
		logger.config("End setUp()");
	}

	@Test
	public void basicProgramEndTest() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Token actualToken;
		Simbolo actualSimbolo, expectedSimbolo;
		
		stringBuffer = new StringBuffer("program end.");
		lexico = new Lexico(stringBuffer);
		
		// program part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSimbolo();
		expectedSimbolo = Simbolo.PROGRAM;
		assertEquals(expectedSimbolo, actualSimbolo);
		
		// end part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSimbolo();
		expectedSimbolo = Simbolo.END;
		assertEquals(expectedSimbolo, actualSimbolo);

		// . part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSimbolo();
		expectedSimbolo = Simbolo.POINT;
		assertEquals(expectedSimbolo, actualSimbolo);
		
	}

}
