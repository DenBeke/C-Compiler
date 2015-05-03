package Compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class SemanticsTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public SemanticsTest(String testName) {
		super(testName);
		Log.debug = false;
		Log.exception = true;
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(SemanticsTest.class);
	}

	/**
	 * Test for functions with correct returns
	 */
	public void testCorrectReturns() {

		Log.debug("testCorrectReturns");

		try {

			try {
				InputStream is = new FileInputStream(
						"src/test/input/semantics/return1_ok.c");
				ANTLRInputStream input = new ANTLRInputStream(is);
				CLexer lexer = new CLexer(input);
				AstParser parser = new AstParser(new CommonTokenStream(lexer));
				Ast.Node root = parser.buildAst();

				Visitor visitor = new SymbolTableVisitor();
				visitor.visit(root);
				visitor = new SemanticVisitor();
				visitor.visit(root);

				// OK if no exception was thrown
				assertTrue(true);

				is = new FileInputStream(
						"src/test/input/semantics/return2_ok.c");
				input = new ANTLRInputStream(is);
				lexer = new CLexer(input);
				parser = new AstParser(new CommonTokenStream(lexer));
				root = parser.buildAst();

				visitor = new SymbolTableVisitor();
				visitor.visit(root);
				visitor = new SemanticVisitor();
				visitor.visit(root);

				// OK if no exception was thrown
				assertTrue(true);
			} catch(Log.FatalException e) {
				System.out.println(e.toString());
				assertTrue(false);
			}

		} catch(FileNotFoundException e) {
			fail("Could not load input file");
			return;
		} catch(IOException e) {
			fail("Could not load input file");
			return;
		}

	}

	/**
	 * Test for functions with incorrect returns
	 */
	public void testInCorrectReturns() {

		Log.debug("testInCorrectReturns");

		try {

			try {
				InputStream is = new FileInputStream(
						"src/test/input/semantics/return1_bad.c");
				ANTLRInputStream input = new ANTLRInputStream(is);
				CLexer lexer = new CLexer(input);
				AstParser parser = new AstParser(new CommonTokenStream(lexer));
				Ast.Node root = parser.buildAst();

				Visitor visitor = new SymbolTableVisitor();
				visitor.visit(root);
				visitor = new SemanticVisitor();
				visitor.visit(root);

				// OK if no exception was thrown
				assertTrue(false);

			} catch(Log.FatalException e) {
				// System.out.println(e.toString());
				assertTrue(true);
				assertEquals(
						"12: Control may reach end of non-void function 'a' without return",
						e.getMessage());
			}

		} catch(FileNotFoundException e) {
			fail("Could not load input file");
			return;
		} catch(IOException e) {
			fail("Could not load input file");
			return;
		}

	}

	/**
	 * Test for functions with correct void (i.e. empty) returns
	 */
	public void testCorrectVoidReturns() {

		Log.debug("testCorrectVoidReturns");

		try {

			try {
				InputStream is = new FileInputStream(
						"src/test/input/semantics/return_void_ok.c");
				ANTLRInputStream input = new ANTLRInputStream(is);
				CLexer lexer = new CLexer(input);
				AstParser parser = new AstParser(new CommonTokenStream(lexer));
				Ast.Node root = parser.buildAst();

				Visitor visitor = new SymbolTableVisitor();
				visitor.visit(root);
				visitor = new SemanticVisitor();
				visitor.visit(root);

				// OK if no exception was thrown
				assertTrue(true);
			} catch(Log.FatalException e) {
				System.out.println(e.toString());
				assertTrue(false);
			}

		} catch(FileNotFoundException e) {
			fail("Could not load input file");
			return;
		} catch(IOException e) {
			fail("Could not load input file");
			return;
		}

	}

	/**
	 * Test for functions with incorrect void returns
	 */
	public void testInCorrectVoidReturns() {

		Log.debug("testInCorrectVoidReturns");

		try {

			try {
				InputStream is = new FileInputStream(
						"src/test/input/semantics/return_void_bad.c");
				ANTLRInputStream input = new ANTLRInputStream(is);
				CLexer lexer = new CLexer(input);
				AstParser parser = new AstParser(new CommonTokenStream(lexer));
				Ast.Node root = parser.buildAst();

				Visitor visitor = new SymbolTableVisitor();
				visitor.visit(root);
				visitor = new SemanticVisitor();
				visitor.visit(root);

				// OK if no exception was thrown
				assertTrue(false);

			} catch(Log.FatalException e) {
				// System.out.println(e.toString());
				assertTrue(true);
				assertEquals("9: Empty return in non-void function",
						e.getMessage());
			}

		} catch(FileNotFoundException e) {
			fail("Could not load input file");
			return;
		} catch(IOException e) {
			fail("Could not load input file");
			return;
		}

	}

}