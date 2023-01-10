package fluorite.util;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fluorite.commands.EHICommand;

public class LogReaderTest {
	
	private EHLogReader reader;
	
	@Before
	public void setUp() {
		reader = new EHLogReader();
	}

	@Test
	public void testReadDocumentChanges() {
		try {
			List<EHICommand> documentChanges =
					reader.readDocumentChanges("//Users//jasoncarter//Documents//Barrier_User_Study_Logs//1//Log2013-08-06-09-53-32-746.xml")
					;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		int b = i;
		
//		assertEquals(9, documentChanges.size());
//
//		assertTrue(documentChanges.get(0) instanceof FileOpenCommand);
//		FileOpenCommand foc = (FileOpenCommand) documentChanges.get(0);
//		assertEquals("HelloWorld", foc.getProjectName());
//		assertEquals(
//				"D:\\Programming\\RuntimeWorkspaces\\runtime-Azurite\\HelloWorld\\src\\helloworld\\HelloWorld.java",
//				foc.getFilePath());
//		
//		String expectedSnapshot = "package helloworld;\r\n"
//				+ "\r\n"
//				+ "public class HelloWorld {\r\n"
//				+ "\r\n"
//				+ "\t/**\r\n"
//				+ "\t * @param args\r\n"
//				+ "\t */\r\n"
//				+ "\tpublic static void main(String[] args) {\r\n"
//				+ "\t\tSystem.out.println(\"Hello, world!\");\r\n"
//				+ "\t\tSystem.out.println(\"Hello, Charlie!\");\r\n"
//				+ "\t}\r\n"
//				+ "\t\r\n"
//				+ "\tpublic void foo() {\r\n"
//				+ "\t\t// Just a test method..\r\n"
//				+ "\t\t// \r\n"
//				+ "\t}\r\n"
//				+ "\r\n"
//				+ "}\r\n"
//				+ "";
//		
//		assertEquals(expectedSnapshot, foc.getSnapshot());
	}

}
