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
		
		logger.debug(parsedTree.printTreeTextToken());

		String expected = ""
				 + "run"
				 + "\n	PROGRAM"
				 + "\n		PROGRAM_HEADER"
				 + "\n			Program"
				 + "\n			HelloWorld"
				 + "\n		;"
				 + "\n		BLOCK"
				 + "\n			DECLARATION_PART"
				 + "\n				VAR_DECLARATION_PART"
				 + "\n				PROCEDURE_FUNCTION_DECLARATION_PART"
				 + "\n			COMPOUND_STATEMENT"
				 + "\n				Begin"
				 + "\n				statement"
				 + "\n					simpleStatement"
				 + "\n						FUNCTION_CALL"
				 + "\n							WriteLn"
				 + "\n							actualParameterList"
				 + "\n								("
				 + "\n								EXPRESSION"
				 + "\n									SIMPLE_EXPRESSION"
				 + "\n										TERM"
				 + "\n											FACTOR"
				 + "\n												'Hello world!'"
				 + "\n								)"
				 + "\n				End"
				 + "\n		."
				 ;
		
		assertEquals(expected, parsedTree.printTreeTextToken());
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
	
	@Test
	public void testVarDeclarations() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getVarDeclarations();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();

		logger.debug(parsedTree.printTreeTextToken());
	}

}
