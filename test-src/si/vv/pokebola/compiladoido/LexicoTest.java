package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.Simbolo;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

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
		Symbol actualSimbolo;
		Symbol expectedSimbolo;
		
		stringBuffer = new StringBuffer("program end.");
		lexico = new Lexico(stringBuffer);
		
		// program part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.PROGRAM;
		assertEquals(expectedSimbolo, actualSimbolo);
		
		// end part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.END;
		assertEquals(expectedSimbolo, actualSimbolo);

		// . part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = Simbolo.POINT;
		assertEquals(expectedSimbolo, actualSimbolo);
		
	}
	
	@Test
	public void wikiProgram1Test() {
		Lexico lexico;
		StringBuffer stringBuffer;
		
		stringBuffer = new StringBuffer();
		stringBuffer.append("Program HelloWorld;\n");
		stringBuffer.append("Begin\n");
		stringBuffer.append("WriteLn('Hello world!')\n");
		stringBuffer.append("{no \";\" is required after the last statement of a block -\n");
		stringBuffer.append("adding one adds a \"null statement\" to the program}\n");
		stringBuffer.append("End.");
		
		lexico = new Lexico(stringBuffer);
		
		assertEquals(WordSymbols.PROGRAM, lexico.getToken().getSymbol());
		assertEquals(Simbolo.ID, lexico.getToken().getSymbol());
		assertEquals(Simbolo.SEMICOLON, lexico.getToken().getSymbol());
		assertEquals(WordSymbols.BEGIN, lexico.getToken().getSymbol());
		assertEquals(Simbolo.ID, lexico.getToken().getSymbol());
		assertEquals(Simbolo.OPEN_PARENTHESIS, lexico.getToken().getSymbol());
		assertEquals(Simbolo.QUOTE, lexico.getToken().getSymbol());
		assertEquals(Simbolo.ID, lexico.getToken().getSymbol());
		
		
	}
	

}
