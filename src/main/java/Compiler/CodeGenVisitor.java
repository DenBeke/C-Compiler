package Compiler;

import java.util.Vector;

public class CodeGenVisitor extends Visitor {
	private Vector<String> instructions = new Vector<String>();

	private static int labelCounter = 0;

	public static String getUniqueLabel() {
		String label = "L" + Integer.toString(labelCounter);
		labelCounter += 1;

		return label;
	}

	public static String typeToPtype(Ast.TypeNode t) {
		if(t instanceof Ast.IntTypeNode) {
			return "i";
		}

		if(t instanceof Ast.CharTypeNode) {
			return "c";
		}

		if(t instanceof Ast.PointerTypeNode) {
			return "a";
		}
		
		if(t instanceof Ast.StaticArrayTypeNode) {
			return "a";
		}

		Log.warning(
				"Can't convert type to pmachine type: "
						+ t.getStringRepresentation(), t.line);
		return "";
	}

	@Override
	public void visit(Ast.FileNode node) {
		instructions.addAll(node.code());

		for(int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
	}

	@Override
	public void visit(Ast.DeclarationNode node) {
		instructions.addAll(node.code());
	}

	@Override
	public void visit(Ast.FunctionDeclarationNode node) {
		instructions.addAll(node.code());
	}
}
