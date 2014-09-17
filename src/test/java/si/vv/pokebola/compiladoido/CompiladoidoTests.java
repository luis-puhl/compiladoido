package si.vv.pokebola.compiladoido;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ LexicalAutomataTest.class, SyntaticTest.class })
public class CompiladoidoTests {
	
	public static StringBuffer getMinimalProg() {
		StringBuffer minimalProg;
		minimalProg = new StringBuffer("  program p; begin READ(x) end. ");
		return minimalProg;
	}

	public static StringBuffer getHelloWorld() {
		StringBuffer helloWord;
		helloWord = new StringBuffer();
		helloWord.append("Program HelloWorld;\n");
		helloWord.append("Begin\n");
		helloWord.append("WriteLn('Hello world!')\n");
		helloWord.append("{no \";\" is required after the last statement of a block -\n");
		helloWord.append("adding one adds a \"null statement\" to the program}\n");
		helloWord.append("End.");
		return helloWord;
	}
	
	public static StringBuffer getWikiProgramProcedure() {
		StringBuffer wikiProgramProcedure;
		wikiProgramProcedure = new StringBuffer();
		wikiProgramProcedure.append("program Mine();\n");
		wikiProgramProcedure.append("\n");
		wikiProgramProcedure.append("var i : integer;\n");
		wikiProgramProcedure.append("		 \n");
		wikiProgramProcedure.append("procedure Print(var j : integer);\n");
		wikiProgramProcedure.append("begin\n");
		wikiProgramProcedure.append("end;\n");
		wikiProgramProcedure.append("		 \n");
		wikiProgramProcedure.append("begin\n");
		wikiProgramProcedure.append("Print(i);\n");
		wikiProgramProcedure.append("end.\n");
		return wikiProgramProcedure;
	}
}
