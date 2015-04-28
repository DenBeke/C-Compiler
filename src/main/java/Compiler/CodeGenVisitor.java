package Compiler;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

public class CodeGenVisitor extends Visitor {
	private Vector<String> instructions = new Vector<String>();

	public void visit(Ast.BinaryOperatorNode node) {
		System.out.println(instructions.toString());

		switch(node.operator) {
		case "=":
		case "==":
		case "!=":
		case "+":
			instructions.addAll(node.getLeftChild().codeR());
			instructions.addAll(node.getRightChild().codeR());
			instructions.add("add i");
			break;
		case "-":
		case "/":
		case "*":
		case ">":
		case ">=":
		case "<":
		case "<=":
		default:
			Log.fatal("Codegen invalid binary operator: " + node.operator,
					node.line);
        }

		System.out.println(instructions.toString());
	}
}
