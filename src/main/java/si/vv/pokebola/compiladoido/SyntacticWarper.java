package si.vv.pokebola.compiladoido;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.Token;

public class SyntacticWarper {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private LexicalAutomata lexico;
	
	private SyntaticTreeNode treeRoot;
	
	public SyntacticWarper(LexicalAutomata lexico){
		this.lexico = lexico;
	}
	
	public SyntaticTreeNode parse(){
		List<Token> tokenList = lexico.getTokenList();
		
		PascalSyntacticAutomata automato = new PascalSyntacticAutomata(tokenList);
		try {
			treeRoot = automato.run();
		} catch (SyntacticAutomataException e){
			e.log();
			LOGGER.fatal("Automato didn't run fine =( =( =( ");
			LOGGER.catching(e);
			throw new RuntimeException(e);
		}
		
		return treeRoot;
	}
	
	public List<Token> getExpression(){
		return null;
	}
	
}
