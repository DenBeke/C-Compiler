package Compiler;

import java.util.Vector;
import Compiler.SymbolTableVisitor.*;

public class Ast {

	/**
	 * @brief Abstract base class of Node
	 */
	public static abstract class Node {

		public int scope;
		public int line = -1;

		/**
		 * Abstract visitor node
		 *
		 * @param visitor
		 */
		public abstract void visit(Visitor visitor);

		public Vector<Node> children = new Vector<Node>();

		public Boolean hasChildren() {
			return children.size() > 0;
		}

		public void insertLefMostLeaf(Node n) {
			Assert.Assert(this instanceof PointerTypeNode);
			if(hasChildren()) {
				children.get(0).insertLefMostLeaf(n);
			} else {
				children.add(0, n);
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
			children.add(pos, declaration);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static abstract class TypeNode extends Node {
		public Boolean constant = false;
		public Boolean topLevel = false;
	}

	public static class ConstTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class PointerTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IntTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class CharTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class VoidTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class StaticArrayTypeNode extends TypeNode {
		public Integer size;
		private TypeNode type;

		public StaticArrayTypeNode(Integer size, TypeNode type) {
			this.size = size;
			this.type = type;

			children.add(0, type);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static abstract class LiteralNode extends ExpressionNode {
	}

	public static class IntNode extends LiteralNode {
		public Integer value;

		public IntNode(Integer value) {
			this.value = value;
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
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IdNode extends ExpressionNode {
		public String id;

		// Filled in by symbolTableVisitor
		public Symbol symbol;

		public IdNode(String id) {
			this.id = id;
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class DeclarationNode extends ExpressionNode {
		public String id;
		public TypeNode type;
		// Nothing or Expression
		public Node initializer;

		public DeclarationNode(String id, Ast.TypeNode type,
				Ast.Node initializer) {
			Assert.Assert(initializer instanceof NothingNode
					|| initializer instanceof ExpressionNode);
			this.id = id;
			this.type = type;
			this.initializer = initializer;

			children.add(0, initializer);
			children.add(0, type);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionDeclarationNode extends Node {
		public String id;
		public TypeNode returnType;
		public FormalParametersNode params;
		public BlockStatementNode block;

		public FunctionDeclarationNode(String id, TypeNode returnType,
				FormalParametersNode params, BlockStatementNode block) {
			this.id = id;
			this.returnType = returnType;
			this.params = params;
			this.block = block;

			children.add(0, block);
			children.add(0, params);
			children.add(0, returnType);
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
			children.add(pos, param);
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

			children.add(0, type);
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

			children.add(0, param);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionCallNode extends ExpressionNode {
		public String id;
		private Vector<ParamNode> params = new Vector<ParamNode>();

		public FunctionCallNode(String id) {
			this.id = id;
		}

		public void addParam(int pos, ParamNode param) {
			params.add(pos, param);

			children.add(pos, param);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static abstract class StatementNode extends Node {
	}

	public static abstract class ExpressionNode extends Node {
	}

	public static class BlockStatementNode extends StatementNode {
		private Vector<StatementNode> statements = new Vector<StatementNode>();

		public void addStatement(int pos, StatementNode statement) {
			statements.add(pos, statement);

			children.add(pos, statement);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ExprStatementNode extends StatementNode {
		private ExpressionNode expression;

		public ExprStatementNode(ExpressionNode expression) {
			this.expression = expression;

			children.add(0, expression);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class BinaryOperatorNode extends ExpressionNode {
		public String operator;
		private ExpressionNode left;
		private ExpressionNode right;

		public BinaryOperatorNode(String operator, ExpressionNode left,
				ExpressionNode right) {
			this.operator = operator;
			this.left = left;
			this.right = right;

			children.add(0, right);
			children.add(0, left);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class UnaryOperatorNode extends ExpressionNode {
		public String operator;
		private ExpressionNode expression;

		public UnaryOperatorNode(String operator, ExpressionNode expression) {
			this.operator = operator;
			this.expression = expression;

			children.add(0, expression);
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

			children.add(0, body);
			children.add(0, third);
			children.add(0, second);
			children.add(0, first);
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
		private ExpressionNode expression;

		public ReturnStatementNode(ExpressionNode expression) {
			this.expression = expression;

			children.add(0, expression);
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

			children.add(0, body);
			children.add(0, condition);
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

			children.add(0, elseBody);
			children.add(0, body);
			children.add(0, condition);
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
