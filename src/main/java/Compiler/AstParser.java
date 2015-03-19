package Compiler;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.*;

import Compiler.Ast.*;

public class AstParser extends CParser {
	private Node root;
	private LinkedList<Node> list;
	private int scope = 0;
	private static Logger log;

	public AstParser(TokenStream input) {
		super(input);
		log = Logger.getLogger(AstParser.class.getName());
		log.setLevel(Level.OFF);
	}

	public void handleVarDecl(String id) {
		log.log(Level.INFO, "handleVarDecl: " + id);

		DeclarationNode node = new DeclarationNode();
		node.id = id;

		// We have an initializer
		if (list.size() >= 1 && !(list.peekFirst() instanceof TypeNode)) {
			node.children.add(list.removeFirst());
		}

		// type
		Assert.Assert(list.peekFirst() instanceof TypeNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleFuncDecl(String id) {
		log.log(Level.INFO, "handleFuncDecl: " + id);

		FunctionDeclarationNode node = new FunctionDeclarationNode();
		node.id = id;

		// log.log(Level.INFO, list);

		// Add empty formal parameter list if no parameters.
		if (list.size() < 2 || !(list.get(1) instanceof FormalParametersNode)) {
			node.children.add(new FormalParametersNode());
		} else {
			node.children.add(list.remove(1));
		}

		// Add block
		node.children.add(list.removeFirst());

		// Add type
		Assert.Assert(list.peekFirst() instanceof TypeNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleFormalParameters() {
		log.log(Level.INFO, "handleFormalParameters");

		FormalParametersNode node = new FormalParametersNode();

		// Add all Formalparameters to our children
		Node paramNode;
		while ((paramNode = list.peekFirst()) != null) {
			if (!(paramNode instanceof FormalParameterNode)) {
				break;
			}

			list.removeFirst();
			node.children.add(0, paramNode);
		}

		insertNode(0, node);
	}

	public void handleFormalParameter(String id) {
		log.log(Level.INFO, "handleFormalParameter: " + id);

		FormalParameterNode node = new FormalParameterNode();
		node.id = id;

		insertNode(0, node);
	}

	public void handleBlock() {
		log.log(Level.INFO, "handleBlock");

		BlockStatementNode node = new BlockStatementNode();

		// Add all statements in our scope.
		Node stmtNode;
		while ((stmtNode = list.peekFirst()) != null) {
			if (!(stmtNode instanceof StatementNode)
					|| stmtNode.scope != scope + 1) {
				break;
			}

			list.removeFirst();
			node.children.add(0, stmtNode);
		}

		insertNode(0, node);
	}

	public void handleExpr() {
		log.log(Level.INFO, "handleExpr");
	}

	public void handleExprStatement() {
		log.log(Level.INFO, "handleExprStatement");

		ExprStatementNode node = new ExprStatementNode();
		node.children.add(list.removeFirst());

		insertNode(0, node);
	}

	public void handleInt(String n) {
		log.log(Level.INFO, "handleInt " + n);

		IntNode node = new IntNode();
		node.value = Integer.parseInt(n);
		insertNode(0, node);

	}

	public void handleChar(String n) {
		log.log(Level.INFO, "handleChar " + n);

		CharNode node = new CharNode();
		node.value = n.substring(1, n.length() - 1);
		insertNode(0, node);

	}

	public void handleString(String n) {
		log.log(Level.INFO, "handleString " + n);

		StringNode node = new StringNode();
		node.value = n.substring(1, n.length() - 1);
		insertNode(0, node);

	}

	public void handleParam() {
		log.log(Level.INFO, "handleParam");

		ParamNode node = new ParamNode();

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);

	}

	public void handleFunctionCall(String n) {
		log.log(Level.INFO, "handleFunctionCall " + n);

		FunctionCallNode node = new FunctionCallNode();
		node.value = n;

		while (list.peekFirst() instanceof ParamNode) {
			node.children.add(0, list.removeFirst());
		}

		insertNode(0, node);

	}

	public void handleBinaryOperator(String operator) {
		log.log(Level.INFO, "handleBinaryOperator: " + operator);

		BinaryOperatorNode node = new BinaryOperatorNode();

		// There should be atleast 2 operands
		if (list.size() < 2) {
			log.log(Level.INFO,
					"There should atleast be 2 operands on the stack");
		}

		node.operator = operator;

		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	};

	public void handleUnaryOperator(String operator) {
		log.log(Level.INFO, "handleUnaryOperator: " + operator);

		UnaryOperatorNode node = new UnaryOperatorNode();

		// There should be atleast 1 operand
		if (list.size() < 1) {
			log.log(Level.INFO,
					"There should atleast be 1 operands on the stack");
		}

		node.operator = operator;

		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	};

	public void handleID(String id) {
		log.log(Level.INFO, "handleID: " + id);

		IdNode node = new IdNode();
		node.id = id;

		insertNode(0, node);
	}

	public void handleType(String t, String c) {
		log.log(Level.INFO, "handleType: " + t + " c=" + c);

		TypeNode node = null;

		switch (t.toLowerCase()) {
		case "int":
			node = new IntTypeNode();
			break;
		case "char":
			node = new CharTypeNode();
			break;
		case "void":
			node = new VoidTypeNode();
			break;
		default:
			Assert.Assert(false);
		}

		node.constant = false;
		node.topLevel = false;

		if (c != null) {
			if (c.equals("const")) {
				node.constant = true;
			}
		}

		if (list.peekFirst() instanceof NothingNode) {
			list.removeFirst();
			insertNode(0, node);
			return;
		}

		if (list.peekFirst() instanceof ConstTypeNode) {
			node.constant = true;
			list.removeFirst();

			// check again if it wasn't the last type on the stack
			if (list.peekFirst() instanceof NothingNode) {
				list.removeFirst();
				insertNode(0, node);
				return;
			}
		}

		if (list.peekFirst() instanceof PointerTypeNode
				&& !((TypeNode) list.peekFirst()).topLevel) {
			list.get(0).insertLefMostLeaf(node);
			((TypeNode) list.get(0)).topLevel = true;
		} else {
			// log.log(Level.INFO, "    Inserting node");
			insertNode(0, node);
		}
	}

	public void handleStaticArray(String n) {
		log.log(Level.INFO, "handleStaticArray: " + n);

		StaticArrayTypeNode node = new StaticArrayTypeNode();
		node.size = Integer.parseInt(n);
		node.topLevel = true;

		Assert.Assert(list.peekFirst() instanceof TypeNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleConst() {
		log.log(Level.INFO, "handleConst");

		if (list.peekFirst() instanceof NothingNode) {
			// list.removeFirst();
		}

		ConstTypeNode node = new ConstTypeNode();
		insertNode(0, node);

	}

	public void handlePointer() {
		log.log(Level.INFO, "handlePointer");

		PointerTypeNode node = new PointerTypeNode();
		node.constant = false;
		node.topLevel = false;

		// just insert the node if it's the last pointer on the stack
		if (list.peekFirst() instanceof NothingNode) {
			list.removeFirst();
			insertNode(0, node);
			return;
		}

		// check if it is constant
		if (list.peekFirst() instanceof ConstTypeNode) {
			node.constant = true;
			list.removeFirst();

			// check again if it wasn't the last pointer on the stack
			if (list.peekFirst() instanceof NothingNode) {
				list.removeFirst();
				insertNode(0, node);
				return;
			}
		}

		if (list.peekFirst() instanceof PointerTypeNode) {
			list.get(0).insertLefMostLeaf(node);
		} else {
			insertNode(0, node);
		}
	}

	public void handleForStatement() {
		log.log(Level.INFO, "handleForStatement");

		ForStatementNode node = new ForStatementNode();
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleReturnStatement() {
		log.log(Level.INFO, "handleReturnStatement");

		ReturnStatementNode node = new ReturnStatementNode();
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleBreakStatement() {
		log.log(Level.INFO, "handleBreakStatement");

		BreakStatementNode node = new BreakStatementNode();

		insertNode(0, node);
	}

	public void handleContinueStatement() {
		log.log(Level.INFO, "handleContinueStatement");

		ContinueStatementNode node = new ContinueStatementNode();

		insertNode(0, node);
	}

	public void handleWhileStatement() {
		log.log(Level.INFO, "handleWhileStatement");

		WhileStatementNode node = new WhileStatementNode();

		log.log(Level.INFO, list.peekFirst().toString());
		Assert.Assert(list.peekFirst() instanceof StatementNode);
		node.children.add(0, list.removeFirst());
		log.log(Level.INFO, list.peekFirst().toString());
		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleIfStatement() {
		log.log(Level.INFO, "handleIfStatement");

		IfStatementNode node = new IfStatementNode();

		// else
		Assert.Assert(list.peekFirst() instanceof StatementNode
				|| list.peekFirst() instanceof NothingNode);
		node.children.add(0, list.removeFirst());

		// if block/stmt
		Assert.Assert(list.peekFirst() instanceof StatementNode);
		node.children.add(0, list.removeFirst());

		// if condition/expr
		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleNothing() {
		log.log(Level.INFO, "handleNothing");

		NothingNode node = new NothingNode();

		insertNode(0, node);
	}

	public void handleFile() {
		log.log(Level.INFO, "handleFile");

		FileNode node = new FileNode();

		while (list.size() != 0) {
			node.children.add(0, list.removeFirst());
		}

		insertNode(0, node);
	};

	public void startScope() {
		scope += 1;
	}

	public void endScope() {
		scope -= 1;
	}

	private void insertNode(int pos, Node node) {
		node.scope = scope;
		list.add(pos, node);
	}

	/**
	 * Build the ast
	 */
	public Node buildAst() {
		// Reset ast
		root = null;
		list = new LinkedList<Node>();

		// Start the parsing
		start();

		// FileNode should be the only 1 on the stack
		if (list.size() != 1) {
			log.log(Level.INFO, "Stack should contain 1 element");
		} else {
			root = list.removeFirst();
			if (!(root instanceof FileNode)) {
				log.log(Level.INFO, "Root should be a FileNode");
			}
		}

		System.out.println(root.toString());
		return root;
	};
}
