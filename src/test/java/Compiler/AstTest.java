package Compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.TestRig;

import Compiler.AstParser.*;

/**
 * Unit test for ast.
 */
public class AstTest extends TestCase
{
	private AstParser.Node ast1;
	
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
			assertTrue("Should be variable declaration", ast1.children.get(0) instanceof AstParser.DeclarationNode);
			assertTrue("Should be function declaration", ast1.children.get(1) instanceof AstParser.FunctionDeclarationNode);
			assertTrue("Should be variable declaration", ast1.children.get(2) instanceof AstParser.DeclarationNode);
    }
		
  
    public void testFunctionDeclaration1() {
    	System.out.println("testFunctionDeclaration");
    	
    	assertTrue("Should be function", ast1.children.get(1) instanceof AstParser.FunctionDeclarationNode);	
    	AstParser.FunctionDeclarationNode func = (AstParser.FunctionDeclarationNode)ast1.children.get(1);
    	assertTrue("Name should be main", func.id.equals(new String("main")));
    	assertTrue("Should have formal parameters node", func.children.get(0) instanceof AstParser.FormalParametersNode);
    	assertTrue("Should have no parameters", func.children.get(0).children.size() == 0);
    	assertTrue("Should block statement node", func.children.get(1) instanceof AstParser.BlockStatementNode);
    	assertTrue("Should have 3 children in block", func.children.get(1).children.size() == 3);
    }
    
    public void testVariableDeclaration1() {
    	System.out.println("testVariableDeclaration1");
    	
    	assertTrue("Should be variable declaration", ast1.children.get(0) instanceof AstParser.DeclarationNode);
    	AstParser.DeclarationNode decl1 = (AstParser.DeclarationNode)ast1.children.get(0);
    	assertTrue("Id should be 'c'", decl1.id.equals(new String("c")));
    	assertTrue("Declaration should be assigned with int", decl1.children.get(0) instanceof AstParser.IntNode);
    	assertTrue("Declaration should be assigned with value 123", ((AstParser.IntNode)decl1.children.get(0)).value == 123);
    	
 
    	assertTrue("Should be variable declaration", ast1.children.get(2) instanceof AstParser.DeclarationNode);
    	AstParser.DeclarationNode decl2 = (AstParser.DeclarationNode)ast1.children.get(2);
    	assertTrue("Id should be 'a'", decl2.id.equals(new String("a")));
    	assertTrue("Declaration should be assigned with int", decl2.children.get(0) instanceof AstParser.IntNode);
    	assertTrue("Declaration should be assigned with value 2", ((AstParser.IntNode)decl2.children.get(0)).value == 2);
    }
}
