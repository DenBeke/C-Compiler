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
		log.setLevel(Level.ALL);
	}


    /**
     * Handle variable declaration
     *
     * @param id
     */
	public void handleVarDecl(String id) {
		log.log(Level.INFO, "handleVarDecl: " + id);

		// Initializer
		Ast.Node initializer =  list.removeFirst();

		// type
		Assert.Assert(list.peekFirst() instanceof TypeNode);
		TypeNode type = (TypeNode)list.removeFirst();
		
		DeclarationNode node = new DeclarationNode(id, type, initializer);
		insertNode(0, node);
	}
		

    /**
     * Handle function declaration
     *
     * @param id
     */
	public void handleFuncDecl(String id) {
		log.log(Level.INFO, "handleFuncDecl: " + id);

		FormalParametersNode params = null;
		// Add empty formal parameter list if no parameters.
		if(list.size() < 2 || !(list.get(1) instanceof FormalParametersNode)) {
			params = new FormalParametersNode();
		} else {
			params = (FormalParametersNode)list.remove(1);
		}

		// Block
		Assert.Assert(list.peekFirst() instanceof BlockStatementNode);
		BlockStatementNode body = (BlockStatementNode)list.removeFirst();

		// Return type
		Assert.Assert(list.peekFirst() instanceof TypeNode);
		TypeNode returnType = (TypeNode)list.removeFirst();
		
		FunctionDeclarationNode node = new FunctionDeclarationNode(id, returnType, params, body);

		insertNode(0, node);
	}


    /**
     * Handle formal parameters
     * (of a function declaration)
     */
	public void handleFormalParameters() {
		log.log(Level.INFO, "handleFormalParameters");

		FormalParametersNode node = new FormalParametersNode();

		// Add all Formalparameters to our children
		Node paramNode;
		while((paramNode = list.peekFirst()) != null) {
			if(!(paramNode instanceof FormalParameterNode)) {
				break;
			}

			FormalParameterNode param = (FormalParameterNode)list.removeFirst();
			node.addParam(0, param);
		}

		insertNode(0, node);
	}


    /**
     * Handle one single formal parameter
     *
     * @param id
     */
	public void handleFormalParameter(String id) {
		log.log(Level.INFO, "handleFormalParameter: " + id);

		Assert.Assert(list.peekFirst() instanceof TypeNode);
		TypeNode type = (TypeNode)list.removeFirst();

		FormalParameterNode node = new FormalParameterNode(id, type);
		
		insertNode(0, node);
	}


    /**
     * Handle block
     */
	public void handleBlock() {
		log.log(Level.INFO, "handleBlock");

		BlockStatementNode node = new BlockStatementNode();

		// Add all statements in our scope.
		Node stmtNode;
		while((stmtNode = list.peekFirst()) != null) {
			if(!(stmtNode instanceof StatementNode)
					|| stmtNode.scope != scope + 1) {
				break;
			}

			node.addStatement(0, (StatementNode)list.removeFirst());
		}

		insertNode(0, node);
	}


    /**
     * Handle expression
     */
	public void handleExpr() {
		log.log(Level.INFO, "handleExpr");
	}


    /**
     * Handle expression statement
     */
	public void handleExprStatement() {
		log.log(Level.INFO, "handleExprStatement");

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ExprStatementNode node = new ExprStatementNode((ExpressionNode)list.removeFirst());

		insertNode(0, node);
	}


    /**
     * Handle int literal
     *
     * @param n
     */
	public void handleInt(String n) {
		log.log(Level.INFO, "handleInt " + n);

		IntNode node = new IntNode(Integer.parseInt(n));
		insertNode(0, node);
	}


    /**
     * Handle char literal
     *
     * @param n
     */
	public void handleChar(String n) {
		log.log(Level.INFO, "handleChar " + n);

		CharNode node = new CharNode(n.charAt(0));
		insertNode(0, node);
	}


    /**
     * Handle string literal
     *
     * @param n
     */
	public void handleString(String n) {
		log.log(Level.INFO, "handleString " + n);

		StringNode node = new StringNode(n.substring(1, n.length() - 1));
		insertNode(0, node);
	}


    /**
     * Handle function parameter
     */
	public void handleParam() {
		log.log(Level.INFO, "handleParam");

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ParamNode node = new ParamNode((ExpressionNode)list.removeFirst());
		
		insertNode(0, node);
	}


    /**
     * Handle function call
     *
     * @param n
     */
	public void handleFunctionCall(String n) {
		log.log(Level.INFO, "handleFunctionCall " + n);

		FunctionCallNode node = new FunctionCallNode(n);

		while(list.peekFirst() instanceof ParamNode) {
			node.addParam(0, (ParamNode)list.removeFirst());
		}

		insertNode(0, node);

	}


    /**
     * Handle binary operator
     *
     * @param operator
     */
	public void handleBinaryOperator(String operator) {
		log.log(Level.INFO, "handleBinaryOperator: " + operator);

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ExpressionNode left = (ExpressionNode)list.removeFirst();
		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ExpressionNode right = (ExpressionNode)list.removeFirst();
		
		BinaryOperatorNode node = new BinaryOperatorNode(operator, left, right);

		insertNode(0, node);
	}


    /**
     * Handle unary operator
     *
     * @param operator
     */
	public void handleUnaryOperator(String operator) {
		log.log(Level.INFO, "handleUnaryOperator: " + operator);

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		UnaryOperatorNode node = new UnaryOperatorNode(operator, (ExpressionNode)list.removeFirst());

		insertNode(0, node);
	}


    /**
     * Handle identifier
     *
     * @param id
     */
	public void handleID(String id) {
		log.log(Level.INFO, "handleID: " + id);

		IdNode node = new IdNode(id);

		insertNode(0, node);
	}


    /**
     * Handle type
     *
     * @param t: type name
     * @param c: const
     */
	public void handleType(String t, String c) {
		log.log(Level.INFO, "handleType: " + t + " c=" + c);

		TypeNode node = null;

		switch(t.toLowerCase()) {
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

		if(c != null) {
			if(c.equals("const")) {
				node.constant = true;
			}
		}

		if(list.peekFirst() instanceof NothingNode) {
			list.removeFirst();
			insertNode(0, node);
			return;
		}

		if(list.peekFirst() instanceof ConstTypeNode) {
			node.constant = true;
			list.removeFirst();

			// check again if it wasn't the last type on the stack
			if(list.peekFirst() instanceof NothingNode) {
				list.removeFirst();
				insertNode(0, node);
				return;
			}
		}

		if(list.peekFirst() instanceof PointerTypeNode
				&& !((TypeNode) list.peekFirst()).topLevel) {
			list.get(0).insertLefMostLeaf(node);
			((TypeNode) list.get(0)).topLevel = true;
		} else {
			// log.log(Level.INFO, "    Inserting node");
			insertNode(0, node);
		}
	}


    /**
     * Handle static array
     *
     * @param n
     */
	public void handleStaticArray(String n) {
		log.log(Level.INFO, "handleStaticArray: " + n);

		Assert.Assert(list.peekFirst() instanceof TypeNode);
		StaticArrayTypeNode node = new StaticArrayTypeNode(Integer.parseInt(n), (TypeNode)list.removeFirst());
		node.topLevel = true;

		insertNode(0, node);
	}


    /**
     * Handle const keyword in type
     */
	public void handleConst() {
		log.log(Level.INFO, "handleConst");

		ConstTypeNode node = new ConstTypeNode();
		insertNode(0, node);
	}


    /**
     * Handle pointer in type
     */
	public void handlePointer() {
		log.log(Level.INFO, "handlePointer");

		PointerTypeNode node = new PointerTypeNode();
		node.constant = false;
		node.topLevel = false;

		// just insert the node if it's the last pointer on the stack
		if(list.peekFirst() instanceof NothingNode) {
			list.removeFirst();
			insertNode(0, node);
			return;
		}

		// check if it is constant
		if(list.peekFirst() instanceof ConstTypeNode) {
			node.constant = true;
			list.removeFirst();

			// check again if it wasn't the last pointer on the stack
			if(list.peekFirst() instanceof NothingNode) {
				list.removeFirst();
				insertNode(0, node);
				return;
			}
		}

		if(list.peekFirst() instanceof PointerTypeNode) {
			list.get(0).insertLefMostLeaf(node);
		} else {
			insertNode(0, node);
		}
	}


    /**
     * Handle for statement
     */
	public void handleForStatement() {
		log.log(Level.INFO, "handleForStatement");

		Assert.Assert(list.peekFirst() instanceof StatementNode);
		StatementNode body = (StatementNode)list.removeFirst();
		Node third = list.removeFirst();
		Node second = list.removeFirst();
		Node first = list.removeFirst();
		ForStatementNode node = new ForStatementNode(first, second, third, body);
				
		insertNode(0, node);
	}


    /**
     * Handle return statement
     */
	public void handleReturnStatement() {
		log.log(Level.INFO, "handleReturnStatement");

		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ReturnStatementNode node = new ReturnStatementNode((ExpressionNode)list.removeFirst());

		insertNode(0, node);
	}


    /**
     * Handle break statement
     */
	public void handleBreakStatement() {
		log.log(Level.INFO, "handleBreakStatement");

		BreakStatementNode node = new BreakStatementNode();

		insertNode(0, node);
	}


    /**
     * Handle continue statement
     */
	public void handleContinueStatement() {
		log.log(Level.INFO, "handleContinueStatement");

		ContinueStatementNode node = new ContinueStatementNode();

		insertNode(0, node);
	}


    /**
     * Handle while statement
     */
	public void handleWhileStatement() {
		log.log(Level.INFO, "handleWhileStatement");

		Assert.Assert(list.peekFirst() instanceof StatementNode);
		StatementNode body = (StatementNode)list.removeFirst();
		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ExpressionNode condition = (ExpressionNode)list.removeFirst();
		
		WhileStatementNode node = new WhileStatementNode(condition, body);

		insertNode(0, node);
	}


    /**
     * Handle if statement
     */
	public void handleIfStatement() {
		log.log(Level.INFO, "handleIfStatement");

		Node elseBody = list.removeFirst();
		Assert.Assert(list.peekFirst() instanceof StatementNode);
		StatementNode body = (StatementNode)list.removeFirst();
		Assert.Assert(list.peekFirst() instanceof ExpressionNode);
		ExpressionNode condition = (ExpressionNode)list.removeFirst();
		
		IfStatementNode node = new IfStatementNode(condition, body, elseBody);

		insertNode(0, node);
	}


    /**
     * Handle 'nothing'
     */
	public void handleNothing() {
		log.log(Level.INFO, "handleNothing");

		NothingNode node = new NothingNode();

		insertNode(0, node);
	}


    /**
     * Handle file
     */
	public void handleFile() {
		log.log(Level.INFO, "handleFile");

		FileNode node = new FileNode();

		while(list.size() != 0) {
			node.addDeclaration(0, list.removeFirst());
		}

		insertNode(0, node);
	}

	public void startScope() {
		scope += 1;
	}

	public void endScope() {
		scope -= 1;
	}

	private void insertNode(int pos, Node node) {
		node.scope = scope;
		node.line = getCurrentToken().getLine();
		list.add(pos, node);
	}


	/**
	 * Build the AST
	 */
	public Node buildAst() {
		// Reset ast
		root = null;
		list = new LinkedList<Node>();

		// Start the parsing
		start();

		// FileNode should be the only 1 on the stack
		if(list.size() != 1) {
			log.log(Level.INFO, "Stack should contain 1 element");
		} else {
			root = list.removeFirst();
			if(!(root instanceof FileNode)) {
				log.log(Level.INFO, "Root should be a FileNode");
			}
		}

		System.out.println(root.toString());
		return root;
	};
}
