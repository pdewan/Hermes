package fluorite.recorders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.impl.WeakHashtable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaElementDelta;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import dayton.ellwanger.hermes.xmpp.TaggedJSONListener;
import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.commands.EHBuildEndEvent;
import fluorite.commands.EHBuildStartEvent;
import fluorite.commands.EHCompilationCommand;
import hermes.tags.Tags;
import util.misc.Common;


public class EHCompilationParticipantRecorder extends CompilationParticipant  implements TaggedJSONListener{
//	protected static final String PROBLEMS_FILE_NAME = "problemhistory.txt";
//	protected Set<IProblem> problemHistorySet;

	Map<IProblem, EHCompilationCommand> allProblems = new HashMap<>();
	Set<IProblem> pendingProblems = new HashSet<>();
	private final int AST_LEVEL_THREE = 3;
	private final int AST_LEVEL_FOUR = 4;
	public static final int MAX_COMPILE_ERRORS = 15; // do not overwhelm the system for large workspaces
	public static final int MAX_COMPILE_WARNINGS = 10; // do not overwhelm the system for large workspaces

	private static EHCompilationRecorder compilationRecorder = EHCompilationRecorder.getInstance();
	public EHCompilationParticipantRecorder() {
//		problemHistorySet = new HashSet<>();
//		File aProblemHistoryFile = new File(PROBLEMS_FILE_NAME);
//		if (aProblemHistoryFile.exists()) {
//			
//		}
		ConnectionManager.getInstance().addTaggedJSONObjectListener(this, Tags.DOCUMENT_CHANGE);
	
	}
//	protected void readProblemHistory(File aProblemHistoryFile) {
//		try {
//			StringBuilder aPreviousProblems = Common.readFile(aProblemHistoryFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}
	public static final int PROBLEM_MAX_GROWTH = 10;
	public static final int PROBLEM_MAX_DISPLACEMENT = 30;
	public static boolean equals(IProblem aProblem1, IProblem aProblem2) {
		return aProblem1.getID() == aProblem2.getID() && 
				aProblem1.getMessage().equals(aProblem2.getMessage()) &&
				new String(aProblem1.getOriginatingFileName()).equals(new String(aProblem2.getOriginatingFileName())) &&
				Math.abs(
						(aProblem1.getSourceEnd() - aProblem1.getSourceStart()) -
						(aProblem2.getSourceEnd() - aProblem2.getSourceStart())) < PROBLEM_MAX_GROWTH &&
				Math.abs(aProblem1.getSourceLineNumber() - aProblem2.getSourceLineNumber()) < PROBLEM_MAX_DISPLACEMENT;
//		&&
//		getErrorMessage().equals(anOther.getMessageId()) &&
//		getSourceStart().equals(anOther.getSourceStart());
	}
	public static IProblem contains(IProblem[] aProblems, IProblem aProblem) {
		for (IProblem aCandidate:aProblems) {
			if (equals(aCandidate, aProblem))
				return aCandidate;
		}
		return null;
	}
	public static IProblem contains(Collection<IProblem> aProblems, IProblem aProblem) {
		for (IProblem aCandidate:aProblems) {
			if (equals(aCandidate, aProblem))
				return aCandidate;
		}
		return null;
	}
	
	public static EHCompilationCommand get(Map<IProblem, EHCompilationCommand> aMap, IProblem aProblem ) {
		for (IProblem aCandidate:aMap.keySet()) {
			if (equals(aCandidate, aProblem))
				return aMap.get(aCandidate);
		}
		return null;
	}
	protected String lastFileName = "";
	protected String lastFileContents = "";
	protected String lastPackage = "";
	protected String lastProject = "";
	protected Set<String> aJars = new HashSet<>();
	protected CompilationUnit lastFileADT = null;
	protected IDocument lastFileDocument = null;
	
