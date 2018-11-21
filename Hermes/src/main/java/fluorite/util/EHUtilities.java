package fluorite.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.ISourceViewerExtension4;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

//import edu.cmu.scs.fluorite.util.Utilities;
import fluorite.commands.EclipseCommand;
import fluorite.commands.FindCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.InsertStringCommand;
import fluorite.dialogs.FindConfigureDialog;
import fluorite.model.EHEventRecorder;
//import fluorite.plugin.Activator;
import fluorite.plugin.EHActivator;
import util.misc.ThreadSupport;
/*
 * This is are not being referred to, instead the Flourite Utilites class is referenced
 */
public class EHUtilities /*extends Utilities*/{
	protected static Map <IFile, ITextEditor> fileToEditor = new HashMap<>();
	private static Map<Integer, Command> mFillInCommands = new HashMap<Integer, Command>();
	protected static Map<String, Command> stringToCommand = new HashMap<>();

	public static final String FillInPrefix = "eventLogger.styledTextCommand";
	public static final String JavaBuilder = "org.eclipse.jdt.core.javabuilder";
	public static final String[] JAVA_NATURE = {"org.eclipse.jdt.core.javanature"};
	public static Method clickButtonMethod;

	

	public static String NewLine = System.getProperty("line.separator");

	private static Set<String> mEditCategories;
	
