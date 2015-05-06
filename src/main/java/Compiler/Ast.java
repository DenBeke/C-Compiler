package Compiler;

import java.util.Vector;

import Compiler.SymbolTableVisitor.FuncSymbol;
import Compiler.SymbolTableVisitor.Symbol;
import Compiler.SymbolTableVisitor.VarSymbol;

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

		public Vector<String> stringLiterals = new Vector<String>();
		
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
					((DeclarationNode) children.get(i)).symbol.offset = varDecls;
					varDecls += 1;
				} else if(children.get(i) instanceof FunctionDeclarationNode) {
					FunctionDeclarationNode fd = (FunctionDeclarationNode) children
							.get(i);
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
			
			// Store static strings
			for(int i = 0; i < stringLiterals.size(); i++) {
				for(int c = 0; c < stringLiterals.get(i).length(); c++) {
					instructions.add("ldc c " + (int)stringLiterals.get(i).charAt(c));
				}
				instructions.add("ldc c 0");

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
				return ((TypeNode) children.get(0))
						.equals((TypeNode) t.children.get(0));
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
	
	public static class VariadicTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String getStringRepresentation() {
			return "<variadic>";
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
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.add("ldc c " + (int)value.charValue());
			return instructions;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

	public static class StringNode extends LiteralNode {
		public String value;
		public int stringPosition = 0;

		public StringNode(String value) {
			this.value = value;
			type = new PointerTypeNode();
			type.addChild(0, new CharTypeNode());
		}


		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.add("ldc a " + Integer.toString(stringPosition));
			return instructions;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class IdNode extends ExpressionNode {
		public String id;

		private Symbol symbol;
		public FunctionDeclarationNode function;

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

			int depth = 0;
			if(((VarSymbol) symbol).declaration != null
					&& ((VarSymbol) symbol).declaration.function != function) {
				Node n = this;
				while(n != ((VarSymbol) symbol).declaration.function) {
					if(n instanceof FunctionDeclarationNode) {
						depth += 1;
					}

					n = n.parent;
				}
			}

			int offset = symbol.offset;
			instructions.add("lod " + CodeGenVisitor.typeToPtype(getType())
					+ " " + Integer.toString(depth) + " "
					+ Integer.toString(offset));

			return instructions;
		}

		@Override
		public Vector<String> codeL() {
			Vector<String> instructions = new Vector<String>();

			int depth = 0;
			if(((VarSymbol) symbol).declaration != null
					&& ((VarSymbol) symbol).declaration.function != function) {
				Node n = this;
				while(n != ((VarSymbol) symbol).declaration.function) {
					if(n instanceof FunctionDeclarationNode) {
						depth += 1;
					}

					n = n.parent;
				}
			}

			int offset = symbol.offset;
			instructions.add("lda " + Integer.toString(depth) + " "
					+ Integer.toString(offset));

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
		public FunctionDeclarationNode function;

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
				instructions.add("str " + CodeGenVisitor.typeToPtype(getType())
						+ " 0 " + Integer.toString(symbol.offset));
			}

			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = code();
			instructions.add("lod " + CodeGenVisitor.typeToPtype(getType()) + 0
					+ Integer.toString(symbol.offset));

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

	public static class FunctionDeclarationNode extends StatementNode {
		private static int functionCounter = 0;
		public FunctionDeclarationNode owner;

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
		

		private Vector<String> generateBuiltin() {
			if(id.equals("printf")) {
				return BuiltinCode.generatePrintf();
			} else if(id.equals("print")) {
				return BuiltinCode.generatePrint();
			} else if(id.equals("strcmp")) {
				return BuiltinCode.generateStrcmp();
			} else if(id.equals("scanf")) {
				return BuiltinCode.generateScanf();
			} else if(id.equals("isdigit")) {
				return BuiltinCode.generateIsdigit();
			} else if(id.equals("pow")) {
				return BuiltinCode.generatePow();
			} else if(id.equals("chartoint")) {
				return BuiltinCode.generateChartoint();
			} else if(id.equals("readstr")) {
				return BuiltinCode.generateReadstr();
			}  else {
				Log.fatal("Builtin function not found " + id, line);
			}
			
			return null;
		}
		
		@Override
		public Vector<String> code() {		
			Vector<String> instructions = new Vector<String>();

			if(symbol.builtin) {
				instructions.addAll(generateBuiltin());
			} else {
				instructions.add(symbol.label + ":");
				instructions.addAll(getBlock().code());
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
		public FunctionDeclarationNode owner;

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


			int depth = 0;
			if(symbol.declaration.owner != owner) {
				Node n = this;
				while(n != symbol.declaration.owner) {
					if(n instanceof FunctionDeclarationNode) {
						depth += 1;
					}

					n = n.parent;
				}
			}

			instructions.add("mst " + Integer.toString(depth));

			int nrParams = 0;
			if(symbol.variadic) {
				instructions.add("ldc i " + children.size());
				nrParams += 1;
			}
			for(int i = 0; i < children.size(); i++) {
				instructions.addAll(getParamExpression(i).codeR());
				nrParams += 1;
			}

			instructions.add("cup " + Integer.toString(nrParams) + " "
					+ symbol.label);

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

			instructions.add("cup " + Integer.toString(children.size()) + " "
					+ symbol.label);

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
			return (ExpressionNode) children.get(0);
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
			return (ExpressionNode) children.get(0);
		}

		@Override
		public Vector<String> codeL() {
			Vector<String> instructions = new Vector<String>();

			// Dereferencing an lvalue means we should put the address we point
			// to on the stack.
			// So generate the rvalue of the poiner
			instructions.addAll(getExpression().codeR());
			return instructions;
		}

		@Override
		public Vector<String> codeR() {
			Vector<String> instructions = new Vector<String>();

			instructions.addAll(getExpression().codeR());
			// The above code will put the address to which the pointer points
			// to on the stack.
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

	public static class PointerToPointerExpressionNode extends
			CastExpressionNode {
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

	public static int resolvePositions(Node a, int start) {
		int pos = start;
		for(int i = 0; i < a.children.size(); i++) {
			if(a.children.get(i) instanceof DeclarationNode) {
				DeclarationNode decl = (DeclarationNode) a.children.get(i);
				if(decl.symbol.offset == -1) {
					decl.symbol.offset = pos;
					pos++;
				}
			}

			if(!(a.children.get(i) instanceof FunctionDeclarationNode)) {
				pos = resolvePositions(a.children.get(i), pos);
			}
		}

		return pos;
	}

	public static class BlockStatementNode extends StatementNode {
		public void addStatement(int pos, StatementNode statement) {
			addChild(pos, statement);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			// String start = CodeGenVisitor.getUniqueLabel();
			// String end = CodeGenVisitor.getUniqueLabel();
			// instructions.add("ujp " + end);
			// instructions.add(start + ":");

			int staticDataSize = 5;
			if(parent instanceof FunctionDeclarationNode) {
				FunctionDeclarationNode p = (FunctionDeclarationNode) parent;
				for(int i = 0; i < p.getParams().children.size(); i++) {
					FormalParameterNode fp = (FormalParameterNode) p
							.getParams().children.get(i);
					fp.symbol.offset = staticDataSize;
					staticDataSize += 1;
				}
			}

			staticDataSize += resolvePositions(this, staticDataSize) - 5;

			if(parent instanceof FunctionDeclarationNode) {
				instructions.add("ssp " + Integer.toString(staticDataSize));
			}

			for(int i = 0; i < children.size(); i++) {
				boolean functionDecl = children.get(i) instanceof FunctionDeclarationNode;
				if(functionDecl) {
					String skip = CodeGenVisitor.getUniqueLabel();
					instructions.add("ujp " + skip);
					instructions.addAll(children.get(i).code());
					instructions.add(skip + ":");
				} else {
					instructions.addAll(children.get(i).code());
				}
			}

			if(parent instanceof FunctionDeclarationNode) {
				if(!(parent.children.lastElement() instanceof ReturnStatementNode)) {
					instructions.add("retp");
				}
			}

			return instructions;
		}

		/*
		 * @Override public Vector<String> code() { Vector<String> instructions
		 * = new Vector<String>();
		 * 
		 * 
		 * for(int i = 0; i < children.size(); i++) {
		 * instructions.addAll(children.get(i).code()); }
		 * 
		 * return instructions; }
		 */

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
			String childPType = CodeGenVisitor.typeToPtype(getLeftChild()
					.getType());

			if(pType == null || childPType == null) {
				Log.fatal("Cant convert type to ptype", line);
			}
			
			boolean math = operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/");
			boolean logical = operator.equals("&&") || operator.equals("||");

			if(operator.equals("=")) {
				instructions.addAll(getLeftChild().codeL());
				instructions.addAll(getRightChild().codeR());
			} else {
				instructions.addAll(getLeftChild().codeR());
				if(math) {
					instructions.add("conv " + CodeGenVisitor.typeToPtype(getLeftChild().getType()) + " i");
				} else if(logical) {
					instructions.add("conv i b");
				}
				
				instructions.addAll(getRightChild().codeR());
				if(math) {
					instructions.add("conv " + CodeGenVisitor.typeToPtype(getRightChild().getType()) + " i");
				} else if(logical) {
					instructions.add("conv i b");
				}
			}

			switch(operator) {
			case "=":
				instructions.add("sto "
						+ CodeGenVisitor.typeToPtype(getLeftChild().getType()));
				// Put assigned value back on stack
				instructions.addAll(getLeftChild().codeL());
				instructions.add("ind "
						+ CodeGenVisitor.typeToPtype(getLeftChild().getType()));
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
				instructions.add("add i");
				break;
			case "-":
				instructions.add("sub i");
				break;
			case "/":
				instructions.add("div i");
				break;
			case "*":
				instructions.add("mul i");
				break;
			case "&&":
				instructions.add("and");
				instructions.add("conv b i");
				break;
			case "||":
				instructions.add("or");
				instructions.add("conv b i");
				break;
			default:
				Log.fatal("Codegen invalid binary operator: " + operator, line);
			}

			if(math && !(getType() instanceof IntTypeNode)) {
				instructions.add("conv i " + CodeGenVisitor.typeToPtype(getType()));
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

			if(pType == null) {
				Log.fatal("Cant convert type to ptype", line);
			}
			
			switch(operator) {
			case "++":
				instructions.addAll(getExpression().codeL());
				instructions.addAll(getExpression().codeR());
				instructions.add("inc " + pType + " 1");
				instructions.add("sto " + pType);
				instructions.addAll(getExpression().codeR());
				break;
			case "--":
				instructions.addAll(getExpression().codeL());
				instructions.addAll(getExpression().codeR());
				instructions.add("dec " + pType + " 1");
				instructions.add("sto " + pType);
				instructions.addAll(getExpression().codeR());
				break;
			case "!":
				instructions.addAll(getExpression().codeR());
				instructions.add("conv i b");
				instructions.add("not");
				instructions.add("conv b i");
				break;
			default:
				Log.fatal("Codegen invalid unary operator: " + operator, line);
			}
			
			return instructions;
		}


		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ForStatementNode extends StatementNode {

        public String beginForLabel;
        public String endForLabel;

		public ForStatementNode(Node first, Node second, Node third,
				StatementNode body) {
			Assert.Assert(first instanceof NothingNode
					|| first instanceof ExpressionNode);
			Assert.Assert(second instanceof NothingNode
					|| second instanceof ExpressionNode);
			Assert.Assert(third instanceof NothingNode
					|| third instanceof ExpressionNode);

			addChild(0, body);
			addChild(0, third);
			addChild(0, second);
			addChild(0, first);
		}

        public Node getCondition() {
            return children.get(1);
        }

        public Node getBody() {
            return children.get(3);
        }

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

        @Override
        public Vector<String> code() {
            Vector<String> instructions = new Vector<String>();

            if(!(children.get(0) instanceof NothingNode)) {
                instructions.addAll(children.get(0).code());
            }

            //String beginForLabel = CodeGenVisitor.getUniqueLabel();
            //String endForLabel = CodeGenVisitor.getUniqueLabel();

            instructions.add(beginForLabel + ":");
            if(getCondition() instanceof NothingNode) {
                instructions.add("ldc b t");
            } else {
                instructions.addAll(getCondition().codeR());
                instructions.add("conv " + CodeGenVisitor.typeToPtype(((ExpressionNode)getCondition()).getType()) + " b");
            }
            instructions.add("fjp " + endForLabel);
            instructions.addAll(getBody().code());

            if(!(children.get(2) instanceof NothingNode)) {
                instructions.addAll(children.get(2).code());
            }

            instructions.add("ujp " + beginForLabel);

            instructions.add(endForLabel + ":");

            return instructions;
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
			return children.get(0);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			if(!(getExpression() instanceof NothingNode)) {
				instructions.addAll(((ExpressionNode) getExpression()).codeR());
				instructions.add("str "
						+ CodeGenVisitor
								.typeToPtype(((ExpressionNode) getExpression())
										.getType()) + " 0 0");
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

        public String beginWhileLabel;
        public String endWhileLabel;

		public WhileStatementNode(ExpressionNode condition, StatementNode body) {
			addChild(0, body);
			addChild(0, condition);
		}

        public ExpressionNode getCondition() {
            return (ExpressionNode)children.get(0);
        }

        public StatementNode getBody() {
            return (StatementNode)children.get(1);
        }

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

        @Override
        public Vector<String> code() {
            Vector<String> instructions = new Vector<String>();

            //String beginWhileLable = CodeGenVisitor.getUniqueLabel();
            //String endWhileLable = CodeGenVisitor.getUniqueLabel();

            instructions.add(beginWhileLabel + ":");
            instructions.addAll(getCondition().codeR());
            instructions.add("conv " + CodeGenVisitor.typeToPtype(getCondition().getType()) + " b");
            instructions.add("fjp " + endWhileLabel);
            instructions.addAll(getBody().code());
            instructions.add("ujp " + beginWhileLabel);
            instructions.add(endWhileLabel + ":");

            return instructions;
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

        public String endIfLabel;
        public String elseLabel;

		public ExpressionNode getCondition() {
			return (ExpressionNode) children.get(0);
		}

		public StatementNode getBody() {
			return (StatementNode) children.get(1);
		}

		public Node getElse() {
			return children.get(2);
		}

		@Override
		public Vector<String> code() {
			Vector<String> instructions = new Vector<String>();

			//String endIfLabel = CodeGenVisitor.getUniqueLabel();
			//String elseLabel = CodeGenVisitor.getUniqueLabel();

			instructions.addAll(getCondition().codeR());
			instructions.add("conv "
					+ CodeGenVisitor.typeToPtype(getCondition().getType())
					+ " b");
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

        public String label;

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

        @Override
        public Vector<String> code() {
            Vector<String> instructions = new Vector<String>();

            instructions.add("ujp " + label);

            return instructions;
        }

	}

	public static class ContinueStatementNode extends StatementNode {

        public String label;

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

        @Override
        public Vector<String> code() {
            Vector<String> instructions = new Vector<String>();

            instructions.add("ujp " + label);

            return instructions;
        }
	}

}
