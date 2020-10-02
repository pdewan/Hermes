package dayton.ellwanger.helpbutton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ui.editors.text.EditorsUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dayton.ellwanger.helpbutton.exceptionMatcher.ExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.JavaExceptoinMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.PrologExceptionMatcher;
import dayton.ellwanger.helpbutton.preferences.HelpPreferencePage;
import dayton.ellwanger.helpbutton.preferences.HelpPreferences;
import fluorite.commands.EHICommand;
import fluorite.commands.FileOpenCommand;
import fluorite.commands.GetHelpCommand;
import fluorite.commands.RequestHelpCommand;
import fluorite.commands.ShellCommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.EclipseEventListener;
import fluorite.util.EHLogReader;
import fluorite.util.EHUtilities;

public class HelpViewController implements HelpListener, EclipseEventListener {
	//	private static final String testURL = "http://localhost:12345";
	private static final String getHelpURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/get-available-help";
	private static final String requestHelpURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/request-help";
	private static final String reportURL = "http://localhost:12345";
//	private final Pattern exceptionPattern = Pattern.compile(".+Exception[^\\n]++(\\s+at .++)+");
//	private List<ExceptionMatcher> exceptionMatchers = new ArrayList<>();
	private HelpView view;
	private int numCommand = 0;
	private static final int commandLim = 100;
	private ExceptionMatcher em;
	
	public HelpViewController(HelpView view) {
		this.view = view;
//		addExceptionMatcher(JavaExceptoinMatcher.getInstance());
		view.setHelpListener(this);
		setExceptionMatcher(EditorsUI.getPreferenceStore().getString(HelpPreferences.LANGUAGE));
		ConsoleListener.getInstance().addListener(this);
		EHEventRecorder.getInstance().addEclipseEventListener(this);
	}

	public void exceptionEvent(String output){
		List<String> exceptions = new ArrayList<>();
//		Matcher matcher = exceptionPattern.matcher(output);
//		while (matcher.find()) {
//			String ex = matcher.group();
//			if (!exceptions.contains(ex)) {
//				exceptions.add(matcher.group());
//			}
//		}
//		for (ExceptionMatcher em : exceptionMatchers) {
			exceptions = em.match(output);
//			if (exceptions.size() != 0) {
//				break;
//			}
//		}
		view.populateErrorCombo(exceptions);
	}
	
//	public void addExceptionMatcher(ExceptionMatcher em) {
//		exceptionMatchers.add(em);
//	}
//	
//	public void removeExcetionMatcher(ExceptionMatcher em) {
//		exceptionMatchers.remove(em);
//	}
	
	public void setExceptionMatcher(String language) {
		switch (language) {
		case "java":
			em = JavaExceptoinMatcher.getInstance();
			break;
		case "prolog":
			em = PrologExceptionMatcher.getInstance();
			break;
		case "python":
			em = JavaExceptoinMatcher.getInstance();
			break;
		default:
			em = JavaExceptoinMatcher.getInstance();
			break;
		}
	}

	public void consoleOutput(String output) {
		view.updateConsoleOutput(output);
		exceptionEvent(output);
	}

