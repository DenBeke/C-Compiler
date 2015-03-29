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
        Log.debug     = false;
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


                    is = new FileInputStream("src/test/input/good1.c");
                    input = new ANTLRInputStream(is);
                    lexer = new CLexer(input);
                    parser = new AstParser(new CommonTokenStream(lexer));
                    root = parser.buildAst();

                    visitor = new SymbolTableVisitor();
                    visitor.visit(root);

                    // OK if no exception was thrown
                    assertTrue(true);
                }
                catch (Log.FatalException e) {
                    // NOT OK when exception is thrown
                    assertTrue(false);
                }


            } catch (FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch (IOException e) {
                fail("Could not load input file");
                return;
            }

    }

    /**
     * Test for symbol redeclaration
     */
    public void testRedeclaration() {

        Log.debug("testRedeclaration");

        // Test without duplicate declarations
        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test2_ok.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // Should NOT throw exception
                assertTrue(true);


            } catch(FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch(IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // Should NOT throw exception
            assertTrue(false);
        }


        // Test with redeclarations
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
            assertEquals("5: Symbol 'b' previously declared (on line 4)", e.getMessage());
        }

        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test2_fail2.c");
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
            assertEquals("4: Symbol 'b' previously declared (on line 3)", e.getMessage());
        }

    }


    /**
     * Test for parameter redeclaration
     */
    public void testParamRedeclaration() {

        Log.debug("testParamRedeclaration");

        // Test without duplicate declarations
        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test3_ok.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // Should NOT throw exception
                assertTrue(true);


            } catch(FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch(IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // Should NOT throw exception
            assertTrue(false);
        }


        // Test with redeclarations
        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test3_fail.c");
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
            assertEquals("2: Parameter with name 'b' already exists", e.getMessage());
        }


    }



    /**
     * Test for undeclared symbols
     */
    public void testUndeclared() {

        Log.debug("testUndeclared");

        // Test without undeclared symbol
        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test4_ok.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // Should NOT throw exception
                assertTrue(true);


            } catch (FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch (IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // Should NOT throw exception
            assertTrue(false);
        }


        // Test with undeclared symbol
        try {

            try {
                InputStream is = new FileInputStream("src/test/input/symboltable/test4_fail.c");
                ANTLRInputStream input = new ANTLRInputStream(is);
                CLexer lexer = new CLexer(input);
                AstParser parser = new AstParser(new CommonTokenStream(lexer));
                Ast.Node root = parser.buildAst();

                Visitor visitor = new SymbolTableVisitor();
                visitor.visit(root);

                // Should throw exception
                assertTrue(false);


            } catch (FileNotFoundException e) {
                fail("Could not load input file");
                return;
            } catch (IOException e) {
                fail("Could not load input file");
                return;
            }


        } catch (Log.FatalException e) {
            // Should throw exception
            assertTrue(true);
            assertEquals("5: Use of undeclared 'a'", e.getMessage());
        }

    }

}
