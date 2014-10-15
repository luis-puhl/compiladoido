package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;

public class PascalSemanticActionsTest {
	
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		Compiladoido.getInstance();
		logger = LogManager.getLogger();
		logger.info("Testing Pascal Semantic Actions");
	}
	
	@Test
	public void testSemWikiProgramProcedure() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getWikiProcedure();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();
		
		String result = parsedTree.printTreeSemantic();
		
		logger.debug("test Semantic WikiProcedure got:\n" + result);
		
		String expected = CompiladoidoTests.getWikiProcedureSem();
		
		logger.debug("test Semantic WikiProcedure expect:\n" + expected);
		
		assertEquals(expected, result);
	}
}
