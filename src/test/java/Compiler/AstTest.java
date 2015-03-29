package Compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.antlr.v4.runtime.*;

import Compiler.Ast.*;

/**
 * Unit test for ast.
 */
public class AstTest extends TestCase {
	private Node ast1;
	private Node ast2;

	public AstTest(String testName) {
		super(testName);
		Log.debug = false;

		try {
			InputStream is = new FileInputStream(
					"src/test/input/ast/test1_ok.c");
			ANTLRInputStream input = new ANTLRInputStream(is);
			CLexer lexer = new CLexer(input);
			AstParser parser = new AstParser(new CommonTokenStream(lexer));
			ast1 = parser.buildAst();

			is = new FileInputStream("src/test/input/ast/test2_ok.c");
			input = new ANTLRInputStream(is);
			lexer = new CLexer(input);
			parser = new AstParser(new CommonTokenStream(lexer));
			ast2 = parser.buildAst();
		} catch(FileNotFoundException e) {
			fail("Could not load input file");
			return;
		} catch(IOException e) {
			fail("Could not load input file");
			return;
		}
	}

	public static Test suite() {
		return new TestSuite(AstTest.class);
	}

	public void testFile1() {
		Log.debug("testFile1");

		assertTrue("Should have 3 children", ast1.children.size() == 3);
		assertTrue("Should be variable declaration",
				ast1.children.get(0) instanceof Ast.DeclarationNode);
		assertTrue("Should be function declaration",
				ast1.children.get(1) instanceof Ast.FunctionDeclarationNode);
		assertTrue("Should be variable declaration",
				ast1.children.get(2) instanceof Ast.DeclarationNode);
	}

	public void testFunctionDeclaration1() {
		Log.debug("testFunctionDeclaration1");

		assertTrue("Should be function",
				ast1.children.get(1) instanceof Ast.FunctionDeclarationNode);
		Ast.FunctionDeclarationNode func = (Ast.FunctionDeclarationNode) ast1.children
				.get(1);
		assertTrue("Name should be main", func.id.equals(new String("main")));
		assertTrue("Should have formal parameters node",
				func.children.get(1) instanceof Ast.FormalParametersNode);
		assertTrue("Should have no parameters",
				func.children.get(1).children.size() == 0);
		assertTrue("Should block statement node",
				func.children.get(2) instanceof Ast.BlockStatementNode);
		assertTrue("Should have 3 children in block",
				func.children.get(2).children.size() == 3);
	}

	public void testVariableDeclaration1() {
		Log.debug("testVariableDeclaration1");

		assertTrue("Should be variable declaration",
				ast1.children.get(0) instanceof Ast.DeclarationNode);
		Ast.DeclarationNode decl1 = (Ast.DeclarationNode) ast1.children.get(0);
		assertTrue("Id should be 'c'", decl1.id.equals(new String("c")));
		assertTrue("Declaration should be assigned with int",
				decl1.children.get(1) instanceof Ast.IntNode);
		assertTrue("Declaration should be assigned with value 123",
				((Ast.IntNode) decl1.children.get(1)).value == 123);

		assertTrue("Should be variable declaration",
				ast1.children.get(2) instanceof Ast.DeclarationNode);
		Ast.DeclarationNode decl2 = (Ast.DeclarationNode) ast1.children.get(2);
		assertTrue("Id should be 'a'", decl2.id.equals(new String("a")));
		assertTrue("Declaration should have IntTypeNode",
				decl2.children.get(0) instanceof IntTypeNode);
		assertTrue("Declaration should be assigned with int",
				decl2.children.get(1) instanceof Ast.IntNode);
		assertTrue("Declaration should be assigned with value 2",
				((Ast.IntNode) decl2.children.get(1)).value == 2);
	}

