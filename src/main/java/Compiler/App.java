package Compiler;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Hello world!
 *
 */
public class App
{
	
	public static void buildAST(ParseTree tree) {
		for(int i = 0; i < tree.getChildCount(); i++) {
			System.out.println("");
			buildAST(tree.getChild(i));
		}
	}
	
    public static void main( String[] args )
    {
        
        
        ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(System.in);
			CLexer lexer = new CLexer(input);                           // create a buffer of tokens pulled from the lexer
	        CommonTokenStream tokens = new CommonTokenStream(lexer);    // create a parser that feeds off the tokens buffer
	        CParser parser = new CParser(tokens);
	        CParser.FileContext filecontext = parser.file();
	        
	        System.out.println( filecontext.toStringTree() );
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   // create a lexer that feeds off of input CharStream
                            // print LISP-style tree }
        
        
        System.out.println( "Hello World!" );
    }
}
