package fluorite.recorders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import dayton.ellwanger.hermes.xmpp.TaggedJSONListener;
import fluorite.commands.BuildEndEvent;
import fluorite.commands.BuildStartEvent;
import fluorite.commands.CompilationCommand;
import fluorite.commands.LibrariesAdded;
import fluorite.commands.LibrariesRemoved;
import fluorite.commands.PropertyDialogClosedCommand;
import fluorite.commands.ShellCommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EclipseEventListener;
import hermes.tags.Tags;
import util.trace.Tracer;


public class EHCompilationParticipantRecorder extends CompilationParticipant  implements TaggedJSONListener, EclipseEventListener{
//	protected static final String PROBLEMS_FILE_NAME = "problemhistory.txt";
//	protected Set<IProblem> problemHistorySet;

//	Map<IProblem, EHCompilationCommand> allProblems = new HashMap<>();
	Set<CompilationCommand> allCommands = new HashSet();
//	Set<IProblem> pendingProblems = new HashSet<>();
	Set<CompilationCommand> unrecordedCommands = new HashSet<>();
	List<CompilationCommand> reconcileCommands = new ArrayList<>(); // changed on each reconcile event
	private final int AST_LEVEL_THREE = 3;
	private final int AST_LEVEL_FOUR = 4;
	public static final int MAX_COMPILE_ERRORS = 15; // do not overwhelm the system for large workspaces
	public static final int MAX_COMPILE_WARNINGS = 10; // do not overwhelm the system for large workspaces

