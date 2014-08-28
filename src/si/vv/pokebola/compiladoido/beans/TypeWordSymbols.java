package si.vv.pokebola.compiladoido.beans;

import java.util.HashMap;
import java.util.Map;

public enum TypeWordSymbols implements Symbol {
	
	// palavras reservadas
	REAL,
	INTEGER;
	
	private static Map<String, TypeWordSymbols> map = null;

	public Map<String, TypeWordSymbols> allMap(){
		if (map == null){
			TypeWordSymbols[] values = values();
			
			map = new HashMap<String, TypeWordSymbols>(values.length);
			for (TypeWordSymbols simbolo: values) {
				map.put(simbolo.toString(), simbolo);
			}
		}
		return map;
	}

	public OperatorSymbols getMirror() {
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

