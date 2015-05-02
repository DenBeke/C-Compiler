package Compiler;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

public class SymbolTableVisitor extends Visitor {
	private int functionDeclCounter = 0;

	/**
	 * Generalize two types
	 * 
	 * @param t1
	 *            First type
	 * @param t2
	 *            Second type
	 * 
	 * @return The type that can hold both t1 and t2. Return's null if no such
	 *         type exists.
	 */
	public Ast.TypeNode generalize(Ast.TypeNode t1, Ast.TypeNode t2) {
		if(t1 instanceof Ast.CharTypeNode) {
			if(t2 instanceof Ast.CharTypeNode) {
				return new Ast.CharTypeNode();
			} else if(t2 instanceof Ast.IntTypeNode) {
				return new Ast.IntTypeNode();
			}/*else if(t2 instanceof Ast.PointerTypeNode) {
				return new Ast.IntTypeNode();
			}*/
		}

		if(t1 instanceof Ast.IntTypeNode) {
			if(t2 instanceof Ast.CharTypeNode) {
				return new Ast.IntTypeNode();
			} else if(t2 instanceof Ast.IntTypeNode) {
				return new Ast.IntTypeNode();
			} /*else if(t2 instanceof Ast.PointerTypeNode) {
				return new Ast.IntTypeNode();
			}*/
		}

/*		if(t1 instanceof Ast.PointerTypeNode) {
			if(t2 instanceof Ast.CharTypeNode) {
				return new Ast.IntTypeNode();
			} else if(t2 instanceof Ast.IntTypeNode) {
				return new Ast.IntTypeNode();
			} else if(t2 instanceof Ast.PointerTypeNode) {
				return new Ast.IntTypeNode();
			}
		}
*/
		return null;
	}

	/*
	 * Add typecasts for the generalized type
	 * 
	 * @param e1 First expression
	 * 
	 * @param e2 Second expression
	 * 
	 * @return The type to which the expressions are casted. Null if no cast is
	 * possible.
	 */
	public Ast.TypeNode consistent(Ast.ExpressionNode e1, Ast.ExpressionNode e2) {
		Ast.TypeNode m = generalize(e1.getType(), e2.getType());
		if(m == null) {
			return null;
		}

		convert(e1, m);
		convert(e2, m);

		return m;
	}

	/*
	 * Add typecast to the AST
	 * 
	 * @param n The expression to typecast
	 * 
	 * @param t The type to typecast to
	 */
	public void convert(Ast.ExpressionNode e, Ast.TypeNode t) {
		if(e.getType().equals(t)) {
			return;
		}

		Ast.Node cast = e.getType().getTypeCastNode(t);
		if(cast == null) {
			Log.fatal("Can't cast " + e.getType().getStringRepresentation()
					+ " to " + t.getStringRepresentation(), e.line);
		}

		if(e.parent != null) {
			e.parent.replaceNode(e, cast);
		}

		cast.parent = e.parent;
		((Ast.CastExpressionNode) cast).setExpression(e);
	}

	/**
	 * Add a manual cast to ast for the expression
	 * 
	 * @param e
	 *            : expression node
	 */
	public void handleCastExpression(Ast.ExpressionNode e) {
		if(e.cast == null) {
			return;
		}

		convert(e, e.cast);
	}

	/**
	 * @brief Class representing a Symbol
	 *
	 *        A symbol has an identifier and a TypeNode
	 */
	public static class Symbol {
		public String id;
		public Ast.TypeNode type;
		public int scope;
		public int offset;
	}

	/**
	 * @brief Class representing a Symbol for a variable
	 */
	public static class VarSymbol extends Symbol {
	}

	/**
	 * @brief Class representing a Symbol for a function
	 *
	 *        - id - type (= return type) - paramTypes
	 */
	public static class FuncSymbol extends Symbol {

		public Vector<Ast.TypeNode> paramTypes = new Vector<>();
		public Ast.TypeNode returnType;
		public String label;

