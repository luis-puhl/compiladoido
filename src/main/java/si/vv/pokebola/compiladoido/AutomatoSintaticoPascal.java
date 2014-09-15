package si.vv.pokebola.compiladoido;

import java.util.Collection;
import java.util.LinkedList;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class AutomatoSintaticoPascal {
	
	/**
	 * 
	 */
	private final Sintatico sintatico;
	private Lexico lexico;
	
	public AutomatoSintaticoPascal(Sintatico sintatico, Lexico lexico){
		this.sintatico = sintatico;
		this.lexico = lexico;	
	}
	
	public void run() throws AutomatoException{
		program();
	}
	
	private Symbol getSymbolLexico(){
		Symbol symbol;
		Token token;
		token = lexico.getToken();
		symbol = token.getSymbol(); 
		if (symbol instanceof OperatorSymbols && ((OperatorSymbols) symbol).isComment() ){
			this.sintatico.getLogger().fine("Got a COMMENT");
			symbol = this.getSymbolLexico();
		}
		return symbol;
	}
	
	private Symbol expect(Symbol expected, boolean optional) throws AutomatoException{
		Symbol symbol;
		symbol = this.getSymbolLexico();
		if (!symbol.equals(expected)){
			lexico.rollback();
			throw new AutomatoException(this.sintatico.getLogger(), expected, optional);
		}
		return symbol;
	}
	
	private Symbol expect(Symbol expected) throws AutomatoException{
		return expect(expected, false);
	}
	
	private Symbol expect(Collection<? extends Symbol> expected) throws AutomatoException {
		Symbol symbol;
		symbol = this.getSymbolLexico();
		if (!expected.contains(symbol)){
			lexico.rollback();
			throw new AutomatoException(this.sintatico.getLogger(), expected);
		}
		return symbol;
	}
	
	/* ***************************************************************** */
	
	private void program() throws AutomatoException{
		/** PROGRAM */
		// PROGRAM HEADER
		programHeader();
		expect(OperatorSymbols.SEMICOLON);
		// op USES CLAUSE
		try {
			usesClause();
		} catch (AutomatoException e){
			e.log();
			this.sintatico.getLogger().finer("No USES CLAUSE declaration");
		}
		// BLOCK
		block();
		// PERIOD
		expect(OperatorSymbols.POINT);
	}
	
	private void programHeader() throws AutomatoException{
		expect(WordSymbols.PROGRAM);
		expect(OperatorSymbols.ID);
		// op PROGRAM PARAMETERS
		try {
			parametros();
		} catch (AutomatoException e){
			e.log();
			this.sintatico.getLogger().finer("No PROGRAM PARAMETERS declaration");
		}
	}
	
	private void usesClause() throws AutomatoException{
		expect(WordSymbols.USES, true);
		expect(OperatorSymbols.ID);
		// op STRING LITERAL
		try {
			expect(WordSymbols.IN);
			// deve ser STRING LITERAL
			expect(OperatorSymbols.ID);
		} catch (AutomatoException e){}
		// op MORE USES
		try {
			expect(OperatorSymbols.COMMA);
			usesClause();
		} catch (AutomatoException e){}
		expect(OperatorSymbols.SEMICOLON);
	}
	
	private void block() throws AutomatoException{
		// DECLARATION PART
		declaracoes();
		// STATEMENT PART
		expect(WordSymbols.BEGIN);
		comandos();
		expect(WordSymbols.END);
	}
	
	private void declaracoes() throws AutomatoException{
		declaracaoVariavel();
		declaracaoProcedimento();
	}
	
	private void declaracaoVariavel() throws AutomatoException{
		try {
			expect(WordSymbols.VAR);
		} catch (AutomatoException e){
			return;
		}
		variaveis();
		expect(OperatorSymbols.COLON);
		tipoVariavel();
		expect(OperatorSymbols.SEMICOLON);
		declaracaoVariavel();
	}
	
	private void variaveis() throws AutomatoException{
		expect(OperatorSymbols.ID);
		maisVariaveis();
	}
	
	private void maisVariaveis() throws AutomatoException{
		try {
			expect(OperatorSymbols.COMMA);
		} catch (AutomatoException e){
			return;
		}
		variaveis();
	}
	
	private void tipoVariavel() throws AutomatoException{
		Collection<TypeWordSymbols> tipos = new LinkedList<TypeWordSymbols>();
		tipos.add(TypeWordSymbols.REAL);
		tipos.add(TypeWordSymbols.INTEGER);
		expect(tipos);
	}
	
	private void declaracaoProcedimento() throws AutomatoException{
		try {
			expect(WordSymbols.PROCEDURE);
		} catch (AutomatoException e){
			return;
		}
		expect(OperatorSymbols.ID);
		parametros();
		expect(OperatorSymbols.SEMICOLON);
		corpoProcedimento();
		expect(OperatorSymbols.SEMICOLON);
		declaracaoProcedimento();
	}
	
	private void parametros() throws AutomatoException{
		try {
			expect(OperatorSymbols.OPEN_PARENTHESIS);
		} catch (AutomatoException e){
			return;
		}
		listaParametros();
		expect(OperatorSymbols.CLOSE_PARENTHESIS);
	}

	private void listaParametros() throws AutomatoException {
		variaveis();
		expect(OperatorSymbols.COLON);
		tipoVariavel();
		maisParametros();
	}

	private void maisParametros() throws AutomatoException {
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e){
			return;
		}
		listaParametros();
	}
	
	private void corpoProcedimento() throws AutomatoException {
		declaracaoVariavelLocal();
		expect(WordSymbols.BEGIN);
		comandos();
		expect(WordSymbols.END);
	}

	private void declaracaoVariavelLocal() throws AutomatoException {
		declaracaoVariavel();
	}
	
	private void lista_arg() throws AutomatoException{
		expect(OperatorSymbols.OPEN_PARENTHESIS);
		argumentos();
		expect(OperatorSymbols.CLOSE_PARENTHESIS);
	}

	private void argumentos() throws AutomatoException {
		expect(OperatorSymbols.ID);
		maisIDs();
	}

	private void maisIDs() throws AutomatoException {
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e) {
			return;
		}
		argumentos();
	}
	
	private void pfalsa() throws AutomatoException{
		try {
			expect(WordSymbols.ELSE);
		} catch (AutomatoException e) {
			return;
		}
		cmd();
	}
	
	private void comandos() throws AutomatoException {
		cmd();
		try {
			expect(OperatorSymbols.SEMICOLON);
		} catch (AutomatoException e){}
	}

	private void cmd() throws AutomatoException {
		Collection<Symbol> comandosPossiveis = new LinkedList<Symbol>();
		comandosPossiveis.add(CommandWordSymbols.READ);
		comandosPossiveis.add(CommandWordSymbols.WRITE);
		comandosPossiveis.add(WordSymbols.WHILE);
		comandosPossiveis.add(WordSymbols.REPEAT);
		comandosPossiveis.add(WordSymbols.IF);
		comandosPossiveis.add(OperatorSymbols.ID);
		comandosPossiveis.add(WordSymbols.BEGIN);
		
		Symbol symbol = expect(comandosPossiveis);
		if (symbol instanceof CommandWordSymbols){
			CommandWordSymbols command = (CommandWordSymbols) symbol; 
			switch (command) {
			case READ:
			case WRITE:
				expect(OperatorSymbols.OPEN_PARENTHESIS);
				variaveis();
				expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
				break;
			default:
				this.sintatico.getLogger().severe("UNKNOWN COMMAND " + command.getName());
				break;
			}
		} else if (symbol instanceof WordSymbols){
			WordSymbols word = (WordSymbols) symbol;
			switch (word) {
			case WHILE:
				condicao();
				expect(WordSymbols.DO);
				cmd();
				break;
			case REPEAT:
				cmd();
				expect(WordSymbols.UNTIL);
				condicao();
				break;
			case IF:
				condicao();
				expect(WordSymbols.THEN);
				cmd();
				pfalsa();
				break;
			case BEGIN:
				comandos();
				expect(WordSymbols.END);
				break;
			default:
				this.sintatico.getLogger().severe("UNKNOWN or MISPLACED WORD " + word.getName());
				break;
			}
		} else if (symbol instanceof OperatorSymbols){
			OperatorSymbols operator = (OperatorSymbols) symbol;
			switch (operator) {
			case ID:
				try {
					expect(OperatorSymbols.COLON_EQUAL);
					expressao();
				} catch (AutomatoException e) {
					lista_arg();
				}
				break;
			default:
				this.sintatico.getLogger().severe("UNKNOWN or MISPLACED OPERATOR " + operator.getName());
				break;
			}
		}
		
	}

	private void condicao() throws AutomatoException {
		expressao();
		relacao();
		expressao();
	}
	
	private void relacao() throws AutomatoException {
		Collection<Symbol> relacoesPossiveis = new LinkedList<Symbol>();
		relacoesPossiveis.add(OperatorSymbols.EQUAL);
		relacoesPossiveis.add(OperatorSymbols.NOT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.LT_EQUAL);
		relacoesPossiveis.add(OperatorSymbols.GT);
		relacoesPossiveis.add(OperatorSymbols.LT);
		
		expect(relacoesPossiveis);
	}
	
	private void expressao() throws AutomatoException {
		termo();
		outrosTermos();
	}

	private void outrosTermos() throws AutomatoException {
		try {
			operadorUnario();
		} catch (AutomatoException e){
			return;
		}
		termo();
		outrosTermos();
	}

	private void termo() throws AutomatoException {
		try {
			operadorBinario();
		} catch (AutomatoException e){
			return;
		}
		fator();
		maisFatores();
	}

	private void maisFatores() throws AutomatoException {
		operadorBinario();
		fator();
	}
	
	private void operadorBinario() throws AutomatoException {
		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.ASTERISK);
		operadoresPossiveis.add(OperatorSymbols.FOWARD_SLASH);
		
		expect(operadoresPossiveis);		
	}
	
	private void operadorUnario() throws AutomatoException {
		Collection<Symbol> operadoresPossiveis = new LinkedList<Symbol>();
		operadoresPossiveis.add(OperatorSymbols.PLUS);
		operadoresPossiveis.add(OperatorSymbols.MINUS);
		
		expect(operadoresPossiveis);
	}
	
	private void fator() throws AutomatoException {
		Collection<Symbol> fatoresPossiveis = new LinkedList<Symbol>();
		fatoresPossiveis.add(OperatorSymbols.ID);
		fatoresPossiveis.add(OperatorSymbols.OPEN_PARENTHESIS);
		
		Symbol symbol = expect(fatoresPossiveis);
		if (symbol instanceof OperatorSymbols){
			OperatorSymbols fator = (OperatorSymbols) symbol; 
			switch (fator) {
			case ID:
				break;
			case OPEN_PARENTHESIS:
				expressao();
				expect(OperatorSymbols.OPEN_PARENTHESIS.getMirror());
				break;
			default:
				this.sintatico.getLogger().severe("UNKNOWN FACTOR " + fator.getName());
				break;
			}
		} 
	}

	
}