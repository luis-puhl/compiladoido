package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;

public class SyntaticTest {

	private Logger logger;

	@Before
	public void setUp() throws Exception {
		Compiladoido.getInstance();
		logger = LogManager.getLogger();
		logger.info("Testing SyntacticWarper");
	}

	@Test
	public void testParseProgramaBasico() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getMinimalProg();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();
		
		logger.debug(parsedTree.printTreeTextToken());
	}

	@Test
	public void testParseHelloWorld() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getHelloWorld();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();
		
		String result = parsedTree.printTreeTextToken();
		
		logger.debug("Parse HelloWorld got:\n" + result);

		String expected = CompiladoidoTests.getHelloWorldSint();
		
		assertEquals(expected.trim(), result.trim());
	}

	@Test
	public void testParseWikiProgramProcedure() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getWikiProgramProcedure();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();
		
		logger.debug(parsedTree.printTreeTextToken());
	}

}