		/**
		 * Add new parameter type node to the function symbol
		 * 
		 * @param node
		 *            : TypeNode
		 */
		public void addParamType(Ast.TypeNode node) {
			paramTypes.add(node);
		}

		/**
		 * Add new parameter type node to the function symbol
		 * 
		 * @param node
		 *            : TypeNode
		 * @pre node instanceof Ast.TypeNode
		 */
		public void addParamType(Ast.Node node) {
			Assert.Assert(node instanceof Ast.TypeNode, "Expected TypeNode");
			paramTypes.add((Ast.TypeNode) node);
		}
	}

	/**
	 * @brief Class representing a Symbol Table
	 *
	 *        A symbol table has a map of identifiers to symbols
	 */
	public static class SymbolTable {
		private Map<String, Symbol> symbols;

		public SymbolTable() {
			symbols = new TreeMap<String, Symbol>();
		}

		/**
		 * Check if this table contains the symbol
		 *
		 * @param symbol
		 *            : The symbol to check
		 */
		public boolean hasSymbol(String symbol) {
			if(symbols.containsKey(symbol)) {
				return true;
			}

			return false;
		}

		/**
		 * Get symbol from the symbol table
		 * 
		 * @param id
		 *            : identifier
		 * @return Symbol
		 */
		public Symbol getSymbol(String id) {
			return symbols.get(id);
		}

		/**
		 * Add new symbol to symbol table
		 * 
		 * @param symbol
		 *            : symbol to add
		 */
		public void addSymbol(Symbol symbol) {
			symbols.put(symbol.id, symbol);
		}

	}

	/**
	 * Check if a symbol exists in any symbol table on the stack
	 * 
	 * @param id
	 * @return symbol
	 */
	private Symbol findSymbol(String id) {
		for(int i = 0; i < symbolTableStack.size(); i++) {
			if(symbolTableStack.get(i).hasSymbol(id)) {
				return symbolTableStack.get(i).getSymbol(id);
			}
		}

		return null;
	}

	/**
	 * Visit FileNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.FileNode node) {
		Log.debug("file");

		enterNewScope();
		visitChildren(node);
		leaveScope();
	}

	/**
	 * Visit DeclarationNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.DeclarationNode node) {
		Log.debug("var declaration");

		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
			Log.fatal("Symbol '" + node.id + "' previously declared (on line "
					+ symbolTableStack.peek().getSymbol(node.id).type.line
					+ ")", node.line);
		}

		VarSymbol symbol = new VarSymbol();
		symbol.scope = node.scope;
		symbol.id = node.id;
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode,
				"Expected TypeNode");
		symbol.type = (Ast.TypeNode) node.children.get(0);
		symbolTableStack.peek().addSymbol(symbol);

		node.symbol = symbol;

		visitChildren(node);
		handleCastExpression(node);

		if(!(node.getInitializer() instanceof Ast.NothingNode)) {
			convert((Ast.ExpressionNode) node.getInitializer(), node.getType());
		}
	}

	/**
	 * Visit FunctionCallNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.FunctionCallNode node) {
		Symbol symbol = findSymbol(node.id);
		if(symbol == null) {
			Log.fatal("Use of undeclared function '" + node.id + "'", node.line);
		}

		if(!(symbol instanceof FuncSymbol)) {
			Log.fatal("'" + node.id + "' is not a function", node.line);
		}

		FuncSymbol funcSymbol = (FuncSymbol) symbol;
		node.symbol = funcSymbol;

		if(node.children.size() != funcSymbol.paramTypes.size()) {
			Log.fatal(
					"Number of arguments for '" + symbol.id + "': "
							+ String.valueOf(funcSymbol.paramTypes.size())
							+ ", " + String.valueOf(node.children.size())
							+ " given", node.line);
		}

		node.setType(symbol.type);
		visitChildren(node);

		for(int i = 0; i < node.children.size(); i++) {
			convert(node.getParamExpression(i), funcSymbol.paramTypes.get(i));
		}

		handleCastExpression(node);
	}

	/**
	 * Visit ReturnStatementNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.ReturnStatementNode node) {
		visitChildren(node);

		// Check if we are inside a function
		Ast.FunctionDeclarationNode func = null;
		while(node.parent != null) {
			if(node.parent instanceof Ast.FunctionDeclarationNode) {
				func = (Ast.FunctionDeclarationNode) node.parent;
				break;
			}

			node.parent = node.parent.parent;
		}

		if(func == null) {
			Log.fatal("Return statement outside function declaration",
					node.line);
		}

        if(node.getExpression() instanceof Ast.ExpressionNode) {
            convert((Ast.ExpressionNode)node.getExpression(), func.getReturnType());
        }
        else {
            if(!(func.getReturnType() instanceof Ast.VoidTypeNode)) {
                Log.fatal("Empty return in non-void function",
                        node.line);
            }
        }

	}

	/**
	 * Visit CharNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.CharNode node) {
		visitChildren(node);

		handleCastExpression(node);
	}

	/**
	 * Visit IntNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.IntNode node) {
		visitChildren(node);

		handleCastExpression(node);
	}

	/**
	 * Visit StringNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.StringNode node) {
		visitChildren(node);

		handleCastExpression(node);
	}

	/**
	 * Visit IdNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.IdNode node) {
		Symbol symbol = findSymbol(node.id);
		if(symbol == null) {
			Log.fatal("Use of undeclared '" + node.id + "'", node.line);
		}

		node.setSymbol(symbol);

		visitChildren(node);
		handleCastExpression(node);
	}

	/**
	 * Visit BlockStatementNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.BlockStatementNode node) {
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}

	/**
	 * Visit BlockStatementNode
	 *
	 * @param node
	 * @param newScope
	 *            : bool indicating if block must have a new scope
	 */
	public void visit(Ast.BlockStatementNode node, boolean newScope) {
		if(newScope) {
			visit(node);
		} else {
			visitChildren(node);
		}
	}

