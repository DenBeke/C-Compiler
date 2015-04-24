package Compiler;

import java.util.Vector;

import Compiler.SymbolTableVisitor.Symbol;

public class Ast {

	/**
	 * @brief Abstract base class of Node
	 */
	public static abstract class Node {

		// handleBlock() uses this to know which nodes should be added to the
		// block.
		public int scope;
		public int line = -1;

		/**
		 * Abstract visitor node
		 *
		 * @param visitor
		 */
		public abstract void visit(Visitor visitor);

		public Node parent = null;
		public Vector<Node> children = new Vector<Node>();

		public void addChild(int pos, Node n) {
			n.parent = this;
			children.add(pos, n);
		}

		public void replaceNode(Node f, Node t) {
			int pos = -1;
			for(int i = 0; i < children.size(); i++) {
				if(f == children.get(i)) {
					pos = i;
					break;
				}
			}

			if(pos == -1) {
				Log.fatal("Can't replace node. Node doesn't exist.", 0);
			}

			children.set(pos, t);
		}

		public Boolean hasChildren() {
			return children.size() > 0;
		}

		public void insertLefMostLeaf(Node n) {
			Assert.Assert(this instanceof PointerTypeNode);
			if(hasChildren()) {
				children.get(0).insertLefMostLeaf(n);
			} else {
				addChild(0, n);
			}
		}

		public String childrenToString(String prefix) {
			String result = "";

			for(int i = 0; i < children.size(); i++) {
				Assert.Assert(children.get(i) != null,
						"children.get(i) cannot be null");
				result += children.get(i).toString(prefix) + "\n";
			}

			return result;
		}

		/**
		 * Convert node to string
		 * 
		 * @param prefix
		 * @return
		 */
		public String toString(String prefix) {
			String result = prefix + getClass().getSimpleName() + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}

		@Override
		public String toString() {
			return toString("");
		}
	}

	public static class FileNode extends Node {
		private Vector<Node> declarations = new Vector<Node>();

		public void addDeclaration(int pos, Node declaration) {
			Assert.Assert(declaration instanceof DeclarationNode
					|| declaration instanceof FunctionDeclarationNode);
			declarations.add(pos, declaration);
			addChild(pos, declaration);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static abstract class TypeNode extends Node {
		public Boolean constant = false;
		public Boolean topLevel = false;

		public Node getTypeCastNode(TypeNode t) {
			return null;
		}

		public abstract String getStringRepresentation();
	}

	public static class ConstTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String getStringRepresentation() {
			return "TEST";
		}
	}

	public static class PointerTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String getStringRepresentation() {
			String result = "";
			for(int i = 0; i < children.size(); i++) {
				result += ((TypeNode) children.get(i))
						.getStringRepresentation();
			}

			result += "*";

			return result;
		}
	}

	public static class IntTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public Node getTypeCastNode(TypeNode t) {
			if(t instanceof CharTypeNode) {
				return new IntToCharExpressionNode();
			}

			return null;
		}

