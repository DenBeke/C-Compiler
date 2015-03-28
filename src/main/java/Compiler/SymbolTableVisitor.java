package Compiler;

import java.util.Map;
import java.util.TreeMap;
import java.util.Stack;
import java.util.Vector;

public class SymbolTableVisitor extends Visitor {


    /**
     * @brief Class representing a Symbol
     *
     * A symbol has an identifier and a TypeNode
     */
    public static class Symbol {
		public String id;
		public Ast.TypeNode type;
	}


    /**
     * @brief Class representing a Symbol for a variable
     */
    public static class VarSymbol extends Symbol {}


    /**
     * @brief Class representing a Symbol for a function
     *
     * - id
     * - type (= return type)
     * - paramTypes
     */
    public static class FuncSymbol extends Symbol {

        public Vector<Ast.TypeNode> paramTypes = new Vector<>();


        /**
         * Add new parameter type node to the function symbol
         * @param node: TypeNode
         */
        public void addParamType(Ast.TypeNode node) {
            paramTypes.add(node);
        }


        /**
         * Add new parameter type node to the function symbol
         * @param node: TypeNode
         * @pre node instanceof Ast.TypeNode
         */
        public void addParamType(Ast.Node node) {
            Assert.Assert(node instanceof Ast.TypeNode, "Expected TypeNode");
            paramTypes.add((Ast.TypeNode) node);
        }
    }


    /**
     * @brief Class representing a Symbol Table
     *
     * A symbol table has a map of identifiers to symbols
     */
	public static class SymbolTable {
		private Map<String, Symbol> symbols;
		
		public SymbolTable() {
			symbols = new TreeMap<String, Symbol>();
		}
		
		
		/**
		 * Check if this table contains the symbol
		 * 
		 * @param symbol: The symbol to check
		 */
		public boolean hasSymbol(String symbol) {
			if(symbols.containsKey(symbol)) {
				return true;
			}
			
			return false;
		}


        /**
         * Get symbol from the symbol table
         * @param id: identifier
         * @return Symbol
         */
        public Symbol getSymbol(String id) {
            return symbols.get(id);
        }


        /**
         * Add new symbol to symbol table
         * @param symbol: symbol to add
         */
        public void addSymbol(Symbol symbol) {
            symbols.put(symbol.id, symbol);
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
				return symbolTableStack.get(i).getSymbol(id);
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
            Log.fatal("Symbol '" + node.id + "' previously declared (on line " + symbolTableStack.peek().getSymbol(node.id).type.line  +")", node.line);
		}
		
		Symbol symbol = new Symbol();
		symbol.id = node.id;
        Assert.Assert(node.children.get(0) instanceof Ast.TypeNode, "Expected TypeNode");
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().addSymbol(symbol);

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
     * Visit BlockStatementNode
     *
     * @param node
     * @param newScope: bool indicating if block must have a new scope
     */
    public void visit(Ast.BlockStatementNode node, boolean newScope) {
        if(newScope) {
            visit(node, false);
        }
        else {
            visitChildren(node);
        }
    }


    /**
     * Visit FunctionDeclarationNode
     *
     * @param node
     */
	public void visit(Ast.FunctionDeclarationNode node) {
		enterNewScope();

        Assert.Assert(node.children.get(0) instanceof Ast.TypeNode, "Expected TypeNode");
        Assert.Assert(node.children.get(1) instanceof Ast.FormalParametersNode, "Expected FormalParametersNode");

        FuncSymbol symbol = new FuncSymbol();

        // set id
        symbol.id = node.id;

        // set return type
        symbol.type = (Ast.TypeNode) node.children.get(0);

        // add param types
        for(int i = 0; i < node.children.get(1).children.size(); i++) {
            Assert.Assert(node.children.get(1).children.get(i).children.get(0) instanceof Ast.TypeNode, "Expected TypeNode");
            symbol.addParamType(node.children.get(1).children.get(i).children.get(0));
        }

        symbolTableStack.peek().addSymbol(symbol);

        // visit params
        for(int i = 0; i < node.children.get(1).children.size(); i++) {
            Assert.Assert(node.children.get(1).children.get(i) instanceof Ast.FormalParameterNode, "Expected FormalParameterNode");
            visit(node.children.get(1).children.get(i));
        }

        // visit block
        Assert.Assert(node.children.get(2) instanceof Ast.BlockStatementNode, "Expected BlockStatementNode");
        visit((Ast.BlockStatementNode) node.children.get(2), false);

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
		Assert.Assert(node.children.get(0) instanceof Ast.TypeNode, "Expected TypeNode");
		symbol.type = (Ast.TypeNode)node.children.get(0);
		symbolTableStack.peek().addSymbol(symbol);

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