	@Override
	public JSONObject requestHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, int difficulty, String helpText, String requestID, String output, String language) throws IOException {
		try {
			JSONObject helpRequest = new JSONObject();
			JSONObject code = new JSONObject();
			helpRequest.put("code", code);
			if (language == null || language.equals("")) {
				language = "java";
			}
			helpRequest.put("language", language);
			helpRequest.put("environment", "ecipse");
			helpRequest.put("error-message", errorMessage);
			helpRequest.put("error-type", errorType);
			helpRequest.put("course", course);
			helpRequest.put("assignment", assign);
			helpRequest.put("comment", helpText);
			helpRequest.put("problem", problem);
			helpRequest.put("term", term);
			helpRequest.put("email", email);
			helpRequest.put("request-id", "");
			helpRequest.put("output", output);
			File src = new File(getCurrentProjectPath() + File.separator + "Logs" + File.separator + "srcOld");
			if (!src.exists() || numCommand > 50) {
				src = new File(getCurrentProjectPath() + File.separator + "Logs" + File.separator + "src");
			}
			if (!src.exists()) {
				src = findSourceFolder(new File(getCurrentProjectPath()), EditorsUI.getPreferenceStore().getString(HelpPreferences.LANGUAGE));
			}
			List<String> filePaths = new ArrayList<>();
			List<String> relevantFilePaths = new ArrayList<>();
			findSourceFiles(src, filePaths, language);
			if (calculateSize(src) < 900*1024) {
				for (String filePath : filePaths) {
					File file = new File(filePath);
					code.put(file.getName(), readFile(file));
				}
				addHistory(code, calculateSize(src));
			} else if (errorMessage.indexOf("\r\n\t") >= 0) {
				String stackTrace = errorMessage.substring(errorMessage.indexOf("\r\n\t"));
				while (stackTrace.indexOf(".java") >= 0) {
					String fileName = stackTrace.substring(stackTrace.lastIndexOf('(')+1, stackTrace.lastIndexOf(".java")+5);
					for (String file : filePaths) {
						if (file.contains(fileName)) {
							relevantFilePaths.add(file);
							filePaths.remove(file);
							break;
						}
					}
					stackTrace = stackTrace.substring(0, stackTrace.lastIndexOf('('));
				}
				long size = 0;
				for (String filePath : relevantFilePaths) {
					File file = new File(filePath);
					size += file.length();
					if (size > 900) {
						break;
					}
					code.put(file.getName(), readFile(file));
				}
				addHistory(code, size);
			} else {
				long size = addHistory(code, 0);
				for (String filePath : filePaths) {
					File file = new File(filePath);
					if (code.has(file.getName())) {
						continue;
					}
					size += file.length();
					if (size > 900) {
						break;
					}
					code.put(file.getName(), readFile(file));
				}
			}
			JSONObject response = HTTPRequest.post(helpRequest, requestHelpURL);
			if (response == null) {
				recordRequestHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, "", false, difficulty);
				throw new IOException();
			}
			helpRequest.put("request-id", response.get("request-id"));
			recordRequestHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, "", true, difficulty);
			view.addPendingRequest(helpRequest);
			view.removeRepliedRequest(response.getString("request-id"));

			new Thread(()->{
				try {
//					String requestId = response.getJSONObject("input").getJSONObject("body").getString("request-id");
					String requestId = response.getString("request-id");
					String id = requestId.substring(requestId.lastIndexOf('.')+1);
					File[] files = view.getRepliedFolder().listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.equals(id+".JSON");
						}
					});
					File file = new File(view.getPendingFolder().getPath()+ File.separator + id +".JSON");
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
					FileOutputStream os = new FileOutputStream(file);
					if (files.length == 1) {
						os.write(readJSON(files[0]).toString(4).getBytes());
						files[0].delete();
					} else {
						os.write(helpRequest.toString(4).getBytes());
					}
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}).start();
			return helpRequest;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String language) throws IOException {
		try {
			JSONObject helpRequest = new JSONObject();
			JSONObject code = new JSONObject();
			helpRequest.put("code", code);
			helpRequest.put("language", language);
			helpRequest.put("environment", "eclipse");
			helpRequest.put("error-message", errorMessage);
			helpRequest.put("error-type", errorType);
			helpRequest.put("course", course);
			helpRequest.put("assignment", assign);
			helpRequest.put("problem", problem);
			helpRequest.put("term", term);
			helpRequest.put("email", email);
			if (requestID == null) {
				helpRequest.put("request-id", "");
			} else {
				helpRequest.put("request-id", requestID);
			}
			helpRequest.put("output", output);
//			System.out.println(helpRequest.toString(4));
			JSONObject response = HTTPRequest.post(helpRequest, getHelpURL);
			if (response == null) {
				recordGetHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, "", false);
				throw new IOException();
			}
//			System.out.println(response.toString(4));
			List<String> replies = new ArrayList<>();
			JSONArray help = new JSONArray();
			try {
				help = response.getJSONArray("help");
				for (int i = 0; i < help.length(); i++) {
					replies.add(help.getString(i));
				}
			}catch (JSONException e) {
			}
			String id = response.getJSONObject("input").getJSONObject("body").getString("request-id");
			helpRequest.put("request-id", id);
			helpRequest.put("help", help);
			recordGetHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, help.toString(4), true);
			view.updateReplies(replies);
			if (replies.size() == 0) {
				return helpRequest;
			}
			
			view.addRepliedRequest(helpRequest);
			view.removePendingRequest(id);
			new Thread(()->{
				try {
					String requestId = response.getJSONObject("input").getJSONObject("body").getString("request-id");
					String rid = requestId.substring(requestId.lastIndexOf('.')+1);
					File[] files = view.getPendingFolder().listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.equals(rid+".JSON");
						}
					});
					if (files.length == 1) {
						files[0].delete();
					}
