package fluorite.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.ISourceViewerExtension4;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Event;
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
import org.eclipse.ui.ide.IDE;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import edu.cmu.scs.fluorite.util.Utilities;
import fluorite.commands.EclipseCommand;
import fluorite.commands.EHICommand;
import fluorite.commands.InsertStringCommand;
import fluorite.model.EHEventRecorder;
//import fluorite.plugin.Activator;
import fluorite.plugin.EHActivator;
/*
 * This is are not being referred to, instead the Flourite Utilites class is referenced
 */
public class EHUtilities /*extends Utilities*/{
	private static Map<Integer, Command> mFillInCommands = new HashMap<Integer, Command>();
	public static final String FillInPrefix = "eventLogger.styledTextCommand";

	public static String NewLine = System.getProperty("line.separator");

	private static Set<String> mEditCategories;

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
	public static void createProjectFromLocation (String aProjectName, String aLocation) {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();


		IProject project = root.getProject(aProjectName);
		    IWorkspace w = ResourcesPlugin.getWorkspace();
		    IProjectDescription desc=w.newProjectDescription(project.getName()); 
		    String projectLocation= aLocation;
		    IPath path1=new Path(projectLocation+"/"+aProjectName);
		    desc.setLocation(path1); 
		    try {
				project.create(desc, progressMonitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    try {
				project.open(progressMonitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static void createProjectFromFolder (String aProjectName, String aFolderName) {
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
		    
	}
	public static IFile getIFile(IProject aProject, String aFileName) {
		return aProject.getFile(aFileName);
	}
	
	public static void openEditor(IFile file) {
		try {
		IWorkbenchPage page = getIPage();
//		HashMap map = new HashMap();
//		   map.put(IMarker.LINE_NUMBER, new Integer(5));
//		   map.put(IWorkbenchPage.EDITOR_ID_ATTR, 
//		      "org.eclipse.ui.DefaultTextEditor");
//		   IMarker marker = file.createMarker(IMarker.TEXT);
//		   marker.setAttributes(map);
//		   page.openEditor(marker); //2.1 API
		   IDE.openEditor(page, file); //3.0 API
//		   marker.delete();
		} catch (Exception e) {
			e.printStackTrace();
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
				"SELECT_LINE_UP" };

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
}
