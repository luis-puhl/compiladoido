package si.vv.pokebola.compiladoido;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class SintaticoTest {
	
	@Before
	public void setUp() throws Exception {
		String[] args;
		args = new String[0];

		Compiladoido compiladoido = Compiladoido.getInstance();
		compiladoido.instanceMain(args);
		Properties properties = compiladoido.getProperties();
		properties.remove(Lexico.LOG_LEVEL_PROP);
		properties.put(Lexico.LOG_LEVEL_PROP, Level.CONFIG);
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
		
		List<SyntaticTreeNode> children = parsedTree.getChildren();
		SyntaticTreeNode programNode = children.get(0);
		Symbol symbol = programNode.getLexicToken().getSymbol();
		assertEquals(WordSymbols.PROGRAM, symbol);
		
	}

	@Test
	public void testParseHelloWorld() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Sintatico sintatico;
		
		stringBuffer = AllTests.getHelloWorld();
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		sintatico.parse();
	}
	
}
