package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class DifficulyStatusCommand extends EHAbstractCommand {
	
	public enum Status
	{
		Making_Progress,Surmountable, Insurmountable
	}
	
	private Status mUserStatus;
	private String mTryingToDo;
	private String mCausedDifficulty;
	private String mOtherCausedDifficulty;
	private String mOvercomeDifficultyDropDown;
	private String mOtherOverComeDifficultySaveText;
	private String mOtherMinutes;
    private String mPersonAskedForHelp;
	public DifficulyStatusCommand()
	{
		
	}
	
	public DifficulyStatusCommand(Status userStatus)
	{
		mUserStatus = userStatus;
		mTryingToDo = "";
		mCausedDifficulty = "";
		mOtherCausedDifficulty = "";
		mOvercomeDifficultyDropDown = "";
		mOtherOverComeDifficultySaveText = "";
		mOtherMinutes = "";
		mPersonAskedForHelp = "";
	}
	
	public DifficulyStatusCommand(Status userStatus, String tryingToDo, String causedDifficulty, String otherCausedDifficulty,
			String overcomeDifficultyDropDown, String otherOvercomeDifficultySaveText, String otherMinutes)
	{
		mUserStatus = userStatus;
		mTryingToDo = tryingToDo;
		mCausedDifficulty = causedDifficulty;
		mOtherCausedDifficulty = otherCausedDifficulty;
		mOvercomeDifficultyDropDown = overcomeDifficultyDropDown;
		mOtherOverComeDifficultySaveText = otherOvercomeDifficultySaveText;
		mOtherMinutes = otherMinutes;
		mPersonAskedForHelp = "";
	}
	
	public DifficulyStatusCommand(Status userStatus, String tryingToDo, String causedDifficulty, String otherCausedDifficulty,
			String overcomeDifficultyDropDown, String otherOvercomeDifficultySaveText, String otherMinutes, String personAskedForHelp)
	{
		mUserStatus = userStatus;
		mTryingToDo = tryingToDo;
		mCausedDifficulty = causedDifficulty;
		mOtherCausedDifficulty = otherCausedDifficulty;
		mOvercomeDifficultyDropDown = overcomeDifficultyDropDown;
		mOtherOverComeDifficultySaveText = otherOvercomeDifficultySaveText;
		mOtherMinutes = otherMinutes;
		mPersonAskedForHelp = personAskedForHelp;
	}
	
	public static final String XML_TryingToDo_Tag = "tryingToDo";
	public static final String XML_CausedDifficulty_Tag = "causedDifficulty";
	public static final String XML_OtherCausedDifficulty_Tag = "otherCausedDifficulty";
	public static final String XML_OvercomeDifficultyDropDown_Tag = "overcomeDifficultyDropDown";
	public static final String XML_OtherOverComeDifficultySaveText_Tag = "otherOvercomeDifficultySaveText";
	public static final String XML_OtherMinutes_Tag = "otherMinutes";
	public static final String XML_PersonAskedForHelp_Tag = "personAskedForHelp";
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		Attr attr = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("type")) != null) {
			if(attr.getValue().equals("Making_Progress"))
				mUserStatus = Status.Making_Progress;
			if(attr.getValue().equals("Surmountable"))
				mUserStatus = Status.Surmountable;
			if(attr.getValue().equals("Insurmountable"))
				mUserStatus = Status.Insurmountable;
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_TryingToDo_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mTryingToDo = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_CausedDifficulty_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mCausedDifficulty = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_OtherCausedDifficulty_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mOtherCausedDifficulty = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_OvercomeDifficultyDropDown_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mOvercomeDifficultyDropDown = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_OtherOverComeDifficultySaveText_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mOtherOverComeDifficultySaveText = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_OtherMinutes_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mOtherMinutes = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_PersonAskedForHelp_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mPersonAskedForHelp = textNode.getTextContent();
		}
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
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mUserStatus.toString());
		return attrMap;
	}

	
	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		
		if (mTryingToDo != null)
			dataMap.put(XML_TryingToDo_Tag, mTryingToDo);
		if (mCausedDifficulty != null)
			dataMap.put(XML_CausedDifficulty_Tag, mCausedDifficulty);
		if (mOtherCausedDifficulty != null)
			dataMap.put(XML_OtherCausedDifficulty_Tag, mOtherCausedDifficulty);
		if (mOvercomeDifficultyDropDown != null)
			dataMap.put(XML_OvercomeDifficultyDropDown_Tag, mOvercomeDifficultyDropDown);
		if (mOtherOverComeDifficultySaveText != null)
			dataMap.put(XML_OtherOverComeDifficultySaveText_Tag, mOtherOverComeDifficultySaveText);
		if (mOtherMinutes != null)
			dataMap.put(XML_OtherMinutes_Tag, mOtherMinutes);
		if (mPersonAskedForHelp != null)
			dataMap.put(XML_PersonAskedForHelp_Tag, mPersonAskedForHelp);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "DifficultyCommand";
	}

	@Override
	public String getName() {
		return "Difficulty";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCategory() {
		return EHEventRecorder.DifficultyCategory;
	}

	@Override
	public String getCategoryID() {
		return EHEventRecorder.DifficultyCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		return false;
	}

	public Status getStatus() {
		return mUserStatus;
	}

}
