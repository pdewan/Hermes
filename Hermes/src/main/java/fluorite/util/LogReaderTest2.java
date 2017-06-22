package fluorite.util;

import java.io.File;
import java.util.List;

import fluorite.commands.EHICommand;

public class LogReaderTest2 {
	public static void main (String[] args) {
//		String logPath = "C:\Users\Jason\Desktop\Logs";
		String logPath = "data/Log2012-12-04-03-51-21-084.xml";
		File file = new File(logPath);
		System.out.println("File exists:" + file.exists());

		EHLogReader reader = new EHLogReader();

		List<EHICommand> commands = reader.readAll(logPath);
	}

}
