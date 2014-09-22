package si.vv.pokebola.compiladoido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
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

	private LexicalSyntaticConverter syntaticConverter;

	public PascalSyntacticAutomata(List<Token> lexicalTokens) {
		syntaticConverter = new LexicalSyntaticConverter(lexicalTokens);
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
		root = new SyntaticTreeNode(null, "run", null);

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
		String method = "program";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROGRAM);
		/** PROGRAM */
		// PROGRAM HEADER
		node.add(programHeader(node));

		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.SEMICOLON));
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
		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.PERIOD));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode programHeader(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "programHeader";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROGRAM_HEADER);

		node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.PROGRAM));

		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));

		// op PROGRAM PARAMETERS
		try {
			node.add(syntaticConverter.expectNode(node, method, null,
					OperatorSymbols.OPEN_PARENTHESIS));
			node.add(programParameters(node));
			node.add(syntaticConverter.expectNode(node, method, null,
					OperatorSymbols.CLOSE_PARENTHESIS));
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
		String method = "usesClause";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.USE_CLAUSE);

		node.add(syntaticConverter.expectNode(node, method, WordSymbols.USES));

		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));

		// op STRING LITERAL
		try {
			node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.IN));

			// deve ser STRING LITERAL
			node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.STRING_LITERAL,
					OperatorSymbols.ID));

		} catch (SyntacticAutomataException e) {
		}
		// op MORE USES
		try {
			node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.COMMA));

			node.add(usesClause(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.SEMICOLON));

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
		String method = "block";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.BLOCK);

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
		String method = "declarationPart";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.DECLARATION_PART);

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
		String method = "variableDeclarationPart";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION_PART);

		try {
			node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.VAR));
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
		String method = "procedureFuncionDeclarationPart";
		int errors = 0;

		logger.entry();
		node = new SyntaticTreeNode(parent, method,
				SyntaticSymbol.PROCEDURE_FUNCTION_DECLARATION_PART);

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
		String method = "variableDeclaration";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION);

		// id
		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));

		// :
		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.COLON));

		// type
		node.add(type(node));

		// op = expression
		try {
			node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.EQUAL));
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
		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.SEMICOLON));

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
		String method = "type";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.TYPE);

		Collection<TypeWordSymbols> types = TypeWordSymbols.INTEGER.allMap().values();
		node.add(syntaticConverter.expectNode(node, method, null, types));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode constant(SyntaticTreeNode parent) throws SyntacticAutomataException {
		String method = "constant";

		logger.entry();

		parent.add(syntaticConverter.expectNode(parent, method, SyntaticSymbol.CONSTANT,
				OperatorSymbols.ID));

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
		String method = "variableModifiers";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_MODIFIERS);

		boolean rollbackSemicolon = false;
		SyntaticTreeNode syntaticTreeNode = null;
		try {
			syntaticTreeNode = syntaticConverter
					.expectNode(node, method, OperatorSymbols.SEMICOLON);
			rollbackSemicolon = node.add(syntaticTreeNode);
			node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.CVAR));
		} catch (SyntacticAutomataException e) {
			// extra rollback para semicolon
			if (rollbackSemicolon) {
				syntaticConverter.rollback();
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
		String method = "procedureDeclaration";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROCEDURE_DECLARATION);

		// PROCEDURE HEADER
		node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.PROCEDURE));
		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));
		node.add(formalParameterList(node));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.SEMICOLON));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.SEMICOLON));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode subroutineBlock(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "subroutineBlock";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.SUBROTINE_BLOCK);

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
					node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.FORWARD));
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
		String method = "functionDeclaration";
		Collection<TypeWordSymbols> resultTypes = new LinkedList<TypeWordSymbols>();
		for (TypeWordSymbols type : TypeWordSymbols.values()) {
			resultTypes.add(type);
		}

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.FUNCTION_DECLARATION);

		// FUNCTION HEADER
		node.add(syntaticConverter.expectNode(node, method, WordSymbols.FUNCTION));
		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));
		node.add(formalParameterList(node));
		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.COLON));
		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.TYPE, resultTypes));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.SEMICOLON));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.SEMICOLON));

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
		String method = "formalParameterList";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.FORMAL_PARAMETER_LIST);

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.OPEN_PARENTHESIS));

		node.add(parameterDeclaration(node));
		try {
			while (true) {
				node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.SEMICOLON));
				node.add(parameterDeclaration(node));
			}
		} catch (SyntacticAutomataException e) {
		}

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.CLOSE_PARENTHESIS));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode parameterDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "parameterDeclaration";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PARAMETER_DECLARATION);

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
		String method = "valueParameter";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VALUE_PARAMETER);

		try {
			node.add(identifierList(node));
			try {
				node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.ARRAY));
				node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.OF));
			} catch (SyntacticAutomataException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (SyntacticAutomataException eList) {
			node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
					OperatorSymbols.ID));
			node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.COLON));
			// node.add(typeIdentifier(node));
			node.add(type(node));
			node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.EQUAL));
			node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.CONSTANT,
					OperatorSymbols.ID));
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
		String method = "variableParameter";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VARIABLE_PARAMETER);

		node.add(syntaticConverter.expectNode(node, method, WordSymbols.VAR));
		node.add(identifierList(node));

		try {
			node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.COLON));
			try {
				node.add(syntaticConverter.expectNode(node, method, WordSymbols.ARRAY));
				node.add(syntaticConverter.expectNode(node, method, WordSymbols.OF));
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
		String method = "identifierList";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.IDENTIFIER_LIST);

		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
				OperatorSymbols.ID));

		try {
			node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.COMMA));
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
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.HINT_DIRECTIVE);

		hints.add(WordSymbols.DEPRECATED);
		hints.add(WordSymbols.EXPERIMENTAL);
		hints.add(WordSymbols.PLATAFORM);
		hints.add(WordSymbols.UNIMPLEMENTED);

		try {
			Token lexicToken = syntaticConverter.expect(hints, Thread.currentThread()
					.getStackTrace()[0]);
			node.add(new SyntaticTreeNode(node, method, null, lexicToken));

			try {
				if (lexicToken.getSymbol().equals(WordSymbols.DEPRECATED)) {
					node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.IDENTIFIER,
							OperatorSymbols.ID));
				}
			} catch (SyntacticAutomataException e) {
			}
		} catch (SyntacticAutomataException e) {
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode expression(SyntaticTreeNode parent) throws SyntacticAutomataException {
		return expressao(parent);
	}

	private SyntaticTreeNode statement(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "statement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		try {
			// label
			// :
			node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.LABEL,
					OperatorSymbols.ID));
			node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.COLON));
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
		String method = "simpleStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

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
		String method = "structuredStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

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
		String method = "assignmentStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.ID));

		Collection<OperatorSymbols> allowedConstructs = new ArrayList<OperatorSymbols>(5);
		allowedConstructs.add(OperatorSymbols.COLON_EQUAL);
		allowedConstructs.add(OperatorSymbols.PLUS_EQUAL);
		allowedConstructs.add(OperatorSymbols.MINUS_EQUAL);
		allowedConstructs.add(OperatorSymbols.ASTERISK_EQUAL);
		allowedConstructs.add(OperatorSymbols.SLASH_EQUAL);
		node.add(syntaticConverter.expectNode(node, method, allowedConstructs));

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
		String method = "procedureStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		/*
		 * Procedure ID Method ID qualified Method ID variable reference (var
		 * type = procedure)
		 */
		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.ID));

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
	 * @param parent
	 * @return
	 * @throws SyntacticAutomataException
	 */
	private SyntaticTreeNode actualParameterList(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
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
		String method = "gotoStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		// goto
		node.add(syntaticConverter.expectNode(node, method, WordSymbols.GOTO));

		// label
		node.add(syntaticConverter.expectNode(node, method, SyntaticSymbol.LABEL,
				OperatorSymbols.ID));

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
		String method = "compoundStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.COMPOUND_STATEMENT);

		node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.BEGIN));
		while (true) {
			try {
				node.add(statement(node));
				try {
					node.add(syntaticConverter.expectNode(node, method, null,
							OperatorSymbols.SEMICOLON));
				} catch (SyntacticAutomataException e) {
					break;
				}
			} catch (SyntacticAutomataException e) {
				break;
			}
		}
		node.add(syntaticConverter.expectNode(node, method, null, WordSymbols.END));

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
		String method = "structuredStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.CASE);

		// case
		node.add(syntaticConverter.expectNode(node, method, WordSymbols.CASE));

		// expression
		node.add(expression(node));

		// of
		node.add(syntaticConverter.expectNode(node, method, WordSymbols.OF));

		boolean flag;
		do {
			flag = false;

			// case
			node.add(constant(node));
			try {
				node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.PERIOD_PERIOD));
				node.add(constant(node));
			} catch (SyntacticAutomataException e) {
			}
			node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.COLON));
			node.add(statement(node));

			// ;
			try {
				node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.SEMICOLON));
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
			node.add(syntaticConverter.expectNode(node, method, elseSynonyms));

			// statementlist
			node.add(statement(node));
		} catch (SyntacticAutomataException e) {
		}

		// ;
		try {
			node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.SEMICOLON));
		} catch (SyntacticAutomataException e) {
		}

		// end
		node.add(syntaticConverter.expectNode(node, method, WordSymbols.END));

		node.add(syntaticConverter.expectNode(node, method, OperatorSymbols.COLON));

		node.add(syntaticConverter.expectNode(node, method, WordSymbols.CASE));

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

	private SyntaticTreeNode tipoVariavel(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "tipoVariavel";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<TypeWordSymbols> tipos = new LinkedList<TypeWordSymbols>();
		tipos.add(TypeWordSymbols.REAL);
		tipos.add(TypeWordSymbols.INTEGER);

		Token lexicToken = expect(tipos);
		node.add(syntaticConverter.expectNode(node, method, null, lexicToken));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode listaParametros(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "listaParametros";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(identifierList(node));

		node.add(syntaticConverter.expectNode(node, method, null, OperatorSymbols.COLON));

		node.add(tipoVariavel(node));
		node.add(maisParametros(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode maisParametros(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "maisParametros";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(syntaticConverter.expectNode(node, method, null, null,
					OperatorSymbols.SEMICOLON));
		} catch (SyntacticAutomataException e) {
			return null;
		}

		node.add(listaParametros(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode lista_arg(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "lista_arg";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.OPEN_PARENTHESIS);
		node.add(argumentos(node));
		expect(OperatorSymbols.CLOSE_PARENTHESIS);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode argumentos(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "argumentos";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.ID);
		node.add(maisIDs(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode maisIDs(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "maisIDs";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (SyntacticAutomataException e) {
			logger.exit();
			return node;
		}

		node.add(argumentos(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode pfalsa(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "pfalsa";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(WordSymbols.ELSE);
		} catch (SyntacticAutomataException e) {
			logger.exit();
			return node;
		}
		node.add(cmd(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode comandos(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "comandos";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(cmd(node));
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (SyntacticAutomataException e) {
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode condicao(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "condicao";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(expressao(node));
		node.add(relacao(node));
		node.add(expressao(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode relacao(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "relacao";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> relacoesPossiveis = new LinkedList<Symbol>();
		relacoesPossiveis.add(OperatorSymbols.EQUAL);
		relacoesPossiveis.add(OperatorSymbols.NOT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.LT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT);
		relacoesPossiveis.add(OperatorSymbols.LT);

		expect(relacoesPossiveis);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode expressao(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "expressao";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(termo(node));
		node.add(outrosTermos(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode outrosTermos(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "outrosTermos";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

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
		String method = "termo";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

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
		String method = "maisFatores";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		node.add(operadorBinario(node));
		node.add(fator(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode operadorBinario(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "operadorBinario";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.ASTERISK);
		operadoresPossiveis.add(OperatorSymbols.FOWARD_SLASH);

		expect(operadoresPossiveis);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode operadorUnario(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "operadorUnario";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.PLUS);
		operadoresPossiveis.add(OperatorSymbols.MINUS);

		expect(operadoresPossiveis);

		logger.exit();
		return node;
	}

	private SyntaticTreeNode fator(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "fator";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> fatoresPossiveis = new LinkedList<Symbol>();
		fatoresPossiveis.add(OperatorSymbols.ID);
		fatoresPossiveis.add(OperatorSymbols.OPEN_PARENTHESIS);

		Token token = expect(fatoresPossiveis);
		Symbol symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols) {
			OperatorSymbols fator = (OperatorSymbols) symbol;
			switch (fator) {
			case ID:
				break;
			case OPEN_PARENTHESIS:
				node.add(expressao(node));
				expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
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