		@Override
		public String getStringRepresentation() {
			String result = "int";
			for(int i = 0; i < children.size(); i++) {
				result += ((TypeNode) children.get(i))
						.getStringRepresentation();
			}

			return result;
		}
	}

	public static class CharTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public Node getTypeCastNode(TypeNode t) {
			if(t instanceof IntTypeNode) {
				return new CharToIntExpressionNode();
			}

			return null;
		}

		@Override
		public String getStringRepresentation() {
			String result = "char";
			for(int i = 0; i < children.size(); i++) {
				result += ((TypeNode) children.get(i))
						.getStringRepresentation();
			}

			return result;
		}
	}

	public static class VoidTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String getStringRepresentation() {
			String result = "void";
			for(int i = 0; i < children.size(); i++) {
				result += ((TypeNode) children.get(i))
						.getStringRepresentation();
			}

			return result;
		}
	}

	public static class StaticArrayTypeNode extends TypeNode {
		public Integer size;
		private TypeNode type;

		public StaticArrayTypeNode(Integer size, TypeNode type) {
			this.size = size;
			this.type = type;

			addChild(0, type);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String getStringRepresentation() {
			String result = "";
			for(int i = 0; i < children.size(); i++) {
				result += ((TypeNode) children.get(i))
						.getStringRepresentation();
			}

			result += "[" + size.toString() + "]";

			return result;
		}
	}

	public static abstract class LiteralNode extends ExpressionNode {
	}

	public static class IntNode extends LiteralNode {
		public Integer value;

		public IntNode(Integer value) {
			this.value = value;
			type = new IntTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static class CharNode extends LiteralNode {
		public Character value;

		public CharNode(Character value) {
			this.value = value;
			type = new CharTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static class StringNode extends LiteralNode {
		public String value;

		public StringNode(String value) {
			this.value = value;
			type = new PointerTypeNode();
			type.addChild(0, new CharTypeNode());
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IdNode extends ExpressionNode {
		public String id;

		private Symbol symbol;

		public IdNode(String id) {
			this.id = id;
		}

		/*
		 * Set the symbol for this id.
		 * 
		 * @param symbol The symbol for this id
		 * 
		 * @post getType() == symbol.type
		 */
		public void setSymbol(Symbol symbol) {
			this.symbol = symbol;
			type = symbol.type;
		}

		/*
		 * Get the symbol for this id
		 * 
		 * @return The symbol or null if no symbol was set
		 */
		public Symbol getSymbol() {
			return symbol;
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class DeclarationNode extends ExpressionNode {
		public String id;

		// Nothing or Expression

		public DeclarationNode(String id, Ast.TypeNode type,
				Ast.Node initializer) {
			Assert.Assert(initializer instanceof NothingNode
					|| initializer instanceof ExpressionNode);
			this.id = id;
			this.type = type;

			addChild(0, initializer);
			addChild(0, type);
		}

		public Node getInitializer() {
			return children.get(1);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionDeclarationNode extends Node {
		public String id;

		public FunctionDeclarationNode(String id, TypeNode returnType,
				FormalParametersNode params, BlockStatementNode block) {
			this.id = id;

			addChild(0, block);
			addChild(0, params);
			addChild(0, returnType);
		}
		
		public TypeNode getReturnType() {
			return (TypeNode)children.get(0);
		}
		
		public BlockStatementNode getBlock() {
			return (BlockStatementNode)children.get(2);
		}
		
		public FormalParametersNode getParams() {
			return (FormalParametersNode)children.get(1);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FormalParametersNode extends Node {
		private Vector<FormalParameterNode> params = new Vector<FormalParameterNode>();

		public void addParam(int pos, FormalParameterNode param) {
			params.add(pos, param);
			addChild(pos, param);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FormalParameterNode extends Node {
		public String id;
		private TypeNode type;

		public FormalParameterNode(String id, TypeNode type) {
			this.id = id;
			this.type = type;

			addChild(0, type);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ParamNode extends Node {
		private ExpressionNode param;

		public ParamNode(ExpressionNode param) {
			this.param = param;

			addChild(0, param);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionCallNode extends ExpressionNode {
		public String id;

		public FunctionCallNode(String id) {
			this.id = id;
		}

		public void addParam(int pos, ParamNode param) {
			addChild(pos, param);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static abstract class StatementNode extends Node {
	}

	public static abstract class ExpressionNode extends Node {
		protected TypeNode type;
		public TypeNode cast = null;

		/*
		 * Set the type of this node. Will be used by ResolveTypeVisitor.
		 * 
		 * @param type The type for this expression
		 */
		public void setType(TypeNode type) {
			this.type = type;
		}

		/*
		 * Get the type of this expression.
		 * 
		 * @return The type of this expression or null if no type
		 */
		public TypeNode getType() {
			return this.type;
		}
	}

	public static class CastExpressionNode extends ExpressionNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		public void setExpression(ExpressionNode e) {
			children.clear();
			addChild(0, e);
		}

	}

	public static class CharToIntExpressionNode extends CastExpressionNode {
		public CharToIntExpressionNode() {
			type = new IntTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IntToCharExpressionNode extends CastExpressionNode {
		public IntToCharExpressionNode() {
			type = new CharTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class BlockStatementNode extends StatementNode {
		public void addStatement(int pos, StatementNode statement) {
			addChild(pos, statement);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ExprStatementNode extends StatementNode {
		public ExprStatementNode(ExpressionNode expression) {
			addChild(0, expression);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class BinaryOperatorNode extends ExpressionNode {
		public String operator;

		public BinaryOperatorNode(String operator, ExpressionNode left,
				ExpressionNode right) {
			this.operator = operator;

			addChild(0, right);
			addChild(0, left);
		}

		/*
		 * Get the expression to the left of the operator
		 * 
		 * @return The expression to the left
		 */
		public ExpressionNode getLeftChild() {
			return (ExpressionNode) children.get(0);
		}

		/*
		 * Get the expression to the right of the operator
		 * 
		 * @return The expression to the right
		 */
		public ExpressionNode getRightChild() {
			return (ExpressionNode) children.get(1);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class UnaryOperatorNode extends ExpressionNode {
		public String operator;
		public ExpressionNode expression;

		public UnaryOperatorNode(String operator, ExpressionNode expression) {
			this.operator = operator;
			this.expression = expression;

			addChild(0, expression);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ForStatementNode extends StatementNode {
		private Node first;
		private Node second;
		private Node third;
		private StatementNode body;

		public ForStatementNode(Node first, Node second, Node third,
				StatementNode body) {
			Assert.Assert(first instanceof NothingNode
					|| first instanceof ExpressionNode);
			Assert.Assert(second instanceof NothingNode
					|| second instanceof ExpressionNode);
			Assert.Assert(third instanceof NothingNode
					|| third instanceof ExpressionNode);

			this.first = first;
			this.second = second;
			this.third = third;
			this.body = body;

			addChild(0, body);
			addChild(0, third);
			addChild(0, second);
			addChild(0, first);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class NothingNode extends Node {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ReturnStatementNode extends StatementNode {
		public ReturnStatementNode(ExpressionNode expression) {
			addChild(0, expression);
		}
		
		public ExpressionNode getExpression() {
			return (ExpressionNode)children.get(0);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class WhileStatementNode extends StatementNode {
		private ExpressionNode condition;
		private StatementNode body;

		public WhileStatementNode(ExpressionNode condition, StatementNode body) {
			this.condition = condition;
			this.body = body;

			addChild(0, body);
			addChild(0, condition);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IfStatementNode extends StatementNode {
		private ExpressionNode condition;
		private StatementNode body;
		private Node elseBody;

		public IfStatementNode(ExpressionNode condition, StatementNode body,
				Node elseBody) {
			Assert.Assert(elseBody instanceof NothingNode
					|| elseBody instanceof StatementNode);

			this.condition = condition;
			this.body = body;
			this.elseBody = elseBody;

			addChild(0, elseBody);
			addChild(0, body);
			addChild(0, condition);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class BreakStatementNode extends StatementNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ContinueStatementNode extends StatementNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

}
