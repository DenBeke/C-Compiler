package Compiler;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class CastTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public CastTest(String testName) {
		super(testName);
		Log.debug = false;
		Log.exception = true;
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CastTest.class);
	}

	private String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	/**
	 * Test for some correct symbols
	 */
	public void testCasts() {
		Log.debug("testCasts");

		String[] files = {"good1.c", "good2.c", "bad1.c", "bad2.c"};

		/*for(int i = 0; i < files.length; i++) {
			String expected = "";
			try {
				expected = readFile("src/test/output/casts/" + files[i]);

				InputStream is = new FileInputStream(
						"src/test/input/casts/" + files[i]);
				ANTLRInputStream input = new ANTLRInputStream(is);
				CLexer lexer = new CLexer(input);
				AstParser parser = new AstParser(new CommonTokenStream(lexer));
				Ast.Node root = parser.buildAst();

				Visitor visitor = new SymbolTableVisitor();
				visitor.visit(root);

				if(!root.toString().equals(expected)) {
					assertTrue(false);
				}
			} catch(Exception e) {
				if(i == 2 || i == 3) {
					assertTrue(e.getMessage().equals(expected.substring(0, expected.length() - 1)));
				} else {
					System.out.println(e.getMessage());
					assertTrue(false);
				}
			}
		}
		*/
	}

}
