package si.vv.pokebola.compiladoido;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.CommandWordSymbols;
import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;
import si.vv.pokebola.compiladoido.beans.WordSymbols;

public class Lexico {

	private Scanner inputScanner;
	private final static Charset ENCODING = StandardCharsets.UTF_8;

	private final static String LOG_LEVEL_PROP = Lexico.class
			.getCanonicalName() + ".logLevel";
	private Logger logger;

	private StringBuffer buffer;
	
	private Token rollbackToken = null;
	private boolean rollbackFlag = false;
	
	Map<String, OperatorSymbols> operatorsMap = OperatorSymbols.ADDRESS.allMap();
	Map<String, WordSymbols> wordsMap = WordSymbols.AND.allMap();
	Map<String, CommandWordSymbols> commandsMap = CommandWordSymbols.READ.allMap();
	Map<String, TypeWordSymbols> typesMap = TypeWordSymbols.INTEGER.allMap();

	public Lexico(String filename) throws IOException {
		initLogger();

		logger.info("geting scanner");
		try {
			Path path = Paths.get(filename);
			inputScanner = new Scanner(path, ENCODING.name());
		} catch (IOException ioException) {
			logger.warning("can't get scanner");
		}
	}
	
	public Lexico(StringBuffer buffer){
		initLogger();
		this.buffer = buffer;
	}
	
	public StringBuffer getBuffer(){
		return buffer;
	}

	private void initLogger() {
		Compiladoido compiladoido = Compiladoido.getInstance();
		
		logger = compiladoido.getLogger(Lexico.class, LOG_LEVEL_PROP);	
	}
	

	private boolean getTexto(StringBuffer buffer) {
		logger.entering(Lexico.class.getName(), "getTexto");
		if (inputScanner != null && inputScanner.hasNext()) {
			buffer.append(inputScanner.next());
			return true;
		}
		return false;
	}

	public Token getToken() {
		logger.entering(Lexico.class.getName(), "getToken");
		
		if (rollbackFlag){
			rollbackFlag = false;
			return rollbackToken;
		}
		
		if (buffer.length() == 0 && !this.getTexto(buffer)) {
			return null;
		}
		
		rollbackToken = this.consume(buffer);
		
		return rollbackToken;
	}

	enum Estado {
		LER_VAZIO, ID, NUMEROS, OPERADOR, COMENTARIO_LINHA, COMENTARIO_LONGO,
		END_TOKEN, TERMINADO, STRING, ESCAPE_STIRNG;
	}

