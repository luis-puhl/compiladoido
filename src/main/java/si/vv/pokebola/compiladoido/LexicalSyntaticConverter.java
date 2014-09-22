package si.vv.pokebola.compiladoido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.OperatorSymbols;
import si.vv.pokebola.compiladoido.beans.Symbol;
import si.vv.pokebola.compiladoido.beans.SyntaticSymbol;
import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.Token;

public class LexicalSyntaticConverter {
	
	private int tokenIndex;
	private List<Token> lexicalTokens;
	
	private static Logger logger;
	
	public LexicalSyntaticConverter(List<Token> lexicalTokens) {
		this.lexicalTokens = lexicalTokens;
		if (logger == null) {
			logger = LogManager.getLogger();
		}
	}
	
	/* token stuff */

	private Token getToken() {
		return lexicalTokens.get(tokenIndex++);
	}

	public void rollback() {
		tokenIndex--;
	}

	/* tree stuff */
	
	private Token getTokenLexico() {
		Symbol symbol;
		Token token;
		token = this.getToken();
		symbol = token.getSymbol();
		if (symbol instanceof OperatorSymbols && ((OperatorSymbols) symbol).isComment()) {
			logger.info("Got a COMMENT");
			token = this.getTokenLexico();
		}
		logger.info("\n\t" + token.toString());
		return token;
	}
	
	// implementacao
	public Token expect(Collection<? extends Symbol> expected,
			StackTraceElement caller) throws SyntacticAutomataException {
		Symbol symbol;
		Token token;
		token = this.getTokenLexico();
		symbol = token.getSymbol();
		if (!expected.contains(symbol)) {
			this.rollback();
			throw new SyntacticAutomataException(logger, expected, symbol, caller);
		}

		return token;
	}

	public SyntaticTreeNode expectNode(SyntaticTreeNode node, String method,
			SyntaticSymbol syntaticSymbol, Symbol expected) throws SyntacticAutomataException {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[1];
		Collection<Symbol> expects = new ArrayList<Symbol>(1);
		expects.add(expected);
		Token expect = expect(expects, caller);
		return new SyntaticTreeNode(node, method, syntaticSymbol, expect);
	}

	public SyntaticTreeNode expectNode(SyntaticTreeNode node, String method,
			SyntaticSymbol syntaticSymbol, Collection<? extends Symbol> expected)
			throws SyntacticAutomataException {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[1];
		Token expect = expect(expected, caller);
		return new SyntaticTreeNode(node, method, syntaticSymbol, expect);
	}

	public SyntaticTreeNode expectNode(SyntaticTreeNode node, String method,
			Collection<? extends Symbol> expected) throws SyntacticAutomataException {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[1];
		Token expect = expect(expected, caller);
		return new SyntaticTreeNode(node, method, null, expect);
	}

	public SyntaticTreeNode expectNode(SyntaticTreeNode node, String method, Symbol expected)
			throws SyntacticAutomataException {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[1];
		Collection<Symbol> expects = new ArrayList<Symbol>(1);
		expects.add(expected);
		Token expect = expect(expects, caller);
		return new SyntaticTreeNode(node, method, null, expect);
	}

}