	protected static Display myDisplay;
	protected static ICommandService myService;
	protected static IEditorPart currentEditorPart;
	protected static IWorkbenchWindow currentWorkbenchWindow;
	
	
	static {
		mEditCategories = new HashSet<String>();
		mEditCategories.add("org.eclipse.ui.category.edit");
		mEditCategories.add("org.eclipse.ui.category.textEditor");
		mEditCategories.add("org.eclipse.jdt.ui.category.source");
		mEditCategories.add("org.eclipse.jdt.ui.category.refactoring");
		mEditCategories.add(EHEventRecorder.MacroCommandCategoryID);
		mEditCategories.add("eclipse.ui.category.navigate");
		mEditCategories.add(FillInPrefix);
	}
	public static IProject getCurrentProject() {
	    IProject project = null;
	    IWorkbenchWindow window = PlatformUI.getWorkbench()
	            .getActiveWorkbenchWindow();
	    if (window != null) {
	        ISelection iselection = window.getSelectionService().getSelection();
	        IStructuredSelection selection = (IStructuredSelection) iselection;
	        if (selection == null) {
	            return null;
	        }

	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IResource) {
	            project = ((IResource) firstElement).getProject();
	        } else if (firstElement instanceof PackageFragmentRoot) {
	            IJavaProject jProject = ((PackageFragmentRoot) firstElement)
	                    .getJavaProject();
	            project = jProject.getProject();
	        } else if (firstElement instanceof IJavaElement) {
	            IJavaProject jProject = ((IJavaElement) firstElement)
	                    .getJavaProject();
	            project = jProject.getProject();
	        }
	    }
	    return project;
	}

	
	public static void refreshFile (IProject aProject, String aFileName) {
		IFile aFile = aProject.getFile(aFileName);
		refreshResource(aFile);	
//		getMyDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				refreshResource(aFile);	
//			}
//		});
//		refreshResource(aFile);		
	}
	public static void addResource (IProject aProject, String aFileName) {
		IFile aFile = aProject.getFile(aFileName);
		if (!aFile.exists()) {
			byte[] bytes = "File contents".getBytes();
		    InputStream source = new ByteArrayInputStream(bytes);
		    try {
				aFile.create(source, IResource.NONE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void refreshResource(IResource aResource) {
		try {
			aResource.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static IProject getProject(String aProjectName) {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();


		return root.getProject(aProjectName);
		
	}
	public static void setJavaBuilderName (IProjectDescription desc) {
		try {
	    ICommand[] buildSpec = desc.getBuildSpec();
	    ICommand command = desc.newCommand();
	    command.setBuilderName( "org.eclipse.jdt.core.javabuilder" );
//	    Collection<ICommand> aCommands = new ArrayList<>( Arrays.asList( buildSpec ) );
	    Collection<ICommand> aCommands = new ArrayList();
	    aCommands.add(command);
	    desc.setBuildSpec(aCommands.toArray(new ICommand[ aCommands.size()] ));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void setJavaNature (IProjectDescription desc) {
	    desc.setNatureIds(JAVA_NATURE);
	 

	}
//	public static final IClasspathEntry[] CLASS_PATH_ENTRIES = new IClasspathEntry[]{
//			JavaCore.newSourceEntry(new Path("src")),
//			JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8"))
//	};		
	
	public static final String SRC = "src";
	public static final String CONTAINER_PREFIX = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType";

//	public static final String CONTAINER_PREFIX = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8";
	public static void setClassPath(IProject project, String aJavaVersion) {
		try {
            IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
            IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
            if (rawClasspath != null && rawClasspath.length > 1) {
            	return;
            }
            
            IClasspathEntry aSourceEntry = JavaCore.newSourceEntry(new Path(project.getFullPath()+ "/" + SRC));
            IClasspathEntry aContainerEntry = JavaCore.newContainerEntry(new Path(CONTAINER_PREFIX + "/" + aJavaVersion));
            IClasspathEntry[] classPathEntries = new IClasspathEntry[] {
            		aSourceEntry,
            		aContainerEntry
            };
            javaProject.setRawClasspath(classPathEntries, new NullProgressMonitor());

            
                        
//            List list = new LinkedList(java.util.Arrays.asList(rawClasspath));
//            for(String path:jarPathList){
//                String jarPath = path.toString();
//                boolean isAlreadyAdded=false;
//                for(IClasspathEntry cpe:rawClasspath){
//                    isAlreadyAdded=cpe.getPath().toOSString().equals(jarPath);
//                    if (isAlreadyAdded) break;
//                }
//                if (!isAlreadyAdded){
//                    IClasspathEntry jarEntry = JavaCore.newLibraryEntry(new Path(jarPath),null,null);
//                    list.add(jarEntry);
//                }
//            }
//            IClasspathEntry[] newClasspath = (IClasspathEntry[])list.toArray(new IClasspathEntry[0]);
//            javaProject.setRawClasspath(newClasspath,null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
		
	}
	public static IProject createProjectFromLocation (String aProjectName, String aLocation, String aJavaVersion) {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();


		IProject project = root.getProject(aProjectName);
		  if (!project.exists()) {
		
		    IWorkspace w = ResourcesPlugin.getWorkspace();
		    IProjectDescription desc=w.newProjectDescription(project.getName()); 
		    
		    String projectLocation= aLocation;
//		    IPath path1=new Path(projectLocation+"/"+aProjectName);
		    IPath path1=new Path(projectLocation);
		    desc.setLocation(path1);
		    setJavaNature(desc);
		    setJavaBuilderName(desc);
		    
//		    ICommand[] buildSpec = desc.getBuildSpec();
//		    ICommand command = desc.newCommand();
//		    command.setBuilderName( "org.eclipse.jdt.core.javabuilder" );
////		    Collection<ICommand> list = new ArrayList<>( Arrays.asList( buildSpec ) );
//		    Collection<ICommand> aCommands = new ArrayList();
//		    aCommands.add(command);
//		    desc.setBuildSpec(aCommands.toArray(new ICommand[ aCommands.size()] ));
		    
		    
//		    desc.setNatureIds(JAVA_NATURE);
		    
//		    aCommands.add( command );
//		    desc.setBuildSpec( aCommands.toArray( new ICommand[ aCommands.size() ] ) );
//		    String[] natures = desc.getNatureIds();
//		    Collection<String> strings = new ArrayList<>( Arrays.asList( natures ) );
//		    strings.add("org.eclipse.jdt.core.javanature");
//		    desc.setNatureIds(natures);

		    
		 
		  
		    try {
				project.create(desc, progressMonitor);
//				setClassPath(project, aLocation);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    }
		    if (!project.isOpen()) {
		    try {
				project.open(progressMonitor);
				setClassPath(project, aJavaVersion);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		    }
		    return project;
	}
	public IProject createProjectFromFolder (String aProjectName, String aFolderName) {
//		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();


		IProject project = root.getProject(aProjectName);
		IFolder aFolder = project.getFolder(aFolderName);
		try {
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) project.open(null);
		if (!aFolder.exists()) {
			aFolder.create(IResource.NONE, true, null);
		}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
		    
	}
	public static IFile getIFile(IProject aProject, String aFileName) {
		return aProject.getFile(aFileName);
	}
//	public static IEditorPart openEditor(IProject aProject, String aFileName) {
//		IFile aFile = aProject.getFile(aFileName);
//		return openEditor(aFile);
//	}
	static ThreadPoolExecutor executor;
	public static void maybeCreateThreadPoolExecutor() {
		if (executor == null) {
			executor = 
					  (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		}
		
	}
	public static Map<String, String> EMPTY_MAP = new HashMap<>();
	public static void executeCommand(String aCommandName, Map<String, String> aParameters) {
		createStyledTextCommands();
		Command aCommand = stringToCommand.get(aCommandName);
		if (aCommand == null) {
			return;
		}
		if (aParameters == null) {
			aParameters = EMPTY_MAP;
		}
		try {
			
			IHandlerService handlerService = (IHandlerService) ((IServiceLocator) PlatformUI.getWorkbench())
                    .getService(IHandlerService.class);

			Parameterization[] params = new Parameterization[aParameters.size()];
			int aParameterIndex = 0;
			for (String aKey:aParameters.keySet()) {
				Parameterization aParameterization = new Parameterization(
													aCommand.getParameter(aKey), aParameters.get(aKey));
				params[aParameterIndex] = aParameterization;
				
			}
			
//			{ new Parameterization(
//                    aCommand.getParameter(""), "true") };
			ParameterizedCommand parametrizedCommand = new ParameterizedCommand(aCommand, params);
			handlerService.executeCommand(parametrizedCommand, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void invokeCloseInSeparateThread(FindCommand aFindCommand) {
		executor().submit(() -> {
			invokeCloseInUIThread(aFindCommand);
		});
	}
	
	public static void invokeCloseInUIThread(FindCommand aFindCommand) {
		if (getDisplay() == null) {
			return;
		}
		
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
//					aFindCommand.configureNew(Display.getDefault().getActiveShell());
//					aFindCommand.configureWithSearchTermNonBlocking(Display.getDefault().getActiveShell(), aFindCommand.getSearchString());

					FindConfigureDialog aFindConfigureDialog =  aFindCommand.getFindConfigureDialog();
					if (aFindConfigureDialog != null) {
						aFindConfigureDialog.close();
					}
//					Button aFindButton = aFindConfigureDialog.getFindButton();
////					Thread.sleep(1000);
//					clickButtonMethod().invoke(aFindButton);
//					int i = 0;
//					ThreadSupport.sleep(1000);
//					aFindConfigureDialog.close();
				} catch (Exception  e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static void invokeFindInSeparateThread(FindCommand aFindCommand) {
		executor().submit(() -> {
			invokeFindInUIThread(aFindCommand);
		});
	}
	public static void invokeFindInUIThread(FindCommand aFindCommand) {
		if (getDisplay() == null) {
			return;
		}
		
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
//					aFindCommand.configureNew(Display.getDefault().getActiveShell());
					aFindCommand.configureWithSearchTermNonBlocking(Display.getDefault().getActiveShell(), aFindCommand.getSearchString());

					FindConfigureDialog aFindConfigureDialog =  aFindCommand.getFindConfigureDialog();
					Button aFindButton = aFindConfigureDialog.getFindButton();
//					Thread.sleep(1000);
					clickButtonMethod().invoke(aFindButton);
//					int i = 0;
//					ThreadSupport.sleep(1000);
//					aFindConfigureDialog.close();
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException  e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static void invokeUndoInSeparateThread (IUndoManager anUndoManager) {
		executor().submit(() -> {
			invokeUndoInUIThread(anUndoManager);
		});
	}
	public static void invokeUndoInUIThread (IUndoManager anUndoManager) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				anUndoManager.undo();
			}
		});
	}
	public static void invokeRedoInSeparateThread (IUndoManager anUndoManager) {
		executor().submit(() -> {
			invokeUndoInUIThread(anUndoManager);
		});
	}
	public static void invokeRedoInUIThread (IUndoManager anUndoManager) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				anUndoManager.redo();
			}
		});
	}
	public static void showContributionItemInSeparateThread (IContributionItem aMenuManager) {
		executor().submit(() -> {
			showContribitionItemInUIThread(aMenuManager);
		});
	}
	public static void showContribitionItemInUIThread (IContributionItem aMenuManager) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				
					aMenuManager.setVisible(true);
				
			}
		});
	}
	public static void lineBreakpointToggleInSeparateThread(IType aSourceType, ITextEditor aTextEditor, int aLineNumber) { 
		executor().submit(() -> {
			lineBreakpointToggleInUIThread(aSourceType, aTextEditor, aLineNumber);
		});
	}
	public static void lineBreakpointToggleInUIThread(IType aSourceType, ITextEditor aTextEditor, int aLineNumber) { 
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					lineBreakpointToggle(aSourceType, aTextEditor, aLineNumber);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void replaceTextInSeparateThread (StyledText aStyledText, int aStart, int aLength, String aText) {
		executor().submit(() -> {
			replaceTextInUIThread(aStyledText, aStart, aLength, aText);
		});
	}
	public static void replaceTextInUIThread (StyledText aStyledText, int aStart, int aLength, String aText) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aStyledText.replaceTextRange(aStart, aLength, aText);
			}
		});
	}
	public static void findAndSelectTextAfterCursorInSeparateThread (
			StyledText aStyledText,
			IFindReplaceTargetExtension3 aFindReplaceTargetExtension3,
			String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord, boolean aRegExSearch) {
		executor().submit(() -> {
			findAndSelectTextAfterCursorInUIThread(aStyledText, aFindReplaceTargetExtension3, aFindString, aSearchForward, aCaseSensitive, aWholeWord, aRegExSearch);
		});
	}
	public static void findAndSelectTextAfterCursorInUIThread (
			StyledText aStyledText,
			IFindReplaceTargetExtension3 aFindReplaceTargetExtension3,
			String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord, boolean aRegExSearch) {
		
	
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aFindReplaceTargetExtension3.findAndSelect(
						aStyledText.getCaretOffset(), aFindString, aSearchForward, aCaseSensitive, aWholeWord, aRegExSearch);
			}
		});
	}
	
	public static void replaceSelectionInSeparateThread (
			StyledText aStyledText,
			IFindReplaceTargetExtension3 aFindReplaceTargetExtension3,
			String aReplacement,
			 boolean aRegExSearch) {
		executor().submit(() -> {
			replaceSelectionInUIThread(aStyledText, aReplacement, aFindReplaceTargetExtension3, aRegExSearch);
		});
	}
	public static void replaceSelectionInUIThread (
			StyledText aStyledText,
			String aReplacement,
			IFindReplaceTargetExtension3 aFindReplaceTargetExtension3,
			 boolean aRegExSearch){
		
	
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aFindReplaceTargetExtension3.replaceSelection(aReplacement, aRegExSearch);
			}
		});
	}
	public static void replaceAllInUIThread (
			ITextViewer aTextViewer,
			IUndoManager anUndoManager,
			StyledText aStyledText,
			IFindReplaceTargetExtension3 aFindReplaceTargetExtension3,
			String aReplaceString,
			String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord, boolean aRegExSearch) {
		
	
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				int anOffset = aStyledText.getCaretOffset();
				
				
				aFindReplaceTargetExtension3.findAndSelect(
						aStyledText.getCaretOffset(), aFindString, aSearchForward, aCaseSensitive, aWholeWord, aRegExSearch);
			}
		});
	}
	
	public static void insertTextAfterCursorInSeparateThread (StyledText aStyledText,  String aText) {
		executor().submit(() -> {
			insertTextAfterCursorInUIThread(aStyledText,  aText);
		});
	}
	public static void insertTextAfterCursorInUIThread (StyledText aStyledText,  String aText) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aStyledText.replaceTextRange(aStyledText.getCaretOffset(), 0, aText);
			}
		});
	}
	public static void executeCommandInSeparateThread (String aCommandName, Map<String, String> aParameters) {
		executor().submit(() -> {
			executeCommandInUIThread(aCommandName, aParameters);
		    return null;
		});
	}
	public static void executeCommandInUIThread (String aCommandName, Map<String, String> aParameters) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				executeCommand(aCommandName, aParameters);
			}
		});
	}
	public static void invokeActionInSeparateThread (StyledText aStyledText, int anAction) {
		executor().submit(() -> {
			invokeActionInUIThread(aStyledText, anAction);
		    return null;
		});
	}
	public static void invokeActionInUIThread (StyledText aStyledText, int anAction) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aStyledText.invokeAction(anAction);;
			}
		});
	}
	public static void  openEditorInUIThread(IProject aProject, String aFileName) {
		IFile aFile = aProject.getFile(aFileName);
		 openEditorInUIThread(aFile);
	}
	public static void  openEditor(String aProjectName, String aFileName) {
		IProject aProject = getProject(aProjectName);
		IFile aFile = aProject.getFile(aFileName);
		 openEditorInUIThread(aFile);
	}
	public static ThreadPoolExecutor executor() {
		maybeCreateThreadPoolExecutor();
		return executor;
	}
	public static void launchInSeparateThread (ILaunchConfiguration aConfiguration, String aMode) {
		executor().submit(() -> {
			launchInUIThread(aConfiguration, aMode);
		    return null;
		});
	}
	public static void launchInUIThread (ILaunchConfiguration aConfiguration, String aMode) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				launch(aConfiguration, aMode);
			}
		});
	}
	
	public static void launch(ILaunchConfiguration aConfiguration, String aMode) {
		try {
			aConfiguration.launch(aMode, nullProgressMonitor );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void saveTextInSeparateThread(ITextEditor anEditor) {
		
				executor().submit(() -> {
					saveTextInUIThread(anEditor);
				    return null;
				});
//		Runnable newRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				saveTextInUIThread(anEditor);
//			}
//			
//		};
//		Thread newThread = new Thread(newRunnable) ;
//		newThread.start();
	}
	public static void openEditorFromSeparateThread(IProject aProject, String aFileName) {
		executor().submit(() -> {
			openEditorInUIThread(aProject, aFileName);
		    return null;
		});
		
	}
	public static void positionCursorInSeparateThread(StyledText aText, int anOffset) { 
		executor().submit(() -> {
			positionCursorInUIThread(aText, anOffset);
		    return null;
		});
		
//		Runnable newRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				selectTextInUIThread(anEditor, anOffset, aLength);
//			}
//			
//		};
//		Thread newThread = new Thread(newRunnable) ;
//		newThread.start();
		
	}
	public static void positionCursorInUIThread(StyledText aText, int anOffset) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				aText.setCaretOffset(anOffset);
			}
		});
	}
	public static void selectTextInSeparateThread(ITextEditor anEditor, int anOffset, int aLength) { 
		executor().submit(() -> {
			selectTextInUIThread(anEditor, anOffset, aLength);
		    return null;
		});
		
//		Runnable newRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				selectTextInUIThread(anEditor, anOffset, aLength);
//			}
//			
//		};
//		Thread newThread = new Thread(newRunnable) ;
//		newThread.start();
		
	}
	public static void selectTextInUIThread(ITextEditor anEditor, int anOffset, int aLength) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				anEditor.selectAndReveal(anOffset, aLength);
			}
		});
	}
	static IProgressMonitor nullProgressMonitor = new NullProgressMonitor();
	public static void saveTextInUIThread(ITextEditor anEditor) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				anEditor.doSave(nullProgressMonitor);
			}
		});
	}
	public static void openEditorInUIThread(IFile aFile) {
		
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				openAndRegisterEditor(aFile);
			}
		});
	}

	
	public static IEditorPart openAndRegisterEditor(IFile file) {
		
		try {
		IWorkbenchPage page = getIPage();
//		HashMap map = new HashMap();
//		   map.put(IMarker.LINE_NUMBER, new Integer(5));
//		   map.put(IWorkbenchPage.EDITOR_ID_ATTR, 
//		      "org.eclipse.ui.DefaultTextEditor");
//		   IMarker marker = file.createMarker(IMarker.TEXT);
//		   marker.setAttributes(map);
//		   page.openEditor(marker); //2.1 API
			IEditorPart retVal = IDE.openEditor(page, file); //3.0 API
			page.activate(retVal);
			if (retVal instanceof ITextEditor) {
				fileToEditor.put(file, (ITextEditor) retVal);	
			}
			
			return retVal;
//		   marker.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static IWorkbenchPage getIPage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public static IEditorPart getActiveEditor() {

		IEditorPart editor = null;

		// find the active part
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IPartService partService = window.getPartService();
			IWorkbenchPart part = partService.getActivePart();

			// Is the part an editor?
			if (part instanceof IEditorPart) {

				editor = (IEditorPart) part;
			}
		}

		return editor;
	}
	
	public static ISourceViewer getSourceViewer(IEditorPart editor) {
		if (editor == null) {
			return null;
		}

		ISourceViewer viewer = (ISourceViewer) editor
				.getAdapter(ITextOperationTarget.class);

		return viewer;
	}
	


	public static StyledText getStyledText(IEditorPart editor) {
		if (editor == null) {
			return null;
		}

		StyledText styledText = null;

		ISourceViewer viewer = (ISourceViewer) editor
				.getAdapter(ITextOperationTarget.class);
		if (viewer != null) {
			styledText = viewer.getTextWidget();
		}

		return styledText;
	}

	public static IDocument getDocument(IEditorPart editor) {
		if (editor == null) {
			return null;
		}

		IDocument doc = null;

		ISourceViewer viewer = (ISourceViewer) editor
				.getAdapter(ITextOperationTarget.class);

		if (viewer != null) {
			doc = viewer.getDocument();
		}

		return doc;
	}

	public static ISourceViewerExtension3 getSourceViewerExtension3(
			IEditorPart editor) {
		if (editor == null) {
			return null;
		}

		ISourceViewer viewer = (ISourceViewer) editor
				.getAdapter(ITextOperationTarget.class);
		if (viewer instanceof ISourceViewerExtension3) {
			return (ISourceViewerExtension3) viewer;
		}

		return null;
	}

	public static ISourceViewerExtension4 getSourceViewerExtension4(
			IEditorPart editor) {
		if (editor == null) {
			return null;
		}

		ISourceViewer viewer = (ISourceViewer) editor
				.getAdapter(ITextOperationTarget.class);
		if (viewer instanceof ISourceViewerExtension4) {
			return (ISourceViewerExtension4) viewer;
		}

		return null;
	}

	// public static List<Command> getFillInCommands()
	// {
	// if (mFillInCommands.size()>0)
	// }

	public static Command getStyledTextCommand(int styledTextCode) {
		if (mFillInCommands.size() == 0)
			createStyledTextCommands();

		return mFillInCommands.get(styledTextCode);
	}
	   public static final String ECLIPSE_LINE_UP = "eventLogger.styledTextCommand.LINE_UP";
	    public static final String ECLIPSE_LINE_DOWN = "eventLogger.styledTextCommand.LINE_DOWN";
	    public static final String ECLIPSE_COLUMN_NEXT = "eventLogger.styledTextCommand.COLUMN_NEXT";
	    public static final String ECLIPSE_COLUMN_PREVIOUS = "eventLogger.styledTextCommand.COLUMN_PREVIOUS";
	    public static final String ECLIPSE_GOTO_START = "org.eclipse.ui.edit.text.goto.lineStart";
	    public static final String ECLIPSE_GOTO_END = "org.eclipse.ui.edit.text.goto.lineEnd";
	    public static final String ECLIPSE_SELECT_Start = "org.eclipse.ui.edit.text.select.lineStart";
	    public static final String ECLIPSE_SELECT_END = "org.eclipse.ui.edit.text.select.lineEnd";
	    public static final String ECLIPSE_SELECT_UP = "eventLogger.styledTextCommand.SELECT_LINE_UP";
	    public static final String ECLIPSE_SELECT_DOWN = "eventLogger.styledTextCommand.SELECT_LINE_DOWN";
	    public static final String ECLIPSE_SELECT_COLUMN_NEXT = "eventLogger.styledTextCommand.SELECT_COLUMN_NEXT";
	    public static final String ECLIPSE_SELECT_ECLIPSE_COLUMN_PREVIOUS = "eventLogger.styledTextCommand.SELECT_COLUMN_PREVIOUS";
	    public static final String ECLIPSE_SELECT_ALL = "org.eclipse.ui.edit.selectAll";

	public static void createStyledTextCommands() {
		if (mFillInCommands.size() > 0)
			return;
		
		// create styled text commands and fill in map
		String[] styledTextConstantStrings = { "DELETE_PREVIOUS", "LINE_DOWN",
				"SELECT_WORD_PREVIOUS", "WORD_PREVIOUS",
				"SELECT_COLUMN_PREVIOUS", "COLUMN_PREVIOUS",
				"SELECT_WORD_NEXT", "WORD_NEXT", "SELECT_COLUMN_NEXT",
				"COLUMN_NEXT", "LINE_UP", "SELECT_TEXT_START", "TEXT_START",
				"SELECT_LINE_START", "LINE_START", "SELECT_TEXT_END",
				"TEXT_END", "SELECT_LINE_END", "LINE_END", "SELECT_WINDOW_END",
				"WINDOW_END", "SELECT_PAGE_DOWN", "PAGE_DOWN",
				"SELECT_WINDOW_START", "WINDOW_START", "SELECT_PAGE_UP",
				"PAGE_UP", "DELETE_WORD_NEXT", "CUT", "DELETE_NEXT", "COPY",
				"PASTE", "TOGGLE_OVERWRITE", "SELECT_LINE_DOWN",
				"SELECT_LINE_UP",
				"SELECT_ALL"
				};

		ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
				.getAdapter(ICommandService.class);
		Category cat = cs.getCategory(FillInPrefix);
		cat.define("Styled Text Commands", "");
		for (int i = 0; i < styledTextConstantStrings.length; i++) {
			int constant = 0;
			boolean success = false;
			try {
				constant = ST.class.getField(styledTextConstantStrings[i])
						.getInt(null);
				success = true;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!success) {
				continue;
			}

			// int constant = styledTextConstants[i];
			Command newCommand = cs.getCommand(FillInPrefix + "."
					+ styledTextConstantStrings[i]);
			stringToCommand.put(styledTextConstantStrings[i], newCommand);
			newCommand.define(getNameForStyledTextConstant(constant), "", cat);
			newCommand.setHandler(new StyledTextHandler(constant));
			mFillInCommands.put(constant, newCommand);
		}
	}

