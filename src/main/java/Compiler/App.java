package Compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * @brief Main
 */
public class App {
	public static void main(String[] args) {

		Log.debug = false;

		ANTLRInputStream input;
		try {
			input = new ANTLRInputStream(System.in);
			CLexer lexer = new CLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			AstParser parser = new AstParser(tokens);
			Ast.Node root = parser.buildAst();
			Visitor visitor = new SymbolTableVisitor();
			visitor.visit(root);
            visitor = new SemanticVisitor();
            visitor.visit(root);
            visitor = new CodeGenVisitor();
            visitor.visit(root);
//			System.out.println(root.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
