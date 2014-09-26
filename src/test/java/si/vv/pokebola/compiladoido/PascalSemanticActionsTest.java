package si.vv.pokebola.compiladoido;

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
		logger.info("Testing SyntacticWarper");
	}

	@Test
	public void testVarDeclarations() {
		LexicalAutomata lexico;
		StringBuffer stringBuffer;
		SyntacticWarper sintatico;

		stringBuffer = CompiladoidoTests.getWikiProgramProcedure();
		lexico = new LexicalAutomata(stringBuffer);

		sintatico = new SyntacticWarper(lexico);

		SyntaticTreeNode parsedTree = sintatico.parse();

		logger.debug(parsedTree.printTree());
		
	}
}
