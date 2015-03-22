package Compiler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class SymbolTableVisitor extends Visitor {
	public static class SymbolTable {
		private int depth;
		public SymbolTable parent;
		public Vector<SymbolTable> children;
		public TreeMap<String, Ast.TypeNode> symbols;
		
		public SymbolTable(SymbolTable p) {
			parent = p;
			children = new Vector<SymbolTable>();
			symbols = new TreeMap<String, Ast.TypeNode>();
			if(p != null) {
				depth = p.depth + 1;
			} else {
				depth = 0;
			}
		}
		
		public String toString() {
			String result = new String("Depth: " + String.valueOf(depth) + "\n");
			
			for(Map.Entry<String, Ast.TypeNode> entry : symbols.entrySet()) {
				String symbol = entry.getKey();
				Ast.TypeNode type = entry.getValue();
				result += symbol + " => " + type.toString() + "\n";
			}
			
			for(int i = 0; i < children.size(); i++) {
				result += children.get(i).toString();
			}
			
			return result;
		}
		
		/*
		 * Check if this table or it's parent has the symbol
		 * 
		 * @param symbol The symbol to check
		 */
		boolean hasSymbol(String symbol) {
			if(hasSymbolCurrent(symbol)) {
				return true;
			}
			
			if(parent == null) {
				return false;
			}
			
			return parent.hasSymbol(symbol);
		}
		
		/*
		 * Check if this table contains the symbol
		 * 
		 * @param symbol The symbol to check
		 */
		boolean hasSymbolCurrent(String symbol) {
			if(symbols.containsKey(symbol)) {
				return true;
			}
			
			return false;
		}
	}
	
	private SymbolTable root;
	private SymbolTable current;
	
	private void enterNewScope() {
		SymbolTable scope = new SymbolTable(current);
		current.children.add(scope);
		current = scope;
	}
	
	private void leaveScope() {
		current = current.parent;
	}
	
	public void visit(Ast.FileNode node) {
		System.out.println("file");
		
		// Create root table
		root = new SymbolTable(null);
		current = root;
		
		visitChildren(node);
		System.out.println(root);
	}
	
	public void visit(Ast.DeclarationNode node) {
		System.out.println("var declaration");
		
		// Check for multiple declaration
		if(current.hasSymbolCurrent(node.id) == true) {
			System.out.println("Multiple declaration: " + node.id);
		}
		
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		current.symbols.put(node.id, (Ast.TypeNode)node.children.get(0));

		visitChildren(node);
	}
	
	public void visit(Ast.FunctionDeclarationNode node) {
		System.out.println("function declaration");
		
		// Check for multiple declaration
		if(current.hasSymbolCurrent(node.id) == true) {
			System.out.println("Multiple declaration: " + node.id);
		}
		
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		// TODO: Add type for the function itself, not the return type.
		current.symbols.put(node.id, (Ast.TypeNode)node.children.get(0));

		// Visit parameters
		visit(node.children.get(1));
		// Make parameter scope parent of function body
		current = current.children.firstElement();
		// Visit body
		visit(node.children.get(2));
		// Reset scope
		current = current.parent;
	}
	
	public void visit(Ast.FormalParametersNode node) {
		// Create a scope for the parameters
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}
	
	public void visit(Ast.FormalParameterNode node) {
		// Check for same id in the parameter list
		if(current.hasSymbolCurrent(node.id) == true) {
			System.out.println("Parameter " + node.id + " is already declared");
		}
		
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		current.symbols.put(node.id, (Ast.TypeNode)node.children.get(0));
		
		visitChildren(node);
	}
	
	public void visit(Ast.IdNode node) {
		System.out.println("id");
		
		// Check for usage before declaration
		if(!current.hasSymbol(node.id)) {
			System.out.println(node.id + " is used before declaration");
		}
		
		visitChildren(node);
	}
	
	public void visit(Ast.FunctionCallNode node) {
		System.out.println("function call");
		
		// Check for usage before declaration
		if(!current.hasSymbol(node.id)) {
			System.out.println(node.id + " is used before declaration");
		}
		
		visitChildren(node);
	}

	public void visit(Ast.BlockStatementNode node) {
		System.out.println("block");
		
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}
}