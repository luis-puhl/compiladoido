package si.vv.pokebola.compiladoido.beans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SyntaticTreeNode {

	// tree structure
	private SyntaticTreeNode parent;
	private List<SyntaticTreeNode> children;

	// payload
	private Token lexicToken;
	private SyntaticSymbol syntaticSymbol; 
	private String creatorMethodName;

	public SyntaticTreeNode(SyntaticTreeNode parent, String creatorMethodName,
			SyntaticSymbol syntaticSymbol, Token lexicToken) {
		this.lexicToken = lexicToken;
		this.creatorMethodName = creatorMethodName;
		this.parent = parent;
		this.syntaticSymbol = syntaticSymbol;
	}
	
	public SyntaticTreeNode(SyntaticTreeNode parent, String creatorMethodName,
			SyntaticSymbol syntaticSymbol) {
		this.creatorMethodName = creatorMethodName;
		this.parent = parent;
		this.syntaticSymbol = syntaticSymbol;
	}

	public SyntaticTreeNode getRoot() {
		if (getParent() != null) {
			return getParent().getRoot();
		} else {
			return this;
		}
	}

	public SyntaticTreeNode getChild(SyntaticTreeNode parent, int index) {
		return getChildren().get(index);
	}

	public int getChildCount(SyntaticTreeNode parent) {
		return getChildren().size();
	}

	public boolean isLeaf(SyntaticTreeNode node) {
		return this.getChildren().contains(node);
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

	public void add(SyntaticTreeNode child) {
		if (child != null){
			this.getChildren().add(child);
		}
	}
	
	public void addAll(Collection<? extends SyntaticTreeNode> childs){
		if (childs != null && !childs.isEmpty()){
			this.getChildren().addAll(childs);
		}
	}

}