//	public static EHICommand getCommandForKeyEvent(Event event) {
//		return (EHICommand) EHUtilities.getCommandForKeyEvent(event);
//	}
//	
	


	public static String getNameForStyledTextConstant(int constant) {
		switch (constant) {
		case ST.DELETE_PREVIOUS:
			return "Delete previous character";
		case ST.LINE_DOWN:
			return "Move cursor down";
		case ST.SELECT_WORD_PREVIOUS:
			return "Select previous word";
		case ST.WORD_PREVIOUS:
			return "Move cursor to previous word";
		case ST.SELECT_COLUMN_PREVIOUS:
			return "Select previous character";
		case ST.COLUMN_PREVIOUS:
			return "Move cursor left";
		case ST.SELECT_WORD_NEXT:
			return "Select next word";
		case ST.WORD_NEXT:
			return "Move cursor to next word";
		case ST.SELECT_COLUMN_NEXT:
			return "Select next character";
		case ST.COLUMN_NEXT:
			return "Move cursor right";
		case ST.LINE_UP:
			return "Move cursor up";
		case ST.SELECT_TEXT_START:
			return "Select to start of document";
		case ST.TEXT_START:
			return "Move cursor to start of document";
		case ST.SELECT_LINE_START:
			return "Select to line start";
		case ST.LINE_START:
			return "Move cursor to line start";
		case ST.SELECT_TEXT_END:
			return "Select to end of document";
		case ST.TEXT_END:
			return "Move cursor to end of document";
		case ST.SELECT_LINE_END:
			return "Select to end of line";
		case ST.LINE_END:
			return "Move cursor to end of line";
		case ST.SELECT_WINDOW_END:
			return "Select to window end";
		case ST.WINDOW_END:
			return "Move cursor to window end";
		case ST.SELECT_PAGE_DOWN:
			return "Select page down";
		case ST.PAGE_DOWN:
			return "Move cursor down a page";
		case ST.SELECT_WINDOW_START:
			return "Select to window start";
		case ST.WINDOW_START:
			return "Move cursor to window start";
		case ST.SELECT_PAGE_UP:
			return "Select page up";
		case ST.PAGE_UP:
			return "Move cursor up a page";
		case ST.DELETE_WORD_NEXT:
			return "Delete next word";
		case ST.CUT:
			return "Cut";
		case ST.DELETE_NEXT:
			return "Delete next character";
		case ST.COPY:
			return "Copy";
		case ST.PASTE:
			return "Paste";
		case ST.TOGGLE_OVERWRITE:
			return "Toggle overwrite mode";
		case ST.SELECT_LINE_DOWN:
			return "Select line down";
		case ST.SELECT_LINE_UP:
			return "Select line up";
		}

		return "Unknown constant";
	}
