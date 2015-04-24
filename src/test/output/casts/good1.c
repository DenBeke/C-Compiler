FileNode
	DeclarationNode
		CharTypeNode

		IntToCharExpressionNode
			IntNode



	DeclarationNode
		CharTypeNode

		IntToCharExpressionNode
			IntNode



	DeclarationNode
		IntTypeNode

		CharToIntExpressionNode
			IdNode



	FunctionDeclarationNode
		IntTypeNode

		FormalParametersNode
			FormalParameterNode
				CharTypeNode


			FormalParameterNode
				IntTypeNode



		BlockStatementNode
			ReturnStatementNode
				CharToIntExpressionNode
					BinaryOperatorNode
						IdNode

						IdNode






	DeclarationNode
		CharTypeNode

		IntToCharExpressionNode
			FunctionCallNode
				ParamNode
					IntToCharExpressionNode
						IdNode



				ParamNode
					CharToIntExpressionNode
						IdNode






