package Compiler;

/**
 * @brief Visitor for Ast
 */
public abstract class Visitor {

	/*
	 * Visit a generic node. Will use double dispatch to call the correct
	 * method. If there is no overridden method for this node, it will lead in
	 * infinite recursion.
	 * 
	 * @param node The node to visit.
	 */
	public void visit(Ast.Node node) {
		// Go go double dispatch. The node will call one of the specialized
		// visit methods.
		node.visit(this);
	}

	public void visit(Ast.FileNode node) {
		visitChildren(node);
	}

	public void visit(Ast.PointerTypeNode node) {
		visitChildren(node);
	}

	public void visit(Ast.IntTypeNode node) {
		visitChildren(node);
	}

	public void visit(Ast.CharTypeNode node) {
		visitChildren(node);
	}

	public void visit(Ast.VoidTypeNode node) {
		visitChildren(node);
	}

	public void visit(Ast.StaticArrayTypeNode node) {
		visitChildren(node);
	}

	public void visit(Ast.IntNode node) {
		visitChildren(node);
	}

	public void visit(Ast.CharNode node) {
		visitChildren(node);
	}

	public void visit(Ast.StringNode node) {
		visitChildren(node);
	}

	public void visit(Ast.IdNode node) {
		visitChildren(node);
	}

	public void visit(Ast.DeclarationNode node) {
		visitChildren(node);
	}

	public void visit(Ast.FunctionDeclarationNode node) {
		visitChildren(node);
	}

	public void visit(Ast.FormalParametersNode node) {
		visitChildren(node);
	}

	public void visit(Ast.FormalParameterNode node) {
		visitChildren(node);
	}

	public void visit(Ast.ParamNode node) {
		visitChildren(node);
	}

	public void visit(Ast.FunctionCallNode node) {
		visitChildren(node);
	}

	public void visit(Ast.BlockStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.BinaryOperatorNode node) {
		visitChildren(node);
	}

	public void visit(Ast.UnaryOperatorNode node) {
		visitChildren(node);
	}

	public void visit(Ast.ForStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.NothingNode node) {
		visitChildren(node);
	}

	public void visit(Ast.ReturnStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.WhileStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.IfStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.BreakStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.ContinueStatementNode node) {
		visitChildren(node);
	}

	public void visit(Ast.ExprStatementNode node) {
		visitChildren(node);
	};

	public void visit(Ast.CharToIntExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.IntToCharExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.IntToPointerExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.PointerToIntExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.PointerToPointerExpressionNode node) {
		visitChildren(node);
	};
	
	public void visit(Ast.ArrayToPointerExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.ReferenceExpressionNode node) {
		visitChildren(node);
	};

	public void visit(Ast.DereferenceExpressionNode node) {
		visitChildren(node);
	};
	
	public void visit(Ast.VariadicTypeNode node) {
		visitChildren(node);
	};
	
	public void visit(Ast.SubscriptExpressionNode node) {
		visitChildren(node);
	};
	
	public void visit(Ast.InitializerListNode node) {
		visitChildren(node);
	};

	/*
	 * Visit all the children of the node.
	 * 
	 * @param node The node who's children to visit.
	 */
	public void visitChildren(Ast.Node node) {
		Assert.Assert(node.children != null, "node.children cannot be null?!");
		for(int i = 0; i < node.children.size(); i++) {
			Assert.Assert(node.children.get(i) != null,
					"node.children.get(i) cannot be null?!");
			node.children.get(i).visit(this);
		}
	}

}
