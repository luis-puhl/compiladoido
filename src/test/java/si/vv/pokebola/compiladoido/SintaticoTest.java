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
		
		stringBuffer = new StringBuffer("  program p; end. ");
		lexico = new Lexico(stringBuffer);
		
		sintatico = new Sintatico(lexico);
		
		sintatico.parse();

		/*
		assertEquals(WordSymbols.PROGRAM, lexico.getToken().getSymbol());
		assertEquals(WordSymbols.END, lexico.getToken().getSymbol());
		assertEquals(OperatorSymbols.POINT, lexico.getToken().getSymbol());
		
		assertEquals(null, lexico.getToken());
		*/
	}

}
