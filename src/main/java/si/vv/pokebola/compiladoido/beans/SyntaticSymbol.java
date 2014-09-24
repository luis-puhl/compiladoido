package si.vv.pokebola.compiladoido.beans;

import java.util.Map;

public enum SyntaticSymbol implements Symbol {
	
	/* WORDS */
	IDENTIFIER, 
	CONSTANT,
	TYPE,
	STRING_LITERAL,
	LABEL,	
	VAR_REFERENCE,
	UNSIGNED_NUMBER,
	UNSIGNED_CONSTANT,
	METHOD_IDENTIFIER,
	
	/* BLOCKS */
	PROGRAM, 
	PROGRAM_HEADER, 
	USE_CLAUSE, 
	BLOCK, 
	DECLARATION_PART, 
	STATEMENT_PART, 
	VAR_DECLARATION_PART, 
	VAR_DECLARATION, 
	VAR_MODIFIERS,
	HINT_DIRECTIVE,
	PROCEDURE_FUNCTION_DECLARATION_PART,
	PROCEDURE_DECLARATION,
	FUNCTION_DECLARATION,
	SUBROTINE_BLOCK, 
	FORMAL_PARAMETER_LIST,
	PARAMETER_DECLARATION,
	COMPOUND_STATEMENT, 
	VALUE_PARAMETER,
	VARIABLE_PARAMETER, 
	IDENTIFIER_LIST,
	CASE, 
	EXPRESSION, 
	SIMPLE_EXPRESSION, 
	TERM,
	FACTOR, 
	FUNCTION_CALL,
	VALUE_TYPECAST, 
	ADDRESS_FACTOR, 
	;

	public Map<String, ? extends Symbol> allMap() {
		return null;
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
		return name();
	}

	public int toInt() {
		return ordinal();
	}

}
