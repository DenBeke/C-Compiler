package Compiler;

import java.util.ArrayDeque;
import java.util.Vector;

import org.antlr.v4.runtime.*;

public class AstParser extends CParser {
	public static abstract class Node {
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

		public abstract String toString(String prefix);

	}

	public static class FileNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "FileNode\n";
			result += childrenToString(prefix + "\t");

			return result;
		}

	}

	public static class IntNode extends Node {
		public Integer value;

		public String toString(String prefix) {
			String result = prefix + "IntNode: " + String.valueOf(value);
			return result;
		}

	}

	public static class DeclarationNode extends Node {
		public String variable;

		public String toString(String prefix) {
			String result = prefix + "DeclarationNode: " + variable + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	public static class FunctionDeclarationNode extends Node {
		public String name;

		public String toString(String prefix) {
			String result = prefix + "FunctionDeclarationNode: " + name + "\n";
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

	public static class BinaryOperatorNode extends Node {
		public String operator;

		public String toString(String prefix) {
			String result = prefix + "BinaryOperatorNode: " + operator + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
	}

	private Node root;
	private ArrayDeque<Node> queue;

	public AstParser(TokenStream input) {
		super(input);
	}

	public void handleVarDecl(String id) {
		// System.out.println("handleVarDecl: " + id);

		DeclarationNode node = new DeclarationNode();
		node.variable = id;

		// We have an initializer
		if (queue.size() == 1) {
			node.children.add(queue.remove());
		}

		queue.add(node);
	}

	public void handleFuncDecl(String id) {
		// System.out.println("handleFuncDecl: " + id);

		FunctionDeclarationNode node = new FunctionDeclarationNode();
		node.name = id;

		// Add empty formal parameter list if no parameters.
		if (!(queue.peek() instanceof FormalParametersNode)) {
			node.children.add(new FormalParametersNode());
		} else {
			node.children.add(queue.remove());
		}

		queue.add(node);
	}

	public void handleFormalParameters() {
		// System.out.println("handleFormalParameters: " + id);

		FormalParametersNode node = new FormalParametersNode();

		// Add all Formalparameters to our children
		Node paramNode;
		while ((paramNode = queue.peek()) != null) {
			if (!(paramNode instanceof FormalParameterNode)) {
				break;
			}

			queue.remove();
			node.children.add(paramNode);
		}

		queue.add(node);
	}

	public void handleFormalParameter(String id) {
		// System.out.println("handleFormalParameter: " + id);

		FormalParameterNode node = new FormalParameterNode();
		node.id = id;

		queue.add(node);
	}

	public void handleExpr() {
		// System.out.println("handleExpr");
	}

	public void handleInt(String n) {
		// System.out.println("handleInt " + n);

		IntNode node = new IntNode();
		node.value = Integer.parseInt(n);
		queue.add(node);

	}

	public void handleBinaryOperator(String operator) {
		// System.out.println("handleBinaryOperator: " + operator);

		BinaryOperatorNode node = new BinaryOperatorNode();

		// There should be atleast 2 operands
		if (queue.size() < 2) {
			System.out.println("There should atleast be 2 operands on the stack");
		}

		node.operator = operator;

		node.children.add(queue.remove());
		node.children.add(queue.remove());

		queue.add(node);
	};

	public void handleID(String id) {
		// System.out.println("handleID: " + id);
	}

	public void handleFile() {
		// System.out.println("handleFile");

		FileNode node = new FileNode();

		while (queue.size() != 0) {
			node.children.add(queue.remove());
		}

		queue.add(node);
	};

	/**
	 * Build the ast
	 */
	public void buildAst() {
		// Reset ast
		root = null;
		queue = new ArrayDeque<Node>();

		// Start the parsing
		start();

		// FileNode should be the only 1 on the stack
		if (queue.size() != 1) {
			System.out.println("Stack should contain 1 element");
		} else {
			root = queue.remove();
			if (!(root instanceof FileNode)) {
				System.out.println("Root should be a FileNode");
			}
		}

		System.out.println(root.toString());
	};
}