	private static EHCompilationRecorder compilationRecorder = EHCompilationRecorder.getInstance();
	protected enum DeltaKind {EDIT, NON_EDIT, UNKNOWN}; // will assume NON_EDIT is class path changed
	protected DeltaKind lastDeltaKind;
	public EHCompilationParticipantRecorder() {
//		problemHistorySet = new HashSet<>();
//		File aProblemHistoryFile = new File(PROBLEMS_FILE_NAME);
//		if (aProblemHistoryFile.exists()) {
//			
//		}
		ConnectionManager.getInstance().addTaggedJSONObjectListener(this, Tags.DOCUMENT_CHANGE);
		EHEventRecorder.getInstance().addEclipseEventListener(this);
	
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
	
//	public static boolean overlaps (int aCandidatePos, int aStartPos, int anEndPos ) {
//		return aCandidatePos >= aStartPos && aCandidatePos <= anEndPos;
//	}
//	
//	public static boolean contains (int aCandidatePos, int aStartPos, int anEndPos ) {
//		return aCandidatePos >= aStartPos && aCandidatePos <= anEndPos;
//	}
//	/*
//	 * Overlaps commutes so I suppose do not have to check both ways
//	 */
//	public static boolean overlaps(EHCompilationCommand anExistingCommand, EHCompilationCommand aNewCommand) {
//		return anExistingCommand.getFileName().equals(aNewCommand.getFileName()) &&
//				overlaps(aNewCommand.getSourceStart(), anExistingCommand.getSourceStart(), anExistingCommand.getSourceEnd()) ||
//				overlaps(aNewCommand.getSourceEnd(), anExistingCommand.getSourceStart(), anExistingCommand.getSourceEnd());
//				
//	}
//	public static boolean hasMorphed(EHCompilationCommand anExistingCommand, EHCompilationCommand aNewCommand) {
//		return 
//	}
//	//everything same
//	public static boolean equals(EHCompilationCommand anExistingCommand, EHCompilationCommand aNewCommand){
//		return 
//				anExistingCommand.getMessageId() == aNewCommand.getMessageId() && 
//				anExistingCommand.equals(aNewCommand.getErrorMessage()) &&
//				anExistingCommand.getFileName().equals(aNewCommand.getFileName()) &&
//				anExistingCommand.getProblemText().equals(aNewCommand.getProblemText());
//
//	}
	/*
	 * Deprecated
	 */
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
	public static boolean equalsOrOverlaps(CompilationCommand aCommand1, CompilationCommand aCommand2) {
//		return equals(aCommand1, aCommand2) || overlaps(aCommand1, aCommand2);
		return aCommand1.equals(aCommand2) || aCommand1.overlaps(aCommand2);

				
//				aProblem1.getID() == aProblem2.getID() && 
//				aProblem1.getMessage().equals(aProblem2.getMessage()) &&
//				new String(aProblem1.getOriginatingFileName()).equals(new String(aProblem2.getOriginatingFileName())) &&
//				Math.abs(
//						(aProblem1.getSourceEnd() - aProblem1.getSourceStart()) -
//						(aProblem2.getSourceEnd() - aProblem2.getSourceStart())) < PROBLEM_MAX_GROWTH &&
//				Math.abs(aProblem1.getSourceLineNumber() - aProblem2.getSourceLineNumber()) < PROBLEM_MAX_DISPLACEMENT;
				
//		&&
//		getErrorMessage().equals(anOther.getMessageId()) &&
//		getSourceStart().equals(anOther.getSourceStart());
	}
	/*
	 * Deprecated
	 */
	public static IProblem contains(IProblem[] aProblems, IProblem aProblem) {
		for (IProblem aCandidate:aProblems) {
			if (equals(aCandidate, aProblem))
				return aCandidate;
		}
		return null;
	}
	/*
	 * Deprecated
	 */
	public static IProblem contains(Collection<IProblem> aProblems, IProblem aProblem) {
		for (IProblem aCandidate:aProblems) {
			if (equals(aCandidate, aProblem))
				return aCandidate;
		}
		return null;
	}
	public static CompilationCommand find(Collection<CompilationCommand> aCommands, CompilationCommand aCommand) {
		for (CompilationCommand aCandidate:aCommands) {
			if (aCandidate.equals(aCommand) || aCandidate.overlaps(aCommand))
//			if (equals(aCandidate, aCommand))
				return aCandidate;
		}
		return null;
	}
	
	public static CompilationCommand get(Map<IProblem, CompilationCommand> aMap, IProblem aProblem ) {
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
	protected Set<String> allJars = new HashSet<>(); // jars seen on this activation of eclipse
	protected Set<String> addedJars = new HashSet<>(); //the ones that triggered reconcile
	protected Set<String> deletedJars = new HashSet<>(); //the ones that triggered reconcile
	
	protected Map<String, List<String>> allImports = new HashMap<>(); 
	protected Map<String, List<String>> addedImports = new HashMap<>(); //the ones that triggered reconcile
	protected Map<String, List<String>> deletedImports = new HashMap<>(); //the ones that triggered recon

	protected CompilationUnit lastFileADT = null;
	protected IDocument lastFileDocument = null;
	// called on first reconcile, when no change has been made
	protected void setJars(ReconcileContext context) {
		ICompilationUnit aWorkingCopy = context.getWorkingCopy();
//		String aProject = aWorkingCopy.getJavaProject().getElementName();
//		IJavaModel aModel = aWorkingCopy.getJavaModel();
//		String aPackage = null;
		Set<String> aNewJars = new HashSet<>();
//		IPackageDeclaration aPackageObject;
		try {
//			 IPackageDeclaration[] aPackageDeclarations = aWorkingCopy.getPackageDeclarations();
//			 if (aPackageDeclarations.length == 0) {
//				 lastPackage = "";
//			 } else {
//			    lastPackage = aPackageDeclarations[0].getElementName();
//			 }
			 
			IPackageFragmentRoot[] aLibrrayJars = aWorkingCopy.getJavaProject().getPackageFragmentRoots();
			for (IPackageFragmentRoot aJar:aLibrrayJars) {
				String anElementName = aJar.getElementName();
				String aFullName = aJar.toString();
				if (aFullName.contains("jre") || anElementName.equals("src")) {
					continue;
				}
				if (anElementName.endsWith(".jar") || anElementName.endsWith(".zip")) {
					aNewJars.add(anElementName);
//					if (allJars.contains(anElementName))
//						continue;
//					allJars.add(anElementName); // delta tells us if properties wre used
				}
			}

			 
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addedJars.clear();
		addedJars.addAll(aNewJars);
		addedJars.removeAll(allJars);
		deletedJars.clear();
		deletedJars.addAll(allJars);
		deletedJars.removeAll(aNewJars);
		allJars.addAll(aNewJars);
		
	
	}
	protected void setPackageAndProject(ReconcileContext context) {
		ICompilationUnit aWorkingCopy = context.getWorkingCopy();
//		String aProject = aWorkingCopy.getJavaProject().getElementName();
//		IJavaModel aModel = aWorkingCopy.getJavaModel();
		String aPackage = null;
//		IPackageDeclaration aPackageObject;
		try {
			 IPackageDeclaration[] aPackageDeclarations = aWorkingCopy.getPackageDeclarations();
			 if (aPackageDeclarations.length == 0) {
				 lastPackage = "";
			 } else {
			    lastPackage = aPackageDeclarations[0].getElementName();
			 }
			 
//			IPackageFragmentRoot[] aLibrrayJars = aWorkingCopy.getJavaProject().getPackageFragmentRoots();
//			for (IPackageFragmentRoot aJar:aLibrrayJars) {
//				String anElementName = aJar.getElementName();
//				if (anElementName.contains("jre") || anElementName.equals("src")) {
//					continue;
//				}
//				if (anElementName.endsWith(".jar") || anElementName.endsWith(".zip"))
//				allJars.add(anElementName); // delta tells us if properties wre used
//			}

			 
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
//	protected void flushPendingProblems() {
//		for (IProblem aProblem:pendingProblems) {
//			EHCompilationCommand aCommand = get(allProblems, aProblem);
//			if (aCommand != null) {
//				compilationRecorder.record(aCommand);
//			}
//		}
//		pendingProblems.clear();
//		unrecordedCommands.clear();
//	}
	protected boolean flushing; // to avoid reentrance by the same thread
	protected synchronized void flushPendingCommands() {
		if (flushing)
			return;
		flushing = true;
		for (CompilationCommand aCommand:unrecordedCommands) {
			if (aCommand != null) {
				System.out.println("Recording command:" + aCommand);
				compilationRecorder.record(aCommand);
			}
		}
		unrecordedCommands.clear();
		flushing = false;
//		pendingProblems.clear();
	}
//	protected boolean maybeProcessNewFile(String anOldFileName, String aNewFileName) {
//		if (!anOldFileName.equals(aNewFileName)) {
//			
//		}
//		return false;
//		
//	}
	public static void printBinaryWithLeadingZeros(int anInt) {
		System.out.println(String.format("%32s", Integer.toBinaryString(anInt)).replace(' ', '0'));
	}
	protected void processDelta(IJavaElementDelta aDelta) {
		int aFlags = aDelta.getFlags();
		if ((aFlags & aDelta.F_FINE_GRAINED) != 0) {
			lastDeltaKind = DeltaKind.EDIT;
		} else if ((aFlags & aDelta.F_AST_AFFECTED) != 0) {
			lastDeltaKind = DeltaKind.NON_EDIT; // classpath changed does not work it seems
		} else {
			lastDeltaKind = DeltaKind.UNKNOWN;
		}
//		printBinaryWithLeadingZeros(aFlags);
//		printBinaryWithLeadingZeros(aDelta.ADDED); // amy fine grained change
//		printBinaryWithLeadingZeros(aDelta.F_AST_AFFECTED); // when library  is added, only this gets on 
//		printBinaryWithLeadingZeros(aDelta.F_ARCHIVE_CONTENT_CHANGED); 
//		printBinaryWithLeadingZeros(aDelta.F_FINE_GRAINED); // any edit = delete or insert etc, gets on
//
//		printBinaryWithLeadingZeros(aDelta.F_PRIMARY_WORKING_COPY);
//		printBinaryWithLeadingZeros(aDelta.F_RESOLVED_CLASSPATH_CHANGED);
//		printBinaryWithLeadingZeros(aDelta.F_CLASSPATH_CHANGED);
//
//
//
//		printBinaryWithLeadingZeros(aDelta.CHANGED);
//		printBinaryWithLeadingZeros(aDelta.F_CHILDREN);	
//		printBinaryWithLeadingZeros(aDelta.F_OPENED);
//		printBinaryWithLeadingZeros(aDelta.F_AST_AFFECTED); // always on
//		printBinaryWithLeadingZeros(aDelta.F_CLOSED);
//		printBinaryWithLeadingZeros(aDelta.F_CATEGORIES);
//		printBinaryWithLeadingZeros(aDelta.F_PRIMARY_WORKING_COPY);
//		
//
//		IJavaElementDelta[] anAfectedDeltas = aDelta.getAffectedChildren();
//		IJavaElementDelta[] anAddedDeltas = aDelta.getAddedChildren();
//		IJavaElementDelta[] aChangedDeltas = aDelta.getChangedChildren();
//		CompilationUnit anAST = aDelta.getCompilationUnitAST();
//		System.out.println(Integer.toString(aDelta.F_ADDED_TO_CLASSPATH, 2));
//		System.out.println(Integer.toString(aDelta.F_CLASSPATH_CHANGED, 2) + " " + Integer.toString(aFlags, 2) + " && " +  Integer.toString(aDelta.F_CLASSPATH_CHANGED & aFlags, 2));
		
	}
	/*
	 * Assuming that new json object will be received before reconsile is called
	 * @see org.eclipse.jdt.core.compiler.CompilationParticipant#reconcile(org.eclipse.jdt.core.compiler.ReconcileContext)
	 * This method is called essentially on each edit after the file name and text has been set, and also it seems when a new file is opened
	 */
	@Override
	public void reconcile(ReconcileContext context) {
		if (lastFileContents.isEmpty()) {
			setJars(context);
			return; // no edit has taken place, starting up
		}
		IJavaElementDelta aDelta = context.getDelta();
		processDelta(aDelta);
		/*
		 * Switch statement is better?
		 */
		if (lastDeltaKind == DeltaKind.UNKNOWN) {
			return;
		}
		if (lastDeltaKind == DeltaKind.NON_EDIT && lastCommandExecuted.equals(PropertyDialogClosedCommand.class.getSimpleName())) {
			setJars(context); // not sure what it can be besides class path change
			maybeRecordJarCommands();
			return;
		}
		reconcileCommands.clear(); // maybe should make this a local var
		// System.out.println("Context:" + context + "class, package, project: "
		// + context.getWorkingCopy() +
		// " file name" + context.getDelta().getElement().getElementName() + "
		// delta " + context.getDelta());
		// String aNewFileName =
		// context.getDelta().getElement().getElementName();
		// IPath aFilePath =
		// context.getDelta().getElement().getResource().getFullPath();
		// maybeProcessNewFile(lastFileName, aNewFileName);

		setPackageAndProject(context); // the sets not used it seems

		if (lastFileADT == null) {
			Tracer.info(this, "Last file adt null for adelta:" + aDelta);
			return;
		}
		lastFileADT = aDelta.getCompilationUnitAST();
		
		List<String> anImports = lastFileADT.imports();
		// System.out.println("AST/full source code:" + lastFileADT);
		IProblem[] problems = null;

		switch (context.getASTLevel()) {

		case AST_LEVEL_THREE:
			try {
				problems = context.getAST3().getProblems();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			break;
		case AST_LEVEL_FOUR:
			try {
				problems = context.getAST4().getProblems();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			break;
		default:
			try {
				problems = context.getAST3().getProblems();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			break;
		}
		Set<CompilationCommand> aRemovedCommands = new HashSet();

		if (problems != null) {
			if (problems.length > 0) {
				int numWarnings = 0;
				int numErrors = 0;
				Set<IProblem> aCurrentProblemSet = new HashSet(Arrays.asList(problems));
				// Set<IProblem> aRemovedProblems = new HashSet();
				for (int i = 0; i < problems.length
						&& (numWarnings < MAX_COMPILE_WARNINGS || numErrors < MAX_COMPILE_ERRORS); i++) {
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
					CompilationCommand aPossiblyNewCommand = createCommand(problem);
					reconcileCommands.add(aPossiblyNewCommand);
				}
			}
		}
				/*
				 * Find each previous problem and unrecorded command in
				 * reconsole commands to see if it should be recorded or
				 * disappeared
				 */
				for (CompilationCommand aPreviousCommand : allCommands) {

					// if (!aCurrentProblemSet.contains(aProblem)) {
					/*
					 * previous pronlem is no longer a problem, and it has
					 * should be recorded or unrecorded
					 */
					if (find(reconcileCommands, aPreviousCommand) == null) {
						// aRemovedProblems.add(aProblem);
						aRemovedCommands.add(aPreviousCommand);
						if (find(unrecordedCommands, aPreviousCommand) == null) {
							// EHCompilationCommand aCommand = get(allProblems,
							// aProblem);

							aPreviousCommand.setDisappeared(true);

							compilationRecorder.record(aPreviousCommand);
						}
					}
				}
				// for (IProblem aRemovedProblem:aRemovedProblems) {
				// allProblems.remove(aRemovedProblem);
				// }
				for (CompilationCommand aRemovedCommand : aRemovedCommands) {
					allCommands.remove(aRemovedCommand);
				}
				/*
				 * Now search each new command in existing commands to see if it
				 * is new and hence should be added to unrecorded commands and
				 * allcommands can this search be integrated with the previous
				 * one?
				 */
				for (CompilationCommand aReconcileCommand : reconcileCommands) {
					CompilationCommand anExistingCommand = find(allCommands, aReconcileCommand);
					CompilationCommand anExistingOrNewCommand = anExistingCommand;
					if (anExistingCommand == null) {
						unrecordedCommands.add(aReconcileCommand);
						allCommands.add(aReconcileCommand);
						anExistingOrNewCommand = aReconcileCommand;
					} else {
						// anExistingCommand.setProblemLine(aReconcileCommand.getProblemLine());
						// anExistingCommand.setProblemText(aReconcileCommand.getProblemText());
						anExistingCommand.merge(aReconcileCommand); // need to this only for urecorded actually
						anExistingCommand.increaseRepeatCount();
						// EHCompilationCommand anExistingUnrecordedCommand =
						// find(unrecordedCommands, aReconcileCommand);
						//
						// if (anExistingUnrecordedCommand != null) {
						//
						//// unrecordedCommands.remove(anUnrecordedCommand);
						//// unrecordedCommands.add(aReconcileCommand);
						// } else {
						// anExistingCommand.increaseRepeatCount(); // already
						// // recorded
						// // so
						// // pointless
						// // for
						// // now
						// }

					}
					// EHCompilationCommand anUnrecordedCommand =
					// find(unrecordedCommands, aReconcileCommand);

					if (anExistingOrNewCommand.isPersistent() && (find (unrecordedCommands, anExistingOrNewCommand) == null)) { // shoul not
																	// return
																	// true if
																	// aCommand
																	// is new

						unrecordedCommands.remove(anExistingOrNewCommand);
						compilationRecorder.record(anExistingOrNewCommand);
					}
//				}
//
//			}

		}

	}
//	@Override
//	public void reconcile(ReconcileContext context) 
//	{
//		reconcileCommands.clear();
//		System.out.println("Context:" + context + "class, package, project: " + context.getWorkingCopy()  +
//				" file name" + context.getDelta().getElement().getElementName() + " delta " + context.getDelta());
//		String aNewFileName = context.getDelta().getElement().getElementName();
//		IPath aFilePath = context.getDelta().getElement().getResource().getFullPath();
//		maybeProcessNewFile(lastFileName, aNewFileName);
//		
//		setPackageAndProject(context);
//		
//		IJavaElementDelta aDelta = context.getDelta();
//		processDelta(aDelta);
//		lastFileADT = aDelta.getCompilationUnitAST();
//		System.out.println("AST/full source code:" + lastFileADT);
//		IProblem[] problems= null;
//		
//		switch(context.getASTLevel())
//		{
//
//		case AST_LEVEL_THREE:
//			try 
//			{
//				problems = context.getAST3().getProblems();
//			} catch (JavaModelException e) 
//			{
//				e.printStackTrace();
//			}
//			break;
//		case AST_LEVEL_FOUR:
//			 try
//			 {
//				 problems = context.getAST4().getProblems();
//			 } catch (JavaModelException e)
//			 {
//				 e.printStackTrace();
//			 }
//			 break;
//			default:
//				try 
//				{
//					problems = context.getAST3().getProblems();
//				} catch (JavaModelException e) 
//				{
//					e.printStackTrace();
//				}
//				break;
//		}
//		
//		if(problems != null)
//		{
//			if(problems.length > 0)
//			{
//				int numWarnings = 0;
//				int numErrors = 0;
//				Set<IProblem> aCurrentProblemSet = new HashSet(Arrays.asList(problems));
//				Set<IProblem> aRemovedProblems = new HashSet();
//				for (IProblem aProblem:allProblems.keySet()) {
//					EHCompilationCommand aPossiblyNewCommand = createCommand(aProblem);
//					reconcileCommands.add(aPossiblyNewCommand);
//				}
//				
//				for (IProblem aProblem:allProblems.keySet()) {
//					EHCompilationCommand aPossiblyNewCommand = createCommand(aProblem);
//					reconcileCommands.add(aPossiblyNewCommand);
//					
//					
//
////					if (!aCurrentProblemSet.contains(aProblem)) {
//					/*
//					 * previous pronlem is no longer a problem, and it has been recorded
//					 */
//					if (contains(problems, aProblem) == null ) {
//						aRemovedProblems.add(aProblem);
//						if (contains(pendingProblems, aProblem) == null) {
//						EHCompilationCommand aCommand = get(allProblems, aProblem);
//
//						aCommand.setDisappeared(true);
//						
//						compilationRecorder.record(aCommand);
//						}
//					}
//				}
//				for (IProblem aRemovedProblem:aRemovedProblems) {
//					allProblems.remove(aRemovedProblem);
//				}
//				for(int i = 0; i < problems.length && (numWarnings < MAX_COMPILE_WARNINGS || numErrors < MAX_COMPILE_ERRORS) ; i++)
//				{
//					IProblem problem = problems[i];
//					
//					if (problem.isWarning()) {
//						numWarnings++;
//						if (numWarnings > MAX_COMPILE_WARNINGS) {
//							continue;
//						}
//					} else if (problem.isError()) {
//						numErrors++;
//						if (numErrors > MAX_COMPILE_ERRORS) {
//							continue;
//						}
//					}
////					EHCompilationCommand aCommand = allProblems.get(problem);
//					
//					EHCompilationCommand anExistingCommand = get(allProblems, problem);
//					EHCompilationCommand aCommand = anExistingCommand;
//					if (anExistingCommand == null) {
//						aCommand = aPossiblyNewCommand;
////						anExistingCommand = createCommand(problem);
//						allProblems.put(problem, aCommand);
//						allCommands.add(aCommand);
//						pendingProblems.add(problem);
//						unrecordedCommands.add(anExistingCommand);
//					} else {
//						anExistingCommand.increaseRepeatCount();
//						
//					}
//					if (aCommand.isPersistent()) { // shoul not return true if aCommand is new
//						IProblem aPendingProblem = contains(pendingProblems, problem);
//						if (aPendingProblem != null ) {
//					
//						pendingProblems.remove(aPendingProblem);
//						compilationRecorder.record(anExistingCommand);
//						}
//					}
//					
//					
////					compilationRecorder.record(command);
//				}
//			}
//		}
//	}
//	@Override
//	public void reconcile(ReconcileContext context) 
//	{
//		reconcileCommands.clear();
//		System.out.println("Context:" + context + "class, package, project: " + context.getWorkingCopy()  +
//				" file name" + context.getDelta().getElement().getElementName() + " delta " + context.getDelta());
//		String aNewFileName = context.getDelta().getElement().getElementName();
//		IPath aFilePath = context.getDelta().getElement().getResource().getFullPath();
//		maybeProcessNewFile(lastFileName, aNewFileName);
//		
//		setPackageAndProject(context);
//		
//		IJavaElementDelta aDelta = context.getDelta();
//		processDelta(aDelta);
//		lastFileADT = aDelta.getCompilationUnitAST();
//		System.out.println("AST/full source code:" + lastFileADT);
//		IProblem[] problems= null;
//		
//		switch(context.getASTLevel())
//		{
//
//		case AST_LEVEL_THREE:
//			try 
//			{
//				problems = context.getAST3().getProblems();
//			} catch (JavaModelException e) 
//			{
//				e.printStackTrace();
//			}
//			break;
//		case AST_LEVEL_FOUR:
//			 try
//			 {
//				 problems = context.getAST4().getProblems();
//			 } catch (JavaModelException e)
//			 {
//				 e.printStackTrace();
//			 }
//			 break;
//			default:
//				try 
//				{
//					problems = context.getAST3().getProblems();
//				} catch (JavaModelException e) 
//				{
//					e.printStackTrace();
//				}
//				break;
//		}
//		
//		if(problems != null)
//		{
//			if(problems.length > 0)
//			{
//				int numWarnings = 0;
//				int numErrors = 0;
//				Set<IProblem> aCurrentProblemSet = new HashSet(Arrays.asList(problems));
//				Set<IProblem> aRemovedProblems = new HashSet();
//				for (IProblem aProblem:allProblems.keySet()) {
//					EHCompilationCommand aPossiblyNewCommand = createCommand(aProblem);
//					reconcileCommands.add(aPossiblyNewCommand);
//				}
//				
//				for (EHCompilationCommand aPossiblyNewCommand:allCommands) {
////					EHCompilationCommand aPossiblyNewCommand = createCommand(aProblem);
////					reconcileCommands.add(aPossiblyNewCommand);
//					
//					
//
////					if (!aCurrentProblemSet.contains(aProblem)) {
//					/*
//					 * previous pronlem is no longer a problem, and it has been recorded
//					 */
//					if (contains(problems, aProblem) == null ) {
//						aRemovedProblems.add(aProblem);
//						if (contains(pendingProblems, aProblem) == null) {
//						EHCompilationCommand aCommand = get(allProblems, aProblem);
//
//						aCommand.setDisappeared(true);
//						
//						compilationRecorder.record(aCommand);
//						}
//					}
//				}
//				for (IProblem aRemovedProblem:aRemovedProblems) {
//					allProblems.remove(aRemovedProblem);
//				}
//				for(int i = 0; i < problems.length && (numWarnings < MAX_COMPILE_WARNINGS || numErrors < MAX_COMPILE_ERRORS) ; i++)
//				{
//					IProblem problem = problems[i];
//					
//					if (problem.isWarning()) {
//						numWarnings++;
//						if (numWarnings > MAX_COMPILE_WARNINGS) {
//							continue;
//						}
//					} else if (problem.isError()) {
//						numErrors++;
//						if (numErrors > MAX_COMPILE_ERRORS) {
//							continue;
//						}
//					}
////					EHCompilationCommand aCommand = allProblems.get(problem);
//					
//					EHCompilationCommand anExistingCommand = get(allProblems, problem);
//					EHCompilationCommand aCommand = anExistingCommand;
//					if (anExistingCommand == null) {
//						aCommand = aPossiblyNewCommand;
////						anExistingCommand = createCommand(problem);
//						allProblems.put(problem, aCommand);
//						allCommands.add(aCommand);
//						pendingProblems.add(problem);
//						unrecordedCommands.add(anExistingCommand);
//					} else {
//						anExistingCommand.increaseRepeatCount();
//						
//					}
//					if (aCommand.isPersistent()) { // shoul not return true if aCommand is new
//						IProblem aPendingProblem = contains(pendingProblems, problem);
//						if (aPendingProblem != null ) {
//					
//						pendingProblems.remove(aPendingProblem);
//						compilationRecorder.record(anExistingCommand);
//						}
//					}
//					
//					
////					compilationRecorder.record(command);
//				}
//			}
//		}
//	}
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
	protected void maybeRecordJarCommands () {
		if (addedJars.size() > 0) {
			LibrariesAdded anAddedCommand = new LibrariesAdded("", addedJars);
			compilationRecorder.record(anAddedCommand);
		}
		if (deletedJars.size() > 0) {
			LibrariesRemoved aRemovedCommand = new LibrariesRemoved("", deletedJars);
			compilationRecorder.record(aRemovedCommand);
		}		
	}

	protected CompilationCommand createCommand (IProblem problem)
	{
//		String aSource = getCurrentEditorContent() ;
//		String aSource = lastFileContents.toString();
//		int aLineNumber = problem.getSourceLineNumber();
//		int aSourceStart = lastFileContents.getPosition(aSource, problem.getSourceStart())
		
		
		
		int aProblemLineStart = lastFileADT.getPosition(problem.getSourceLineNumber() - 1, 0 );
		if (aProblemLineStart < 0) {
			aProblemLineStart = problem.getSourceStart();
		}
		
		int aProblemLineEnd =  Math.max(problem.getSourceEnd(),lastFileADT.getPosition(problem.getSourceLineNumber() + 1, 0));
//		if (aProblemLineEnd < 0) {
//			aProblemLineEnd = problem.getSourceEnd();
//		}
//		int aProblemLineStart = problem.getSourceStart();
//		int aProblemLineEnd = problem.getSourceEnd();
		if (aProblemLineStart < 0) {
			System.err.println("aProblemLineStart is < 0");
					aProblemLineStart = 0;		
		}
		if (aProblemLineEnd < 0) {
			System.err.println("aProblemLineEnd is < 0");
			   aProblemLineEnd = 0;		
		}
		String aProblemLine = lastFileContents.substring(aProblemLineStart, aProblemLineEnd);

//		System.out.println("Message: " + problem.getMessage());
//		System.out.println("Source Line #: " + problem.getSourceLineNumber());
//		System.out.println("Source Start: " + problem.getSourceStart());
//		System.out.println("Source End: " + problem.getSourceEnd());
		String aProblemText = lastFileContents.substring(problem.getSourceStart(), problem.getSourceEnd()+1);
//		System.out.println("File name: " + problem.getOriginatingFileName().toString());
//		System.out.println("Problem Id: " + problem.getID());
		
//		String aProblemText = lastFileContents.toString().substring(problem.getSourceStart(), problem.getSourceEnd());
		CompilationCommand command = null;
		String fileName = new String(problem.getOriginatingFileName());
		if(problem.isError())
		{
			command = new CompilationCommand(false, problem.getMessage(), problem.getID(), problem.getSourceLineNumber(),
					problem.getSourceStart(), problem.getSourceEnd(), aProblemText, aProblemLine, fileName);
		}
		else if(problem.isWarning())
		{
			command = new CompilationCommand(true, problem.getMessage(), problem.getID(), problem.getSourceLineNumber(),
					problem.getSourceStart(), problem.getSourceEnd(), aProblemText, aProblemLine, fileName);
		}
//		{
//			command = new EHCompilationCommand(false, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
//					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), aProblemText, aProblemLine, fileName);
//		}
//		else if(problem.isWarning())
//		{
//			command = new EHCompilationCommand(true, problem.getMessage(),String.valueOf(problem.getID()), String.valueOf(problem.getSourceLineNumber()),
//					String.valueOf(problem.getSourceStart()), String.valueOf(problem.getSourceEnd()), aProblemText, aProblemLine, fileName);
//		}
		

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
		BuildStartEvent command = new BuildStartEvent("");
		compilationRecorder.record(command);
		return CompilationParticipant.READY_FOR_BUILD;
	}
	/*
	 * Called after file save
	 */
	@Override
	public void buildFinished(IJavaProject project) 
	{
		BuildEndEvent command = new BuildEndEvent("");
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
		int anOffeset = aDocumentEvent.getOffset();
		lastFileDocument = aDocumentEvent.getDocument();
		lastFileContents = lastFileDocument.get();
//		String aContents = aDocumentEvent.getDocument().get();
//		System.out.println(aContents);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	@Override
	public void eventRecordingStarted(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void eventRecordingEnded() {
		// TODO Auto-generated method stub
		
	}
	protected String lastCommandExecuted = "";
	@Override
	public void commandExecuted(String aCommandName, long aTimestamp) {
		// lost focus seems to be executed before recocile is called,
		// we will need a set of ignore commands perhaps
		if (!aCommandName.equals(ShellCommand.class.getSimpleName())) { 
		lastCommandExecuted = aCommandName;
		}
		/*
		 * return if this a compliation event
		 */
		if (aCommandName.equals(CompilationCommand.class.getSimpleName())) {
			return;
		}
		flushPendingCommands();
		
	}
	@Override
	public void documentChanged(String aCommandName, long aTimestamp) {
		lastCommandExecuted = "";
		// TODO Auto-generated method stub
		
	}
	@Override
	public void documentChangeFinalized(long aTimestamp) {
		flushPendingCommands();
				
	}

}
