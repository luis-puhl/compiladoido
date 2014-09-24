package si.vv.pokebola.compiladoido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.SyntaticSymbol;
import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

/**
 * Pushdown automaton warper.
 * 
 * 
 * @author luispuhl
 *
 */
public class PascalSyntacticAutomata {

	private SyntaticTreeNode root;

	private static Logger logger;
	private Level exceptionLevel = Level.TRACE;

	private LexicalSyntaticConverter converter;

	public PascalSyntacticAutomata(List<Token> lexicalTokens) {
		converter = new LexicalSyntaticConverter(lexicalTokens);
		if (logger == null) {
			logger = LogManager.getLogger();
		}
	}

	public SyntaticTreeNode getRoot() {
		return root;
	}

	public void setRoot(SyntaticTreeNode root) {
		this.root = root;
	}

	public SyntaticTreeNode run() throws SyntacticAutomataException {
		root = new SyntaticTreeNode(null, "run", null, null);

		root.add(program(root));
		return root;
	}

	/* ***************************************************************** */

	/*
	 * BEGIN program
	 * http://www.freepascal.org/docs-html/ref/refse95.html#x201-21100016.1
	 */

	private SyntaticTreeNode program(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.PROGRAM);
		/** PROGRAM */
		// PROGRAM HEADER
		node.add(programHeader(node));

