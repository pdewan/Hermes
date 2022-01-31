package analyzer.extension.replayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileEditor {
	File file;
	int cursor = 0;
	StringBuilder content;
	static final Pattern PATTERN = Pattern.compile("(public |private |protected )(.*)( +static)? +(.+)( *)\\(.*\\)");
	static String LINE_SEPARATOR = System.lineSeparator();
	public FileEditor(File file) {
		this.file = file;
		content = new StringBuilder();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))){
				content.append(br.lines().collect(Collectors.joining(LINE_SEPARATOR)));
//				String nextLine = null;
//				while ((nextLine = br.readLine()) != null) {
//					content.append(nextLine + LINE_SEPARATOR);
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public FileEditor(File file, String contentText) {
		this.file = file;
		content = new StringBuilder();
		content.append(contentText);
	}
	
	public void moveCursor(int offset) {
		cursor = regularize(offset);
	}
	
	public void insert(int offset, String text) {
		content.insert(regularize(offset), text);
		cursor = regularize(offset + text.length());
	}
	
	public void undoInsert(int offset, String text) {
		delete(offset, text);
	}
	
	public void delete(int offset, String text) {
		content.delete(regularize(offset), regularize(offset + text.length()));
		cursor = regularize(offset);
	}
	
	public void undoDelete(int offset, String text) {
		insert(offset, text);
	}
	
	public void replace(int offset, String deletedText, String insertedText) {		
		content.replace(regularize(offset), regularize(offset + deletedText.length()), insertedText);
		cursor = regularize(offset + insertedText.length());
	}
	
	public void undoReplace(int offset, String deletedText, String insertedText) {
		replace(offset, insertedText, deletedText);
	}
	
	public String getCurrentMethod() {
		Matcher matcher = PATTERN.matcher(content.substring(0, cursor));
		String method = "";
		while (matcher.find()) {
			method = matcher.group(4);
		}
		return method;
	}
	
	private int regularize(int offset) {
		return offset > content.length() ? content.length() : offset;
	}
	
	public void writeToDisk() {
		boolean created = false;
		if (file.exists()) {
			file.delete();
		}
		try {
			file.getParentFile().mkdirs();
			created = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (created) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write(content.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getContent() {
		return content.toString();
	}
	
	public String getFileName() {
		return file.getName();
	}
	
	public static FileEditor getEditor(File file) {
		return new FileEditor(file);
	}
	
	public static FileEditor getEditor(String path) {
		return new FileEditor(new File(path));
	}
	
	public static FileEditor getEditor(File file, String path) {
		return new FileEditor(new File(file, path));
	}
	
	public static FileEditor getEditor(File file, String path, String content) {
		return new FileEditor(new File(file, path), content);
	}
	
	public static FileEditor getEditor(String path, String content) {
		return new FileEditor(new File(path), content);
	}

}
