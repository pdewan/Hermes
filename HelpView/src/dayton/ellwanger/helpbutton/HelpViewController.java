package dayton.ellwanger.helpbutton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import dayton.ellwanger.helpbutton.exceptionMatcher.ExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.JavaExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.PrologExceptionMatcher;
import dayton.ellwanger.helpbutton.exceptionMatcher.SMLExceptionMatcher;
import dayton.ellwanger.helpbutton.preferences.HelpPreferences;
import fluorite.commands.GetHelpCommand;
import fluorite.commands.RequestHelpCommand;
import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class HelpViewController implements HelpListener {
	private static final String getHelpURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/get-available-help";
	private static final String requestHelpURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/request-help";
//	private static final String reportURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/cyverse/add-cyverse-log";
	private HelpView view;
	private ExceptionMatcher em;
	private static final int LIMIT = 1024*1024;
	
	public HelpViewController(HelpView view) {
		this.view = view;
		view.setHelpListener(this);
		setExceptionMatcher(EditorsUI.getPreferenceStore().getString(HelpPreferences.LANGUAGE));
		ConsoleListener.getInstance().addListener(this);
	}

	public void exceptionEvent(String output){
		List<String> exceptions = new ArrayList<>();
		exceptions = em.match(output);
		view.populateErrorCombo(exceptions);
	}
	
	public void setExceptionMatcher(String language) {
		switch (language) {
		case "java":
			em = JavaExceptionMatcher.getInstance();
			break;
		case "prolog":
			em = PrologExceptionMatcher.getInstance();
			break;
		case "python":
			em = JavaExceptionMatcher.getInstance();
			break;
		case "SML":
			em = SMLExceptionMatcher.getInstance();
			break;
		default:
			em = JavaExceptionMatcher.getInstance();
			break;
		}
	}

	public void consoleOutput(String output) {
		view.updateConsoleOutput(output);
		exceptionEvent(output);
	}

	@Override
	public void requestHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, int difficulty, String helpText, String requestID, String output, String language, IProject project) {
		new Thread(new Runnable() {
			public void run() {
				try {
//					MessageConsoleStream out = view.findConsole("debugRequestHelp").newMessageStream();
//					out.println("requesting help");
					JSONObject helpRequest = new JSONObject();
					JSONObject code = new JSONObject();
					helpRequest.put("code", code);
					if (language == null || language.equals("")) {
						helpRequest.put("language", "java");
					} else {
						helpRequest.put("language", language);
					}
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
					if (project != null) {
						String projectPath = project.getLocation().toOSString();
						File src = findSourceFolder(new File(projectPath), language);
						if (src.exists()) {
							List<String> filePaths = new ArrayList<>();
							findSourceFiles(src, filePaths, language);
							for (String filePath : filePaths) {
								File file = new File(filePath);
								code.put(file.getName(), readFile(file));
							}
						}
//						File logFolder = new File(projectPath, "Logs"+File.separator+"Eclipse");
//						JSONArray log = new JSONArray();
//						code.put("log", log);
//						if (logFolder.exists()) {
//							List<File> logFiles = findLogFiles(project, logFolder);
//							if (logFiles != null) {
//								for (File file : logFiles) {
//									log.put(readFile(file));
//								}
//							}
//						}
					}
					JSONObject response = HTTPRequest.post(helpRequest, requestHelpURL);
//					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							try {
//								view.popupMessage("Response", response.toString(4));
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//						}
//					});
					helpRequest.put("request-id", response.get("request-id"));
					recordRequestHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, "", response!=null, difficulty);
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							view.processRequestHelpResponse(response);
						}
					});
					

					new Thread(()->{
						try {
//							String requestId = response.getJSONObject("input").getJSONObject("body").getString("request-id");
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
				} catch (JSONException e) {
					e.printStackTrace();
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							view.processRequestHelpResponse(null);
						}
					});
				}
			}
		}).start();
	}

	public void getHelp(String email, String course, String assign, String errorType, String errorMessage, String problem, String term, String requestID, String output, String language) {
		new Thread(new Runnable() {
			public void run() {
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
//					System.out.println(helpRequest.toString(4));
					JSONObject response = HTTPRequest.post(helpRequest, getHelpURL);
					if (response == null) {
						recordGetHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, "", false);
					} else {
//					System.out.println(response.toString(4));
						List<String> replies = new ArrayList<>();
						JSONArray help = new JSONArray();
						try {
							help = response.getJSONArray("help");
							for (int i = 0; i < help.length(); i++) {
								replies.add(help.getString(i));
							}
						}catch (JSONException e) {
						}
						String id = response.getString("request-id");
						helpRequest.put("request-id", id);
						helpRequest.put("help", help);
						recordGetHelpCommand(email, course, assign, errorType, errorMessage, problem, term, requestID, output, help.toString(4), true);
					}
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							view.processGetHelpResponse(helpRequest);
						}
					});

					new Thread(()->{
						try {
							String requestId = response.getString("request-id");
							String rid = requestId.substring(requestId.lastIndexOf('.')+1);
							File[] files = view.getPendingFolder().listFiles(new FilenameFilter() {
								public boolean accept(File dir, String name) {
									return name.equals(rid+".JSON");
								}
							});
							if (files.length == 1) {
								files[0].delete();
							}
//							helpRequest.put("help", help);
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();;
	}

	private void findSourceFiles(File file, List<String> files, String language){
//		view.findConsole("debugRequestHelp").newMessageStream().println("finding " + language + " files from " + file);
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
//				view.findConsole("debugRequestHelp").newMessageStream().println("finding " + language + " files from " + file2);
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
			case "SML":
				if (file.getName().toLowerCase().endsWith(".sml")) {
					files.add(file.getAbsolutePath());
				}
				break;
			default:
				break;
			}
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
	
	private String getCurrentProjectPath(){
		IProject project = EHUtilities.getCurrentProject();
		if (project == null) {
			return "";
		}
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
			case "SML":
				return name.endsWith(".sml");
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
	
	public List<File> findLogFiles(IProject project, File logFolder) {
		if (project == null || logFolder == null || !logFolder.exists()) {
			return null;
		} 
		EHEventRecorder.getInstance().flushAndStopLogging(project);
		List<File> retVal = new ArrayList<>();
		List<File> logFiles = Arrays.asList(logFolder.listFiles());
		Collections.sort(logFiles, Collections.reverseOrder());
		int size = 0;
		for(File file : logFiles){
			size += file.length();
			if (size < LIMIT) {
				retVal.add(file);
			} else {
				size -= file.length();
				break;
			}
		}
        EHEventRecorder.getInstance().continueLogging(project);
        return retVal;
	}
}


