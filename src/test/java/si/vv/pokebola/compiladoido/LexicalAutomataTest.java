package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class LexicalAutomataTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Compiladoido.getInstance();
	}

	@Test
	public void basicProgramEndTest() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		Token actualToken;
		Symbol actualSimbolo;
		Symbol expectedSimbolo;

		stringBuffer = CompiladoidoTests.getMinimalProg();
		lexico = new LexicalAutomata(stringBuffer);

		// program p; begin READ(x) end.
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

		// READ part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = CommandWordSymbols.READ;
		assertEquals(expectedSimbolo, actualSimbolo);

		// ( part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.OPEN_PARENTHESIS;
		assertEquals(expectedSimbolo, actualSimbolo);

		// x part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.ID;
		assertEquals(expectedSimbolo, actualSimbolo);

		// ) part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.CLOSE_PARENTHESIS;
		assertEquals(expectedSimbolo, actualSimbolo);
				
		// end part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = WordSymbols.END;
		assertEquals(expectedSimbolo, actualSimbolo);

		// . part
		actualToken = lexico.getToken();
		actualSimbolo = actualToken.getSymbol();
		expectedSimbolo = OperatorSymbols.POINT;
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

		assertEquals(WordSymbols.PROGRAM, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.BEGIN, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS, lexico.getToken()
				.getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS.getMirror(), lexico
				.getToken().getSymbol());

		assertEquals(OperatorSymbols.COMMENT, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.END, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.POINT, lexico.getToken().getSymbol());

		assertEquals(null, lexico.getToken());

	}

	@Test
	public void wikiProgramProcedure() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;

		stringBuffer = CompiladoidoTests.getWikiProgramProcedure();

		lexico = new LexicalAutomata(stringBuffer);

		assertEquals(WordSymbols.PROGRAM, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.OPEN_PARENTHESIS, lexico.getToken()
				.getSymbol());
		// removido porque este compilador não suporta programas parametrizados
		// assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS.getMirror(), lexico
				.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.VAR, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.COLON, lexico.getToken().getSymbol());
		assertEquals(TypeWordSymbols.INTEGER, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.PROCEDURE, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS, lexico.getToken()
				.getSymbol());
		assertEquals(WordSymbols.VAR, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.COLON, lexico.getToken().getSymbol());
		assertEquals(TypeWordSymbols.INTEGER, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS.getMirror(), lexico
				.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.BEGIN, lexico.getToken().getSymbol());
		assertEquals(WordSymbols.END, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.BEGIN, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS, lexico.getToken()
				.getSymbol());
		assertEquals(OperatorSymbols.ID, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.OPEN_PARENTHESIS.getMirror(), lexico
				.getToken().getSymbol());

		assertEquals(OperatorSymbols.SEMICOLON, lexico.getToken().getSymbol());

		assertEquals(WordSymbols.END, lexico.getToken().getSymbol());

		assertEquals(OperatorSymbols.POINT, lexico.getToken().getSymbol());

		assertEquals(null, lexico.getToken());

	}

}
