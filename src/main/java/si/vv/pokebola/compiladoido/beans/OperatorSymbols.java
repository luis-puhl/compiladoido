package si.vv.pokebola.compiladoido.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum OperatorSymbols implements Symbol {
	
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
	SLASH {
		@Override
		public String toString() {
			return "/";
		}
	},
	BACK_SLASH {
		@Override
		public String toString() {
			return "\\";
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
	PERIOD {
		@Override
		public String toString() {
			return ".";
		}
	},
	/**
	 * is ","
	 */
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
		@Override
		public OperatorSymbols getMirror() {
			return CLOSE_PARENTHESIS;
		}
	},
	CLOSE_PARENTHESIS {
		@Override
		public String toString() {
			return ")";
		}
		@Override
		public OperatorSymbols getMirror() {
			return OPEN_PARENTHESIS;
		}
	},
	/**
	 * is ':'
	 */
	COLON {
		@Override
		public String toString() {
			return ":";
		}
	},
	/**
	 * is ';'
	 */
	SEMICOLON {
		@Override
		public String toString() {
			return ";";
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
		public OperatorSymbols getMirror() {
			return CLOSE_CURLY_BRACKET;
		}
		@Override
		public boolean isMultiLine() {
			return true;
		}
		@Override
		public boolean isComment(){
			return true;
		}
	},
	CLOSE_CURLY_BRACKET {
		@Override
		public String toString() {
			return "}";
		}
		@Override
		public OperatorSymbols getMirror() {
			return CLOSE_CURLY_BRACKET;
		}
		@Override
		public boolean isComment(){
			return true;
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
	/**
	 * Is "<>"
	 */
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
	ASTERISK_EQUAL {
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
	/**
	 * is '..'
	 */
	PERIOD_PERIOD {
		@Override
		public String toString() {
			return "..";
		}
	},
	OPEN_PARENTHESIS_STAR {
		@Override
		public String toString() {
			return "(*";
		}
		@Override
		public boolean isMultiLine(){
			return true;
		}
		@Override
		public OperatorSymbols getMirror() {
			return CLOSE_PARENTHESIS_STAR;
		}
	},
	CLOSE_PARENTHESIS_STAR {
		@Override
		public String toString() {
			return "*)";
		}
		@Override
		public OperatorSymbols getMirror() {
			return OPEN_PARENTHESIS_STAR;
		}
	},
	OPEN_PARENTHESIS_DOT {
		@Override
		public String toString() {
			return "(.";
		}
		@Override
		public OperatorSymbols getMirror() {
			return CLOSE_PARENTHESIS_DOT;
		}
	},
	CLOSE_PARENTHESIS_DOT {
		@Override
		public String toString() {
			return ".)";
		}
		@Override
		public OperatorSymbols getMirror() {
			return OPEN_PARENTHESIS_DOT;
		}
	},
	SLASH_SLASH {
		@Override
		public String toString() {
			return "//";
		}
		@Override
		public boolean isLine() {
			return true;
		}
		@Override
		public boolean isComment() {
			return true;
		}
	},
	COMMENT{
		@Override
		public boolean isComment() {
			return true;
		}
	},
	ID,
	NONE {
		@Override
		public String toString() {
			return "";
		}
	},
	STRING_CONSTANT
	;

	private static Map<String, OperatorSymbols> map = null;

	public Map<String, OperatorSymbols> allMap(){
		if (map == null){
			OperatorSymbols[] values = values();
			
			map = new HashMap<String, OperatorSymbols>(values.length);
			for (OperatorSymbols simbolo: values) {
				map.put(simbolo.toString(), simbolo);
			}
		}
		return map;
	}
	
	public boolean isLine(){
		return false;
	}
	
	public boolean isMultiLine(){
		return false;
	}
	
	public OperatorSymbols getMirror(){
		return null;
	}
	
	public String getName() {
		return super.name();
	}
	
	public int toInt() {
		return super.ordinal();
	}
	
	public boolean isComment(){
		return false;
	}

	
	public Collection<Symbol> getSimpleExpressionOperators(){
		Collection<Symbol> simpleOps = new ArrayList<>();
		
		simpleOps.add(PLUS);
		simpleOps.add(MINUS);
		simpleOps.add(WordSymbols.OR);
		simpleOps.add(WordSymbols.XOR);
		
		return simpleOps;
	}
	
	public Collection<Symbol> getAditionOperators(){
		Collection<Symbol> addingOps = new ArrayList<>();
		
		addingOps.add(ASTERISK);
		addingOps.add(LT_EQUAL);
		addingOps.add(GT_EQUAL);
		addingOps.add(GT);
		addingOps.add(EQUAL);
		addingOps.add(NOT_EQUAL);
		addingOps.add(WordSymbols.IN);
		addingOps.add(WordSymbols.IS);
		
		return addingOps;
	}
	
	public Collection<Symbol> getMultiplicationOperators(){
		Collection<Symbol> multOps = new ArrayList<>();
		
		multOps.add(ASTERISK);
		multOps.add(SLASH);
		multOps.add(WordSymbols.DIV);
		multOps.add(WordSymbols.MOD);
		multOps.add(WordSymbols.AND);
		multOps.add(WordSymbols.SHL);
		multOps.add(WordSymbols.SHR);
		multOps.add(WordSymbols.AS);
		
		return multOps;
	}
	
}
