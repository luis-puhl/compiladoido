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

	@Override
	public OperatorSymbols getMirror() {
		return null;
	}

	@Override
	public boolean isLine() {
		return false;
	}

	@Override
	public boolean isMultiLine() {
		return false;
	}
	
	@Override
	public String getName() {
		return super.name();
	}
	
	@Override
	public int toInt() {
		return super.ordinal();
	}
	
}

