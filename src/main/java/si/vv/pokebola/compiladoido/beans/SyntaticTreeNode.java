package si.vv.pokebola.compiladoido.beans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SyntaticTreeNode {

	private static int modCount = 0;

	// tree structure
	private SyntaticTreeNode parent;
	private List<SyntaticTreeNode> children;

	// payload
	private int id;
	private Token lexicToken;
	private SyntaticSymbol syntaticSymbol;
	private String creatorMethodName;

	public SyntaticTreeNode(SyntaticTreeNode parent, String creatorMethodName,
			SyntaticSymbol syntaticSymbol, Token lexicToken) {
		id = modCount++;
		this.lexicToken = lexicToken;
		this.creatorMethodName = creatorMethodName;
		this.parent = parent;
		this.syntaticSymbol = syntaticSymbol;
	}

	public SyntaticTreeNode(SyntaticTreeNode parent, String creatorMethodName,
			SyntaticSymbol syntaticSymbol) {
		this(parent, creatorMethodName, syntaticSymbol, null);
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
		if (child != null) {
			this.getChildren().add(child);
		}
	}

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
		} else if (lexicToken != null && lexicToken.getSymbol() != null) {
			syntaticName = lexicToken.getSymbol().getName();
		}

		if (lexicToken != null && lexicToken.getTexto() != null) {
			tokenName = lexicToken.getTexto();
		}

		return id + "." + tokenName + "(" + syntaticName + ")" + "^" + parentId;
	}

	public String printSubTree() {
		StringBuilder builder = new StringBuilder();
		Queue<SyntaticTreeNode> printQueue, waitQueue;

		printQueue = new LinkedList<SyntaticTreeNode>();
		waitQueue = new LinkedList<SyntaticTreeNode>();

		builder.append(this.toString());
		builder.append("\n\n");

		builder.append(this.name());
		builder.append("\n");

		if (getChildren().isEmpty()) {
			return builder.toString();
		}
		printQueue.addAll(getChildren());

		do {
			while (!printQueue.isEmpty()) {
				SyntaticTreeNode printNode = printQueue.remove();
				builder.append(printNode.name()).append(" ");
				waitQueue.addAll(printNode.getChildren());
			}
			builder.append("\n\n");
			printQueue.addAll(waitQueue);
			waitQueue.clear();
		} while (!printQueue.isEmpty());

		return builder.toString();
	}

}