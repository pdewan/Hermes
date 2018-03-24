package fluorite.commands;

public class EHHitBreakpointAfterRunEvent extends EHProgramExecutionEvent implements EHICommand{
	public EHHitBreakpointAfterRunEvent() {
		super();
		
	}
//	
	public EHHitBreakpointAfterRunEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, aNumEvents);
	}

//	@Override
//	public void createFrom(Element commandElement) {
//		super.createFrom(commandElement);
//		
//		Attr  attr = null;
//		
////		if ((attr = commandElement.getAttributeNode("type")) != null) {
////			mDebug = Boolean.parseBoolean(attr.getValue());
////		}
//		
//		if ((attr = commandElement.getAttributeNode("kind")) != null) {
//			if (attr.getValue().equals( "Terminate"))
//			{
////				mTerminate = true;
////				mCreate = false;
//				mHitBreakPoint = false;
//				mStepEnd = false;
//		        mStepInto = false;;
//				mStepReturn = false;
//			}
//			
//			if (attr.getValue().equals( "Create"))
//			{
////				mTerminate = false;
////				mCreate = true;
//				mHitBreakPoint = false;
//				mStepEnd = false;
//		        mStepInto = false;;
//				mStepReturn = false;
//			}
//			
//			if (attr.getValue().equals( "HitBreakPoint"))
//			{
////				mTerminate = false;
////				mCreate = false;
//				mHitBreakPoint = true;
//				mStepEnd = false;
//		        mStepInto = false;;
//				mStepReturn = false;
//			}
//			
//			if (attr.getValue().equals("StepEnd"))
//			{
////				mTerminate = false;
////				mCreate = false;
//				mHitBreakPoint = false;
//				mStepEnd = true;
//		        mStepInto = false;;
//				mStepReturn = false;
//			}
//			
//			if (attr.getValue().equals("StepInto"))
//			{
////				mTerminate = false;
////				mCreate = false;
//				mHitBreakPoint = false;
//				mStepEnd = false;
//		        mStepInto = true;;
//				mStepReturn = false;
//			}
//			
//			if (attr.getValue().equals("StepReturn"))
//			{
////				mTerminate = false;
////				mCreate = false;
//				mHitBreakPoint = false;
//				mStepEnd = false;
//		        mStepInto = false;;
//				mStepReturn = true;
//			}
//		}
//
////		if ((attr = commandElement.getAttributeNode("projectName")) != null) {
////			mProjectName = attr.getValue();
////		}
//	}
//	public String toString() {
//		return super.toString();
//	}
//	public String getName() {
//		return attr.getValue();
//	}

//	public String getCommandType() {
//		return "RunCommand";
//	}
//
//	public String getName() {
//		
//		String name = "";
//		String debugRun = "";
//		
//		if(mTerminate)
//			name = "Terminate";
//		if(mCreate)
//			name = "Create";
//		if(mDebug)
//			debugRun = "Debug";
//		if(mRun)
//			debugRun = "Run";
//		if(mHitBreakPoint);
//			name = "HitBreakPoint";
//		if(mStepEnd)
//			name = "StepEnd";
//		if(mStepInto)
//			name = "StepInto";
//		if(mStepReturn)
//			name = "StepReturn";
//		
//		
//		return name + debugRun + "Application";
//		
//		
////		return (mTerminate ? "Terminate" : "Create") + " "
////				+ (mDebug ? "Debug" : "Run") + " Application";
//	}
//
//	public String getDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getCategory() {
//		return EventRecorder.MacroCommandCategory;
//	}
//
//	public String getCategoryID() {
//		return EventRecorder.MacroCommandCategoryID;
//	}
//
//	public boolean combine(EHICommand anotherCommand) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
