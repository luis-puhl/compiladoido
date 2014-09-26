package si.vv.pokebola.compiladoido;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ LexicalAutomataTest.class, SyntaticTest.class, PascalSemanticActionsTest.class })
public class CompiladoidoTests {

	private static final String TEST_TEST_EXAMPLES_DIR = "test.testExamplesDir";

	private static StringBuffer getFileExample(String fileName) {
		String exampleDir;
		Scanner inputScanner;
		StringBuffer fileStringBuffer = null;

		exampleDir = Compiladoido.getInstance().getProperties().getProperty(TEST_TEST_EXAMPLES_DIR);
		fileName = exampleDir + fileName;

		Path path = Paths.get(fileName);
		try {
			inputScanner = new Scanner(path, StandardCharsets.UTF_8.name());

			fileStringBuffer = new StringBuffer();
			while (inputScanner.hasNext()) {
				fileStringBuffer.append(inputScanner.next());
				fileStringBuffer.append(" ");
			}

			inputScanner.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return fileStringBuffer;
	}

	/*
	 * 
	 */

	public static StringBuffer getMinimalProg() {
		return getFileExample("minimalProg.pas");
	}

	public static String getMinimalSintaticTree() {
		return getFileExample("minimalProg.pas.sint").toString();
	}

	/*
	 * 
	 */

	public static StringBuffer getHelloWorld() {
		return getFileExample("helloWorld.pas");
	}

	/*
	 * 
	 */

	public static StringBuffer getWikiProgramProcedure() {
		return getFileExample("wikiProcedure.pas");
	}

}
