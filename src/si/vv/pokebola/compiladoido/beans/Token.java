package si.vv.pokebola.compiladoido.beans;

public class Token{

	private Symbol symbol;
	private String texto;
	
	public Token(Symbol symbol, String texto) {
		this.symbol = symbol;
		this.texto = texto;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Override
	public String toString() {
		return "Token [symbol=" + symbol + ", texto=" + texto + "]";
	}
	
	
}
