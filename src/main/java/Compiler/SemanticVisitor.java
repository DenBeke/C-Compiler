package Compiler;


public class SemanticVisitor extends Visitor {

	/**
	 * Visit FunctionDeclarationNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.FunctionDeclarationNode node) {
		if(node.symbol.builtin) {
			return;
		}
		
		if(!checkReturn(node)
				&& !(node.getReturnType() instanceof Ast.VoidTypeNode)) {
			Log.warning("Control may reach end of non-void function '"
					+ node.id + "' without return", node.line);
		}
	}

	/**
	 * Check if there are enough return statements in this block
	 * 
	 * @param node
	 * @return
	 */
	public static boolean checkReturn(Ast.Node node) {
		int count = 0;
		for(Ast.Node child : node.children) {
			if(node instanceof Ast.IfStatementNode) {
				if(count <= 1) {
					count++;
					continue;
				}
			}
			if(child instanceof Ast.ReturnStatementNode) {
				return true;
			}

			if(checkReturn(child)) {
				return true;
			}

			count++;
		}

		return false;
	}

}