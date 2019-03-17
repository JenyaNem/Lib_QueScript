package ch.maybites.quescript.commands;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import ch.maybites.quescript.expression.RunTimeEnvironment;
import ch.maybites.quescript.messages.CMsgShuttle;
import ch.maybites.quescript.messages.ScriptMsgException;

public class CmndScript extends Cmnd{
	public static String NODE_NAME = "script";
	
	/** list with only que nodes */
	private ArrayList<Cmnd> queChildren;
	private ArrayList<Cmnd> playChildren;
	private ArrayList<Cmnd> stopChildren;

	OutputInterface output;

	public CmndScript() {
		super(null);
		queChildren = new ArrayList<Cmnd>();
		playChildren = new ArrayList<Cmnd>();
		stopChildren = new ArrayList<Cmnd>();
		super.setCmndName(NODE_NAME);
	}
	
	public void build(Node _xmlNode) throws ScriptMsgException{
		super.build(_xmlNode);
		queChildren.clear();
		stopChildren.clear();
		playChildren.clear();
		for(Cmnd child: this.getChildren()){
			if(child.cmdName.equals(CmndQue.NODE_NAME)){
				queChildren.add(child);
			} else if(child.cmdName.equals(CmndInternal.NODE_NAME_STOP)){
				stopChildren.add(child);
			}else if(child.cmdName.equals(CmndInternal.NODE_NAME_PLAY)){
				playChildren.add(child);
			}
		}
	}
		
	public void setup(RunTimeEnvironment rt)throws ScriptMsgException{
		for(Cmnd child: this.getChildren()){
			child.setup(rt);
		}
	}

	public void setOutput(OutputInterface output){
		this.output = output;
	}

	public OutputInterface getOutput(){
		return output;
	}

	/**
	 * gets all this objects ques
	 * @return
	 */
	public List<Cmnd> getQues(){
		return queChildren;
	}

	/**
	 * gets all this objects stops
	 * @return
	 */
	public List<Cmnd> getStops(){
		return stopChildren;
	}

	/**
	 * gets all this objects plays
	 * @return
	 */
	public List<Cmnd> getPlays(){
		return playChildren;
	}

	/**
	 * Checks if this Script has a Que of this name
	 * @param queName
	 * @return true if this is the case 
	 */
	public boolean hasQue(String queName){
		for(Cmnd q: getQues()){
			CmndQue que = (CmndQue) q;
			if(que.getQueName().equals(queName))
				return true;
		}
		return false;
	}

	/**
	 * Returns the first que of this name this script can find inside its children.
	 * @param queName
	 * @return the instance of the que
	 */
	public CmndQue getQue(String queName){
		for(Cmnd q: getQues()){
			CmndQue que = (CmndQue) q;
			if(que.getQueName().equals(queName)){
				return que;
			}
		}
		return null;
	}
	
	/**
	 * Checks if this Script has a playing que
	 * @return true if one of its ques is playing
	 */
	public boolean hasQuePlaying(){
		for(Cmnd q: getQues()){
			CmndQue que = (CmndQue) q;
			if(que.isPlaying){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void bang(CMsgShuttle _msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lockLessBang(CMsgShuttle _msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume(long _timePassed) {
		// TODO Auto-generated method stub
		
	}

}
