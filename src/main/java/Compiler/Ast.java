package Compiler;

import java.util.Vector;


public class Ast {
    public static abstract class Node {
		public int scope;
		public Vector<Node> children = new Vector<Node>();

		public abstract void visit(Visitor visitor);
		
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
            Assert.Assert(this instanceof PointerTypeNode);
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

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}


    public static abstract class TypeNode extends Node {
        public Boolean constant = false;
        public Boolean topLevel = false;

        public String toString(String prefix) {
            String result = prefix + "TypeNode: " + getClass().getSimpleName();
            if(constant) {
                result += " const";
            }

            result += "\n";

            result += childrenToString(prefix + "\t");

            return result;
        }
    }

    public static class ConstTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class PointerTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class IntTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class CharTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class VoidTypeNode extends TypeNode {
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class StaticArrayTypeNode extends TypeNode {
        public Integer size = 0;
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static abstract class LiteralNode extends Node {
    }

	public static class IntNode extends LiteralNode {
		public Integer value;

		public String toString(String prefix) {
			String result = prefix + "IntNode: " + String.valueOf(value);
			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

	}

    public static class CharNode extends LiteralNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "CharNode: " + String.valueOf(value);
            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}

    }

    public static class StringNode extends LiteralNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "StringNode: " + String.valueOf(value);
            return result;
        }

		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

	public static class IdNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "IdNode: " + id;
			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class DeclarationNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "DeclarationNode: " + id + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FunctionDeclarationNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "FunctionDeclarationNode: " + id + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FormalParametersNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "FormalParametersNode:\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class FormalParameterNode extends Node {
		public String id;

		public String toString(String prefix) {
			String result = prefix + "FormalParameterNode: " + id;
			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}


    public static class ParamNode extends Node {
        public String toString(String prefix) {
            String result = prefix + "ParamNode:\n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class FunctionCallNode extends ExpressionNode {
        public String value;

        public String toString(String prefix) {
            String result = prefix + "FunctionCallNode: " + String.valueOf(value) + "\n";
            result += childrenToString(prefix + "\t");
            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
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
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ExprStatementNode extends StatementNode {
		public String toString(String prefix) {
			String result = prefix + "ExprStatementNode: \n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class BinaryOperatorNode extends ExpressionNode {
		public String operator;

		public String toString(String prefix) {
			String result = prefix + "BinaryOperatorNode: " + operator + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static class UnaryOperatorNode extends ExpressionNode {
		public String operator;

		public String toString(String prefix) {
			String result = prefix + "UnaryOperatorNode: " + operator + "\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class ForStatementNode extends StatementNode {
		public String toString(String prefix) {
			String result = prefix + "ForStatementNode:\n";
			result += childrenToString(prefix + "\t");

			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static class NothingNode extends Node {
		public String toString(String prefix) {
			String result = prefix + "NothingNode";
			return result;
		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
	}

    public static class ReturnStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "ReturnStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class WhileStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "WhileStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class IfStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "IfStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }


    public static class BreakStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "BreakStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }

    public static class ContinueStatementNode extends StatementNode {
        public String toString(String prefix) {
            String result = prefix + "ContinueStatementNode: \n";
            result += childrenToString(prefix + "\t");

            return result;
        }
        
		@Override
		public void visit(Visitor visitor) {
			visitor.visit(this);
		}
    }


}