	/**
	 * Visit FunctionDeclarationNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.FunctionDeclarationNode node) {

		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode,
				"Expected TypeNode");
		Assert.Assert(node.children.get(1) instanceof Ast.FormalParametersNode,
				"Expected FormalParametersNode");

		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
			Log.fatal("Symbol '" + node.id + "' previously declared (on line "
					+ symbolTableStack.peek().getSymbol(node.id).type.line
					+ ")", node.line);
		}

		FuncSymbol symbol = new FuncSymbol();

		symbol.returnType = node.getReturnType();
		symbol.id = node.id;
		symbol.label = node.id + Integer.toString(functionDeclCounter);
		functionDeclCounter += 1;
		symbol.type = (Ast.TypeNode) node.children.get(0);
		node.symbol = symbol;

		// add param types
		for(int i = 0; i < node.children.get(1).children.size(); i++) {
			Assert.Assert(
					node.children.get(1).children.get(i).children.get(0) instanceof Ast.TypeNode,
					"Expected TypeNode");
			symbol.addParamType(node.children.get(1).children.get(i).children
					.get(0));
		}

		symbolTableStack.peek().addSymbol(symbol);

		enterNewScope();

		// visit params
		for(int i = 0; i < node.children.get(1).children.size(); i++) {
			Assert.Assert(
					node.children.get(1).children.get(i) instanceof Ast.FormalParameterNode,
					"Expected FormalParameterNode");
			visit(node.children.get(1).children.get(i));
		}

		// visit block
		Assert.Assert(node.children.get(2) instanceof Ast.BlockStatementNode,
				"Expected BlockStatementNode");
		visit((Ast.BlockStatementNode) node.children.get(2), false);

		leaveScope();
	}

	/**
	 * Visit FormalParameterNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.FormalParameterNode node) {
		Log.debug("formal parameter");

		// Skip anonymous parameters (with no id)
		if(node.id == null) {
			visitChildren(node);
			return;
		}

		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
			Log.fatal("Parameter with name '" + node.id + "' already exists",
					node.line);
		}

		VarSymbol symbol = new VarSymbol();
		// AstParser will put parameters in the enclosing scope.
		// Put it in the same scope as the function's local variables.
		symbol.scope = node.scope + 1;
		symbol.id = node.id;
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode,
				"Expected TypeNode");
		symbol.type = (Ast.TypeNode) node.children.get(0);
		symbolTableStack.peek().addSymbol(symbol);

		node.symbol = symbol;

		visitChildren(node);
	}

	/**
	 * Visit UnaryOperatorNode
	 *
	 * @param node
	 */
	@Override
	public void visit(Ast.UnaryOperatorNode node) {
		Log.debug("UnaryOperatorNode");

		visitChildren(node);

		if(!(node.getExpression().getType() instanceof Ast.IntTypeNode)
				&& !(node.getExpression().getType() instanceof Ast.CharTypeNode)) {

			Log.fatal("UnaryOperator not supported for type '"
					+ node.getExpression().getType().getStringRepresentation()
					+ "'", node.line);
		}

		switch(node.operator) {
		case "++":
		case "--":
			node.setType(node.getExpression().getType());
			break;

		default:
			Log.fatal("Unary operator not implemented: " + node.operator,
					node.line);
		}

		handleCastExpression(node);
	}

