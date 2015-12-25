package ch.maybites.mxj.quescript;

import org.w3c.dom.Node;

import ch.maybites.mxj.expression.Expression;
import ch.maybites.mxj.expression.ExpressionVar;
import ch.maybites.mxj.expression.RunTimeEnvironment;
import ch.maybites.mxj.expression.Expression.ExpressionException;
import ch.maybites.mxj.quescript.messages.CMsgFade;
import ch.maybites.mxj.quescript.messages.CMsgShuttle;
import ch.maybites.mxj.quescript.messages.ScriptMsgException;
import ch.maybites.tools.Debugger;

public class CmndExpr extends Cmnd {
	protected static String NODE_NAME = "expr";

	ExpressionVar variable;

	public CmndExpr(CmndInterface _parentNode){
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
			variable = new Expression(super.content, "{", "}").parse(rt);
		} catch (ExpressionException e) {
			throw new ScriptMsgException("QueScript - Command <expr>: Value Expression: " + e.getMessage());
		}
		if(getDebugMode())
			Debugger.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+"created expr-Comnd = "+ super.content);	

	}

	@Override
	public void store(Node _parentElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			lockLessBang(_msg);
		}
	}
	
	public void lockLessBang(CMsgShuttle _msg){
		try {
			variable.eval();
		} catch (ExpressionException e) {
			Debugger.error("QueScript que("+parentNode.getQueName()+") - Command <expr>: Value Expression", e.getMessage());
		}
	}

	
	@Override
	public void resume(long _timePassed) {
		// dont care
	}

}
