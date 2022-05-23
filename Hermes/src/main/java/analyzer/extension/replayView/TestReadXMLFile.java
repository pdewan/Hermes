package analyzer.extension.replayView;

import java.io.File;
import java.util.List;

import fluorite.commands.EHICommand;
import fluorite.util.EHLogReader;

//static String project = "C:\\Users\\dewan\\Downloads\\Assignment2A\\Assignment 2\\Hauck, Hyacinth(Hyacinth Hauck)\\Submission attachment\\Submission attachment(s)\\assignment2\\Logs\\Eclipse\Generated\Log2021-06-08-12-47-41-096.xml"
//static String project = "C:\\Users\\dewan\\Downloads\\Assignment2A\\Assignment 2\\Hauck, Hyacinth(Hyacinth Hauck)\\Submission attachment\\Submission attachment(s)\\assignment2\\Logs\\Eclipse\Generated\Log2021-06-08-12-47-41-096.xml"
public class TestReadXMLFile {
	static EHLogReader reader = new EHLogReader();
	static String generatedFile = "C:\\Users\\dewan\\Downloads\\Assignment2A\\Assignment 2\\Hauck, Hyacinth(Hyacinth Hauck)\\Submission attachment(s)\\assignment2\\Logs\\Eclipse\\Generated\\Log2021-06-08-12-47-41-096.xml";

	static String originalFile = "C:\\Users\\dewan\\Downloads\\Assignment2A\\Assignment 2\\Hauck, Hyacinth(Hyacinth Hauck)\\Submission attachment(s)\\assignment2\\Logs\\Eclipse\\Log2021-06-08-12-47-41-096.xml";

	public static List<EHICommand> readOneLogFile(File log){
		String path = log.getPath();
		System.out.println("Reading file " + path);
		if (!log.exists()) {
			System.err.println("log does not exist:" + path);
			return null;
		}
		if (!path.endsWith(".xml")) {
			System.err.println("log is not in xml format:" + path);
			return null;
		}
		try {
			List<EHICommand> commands = reader.readAll(path);
//			sortCommands(commands);
			return commands;
		} catch (Exception e) {
			System.err.println("Could not read file" + path + "\n"+ e);
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		readOneLogFile(new File(originalFile));
		readOneLogFile(new File(generatedFile));

	}
}
