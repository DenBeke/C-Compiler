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

	public AstTest(String testName) {
		super(testName);

		InputStream is;
		try {
			is = new FileInputStream("src/test/input/ast/test1_ok.c");
		} catch (FileNotFoundException e) {
			fail("Could not load input file");
			return;
		}
		ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(is);
		} catch (IOException e) {
			fail("Could not load input file");
			return;
		}
		CLexer lexer = new CLexer(input);
		AstParser parser = new AstParser(new CommonTokenStream(lexer));
		ast1 = parser.buildAst();
	}

	public static Test suite() {
		return new TestSuite(AstTest.class);
	}

	public void testFile1() {
		System.out.println("testFile");

		assertTrue("Should have 3 children", ast1.children.size() == 3);
		assertTrue("Should be variable declaration",
				ast1.children.get(0) instanceof Ast.DeclarationNode);
		assertTrue("Should be function declaration",
				ast1.children.get(1) instanceof Ast.FunctionDeclarationNode);
		assertTrue("Should be variable declaration",
				ast1.children.get(2) instanceof Ast.DeclarationNode);
	}

	public void testFunctionDeclaration1() {
		System.out.println("testFunctionDeclaration");

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
		System.out.println("testVariableDeclaration1");

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
}