		node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));
		// op USES CLAUSE
		try {
			node.add(usesClause(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(Level.WARN, e);
			logger.info("No USES CLAUSE declaration");
		}
		// BLOCK
		node.add(block(node));

		// PERIOD
		node.add(converter.expectNode(node, null, OperatorSymbols.PERIOD));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode programHeader(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.PROGRAM_HEADER);

		node.add(converter.expectNode(node, null, WordSymbols.PROGRAM));

		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));

		// op PROGRAM PARAMETERS
		try {
			node.add(converter.expectNode(node, null, OperatorSymbols.OPEN_PARENTHESIS));
			node.add(programParameters(node));
			node.add(converter.expectNode(node, null, OperatorSymbols.CLOSE_PARENTHESIS));
		} catch (SyntacticAutomataException e) {
			e.log();
			logger.info("No PROGRAM PARAMETERS declaration");
		}

		logger.exit();
		return node;
	}

	/**
	 * The program header is provided for backwards compatibility, and is
	 * ignored by the compiler.
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode programParameters(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		// return indentifierList(parent);
		return null;
	}

	private SyntaticTreeNode usesClause(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.USE_CLAUSE);

		node.add(converter.expectNode(node, WordSymbols.USES));

		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));

		// op STRING LITERAL
		try {
			node.add(converter.expectNode(node, null, WordSymbols.IN));

			// deve ser STRING LITERAL
			node.add(converter.expectNode(node, SyntaticSymbol.STRING_LITERAL, OperatorSymbols.ID));

		} catch (SyntacticAutomataException e) {
		}
		// op MORE USES
		try {
			node.add(converter.expectNode(node, null, OperatorSymbols.COMMA));

			node.add(usesClause(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));

		logger.exit();
		return node;
	}

	/* END program */

	/*
	 * BEGIN block definition
	 * http://www.freepascal.org/docs-html/ref/refse98.html#x204-21400016.4
	 */

	private SyntaticTreeNode block(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.BLOCK);

		// DECLARATION PART
		node.add(declarationPart(node));

		// STATEMENT PART
		node.add(compoundStatement(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode declarationPart(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.DECLARATION_PART);

		/**
		 * Em FPC, a ordem não importa, aqui importa.
		 */
		try {
			node.add(labelDeclaration(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(constantDeclaration(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(resourceStringDeclaration(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(typeDeclaration(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(variableDeclarationPart(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(threadVarDeclaration(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}
		try {
			node.add(procedureFuncionDeclarationPart(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}

		logger.exit();
		return node;
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode labelDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode constantDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode resourceStringDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode typeDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	private SyntaticTreeNode variableDeclarationPart(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VAR_DECLARATION_PART);

		try {
			node.add(converter.expectNode(node, null, WordSymbols.VAR));
		} catch (SyntacticAutomataException e) {
			return node;
		}

		while (true) {
			try {
				node.add(variableDeclaration(node));
			} catch (SyntacticAutomataException e) {
				logger.exit();
				return node;
			}
		}
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode threadVarDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	private SyntaticTreeNode procedureFuncionDeclarationPart(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		int errors = 0;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.PROCEDURE_FUNCTION_DECLARATION_PART);

		while (errors < 4) {
			logger.info("Geting a procedure/function");
			try {
				node.add(procedureDeclaration(node));
			} catch (SyntacticAutomataException e) {
				errors++;
			}
			try {
				node.add(functionDeclaration(node));
			} catch (SyntacticAutomataException e) {
				errors++;
			}
			try {
				node.add(constructorDeclaration(node));
			} catch (SyntacticAutomataException e) {
				errors++;
			}
			try {
				node.add(destructorDeclaration(node));
			} catch (SyntacticAutomataException e) {
				errors++;
			}
		}

		logger.exit();
		return node;
	}

	/* END block definition */

	/*
	 * BEGIN var delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	private SyntaticTreeNode variableDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VAR_DECLARATION);

		// id
		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));

		// :
		node.add(converter.expectNode(node, null, OperatorSymbols.COLON));

		// type
		node.add(type(node));

		// op = expression
		try {
			node.add(converter.expectNode(node, null, OperatorSymbols.EQUAL));
			node.add(expression(node));
		} catch (SyntacticAutomataException e) {
		}

		// op variable modifiers
		try {
			node.add(variableModifiers(node));
		} catch (SyntacticAutomataException e) {
		}

		// hint directive
		node.add(hintDirective(node));

		// ;
		node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));

		logger.exit();
		return node;
	}

	/**
	 * <a>http://www.freepascal.org/docs-html/ref/refch3.html#x25-240003</a>
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode type(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.TYPE);

		Collection<TypeWordSymbols> types = TypeWordSymbols.INTEGER.allMap().values();
		node.add(converter.expectNode(node, types));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode constant(SyntaticTreeNode parent) throws SyntacticAutomataException {

		logger.entry();

		parent.add(converter.expectNode(parent, SyntaticSymbol.CONSTANT, OperatorSymbols.ID));

		logger.exit();
		return null;
	}

	/**
	 * <a>http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2</a>
	 * 
	 * EXPORT, EXTERMAL, ABSOLUTE are not suported.
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode variableModifiers(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VAR_MODIFIERS);

		boolean rollbackSemicolon = false;
		SyntaticTreeNode syntaticTreeNode = null;
		try {
			syntaticTreeNode = converter.expectNode(node, OperatorSymbols.SEMICOLON);
			rollbackSemicolon = node.add(syntaticTreeNode);
			node.add(converter.expectNode(node, null, WordSymbols.CVAR));
		} catch (SyntacticAutomataException e) {
			// extra rollback para semicolon
			if (rollbackSemicolon) {
				converter.rollback();
				node.getChildren().remove(syntaticTreeNode);
			}
		}

		logger.exit();
		return node;
	}

	/* END var delcaration */

	/*
	 * BEGIN procedure delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	private SyntaticTreeNode procedureDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.PROCEDURE_DECLARATION);

		// PROCEDURE HEADER
		node.add(converter.expectNode(node, null, WordSymbols.PROCEDURE));
		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));
		node.add(formalParameterList(node));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode subroutineBlock(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.SUBROTINE_BLOCK);

		try {
			node.add(compoundStatement(node));
		} catch (SyntacticAutomataException eBlock) {
			eBlock.log();

			try {
				node.add(externalDirective(node));
			} catch (SyntacticAutomataException eExternalDirective) {
				try {
					node.add(asmBlock(node));
				} catch (SyntacticAutomataException eAsmBlock) {
					node.add(converter.expectNode(node, null, WordSymbols.FORWARD));
				}
			}
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode externalDirective(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	private SyntaticTreeNode asmBlock(SyntaticTreeNode parent) throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/* END procedure delcaration */

	private SyntaticTreeNode functionDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		Collection<TypeWordSymbols> resultTypes = new LinkedList<TypeWordSymbols>();
		for (TypeWordSymbols type : TypeWordSymbols.values()) {
			resultTypes.add(type);
		}

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.FUNCTION_DECLARATION);

		// FUNCTION HEADER
		node.add(converter.expectNode(node, WordSymbols.FUNCTION));
		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));
		node.add(formalParameterList(node));
		node.add(converter.expectNode(node, OperatorSymbols.COLON));
		node.add(converter.expectNode(node,
				Thread.currentThread().getStackTrace()[0].getMethodName(), SyntaticSymbol.TYPE,
				resultTypes));

		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(converter.expectNode(node, OperatorSymbols.SEMICOLON));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(converter.expectNode(node, OperatorSymbols.SEMICOLON));

		logger.exit();
		return node;
	}

	/**
	 * Não implementado. Veja o link para ver detalhes. <a href=
	 * "http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2">
	 * http://www.freepascal.org/docs-html/ref/refse87.html#x173-18300014.9</a>
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode functionProcedureModifiers(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
	}

	private SyntaticTreeNode constructorDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	private SyntaticTreeNode destructorDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/*
	 * BEGIN parameter lists
	 * http://www.freepascal.org/docs-html/ref/refse82.html#x162-17200014.4
	 */

	private SyntaticTreeNode formalParameterList(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.FORMAL_PARAMETER_LIST);

		node.add(converter.expectNode(node, OperatorSymbols.OPEN_PARENTHESIS));

		node.add(parameterDeclaration(node));
		try {
			while (true) {
				node.add(converter.expectNode(node, OperatorSymbols.SEMICOLON));
				node.add(parameterDeclaration(node));
			}
		} catch (SyntacticAutomataException e) {
		}

		node.add(converter.expectNode(node, OperatorSymbols.CLOSE_PARENTHESIS));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode parameterDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.PARAMETER_DECLARATION);

		try {
			node.add(valueParameter(node));
		} catch (SyntacticAutomataException e) {
			node.add(variableParameter(node));
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu60.html#x163-17300014.4.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode valueParameter(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VALUE_PARAMETER);

		try {
			node.add(identifierList(node));
			try {
				node.add(converter.expectNode(node, null, WordSymbols.ARRAY));
				node.add(converter.expectNode(node, null, WordSymbols.OF));
			} catch (SyntacticAutomataException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (SyntacticAutomataException eList) {
			node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));
			node.add(converter.expectNode(node, null, OperatorSymbols.COLON));
			// node.add(typeIdentifier(node));
			node.add(type(node));
			node.add(converter.expectNode(node, null, OperatorSymbols.EQUAL));
			node.add(converter.expectNode(node, SyntaticSymbol.CONSTANT, OperatorSymbols.ID));
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu61.html#x164-17400014.4.2
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode variableParameter(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VARIABLE_PARAMETER);

		node.add(converter.expectNode(node, WordSymbols.VAR));
		node.add(identifierList(node));

		try {
			node.add(converter.expectNode(node, OperatorSymbols.COLON));
			try {
				node.add(converter.expectNode(node, WordSymbols.ARRAY));
				node.add(converter.expectNode(node, WordSymbols.OF));
			} catch (SyntacticAutomataException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (SyntacticAutomataException eList) {
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode identifierList(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.IDENTIFIER_LIST);

		node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER, OperatorSymbols.ID));

		try {
			node.add(converter.expectNode(node, OperatorSymbols.COMMA));
			identifierList(node);
		} catch (SyntacticAutomataException e) {
		}

		logger.exit();
		return node;
	}

	/* END parameter lists */

	private SyntaticTreeNode hintDirective(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "hintDirective";
		Collection<WordSymbols> hints = new LinkedList<WordSymbols>();

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.HINT_DIRECTIVE);

		hints.add(WordSymbols.DEPRECATED);
		hints.add(WordSymbols.EXPERIMENTAL);
		hints.add(WordSymbols.PLATAFORM);
		hints.add(WordSymbols.UNIMPLEMENTED);

		try {
			Token lexicToken = converter.expect(hints, Thread.currentThread().getStackTrace()[0]);
			node.add(new SyntaticTreeNode(node, method, null, lexicToken));

			try {
				if (lexicToken.getSymbol().equals(WordSymbols.DEPRECATED)) {
					node.add(converter.expectNode(node, SyntaticSymbol.IDENTIFIER,
							OperatorSymbols.ID));
				}
			} catch (SyntacticAutomataException e) {
			}
		} catch (SyntacticAutomataException e) {
		}

		logger.exit();
		return node;
	}

	/**
	 * Theory http://www.freepascal.org/docs-html/ref/refch12.html#x126-13600012
	 * 
	 * Implementation
	 * http://www.freepascal.org/docs-html/ref/refse68.html#x127-13700012.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode expression(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.EXPRESSION);

		// simple expression
		node.add(simpleExpression(node));

		// binary operator + simple expression
		try {
			Collection<Symbol> binaryOps = new ArrayList<>(7);

			binaryOps.add(OperatorSymbols.ASTERISK);
			binaryOps.add(OperatorSymbols.LT_EQUAL);
			binaryOps.add(OperatorSymbols.GT);
			binaryOps.add(OperatorSymbols.GT_EQUAL);
			binaryOps.add(OperatorSymbols.NOT_EQUAL);
			binaryOps.add(WordSymbols.IN);
			binaryOps.add(WordSymbols.IS);

			node.add(converter.expectNode(node, binaryOps));

			node.add(simpleExpression(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(e);
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse68.html#x127-13700012.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode simpleExpression(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.SIMPLE_EXPRESSION);

		boolean flag = false;
		do {
			// term
			node.add(term(node));

			// unary operator
			try {
				Collection<Symbol> unaryOps = new ArrayList<>(4);

				unaryOps.add(OperatorSymbols.PLUS);
				unaryOps.add(OperatorSymbols.MINUS);
				unaryOps.add(WordSymbols.OR);
				unaryOps.add(WordSymbols.XOR);

				node.add(converter.expectNode(node, unaryOps));

				node.add(simpleExpression(node));
			} catch (SyntacticAutomataException e) {
				logger.catching(e);
				flag = true;
			}
		} while (flag);

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse68.html#x127-13700012.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode term(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.TERM);

		boolean flag = false;
		do {
			// factor
			node.add(factor(node));

			// multiplication operator
			try {
				Collection<Symbol> multiplicationOps = new ArrayList<>(7);

				multiplicationOps.add(OperatorSymbols.ASTERISK);
				multiplicationOps.add(OperatorSymbols.SLASH);
				multiplicationOps.add(WordSymbols.DIV);
				multiplicationOps.add(WordSymbols.MOD);
				multiplicationOps.add(WordSymbols.AND);
				multiplicationOps.add(WordSymbols.SHL);
				multiplicationOps.add(WordSymbols.ASM);

				node.add(converter.expectNode(node, multiplicationOps));

				node.add(simpleExpression(node));
			} catch (SyntacticAutomataException e) {
				logger.catching(e);
				flag = true;
			}
		} while (flag);

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse68.html#x127-13700012.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode factor(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.FACTOR);

		try {
			// parentesis expression
			node.add(converter.expectNode(node, OperatorSymbols.OPEN_PARENTHESIS));
			node.add(expression(node));
			node.add(converter.expectNode(node, OperatorSymbols.CLOSE_PARENTHESIS));
		} catch (SyntacticAutomataException parentesisEx) {
			try {
				// variable reference
				node.add(converter.expectNode(node, OperatorSymbols.ID,
						SyntaticSymbol.VAR_REFERENCE));
			} catch (SyntacticAutomataException varEx) {
				varEx.addSuppressed(parentesisEx);
				try {
					// function call
					node.add(functionCall(node));
				} catch (SyntacticAutomataException functionEx) {
					functionEx.addSuppressed(varEx);
					try {
						// unsigned constant
						node.add(unsignedConstant(node));
					} catch (SyntacticAutomataException usignedEx) {
						usignedEx.addSuppressed(functionEx);
						try {
							// not factor
							node.add(converter.expectNode(node, WordSymbols.NOT));
							node.add(factor(node));
						} catch (SyntacticAutomataException notEx) {
							notEx.addSuppressed(usignedEx);
							try {
								// sig factor
								// UNIMPLEMENTED
								throw new SyntacticAutomataException(logger);
							} catch (SyntacticAutomataException sigEx) {
								sigEx.addSuppressed(notEx);
								try {
									// set constructor
									// UNIMPLEMENTED
									throw new SyntacticAutomataException(logger);
								} catch (SyntacticAutomataException costructorEx) {
									costructorEx.addSuppressed(sigEx);
									try {
										// value typecast
										node.add(valueTypecast(node));
									} catch (SyntacticAutomataException valueEx) {
										valueEx.addSuppressed(costructorEx);
										try {
											// address factor
											node.add(addressFactor(node));
										} catch (SyntacticAutomataException addressEx) {
											addressEx.addSuppressed(valueEx);

										}
									}
								}
							}
						}
					}
				}
			}
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse68.html#x127-13700012.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode unsignedConstant(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.UNSIGNED_CONSTANT);

		try {
			// unsigned Number
			node.add(converter.expectNode(node, OperatorSymbols.ID, SyntaticSymbol.UNSIGNED_NUMBER));
		} catch (SyntacticAutomataException numberEx) {
			try {
				// character string
				node.add(converter.expectNode(node, OperatorSymbols.ID, SyntaticSymbol.STRING_LITERAL));
			} catch (SyntacticAutomataException stringEx) {
				stringEx.addSuppressed(numberEx);
				try {
					// constant identifier
					node.add(converter.expectNode(node, OperatorSymbols.ID, SyntaticSymbol.CONSTANT));
				} catch (SyntacticAutomataException constantEx) {
					constantEx.addSuppressed(stringEx);
					try {
						// nil
						node.add(converter.expectNode(node, WordSymbols.NIL));
					} catch (SyntacticAutomataException nilEx) {
						nilEx.addSuppressed(constantEx);
						logger.throwing(exceptionLevel, nilEx);
						throw nilEx;
					}
				}
			}
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse69.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode functionCall(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.FUNCTION_CALL);

		// function ID, method designator, qualified method designator or
		// variable reference
		node.add(converter.expectNode(node, OperatorSymbols.ID));

		try {
			node.add(actualParameterList(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse71.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode valueTypecast(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.VALUE_TYPECAST);

		// type ID
		node.add(converter.expectNode(node, OperatorSymbols.ID, SyntaticSymbol.TYPE));
		// (
		node.add(converter.expectNode(node, OperatorSymbols.OPEN_PARENTHESIS));
		// expression
		node.add(expression(node));
		// )
		node.add(converter.expectNode(node, OperatorSymbols.CLOSE_PARENTHESIS));

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse74.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode addressFactor(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.ADDRESS_FACTOR);

		// address
		node.add(converter.expectNode(node, OperatorSymbols.ADDRESS));

		// var ID, procedure ID, function ID or qualified method ID
		node.add(converter.expectNode(node, OperatorSymbols.ID));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode statement(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		try {
			// label
			// :
			node.add(converter.expectNode(node, SyntaticSymbol.LABEL, OperatorSymbols.ID));
			node.add(converter.expectNode(node, null, OperatorSymbols.COLON));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}

		try {
			// simple statement
			node.add(simpleStatement(node));
		} catch (SyntacticAutomataException eSimple) {
			try {
				// structured statement
				node.add(structuredStatement(node));
			} catch (SyntacticAutomataException eStruct) {
				try {
					// asm statement
					node.add(asmStatement(node));
				} catch (SyntacticAutomataException eAsm) {
					eAsm.addSuppressed(eSimple);
					eAsm.addSuppressed(eStruct);
					logger.catching(exceptionLevel, eAsm);
				}
			}
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse76.html#x143-15300013.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode simpleStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		try {
			// assignment statement
			node.add(assignmentStatement(node));
		} catch (SyntacticAutomataException eAssignment) {
			try {
				// procedure statement
				node.add(procedureStatement(node));
			} catch (SyntacticAutomataException eProcedure) {
				try {
					// goto statement
					node.add(gotoStatement(node));
				} catch (SyntacticAutomataException eGoto) {
					eGoto.addSuppressed(eAssignment);
					eGoto.addSuppressed(eProcedure);
					logger.throwing(eGoto);
					throw eGoto;
				}
			}
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode structuredStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		try {
			// compound statement
			node.add(compoundStatement(node));
		} catch (SyntacticAutomataException eCompound) {
			try {
				// conditional statement

				try {
					// case statement
					node.add(caseStatement(node));
				} catch (SyntacticAutomataException eCase) {
					try {
						// if statement
						node.add(ifStatement(node));
					} catch (SyntacticAutomataException eIf) {
						eIf.addSuppressed(eCase);
						logger.throwing(exceptionLevel, eIf);
						throw eIf;
					}
				}

			} catch (SyntacticAutomataException eConditional) {
				try {
					// repetitive statement

					try {
						// for statement
						node.add(forStatement(node));
					} catch (SyntacticAutomataException eFor) {
						try {
							// repeat statement
							node.add(repeatStatement(node));
						} catch (SyntacticAutomataException eRepeat) {
							try {
								// while statement
								node.add(whileStatement(node));
							} catch (SyntacticAutomataException eWhile) {
								eWhile.addSuppressed(eFor);
								eWhile.addSuppressed(eRepeat);
								logger.throwing(exceptionLevel, eWhile);
								throw eWhile;
							}
						}
					}

				} catch (SyntacticAutomataException eRepetitive) {
					try {
						// with statement
						node.add(withStatement(node));
					} catch (SyntacticAutomataException eWith) {
						try {
							// exception statement
							node.add(exceptionStatement(node));
						} catch (SyntacticAutomataException eException) {
							eException.addSuppressed(eCompound);
							eException.addSuppressed(eConditional);
							eException.addSuppressed(eRepetitive);
							eException.addSuppressed(eWith);

							logger.throwing(eException);
							throw eException;
						}
					}
				}
			}
		}

		logger.exit();
		return node;
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * Eu realmente gostaria de implementar isso, mas envolve alterar o lexico.
	 * 
	 * http://www.freepascal.org/docs-html/ref/refse78.html#x157-16700013.3
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode asmStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/* simple statements */

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu48.html#x144-15400013.1.1
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode assignmentStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		node.add(converter.expectNode(node, OperatorSymbols.ID));

		Collection<OperatorSymbols> allowedConstructs = new ArrayList<OperatorSymbols>(5);
		allowedConstructs.add(OperatorSymbols.COLON_EQUAL);
		allowedConstructs.add(OperatorSymbols.PLUS_EQUAL);
		allowedConstructs.add(OperatorSymbols.MINUS_EQUAL);
		allowedConstructs.add(OperatorSymbols.ASTERISK_EQUAL);
		allowedConstructs.add(OperatorSymbols.SLASH_EQUAL);
		node.add(converter.expectNode(node, allowedConstructs));

		node.add(expression(node));

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu49.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode procedureStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		/*
		 * Procedure ID Method ID qualified Method ID variable reference (var
		 * type = procedure)
		 */
		node.add(converter.expectNode(node, OperatorSymbols.ID));

		try {
			// actual Parameter List
			node.add(actualParameterList(node));
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refse69.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode actualParameterList(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		// (
		node.add(converter.expectNode(node, OperatorSymbols.OPEN_PARENTHESIS));

		try {
			boolean flag = true;
			do {
				node.add(expression(node));
				try {
					node.add(converter.expectNode(node, OperatorSymbols.COMMA));
				} catch (SyntacticAutomataException e) {
					flag = false;
				}
			} while (flag);
		} catch (SyntacticAutomataException e) {
			logger.catching(exceptionLevel, e);
		}

		// )
		node.add(converter.expectNode(node, OperatorSymbols.CLOSE_PARENTHESIS));

		logger.exit();
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu50.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode gotoStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		// goto
		node.add(converter.expectNode(node, WordSymbols.GOTO));

		// label
		node.add(converter.expectNode(node, SyntaticSymbol.LABEL, OperatorSymbols.ID));

		logger.exit();
		return node;
	}

	/* structed statements */

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu51.html
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode compoundStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.COMPOUND_STATEMENT);

		node.add(converter.expectNode(node, null, WordSymbols.BEGIN));
		while (true) {
			try {
				node.add(statement(node));
				try {
					node.add(converter.expectNode(node, null, OperatorSymbols.SEMICOLON));
				} catch (SyntacticAutomataException e) {
					break;
				}
			} catch (SyntacticAutomataException e) {
				break;
			}
		}
		node.add(converter.expectNode(node, null, WordSymbols.END));

		logger.exit();
		return node;
	}

	/* conditionalStatement */
	/**
	 * http://www.freepascal.org/docs-html/ref/refsu52.html conditionalStatement
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode caseStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, SyntaticSymbol.CASE);

		// case
		node.add(converter.expectNode(node, WordSymbols.CASE));

		// expression
		node.add(expression(node));

		// of
		node.add(converter.expectNode(node, WordSymbols.OF));

		boolean flag;
		do {
			flag = false;

			// case
			node.add(constant(node));
			try {
				node.add(converter.expectNode(node, OperatorSymbols.PERIOD_PERIOD));
				node.add(constant(node));
			} catch (SyntacticAutomataException e) {
			}
			node.add(converter.expectNode(node, OperatorSymbols.COLON));
			node.add(statement(node));

			// ;
			try {
				node.add(converter.expectNode(node, OperatorSymbols.SEMICOLON));
			} catch (SyntacticAutomataException e) {
				flag = true;
			}
		} while (flag);

		// else part
		try {
			Collection<Symbol> elseSynonyms = new ArrayList<Symbol>(2);
			elseSynonyms.add(WordSymbols.ELSE);
			elseSynonyms.add(WordSymbols.OTHERWISE);

			// else
			node.add(converter.expectNode(node, elseSynonyms));

			// statementlist
			node.add(statement(node));
		} catch (SyntacticAutomataException e) {
		}

		// ;
		try {
			node.add(converter.expectNode(node, OperatorSymbols.SEMICOLON));
		} catch (SyntacticAutomataException e) {
		}

		// end
		node.add(converter.expectNode(node, WordSymbols.END));

		node.add(converter.expectNode(node, OperatorSymbols.COLON));

		node.add(converter.expectNode(node, WordSymbols.CASE));

		logger.exit();
		return node;
	}

	/**
	 * 
	 * conditionalStatement
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode ifStatement(SyntaticTreeNode parent) throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/* end conditionalStatement */

	/* repetitive repetitiveSatatement */

	/**
	 * 
	 * repetitiveSatatement
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode forStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * 
	 * repetitiveSatatement
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode repeatStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * 
	 * repetitiveSatatement
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode whileStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/* end repetitiveSatatement */

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode withStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/**
	 * UNIMPLEMENTED
	 * 
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode exceptionStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	/* END structed statements */

	/* ********************************************************************************************************
	 */

	private SyntaticTreeNode argumentos(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		// expect(OperatorSymbols.ID);
		node.add(maisIDs(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode maisIDs(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		// try {
		// expect(OperatorSymbols.SEMICOLON);
		// } catch (SyntacticAutomataException e) {
		// logger.exit();
		// return node;
		// }

		node.add(argumentos(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode outrosTermos(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		try {
			node.add(operadorUnario(node));
		} catch (SyntacticAutomataException e) {
			logger.exit();
			return node;

		}
		node.add(termo(node));
		node.add(outrosTermos(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode termo(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		try {
			node.add(operadorBinario(node));
		} catch (SyntacticAutomataException e) {
			logger.exit();
			return node;
		}

		node.add(fator(node));
		node.add(maisFatores(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode maisFatores(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		node.add(operadorBinario(node));
		node.add(fator(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode operadorBinario(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.ASTERISK);
		operadoresPossiveis.add(OperatorSymbols.SLASH);

		// expect(operadoresPossiveis);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode operadorUnario(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.PLUS);
		operadoresPossiveis.add(OperatorSymbols.MINUS);

		// expect(operadoresPossiveis);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode fator(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;

		logger.entry();
		node = new SyntaticTreeNode(parent, null);

		Collection<Symbol> fatoresPossiveis = new LinkedList<Symbol>();
		fatoresPossiveis.add(OperatorSymbols.ID);
		fatoresPossiveis.add(OperatorSymbols.OPEN_PARENTHESIS);

		Token token = null;
		// token = expect(fatoresPossiveis);
		Symbol symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols) {
			OperatorSymbols fator = (OperatorSymbols) symbol;
			switch (fator) {
			case ID:
				break;
			case OPEN_PARENTHESIS:
				// node.add(expressao(node));
				// expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
				break;
			default:
				logger.error("UNKNOWN FACTOR " + fator.getName());
				break;
			}
		}

		logger.exit();
		return node;
	}
}
