package si.vv.pokebola.compiladoido.beans;

import java.util.HashMap;
import java.util.Map;

public enum WordSymbols implements Symbol {
	
	// palavras reservadas
	AND,
	ARRAY,
	ASM,
	BEGIN,
	BREAK,
	CASE,
	CONST,
	CONSTRUCTOR,
	CONTINUE,
	DESTRUCTOR,
	DIV,
	DO,
	DOWNTO,
	ELSE,
	END,
	FALSE,
	FILE,
	FOR,
	FUNCTION,
	GOTO,
	IF,
	IMPLEMENTATION,
	IN,
	INLINE,
	INTERFACE,
	LABEL,
	MOD,
	NIL,
	NOT,
	OBJECT,
	OF,
	ON,
	OPERATOR,
	OR,
	PACKED,
	PROCEDURE,
	PROGRAM,
	RECORD,
	REPEAT,
	SET,
	SHL,
	SHR,
	STRING,
	THEN,
	TO,
	TRUE,
	TYPE,
	UNIT,
	UNTIL,
	USES,
	VAR,
	WHILE,
	WITH,
	XOR,
	DEPRECATED,
	EXPERIMENTAL,
	PLATAFORM,
	UNIMPLEMENTED, 
	ABSOLUTE, 
	EXPORT, 
	EXTERMAL, 
	CVAR, 
	NAME, 
	FORWARD,
	OTHERWISE;
	
	private static Map<String, WordSymbols> map = null;

	public Map<String, WordSymbols> allMap(){
		if (map == null){
			WordSymbols[] values = values();
			
			map = new HashMap<String, WordSymbols>(values.length);
			for (WordSymbols simbolo: values) {
				map.put(simbolo.toString(), simbolo);
			}
		}
		return map;
	}

	
	public Symbol getMirror() {
		return null;
	}

	public boolean isLine() {
		return false;
	}
	
	public boolean isMultiLine() {
		return false;
	}
	
	public String getName() {
		return super.name();
	}
	
	public int toInt() {
		return super.ordinal();
	}
	
}

