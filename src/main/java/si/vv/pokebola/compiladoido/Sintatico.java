package si.vv.pokebola.compiladoido;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class Sintatico {
	
	private final static String LOG_LEVEL_PROP = Sintatico.class.getCanonicalName()+".logLevel";
	private Logger logger;
	
	private Lexico lexico;
	
	public Sintatico(Lexico lexico) throws IOException {
		initLogger();
		this.lexico = lexico;
	}
	
	private void initLogger(){
		logger = Logger.getLogger(Sintatico.class.getName());
		String logLevel = Compiladoido.getInstance().getProperties().getProperty(LOG_LEVEL_PROP); 
		logger.setLevel(Level.parse(logLevel));		
	}
	
	public void parse(){
		
	}
	
	public List<Token> getExpression(){
		return null;
	}
	
	public class AutomatoException extends Exception{
		public AutomatoException(Symbol expected) {
			Sintatico.this.logger.warning(expected.getName() + " EXPECTED");
		}

		public AutomatoException(Collection<? extends Symbol> expected) {
			String msg = "";
			for (Symbol symbol : expected) {
				msg += symbol.getName() + ", ";
			}
			msg += " EXPECTED";
			Sintatico.this.logger.warning(msg);
		}
	}
	
	public class Automato {
		
		private Lexico lexico;
		
		public Automato(Lexico lexico){
			this.lexico = lexico;	
		}
		
		public void run() throws AutomatoException{
			programa();
		}
		
		private Symbol expect(Symbol expected) throws AutomatoException{
			Symbol symbol;
			symbol = lexico.getToken().getSymbol();
			if (!symbol.equals(expected)){
				lexico.rollback();
				throw new AutomatoException(expected);
			}
			return symbol;
		}
		
		private Symbol expect(Collection<? extends Symbol> expected) throws AutomatoException {
			Symbol symbol;
			symbol = lexico.getToken().getSymbol();
			if (!expected.contains(symbol)){
				lexico.rollback();
				throw new AutomatoException(expected);
			}
			return symbol;
		}
		
		
		private void programa() throws AutomatoException{
			expect(WordSymbols.PROGRAM);
			expect(OperatorSymbols.ID);
			expect(OperatorSymbols.SEMICOLON);
			corpo();
			expect(OperatorSymbols.POINT);
		}
		
		private void corpo() throws AutomatoException{
			declaracoes();
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
			expect(OperatorSymbols.SEMICOLON);
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
					logger.severe("UNKNOWN COMMAND " + command.getName());
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
					logger.severe("UNKNOWN or MISPLACED WORD " + word.getName());
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
					logger.severe("UNKNOWN or MISPLACED OPERATOR " + operator.getName());
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
					logger.severe("UNKNOWN FACTOR " + fator.getName());
					break;
				}
			} 
		}

		
	}
	
}
