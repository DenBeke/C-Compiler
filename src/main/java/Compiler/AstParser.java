package Compiler;

import java.util.LinkedList;
import java.util.Vector;

import org.antlr.v4.runtime.*;

public class AstParser extends CParser {

    public static abstract class Node {
		public int scope;
		public Vector<Node> children = new Vector<Node>();

		public String childrenToString(String prefix) {
			String result = "";

			for (int i = 0; i < children.size(); i++) {
				result += children.get(i).toString(prefix) + "\n";
			}

			return result;
		}

		public String toString() {
            return toString("");
		}

        public Boolean hasChildren() {
            return children.size() > 0;
        }

        public void insertLefMostLeaf(Node n) {
            if(hasChildren()) {
                children.get(0).insertLefMostLeaf(n);
            }
            else {
                children.add(0, n);
            }
        }

		public abstract String toString(String prefix);

	}

	public static class FileNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "FileNode\n";
			result += childrenToString(prefix + "\t");

			return result;
		}

	}


    public static class TypeNode extends Node {
        public String typeName;
        public Boolean constant;

        public String toString(String prefix) {
            String result = prefix + "TypeNode: " + typeName;
            if(constant) {
                result += " const\n";
            } else {
                result += "\n";
            }
            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class ConstNode extends TypeNode {};

    public static abstract class LiteralNode extends Node {
    }

	public static class IntNode extends LiteralNode {
		public Integer value;

		public String toString(String prefix) {
			String result = prefix + "IntNode: " + String.valueOf(value);
			return result;
		}

	}

    public static class CharNode extends LiteralNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "CharNode: " + String.valueOf(value);
            return result;
        }

    }

    public static class StringNode extends LiteralNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "StringNode: " + String.valueOf(value);
            return result;
        }

    }

	public static class IdNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "IdNode: " + id;
			return result;
		}
	}

	public static class DeclarationNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "DeclarationNode: " + id + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class FunctionDeclarationNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "FunctionDeclarationNode: " + id + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class FormalParametersNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "FormalParametersNode:\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class FormalParameterNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "FormalParameterNode: " + id;
			return result;
		}
	}


    public static class ParamNode extends Node {
        public String toString(String prefix) {
            String result = prefix + "ParamNode:\n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class FunctionCallNode extends ExpressionNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "FunctionCallNode: " + String.valueOf(value) + "\n";
            result += childrenToString(prefix + "\t");
            return result;
        }

    }


	public static abstract class StatementNode extends Node {
	}

    public static abstract class ExpressionNode extends Node {
    }

	public static class BlockStatementNode extends StatementNode {
		public String toString(String prefix) {
			String result = prefix + "BlockStatementNode:\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class ExprStatementNode extends StatementNode {
		public String toString(String prefix) {
			String result = prefix + "ExprStatementNode: \n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class BinaryOperatorNode extends ExpressionNode {
		public String operator;

		public String toString(String prefix) {
			String result = prefix + "BinaryOperatorNode: " + operator + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}
	
	public static class UnaryOperatorNode extends ExpressionNode {
		public String operator;

		public String toString(String prefix) {
			String result = prefix + "UnaryOperatorNode: " + operator + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class ForStatementNode extends StatementNode {
		public String toString(String prefix) {
			String result = prefix + "ForStatementNode:\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class NothingNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "NothingNode";
			return result;
		}
	}

    public static class ReturnStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "ReturnStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class WhileStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "WhileStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class IfStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "IfStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }


    public static class BreakStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "BreakStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class ContinueStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "ContinueStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
    }


	private Node root;
	private LinkedList<Node> list;
	private int scope = 0;

	public AstParser(TokenStream input) {
		super(input);
	}

	public void handleVarDecl(String id) {
		System.out.println("handleVarDecl: " + id);

		DeclarationNode node = new DeclarationNode();
		node.id = id;

		// We have an initializer
		if (list.size() >= 1) {
			node.children.add(list.removeFirst());
		}

        // type
        Assert.Assert(list.peekFirst() instanceof TypeNode);
        node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

	public void handleFuncDecl(String id) {
		System.out.println("handleFuncDecl: " + id);

		FunctionDeclarationNode node = new FunctionDeclarationNode();
		node.id = id;

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
		System.out.println("handleFormalParameters");

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
		System.out.println("handleFormalParameter: " + id);

		FormalParameterNode node = new FormalParameterNode();
		node.id = id;

		insertNode(0, node);
	}

	public void handleBlock() {
		System.out.println("handleBlock");

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
		System.out.println("handleExpr");
	}

	public void handleExprStatement() {
		System.out.println("handleExprStatement");

		ExprStatementNode node = new ExprStatementNode();
		node.children.add(list.removeFirst());

		insertNode(0, node);
	}

	public void handleInt(String n) {
		System.out.println("handleInt " + n);

		IntNode node = new IntNode();
		node.value = Integer.parseInt(n);
		insertNode(0, node);

	}

    public void handleChar(String n) {
        System.out.println("handleChar " + n);

        CharNode node = new CharNode();
        node.value = n.substring(1, n.length()-1);
        insertNode(0, node);

    }

    public void handleString(String n) {
        System.out.println("handleString " + n);

        StringNode node = new StringNode();
        node.value = n.substring(1, n.length()-1);
        insertNode(0, node);

    }


    public void handleParam() {
        System.out.println("handleParam");

        ParamNode node = new ParamNode();

        Assert.Assert(list.peekFirst() instanceof ExpressionNode);
        node.children.add(0, list.removeFirst());

        insertNode(0, node);

    }


    public void handleFunctionCall(String n) {
        System.out.println("handleFunctionCall " + n);

        FunctionCallNode node = new FunctionCallNode();
        node.value = n;

        while (list.peekFirst() instanceof ParamNode) {
            node.children.add(0, list.removeFirst());
        }

        insertNode(0, node);

    }


	public void handleBinaryOperator(String operator) {
		System.out.println("handleBinaryOperator: " + operator);

		BinaryOperatorNode node = new BinaryOperatorNode();

		// There should be atleast 2 operands
		if (list.size() < 2) {
			System.out.println("There should atleast be 2 operands on the stack");
		}

		node.operator = operator;

		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	};
	
	public void handleUnaryOperator(String operator) {
		System.out.println("handleUnaryOperator: " + operator);

		UnaryOperatorNode node = new UnaryOperatorNode();

		// There should be atleast 1 operand
		if (list.size() < 1) {
			System.out.println("There should atleast be 1 operands on the stack");
		}

		node.operator = operator;

		node.children.add(0, list.removeFirst());
		
		insertNode(0, node);
	};

	public void handleID(String id) {
		System.out.println("handleID: " + id);

		IdNode node = new IdNode();
		node.id = id;

		insertNode(0, node);
	}


    public void handleType(String t) {
        System.out.println("handleType: " + t);

        TypeNode node = new TypeNode();
        node.typeName = t;
        node.constant = false;

        if(list.peekFirst() instanceof NothingNode) {
            list.removeFirst();
        }

        if(list.peekFirst() instanceof ConstNode) {
            node.constant = true;
            list.removeFirst();
        }

        if(list.peekFirst() instanceof TypeNode && ((TypeNode) list.peekFirst()).typeName == "pointer") {
            list.get(0).insertLefMostLeaf(node);
        }
        else {
            //System.out.println("    Inserting node");
            insertNode(0, node);
        }

    }

    public void handleConst() {
        System.out.println("handleConst");

        if(list.peekFirst() instanceof NothingNode) {
            list.removeFirst();
        }

        ConstNode node = new ConstNode();
        insertNode(0, node);

    }

    public void handlePointer() {
        System.out.println("handlePointer");

        TypeNode node = new TypeNode();
        node.typeName = "pointer";
        node.constant = false;

        if(list.peekFirst() instanceof NothingNode) {
            list.removeFirst();
        }

        if(list.peekFirst() instanceof ConstNode) {
            node.constant = true;
            list.removeFirst();
        }

        if(list.peekFirst() instanceof TypeNode) {
            list.get(0).insertLefMostLeaf(node);
        }
        else {
            insertNode(0, node);
        }
    }

	
	public void handleForStatement() {
		System.out.println("handleForStatement");

		ForStatementNode node = new ForStatementNode();
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());
		node.children.add(0, list.removeFirst());

		insertNode(0, node);
	}

    public void handleReturnStatement() {
        System.out.println("handleReturnStatement");

        ReturnStatementNode node = new ReturnStatementNode();
        node.children.add(0, list.removeFirst());

        insertNode(0, node);
    }

    public void handleBreakStatement() {
        System.out.println("handleBreakStatement");

        BreakStatementNode node = new BreakStatementNode();

        insertNode(0, node);
    }

    public void handleContinueStatement() {
        System.out.println("handleContinueStatement");

        ContinueStatementNode node = new ContinueStatementNode();

        insertNode(0, node);
    }

    public void handleWhileStatement() {
        System.out.println("handleWhileStatement");

        WhileStatementNode node = new WhileStatementNode();

        System.out.println(list.peekFirst().toString());
        Assert.Assert(list.peekFirst() instanceof StatementNode);
        node.children.add(0, list.removeFirst());
        System.out.println(list.peekFirst().toString());
        Assert.Assert(list.peekFirst() instanceof ExpressionNode);
        node.children.add(0, list.removeFirst());

        insertNode(0, node);
    }

    public void handleIfStatement() {
        System.out.println("handleIfStatement");

        IfStatementNode node = new IfStatementNode();

        // else
        Assert.Assert(list.peekFirst() instanceof StatementNode || list.peekFirst() instanceof NothingNode);
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
		System.out.println("handleNothing");

		NothingNode node = new NothingNode();

		insertNode(0, node);
	}

	public void handleFile() {
		System.out.println("handleFile");

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
			System.out.println("Stack should contain 1 element");
		} else {
			root = list.removeFirst();
			if (!(root instanceof FileNode)) {
				System.out.println("Root should be a FileNode");
			}
		}

		System.out.println(root.toString());
		return root;
	};
}
