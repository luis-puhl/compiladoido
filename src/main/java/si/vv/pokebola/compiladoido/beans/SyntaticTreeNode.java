package si.vv.pokebola.compiladoido.beans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SyntaticTreeNode {

	private static int modCount = 0;

	// tree structure
	private SyntaticTreeNode parent;
	private List<SyntaticTreeNode> children;

	// syntatic data
	private int id;
	private Token lexicToken;
	private SyntaticSymbol syntaticSymbol;
	private String creatorMethodName;
	
	// semantic data
	private Collection<SemanticToken> context; 

	public SyntaticTreeNode(SyntaticTreeNode parent, String creatorMethodName,
			SyntaticSymbol syntaticSymbol, Token lexicToken) {
		this.id = modCount++;
		this.lexicToken = lexicToken;
		this.creatorMethodName = creatorMethodName;
		this.parent = parent;
		this.syntaticSymbol = syntaticSymbol;
	}

	public SyntaticTreeNode(SyntaticTreeNode parent, SyntaticSymbol syntaticSymbol, Token lexicToken) {
		this(parent, Thread.currentThread().getStackTrace()[2].getMethodName(), syntaticSymbol,
				lexicToken);
	}

	public SyntaticTreeNode(SyntaticTreeNode parent, SyntaticSymbol syntaticSymbol) {
		this(parent, Thread.currentThread().getStackTrace()[2].getMethodName(), syntaticSymbol,
				null);
	}

	public int getId() {
		return id;
	}

	public SyntaticTreeNode getRoot() {
		if (getParent() != null) {
			return getParent().getRoot();
		} else {
			return this;
		}
	}

	public boolean isLeaf(SyntaticTreeNode node) {
		return !this.getChildren().isEmpty();
	}

	public Token getLexicToken() {
		return lexicToken;
	}

	public void setLexicToken(Token lexicToken) {
		this.lexicToken = lexicToken;
	}

	public SyntaticTreeNode getParent() {
		return parent;
	}

	public void setParent(SyntaticTreeNode parent) {
		this.parent = parent;
	}

	public List<SyntaticTreeNode> getChildren() {
		if (children == null) {
			children = new LinkedList<SyntaticTreeNode>();
		}
		return children;
	}

	public void setChildren(List<SyntaticTreeNode> children) {
		this.children = children;
	}

	public String getCreatorMethodName() {
		return creatorMethodName;
	}

	public void setCreatorMethodName(String creatorMethodName) {
		this.creatorMethodName = creatorMethodName;
	}

	public SyntaticSymbol getSyntaticSymbol() {
		return syntaticSymbol;
	}

	public void setSyntaticSymbol(SyntaticSymbol syntaticSymbol) {
		this.syntaticSymbol = syntaticSymbol;
	}

	public Collection<SemanticToken> getContext() {
		if (context == null){
			context = new LinkedList<>();
		}
		return context;
	}

	public void setContext(Collection<SemanticToken> context) {
		this.context = context;
	}
	
	public void addContextToken(SemanticToken semanticToken) {
		this.getContext().add(semanticToken);
	}
	
	public Collection<SyntaticTreeNode> getChild(SyntaticSymbol symbol) {
		Collection<SyntaticTreeNode> ret = new LinkedList<SyntaticTreeNode>();
		for (SyntaticTreeNode node : this.getChildren()) {
			if (symbol.equals(node.getSyntaticSymbol())){
				ret.add(node);
			}
		}
		return ret;
	}
	
	public SyntaticTreeNode getFirstChild(SyntaticSymbol symbol) {
		Collection<SyntaticTreeNode> ret = new LinkedList<SyntaticTreeNode>();
		for (SyntaticTreeNode node : this.getChildren()) {
			if (symbol.equals(node.getSyntaticSymbol())){
				ret.add(node);
			}
		}
		return ret.iterator().next();
	}

	public boolean add(SyntaticTreeNode child) {
		if (child != null) {
			return this.getChildren().add(child);
		}
		return false;
	}

	public SyntaticTreeNode getParent(SyntaticSymbol symbol) {
		if (this.getParent() == this){
			return null;
		}
		
		SyntaticTreeNode parent = this.getParent();
		while(parent != null && !symbol.equals(parent.getSyntaticSymbol())) {
			parent = parent.getParent();
		}
		return parent;
	}
	
	/*
	 * 
	 */
	
	public void addAll(Collection<? extends SyntaticTreeNode> childs) {
		if (childs != null && !childs.isEmpty()) {
			this.getChildren().addAll(childs);
		}
	}

	@Override
	public String toString() {
		String parentName = null;
		try {
			parentName = parent.name();
		} catch (NullPointerException e) {
		}

		return "SyntaticTreeNode [id=" + id + ", parent=" + parentName + ", lexicToken="
				+ lexicToken + ", syntaticSymbol=" + syntaticSymbol + ", creatorMethodName="
				+ creatorMethodName + ", childrens=" + getChildren().size() + "]";
	}

	public String name() {
		String syntaticName = "";
		String tokenName = creatorMethodName;
		int parentId = -1;

		if (parent != null) {
			parentId = parent.getId();
		}

		if (syntaticSymbol != null) {
			syntaticName = syntaticSymbol.name();
			syntaticName += " " + this.getContext().toString();
			
		} else if (lexicToken != null && lexicToken.getSymbol() != null) {
			syntaticName = lexicToken.getSymbol().getName();
		}

		if (lexicToken != null && lexicToken.getTexto() != null) {
			tokenName = lexicToken.getTexto();
		}

		return id + "." + tokenName + "(" + syntaticName + ")" + "^" + parentId;
	}

	public String printTree() {
		StringBuilder builder = new StringBuilder();

		builder.append("\n");
		builder.append(this.toString());
		builder.append("\n\n");

		builder.append(this.name());
		for (SyntaticTreeNode n : this.getChildren()) {
			n.printTree(1, builder);
		}

		return builder.toString();
	}

	private void printTree(int tabLevel, StringBuilder builder) {
		builder.append("\n");

		for (int i = 0; i < tabLevel; i++) {
			builder.append("\t");
		}

		builder.append(this.name());

		for (SyntaticTreeNode n : this.getChildren()) {
			n.printTree(tabLevel + 1, builder);
		}
	}
	
	private String tokenName(){
		String r = creatorMethodName;
		
		if (this.getLexicToken() != null){
			r = this.getLexicToken().getTexto();
		} else if (this.getSyntaticSymbol() != null){
			r = this.getSyntaticSymbol().name();
		}
		
		return r;
	}
	
	public String printTreeTextToken() {
		StringBuilder builder = new StringBuilder();

		builder.append(this.tokenName());
		for (SyntaticTreeNode n : this.getChildren()) {
			n.printTreeTextToken(1, builder);
		}

		return builder.toString();
	}

	private void printTreeTextToken(int tabLevel, StringBuilder builder) {
		builder.append("\n");

		for (int i = 0; i < tabLevel; i++) {
			builder.append("\t");
		}

		builder.append(this.tokenName());

		for (SyntaticTreeNode n : this.getChildren()) {
			n.printTreeTextToken(tabLevel + 1, builder);
		}
	}
	
}
