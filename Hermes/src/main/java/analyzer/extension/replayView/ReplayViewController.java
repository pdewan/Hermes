package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.PlatformUI;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import fluorite.commands.EHICommand;
import fluorite.commands.ExportCommand;
import fluorite.model.EHEventRecorder;

public class ReplayViewController implements ReplayListener{
	private Analyzer analyzer;
	private ReplayView replayView; 
	private AReplayer2 replayer;
//	private AnotherReplayer replayer;
	
	public ReplayViewController(ReplayView view) {
		replayView = view;
//		view.addReplayListener(this);
		if (replayer == null || analyzer == null) {
			analyzer = new AnAnalyzer();
			replayer = new AReplayer2(analyzer);
//			replayer = new AnotherReplayer(analyzer);
			analyzer.addAnalyzerListener(replayer);
		}
	}

	@Override
	public void forward(String numStep, String step) {
		// TODO Auto-generated method stub
		replayer.setup();
		List<EHICommand> commands = replayer.forward(numStep, step);
		if (commands == null) {
			return;
		}
		replayView.createForwardCommandList(commands);
		replayView.updateTimeSpent(replayer.getCurrentTimeSpent());
		replayView.updateAbsTimeSpent(replayer.getCurrentTimestamp());

//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeline(replayer.timelineIndex());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
//		replayView.updateMetrics(replayer.getMetrics());
	}
	
	public void updateTimeSpent(String path, String time) {
		if (time.equals(ReplayView.WORKTIME)) {
			time = replayer.getWorkTime(path);
			replayView.updateTimeSpent(time);
		} else if (time.equals(ReplayView.WALLTIME)) {
			time = replayer.getWallTime(path);
			replayView.updateTimeSpent(time);
		} 
	}

	@Override
	public void back(String numStep, String step) {
		// TODO Auto-generated method stub
		replayer.setup();
		List<EHICommand> commands = replayer.back(numStep, step);
		if (commands == null) {
			return;
		}
		replayView.createBackCommandList(commands);
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeSpent(replayer.getCurrentTimeSpent());
		replayView.updateAbsTimeSpent(replayer.getCurrentTimestamp());
		replayView.updateTimeline(replayer.timelineIndex());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
//		replayView.updateMetrics(replayer.getMetrics());
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		replayer.reset();
		List<EHICommand> commands = new ArrayList<>();
		replayView.createBackCommandList(commands);
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeSpent(replayer.getCurrentTimeSpent());
		replayView.updateAbsTimeSpent(replayer.getCurrentTimestamp());
		replayView.updateTimeline(replayer.timelineIndex());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.removeTabItems();
		replayView.updateReplayedFile(replayer.getReplayedFile());
//		replayView.updateMetrics(replayer.getMetrics());
	}
	
	public void jumpTo(int index, String step) {
		replayer.setup();
		if (index >= 990) {
			replayView.createForwardCommandList(replayer.jumpTo(index, step));
		} else {
			replayView.createBackCommandList(replayer.jumpTo(index, step));
		}
		replayView.updateTimeSpent(replayer.getCurrentTimeSpent());
		replayView.updateAbsTimeSpent(replayer.getCurrentTimestamp());
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
//		replayView.updateMetrics(replayer.getMetrics());
	}

