package ch.maybites.quescript.commands;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.Expression;
import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.utils.Debug;

public class CmndExpr extends Cmnd {
	protected static String NODE_NAME = "expr";

	ExpressionVar variable;

	public CmndExpr(Cmnd _parentNode){
		super(_parentNode);
		super.setCmndName(NODE_NAME);
	}

	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
	}

	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		try {
			variable = new Expression(super.content, "{", "}").setInfo(" at line(" + lineNumber + ")").parse(rt);
		} catch (ExpressionException e) {
			throw new ScriptMsgException("QueScript - Command <expr>: Value Expression: " + e.getMessage());
		}
		if(debugMode)
			Debug.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+"created expr-Comnd = "+ super.content);	

	}

	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}
	
	public void lockLessBang(CMsgShuttle _msg){
		try {
			variable.eval();
		} catch (ExpressionException e) {
			Debug.error("QueScript que("+parentNode.getQueName()+") - Command <expr>: Value Expression", e.getMessage());
		}
	}

	public void resume(long _timePassed) {;}

}
