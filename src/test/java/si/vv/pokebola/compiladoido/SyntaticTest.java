package si.vv.pokebola.compiladoido;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;

public class SyntaticTest {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Before
	public void setUp() throws Exception {
		Compiladoido.getInstance();
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
		
		LOGGER.debug(parsedTree.printSubTree());
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
		
		LOGGER.debug(parsedTree.printSubTree());
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
		
		LOGGER.debug(parsedTree.printSubTree());
	}
	
}
