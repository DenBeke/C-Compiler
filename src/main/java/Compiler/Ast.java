package Compiler;

import java.util.Vector;

import Compiler.SymbolTableVisitor.*;

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

		public int countChildren(Ast.Node n) {
			int c = 0;
			for(int i = 0; i < children.size(); i++) {
				if(children.get(i).getClass().equals(n.getClass())) {
					c++;
				}
			}

			return c;
		}

		public Vector<String> codeL() {
			Log.fatal("codeL not implemented for " + getClass().getName(), line);
			return null;
		}

		public Vector<String> codeR() {
			Log.fatal("codeR not implemented for " + getClass().getName(), line);
			return null;
		}

		public Vector<String> code() {
			Log.fatal("code not implemented for " + getClass().getName(), line);
			return null;
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
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			FuncSymbol main = null;

			int varDecls = 5;
			for(int i = 0; i < children.size(); i++) {
				if(children.get(i) instanceof DeclarationNode) {
					((DeclarationNode)children.get(i)).symbol.offset = varDecls;
					varDecls += 1;
				} else if(children.get(i) instanceof FunctionDeclarationNode) {
					FunctionDeclarationNode fd = (FunctionDeclarationNode)children.get(i);
					if(fd.id.equals("main")) {
						main = fd.symbol;
					}
				}
			}

			if(main == null) {
				Log.fatal("Could not find main function", -1);
			}

			if(main.paramTypes.size() > 0) {
				Log.fatal("Main can't take arguments", -1);
			}

			if(!(main.returnType instanceof VoidTypeNode)) {
				Log.fatal("Main should return void", -1);
			}


			// Pretend that global scope is a function enclosing everything else
			instructions.add("mst 0");
			instructions.add("cup 0 init");
			instructions.add("init:");
			instructions.add("ssp " + Integer.toString(varDecls));
			for(int i = 0; i < children.size(); i++) {
				if(children.get(i) instanceof DeclarationNode) {
					instructions.addAll(children.get(i).code());
				}
			}

			instructions.add("mst 0");
			instructions.add("cup 0 " + main.label);
			instructions.add("hlt");

			for(int i = 0; i < children.size(); i++) {
				if(children.get(i) instanceof FunctionDeclarationNode) {
					instructions.addAll(children.get(i).code());
				}
			}

			return instructions;
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

		public boolean equals(TypeNode t) {
			if(!getClass().equals(t.getClass())) {
				return false;
			}

			if(children.size() != t.children.size()) {
				return false;
			}

			if(children.size() > 0 && t.children.size() > 0) {
				return ((TypeNode)children.get(0)).equals((TypeNode)t.children.get(0));
			}

			return true;
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
		public Node getTypeCastNode(TypeNode t) {
			if(t instanceof IntTypeNode) {
				return new PointerToIntExpressionNode();
			}

			if(t instanceof PointerTypeNode) {
				return new PointerToPointerExpressionNode();
			}

			return null;
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

			if(t instanceof PointerTypeNode) {
				return new IntToPointerExpressionNode();
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

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.add("ldc i " + Integer.toString(value));
			return instructions;
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
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			int depth = scope - symbol.scope;
			int offset = symbol.offset;
			instructions.add("lod " + CodeGenVisitor.typeToPtype(getType()) + " " + Integer.toString(depth) + " " + Integer.toString(offset));

			return instructions;
		}

		@Override
		public Vector<String> codeL() {
			Vector<String> instructions = new Vector<String>();

			int depth = scope - symbol.scope;
			int offset = symbol.offset;
			instructions.add("lda " + Integer.toString(depth) + " " + Integer.toString(offset));

			return instructions;
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class DeclarationNode extends ExpressionNode {
		public String id;
		public Symbol symbol = null;

		public DeclarationNode() {
		}

		public DeclarationNode(String id, Ast.TypeNode type,
				Ast.Node initializer) {
			Assert.Assert(initializer instanceof NothingNode
					|| initializer instanceof ExpressionNode);
			this.id = id;
			this.type = type;

			addChild(0, initializer);
			addChild(0, type);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			if(!(getInitializer() instanceof NothingNode)) {
				instructions.addAll(getInitializer().codeR());
				instructions.add("str " + CodeGenVisitor.typeToPtype(getType()) + " 0 " + Integer.toString(symbol.offset));
			}

			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = code();
			instructions.add("lod " + CodeGenVisitor.typeToPtype(getType()) + 0  + Integer.toString(symbol.offset));

			return instructions;
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
		private static int functionCounter = 0;

		public String id;
		public FuncSymbol symbol;

		public FunctionDeclarationNode(String id, TypeNode returnType,
				FormalParametersNode params, BlockStatementNode block) {
			this.id = id;

			addChild(0, block);
			addChild(0, params);
			addChild(0, returnType);
		}

		public TypeNode getReturnType() {
			return (TypeNode) children.get(0);
		}

		public BlockStatementNode getBlock() {
			return (BlockStatementNode) children.get(2);
		}

		public FormalParametersNode getParams() {
			return (FormalParametersNode) children.get(1);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			instructions.add(symbol.label + ":");
			
			int staticDataSize = 5;

			for(int i = 0; i < getParams().children.size(); i++) {
				FormalParameterNode fp = (FormalParameterNode)getParams().children.get(i);
				fp.symbol.offset = staticDataSize;
				staticDataSize += 1;
			}

			for(int i = 0; i < getBlock().children.size(); i++) {
				if(getBlock().children.get(i) instanceof ExprStatementNode) {
					if(getBlock().children.get(i).children.get(0) instanceof DeclarationNode) {
						DeclarationNode decl = (DeclarationNode)getBlock().children.get(i).children.get(0);
						decl.symbol.offset = staticDataSize;
						staticDataSize += 1;
					}
				}
			}

			instructions.add("ssp " + Integer.toString(staticDataSize));
			for(int i = 0; i < getBlock().children.size(); i++) {
				instructions.addAll(getBlock().children.get(i).code());
			}
		
			// TODO: Should be in AST
			if(getReturnType() instanceof VoidTypeNode) {
				instructions.add("retp");
			} else {
				instructions.add("retf");
			}

			return instructions;
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
		public VarSymbol symbol;

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
		public ParamNode(ExpressionNode param) {
			addChild(0, param);
		}

		public ExpressionNode getExpression() {
			return (ExpressionNode) children.get(0);
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionCallNode extends ExpressionNode {
		public String id;
		public FuncSymbol symbol;

		public FunctionCallNode(String id) {
			this.id = id;
		}

		public void addParam(int pos, ParamNode param) {
			addChild(pos, param);
		}

		public ExpressionNode getParamExpression(int pos) {
			return ((ParamNode) children.get(pos)).getExpression();
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			// Static link to global scope.
			// TODO: If nested function have to be supported this link has to change.
			instructions.add("mst 1");

			for(int i = 0; i < children.size(); i++) {
				instructions.addAll(getParamExpression(i).codeR());
			}

			instructions.add("cup " + Integer.toString(children.size()) + " " + symbol.label);

			// TODO: generate pop instruction if non void function

			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			if(symbol.returnType instanceof VoidTypeNode) {
				Log.fatal("Cannot generate rvalue for void function", line);
			}

			instructions.add("mst 0");

			for(int i = 0; i < children.size(); i++) {
				instructions.addAll(getParamExpression(i).codeR());
			}

			instructions.add("cup " + Integer.toString(children.size()) + " " + symbol.label);

			return instructions;
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

	public static class ReferenceExpressionNode extends ExpressionNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		public ReferenceExpressionNode(ExpressionNode e) {
			addChild(0, e);
		}

		public ExpressionNode getExpression() {
			return (ExpressionNode)children.get(0);
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(getExpression().codeL());
			return instructions;
		}
	}

	public static class DereferenceExpressionNode extends ExpressionNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		public DereferenceExpressionNode(ExpressionNode e) {
			addChild(0, e);
		}

		public ExpressionNode getExpression() {
			return (ExpressionNode)children.get(0);
		}

		@Override
		public Vector<String> codeL() {
			Vector<String> instructions = new Vector<String>();

			// Dereferencing an lvalue means we should put the address we point to on the stack.
			// So generate the rvalue of the poiner
			instructions.addAll(getExpression().codeR());
			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(getExpression().codeR());
			// The above code will put the address to which the pointer points to on the stack.
			// Now we dereference it.
			instructions.add("ind " + CodeGenVisitor.typeToPtype(getType()));

			return instructions;
		}
	}
	public static class CastExpressionNode extends ExpressionNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		public void setExpression(ExpressionNode e) {
			e.children.clear();
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

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(children.get(0).codeR());
			instructions.add("conv c i");

			return instructions;
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

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(children.get(0).codeR());
			instructions.add("conv i c");

			return instructions;
		}
	}

	public static class PointerToIntExpressionNode extends CastExpressionNode {
		public PointerToIntExpressionNode() {
			type = new IntTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(children.get(0).codeR());
			instructions.add("conv a i");

			return instructions;
		}
	}

	public static class PointerToPointerExpressionNode extends CastExpressionNode {
		public PointerToPointerExpressionNode() {
			type = new PointerTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(children.get(0).codeR());

			return instructions;
		}
	}

	public static class IntToPointerExpressionNode extends CastExpressionNode {
		public IntToPointerExpressionNode() {
			type = new PointerTypeNode();
		}

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(children.get(0).codeR());
			instructions.add("conv i a");

			return instructions;
		}
	}

	public static class BlockStatementNode extends StatementNode {
		public void addStatement(int pos, StatementNode statement) {
			addChild(pos, statement);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();


			for(int i = 0; i < children.size(); i++) {
				instructions.addAll(children.get(i).code());
			}

			return instructions;
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

		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();
			instructions.addAll(children.get(0).code());

			return instructions;
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

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();
			instructions.addAll(codeR());

			// TODO: generate pop instruction.

			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			String pType = CodeGenVisitor.typeToPtype(getType());
			String childPType = CodeGenVisitor.typeToPtype(getLeftChild().getType());

			if(pType == null || childPType == null) {
				Log.fatal("Cant convert type to ptype", line);
			}

			if(operator.equals("=")) {
				instructions.addAll(getLeftChild().codeL());
				instructions.addAll(getRightChild().codeR());
			} else {
				instructions.addAll(getLeftChild().codeR());
				instructions.addAll(getRightChild().codeR());
			}

			switch(operator) {
			case "=":
				instructions.add("sto " + CodeGenVisitor.typeToPtype(getLeftChild().getType()));
				// Put assigned value back on stack
				instructions.addAll(getLeftChild().codeL());
				instructions.add("ind " + CodeGenVisitor.typeToPtype(getLeftChild().getType()));
				break;
			case "==":
				instructions.add("equ " + childPType);
				instructions.add("conv b " + pType);
				break;
			case "!=":
				instructions.add("neq " + childPType);
				instructions.add("conv b " + pType);
				break;
			case ">":
				instructions.add("grt " + childPType);
				instructions.add("conv b " + pType);
				break;
			case ">=":
				instructions.add("geq " + childPType);
				instructions.add("conv b " + pType);
				break;
			case "<":
				instructions.add("les " + childPType);
				instructions.add("conv b " + pType);
				break;
			case "<=":
				instructions.add("leq " + childPType);
				instructions.add("conv b " + pType);
				break;
			case "+":
				instructions.add("add " + childPType);
				break;
			case "-":
				instructions.add("sub " + childPType);
				break;
			case "/":
				instructions.add("div " + childPType);
				break;
			case "*":
				instructions.add("mul " + childPType);
				break;
			default:
				Log.fatal("Codegen invalid binary operator: " + operator, line);
			}

			return instructions;
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

		public UnaryOperatorNode(String operator, ExpressionNode expression) {
			this.operator = operator;

			addChild(0, expression);
		}

		public ExpressionNode getExpression() {
			return (ExpressionNode) children.get(0);
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
		public ReturnStatementNode(Node expression) {
			addChild(0, expression);
		}

		public Node getExpression() {
			return (Node) children.get(0);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			if(!(getExpression() instanceof NothingNode)) {
				instructions.addAll(((ExpressionNode)getExpression()).codeR());
				instructions.add("str " + CodeGenVisitor.typeToPtype(((ExpressionNode)getExpression()).getType()) + " 0 0");
				instructions.add("retf");
			} else {
				instructions.add("retp");
			}

			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			return code();
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
		public IfStatementNode(ExpressionNode condition, StatementNode body,
				Node elseBody) {
			Assert.Assert(elseBody instanceof NothingNode
					|| elseBody instanceof StatementNode);

			addChild(0, elseBody);
			addChild(0, body);
			addChild(0, condition);
		}

		public ExpressionNode getCondition() {
			return (ExpressionNode)children.get(0);
		}

		public StatementNode getBody() {
			return (StatementNode)children.get(1);
		}

		public Node getElse() {
			return children.get(2);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();


			String endIfLabel = CodeGenVisitor.getUniqueLabel();
			String elseLabel = CodeGenVisitor.getUniqueLabel();

			instructions.addAll(getCondition().codeR());
			instructions.add("conv " + CodeGenVisitor.typeToPtype(getCondition().getType())  + " b");
			if(getElse() instanceof NothingNode) {
				instructions.add("fjp " + endIfLabel);
			} else {
				instructions.add("fjp " + elseLabel);
			}
			instructions.addAll(getBody().code());
			instructions.add("ujp " + endIfLabel);
			if(!(getElse() instanceof NothingNode)) {
				instructions.add(elseLabel + ":");
				instructions.addAll(getElse().code());
			}
			instructions.add(endIfLabel + ":");

			return instructions;
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
