package Compiler;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

public class CodeGenVisitor extends Visitor {
	private Vector<String> instructions = new Vector<String>();

	public static String typeToPtype(Ast.TypeNode t) {
		if(t.equals(new Ast.IntTypeNode())) {
			return "i";
		}

		if(t.equals(new Ast.CharTypeNode())) {
			return "c";
		}

		if(t.equals(new Ast.PointerTypeNode())) {
			return "a";
		}

		return "";
	}

	public void visit(Ast.FileNode node) {
		visitChildren(node);
		System.out.println(instructions.toString());
	}

	public void visit(Ast.DeclarationNode node) {
		instructions.addAll(node.code());
	}

	public void visit(Ast.FunctionDeclarationNode node) {
		instructions.addAll(node.code());
	}
}