//
	static class StyledTextHandler implements IHandler {
		private int mStyledTextAction;

		public StyledTextHandler(int styledTextConstant) {
			mStyledTextAction = styledTextConstant;
		}

		public void addHandlerListener(IHandlerListener handlerListener) {

		}

		public void dispose() {

		}

		public Object execute(ExecutionEvent event) throws ExecutionException {
			IEditorPart target = getActiveEditor();
			EHUtilities.getStyledText(target).invokeAction(mStyledTextAction);
			return null;
		}

		public boolean isEnabled() {
			return true;
		}

		public boolean isHandled() {
			return true;
		}

		public void removeHandlerListener(IHandlerListener handlerListener) {
		}

	}
//
	public static boolean isEditCategory(String categoryID) {
		System.out.println("category test:" + categoryID);
		return mEditCategories.contains(categoryID);
	}

	public static IFindReplaceTarget getFindReplaceTarget(IEditorPart editor) {
		IFindReplaceTarget target = (IFindReplaceTarget) editor
				.getAdapter(IFindReplaceTarget.class);
		return target;
	}

	public static String persistCommand(Map<String, String> attrs,
			Map<String, String> data, EHICommand command) {
		StringBuffer buf = new StringBuffer();

		// Opening Tag
		buf.append("  <" + command.getCommandTag());

		if (attrs == null) {
			attrs = new HashMap<String, String>();
		}

		// Add common attributes
		attrs.put("__id", Integer.toString(command.getCommandIndex()));
		attrs.put("_type", command.getCommandType());
		attrs.put("timestamp", Long.toString(command.getTimestamp()));
		if (command.getRepeatCount() > 1) {
			attrs.put("timestamp2", Long.toString(command.getTimestamp2()));
			attrs.put("repeat", Integer.toString(command.getRepeatCount()));
		}

		if (command.areTopBottomLinesRecorded()) {
			attrs.put("topLine", Integer.toString(command.getTopLineNumber()));
			attrs.put("bottomLine",
					Integer.toString(command.getBottomLineNumber()));
		}

		// write the attributes to the buffer
		TreeSet<String> sortedAttrKeys = new TreeSet<String>(attrs.keySet());
		for (String attrKey : sortedAttrKeys) {
			String attrValue = attrs.get(attrKey);
			buf.append(" " + attrKey + "=\"" + attrValue + "\"");
		}

		// if there's no data
		if (data == null || data.size() == 0) {
			buf.append(" />" + NewLine);
		} else {
			buf.append(">" + NewLine);

			// write each data element
			for (String dataKey : data.keySet()) {
				String dataValue = data.get(dataKey);

				buf.append("    <" + dataKey + ">");
				String adjustedDataValue = dataValue.replace("]]>",
						"]]]]><![CDATA[>");
				buf.append("<![CDATA[" + adjustedDataValue + "]]>");
				buf.append("</" + dataKey + ">" + NewLine);
			}

			// close the tag
			buf.append("  </" + command.getCommandTag() + ">" + NewLine);
		}

		return buf.toString();
	}
