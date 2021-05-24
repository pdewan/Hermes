package fluorite.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import org.eclipse.ltk.core.refactoring.history.IRefactoringHistoryService;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.FileOpenCommand;
import fluorite.util.EHUtilities;
import hermes.proxy.Diff_Match_Patch_Proxy;
import name.fraser.neil.plaintext.diff_match_patch;
//import replayer.RefactorRecord;
import fluorite.model.DiffBasedFileSnapshotManager;

public class DiffBasedFileOpenCommand extends FileOpenCommand {
	
	public DiffBasedFileOpenCommand(){}
	
	public DiffBasedFileOpenCommand(IEditorPart editor) {
		super(editor);
		if (record()) {
			addDiff(editor);
		}
//		updateProjectStructure();
	}
	
	private String diff;

	private void addDiff(IEditorPart editor) {
		if (editor == null) {
			return;
		}
		String filePath = getFilePath();
		IDocument aDocument = EHUtilities.getDocument(editor);
		if (aDocument == null) {
			return;
		}
//		String content = EHUtilities.getDocument(editor).get();
		String content = aDocument.get();

//		calcNumericalValues(content);
		String oldContent = DiffBasedFileSnapshotManager.getInstance().getSnapshot(filePath);
		if (oldContent == null) {
			diff = "null";
		} else {
			diff = Diff_Match_Patch_Proxy.diffString(oldContent, content);
		}
		DiffBasedFileSnapshotManager.getInstance().updateSnapshot(filePath, content);
	}
	
//	private ArrayList<RefactorRecord> refactorRecord; 
//	private ArrayList<String> newFiles;
//	private ArrayList<String> deletedFiles;
//	private final String ORIGINAL_ELEMENT = "Original element: '";
//	private final String RENAMED_ELEMENT = "Renamed element: '";
//	private final String DESTINATION_ELEMENT = "Destination element: '";
//
//
//	
//	private void updateProjectStructure(){
//		IProject currentProject = EHUtilities.getCurrentProject();
//		String projectPath = currentProject.getLocation().toString().replace("/", File.separator);
//		File sourceFolder = new File(projectPath + File.separator + "src");
//		File cloneSrc = new File(projectPath + File.separator + "Logs" + File.separator + "src");
//		File refactorHistory = new File(projectPath + File.separator + "Logs" + File.separator + "RefactorHistory.txt");
//		File newFileHistory = new File(projectPath + File.separator + "Logs" + File.separator + "NewFileHistory.txt");
//		File deleteHistory = new File(projectPath + File.separator + "Logs" + File.separator + "DeleteHistory.txt");
//		try {
//			if (!cloneSrc.exists()) {
//				cloneFolder(sourceFolder, projectPath + File.separator + "src", projectPath + File.separator + "Logs" + File.separator + "src");
//			} else {
//				ArrayList<String> files = new ArrayList<>();
//				ArrayList<String> currentFiles = new ArrayList<>();
//				recordFolderToArray(cloneSrc, files);
//				recordFolderToArray(sourceFolder, currentFiles);
//				
//				for (int i = 0; i < files.size(); i++) {
//					String filePath = files.get(i);
//					if (currentFiles.contains(filePath.replace(File.separator + "Logs", ""))) {
//						File oldFile = new File(filePath);
//						File newFile = new File(filePath.replace(File.separator + "Logs", ""));
//						if (oldFile.lastModified() < newFile.lastModified()) {
//							copyFiles(newFile, oldFile);
//						}
//						files.remove(i);
//						currentFiles.remove(filePath.replace(File.separator + "Logs", ""));
//						i--;
//					}
//				}
//
//				refactorRecord = new ArrayList<>();
//				newFiles = new ArrayList<>();
//				deletedFiles = new ArrayList<>();
//				FileWriter fw = null;
//				long lastModified = refactorHistory.lastModified();
//				
//				if (files.size() > 0 && currentFiles.size() > 0) {					
//					IRefactoringHistoryService refactoringHistoryService = RefactoringCore.getHistoryService();
//					refactoringHistoryService.connect();
//					RefactoringDescriptorProxy[] refactoringDescriptorProxies = refactoringHistoryService.getProjectHistory(EHUtilities.getCurrentProject(), null).getDescriptors();
//					refactoringHistoryService.disconnect();
//					ArrayList<RefactorRecord> reversedRecords = new ArrayList<>();
//					for (RefactoringDescriptorProxy refactoringDescriptorProxy : refactoringDescriptorProxies) {
//						if (refactoringDescriptorProxy.getTimeStamp() < lastModified) {
//							break;
//						}
//						if (refactoringDescriptorProxy.getDescription().contains("Rename")) {
//							RefactoringDescriptor refactoringDescriptor = refactoringDescriptorProxy.requestDescriptor(null);
//							String comment = refactoringDescriptor.getComment();
//							String originalName = comment.substring(comment.indexOf(ORIGINAL_ELEMENT)+ORIGINAL_ELEMENT.length());
//							originalName = projectPath+ File.separator + "Logs" + File.separator + "src"  + File.separator + originalName.substring(0,originalName.indexOf('\'')).replace(".", File.separator) + ".java";
//							String refactoredName = comment.substring(comment.indexOf(RENAMED_ELEMENT)+RENAMED_ELEMENT.length());
//							refactoredName = projectPath+ File.separator + "src" + File.separator + refactoredName.substring(0,refactoredName.indexOf('\'')).replace(".", File.separator) + ".java";
//							reversedRecords.add(new RefactorRecord(refactoringDescriptorProxy.getTimeStamp(), originalName, refactoredName));
//							files.remove(originalName);
//							currentFiles.remove(refactoredName);
//						}
//						if (refactoringDescriptorProxy.getDescription().contains("Move")) {
//							RefactoringDescriptor refactoringDescriptor = refactoringDescriptorProxy.requestDescriptor(null);
//							String comment = refactoringDescriptor.getComment();
//							String originalName = comment.substring(comment.indexOf(ORIGINAL_ELEMENT)+ORIGINAL_ELEMENT.length());
//							originalName = projectPath+File.separator + "Logs" + File.separator + "src" + File.separator + originalName.substring(0,originalName.indexOf('\'')).replace(".",File.separator).replace(File.separator + "java", ".java");
//							String destinationName = comment.substring(comment.indexOf(DESTINATION_ELEMENT)+DESTINATION_ELEMENT.length());
//							destinationName = projectPath+File.separator + "src" + File.separator + destinationName.substring(destinationName.indexOf("/src/")+"/src/".length(),destinationName.indexOf('\'')).replace(".", File.separator)+originalName.substring(originalName.lastIndexOf(File.separator));
//							reversedRecords.add(new RefactorRecord(refactoringDescriptorProxy.getTimeStamp(), originalName, destinationName));
//							files.remove(originalName);
//							currentFiles.remove(destinationName);
//						}
//					}
//					
//					for (RefactorRecord record : reversedRecords) {
//						refactorRecord.add(record);
//					}
//					
//					for (int i = 0; i < currentFiles.size(); i++){
//						BufferedReader fileReader = new BufferedReader(new FileReader(new File(currentFiles.get(i))));
//						String nextLine = fileReader.readLine();
//						String content = "";
//						while(nextLine != null){
//							content += nextLine;
//							nextLine = fileReader.readLine();
//						}
//						fileReader.close();
//						
//						String bestMatch = null;
//						double bestEqualRatio = 0;
//
//						for (int j = 0; j < files.size(); j++) {
//							fileReader = new BufferedReader(new FileReader(new File(files.get(j))));
//							nextLine = fileReader.readLine();
//							String oldContent = "";
//							while(nextLine != null){
//								oldContent += nextLine;
//								nextLine = fileReader.readLine();
//							}
//							fileReader.close();
//							LinkedList<diff_match_patch.Diff> diffs = Diff_Match_Patch_Proxy.diff(oldContent, content);
//							int equalLength = 0;
//							for(diff_match_patch.Diff diff: diffs){
//								if (diff.operation == diff_match_patch.Operation.EQUAL) {
//									equalLength += diff.text.length();
//								}
//							}
//							double equalRatio = ((double)equalLength) / oldContent.length();
//							if (equalRatio > 0.50 && equalRatio > bestEqualRatio) {
//								bestMatch = files.get(j);
//								bestEqualRatio = equalRatio;
//							}
//						}
//						
//						if (bestMatch != null) {
//							refactorRecord.add(new RefactorRecord(System.currentTimeMillis(), bestMatch, currentFiles.get(i)));
//							files.remove(bestMatch);
//							new File(bestMatch).delete();
//						} else {
//							newFiles.add(currentFiles.get(i));
//						}
//						
//						copyFiles(new File(currentFiles.get(i)), new File(currentFiles.get(i).replace(projectPath, projectPath+File.separator + "Logs")));
//						currentFiles.remove(i);
//						i--;
//					}
//				}
//				
//				if (files.size() > 0 && currentFiles.size() == 0) {
//					for (String fileName : files) {
//						deletedFiles.add(fileName);
//						new File(fileName).delete();
//					}
//				}
//				
//				if (files.size() == 0 && currentFiles.size() > 0) {
//					for (String fileName : currentFiles) {
//						newFiles.add(fileName);
//						copyFiles(new File(fileName), new File(fileName.replace(projectPath, projectPath+File.separator + "Logs")));
//					}
//				} 
//				
//				if (refactorRecord.size() != 0) {
//					if (!refactorHistory.exists()) {
//						refactorHistory.createNewFile();
//					} 
//					fw = new FileWriter(refactorHistory, true);
//					for (int i = 0; i < refactorRecord.size(); i++) {
//						fw.write((System.currentTimeMillis()+i) + " " + refactorRecord.get(i).getOriginalFileName().replace(projectPath+File.separator + "Logs" + File.separator + "src"+File.separator, "") + "----->" + refactorRecord.get(i).getRefactoredFileName().replace(projectPath+File.separator + "src" + File.separator, "") + "\r\n");
//					}
//					fw.close();
//				}
//				
//				if (newFiles.size() != 0) {
//					if (!newFileHistory.exists()) {
//						newFileHistory.createNewFile();
//					} 
//					fw = new FileWriter(newFileHistory, true);
//					for (int i = 0; i < newFiles.size(); i++) {
//						fw.write((System.currentTimeMillis()+i) + " " + newFiles.get(i) + "\r\n");
//					}
//					fw.close();
//				}
//				
//				if (deletedFiles.size() != 0) {
//					if (!deleteHistory.exists()) {
//						deleteHistory.createNewFile();
//					} 
//					fw = new FileWriter(deleteHistory, true);
//					for (int i = 0; i < deletedFiles.size(); i++) {
//						fw.write(System.currentTimeMillis() + " " + deletedFiles.get(i) + "\r\n");
//					}
//					fw.close();
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void cloneFolder(File source, String sourcePath, String destPath){
//		try {
//			for (File file : source.listFiles()) {
//				if (file.isDirectory()) {
//					new File(file.getPath().replace(sourcePath, destPath)).mkdirs();
//					cloneFolder(file, file.getPath(), file.getPath().replace(sourcePath, destPath));
//				} else {
//					copyFiles(file, new File(file.getPath().replace(sourcePath, destPath)));
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//	}
//	
//	public void copyFiles(File source, File dest) throws IOException{
//		InputStream inputStream = null;
//		OutputStream outputStream = null;
//		try {
//			if (!dest.exists()) {
//				dest.getParentFile().mkdirs();
//				dest.createNewFile();
//			}
//			inputStream = new FileInputStream(source);
//			outputStream = new FileOutputStream(dest);
//			byte[] buffer = new byte[1024];
//			int length;
//			while ((length = inputStream.read(buffer)) > 0) {
//				outputStream.write(buffer, 0, length);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (inputStream != null) {
//				inputStream.close();
//			}
//			if (outputStream != null) {
//				outputStream.close();
//			}
//		} 
//	}
//	
//	private void recordFolderToArray(File folder, ArrayList<String> files){
//		for(File file: folder.listFiles()){
//			if (file.isDirectory()) 
//				recordFolderToArray(file, files);
//			else 
//				files.add(file.getPath());
//		}
//	}
	
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = super.getDataMap();
		if (diff != null) {
			dataMap.put("diff", diff);
		} else {
			dataMap.put("diff", "null");
		}
		
		return dataMap;			
	}
	
	public String getDiff(){
		return diff;
	}
	
	public String getCommandType(){
		return "DiffBasedFileOpenCommand";
	}
}

//class RefactorRecord {
//	String originalName;
//	String refactoredName;
//	
//	public RefactorRecord(String originalName, String refactoredName){
//		this.originalName = originalName;
//		this.refactoredName = refactoredName;
//	}
//	
//	public String getOriginal(){
//		return originalName;
//	}
//	
//	public String getRefactoredName(){
//		return refactoredName;
//	}
//}
