package Compiler;

import org.antlr.v4.runtime.*;

public class AstParser extends CParser {
	public AstParser(TokenStream input) {
		super(input);
	}

	public void handleVarDecl(String id) {
		System.out.println("handleVarDecl: " + id);
	}

	public void handleExpr() {
		System.out.println("handleExpr");
	}

	public void handleInt() {
		System.out.println("handleInt");
	}

	public void handleAssign() {
		System.out.println("handleAssign");
	}

	public void handleID(String id) {
		System.out.println("handleID: " + id);
	};
}
