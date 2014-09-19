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

	/**
	 * 
	 */
	private int tokenIndex;
	private List<Token> lexicalTokens;

	private SyntaticTreeNode root;

	private static Logger logger;

	public PascalSyntacticAutomata(List<Token> lexicalTokens) {
		this.lexicalTokens = lexicalTokens;
		this.tokenIndex = 0;
		if (logger == null) {
			logger = LogManager.getLogger();
		}
	}

	/* token stuff */

	private Token getToken() {
		return lexicalTokens.get(tokenIndex++);
	}

	private void rollback() {
		tokenIndex--;
	}

	/* tree stuff */

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

	private Token getTokenLexico() {
		Symbol symbol;
		Token token;
		token = this.getToken();
		symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols && ((OperatorSymbols) symbol).isComment()) {
			logger.info("Got a COMMENT");
			token = this.getTokenLexico();
		}
		logger.info("\n\t" + token.toString());
		return token;
	}

	private void unimplemented() throws SyntacticAutomataException {
		throw new SyntacticAutomataException(logger);
	}

	// implementacao
	private Token expect(Collection<? extends Symbol> expected, boolean optional)
			throws SyntacticAutomataException {
		Symbol symbol;
		Token token;
		token = this.getTokenLexico();
		symbol = token.getSymbol();
		if (!expected.contains(symbol)) {
			this.rollback();
			throw new SyntacticAutomataException(logger, expected, optional, symbol);
		}

		return token;
	}

	// referencias
	private Token expect(Collection<? extends Symbol> expected) throws SyntacticAutomataException {
		return this.expect(expected, false);
	}

	private Token expect(Symbol expected, boolean optional) throws SyntacticAutomataException {
		Collection<Symbol> symbols = new ArrayList<Symbol>(1);
		symbols.add(expected);
		return this.expect(symbols, optional);
	}

	private Token expect(Symbol expected) throws SyntacticAutomataException {
		Collection<Symbol> symbols = new ArrayList<Symbol>(1);
		symbols.add(expected);
		return expect(symbols, false);
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

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
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
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.POINT)));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode programHeader(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "programHeader";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROGRAM_HEADER);

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.PROGRAM)));

		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));

		// op PROGRAM PARAMETERS
		try {
			node.add(new SyntaticTreeNode(node, method, null,
					expect(OperatorSymbols.OPEN_PARENTHESIS)));
			node.add(programParameters(node));
			node.add(new SyntaticTreeNode(node, method, null,
					expect(OperatorSymbols.CLOSE_PARENTHESIS)));
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

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.USES, true)));

		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));

		// op STRING LITERAL
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.IN)));

			// deve ser STRING LITERAL
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.STRING_LITERAL,
					expect(OperatorSymbols.ID)));

		} catch (SyntacticAutomataException e) {
		}
		// op MORE USES
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COMMA)));

			node.add(usesClause(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

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
		node.add(labelDeclaration(node));
		node.add(constantDeclaration(node));
		node.add(resourceStringDeclaration(node));
		node.add(typeDeclaration(node));
		node.add(variableDeclarationPart(node));
		node.add(threadVarDeclaration(node));
		node.add(procedureFuncionDeclarationPart(node));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode labelDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
	}

	private SyntaticTreeNode constantDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
	}

	private SyntaticTreeNode resourceStringDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
	}

	private SyntaticTreeNode typeDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
	}

	private SyntaticTreeNode variableDeclarationPart(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "variableDeclarationPart";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION_PART);

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.VAR)));
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

	private SyntaticTreeNode threadVarDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		return null;
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
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));

		// :
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));

		// type
		node.add(type(node));

		// op = expression
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.EQUAL)));
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
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

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
		node.add(new SyntaticTreeNode(node, method, null, expect(types)));

		logger.exit();
		return node;
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
			syntaticTreeNode = new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON));
			rollbackSemicolon =	node.add(syntaticTreeNode);
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.CVAR)));
		} catch (SyntacticAutomataException e) {
			// extra rollback para semicolon
			if (rollbackSemicolon){
				this.rollback();
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
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.PROCEDURE)));
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));
		node.add(formalParameterList(node));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

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
					node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.FORWARD)));
				}
			}
		}

		logger.exit();
		return node;
	}

	private SyntaticTreeNode externalDirective(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		unimplemented();
		return null;
	}

	private SyntaticTreeNode asmBlock(SyntaticTreeNode parent) throws SyntacticAutomataException {
		unimplemented();
		return null;
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
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.FUNCTION)));
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));
		node.add(formalParameterList(node));
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.TYPE, expect(resultTypes)));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (SyntacticAutomataException e) {
		}
		node.add(hintDirective(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		logger.exit();
		return node;
	}

	/**
	 * Não implementado. Veja o link para ver detalhes.
	 * <a>http://www.freepascal.
	 * org/docs-html/ref/refse87.html#x173-18300014.9</a>
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
		unimplemented();
		return null;
	}

	private SyntaticTreeNode destructorDeclaration(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		unimplemented();
		return null;
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

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.OPEN_PARENTHESIS)));

		node.add(parameterDeclaration(node));
		try {
			while (true) {
				node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
				node.add(parameterDeclaration(node));
			}
		} catch (SyntacticAutomataException e) {
		}

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.CLOSE_PARENTHESIS)));

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
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.ARRAY)));
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.OF)));
			} catch (SyntacticAutomataException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (SyntacticAutomataException eList) {
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
					expect(OperatorSymbols.ID)));
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));
			// node.add(typeIdentifier(node));
			node.add(type(node));
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.EQUAL)));
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.CONSTANT,
					expect(OperatorSymbols.ID)));
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

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.VAR)));
		node.add(identifierList(node));

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));
			try {
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.ARRAY)));
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.OF)));
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

		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COMMA)));
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
			Token lexicToken = expect(hints);
			node.add(new SyntaticTreeNode(node, method, null, lexicToken));

			try {
				if (lexicToken.getSymbol().equals(WordSymbols.DEPRECATED)) {
					node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
							expect(OperatorSymbols.ID)));
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

	private SyntaticTreeNode compoundStatement(SyntaticTreeNode parent)
			throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "compoundStatement";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.COMPOUND_STATEMENT);

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.BEGIN)));
		while (true) {
			try {
				node.add(statement(node));
				try {
					node.add(new SyntaticTreeNode(node, method, null,
							expect(OperatorSymbols.SEMICOLON)));
				} catch (SyntacticAutomataException e) {
					break;
				}
			} catch (SyntacticAutomataException e) {
				break;
			}
		}
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.END)));

		logger.exit();
		return node;
	}

	private SyntaticTreeNode statement(SyntaticTreeNode parent) throws SyntacticAutomataException {
		return cmd(parent);
	}

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
		node.add(new SyntaticTreeNode(node, method, null, lexicToken));

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

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));

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
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
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

	private SyntaticTreeNode cmd(SyntaticTreeNode parent) throws SyntacticAutomataException {
		SyntaticTreeNode node;
		String method = "cmd";

		logger.entry();
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> comandosPossiveis = new LinkedList<Symbol>();
		comandosPossiveis.add(CommandWordSymbols.READ);
		comandosPossiveis.add(CommandWordSymbols.WRITE);
		comandosPossiveis.add(WordSymbols.WHILE);
		comandosPossiveis.add(WordSymbols.REPEAT);
		comandosPossiveis.add(WordSymbols.IF);
		comandosPossiveis.add(OperatorSymbols.ID);
		comandosPossiveis.add(OperatorSymbols.SEMICOLON);
		comandosPossiveis.add(WordSymbols.BEGIN);

		Token token = expect(comandosPossiveis);
		Symbol symbol = token.getSymbol();
		if (symbol instanceof CommandWordSymbols) {
			CommandWordSymbols command = (CommandWordSymbols) symbol;
			switch (command) {
			case READ:
			case WRITE:
				expect(OperatorSymbols.OPEN_PARENTHESIS);
				node.add(identifierList(node));
				expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
				break;
			default:
				logger.error("UNKNOWN COMMAND " + command.getName());
				break;
			}
		} else if (symbol instanceof WordSymbols) {
			WordSymbols word = (WordSymbols) symbol;
			switch (word) {
			case WHILE:
				node.add(condicao(node));
				expect(WordSymbols.DO);
				node.add(cmd(node));
				break;
			case REPEAT:
				node.add(cmd(node));
				expect(WordSymbols.UNTIL);
				node.add(condicao(node));
				break;
			case IF:
				node.add(condicao(node));
				expect(WordSymbols.THEN);
				node.add(cmd(node));
				node.add(pfalsa(node));
				break;
			case BEGIN:
				node.add(comandos(node));
				expect(WordSymbols.END);
				break;
			default:
				logger.error("UNKNOWN or MISPLACED WORD " + word.getName());
				break;
			}
		} else if (symbol instanceof OperatorSymbols) {
			OperatorSymbols operator = (OperatorSymbols) symbol;
			switch (operator) {
			case ID:
				try {
					expect(OperatorSymbols.COLON_EQUAL);
					node.add(expressao(node));
				} catch (SyntacticAutomataException e) {
					node.add(lista_arg(node));
				}
				break;
			default:
				logger.error("UNKNOWN or MISPLACED OPERATOR " + operator.getName());
				break;
			}
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