	@Override
	public void visit(Ast.DereferenceExpressionNode node) {
		Log.debug("DereferenceExpressionNode");
		
		visitChildren(node);

		if(!(node.getExpression().getType() instanceof Ast.PointerTypeNode)) {
			Log.fatal("Can't dereference non-pointer type '" + node.getExpression().getType().getStringRepresentation()  + "'", node.line);
		}

		if(node.getExpression().getType().children.size() > 0) {
			node.setType((Ast.TypeNode)node.getExpression().getType().children.get(0));
		} else {
			Log.fatal("Don't go crazy with the casts. Try splitting it up in multiple statements.", node.line);
		}
		handleCastExpression(node);
	}

	@Override
	public void visit(Ast.ReferenceExpressionNode node) {
		Log.debug("ReferenceExpressionNode");
		
		visitChildren(node);

		Ast.PointerTypeNode pointer = new Ast.PointerTypeNode();
		pointer.addChild(0, node.getExpression().getType());
		node.setType(pointer);

		handleCastExpression(node);
	}

	public void visit(Ast.IfStatementNode node) {
		Log.debug("IfStatementNode");

		visitChildren(node);
		convert(node.getCondition(), new Ast.IntTypeNode());
	}

	public void visit(Ast.BinaryOperatorNode node) {
		Log.debug("BinaryOperatorNode");

		visitChildren(node);

		Ast.TypeNode resultType = null;

		switch(node.operator) {
		case "=":
			if(node.getLeftChild().getType().constant) {
				Log.fatal("Can't assign to constant variable", node.line);
			}

			convert(node.getRightChild(), node.getLeftChild().getType());
			resultType = node.getLeftChild().getType();
			break;
		case "==":
		case "!=":
		case ">":
		case ">=":
		case "<":
		case "<=":
			consistent(node.getLeftChild(), node.getRightChild());
			resultType = new Ast.IntTypeNode();
			break;
		case "+":
		case "-":
		case "/":
		case "*":
			resultType = consistent(node.getLeftChild(), node.getRightChild());
			if(resultType == null) {
				Log.fatal("Operator '"
						+ node.operator
						+ "' not supported for types '"
						+ node.getLeftChild().getType()
								.getStringRepresentation()
						+ "', '"
						+ node.getRightChild().getType()
								.getStringRepresentation() + "'", node.line);
			}

			break;

		default:
			Log.fatal("Binary operator not implemented: " + node.operator,
					node.line);
        }

        node.setType(resultType);
		handleCastExpression(node);
	}

	private Stack<SymbolTable> symbolTableStack = new Stack<SymbolTable>();

	private void enterNewScope() {
		Log.debug(">");
		symbolTableStack.push(new SymbolTable());
	}

	private void leaveScope() {
		Log.debug("<");
		symbolTableStack.pop();
	}

}