	protected void setPackageAndProject(ReconcileContext context) {
		ICompilationUnit aWorkingCopy = context.getWorkingCopy();
		String aProject = aWorkingCopy.getJavaProject().getElementName();
		IJavaModel aModel = aWorkingCopy.getJavaModel();
		String aPackage = null;
//		IPackageDeclaration aPackageObject;
		try {
			 IPackageDeclaration[] aPackageDeclarations = aWorkingCopy.getPackageDeclarations();
			 if (aPackageDeclarations.length == 0) {
				 lastPackage = "";
			 } else {
			    lastPackage = aPackageDeclarations[0].getElementName();
			 }
			 
			IPackageFragmentRoot[] aLibrrayJars = aWorkingCopy.getJavaProject().getPackageFragmentRoots();
			for (IPackageFragmentRoot aJar:aLibrrayJars) {
				String anElementName = aJar.getElementName();
				if (anElementName.contains("jre")) {
					continue;
				}
				aJars.add(anElementName);
			}

			 
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	
	protected boolean maybeProcessNewFile(String anOldFileName, String aNewFileName) {
		if (!anOldFileName.equals(aNewFileName)) {
			
		}
		return false;
		
	}
	public static void printBinaryWithLeadingZeros(int anInt) {
		System.out.println(String.format("%32s", Integer.toBinaryString(anInt)).replace(' ', '0'));
	}
	protected void processDelta(IJavaElementDelta aDelta) {
		int aFlags = aDelta.getFlags();
		printBinaryWithLeadingZeros(aFlags);
		printBinaryWithLeadingZeros(aDelta.ADDED); // amy fine grained change
		printBinaryWithLeadingZeros(aDelta.F_AST_AFFECTED);
		printBinaryWithLeadingZeros(aDelta.F_ARCHIVE_CONTENT_CHANGED);
		printBinaryWithLeadingZeros(aDelta.F_FINE_GRAINED); // any edit = delete or insert etc

		printBinaryWithLeadingZeros(aDelta.F_PRIMARY_WORKING_COPY);
		printBinaryWithLeadingZeros(aDelta.F_RESOLVED_CLASSPATH_CHANGED);
		printBinaryWithLeadingZeros(aDelta.F_CLASSPATH_CHANGED);



		printBinaryWithLeadingZeros(aDelta.CHANGED);
		printBinaryWithLeadingZeros(aDelta.F_CHILDREN);	
		printBinaryWithLeadingZeros(aDelta.F_OPENED);
		printBinaryWithLeadingZeros(aDelta.F_AST_AFFECTED); // always on
		printBinaryWithLeadingZeros(aDelta.F_CLOSED);
		printBinaryWithLeadingZeros(aDelta.F_CATEGORIES);
		printBinaryWithLeadingZeros(aDelta.F_PRIMARY_WORKING_COPY);
		

		IJavaElementDelta[] anAfectedDeltas = aDelta.getAffectedChildren();
		IJavaElementDelta[] anAddedDeltas = aDelta.getAddedChildren();
		IJavaElementDelta[] aChangedDeltas = aDelta.getChangedChildren();
		CompilationUnit anAST = aDelta.getCompilationUnitAST();
		System.out.println(Integer.toString(aDelta.F_ADDED_TO_CLASSPATH, 2));
		System.out.println(Integer.toString(aDelta.F_CLASSPATH_CHANGED, 2) + " " + Integer.toString(aFlags, 2) + " && " +  Integer.toString(aDelta.F_CLASSPATH_CHANGED & aFlags, 2));
		
	}
	/*
	 * Assuming that new json object will be received before reconsile is called
	 * @see org.eclipse.jdt.core.compiler.CompilationParticipant#reconcile(org.eclipse.jdt.core.compiler.ReconcileContext)
	 */
	@Override
	public void reconcile(ReconcileContext context) 
	{
		System.out.println("Context:" + context + "class, package, project: " + context.getWorkingCopy()  +
				" file name" + context.getDelta().getElement().getElementName() + " delta " + context.getDelta());
		String aNewFileName = context.getDelta().getElement().getElementName();
		IPath aFilePath = context.getDelta().getElement().getResource().getFullPath();
		maybeProcessNewFile(lastFileName, aNewFileName);
		
		setPackageAndProject(context);
		
		IJavaElementDelta aDelta = context.getDelta();
		processDelta(aDelta);
		lastFileADT = aDelta.getCompilationUnitAST();
		System.out.println("AST/full source code:" + lastFileADT);
		IProblem[] problems= null;
		switch(context.getASTLevel())
		{

		case AST_LEVEL_THREE:
			try 
			{
				problems = context.getAST3().getProblems();
			} catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
			break;
		case AST_LEVEL_FOUR:
			 try
			 {
				 problems = context.getAST4().getProblems();
			 } catch (JavaModelException e)
			 {
				 e.printStackTrace();
			 }
			 break;
			default:
				try 
				{
					problems = context.getAST3().getProblems();
				} catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
				break;
		}
		
		if(problems != null)
		{
			if(problems.length > 0)
			{
				int numWarnings = 0;
				int numErrors = 0;
				Set<IProblem> aCurrentProblemSet = new HashSet(Arrays.asList(problems));
				Set<IProblem> aRemovedProblems = new HashSet();
				for (IProblem aProblem:allProblems.keySet()) {
//					if (!aCurrentProblemSet.contains(aProblem)) {
					/*
					 * previous pronlem is no longer a problem, and it has been recorded
					 */
					if (contains(problems, aProblem) == null ) {
						aRemovedProblems.add(aProblem);
						if (contains(pendingProblems, aProblem) == null) {
						EHCompilationCommand aCommand = get(allProblems, aProblem);

						aCommand.setDisappeared(true);
						
						compilationRecorder.record(aCommand);
						}
					}
				}
				for (IProblem aRemovedProblem:aRemovedProblems) {
					allProblems.remove(aRemovedProblem);
				}
				for(int i = 0; i < problems.length && (numWarnings < MAX_COMPILE_WARNINGS || numErrors < MAX_COMPILE_ERRORS) ; i++)
				{
					IProblem problem = problems[i];
					
					if (problem.isWarning()) {
						numWarnings++;
						if (numWarnings > MAX_COMPILE_WARNINGS) {
							continue;
						}
					} else if (problem.isError()) {
						numErrors++;
						if (numErrors > MAX_COMPILE_ERRORS) {
							continue;
						}
					}
//					EHCompilationCommand aCommand = allProblems.get(problem);
					EHCompilationCommand aCommand = get(allProblems, problem);

					if (aCommand == null) {
						aCommand = createCommand(problem);
						allProblems.put(problem, aCommand);
						pendingProblems.add(problem);
					} else {
						aCommand.increaseRepeatCount();
						
					}
					if (aCommand.isPersistent()) {
						IProblem aPendingProblem = contains(pendingProblems, problem);
						if (aPendingProblem != null ) {
					
						pendingProblems.remove(aPendingProblem);
						compilationRecorder.record(aCommand);
						}
					}
					
					
//					compilationRecorder.record(command);
				}
			}
		}
	}
//	public String getCurrentEditorContent() {
//		try {
//	    IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//	            .getActiveEditor();
//	    if (activeEditor == null)
//	        return null;
//	    final IDocument doc = (IDocument) activeEditor.getAdapter(IDocument.class);
//	    if (doc == null) return null;
//
//	    return doc.get();
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
	private EHCompilationCommand createCommand (IProblem problem)
	{
//		String aSource = getCurrentEditorContent() ;
//		String aSource = lastFileContents.toString();
//		int aLineNumber = problem.getSourceLineNumber();
//		int aSourceStart = lastFileContents.getPosition(aSource, problem.getSourceStart())
		
		
		
		int aProblemLineStart = lastFileADT.getPosition(problem.getSourceLineNumber(), 0 );
		int aProblemLineEnd = lastFileADT.getPosition(problem.getSourceLineNumber() + 1, 0) - 1;
		String aText = lastFileContents.substring(aProblemLineStart, aProblemLineEnd);

		System.out.println("Message: " + problem.getMessage());
		System.out.println("Source Line #: " + problem.getSourceLineNumber());
		System.out.println("Source Start: " + problem.getSourceStart());
		System.out.println("Source End: " + problem.getSourceEnd());
		String aText2 = lastFileContents.substring(problem.getSourceStart(), problem.getSourceEnd());
		System.out.println("File name: " + problem.getOriginatingFileName().toString());
		System.out.println("Problem Id: " + problem.getID());
//		String aProblemText = lastFileContents.toString().substring(problem.getSourceStart(), problem.getSourceEnd());
		EHCompilationCommand command = null;
		String fileName = new String(problem.getOriginatingFileName());
		if(problem.isError())
		{
			command = new EHCompilationCommand(false, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), fileName);
		}
		else if(problem.isWarning())
		{
			command = new EHCompilationCommand(true, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), fileName);
		}
		

		return command;
	}
	
