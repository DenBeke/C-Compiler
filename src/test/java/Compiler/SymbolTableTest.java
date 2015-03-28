package Compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.antlr.v4.runtime.*;

public class SymbolTableTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public SymbolTableTest(String testName) {
        super(testName);
        Log.debug = false;
        Log.exception = true;
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SymbolTableTest.class);
    }


    /**
     * Test for some correct symbols
     */
    public void testCorrect() {

        Log.debug("testCorrect");

        try {


            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test1_ok.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // OK if no exception was thrown
                assertTrue(true);


            } catch (FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch (IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // NOT OK when exception is thrown
            assertTrue(false);
        }

    }

    /**
     * Test for symbol redeclaration
     */
    public void testRedeclaration() {

        Log.debug("testRedeclaration");

        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test2_fail.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // Should throw exception
                assertTrue(false);


            } catch(FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch(IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // Should throw exception
            assertTrue(true);
        }

    }

}
