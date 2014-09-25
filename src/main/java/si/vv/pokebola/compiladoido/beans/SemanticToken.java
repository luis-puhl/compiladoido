package si.vv.pokebola.compiladoido.beans;

public class SemanticToken {

	private String name;
	private SyntaticSymbol syntaticMean;
	private TypeWordSymbols type;
	private int objectAddress;

	public SemanticToken(String name, SyntaticSymbol syntaticMean, TypeWordSymbols type, int objectAddress) {
		this.name = name;
		this.syntaticMean = syntaticMean;
		this.type = type;
		this.objectAddress = objectAddress;
	}

	public SemanticToken() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SyntaticSymbol getSyntaticMean() {
		return syntaticMean;
	}

	public void setSyntaticMean(SyntaticSymbol syntaticMean) {
		this.syntaticMean = syntaticMean;
	}

	public TypeWordSymbols getType() {
		return type;
	}

	public void setType(TypeWordSymbols type) {
		this.type = type;
	}

	public int getObjectAddress() {
		return objectAddress;
	}

	public void setObjectAddress(int objectAddress) {
		this.objectAddress = objectAddress;
	}

	@Override
	public String toString() {
		return "SemanticToken [name=" + name + ", syntaticMean=" + syntaticMean + ", type=" + type
				+ ", objectAddress=" + objectAddress + "]";
	}

}