//
	public static void persistCommand(Document doc, Element commandElement,
			String type, Map<String, String> attrs, Map<String, String> data,
			EHICommand command) {
		commandElement.setAttribute(EHEventRecorder.XML_ID_Tag,
				Integer.toString(command.getCommandIndex()));
		commandElement.setAttribute(EHEventRecorder.XML_CommandType_ATTR, type);
		commandElement.setAttribute("timestamp",
				Long.toString(command.getTimestamp()));
		if (command.getRepeatCount() > 1) {
			commandElement.setAttribute("timestamp2",
					Long.toString(command.getTimestamp2()));
			commandElement.setAttribute("repeat",
					Integer.toString(command.getRepeatCount()));
		}

		if (attrs != null) {
			for (String attr : attrs.keySet()) {
				commandElement.setAttribute(attr, attrs.get(attr));
			}
		}

		if (data != null) {
			for (String dataTag : data.keySet()) {
				String dataContent = data.get(dataTag);
				Element dataChild = doc.createElement(dataTag);
				CDATASection cdataContent = doc.createCDATASection(dataContent);
				// child.setTextContent(dataContent);
				commandElement.appendChild(dataChild);
				dataChild.appendChild(cdataContent);
			}
		}
	}
//
	public static void getCommandData(Element commandElement,
			Set<String> attrKeys, Set<String> dataKeys,
			Map<String, String> attrMap, Map<String, String> dataMap) {
		if (attrMap != null) {
			for (String attrName : attrKeys) {
				String attrValue = commandElement.getAttribute(attrName);
				attrMap.put(attrName, attrValue);
			}
		}

		if (dataMap != null) {
			NodeList children = commandElement.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				String tagName = children.item(i).getNodeName();
				if (dataKeys.contains(tagName)) {
					String data = children.item(i).getTextContent();
					dataMap.put(tagName, data);
				}
			}
		}
	}

	public static IDocument getIDocumentForEditor(IEditorPart editor) {
		TextFileDocumentProvider textFileDocumentProvider = new TextFileDocumentProvider();
		try {
			textFileDocumentProvider.connect(editor.getEditorInput());
		} catch (CoreException coreException) {
			coreException.printStackTrace();
		}

		IDocument document = textFileDocumentProvider.getDocument(editor
				.getEditorInput());
		textFileDocumentProvider.disconnect(editor.getEditorInput());
		return document;
	}

	public static boolean isSupportCategory(String categoryID) {
		if (categoryID.equals(EHEventRecorder.MacroCommandCategoryID))
			return true;
		return false;
	}

	public static IPreferenceStore getMainPreferenceStore() {
		return EHActivator.getDefault().getPreferenceStore();
	}

	public static String getFilePathFromEditor(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			try {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				return fileInput.getFile().getLocation().toOSString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	
	// ---------------------------------- new command--------------------------
	/*
	 * No one reall references it, but just in case, let us get instanceof EHEventRecorder
	 */
	public static Font getFont() {
		IEditorPart editor = EHEventRecorder.getInstance().getEditor();
		if (editor == null) {
			return null;
		}
		
		StyledText styledText = getStyledText(editor);
		if (styledText == null) {
			return null;
		}
		
		return styledText.getFont();
	}
	public static EHICommand getCommandForKeyEvent(Event event) {
		final int key = (SWT.KEY_MASK & event.keyCode);
		if ((key != 0 && !Character.isISOControl(event.character))) {
			return new InsertStringCommand(new String(
					new char[] { event.character }));
		}

		boolean isMod1 = ((event.stateMask & SWT.MOD1) > 0); // ctrl
		boolean isMod2 = ((event.stateMask & SWT.MOD2) > 0); // shift

		switch (key) {
		case SWT.BS:
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(
						ST.DELETE_WORD_PREVIOUS).getId());
			return new EclipseCommand(getStyledTextCommand(ST.DELETE_PREVIOUS)
					.getId());
		case SWT.ARROW_DOWN:
			if (!isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_LINE_DOWN).getId());
			return new EclipseCommand(getStyledTextCommand(ST.LINE_DOWN)
					.getId());
		case SWT.ARROW_LEFT:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_WORD_PREVIOUS).getId());
			else if (isMod1)
				return new EclipseCommand(
						getStyledTextCommand(ST.WORD_PREVIOUS).getId());
			else if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_COLUMN_PREVIOUS).getId());
			return new EclipseCommand(getStyledTextCommand(ST.COLUMN_PREVIOUS)
					.getId());
		case SWT.ARROW_RIGHT:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_WORD_NEXT).getId());
			else if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.WORD_NEXT)
						.getId());
			else if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_COLUMN_NEXT).getId());
			return new EclipseCommand(getStyledTextCommand(ST.COLUMN_NEXT)
					.getId());
		case SWT.ARROW_UP:
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_LINE_UP).getId());
			return new EclipseCommand(getStyledTextCommand(ST.LINE_UP).getId());
		case SWT.HOME:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_TEXT_START).getId());
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.TEXT_START)
						.getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_LINE_START).getId());
			return new EclipseCommand(getStyledTextCommand(ST.LINE_START)
					.getId());
		case SWT.END:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_TEXT_END).getId());
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.TEXT_END)
						.getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_LINE_END).getId());
			return new EclipseCommand(getStyledTextCommand(ST.LINE_END).getId());
		case SWT.PAGE_DOWN:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_WINDOW_END).getId());
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.WINDOW_END)
						.getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_PAGE_DOWN).getId());
			return new EclipseCommand(getStyledTextCommand(ST.PAGE_DOWN)
					.getId());
		case SWT.PAGE_UP:
			if (isMod1 && isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_WINDOW_START).getId());
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.WINDOW_START)
						.getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(
						ST.SELECT_PAGE_UP).getId());
			return new EclipseCommand(getStyledTextCommand(ST.PAGE_UP).getId());
		case SWT.DEL:
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(
						ST.DELETE_WORD_NEXT).getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(ST.CUT).getId());
			return new EclipseCommand(getStyledTextCommand(ST.DELETE_NEXT)
					.getId());
		case SWT.INSERT:
			if (isMod1)
				return new EclipseCommand(getStyledTextCommand(ST.COPY).getId());
			if (isMod2)
				return new EclipseCommand(getStyledTextCommand(ST.PASTE)
						.getId());
			return new EclipseCommand(getStyledTextCommand(ST.TOGGLE_OVERWRITE)
					.getId());
		}

		if (key == SWT.CR || key == SWT.LF) {
			// return new EclipseCommand("org.eclipse.ui.edit.text.smartEnter");
			// try
			// {
			// String
			// delimiter=Utilities.getStyledText(Utilities.getActiveEditor()).getLineDelimiter();
			// return new InsertStringCommand(delimiter);
			// }
			// catch (Exception e)
			{
				return new InsertStringCommand(new String(
						new char[] { (char) key }));
			}

		}

		if (key == SWT.TAB) {
			return new InsertStringCommand(
					new String(new char[] { (char) key }));
		}

		// skip if this doesn't seem to correspond to anything we understand
		return null;
	}

	
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return currentWorkbenchWindow;
	}


	public static void setCurrentWorkbenchWindow(IWorkbenchWindow currentWorkbenchWindow) {
		EHUtilities.currentWorkbenchWindow = currentWorkbenchWindow;
	}
	public static Display getDisplay() {
		return myDisplay;
	}
	public static void setDisplay(Display myDisplay) {
		EHUtilities.myDisplay = myDisplay;
	}
	public static ICommandService getCommandService() {
		return myService;
	}
	public static void setCommandService(ICommandService myService) {
		EHUtilities.myService = myService;
	}
	public static IEditorPart getCurrentEditorPart() {
		return currentEditorPart;
	}

	public static void setCurrentEditorPart(IEditorPart currentEditorPart) {
		EHUtilities.currentEditorPart = currentEditorPart;
	}
	public static ILaunchConfiguration createLaunchConfiguration (String aConfigName, String aProjectName, String aMainClassName) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
