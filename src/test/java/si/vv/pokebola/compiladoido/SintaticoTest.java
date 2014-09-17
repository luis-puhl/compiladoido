package si.vv.pokebola.compiladoido;

import java.util.Properties;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;

public class SintaticoTest {
	
	@Before
	public void setUp() throws Exception {
		String[] args;
		args = new String[0];

		Compiladoido compiladoido = Compiladoido.getInstance();
		compiladoido.instanceMain(args);
		Properties properties = compiladoido.getProperties();
		properties.remove(Lexico.LOG_LEVEL_PROP);
		properties.put(Lexico.LOG_LEVEL_PROP, Level.OFF);
	}
	
	@Test
	public void testParseProgramaBasico() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Sintatico sintatico;
		
		stringBuffer = AllTests.getMinimalProg();
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		SyntaticTreeNode parsedTree = sintatico.parse();
		
		sintatico.getLogger().fine(parsedTree.printSubTree());
	}

	@Test
	public void testParseHelloWorld() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Sintatico sintatico;
		
		stringBuffer = AllTests.getHelloWorld();
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		SyntaticTreeNode parsedTree = sintatico.parse();
		
		sintatico.getLogger().fine(parsedTree.printSubTree());
	}
	
	@Test
	public void testParseWikiProgramProcedure() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Sintatico sintatico;
		
		stringBuffer = AllTests.getWikiProgramProcedure();
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		SyntaticTreeNode parsedTree = sintatico.parse();
		
		sintatico.getLogger().fine(parsedTree.printSubTree());
	}
	
}
