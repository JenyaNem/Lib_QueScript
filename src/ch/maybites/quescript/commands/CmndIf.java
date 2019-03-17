package ch.maybites.quescript.commands;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.Expression;
import ch.maybites.quescript.expression.ExpressionVar;
import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.expression.Expression.ExpressionException;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;
import ch.maybites.utils.Debug;

public class CmndIf extends Cmnd {
	protected static String NODE_NAME = "if";
		
	private static String ATTR_TRUE = "true";
	private static String ATTR_FALSE = "false";
	
	private final int MODE_FALSE 		= 0;
	private final int MODE_TRUE 		= 1;
	
	private int mode = -1;
		
	private ExpressionVar ifCondition = null;
	
	private RunTimeEnvironment prt;

	public CmndIf(Cmnd _parentNode){
		super(_parentNode);
		super.setCmndName(NODE_NAME);
	}

	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
		
		//if there is a nested <anim> node inside this if, it checks if there is another <anim> node
		// further down the tree towards the root
		for(Cmnd child: this.getChildren()){
			if(child instanceof CmndAnim){
				Cmnd parnt = this.parentNode;
				while(!(parnt instanceof CmndQue)){
					if(parnt instanceof CmndAnim)
						throw new ScriptMsgException("Command <if>: Multiple Nesting of <anim> and <if> nodes are prohibited");
					parnt = parnt.parentNode;
				}
			}
		}
	}


	/**
	 * Parse the Expressions with the RuntimeEnvironement
	 */
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		prt = new RunTimeEnvironment(rt);
		String smode = "";
		if(getAttributes().size() == 1){
			smode = getAttributes().iterator().next();
			try {
				if(smode.equals(ATTR_TRUE)){
					mode = MODE_TRUE;
					ifCondition = new Expression(getAttributeValue(ATTR_TRUE), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(prt);
				} else if(smode.equals(ATTR_FALSE)){
					mode = MODE_FALSE;
					ifCondition = new Expression(getAttributeValue(ATTR_FALSE), "{", "}").setInfo(" at line(" + lineNumber + ")").parse(prt);
				}
			} catch (ExpressionException e) {
				throw new ScriptMsgException("Command <if>: Attribute Expression: " + e.getMessage());
			}
		} else {
			Debug.error("Script - Command <if>", "only one of these attibutes are allowed: " + this.getAttributeList());			
			throw new ScriptMsgException("<if>: illegal attribute");
		}	
	
		if(debugMode)
			Debug.verbose("QueScript - NodeFactory", "que("+parentNode.getQueName()+") "+new String(new char[getLevel()]).replace('\0', '_')+" created If Cmnd: " + getAttributeValue(smode));

		// Make sure the que- and local- variables are created before the children are parsed
		for(Cmnd child: this.getChildren()){
			child.setup(prt);
		}
	}
	
	@Override
	public void store(Node _parentElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bang(CMsgShuttle _msg) {
		if(!_msg.isWaitLocked()){
			try {
				if(ifCondition.eval().getNumberValue() == mode){
					for(Cmnd child : getChildren()){
						if(!child.isCmndName(CmndElse.NODE_NAME))
							child.bang(_msg);
					}
				} else {
					for(Cmnd child : getChildren()){
						if(child.isCmndName(CmndElse.NODE_NAME))
							child.bang(_msg);
					}
				}
			} catch (ExpressionException e) {
				Debug.error("Script - Command <if>", "if condition: " + e.getMessage());			
			}
		}
	}
	
	public void lockLessBang(CMsgShuttle _msg){
		try {
			if(ifCondition.eval().getNumberValue() == mode){
				for(Cmnd child : getChildren()){
					if(!child.isCmndName(CmndElse.NODE_NAME))
						child.lockLessBang(_msg);
				}
			} else {
				for(Cmnd child : getChildren()){
					if(child.isCmndName(CmndElse.NODE_NAME))
						child.lockLessBang(_msg);
				}
			}
		} catch (ExpressionException e) {
			Debug.error("Script - Command <if>", "if condition: " + e.getMessage());			
		}
	}
			
	@Override
	public void resume(long _timePassed) {
	}

}
