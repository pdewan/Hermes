package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import fluorite.model.EHEventRecorder;

public class EHProgramExecutionEvent 
	 extends EHAbstractCommand
//	extends edu.cmu.scs.fluorite.commands.RunCommand 
	implements EHICommand{
	int numEvents;
	public EHProgramExecutionEvent() {
		super();
		
	}
	
//	
	public EHProgramExecutionEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
//		super(debug, terminate, projectName, exitValue);
		mDebug = debug;
		mTerminate = terminate;
		mProjectName = projectName;
		// stuff added
		mHitBreakPoint = hitBreakPoint;
		mStepEnd = stepEnd;
		mStepInto = stepInto;
		mStepReturn = stepReturn;
		numEvents = aNumEvents;
	}
	public EHProgramExecutionEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		this(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, 0);
	}

	private boolean mDebug;
	private boolean mRun;
	private boolean mTerminate;
	private boolean mCreate;
	
	private String mProjectName;
	

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		String kind;
		if(mTerminate)
			kind = "Terminate";
		if(mCreate)
			kind = "Create";
		if(mHitBreakPoint);
			kind = "HitBreakPoint";
		if(mStepEnd)
			kind = "StepEnd";
		if(mStepInto)
			kind = "StepInto";
		if(mStepReturn)
			kind = "StepReturn";
		
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mDebug ? "Debug" : "Run");
		attrMap.put("kind", kind);
		attrMap.put("projectName", mProjectName == null ? "(Unknown)"
				: mProjectName);
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	
//	public String getName() {
//		return attr.getValue();
//	}

	public String getCommandType() {
		return "RunCommand";
	}

	public String getName() {
		
		String name = "";
		String debugRun = "";
		
		if(mTerminate)
			name = "Terminate";
		if(mCreate)
			name = "Create";
		if(mDebug)
			debugRun = "Debug";
		if(mRun)
			debugRun = "Run";
		if(mHitBreakPoint);
			name = "HitBreakPoint";
		if(mStepEnd)
			name = "StepEnd";
		if(mStepInto)
			name = "StepInto";
		if(mStepReturn)
			name = "StepReturn";
		
		
		return name + debugRun + "Application";
		
		
//		return (mTerminate ? "Terminate" : "Create") + " "
//				+ (mDebug ? "Debug" : "Run") + " Application";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EHEventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EHEventRecorder.MacroCommandCategoryID;
	}

	public boolean combine(EHICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// stuff added
	protected boolean mHitBreakPoint;
	protected boolean mStepEnd;
	protected boolean mStepInto;
	protected boolean mStepReturn;
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr  attr = null;
		
//		if ((attr = commandElement.getAttributeNode("type")) != null) {
//			mDebug = Boolean.parseBoolean(attr.getValue());
//		}
		
		if ((attr = commandElement.getAttributeNode("kind")) != null) {
			if (attr.getValue().equals( "Terminate"))
			{
//				mTerminate = true;
//				mCreate = false;
				mHitBreakPoint = false;
				mStepEnd = false;
		        mStepInto = false;;
				mStepReturn = false;
			}
			
			if (attr.getValue().equals( "Create"))
			{
//				mTerminate = false;
//				mCreate = true;
				mHitBreakPoint = false;
				mStepEnd = false;
		        mStepInto = false;;
				mStepReturn = false;
			}
			
			if (attr.getValue().equals( "HitBreakPoint"))
			{
//				mTerminate = false;
//				mCreate = false;
				mHitBreakPoint = true;
				mStepEnd = false;
		        mStepInto = false;;
				mStepReturn = false;
			}
			
			if (attr.getValue().equals("StepEnd"))
			{
//				mTerminate = false;
//				mCreate = false;
				mHitBreakPoint = false;
				mStepEnd = true;
		        mStepInto = false;;
				mStepReturn = false;
			}
			
			if (attr.getValue().equals("StepInto"))
			{
//				mTerminate = false;
//				mCreate = false;
				mHitBreakPoint = false;
				mStepEnd = false;
		        mStepInto = true;;
				mStepReturn = false;
			}
			
			if (attr.getValue().equals("StepReturn"))
			{
//				mTerminate = false;
//				mCreate = false;
				mHitBreakPoint = false;
				mStepEnd = false;
		        mStepInto = false;;
				mStepReturn = true;
			}
		}

//		if ((attr = commandElement.getAttributeNode("projectName")) != null) {
//			mProjectName = attr.getValue();
//		}
	}
	public String toString() {
		String retVal = numEvents <= 1? 
				super.toString():
				super.toString() + ":" + numEvents;
		return retVal;
		
//		return numEvents > 1? super.toString():super.toString() + ":" + numEvents;
	}

}
