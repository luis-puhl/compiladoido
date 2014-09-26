package si.vv.pokebola.compiladoido;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import si.vv.pokebola.compiladoido.beans.SemanticToken;
import si.vv.pokebola.compiladoido.beans.SyntaticSymbol;
import si.vv.pokebola.compiladoido.beans.SyntaticTreeNode;
import si.vv.pokebola.compiladoido.beans.Token;
import si.vv.pokebola.compiladoido.beans.TypeWordSymbols;

/**
 * 
 * @author luispuhl
 *
 */
public class PascalSemanticActions {

	private static Logger logger;

	public PascalSemanticActions() {
		if (logger == null) {
			logger = LogManager.getLogger();
		}
	}

	/* ***************************************************************** */

	public void program(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void programHeader(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void programParameters(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void usesClause(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* END program */

	/*
	 * BEGIN block definition
	 * http://www.freepascal.org/docs-html/ref/refse98.html#x204-21400016.4
	 */

	public void block(SyntaticTreeNode blockNode) {
		logger.entry();

		logger.exit();
	}

	public void declarationPart(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void variableDeclarationPart(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void procedureFuncionDeclarationPart(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* END block definition */

	/*
	 * BEGIN var delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	public void variableDeclaration(SyntaticTreeNode root) {
		logger.entry();

		SemanticToken semanticToken;
		TypeWordSymbols type;
		String name;
		
		Token lexicToken;
		
		try {
			SyntaticTreeNode typeNode = root.getFirstChild(SyntaticSymbol.TYPE);
			
			lexicToken = typeNode.getFirstChild(SyntaticSymbol.TYPE).getLexicToken();
			type = (TypeWordSymbols) lexicToken.getSymbol();
			
			lexicToken = root.getFirstChild(SyntaticSymbol.IDENTIFIER).getLexicToken();
			name = lexicToken.getTexto();
			
			semanticToken = new SemanticToken(name, 
					SyntaticSymbol.VAR_DECLARATION, 
					type,
					-1);
			
			root.getParent(SyntaticSymbol.BLOCK).addContextToken(semanticToken);
		} catch(Exception e) {
			logger.catching(e);
		}
				
		logger.exit();
	}

	public void type(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void constant(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void variableModifiers(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* END var delcaration */

	/*
	 * BEGIN procedure delcaration
	 * http://www.freepascal.org/docs-html/ref/refse21.html#x56-630004.2
	 */

	public void procedureDeclaration(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void subroutineBlock(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* END procedure delcaration */

	public void functionDeclaration(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/*
	 * BEGIN parameter lists
	 * http://www.freepascal.org/docs-html/ref/refse82.html#x162-17200014.4
	 */

	public void formalParameterList(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void parameterDeclaration(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void valueParameter(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void variableParameter(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void identifierList(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* END parameter lists */

	public void hintDirective(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void expression(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void simpleExpression(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void term(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void factor(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void unsignedConstant(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void functionCall(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void valueTypecast(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void addressFactor(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void statement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void simpleStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void structuredStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void repetitiveStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void conditionalStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* simple statements */

	public void assignmentStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void procedureStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void actualParameterList(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void gotoStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	/* structed statements */

	public void compoundStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

	public void caseStatement(SyntaticTreeNode root) {
		logger.entry();

		logger.exit();
	}

}
