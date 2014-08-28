package si.vv.pokebola.compiladoido.beans;

import java.util.Map;

public interface Symbol {

	static Map<String, ? extends Symbol> map = null;

	public Map<String, ? extends Symbol> allMap();
	
	public OperatorSymbols getMirror();
	
	public boolean isLine();
	
	public boolean isMultiLine();
	
	public String getName();
	
	public int toInt();
	
}
