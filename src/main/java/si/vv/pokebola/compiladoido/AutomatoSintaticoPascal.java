package si.vv.pokebola.compiladoido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

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

	public SyntaticTreeNode run() throws AutomatoException {
		root = program(new SyntaticTreeNode(null, "run", null));
		return root;
	}

	private Token getTokenLexico() {
		Symbol symbol;
		Token token;
		token = lexico.getToken();
		symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols && ((OperatorSymbols) symbol).isComment()) {
			this.sintatico.getLogger().fine("Got a COMMENT");
			token = this.getTokenLexico();
		}
		return token;
	}

	private void unimplemented() throws AutomatoException {
		throw new AutomatoException(this.sintatico.getLogger());
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
			throw new AutomatoException(this.sintatico.getLogger(), expected, optional);
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
		return expect(expected, false);
	}

	/* ***************************************************************** */

	/*
	 * BEGIN program
	 * http://www.freepascal.org/docs-html/ref/refse95.html#x201-21100016.1
	 */

	private SyntaticTreeNode program(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "program";

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PROGRAM);
		/** PROGRAM */
		// PROGRAM HEADER
		node.add(programHeader(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
		// op USES CLAUSE
		try {
			node.add(usesClause(node));
		} catch (AutomatoException e) {
			e.log();
			this.sintatico.getLogger().finer("No USES CLAUSE declaration");
		}
		// BLOCK
		node.add(block(node));

		// PERIOD
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.POINT)));

		return node;
	}

	private SyntaticTreeNode programHeader(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "programHeader";

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
			this.sintatico.getLogger().finer("No PROGRAM PARAMETERS declaration");
		}

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

		return node;
	}

	/* END program */

	/*
	 * BEGIN block definition
	 * http://www.freepascal.org/docs-html/ref/refse98.html#x204-21400016.4
	 */

	private SyntaticTreeNode block(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "usesClause";

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.USE_CLAUSE);

		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.USES, true)));

		// DECLARATION PART
		node.add(declarationPart(node));

		// STATEMENT PART
		node.add(compoundStatement(node));

		return node;
	}

	private SyntaticTreeNode declarationPart(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "declarationPart";

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

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION_PART);

		try {
			node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
					expect(WordSymbols.VAR)));
		} catch (AutomatoException e) {
			return node;
		}

		while (true) {
			try {
				node.add(variableDeclaration(node));
			} catch (AutomatoException e) {
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

		node = new SyntaticTreeNode(parent, method,
				SyntaticSymbol.PROCEDURE_FUNCTION_DECLARATION_PART);

		while (errors < 4) {
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

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.VAR_DECLARATION);

		// id
		node.add(new SyntaticTreeNode(node, method, SyntaticSymbol.IDENTIFIER,
				expect(OperatorSymbols.ID)));

		// :
		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));

		// type
		node.add(new SyntaticTreeNode(node, method, null, expect(WordSymbols.TYPE)));

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

		return node;
	}

	private SyntaticTreeNode variableModifiers(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variableModifiers";

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

		return node;
	}

	private SyntaticTreeNode subroutineBlock(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "subroutineBlock";

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
		return null;
	}

	private SyntaticTreeNode destructorDeclaration(SyntaticTreeNode parent)
			throws AutomatoException {
		return null;
	}

	/*
	 * BEGIN parameter lists
	 * http://www.freepascal.org/docs-html/ref/refse82.html#x162-17200014.4
	 */

	private SyntaticTreeNode formalParameterList(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "formalParameterList";

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.FORMAL_PARAMETER_LIST);

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.OPEN_PARENTHESIS)));

		try {
			while (true) {
				node.add(parameterDeclaration(node));
				node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
			}
		} catch (AutomatoException e) {
		}

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.CLOSE_PARENTHESIS)));

		return node;
	}

	private SyntaticTreeNode parameterDeclaration(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "parameterDeclaration";

		node = new SyntaticTreeNode(parent, method, SyntaticSymbol.PARAMETER_DECLARATION);

		try {
			node.add(valueParameter(node));
		} catch (AutomatoException e) {

		}

		return node;
	}

	private SyntaticTreeNode valueParameter(SyntaticTreeNode parent) throws AutomatoException {
		unimplemented();
		return null;
	}

	/* END parameter lists */

	private SyntaticTreeNode hintDirective(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "hintDirective";
		Collection<WordSymbols> hints = new LinkedList<WordSymbols>();

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

		return node;
	}

	private SyntaticTreeNode expression(SyntaticTreeNode parent) throws AutomatoException {
		return expressao(parent);
	}

	private SyntaticTreeNode compoundStatement(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "compoundStatement";

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

		return node;
	}

	private SyntaticTreeNode statement(SyntaticTreeNode parent) throws AutomatoException {
		return cmd(parent);
	}

	/* ********************************************************************************************************
	 */

	private SyntaticTreeNode variaveis(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "variaveis";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.ID)));

		node.add(maisVariaveis(node));

		return node;
	}

	private SyntaticTreeNode maisVariaveis(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisVariaveis";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COMMA)));

			node.add(variaveis(node));
		} catch (AutomatoException e) {
			return null;
		}

		return node;
	}

	private SyntaticTreeNode tipoVariavel(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "tipoVariavel";

		node = new SyntaticTreeNode(parent, method, null);

		Collection<TypeWordSymbols> tipos = new LinkedList<TypeWordSymbols>();
		tipos.add(TypeWordSymbols.REAL);
		tipos.add(TypeWordSymbols.INTEGER);

		Token lexicToken = expect(tipos);
		node.add(new SyntaticTreeNode(node, method, null, lexicToken));

		return node;
	}

	private SyntaticTreeNode listaParametros(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "listaParametros";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(variaveis(node));

		node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.COLON)));

		node.add(tipoVariavel(node));
		node.add(maisParametros(node));

		return node;
	}

	private SyntaticTreeNode maisParametros(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisParametros";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(new SyntaticTreeNode(node, method, null, expect(OperatorSymbols.SEMICOLON)));
		} catch (AutomatoException e) {
			return null;
		}

		node.add(listaParametros(node));

		return node;
	}

	private SyntaticTreeNode corpoProcedimento(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "corpoProcedimento";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(declaracaoVariavel(node));
		expect(WordSymbols.BEGIN);
		node.add(comandos(node));
		expect(WordSymbols.END);

		return node;
	}
	
	private SyntaticTreeNode declaracaoVariavel(SyntaticTreeNode parent) throws AutomatoException{
		SyntaticTreeNode node;
		String method = "corpoProcedimento";

		node = new SyntaticTreeNode(parent, method, null);
		
		try {
			expect(WordSymbols.VAR);
		} catch (AutomatoException e){
			return node;
		}
		node.add(variaveis(node));
		expect(OperatorSymbols.COLON);
		node.add(tipoVariavel(node));
		expect(OperatorSymbols.SEMICOLON);
		node.add(declaracaoVariavel(node));
		
		return node;
	}

	private SyntaticTreeNode lista_arg(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "lista_arg";

		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.OPEN_PARENTHESIS);
		node.add(argumentos(node));
		expect(OperatorSymbols.CLOSE_PARENTHESIS);

		return node;
	}

	private SyntaticTreeNode argumentos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "argumentos";

		node = new SyntaticTreeNode(parent, method, null);

		expect(OperatorSymbols.ID);
		node.add(maisIDs(node));

		return node;
	}

	private SyntaticTreeNode maisIDs(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisIDs";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e) {
			return node;
		}
		node.add(argumentos(node));

		return node;
	}

	private SyntaticTreeNode pfalsa(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "pfalsa";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			expect(WordSymbols.ELSE);
		} catch (AutomatoException e) {
			return node;
		}
		node.add(cmd(node));

		return node;
	}

	private SyntaticTreeNode comandos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "comandos";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(cmd(node));
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e) {
		}

		return node;
	}

	private SyntaticTreeNode cmd(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "cmd";

		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> comandosPossiveis = new LinkedList<Symbol>();
		comandosPossiveis.add(CommandWordSymbols.READ);
		comandosPossiveis.add(CommandWordSymbols.WRITE);
		comandosPossiveis.add(WordSymbols.WHILE);
		comandosPossiveis.add(WordSymbols.REPEAT);
		comandosPossiveis.add(WordSymbols.IF);
		comandosPossiveis.add(OperatorSymbols.ID);
		comandosPossiveis.add(WordSymbols.BEGIN);

		Token token = expect(comandosPossiveis);
		Symbol symbol = token.getSymbol();
		if (symbol instanceof CommandWordSymbols) {
			CommandWordSymbols command = (CommandWordSymbols) symbol;
			switch (command) {
			case READ:
			case WRITE:
				expect(OperatorSymbols.OPEN_PARENTHESIS);
				node.add(variaveis(node));
				expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
				break;
			default:
				this.sintatico.getLogger().severe("UNKNOWN COMMAND " + command.getName());
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
				this.sintatico.getLogger().severe("UNKNOWN or MISPLACED WORD " + word.getName());
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
				this.sintatico.getLogger().severe(
						"UNKNOWN or MISPLACED OPERATOR " + operator.getName());
				break;
			}
		}

		return node;
	}

	private SyntaticTreeNode condicao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "condicao";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(expressao(node));
		node.add(relacao(node));
		node.add(expressao(node));

		return node;
	}

	private SyntaticTreeNode relacao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "relacao";

		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> relacoesPossiveis = new LinkedList<Symbol>();
		relacoesPossiveis.add(OperatorSymbols.EQUAL);
		relacoesPossiveis.add(OperatorSymbols.NOT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.LT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT);
		relacoesPossiveis.add(OperatorSymbols.LT);

		expect(relacoesPossiveis);

		return node;
	}

	private SyntaticTreeNode expressao(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "expressao";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(termo(node));
		node.add(outrosTermos(node));

		return node;
	}

	private SyntaticTreeNode outrosTermos(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "outrosTermos";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(operadorUnario(node));
		} catch (AutomatoException e) {
			return node;
		}
		node.add(termo(node));
		node.add(outrosTermos(node));

		return node;
	}

	private SyntaticTreeNode termo(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "termo";

		node = new SyntaticTreeNode(parent, method, null);

		try {
			node.add(operadorBinario(node));
		} catch (AutomatoException e) {
			return node;
		}
		node.add(fator(node));
		node.add(maisFatores(node));

		return node;
	}

	private SyntaticTreeNode maisFatores(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "maisFatores";

		node = new SyntaticTreeNode(parent, method, null);

		node.add(operadorBinario(node));
		node.add(fator(node));

		return node;
	}

	private SyntaticTreeNode operadorBinario(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "operadorBinario";

		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.ASTERISK);
		operadoresPossiveis.add(OperatorSymbols.FOWARD_SLASH);

		expect(operadoresPossiveis);

		return node;
	}

	private SyntaticTreeNode operadorUnario(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "operadorUnario";

		node = new SyntaticTreeNode(parent, method, null);

		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.PLUS);
		operadoresPossiveis.add(OperatorSymbols.MINUS);

		expect(operadoresPossiveis);

		return node;
	}

	private SyntaticTreeNode fator(SyntaticTreeNode parent) throws AutomatoException {
		SyntaticTreeNode node;
		String method = "fator";

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
				this.sintatico.getLogger().severe("UNKNOWN FACTOR " + fator.getName());
				break;
			}
		}

		return node;
	}

}