	public void testFile2() {
		Log.debug("testFile2");

		assertTrue("Should have 3 children", ast2.children.size() == 3);
		assertTrue("Should be variable declaration",
				ast2.children.get(0) instanceof Ast.DeclarationNode);
		assertTrue("Should be function declaration",
				ast2.children.get(1) instanceof Ast.FunctionDeclarationNode);
		assertTrue("Should be variable declaration",
				ast2.children.get(2) instanceof Ast.DeclarationNode);
	}

	public void testFunctionDeclaration2() {
		Log.debug("testFunctionDeclaration2");

		assertTrue("Should be function",
				ast2.children.get(1) instanceof Ast.FunctionDeclarationNode);
		Ast.FunctionDeclarationNode func = (Ast.FunctionDeclarationNode) ast2.children
				.get(1);
		assertTrue("Name should be main", func.id.equals(new String("main")));
		assertTrue("Should have formal parameters node",
				func.children.get(1) instanceof Ast.FormalParametersNode);
		assertTrue("Should have 1 parameter",
				func.children.get(1).children.size() == 1);
		assertTrue(
				"Should be FormalParameterNode",
				func.children.get(1).children.get(0) instanceof FormalParameterNode);
		FormalParameterNode fpn = (FormalParameterNode) func.children.get(1).children
				.get(0);
		assertTrue("Name should be d", fpn.id.equals(new String("d")));
		assertTrue(
				"Should be int*",
				fpn.children.get(0) instanceof PointerTypeNode
						&& fpn.children.get(0).children.get(0) instanceof IntTypeNode);
		assertTrue("Should block statement node",
				func.children.get(2) instanceof Ast.BlockStatementNode);
		assertTrue("Should have 1 child in block",
				func.children.get(2).children.size() == 1);

		assertTrue(
				"Should be declaration",
				func.children.get(2).children.get(0).children.get(0) instanceof Ast.DeclarationNode);
		Ast.DeclarationNode decl = (Ast.DeclarationNode) func.children.get(2).children
				.get(0).children.get(0);
		assertTrue(
				"Should be const pointer",
				decl.children.get(0) instanceof PointerTypeNode
						&& ((PointerTypeNode) decl.children.get(0)).constant == true);
		assertTrue(
				"Should be const pointer to const int",
				decl.children.get(0).children.get(0) instanceof IntTypeNode
						&& ((IntTypeNode) decl.children.get(0).children.get(0)).constant == true);
	}

	public void testVariableDeclaration2() {
		Log.debug("testVariableDeclaration2");

		assertTrue("Should be variable declaration",
				ast2.children.get(0) instanceof Ast.DeclarationNode);
		Ast.DeclarationNode decl1 = (Ast.DeclarationNode) ast2.children.get(0);
		assertTrue("Id should be 'c'", decl1.id.equals(new String("c")));
		assertTrue("Should have IntTypeNode",
				decl1.children.get(0) instanceof IntTypeNode);
		assertTrue("Should be constant",
				((IntTypeNode) decl1.children.get(0)).constant == true);
		assertTrue("Declaration should be assigned with int",
				decl1.children.get(1) instanceof Ast.IntNode);
		assertTrue("Declaration should be assigned with value 123",
				((Ast.IntNode) decl1.children.get(1)).value == 123);

		assertTrue("Should be variable declaration",
				ast2.children.get(2) instanceof Ast.DeclarationNode);
		Ast.DeclarationNode decl2 = (Ast.DeclarationNode) ast2.children.get(2);
		assertTrue("Id should be 'a'", decl2.id.equals(new String("a")));
		assertTrue("Should have PointerTypeNode",
				decl2.children.get(0) instanceof PointerTypeNode);
		assertTrue(
				"Should have PointerTypeNode",
				decl2.children.get(0).children.get(0) instanceof PointerTypeNode);
		assertTrue(
				"Should have IntTypeNode",
				decl2.children.get(0).children.get(0).children.get(0) instanceof IntTypeNode);
		assertTrue("Declaration should be assigned with int",
				decl2.children.get(1) instanceof Ast.IntNode);
		assertTrue("Declaration should be assigned with value 2",
				((Ast.IntNode) decl2.children.get(1)).value == 2);
	}

}
