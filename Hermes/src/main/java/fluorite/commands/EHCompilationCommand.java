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

public class EHCompilationCommand extends EHAbstractCommand {

	public EHCompilationCommand() {

	}

	public EHCompilationCommand(boolean isWarning, String errorMessage,
			String messageId, String sourceCodeLineNumber, String sourceStart,
			String sourceEnd, String fileName) {
		mIsWarning = isWarning;
		mErrorMessage = errorMessage;
		mMessageId = messageId;
		mSourceCodeLineNumber = sourceCodeLineNumber;
		mSourceStart = sourceStart;
		mSourceEnd = sourceEnd;
		mFileName = fileName;
	}

	private String mErrorMessage;
	private String mMessageId;
	private String mSourceCodeLineNumber;
	private String mFileName;
	private String mSourceStart;
	private String mSourceEnd;
	private boolean mIsWarning;

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public String getSourceCodeLineNumber() {
		return mSourceCodeLineNumber;
	}

	public String getFileName() {
		return mFileName;
	}

	public String getSourceStart() {
		return mSourceStart;
	}
	
	public String getSourceEnd() {
		return mSourceEnd;
	}
	
	public boolean getIsWarning() {
		return mIsWarning;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);

		Attr attr = null;
		NodeList nodeList = null;

		if ((attr = commandElement.getAttributeNode("type")) != null) {
			mIsWarning = Boolean.parseBoolean(attr.getValue());
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_ErrorMessage_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mErrorMessage = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_MessageId_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mMessageId = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_SourceCodeLineNumber_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceCodeLineNumber = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_SourceStart_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceStart = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_SourceEnd_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceEnd = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_FileName_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mFileName = textNode.getTextContent();
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

	public static final String XML_ErrorMessage_Tag = "errorMessage";
	public static final String XML_MessageId_Tag = "messageId";
	public static final String XML_SourceCodeLineNumber_Tag = "sourceCodeLineNumber";
	public static final String XML_SourceStart_Tag = "sourceStart";
	public static final String XML_SourceEnd_Tag = "sourceEnd";
	public static final String XML_FileName_Tag = "filename";

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mIsWarning ? "Warning" : "Error");
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (mErrorMessage != null)
			dataMap.put(XML_ErrorMessage_Tag, mErrorMessage);
		if (mMessageId != null)
			dataMap.put(XML_MessageId_Tag, mMessageId);
		if (mSourceCodeLineNumber != null)
			dataMap.put(XML_SourceCodeLineNumber_Tag, mSourceCodeLineNumber);
		if (mSourceStart != null)
			dataMap.put(XML_SourceStart_Tag, mSourceStart);
		if (mSourceEnd != null)
			dataMap.put(XML_SourceEnd_Tag, mSourceEnd);
		if (mFileName != null)
			dataMap.put(XML_FileName_Tag, mFileName);

		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "CompilationCommand";
	}

	@Override
	public String getName() {
		String name;
		if (mIsWarning)
			name = "Warning";
		else
			name = "Error";
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
