package fluorite.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class EHCompilationEvent extends EHAbstractCommand {
	private String mErrorMessage;
	private String mMessageId;
	private String mSourceCodeLineNumber;
	private String mFileName;
	private String mSourceStart;
	private String mSourceEnd;
	private boolean mIsWarning;
	protected String workingContext;
//	protected List<Long> timeStamps= new ArrayList();
//	protected List<Integer> commandNumbers = new ArrayList();
	protected long physicalDuration;
	protected long commandDuration;
	protected long dataEventDuration;
	protected long finalizedDataEventDuration;
	protected long initialTimestamp;
	protected int initialCommandNumber;
	protected int initialDataEventNumber;
	protected int initialFinalizedDataEventNumber;
	protected boolean disappeared;
	protected String problemText;
	
	// duplicating ints for now
	protected int sourceStart; 
	protected int sourceEnd;
	protected int lineNumber;
	protected int messageId;
	



	protected String problemLine;
	public EHCompilationEvent() {

	}
	/*
	 * Wonder why message  id has been converted to string or why the sourve strings etc
	 * have been converted. I suppose for xml conversion.
	 */
	public EHCompilationEvent(boolean isWarning, String errorMessage,
			String messageId, String sourceCodeLineNumber, String sourceStart,
			String sourceEnd, String aProblemText, String aProblemLine, String fileName) {
		mIsWarning = isWarning;
		mErrorMessage = errorMessage;
		mMessageId = messageId;
		mSourceCodeLineNumber = sourceCodeLineNumber;
		mSourceStart = sourceStart;
		mSourceEnd = sourceEnd;
		mFileName = fileName;
		initialTimestamp = System.currentTimeMillis();
		initialCommandNumber = EHEventRecorder.getInstance().getNumNotifiedCommands();
		initialDataEventNumber = EHEventRecorder.getInstance().getNumDataEvents();
		initialFinalizedDataEventNumber = EHEventRecorder.getInstance().getNumFinalizedDataEvents();
		problemText = aProblemText;
		problemLine = aProblemLine;
		
//		workingContext = aWorkingContext;
	}
	public EHCompilationEvent(boolean isWarning, String errorMessage,
			int aMessageId, int aLineNumber, int aStart,
			int anEnd, String aProblemText, String aProblemLine, String fileName) {
		mIsWarning = isWarning;
		mErrorMessage = errorMessage;
		mMessageId = String.valueOf(aMessageId);
		messageId = aMessageId;
		mSourceCodeLineNumber = String.valueOf(aLineNumber);
		mSourceStart = String.valueOf(aStart);
		mSourceEnd = String.valueOf(anEnd);
		sourceStart = aStart;
		sourceEnd = anEnd;
		lineNumber = aLineNumber;
		mFileName = fileName;
		initialTimestamp = System.currentTimeMillis();
		initialCommandNumber = EHEventRecorder.getInstance().getNumNotifiedCommands();
		initialDataEventNumber = EHEventRecorder.getInstance().getNumDataEvents();
		initialFinalizedDataEventNumber = EHEventRecorder.getInstance().getNumFinalizedDataEvents();
		problemText = aProblemText;
		problemLine = aProblemLine;
		
//		workingContext = aWorkingContext;
	}

	
	
	protected static final int MIN_PHYSICAL_DURATION = 10000; // 10 seconds
	protected static final int MIN_COMMAND_DURATION = 3; //
	protected static final int MIN_FINALIZED_DATA_EVENTS = 5;
	protected static final int MIN_DATA_EVENT_DURATION = 5;

	public String toString() {
		return getFileName() + "-" + getErrorMessage();
	}
	public String getErrorMessage() {
		return mErrorMessage;
	}

	public String getStringMessageId() {
		return mMessageId;
	}
	public int getMessageId() {
		return messageId;
	}

	public String getStringSourceCodeLineNumber() {
		return mSourceCodeLineNumber;
	}
	
	public int getCodeLineNumber() {
		return lineNumber;
	}
	
	
	public String getFileName() {
		return mFileName;
	}

	public String getStringSourceStart() {
		return mSourceStart;
	}
	
	public int getSourceStart() {
		return sourceStart;
	}
	
	public String getStringSourceEnd() {
		return mSourceEnd;
	}
	
	public int getSourceEnd() {
		return sourceEnd;
	}
	
	public boolean getIsWarning() {
		return mIsWarning;
	}
	public void setDisappeared(boolean newVal) {
		disappeared = newVal;
	}
	public boolean isDisappeared() {
		return disappeared;
	}
	public void increaseRepeatCount() {
		super.increaseRepeatCount();
		long aTimestamp = System.currentTimeMillis();
		int aCommandNumber = EHEventRecorder.getInstance().getNumNotifiedCommands();
		int aFinalizedDataEventNumber = EHEventRecorder.getInstance().getNumFinalizedDataEvents();
		int aDataEventEventNumber = EHEventRecorder.getInstance().getNumDataEvents();
		physicalDuration = aTimestamp - initialTimestamp;
		commandDuration = aCommandNumber - initialCommandNumber;
		dataEventDuration = aDataEventEventNumber - initialDataEventNumber;
		finalizedDataEventDuration = aFinalizedDataEventNumber - initialFinalizedDataEventNumber;
	}
	
	public boolean isPersistent() {
		return finalizedDataEventDuration >= MIN_FINALIZED_DATA_EVENTS &&
				physicalDuration >= MIN_PHYSICAL_DURATION &&
				commandDuration >= MIN_COMMAND_DURATION &&
				dataEventDuration >= MIN_DATA_EVENT_DURATION;

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
		if ((nodeList = commandElement.getElementsByTagName(XML_Physical_Duration_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			physicalDuration = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_Logical_Duration_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			commandDuration = Integer.parseInt(textNode.getTextContent());
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
	public static final String XML_Physical_Duration_Tag = "physicalDuration";
	public static final String XML_Logical_Duration_Tag = "logicalDuration";
	

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
		dataMap.put(XML_Physical_Duration_Tag, String.valueOf(physicalDuration));
		dataMap.put(XML_Logical_Duration_Tag, String.valueOf(commandDuration));

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
//	anExistingCommand.getMessageId() == aNewCommand.getMessageId() && 
//			anExistingCommand.equals(aNewCommand.getErrorMessage()) &&
//			anExistingCommand.getFileName().equals(aNewCommand.getFileName()) &&
//			anExistingCommand.getProblemText().equals(aNewCommand.getProblemText());
	public static boolean overlaps (int aCandidatePos, int aStartPos, int anEndPos ) {
		return aCandidatePos >= aStartPos && aCandidatePos <= anEndPos;
	}
	public boolean overlaps(EHCompilationEvent aNewCommand) {
		return overlaps(this, aNewCommand);
	}
	
	public static boolean contains (int aCandidatePos, int aStartPos, int anEndPos ) {
		return aCandidatePos >= aStartPos && aCandidatePos <= anEndPos;
	}
	/*
	 * Overlaps commutes so I suppose do not have to check both ways
	 */
	public static boolean overlaps(EHCompilationEvent anExistingCommand, EHCompilationEvent aNewCommand) {
		return anExistingCommand.getFileName().equals(aNewCommand.getFileName()) &&
				overlaps(aNewCommand.getSourceStart(), anExistingCommand.getSourceStart(), anExistingCommand.getSourceEnd()) ||
				overlaps(aNewCommand.getSourceEnd(), anExistingCommand.getSourceStart(), anExistingCommand.getSourceEnd());
				
	}
	public boolean equals(EHCompilationEvent anOther) {
		return 
				getFileName().equals(anOther.getFileName()) &&
				getStringMessageId().equals(anOther.getStringMessageId()) &&
				getErrorMessage().equals(anOther.getStringMessageId()) &&
				getStringSourceStart().equals(anOther.getStringSourceStart());
				
	}
	public void merge (EHCompilationEvent aLaterCommand) {
		sourceStart = aLaterCommand.sourceStart;
		this.mErrorMessage = aLaterCommand.mErrorMessage;
		this.messageId = aLaterCommand.messageId;
		sourceEnd = aLaterCommand.sourceEnd;
		setProblemLine(aLaterCommand.getProblemLine());
		setProblemText(aLaterCommand.getProblemText());
	}
	public String getProblemText() {
		return problemText;
	}
	public void setProblemText(String problemText) {
		this.problemText = problemText;
	}
	public String getProblemLine() {
		return problemLine;
	}
	public void setProblemLine(String problemLine) {
		this.problemLine = problemLine;
	}
	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
