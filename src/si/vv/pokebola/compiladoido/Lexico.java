package si.vv.pokebola.compiladoido;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.Token;
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
		String texto = this.consume(buffer);
		Symbol symbol;
		
		Map<String, OperatorSymbols> simboloAllMap = OperatorSymbols.ADDRESS.allMap();
		Map<String, WordSymbols> wordAllMap = WordSymbols.AND.allMap();
		
		symbol = simboloAllMap.get(texto.toUpperCase());
		if (symbol == null){
			symbol = wordAllMap.get(texto.toUpperCase());  
		}
		if (symbol == null){
			symbol = OperatorSymbols.ID;
		}
		
		rollbackToken = new Token(symbol, texto); 
		return rollbackToken;
	}

	enum Estado {
		LER_VAZIO, ID, NUMEROS, OPERADOR, COMENTARIO_LINHA, COMENTARIO_LONGO,
		END_TOKEN, TERMINADO;
	}

	/**
	 * DFA - Deterministic finite automaton
	 * 
	 * @param buffer
	 * @return
	 */
	private String consume(StringBuffer buffer) {
		logger.entering(Lexico.class.getName(), "consume");

		StringBuilder ret = new StringBuilder();
		OperatorSymbols simboloFechamento = null; 
		Map<String, OperatorSymbols> simboloAllMap = OperatorSymbols.ADDRESS.allMap();

		Estado estado;
		estado = Estado.LER_VAZIO;
		
		while (estado != Estado.TERMINADO && buffer.length() != 0) {
			char c;
			c = buffer.charAt(0);
			CharType type = classifieChar(c);

			logger.entering("consume", estado.name());
			// logger.info("buffer length is " + buffer.length());
			
			switch (estado) {
			case LER_VAZIO: // Estado inicial - Ignora espaços em branco
				switch (type) {
				case C_CHAR:
					estado = Estado.OPERADOR;
					break;
				case C_NUMBER:
					estado = Estado.NUMEROS;
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
						&& simboloAllMap.containsKey(operador)) {
					estado = Estado.OPERADOR;
					
					ret.append(c);
					buffer.deleteCharAt(0);
					
					OperatorSymbols simbolo = simboloAllMap.get(operador);
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
				if (type == CharType.C_NONE){
					estado = Estado.END_TOKEN;
				} else {
					estado = Estado.COMENTARIO_LINHA;
					ret.append(c);
					buffer.deleteCharAt(0);
				}
				break;
				
			case COMENTARIO_LONGO:
				previous = ret.charAt(ret.length());
				s = new StringBuilder(2);
				s.append(previous);
				s.append(c);
				operador = s.toString().toUpperCase();
				
				OperatorSymbols simbolo = simboloAllMap.get(operador);
				
				ret.append(c);
				buffer.deleteCharAt(0);
				
				if (simbolo == simboloFechamento){
					estado = Estado.END_TOKEN;
				} else {
					estado = Estado.COMENTARIO_LONGO;
				}
				break;
				
			case END_TOKEN:
				estado = Estado.TERMINADO;
				break;
			case TERMINADO:
			default:
				logger.warning("Estado inválido");
				break;
			}
		}
		
		logger.exiting(Lexico.class.getName(), "consume", ret);
		
		return ret.toString();
	}

	enum CharType {
		C_NONE, C_CHAR, C_NUMBER, C_ALPHA;
	}

	private CharType classifieChar(char c) {
		int type;
		Map<String, OperatorSymbols> simboloAllMap = OperatorSymbols.ADDRESS.allMap();
		
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
			if (simboloAllMap.containsKey(cString)) {
				return CharType.C_CHAR;
			}
			return CharType.C_NONE;
		}
	}

	public void rollback() {
		rollbackFlag = true;
	}

}
