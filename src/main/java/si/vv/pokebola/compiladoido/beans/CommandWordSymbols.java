package si.vv.pokebola.compiladoido.beans;

import java.util.HashMap;
import java.util.Map;

public enum CommandWordSymbols implements Symbol {
	
	// palavras reservadas
	READ,
	WRITE;
	
	private static Map<String, CommandWordSymbols> map = null;

	public Map<String, CommandWordSymbols> allMap(){
		if (map == null){
			CommandWordSymbols[] values = values();
			
			map = new HashMap<String, CommandWordSymbols>(values.length);
			for (CommandWordSymbols simbolo: values) {
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

