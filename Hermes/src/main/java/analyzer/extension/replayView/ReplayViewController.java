package analyzer.extension.replayView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
	private AReplayer replayer;
//	private AnotherReplayer replayer;
	
	public ReplayViewController(ReplayView view) {
		replayView = view;
//		view.addReplayListener(this);
		if (replayer == null || analyzer == null) {
			analyzer = new AnAnalyzer();
			replayer = new AReplayer(analyzer);
//			replayer = new AnotherReplayer(analyzer);
			analyzer.addAnalyzerListener(replayer);
		}
	}

	@Override
	public void forward(String numStep, String step) {
		// TODO Auto-generated method stub
		replayer.setup();
		List<EHICommand> commands = replayer.forward(numStep, step);
		replayer.cleanup();
		if (commands == null) {
			return;
		}
		replayView.createForwardCommandList(commands);
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeline(replayer.timelineIndex());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
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
		replayer.cleanup();
		if (commands == null) {
			return;
		}
		replayView.createBackCommandList(commands);
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
		replayView.updateTimeline(replayer.timelineIndex());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
	}
	
	public void jumpTo(int index, String step) {
		replayer.setup();
		if (index >= 990) {
			replayView.createForwardCommandList(replayer.jumpTo(index, step));
		} else {
			replayView.createBackCommandList(replayer.jumpTo(index, step));
		}
//		replayView.updateTimeSpent(replayer.getTotalTimeSpent(), replayer.getCurrentTimeSpent());
//		replayView.updateNumOfExceptions(replayer.getCurrentExceptions(), replayer.getTotalExceptions());
		replayView.updateReplayedFile(replayer.getReplayedFile());
		replayView.updateMetrics(replayer.getMetrics());
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
		EHEventRecorder.getInstance().flushAndStopLogging(project);
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
//                System.out.println("Zipping "+filePath);
                ZipEntry ze = new ZipEntry(filePath.replace(projectFolder+File.separator, "").replace(File.separator, "/"));
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
    		showNotification(project.getName() + surfix + " has been exported to your Desktop");
//            deleteFolder(folder);
        } catch (IOException e) {
        	e.printStackTrace();
    		showNotification("Failed to export " + project.getName() + ", please try again.");
        }
        EHEventRecorder.getInstance().continueLogging(project);
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
            else populateFilesList(fileNames, file);
        }
    }
}