	/**
	 * DFA - Deterministic finite automaton
	 * 
	 * @param buffer
	 * @param retSymbol 
	 * @return
	 */
	private Token consume(StringBuffer buffer) {
		logger.entering(Lexico.class.getName(), "consume");

		StringBuilder ret = new StringBuilder();
		Symbol simboloFechamento = null, symbol = OperatorSymbols.NONE;
		Token token;
		
		Estado estado;
		estado = Estado.LER_VAZIO;
		
		while (estado != Estado.TERMINADO && buffer.length() != 0) {
			char c;
			CharType type;
			
			c = buffer.charAt(0);
			type = classifieChar(c);

			logger.entering("consume", estado.name());
			// logger.info("buffer length is " + buffer.length());
			
			switch (estado) {
			case LER_VAZIO: // Estado inicial - Ignora espaços em branco
				switch (type) {
				case C_CHAR:
					estado = Estado.OPERADOR;
					
					String cString = Character.toString(c);
					if (cString != null){
						OperatorSymbols partialOperator = operatorsMap.get(cString);
						
						if (OperatorSymbols.QUOTE.equals(partialOperator)) {
							estado = Estado.STRING;
							simboloFechamento = partialOperator.getMirror();
						} else if (partialOperator.isComment()){
							if (partialOperator.isLine()){
								estado = Estado.COMENTARIO_LINHA;
							} 
							if (partialOperator.isMultiLine()){
								estado = Estado.COMENTARIO_LONGO;
								simboloFechamento = partialOperator.getMirror();
							}
						}
					}
					break;
				case C_NUMBER:
					estado = Estado.NUMEROS;
					symbol = OperatorSymbols.ID;
					break;
				case C_ALPHA:
					estado = Estado.ID;
					break;
				case C_NONE:
				default:
					estado = Estado.LER_VAZIO;
					break;
				}
				if (estado != Estado.LER_VAZIO) {
					ret.append(c);
				}
				buffer.deleteCharAt(0);
				break;

			case ID: // Le identificadores ( [0-9][A-z]{_} )
				if (type == CharType.C_ALPHA || type == CharType.C_NUMBER) {
					// executa acumulacao
					ret.append(c);
					buffer.deleteCharAt(0);

					estado = Estado.ID;
				} else {
					estado = Estado.END_TOKEN;
				}
				break;

			case NUMEROS: // Le apenas numeros
				if (type != CharType.C_NUMBER) {
					estado = Estado.END_TOKEN;
				} else {
					estado = Estado.NUMEROS;

					ret.append(c);
					buffer.deleteCharAt(0);
				}
				break;

			case OPERADOR:
				// recupera os dois chars
				char previous = ' ';
				previous = ret.charAt(ret.length() - 1);
				StringBuilder s = new StringBuilder(2);
				s.append(previous);
				s.append(c);
				String operador = s.toString();

				// verifica se existe um simbolo do tipo
				if (type == CharType.C_CHAR
						&& operatorsMap.containsKey(operador)) {
					estado = Estado.OPERADOR;
					
					ret.append(c);
					buffer.deleteCharAt(0);
					
					OperatorSymbols simbolo = operatorsMap.get(operador);
					
					if (simbolo.isLine())
						estado = Estado.COMENTARIO_LINHA;
					if (simbolo.isMultiLine())
						estado = Estado.COMENTARIO_LONGO;
					simboloFechamento = simbolo.getMirror();
					
				} else {
					estado = Estado.END_TOKEN;
				}
				break;
			
			
			case COMENTARIO_LINHA:
				symbol = OperatorSymbols.COMMENT;
				if (type == CharType.C_NONE){
					estado = Estado.END_TOKEN;
				} else {
					estado = Estado.COMENTARIO_LINHA;
					ret.append(c);
					buffer.deleteCharAt(0);
				}
				break;
				
			case COMENTARIO_LONGO:
				symbol = OperatorSymbols.COMMENT;
				
				StringBuilder operatorBuilder = new StringBuilder(2);
				Symbol currSimbolo; 
				
				if (simboloFechamento.toString().length() == 2){
					previous = ret.charAt(ret.length() - 1);
					operatorBuilder.append(previous);
				}
				operatorBuilder.append(c);
				
				currSimbolo = operatorsMap.get(operatorBuilder.toString().toUpperCase());
				
				ret.append(c);
				buffer.deleteCharAt(0);
				
				if (currSimbolo == simboloFechamento){
					estado = Estado.END_TOKEN;
				} else {
					estado = Estado.COMENTARIO_LONGO;
				}
				break;
				
			case END_TOKEN:
				estado = Estado.TERMINADO;
				break;
			
			case STRING:
				symbol = OperatorSymbols.ID;
				Symbol partialSymbol = operatorsMap.get(Character.toString(c));
				if (OperatorSymbols.BACK_SLASH.equals(partialSymbol)) {
					estado = Estado.ESCAPE_STIRNG;
				} else if (OperatorSymbols.QUOTE.equals(partialSymbol)) {
					estado = Estado.STRING;
					ret.append(c);
					estado = Estado.END_TOKEN;
				} else {
					ret.append(c);
				}
				
				buffer.deleteCharAt(0);
				break;
			case ESCAPE_STIRNG:
				ret.append(c);
				buffer.deleteCharAt(0);
				break;
			case TERMINADO:
			default:
				logger.warning("Estado inválido");
				break;
			}
		}
		
		String texto = ret.toString();
		
		token = new Token(OperatorSymbols.NONE, texto);
		
		if (OperatorSymbols.NONE.equals(symbol) || symbol == null){
			
			symbol = operatorsMap.get(texto.toUpperCase());
			if (OperatorSymbols.NONE.equals(symbol) || symbol == null){
				symbol = wordsMap.get(texto.toUpperCase());  
			}
			if (OperatorSymbols.NONE.equals(symbol) || symbol == null){
				symbol = typesMap.get(texto.toUpperCase());  
			}
			if (OperatorSymbols.NONE.equals(symbol) || symbol == null){
				symbol = commandsMap.get(texto.toUpperCase());  
			}
			if (OperatorSymbols.NONE.equals(symbol) || symbol == null){
				symbol = OperatorSymbols.ID;
			}
		}
		token.setSymbol(symbol);
		
		logger.exiting(Lexico.class.getName(), "consume", token);
		return token;
	}

	enum CharType {
		C_NONE, C_CHAR, C_NUMBER, C_ALPHA;
	}

	private CharType classifieChar(char c) {
		int type;
		
		type = Character.getType(c);
		
		switch (type) {
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.CONNECTOR_PUNCTUATION:
			return CharType.C_ALPHA;
		case Character.DECIMAL_DIGIT_NUMBER:
			return CharType.C_NUMBER;
		case Character.MATH_SYMBOL:
			return CharType.C_CHAR;
		case Character.SPACE_SEPARATOR:
			return CharType.C_NONE;
		default:
			String cString = Character.toString(c);
			if (operatorsMap.containsKey(cString)) {
				return CharType.C_CHAR;
			}
			return CharType.C_NONE;
		}
	}

	public void rollback() {
		rollbackFlag = true;
	}

}
