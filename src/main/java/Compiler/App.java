package Compiler;

import org.antlr.v4.runtime.*;

public class App {
	public static void main(String[] args) {

		ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(System.in);
			CLexer lexer = new CLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			AstParser parser = new AstParser(tokens);
			parser.buildAst();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
