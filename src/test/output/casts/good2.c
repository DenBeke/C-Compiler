FileNode
	FunctionDeclarationNode
		IntTypeNode

		FormalParametersNode
			FormalParameterNode
				IntTypeNode



		BlockStatementNode
			IfStatementNode
				BinaryOperatorNode
					IdNode

					IntNode


				BlockStatementNode
					ReturnStatementNode
						IntNode



				NothingNode


			ReturnStatementNode
				BinaryOperatorNode
					IdNode

					FunctionCallNode
						ParamNode
							BinaryOperatorNode
								IdNode

								IntNode








	DeclarationNode
		CharTypeNode

		IntToCharExpressionNode
			FunctionCallNode
				ParamNode
					IntNode





	DeclarationNode
		PointerTypeNode
			CharTypeNode


		ReferenceExpressionNode
			IdNode



	DeclarationNode
		IntTypeNode

		FunctionCallNode
			ParamNode
				FunctionCallNode
					ParamNode
						CharToIntExpressionNode
							DereferenceExpressionNode
								IdNode








