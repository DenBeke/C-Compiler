package Compiler;

import java.io.FileInputStream;
import java.io.InputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.antlr.v4.runtime.*;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
		Log.debug = false;
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testLexer() {
		try {
			InputStream is = new FileInputStream("src/test/input/good1.c");
			ANTLRInputStream input = new ANTLRInputStream(is);
			CLexer lexer = new CLexer(input);
			CParser parser = new CParser(new CommonTokenStream(lexer));
			// Remove output messages
			lexer.removeErrorListeners();
			parser.removeErrorListeners();
			parser.file();
			assertTrue(parser.getNumberOfSyntaxErrors() == 0);

			is = new FileInputStream("src/test/input/bad1.c");
			input = new ANTLRInputStream(is);
			lexer = new CLexer(input);
			parser = new CParser(new CommonTokenStream(lexer));
			// Remove output messages
			lexer.removeErrorListeners();
			parser.removeErrorListeners();
			parser.file();
			assertTrue(parser.getNumberOfSyntaxErrors() != 0);

			is = new FileInputStream("src/test/input/bad2.c");
			input = new ANTLRInputStream(is);
			lexer = new CLexer(input);
			parser = new CParser(new CommonTokenStream(lexer));
			// Remove output messages
			lexer.removeErrorListeners();
			parser.removeErrorListeners();
			parser.file();
			assertTrue(parser.getNumberOfSyntaxErrors() != 0);

			is = new FileInputStream("src/test/input/bad3.c");
			input = new ANTLRInputStream(is);
			lexer = new CLexer(input);
			parser = new CParser(new CommonTokenStream(lexer));
			// Remove output messages
			lexer.removeErrorListeners();
			parser.removeErrorListeners();
			parser.file();
			assertTrue(parser.getNumberOfSyntaxErrors() != 0);

		} catch(java.io.FileNotFoundException e) {
			fail(e.getMessage());
		} catch(java.io.IOException e) {
			fail(e.getMessage());
		}
	}
}
