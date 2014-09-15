package si.vv.pokebola.compiladoido;

import org.junit.Before;
import org.junit.Test;

public class SintaticoTest {
	
	@Before
	public void setUp() throws Exception {
		String[] args;
		args = new String[0];

		Compiladoido compiladoido = Compiladoido.getInstance();
		compiladoido.instanceMain(args);
	}
	
	@Test
	public void testParseProgramaBasico() {
		Lexico lexico;
		StringBuffer stringBuffer;
		Sintatico sintatico;
		
		stringBuffer = AllTests.getMinimalProg();
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		sintatico.parse();
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
