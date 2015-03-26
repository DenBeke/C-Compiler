package Compiler;

import java.util.Map;
import java.util.TreeMap;
import java.util.Stack;

public class SymbolTableVisitor extends Visitor {


    /**
     * Class representing a Symbol
     *
     * A symbol has an identifier and a TypeNode
     */
    public static class Symbol {
		public String id;
		public Ast.TypeNode type;
	}


    /**
     * Class representing a Symbol Table
     *
     * A symbol table has a map of identifiers to symbols
     */
	public static class SymbolTable {
		public Map<String, Symbol> symbols;
		
		public SymbolTable() {
			symbols = new TreeMap<String, Symbol>();
		}
		
		public String toString() {
			return "";
		}
		
		
		/**
		 * Check if this table contains the symbol
		 * 
		 * @param The symbol to check
		 */
		boolean hasSymbol(String symbol) {
			if(symbols.containsKey(symbol)) {
				return true;
			}
			
			return false;
		}
	}


    /**
     * Check if a symbol exists in any symbol table on the stack
     * @param id
     * @return symbol
     */
	private Symbol findSymbol(String id) {
		for(int i = 0; i < symbolTableStack.size(); i++) {
			if(symbolTableStack.get(i).hasSymbol(id)) {
				return symbolTableStack.get(i).symbols.get(id);
			}
		}

		return null;
	}


    /**
     * Visit FileNode
     *
     * @param node
     */
	public void visit(Ast.FileNode node) {
		System.out.println("file");
		
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}


    /**
     * Visit DeclarationNode
     *
     * @param node
     */
	public void visit(Ast.DeclarationNode node) {
		System.out.println("var declaration");
		
		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
            Log.fatal("Symbol '" + node.id + "' previously declared (on line " + symbolTableStack.peek().symbols.get(node.id).type.line  +")", node.line);
		}
		
		Symbol symbol = new Symbol();
		symbol.id = node.id;
        Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().symbols.put(node.id, symbol);

		visitChildren(node);
	}

    /**
     * Visit IdNode
     *
     * @param node
     */
	public void visit(Ast.IdNode node) {
		Symbol symbol = findSymbol(node.id);
		if(symbol == null) {
            Log.fatal("Use of undeclared '" + node.id + "'", node.line);
		}

		node.symbol = symbol;

		visitChildren(node);
	}


    /**
     * Visit BlockStatementNode
     *
     * @param node
     */
	public void visit(Ast.BlockStatementNode node) {
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}


    /**
     * Visit FunctionDeclarationNode
     *
     * @param node
     */
	public void visit(Ast.FunctionDeclarationNode node) {
		enterNewScope();
		visitChildren(node);
		leaveScope();
	}


    /**
     * Visit FormalParameterNode
     *
     * @param node
     */
	public void visit(Ast.FormalParameterNode node) {
		System.out.println("formal parameter");
		
		// Check for multiple declarations
		if(symbolTableStack.peek().hasSymbol(node.id)) {
            Log.fatal("Parameter with name '" + node.id + "' already exists", node.line);
		}

		Symbol symbol = new Symbol();
		symbol.id = node.id;
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode);
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().symbols.put(node.id, symbol);

		visitChildren(node);
	}




    private Stack<SymbolTable> symbolTableStack = new Stack<SymbolTable>();

    private void enterNewScope() {
        System.out.println(">");
        symbolTableStack.push(new SymbolTable());
    }

    private void leaveScope() {
        System.out.println("<");
        symbolTableStack.pop();
    }

}
