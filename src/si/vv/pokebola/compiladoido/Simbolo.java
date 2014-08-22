package si.vv.pokebola.compiladoido;

import java.util.HashMap;
import java.util.Map;

public enum Simbolo {

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
	
	// operadores 	
	//# From http://www.freepascal.org/docs-html/ref/refse1.html#x9-80001.1,
	//# The following characters have a special meaning: ,
	PLUS {
		@Override
		public String toString() {
			return "+";
		}
	},
	MINUS {
		@Override
		public String toString() {
			return "-";
		}
	},
	ASTERISK {
		@Override
		public String toString() {
			return "*";
		}
	},
	FOWARD_SLASH {
		@Override
		public String toString() {
			return "/";
		}
	},
	EQUAL {
		@Override
		public String toString() {
			return "=";
		}
	},
	LT {
		@Override
		public String toString() {
			return "<";
		}
	},
	GT {
		@Override
		public String toString() {
			return ">";
		}
	},
	OPEN_BRACKET {
		@Override
		public String toString() {
			return "[";
		}
	},
	CLOSE_BRACKET {
		@Override
		public String toString() {
			return "]";
		}
	},
	POINT {
		@Override
		public String toString() {
			return ".";
		}
	},
	COMMA {
		@Override
		public String toString() {
			return ",";
		}
	},
	OPEN_PARENTHESIS {
		@Override
		public String toString() {
			return "(";
		}
	},
	CLOSE_PARENTHESIS {
		@Override
		public String toString() {
			return ")";
		}
	},
	COLON {
		@Override
		public String toString() {
			return ":";
		}
	},
	
	POINTER {
		@Override
		public String toString() {
			return "^";
		}
	},
	ADDRESS {
		@Override
		public String toString() {
			return "@";
		}
	},
	OPEN_CURLY_BRACKET {
		@Override
		public String toString() {
			return "{";
		}
		@Override
		public Simbolo getMirror() {
			return CLOSE_CURLY_BRACKET;
		}
		@Override
		public boolean isMultiLineComment() {
			return true;
		}
	},
	CLOSE_CURLY_BRACKET {
		@Override
		public String toString() {
			return "}";
		}
		@Override
		public Simbolo getMirror() {
			return CLOSE_CURLY_BRACKET;
		}
	},
	DOLLAR {
		@Override
		public String toString() {
			return "$";
		}
	},
	// #this is a sharp '#',
	SHARP {
		@Override
		public String toString() {
			return "#";
		}
	},
	AMPERSAND {
		@Override
		public String toString() {
			return "&";
		}
	},
	PERCENT {
		@Override
		public String toString() {
			return "%";
		}
	},
	QUOTE {
		@Override
		public String toString() {
			return "'";
		}
	},
	DOUBLE_QUOTE {
		@Override
		public String toString() {
			return "\"";
		}
	},
	
	// # and the following character pairs too: ,
	LEFT_SHIFT {
		@Override
		public String toString() {
			return "<<";
		}
	},
	RIGHT_SHIFT {
		@Override
		public String toString() {
			return ">>";
		}
	},
	NOT_EQUAL {
		@Override
		public String toString() {
			return "<>";
		}
	},
	// not implemented **,
	// not implemented ><, Symmetric difference
	LT_EQUAL {
		@Override
		public String toString() {
			return "<=";
		}
	},
	GT_EQUAL {
			@Override
			public String toString() {
				return ">=";
			}
	},
	COLON_EQUAL {
		@Override
		public String toString() {
			return ":=";
		}
	},
	PLUS_EQUAL {
		@Override
		public String toString() {
			return "+=";
		}
	},
	MINUS_EQUAL {
		@Override
		public String toString() {
			return "-=";
		}
	},
	STAR_EQUAL {
		@Override
		public String toString() {
			return "*=";
		}
	},
	SLASH_EQUAL {
		@Override
		public String toString() {
			return "/=";
		}
	},
	OPEN_PARENTHESIS_STAR {
		@Override
		public String toString() {
			return "(*";
		}
		@Override
		public boolean isMultiLineComment(){
			return true;
		}
		@Override
		public Simbolo getMirror() {
			return CLOSE_PARENTHESIS_STAR;
		}
	},
	CLOSE_PARENTHESIS_STAR {
		@Override
		public String toString() {
			return "*)";
		}
		@Override
		public Simbolo getMirror() {
			return OPEN_PARENTHESIS_STAR;
		}
	},
	OPEN_PARENTHESIS_DOT {
		@Override
		public String toString() {
			return "(.";
		}
		@Override
		public Simbolo getMirror() {
			return CLOSE_PARENTHESIS_DOT;
		}
	},
	CLOSE_PARENTHESIS_DOT {
		@Override
		public String toString() {
			return ".)";
		}
		@Override
		public Simbolo getMirror() {
			return OPEN_PARENTHESIS_DOT;
		}
	},
	SLASH_SLASH {
		@Override
		public String toString() {
			return "//";
		}
		@Override
		public boolean isLineComment() {
			return true;
		}
	},
	COMMENT,
	ID;

	private static Map<String, Simbolo> map = null;

	public static Map<String, Simbolo> allMap(){
		if (map == null){
			Simbolo[] values = values();
			
			map = new HashMap<String, Simbolo>(values.length);
			for (Simbolo simbolo: values) {
				map.put(simbolo.toString(), simbolo);
			}
		}
		return map;
	}
	
	public boolean isLineComment(){
		return false;
	}
	
	public boolean isMultiLineComment(){
		return false;
	}
	
	public Simbolo getMirror(){
		return null;
	}
	
}