	public void zipCurrentProject(IProject project) {
		if (project == null) {
			EHEventRecorder.getInstance().recordCommand(new ExportCommand("null", false));
			showNotification("Failed to export current porject, open a file in the project you want to export and try again.");
			return;
		} else {
	        EHEventRecorder.getInstance().recordCommand(new ExportCommand(project.getName(), true));
		}
		File projectFolder = new File(project.getLocation().toOSString());
//		EHEventRecorder.getInstance().flushAndStopLogging(project);
        try {
        	List<String> fileNames = new ArrayList<>();
        	populateFilesList(fileNames, projectFolder);
        	String zipFileName = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + projectFolder.getName();
        	String surfix = ".zip";
        	int surfixNum = 0;
        	while (new File(zipFileName+surfix).exists()) {
    			surfixNum++;
    			surfix = "(" + surfixNum + ").zip";
			}
        	zipFileName += surfix;
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : fileNames){
                String zipPath = "";
                if (filePath.endsWith("copy.xml")) {
                	zipPath = filePath.replace(projectFolder+File.separator, "").replace(File.separator, "/").replace("copy.xml", ".xml");
                } else {
                	zipPath = filePath.replace(projectFolder+File.separator, "").replace(File.separator, "/");
                }
				ZipEntry ze = new ZipEntry(zipPath);
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
    		showNotification(project.getName() + surfix + " has been exported to Desktop");
        } catch (IOException e) {
        	e.printStackTrace();
        	try {
        		File logFile = new File(System.getProperty("user.home") + File.separator + "helper-config", "ExportError" + System.currentTimeMillis() + ".txt");
        		if (!logFile.exists()) {
                	logFile.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
				bw.write(e.getMessage());
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    		showNotification("Failed to export " + project.getName() + ", please try again.\n" + e.getMessage());
        }
//        EHEventRecorder.getInstance().continueLogging(project);
	}
	
	private void showNotification(String msg) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ToolTip	toolTip = new ToolTip(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), SWT.BALLOON
						| SWT.ICON_INFORMATION);

				if (!toolTip.isDisposed()) {
					toolTip.setMessage(msg);
					EHEventRecorder.getTrayItem().setToolTip(toolTip);
					toolTip.setVisible(true);
				}
			}
		});
	}
	
	private void populateFilesList(List<String> fileNames, File folder) throws IOException {
        File[] files = folder.listFiles();
        for(File file : files){
            if(file.isFile()) fileNames.add(file.getAbsolutePath());
            else if (file.getName().equals("Eclipse") && file.getParent().endsWith("Logs")) {
            	populateLogFolder(fileNames, file);
			} else {
				populateFilesList(fileNames, file);
			}
        }
    }
	
	protected File[] getLogFiles(File logFolder) {
		File[] logFiles = logFolder.listFiles(File::isDirectory);
		
		if (logFiles != null && logFiles.length > 0) {
			logFiles = logFiles[0].listFiles((file)->{
				if(file.getName().contains("copy.xml")) {
					file.delete();
					return false;
				}
				return file.getName().startsWith("Log") && file.getName().endsWith(".xml");});
		} else {
			logFiles = logFolder.listFiles((file)->{
				if(file.getName().contains("copy.xml")) {
					file.delete();
					return false;
				}
				return file.getName().startsWith("Log") && file.getName().endsWith(".xml");});
		}
		if (logFiles == null || logFiles.length == 0) {
			return null;
		}
		Arrays.sort(logFiles);
		File lastLogFile = new File(logFiles[logFiles.length-1].getPath());
		if (lastLogFile.exists()) {
			try {
				if (!lastLogFile.renameTo(lastLogFile)) {
					File copy = new File(lastLogFile.getPath().substring(0,lastLogFile.getPath().length()-4)+"copy.xml");
					copyFiles(lastLogFile, copy);
					logFiles[logFiles.length-1] = copy;
				};
			} catch (Exception e) {
				try {
					File copy = new File(lastLogFile.getPath().substring(0,lastLogFile.getPath().length()-4)+"copy.xml");
					copyFiles(lastLogFile, copy);
					logFiles[logFiles.length-1] = copy;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		refineLogFiles(logFiles);
		return logFiles;
	}
	
	public static final String XML_FILE_ENDING = "\r\n</Events>"; 
	
	public void refineLogFiles(File[] logFiles){
		try {
			for (File file : logFiles) {
				File lckFile = new File(file.getPath()+".lck");
				if (lckFile.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String lastLine = null;
					String currentLine = null;
					while((currentLine = reader.readLine()) != null) {
						lastLine = currentLine;
					}
					if (lastLine != null && !lastLine.endsWith("</Events>")) {
						BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
						writer.write(XML_FILE_ENDING);
						writer.close();
					}	
					reader.close();
					lckFile.delete();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
	}
	
	private void populateLogFolder(List<String> fileNames, File folder) throws IOException {
        File[] files = getLogFiles(folder);
        for (File file : files) {
			fileNames.add(file.getAbsolutePath());
		}
    }
	
	public void copyFiles(File source, File dest) throws IOException{
		InputStream is = null;
		OutputStream os = null;
		try {
			if (!dest.exists()) {
				dest.getParentFile().mkdirs();
				dest.createNewFile();
			} else {
				dest.delete();
				dest.createNewFile();
			}
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			buffer = "</Events>".getBytes();
			os.write(buffer, 0, buffer.length);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		} 
	}

	public void annotate(String annotation) {
		replayer.annotate(annotation);
	}
}
