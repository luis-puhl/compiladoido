package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class LexicalAutomataTest {

	private Logger logger;

	@Before
	public void setUp() throws Exception {
		Compiladoido.getInstance();
		logger = LogManager.getLogger();
		logger.info("Testing LexicalAutomata");
	}

	@Test
	public void minimalProg() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		Token actualToken;
		Symbol actualSimbolo;
		Symbol expectedSimbolo;

		stringBuffer = CompiladoidoTests.getMinimalProg();
		lexico = new LexicalAutomata(stringBuffer);

		/*
		 * program minimalProg; begin end.
		 */
		// program part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.PROGRAM;
		assertEquals(expectedSimbolo, actualSimbolo);

		// p part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.ID;
		assertEquals(expectedSimbolo, actualSimbolo);

		// ; part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.SEMICOLON;
		assertEquals(expectedSimbolo, actualSimbolo);

		// begin part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.BEGIN;
		assertEquals(expectedSimbolo, actualSimbolo);

		// end part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.END;
		assertEquals(expectedSimbolo, actualSimbolo);

		// . part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.PERIOD;
		assertEquals(expectedSimbolo, actualSimbolo);

	}

	@Test(expected = NullPointerException.class)
	public void bufferForLexicoShouldNotBeNull() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;

		stringBuffer = null;
		lexico = new LexicalAutomata(stringBuffer);

		assertEquals(WordSymbols.PROGRAM, lexico.getToken().getSymbol());
	}

	@Test
	public void helloWorldTest() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;

		stringBuffer = CompiladoidoTests.getHelloWorld();

		lexico = new LexicalAutomata(stringBuffer);

		String result = lexico.getTokenList().toString();

		logger.debug("Lexico for helloWorld got:\n" + result);

		String expected = CompiladoidoTests.getHelloWorldLex();

		assertEquals(expected.trim(), result.trim());
	}

	@Test
	public void wikiProgramProcedure() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;

		stringBuffer = CompiladoidoTests.getWikiProcedure();

		lexico = new LexicalAutomata(stringBuffer);

		String result = lexico.getTokenList().toString();

		logger.debug("Lexico for wikiProcedure got:\n" + result);

		String expected = CompiladoidoTests.getWikiProcedureLex();

		assertEquals(expected.trim(), result.trim());
	}

}