	@Override
	public boolean isActive(IJavaProject project)
	{
		return true;
	}
	/*
	 * This is called before file save
	 */
	@Override
	public int aboutToBuild(IJavaProject project) 
	{
		EHBuildStartEvent command = new EHBuildStartEvent("");
		compilationRecorder.record(command);
		return CompilationParticipant.READY_FOR_BUILD;
	}
	/*
	 * Called after file save
	 */
	@Override
	public void buildFinished(IJavaProject project) 
	{
		EHBuildEndEvent command = new EHBuildEndEvent("");
		compilationRecorder.record(command);
		//upload files
//		 ProjectUploader projectUploader = new ProjectUploader();
//		 new Thread(projectUploader).start();
	}
	/*
	 * Can get this information by becoming a document listener, this seems more convenient, but lack
	 * of type checking can be an issue
	 */
	@Override
	public void newJSONObject(JSONObject messageData) {
		
		try {
			lastFileName = messageData.getString(Tags.RELATIVE_FILE_NAME);
		
		DocumentEvent aDocumentEvent = (DocumentEvent) messageData.get(Tags.DOCUMENT_CHANGE);
		lastFileDocument = aDocumentEvent.getDocument();
		lastFileContents = lastFileDocument.get();
//		String aContents = aDocumentEvent.getDocument().get();
//		System.out.println(aContents);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
