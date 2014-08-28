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

