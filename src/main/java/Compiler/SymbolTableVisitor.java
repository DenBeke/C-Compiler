package Compiler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Stack;

public class SymbolTableVisitor extends Visitor {
	public static class Symbol {
		public String id;
		public Ast.TypeNode type;
	}

	public static class SymbolTable {
		public Map<String, Symbol> symbols;
		
		public SymbolTable() {
			symbols = new TreeMap<String, Symbol>();
		}
		
		public String toString() {
			return "";
		}
		
		
		/*
		 * Check if this table contains the symbol
		 * 
		 * @param symbol The symbol to check
		 */
		boolean hasSymbol(String symbol) {
			if(symbols.containsKey(symbol)) {
				return true;
			}
			
			return false;
		}
	}
	
	private Stack<SymbolTable> symbolTableStack = new Stack<SymbolTable>();

	private Symbol findSymbol(String id) {
		for(int i = 0; i < symbolTableStack.size(); i++) {
			if(symbolTableStack.get(i).hasSymbol(id)) {
				return symbolTableStack.get(i).symbols.get(id);
			}
		}

		return null;
	}
	
	private void enterNewScope() {
		System.out.println(">");
		symbolTableStack.push(new SymbolTable());
	}
	
	private void leaveScope() {
		System.out.println("<");
		symbolTableStack.pop();
	}
	
	public void visit(Ast.FileNode node) {
		System.out.println("file");
		
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}
	
	public void visit(Ast.DeclarationNode node) {
		System.out.println("var declaration");
		
		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
			System.out.println("Multiple declaration: " + node.id);
		}

		
		Symbol symbol = new Symbol();
		symbol.id = node.id;
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().symbols.put(node.id, symbol);

		visitChildren(node);
	}

	public void visit(Ast.IdNode node) {
		Symbol symbol = findSymbol(node.id);
		if(symbol == null) {
			System.out.println("Use of " + node.id + " before declaration");
		}

		node.symbol = symbol;

		visitChildren(node);
	}

	public void visit(Ast.BlockStatementNode node) {
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}

	public void visit(Ast.FunctionDeclarationNode node) {
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}

	public void visit(Ast.FormalParameterNode node) {
		System.out.println("formal parameter");
		
		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
			System.out.println("Parameter with name: " + node.id + " already exists");
		}

		Symbol symbol = new Symbol();
		symbol.id = node.id;
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().symbols.put(node.id, symbol);

		visitChildren(node);
	}
}
