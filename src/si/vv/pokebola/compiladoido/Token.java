package si.vv.pokebola.compiladoido;

public class Token{

	private Simbolo simbolo;
	private String texto;
	
	public Token(Simbolo simbolo, String texto) {
		this.simbolo = simbolo;
		this.texto = texto;
	}

	public Simbolo getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(Simbolo simbolo) {
		this.simbolo = simbolo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
}
