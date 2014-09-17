package si.vv.pokebola.compiladoido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.SyntaticSymbol;
import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class AutomatoSintaticoPascal {

	/**
	 * 
	 */
	private final Sintatico sintatico;
	private Lexico lexico;

	private SyntaticTreeNode root;

	public AutomatoSintaticoPascal(Sintatico sintatico, Lexico lexico) {
		this.sintatico = sintatico;
		this.lexico = lexico;
	}

	public SyntaticTreeNode getRoot() {
		return root;
	}

	public void setRoot(SyntaticTreeNode root) {
		this.root = root;
	}

	private Logger getLogger() {
		return this.sintatico.getLogger();
	}

	private void entering(String method) {
		getLogger().entering(getClass().getName(), method);
	}

	private void exiting(String method) {
		getLogger().exiting(getClass().getName(), method);
	}

	public SyntaticTreeNode run() throws AutomatoException {
		root = new SyntaticTreeNode(null, "run", null);

		root.add(program(root));
		return root;
	}

	private Token getTokenLexico() {
		Symbol symbol;
		Token token;
		token = lexico.getToken();
		symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols && ((OperatorSymbols) symbol).isComment()) {
			getLogger().fine("Got a COMMENT");
			token = this.getTokenLexico();
		}
		getLogger().fine("\n\t" + token.toString());
		return token;
	}

	private void unimplemented() throws AutomatoException {
		throw new AutomatoException(getLogger());
	}

	// implementacao
	private Token expect(Collection<? extends Symbol> expected, boolean optional)
			throws AutomatoException {
		Symbol symbol;
		Token token;
		token = this.getTokenLexico();
		symbol = token.getSymbol();
		if (!expected.contains(symbol)) {
			lexico.rollback();
			throw new AutomatoException(getLogger(), expected, optional, symbol);
		}

		return token;
	}

	// referencias
	private Token expect(Collection<? extends Symbol> expected) throws AutomatoException {
		return this.expect(expected, false);
	}

	private Token expect(Symbol expected, boolean optional) throws AutomatoException {
		Collection<Symbol> symbols = new ArrayList<Symbol>(1);
		symbols.add(expected);
		return this.expect(symbols, optional);
	}

	private Token expect(Symbol expected) throws AutomatoException {
		Collection<Symbol> symbols = new ArrayList<Symbol>(1);
		symbols.add(expected);
		return expect(symbols, false);
	}

	/* ***************************************************************** */

	/*
	 * BEGIN program
	 * http://www.freepascal.org/docs-html/ref/refse95.html#x201-21100016.1
	 */

	private SyntaticTreeNode program(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "program";

		entering(method);
		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROGRAM);
		/** PROGRAM */
		// PROGRAM HEADER
		node.add(programHeader(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
		// op USES CLAUSE
		try {
			node.add(usesClause(node));
		} catch (AutomatoException e) {
			getLogger().finer("No USES CLAUSE declaration");
		}
		// BLOCK
		node.add(block(node));

		// PERIOD
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.POINT)));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode programHeader(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "programHeader";

		entering(method);
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
		} catch (AutomatoException e) {
			e.log();
			getLogger().finer("No PROGRAM PARAMETERS declaration");
		}

		exiting(method);
		return node;
	}

	/**
	 * The program header is provided for backwards compatibility, and is
	 * ignored by the compiler.
	 * 
	 * @param parent
	 * @return
	 * @throws AutomatoException
	 */
	private SyntaticTreeNode programParameters(SyntaticTreeNode parent) throws AutomatoException {
		// return indentifierList(parent);
		return null;
	}

	private SyntaticTreeNode usesClause(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "usesClause";

		entering(method);
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

		} catch (AutomatoException e) {
		}
		// op MORE USES
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COMMA)));

			node.add(usesClause(node));
		} catch (AutomatoException e) {
		}
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		exiting(method);
		return node;
	}

	/* END program */

	/*
	 * BEGIN block definition
	 * http://www.freepascal.org/docs-html/ref/refse98.html#x204-21400016.4
	 */

	private SyntaticTreeNode block(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "block";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.BLOCK);

		// DECLARATION PART
		node.add(declarationPart(node));

		// STATEMENT PART
		node.add(compoundStatement(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode declarationPart(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "declarationPart";

		entering(method);
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

		exiting(method);
		return node;
	}

	private SyntaticTreeNode labelDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode constantDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode resourceStringDeclaration(SyntaticTreeNode parent)
			throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode typeDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode variableDeclarationPart(SyntaticTreeNode parent)
			throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variableDeclarationPart";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION_PART);

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.VAR)));
		} catch (AutomatoException e) {
			return node;
		}

		while (true) {
			try {
				node.add(variableDeclaration(node));
			} catch (AutomatoException e) {
				exiting(method);
				return node;
			}
		}
	}

	private SyntaticTreeNode threadVarDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode procedureFuncionDeclarationPart(SyntaticTreeNode parent)
			throws AutomatoException {
		SyntaticTreeNode node;
		String method = "procedureFuncionDeclarationPart";
		int errors = 0;

		entering(method);
		node = new SyntaticTreeNode(parent, method,
				SyntaticSymbol.PROCEDURE_FUNCTION_DECLARATION_PART);

		while (errors < 4) {
			getLogger().fine("Geting a procedure/function");
			try {
				node.add(procedureDeclaration(node));
			} catch (AutomatoException e) {
				errors++;
			}
			try {
				node.add(functionDeclaration(node));
			} catch (AutomatoException e) {
				errors++;
			}
			try {
				node.add(constructorDeclaration(node));
			} catch (AutomatoException e) {
				errors++;
			}
			try {
				node.add(destructorDeclaration(node));
			} catch (AutomatoException e) {
				errors++;
			}
		}

		exiting(method);
		return node;
	}

	/* END block definition */

	/*
	 * BEGIN var delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	private SyntaticTreeNode variableDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variableDeclaration";

		entering(method);
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
		} catch (AutomatoException e) {
		}

		// op variable modifiers
		try {
			node.add(variableModifiers(node));
		} catch (AutomatoException e) {
		}

		// hint directive
		node.add(hintDirective(node));

		// ;
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		exiting(method);
		return node;
	}

	/**
	 * <a>http://www.freepascal.org/docs-html/ref/refch3.html#x25-240003</a>
	 * 
	 * @param parent
	 * @return
	 * @throws AutomatoException
	 */
	private SyntaticTreeNode type(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "type";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.TYPE);

		Collection<TypeWordSymbols> types = TypeWordSymbols.INTEGER.allMap().values();
		node.add(new SyntaticTreeNode(node, method, null, expect(types)));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode variableModifiers(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variableModifiers";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_MODIFIERS);

		/**
		 * mais uma vez,em FPC a ordem não importa, mas aqui sim
		 */
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.ABSOLUTE)));

			// integer expression or id, I go for id only
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
					expect(OperatorSymbols.ID)));
		} catch (AutomatoException e) {
		}

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.EXPORT)));
		} catch (AutomatoException e) {
		}

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.CVAR)));
		} catch (AutomatoException e) {
		}

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.EXTERMAL)));
			try {
				node.add(new SyntaticTreeNode(node, method, null,
						expect(OperatorSymbols.STRING_CONSTANT)));
			} catch (AutomatoException e) {
			}
			try {
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.NAME)));
				node.add(new SyntaticTreeNode(node, method, null,
						expect(OperatorSymbols.STRING_CONSTANT)));
			} catch (AutomatoException e) {
			}
		} catch (AutomatoException e) {
		}

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.NAME)));
		} catch (AutomatoException e) {
		}

		exiting(method);
		return node;
	}

	/* END var delcaration */

	/*
	 * BEGIN procedure delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	private SyntaticTreeNode procedureDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "procedureDeclaration";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROCEDURE_DECLARATION);

		// PROCEDURE HEADER
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.PROCEDURE)));
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));
		node.add(formalParameterList(node));
		try {
			node.add(functionProcedureModifiers(node));
		} catch (AutomatoException e) {
		}
		node.add(hintDirective(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode subroutineBlock(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "subroutineBlock";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.SUBROTINE_BLOCK);

		try {
			node.add(block(node));
		} catch (AutomatoException eBlock) {
			eBlock.log();

			try {
				node.add(externalDirective(node));
			} catch (AutomatoException eExternalDirective) {
				try {
					node.add(asmBlock(node));
				} catch (AutomatoException eAsmBlock) {
					node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.FORWARD)));
				}
			}
		}

		exiting(method);
		return node;
	}

	private SyntaticTreeNode externalDirective(SyntaticTreeNode parent) throws AutomatoException {
		unimplemented();
		return null;
	}

	private SyntaticTreeNode asmBlock(SyntaticTreeNode parent) throws AutomatoException {
		unimplemented();
		return null;
	}

	/* END procedure delcaration */

	private SyntaticTreeNode functionDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "functionDeclaration";
		Collection<TypeWordSymbols> resultTypes = new LinkedList<TypeWordSymbols>();
		for (TypeWordSymbols type : TypeWordSymbols.values()) {
			resultTypes.add(type);
		}

		entering(method);
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
		} catch (AutomatoException e) {
		}
		node.add(hintDirective(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		// SUBROUTINE BLOCK
		node.add(subroutineBlock(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));

		exiting(method);
		return node;
	}

	/**
	 * Não implementado. Veja o link para ver detalhes.
	 * <a>http://www.freepascal.
	 * org/docs-html/ref/refse87.html#x173-18300014.9</a>
	 * 
	 * @param parent
	 * @return
	 * @throws AutomatoException
	 */
	private SyntaticTreeNode functionProcedureModifiers(SyntaticTreeNode parent)
			throws AutomatoException {
		return null;
	}

	private SyntaticTreeNode constructorDeclaration(SyntaticTreeNode parent)
			throws AutomatoException {
		unimplemented();
		return null;
	}

	private SyntaticTreeNode destructorDeclaration(SyntaticTreeNode parent)
			throws AutomatoException {
		unimplemented();
		return null;
	}

	/*
	 * BEGIN parameter lists
	 * http://www.freepascal.org/docs-html/ref/refse82.html#x162-17200014.4
	 */

	private SyntaticTreeNode formalParameterList(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "formalParameterList";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.FORMAL_PARAMETER_LIST);

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.OPEN_PARENTHESIS)));

		node.add(parameterDeclaration(node));
		try {
			while (true) {
				node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
				node.add(parameterDeclaration(node));
			}
		} catch (AutomatoException e) {
		}

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.CLOSE_PARENTHESIS)));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode parameterDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "parameterDeclaration";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PARAMETER_DECLARATION);

		try {
			node.add(valueParameter(node));
		} catch (AutomatoException e) {
			node.add(variableParameter(node));
		}

		exiting(method);
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu60.html#x163-17300014.4.1
	 * 
	 * @param parent
	 * @return
	 * @throws AutomatoException
	 */
	private SyntaticTreeNode valueParameter(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "valueParameter";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VALUE_PARAMETER);

		try {
			node.add(identifierList(node));
			try {
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.ARRAY)));
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.OF)));
			} catch (AutomatoException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (AutomatoException eList) {
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
					expect(OperatorSymbols.ID)));
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));
			// node.add(typeIdentifier(node));
			node.add(type(node));
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.EQUAL)));
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.CONSTANT,
					expect(OperatorSymbols.ID)));
		}

		exiting(method);
		return node;
	}

	/**
	 * http://www.freepascal.org/docs-html/ref/refsu61.html#x164-17400014.4.2
	 * 
	 * @param parent
	 * @return
	 * @throws AutomatoException
	 */
	private SyntaticTreeNode variableParameter(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variableParameter";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VARIABLE_PARAMETER);

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.VAR)));
		node.add(identifierList(node));

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));
			try {
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.ARRAY)));
				node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.OF)));
			} catch (AutomatoException eList) {
			}
			// node.add(parameterType(node));
			node.add(type(node));
		} catch (AutomatoException eList) {
		}

		exiting(method);
		return node;
	}

	private SyntaticTreeNode identifierList(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "identifierList";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.IDENTIFIER_LIST);

		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));
		
		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COMMA)));
			identifierList(node);
		} catch (AutomatoException e) {
		}

		exiting(method);
		return node;
	}

	/* END parameter lists */

	private SyntaticTreeNode hintDirective(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "hintDirective";
		Collection<WordSymbols> hints = new LinkedList<WordSymbols>();

		entering(method);
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
			} catch (AutomatoException e) {
			}
		} catch (AutomatoException e) {
		}

		exiting(method);
		return node;
	}

	private SyntaticTreeNode expression(SyntaticTreeNode parent) throws AutomatoException {
		return expressao(parent);
	}

	private SyntaticTreeNode compoundStatement(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "compoundStatement";

		entering(method);
		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.COMPOUND_STATEMENT);

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.BEGIN)));
		while (true) {
			node.add(statement(node));
			try {
				node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
			} catch (AutomatoException e) {
				break;
			}
		}
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.END)));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode statement(SyntaticTreeNode parent) throws AutomatoException {
		return cmd(parent);
	}

	/* ********************************************************************************************************
	 */

	private SyntaticTreeNode tipoVariavel(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "tipoVariavel";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		Collection<TypeWordSymbols> tipos = new LinkedList<TypeWordSymbols>();
		tipos.add(TypeWordSymbols.REAL);
		tipos.add(TypeWordSymbols.INTEGER);

		Token lexicToken = expect(tipos);
		node.add(new SyntaticTreeNode(node, method, null, lexicToken));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode listaParametros(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "listaParametros";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		node.add(identifierList(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));

		node.add(tipoVariavel(node));
		node.add(maisParametros(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode maisParametros(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisParametros";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
		} catch (AutomatoException e) {
			return null;
		}

		node.add(listaParametros(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode lista_arg(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "lista_arg";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.OPEN_PARENTHESIS);
		node.add(argumentos(node));
		expect(OperatorSymbols.CLOSE_PARENTHESIS);

		exiting(method);
		return node;
	}

	private SyntaticTreeNode argumentos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "argumentos";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.ID);
		node.add(maisIDs(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode maisIDs(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisIDs";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e) {
			exiting(method);
			return node;
		}

		node.add(argumentos(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode pfalsa(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "pfalsa";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(WordSymbols.ELSE);
		} catch (AutomatoException e) {
			exiting(method);
			return node;
		}
		node.add(cmd(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode comandos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "comandos";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		node.add(cmd(node));
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e) {
		}

		exiting(method);
		return node;
	}

	private SyntaticTreeNode cmd(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "cmd";

		entering(method);
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
				getLogger().severe("UNKNOWN COMMAND " + command.getName());
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
				getLogger().severe("UNKNOWN or MISPLACED WORD " + word.getName());
				break;
			}
		} else if (symbol instanceof OperatorSymbols) {
			OperatorSymbols operator = (OperatorSymbols) symbol;
			switch (operator) {
			case ID:
				try {
					expect(OperatorSymbols.COLON_EQUAL);
					node.add(expressao(node));
				} catch (AutomatoException e) {
					node.add(lista_arg(node));
				}
				break;
			default:
				getLogger().severe("UNKNOWN or MISPLACED OPERATOR " + operator.getName());
				break;
			}
		}

		exiting(method);
		return node;
	}

	private SyntaticTreeNode condicao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "condicao";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		node.add(expressao(node));
		node.add(relacao(node));
		node.add(expressao(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode relacao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "relacao";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> relacoesPossiveis = new LinkedList<Symbol>();
		relacoesPossiveis.add(OperatorSymbols.EQUAL);
		relacoesPossiveis.add(OperatorSymbols.NOT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.LT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT);
		relacoesPossiveis.add(OperatorSymbols.LT);

		expect(relacoesPossiveis);

		exiting(method);
		return node;
	}

	private SyntaticTreeNode expressao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "expressao";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		node.add(termo(node));
		node.add(outrosTermos(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode outrosTermos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "outrosTermos";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(operadorUnario(node));
		} catch (AutomatoException e) {
			exiting(method);
			return node;

		}
		node.add(termo(node));
		node.add(outrosTermos(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode termo(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "termo";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(operadorBinario(node));
		} catch (AutomatoException e) {
			exiting(method);
			return node;
		}

		node.add(fator(node));
		node.add(maisFatores(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode maisFatores(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisFatores";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		node.add(operadorBinario(node));
		node.add(fator(node));

		exiting(method);
		return node;
	}

	private SyntaticTreeNode operadorBinario(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "operadorBinario";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.ASTERISK);
		operadoresPossiveis.add(OperatorSymbols.FOWARD_SLASH);

		expect(operadoresPossiveis);

		exiting(method);
		return node;
	}

	private SyntaticTreeNode operadorUnario(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "operadorUnario";

		entering(method);
		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.PLUS);
		operadoresPossiveis.add(OperatorSymbols.MINUS);

		expect(operadoresPossiveis);

		exiting(method);
		return node;
	}

	private SyntaticTreeNode fator(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "fator";

		entering(method);
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
				getLogger().severe("UNKNOWN FACTOR " + fator.getName());
				break;
			}
		}

		exiting(method);
		return node;
	}
}
