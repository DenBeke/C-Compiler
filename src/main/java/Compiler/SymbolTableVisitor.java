package Compiler;

public class SymbolTableVisitor extends Visitor {
	public void visit(Ast.DeclarationNode node) {
		System.out.println("declaration");

		visitChildren(node);
	}

	public void visit(Ast.BlockStatementNode node) {
		System.out.println(">block");
		visitChildren(node);
		System.out.println("<block");
	}
}