//		ILaunchConfigurationType configurationType =     manager.getLaunchConfigurationType("org.eclipse.cdt.launch.applicationLaunchType");
		ILaunchConfigurationType configurationType =     manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

		try {
//			ILaunchConfiguration[] aLaunchConfiguration = manager.getLaunchConfigurations(type);
			ILaunchConfigurationWorkingCopy workingCopy = configurationType.newInstance(null, aConfigName);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, aProjectName);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, aMainClassName);
			return workingCopy.doSave();			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static final Method clickButtonMethod() {
		if (clickButtonMethod == null) {
			 try 
			    {                           
//			        Class<?>buttonClass = button.getClass().getSuperclass();

				 	clickButtonMethod = Button.class.getDeclaredMethod("click");
				 	clickButtonMethod.setAccessible(true);
//			        method.invoke(button);          
			    }
			 catch (Exception e) {
				 e.printStackTrace();
			 }
		}
		return clickButtonMethod;
	}
	/**
     * Prunes out all naming occurrences of anonymous inner types, since these types have no names
     * and cannot be derived visiting an AST (no positive type name matching while visiting ASTs)
     * @param type
     * @return the compiled type name from the given {@link IType} with all occurrences of anonymous inner types removed
     * @since 3.4
     */
	public static String pruneAnonymous(IType type) {
    	StringBuffer buffer = new StringBuffer();
    	IJavaElement parent = type;
    	while(parent != null) {
    		if(parent.getElementType() == IJavaElement.TYPE){
    			IType atype = (IType) parent;
    			try {
	    			if(!atype.isAnonymous()) {
	    				if(buffer.length() > 0) {
	    					buffer.insert(0, '$');
	    				}
	    				buffer.insert(0, atype.getElementName());
	    			}
    			}
    			catch(JavaModelException jme) {}
    		}
    		parent = parent.getParent();
    	}
    	return buffer.toString();
    }
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
     * Returns the package qualified name, while accounting for the fact that a source file might
     * not have a project
     * @param type the type to ensure the package qualified name is created for
     * @return the package qualified name
     * @since 3.3
     */
    public static String createQualifiedTypeName(IType type) {
    	String tname = pruneAnonymous(type);
    	try {
    		String packName = null;
    		if (type.isBinary()) {
    			packName = type.getPackageFragment().getElementName();
    		} else {
    			IPackageDeclaration[] pd = type.getCompilationUnit().getPackageDeclarations();
				if(pd.length > 0) {
					packName = pd[0].getElementName();
				}
    		}
			if(packName != null && !packName.equals(EMPTY_STRING)) {
				tname =  packName+"."+tname; //$NON-NLS-1$
			}
    	} 
    	catch (JavaModelException e) {}
    	return tname;
    }
    /**
	 * Deletes the given breakpoint using the operation history, which allows to undo the deletion.
	 * 
	 * @param breakpoint the breakpoint to delete
	 * @param part a workbench part, or <code>null</code> if unknown
	 * @param progressMonitor the progress monitor
	 * @throws CoreException if the deletion fails
	 */
	public static void deleteBreakpoint(IJavaBreakpoint breakpoint, IWorkbenchPart part, IProgressMonitor monitor) throws CoreException {
		final Shell shell= part != null ? part.getSite().getShell() : null;
		final boolean[] result= new boolean[] { true };

		final IEclipsePreferences prefs= InstanceScope.INSTANCE.getNode(JDIDebugUIPlugin.getUniqueIdentifier());
		boolean prompt= prefs.getBoolean(IJDIPreferencesConstants.PREF_PROMPT_DELETE_CONDITIONAL_BREAKPOINT, true);
		if (prompt && breakpoint instanceof IJavaLineBreakpoint && ((IJavaLineBreakpoint)breakpoint).getCondition() != null) {
			Display display= shell != null && !shell.isDisposed() ? shell.getDisplay() : PlatformUI.getWorkbench().getDisplay();
			if (!display.isDisposed()) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialogWithToggle dialog= MessageDialogWithToggle.openOkCancelConfirm(shell, ActionMessages.ToggleBreakpointAdapter_confirmDeleteTitle,
								ActionMessages.ToggleBreakpointAdapter_confirmDeleteMessage, ActionMessages.ToggleBreakpointAdapter_confirmDeleteShowAgain, false,
								null, null);
						if (dialog.getToggleState()) {
							prefs.putBoolean(IJDIPreferencesConstants.PREF_PROMPT_DELETE_CONDITIONAL_BREAKPOINT, false);
						}
						result[0]= dialog.getReturnCode() == IDialogConstants.OK_ID;
					}
				});
			}
		}
		
		if (result[0]) {
			DebugUITools.deleteBreakpoints(new IBreakpoint[] { breakpoint }, shell, monitor);
		}
	}
	
	public static void lineBreakpointToggle(IType aSourceType, ITextEditor aTextEditor, int aLineNumber) {
		String tname = createQualifiedTypeName(aSourceType);
//    	IJavaProject project = lastSourceType.getJavaProject();
//    	if (locator == null || (project != null && !project.isOnClasspath(type))) {
//    		tname = createQualifiedTypeName(type);
//    	} else {
//    		tname = locator.getFullyQualifiedTypeName();
//    	}
		
    	IResource resource = BreakpointUtils.getBreakpointResource(aSourceType);
		IJavaLineBreakpoint existingBreakpoint;
		try {
			existingBreakpoint = JDIDebugModel.lineBreakpointExists(resource, tname, aLineNumber);
		
		if (existingBreakpoint != null) {
			deleteBreakpoint(existingBreakpoint, aTextEditor, null);
			return ;
		}
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Object> attributes =new HashMap<String, Object>(10);
		IDocumentProvider documentProvider = aTextEditor.getDocumentProvider();
		if (documentProvider == null) {
		    return;
		}
		IDocument document = documentProvider.getDocument(aTextEditor.getEditorInput());
		int charstart = -1, charend = -1;
		try {
			IRegion line = document.getLineInformation(aLineNumber - 1);
			charstart = line.getOffset();
			charend = charstart + line.getLength();
		} 	
		catch (BadLocationException ble) {JDIDebugUIPlugin.log(ble);}
		BreakpointUtils.addJavaBreakpointAttributes(attributes, aSourceType);
		try {
			IJavaLineBreakpoint breakpoint = JDIDebugModel.createLineBreakpoint(resource, tname, aLineNumber, charstart, charend, 0, true, attributes);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void invokeClickInSeparateThread (Button aButton) {
		executor().submit(() -> {
			invokeClickInUIThread(aButton);
		});
	}
	public static void invokeClickInUIThread (Button aButton) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					clickButtonMethod().invoke(aButton);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static void invokeRefactoringInSeparateThread (ICompilationUnit aCompilationUnit, String aNewName) {
		executor().submit(() -> {
			invokeRefactoringInUIThread(aCompilationUnit, aNewName);
		});
	}
	public static void invokeRefactoringInUIThread (ICompilationUnit aCompilationUnit, String aNewName) {
		if (getDisplay() == null) {
			return;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					invokeRefactoringInUIThread(aCompilationUnit, aNewName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void refactor(ICompilationUnit aCompilationUnit, String aNewName) {
		RefactoringContribution contribution =
			    RefactoringCore.getRefactoringContribution(IJavaRefactorings .RENAME_COMPILATION_UNIT);
		RenameJavaElementDescriptor descriptor =
			    (RenameJavaElementDescriptor) contribution.createDescriptor();
			descriptor.setProject(aCompilationUnit.getResource().getProject().getName( ));
			descriptor.setNewName(aNewName); // new name for a Class
			descriptor.setJavaElement(aCompilationUnit);

			RefactoringStatus status = new RefactoringStatus();
			try {
			    Refactoring refactoring = descriptor.createRefactoring(status);

			    IProgressMonitor monitor = new NullProgressMonitor();
			    refactoring.checkInitialConditions(monitor);
			    refactoring.checkFinalConditions(monitor);
			    Change change = refactoring.createChange(monitor);
			    change.perform(monitor);

			} catch (CoreException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
	}
	

}
//ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
//
//ILaunchConfigurationType type =     manager.getLaunchConfigurationType("org.eclipse.cdt.launch.applicationLaunchType");
//ILaunchConfiguration[] lcs = manager.getLaunchConfigurations(type);
//
//     for (ILaunchConfiguration iLaunchConfiguration : lcs) {
//         if (iLaunchConfiguration.getName().equals("Test PThread")) {
//             ILaunchConfigurationWorkingCopy t = iLaunchConfiguration.getWorkingCopy();
//             ILaunchConfiguration config = t.doSave();
//             if (config != null) {
//                 // config.launch(ILaunchManager.RUN_MODE, null);
//                 DebugUITools.launch(config, ILaunchManager.DEBUG_MODE);
//             }
//         }
//     }
//public static void create(IProject project) {
//    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
//    ILaunchConfigurationType launchType = manager
//            .getLaunchConfigurationType(JUNIT_LAUNCH_IDENTIFIER);
//    try {
//        ILaunchConfigurationWorkingCopy workingCopy = launchType.newInstance(null, project.getName());
//        List<IResource> resources = new ArrayList<IResource>();
//        resources.add(project);
//        IResource[] resourcesArray = toArray(resources);
//        workingCopy.setMappedResources(resources.toArray(resourcesArray) );
//        workingCopy.setAttribute("org.eclipse.jdt.junit.CONTAINER", project.getName().replace("#", "\\#"));
//        workingCopy.setAttribute("org.eclipse.jdt.junit.KEEPRUNNING_ATTR", false);
//        workingCopy.setAttribute("org.eclipse.jdt.junit.TESTNAME", "");
//        workingCopy.setAttribute("org.eclipse.jdt.junit.TEST_KIND", "org.eclipse.jdt.junit.loader.junit4");
//        workingCopy.setAttribute("org.eclipse.jdt.launching.MAIN_TYPE", "");
//        workingCopy.setAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
//        workingCopy.setAttribute("org.eclipse.jdt.launching.VM_ARGUMENTS", "-Xms128m -Xmx512m -DSYS_DRIVE=${env_var:SYS_DRIVE} " +
//                "-DAPPL_DRIVE=${env_var:APPL_DRIVE} -DDATA1_DRIVE=${env_var:DATA1_DRIVE} -DSYS_DIR=${env_var:SYS_DIR} " +
//                "-DEXT1_DRIVE=F: -DTESTDATA_ROOT=${workspace_loc:trunk#IS+LVIS/testdata}");
//        workingCopy.doSave();
//    } catch (CoreException e) {
//        log.log(Level.WARNING,
//                "Unable to create a new launch configuration.", e);
//    }
//}