//					helpRequest.put("help", help);
					File file = new File(view.getRepliedFolder().getPath()+ File.separator + rid +".JSON");
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
					FileOutputStream os = new FileOutputStream(file);
					os.write(helpRequest.toString(4).getBytes());
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}).start();
			return helpRequest;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null; 
	}

	private void findSourceFiles(File file, List<String> files, String language){
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
				findSourceFiles(file2, files, language);
			}
		} else {
			switch (language) {
			case "java":
				if (file.getName().toLowerCase().endsWith(".java")) {
					files.add(file.getAbsolutePath());
				}
				break;
			case "python":
				if (file.getName().toLowerCase().endsWith(".py")) {
					files.add(file.getAbsolutePath());
				}
				break;
			case "prolog":
				if (file.getName().toLowerCase().endsWith(".pl")) {
					files.add(file.getAbsolutePath());
				}
				break;
			default:
				break;
			}
		}
	}

	private long calculateSize(File file) {
		long size = 0;
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
				size += calculateSize(file2);
			}
			return size;
		} else {
			return file.length();
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

	private JSONObject readJSON(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			reader.close();
			return new JSONObject(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void recordGetHelpCommand(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String help, boolean success) {
		new Thread(()->{
			EHEventRecorder.getInstance().recordCommand(new GetHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, help, success));
		}).start();
	}

	private void recordRequestHelpCommand(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String help, boolean success, int difficulty) {
		new Thread(()->{
			EHEventRecorder.getInstance().recordCommand(new RequestHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, help, success, difficulty));
		}).start();
	}
	//	@Override
	//	public void difficultyUpdate(int difficulty) {
	//		if(ConnectionManager.getInstance() != null) {
	//			JSONObject helpRequest = new JSONObject();
	//			try {
	//				helpRequest.put("difficulty", difficulty);
	//			} catch (JSONException ex) {
	//				ex.printStackTrace();
	//			}
	//			DifficultyUpdateForwardedToConnectionManager.newCase(this, helpRequest.toString());
	////			JSONObjectForwardedToConnectionManager.newCase(this, helpRequest.toString());
	//
	//			ConnectionManager.getInstance().sendMessage(helpRequest);
	//		}		
	//	}
	
	private long addHistory(JSONObject code, long size) {
		File log = new File(getCurrentProjectPath()+File.separator+"Logs"+File.separator+"Eclipse");
		if (!log.exists()) {
			return size;
		}
		File[] logs = log.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		if (logs.length == 0) {
			return size;
		}
		File currentLog = logs[0];
		for (File file : logs) {
			if (file.lastModified() > currentLog.lastModified()) {
				currentLog = file;
			}
		}
		try {
//			List<EHICommand> commands = EHEventRecorder.getInstance().getAllCommands();
			File temp = new File(log, "temp.xml");
			copyFiles(currentLog, temp);
			List<EHICommand> commands;
			try {
				commands = new EHLogReader().readAll(temp.getPath());
//				commands = new EHLogReader().readAll("C:\\Users\\Zhizhou\\OneDrive\\UNC CH\\Junior 1st Sem\\hermes\\test\\Logs\\Eclipse\\Log2020-09-02-11-07-58-675.xml");
			} catch (NullPointerException e) {
				temp.delete();
				return size;
			}
			int num = numCommand > commandLim/2? numCommand : numCommand + commandLim;
			temp.delete();
			List<File> modifiedFiles = new ArrayList<>(); 
			JSONArray history = new JSONArray();
			List<EHICommand> filteredCommand = new ArrayList<>();
			code.put("log", history);
			for (int i = commands.size()-1; i >= 0; i--) {
				EHICommand command = commands.get(i);
				if (command instanceof FileOpenCommand) {
					File f = new File(command.getDataMap().get("filePath"));
					if (f.exists() && !modifiedFiles.contains(f)) {
						modifiedFiles.add(f);
					}
				}
//				if (command instanceof ShellCommand || command instanceof GetHelpCommand || command instanceof RequestHelpCommand) {
//					continue;
//				}
				if (command instanceof ShellCommand) {
					continue;
				}
				size += command.persist().length();
				if (size > 900 * 1024) {
					break;
				}
				filteredCommand.add(command);
				if (filteredCommand.size() == num) {
					break;
				}
			}
			for (int i = filteredCommand.size() - 1; i >= 0; i--) {
				history.put(filteredCommand.get(i).persist());
			}
			for (File file : modifiedFiles) {
				if (!code.has(file.getName())) {
					size += file.length();
					if (size > 900*1024) {
						break;
					}
					code.put(file.getName(), readFile(file));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}
	
	private String getCurrentProjectPath(){
		return EHUtilities.getCurrentProject().getLocation().toOSString();
	}
	
	private String readFile(File file) {
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void eventRecordingStarted(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventRecordingEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timestampReset(long aStartTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commandExecuted(String aCommandName, long aTimestamp) {
		// TODO Auto-generated method stub
		if (aCommandName.equals("ShellCommand")) {
			return;
		}
		createSnapshot();
	}

	@Override
	public void documentChanged(String aCommandName, long aTimestamp) {
		// TODO Auto-generated method stub
		createSnapshot();
	}

	@Override
	public void documentChangeFinalized(long aTimestamp) {
		// TODO Auto-generated method stub
		createSnapshot();
	}
	
	private void createSnapshot() {
		numCommand++;
		if (numCommand == commandLim) {
			numCommand = 0;
			new Thread(()->{
				File src = findSourceFolder(new File(getCurrentProjectPath()), EditorsUI.getPreferenceStore().getString(HelpPreferences.LANGUAGE));
				File snapshot = new File(getCurrentProjectPath()+File.separator+"Logs"+File.separator+"src");
				File snapshotOld = new File(getCurrentProjectPath()+File.separator+"Logs"+File.separator+"srcOld");
				if (snapshot.exists()) {
					if (snapshotOld.exists()) {
						deleteFolder(snapshot);
					}
					cloneFolder(snapshot, snapshot.getPath(), snapshotOld.getPath());
				}
				cloneFolder(src, src.getPath(), snapshot.getPath());
				sendLogToServer();
			}).start();
		}
	}

	private File findSourceFolder(File folder, String language) {
		if (language.equals("java")) {
			return new File(folder + File.separator + "src");
		}
		if(folder.listFiles((File dir, String name)->{
			switch (language) {
			case "prolog":
				return name.endsWith(".pl");
			case "python":
				return name.endsWith(".py");
			default:
				return false;
			}
		}).length > 0) {
			return folder;
		}
		File src = null;
		for (File dir : folder.listFiles((File pathname)-> {return pathname.isDirectory() && !pathname.getPath().contains(getCurrentProjectPath()+File.separator+"Logs");})) {
			src = findSourceFolder(dir, language);
			if (src != null) {
				if (folder.getPath().equals(getCurrentProjectPath())) {
					return dir;
				}
				return src;
			}
		}
		return null;
	}
	
	private void sendLogToServer() {
		File log = new File(getCurrentProjectPath()+File.separator+"Logs"+File.separator+"Eclipse");
		if (!log.exists()) {
			return;
		}
		File[] logs = log.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		if (logs.length == 0) {
			return;
		}
		File currentLog = logs[0];
		for (File file : logs) {
			if (file.lastModified() > currentLog.lastModified()) {
				currentLog = file;
			}
		}
		try {
			File temp = new File(log, "temp2.xml");
			copyFiles(currentLog, temp);
			List<EHICommand> commands;
			try {
				commands = new EHLogReader().readAll(temp.getPath());
			} catch (NullPointerException e) {
				temp.delete();
				return;
			}
			temp.delete();
			JSONArray history = new JSONArray();
			List<EHICommand> filteredCommand = new ArrayList<>();
			JSONObject report = new JSONObject();
			JSONObject code = new JSONObject();
			report.put("code", code);
			report.put("language", "java");
			report.put("environment", "ecipse");
			code.put("log", history);
			int size = report.length();
			int lim = commands.size() - commandLim;
			for (int i = commands.size()-1; i >= lim; i--) {
				EHICommand command = commands.get(i);
				if (command instanceof ShellCommand) {
					if (lim > 0) {
						lim--;
					}
					continue;
				}
				size += command.persist().length();
				if (size > 900 * 1024) {
					break;
				}
				filteredCommand.add(command);
			}
			for (int i = filteredCommand.size() - 1; i >= 0; i--) {
				history.put(filteredCommand.get(i).persist());
			}
			HTTPRequest.post(report, reportURL);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void cloneFolder(File source, String sourcePath, String destPath){
		try {
			for (File file : source.listFiles()) {
				if (file.isDirectory()) {
					new File(file.getPath().replace(sourcePath, destPath)).mkdirs();
					cloneFolder(file, file.getPath(), file.getPath().replace(sourcePath, destPath));
				} else {
					copyFiles(file, new File(file.getPath().replace(sourcePath, destPath)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void deleteFolder(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}
}


