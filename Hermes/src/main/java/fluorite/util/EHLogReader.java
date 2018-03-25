package fluorite.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fluorite.commands.AbstractCommand;
import fluorite.commands.AnnotateCommand;
import fluorite.commands.CompilationCommand;
import fluorite.commands.DifficultyCommand;
import fluorite.commands.EclipseCommand;
import fluorite.commands.EHICommand;
import fluorite.model.EHEventRecorder;
import util.trace.recorder.ParsedCommand;
/*
 * Cannot extend LogReader as it returns Events rather than Icommands 
 */
public class EHLogReader {

	/**
	 * Initializes a new LogReader instance. Does nothing special.
	 */
	public EHLogReader() {
	}

	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * All commands will be included.
	 * 
	 * @param logPath
	 *            the file path to the log file.
	 * @return deserialized list of commands
	 */
	public List<EHICommand> readAll(String logPath) {
		return readFilter(logPath, null);
	}

	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * Only the FileOpenCommand and all the DocumentChanges will be included.
	 * 
	 * @param logPath
	 *            the file path to the log file.
	 * @return deserialized list of commands
	 */
	public List<EHICommand> readDocumentChanges(String logPath) {
		return readFilter(logPath, new IFilter() {
			@Override
			public boolean filter(Element element) {
				/*
				 * if (isCommandTyped(element, "FileOpenCommand") ||
				 * isDocumentChange(element)) { return true; }
				 * 
				 * return false;
				 */

				return true;
			}
		});
	}

	public static void readStringXML(String xml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xml
					.getBytes("UTF-8")));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * Only the filtered commands will be included.
	 * 
	 * @param logPath
	 *            the file path to the log file.
	 * @return deserialized list of commands
	 * @throws DocumentException
	 */
	public List<EHICommand> readFilter(String logPath, IFilter filter) {
		if (logPath == null) {
			throw new IllegalArgumentException();
		}

		List<EHICommand> result = new ArrayList<EHICommand>();

		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dbFactory.setValidating(false);
			dbFactory.setFeature("http://xml.org/sax/features/namespaces",
					false);
			dbFactory.setFeature("http://xml.org/sax/features/validation",
					false);
			dbFactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
							false);
			dbFactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			dBuilder.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					if (systemId.contains("foo.dtd")) {
						return new InputSource(new StringReader(""));
					} else {
						return null;
					}
				}
			});

			File file = new File(logPath);
			FileInputStream fis = new FileInputStream(file);
			doc = dBuilder.parse(fis);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element root = doc.getDocumentElement();

		boolean prevState = AbstractCommand.getIncrementCommandID();
		AbstractCommand.setIncrementCommandID(false);
		String startTimestamp = root.getAttribute("startTimestamp");
		DifficultyCommand startCommand = new DifficultyCommand();
		startCommand.setTimestamp2(Long.parseLong(startTimestamp));
		
		result.add(startCommand);
		//startCommand.setTimestamp(r);
		for (Node node = root.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (!(node instanceof Element)) {
				continue;
			}

			Element child = (Element) node;

			if (filter == null || filter.filter(child)) {
				result.add(parse(child));
			}
		}

		AbstractCommand.setIncrementCommandID(prevState);

		return result;
	}

	private static boolean isCommandTyped(Element element, String typeName) {
		return isCommand(element) && isType(element, typeName);
	}

	private static boolean isType(Element element, String typeName) {
		Attr attr = element
				.getAttributeNode(EHEventRecorder.XML_CommandType_ATTR);
		return attr != null && attr.getValue().equals(typeName);
	}

	private static boolean isCommand(Element element) {
		return element.getTagName().equals(EHEventRecorder.XML_Command_Tag);
	}

	private static boolean isDocumentChange(Element element) {
		return element.getTagName()
				.equals(EHEventRecorder.XML_DocumentChange_Tag);
	}

	private static boolean isAnnotation(Element element) {
		return element.getTagName().equals(EHEventRecorder.XML_Annotation_Tag);
	}

	private static boolean isSurmountableDifficulty(Element element) {
		return element.getTagName().equals(
				EHEventRecorder.XML_DifficultyStatus_Tag);
	}
	
	private static EHICommand parse(Element element) {
		EHICommand retVal = doParse(element);
		ParsedCommand.newCase(retVal, element, EHLogReader.class);
		return retVal;
	}
	

	private static EHICommand doParse(Element element) {
//		try {
		if (isCommand(element) || isDocumentChange(element)) {
			String typeName = element
					.getAttribute(EHEventRecorder.XML_CommandType_ATTR);
			if (typeName == null) {
				throw new IllegalArgumentException();
			}

			Package commandsPackage = EHICommand.class.getPackage();
			String fullyQualifiedName = commandsPackage.getName() + "."
					+ typeName;
			
			
			if (fullyQualifiedName
//					.equals("edu.cmu.scs.fluorite.commands.EclipseCommand"))
				.equals(EclipseCommand.class.getName()))

			{
				int i = 0;
				int b = i;
			}

			try {
				Class<?> c = Class.forName(fullyQualifiedName);
				EHICommand command = (EHICommand) c.newInstance();
				command.createFrom(element);
				
				if (fullyQualifiedName
//						.equals("edu.cmu.scs.fluorite.commands.EclipseCommand"))
					.equals(EclipseCommand.class.getName()))

				{
					int i = 0;
					int b = i;
				}

				return command;
			} catch (ClassNotFoundException e) {

				if (fullyQualifiedName
						.equals("edu.cmu.scs.fluorite.commands.CompilationCommand")) {
					CompilationCommand compliationCommand = new CompilationCommand();
					compliationCommand.createFrom(element);
					return compliationCommand;
				} 
				else if(fullyQualifiedName
						.equals("edu.cmu.scs.fluorite.commands.DifficultyCommand"))
					{
					DifficultyCommand difficultyCommand = new DifficultyCommand();
					difficultyCommand.createFrom(element);
					return difficultyCommand;
					
					}
					else {
					e.printStackTrace();
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (isAnnotation(element)) {
			AnnotateCommand annotateCommand = new AnnotateCommand();
			annotateCommand.createFrom(element);

			return annotateCommand;
		} else if (isSurmountableDifficulty(element)) {
			DifficultyCommand surmountableDifficultyCommand = new DifficultyCommand();
			surmountableDifficultyCommand.createFrom(element);
			return surmountableDifficultyCommand;
		} else {
			throw new IllegalArgumentException();
		}
		

		return null;
//		} finally {
//			ParsedCommand.newCase(aCommand, aStartTimestamp, anElement, aFinder)
//		}
	}
}
