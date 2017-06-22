package difficultyPrediction;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHICommand;


public class AnEndOfQueueCommand implements EHICommand {
	public AnEndOfQueueCommand() {
		
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String persist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Document doc, Element commandElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTimestamp(long timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTimestamp2(long timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimestamp2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void increaseRepeatCount() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRepeatCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCommandIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean areTopBottomLinesRecorded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTopLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottomLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

//	@Override
//	public boolean combineWith(EHICommand anotherCommand) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public String getCommandTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean combineWith(ICommand arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCommandIndex(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSessionId(long arg0) {
		// TODO Auto-generated method stub
		
	}

}
