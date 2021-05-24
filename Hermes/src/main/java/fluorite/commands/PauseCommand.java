package fluorite.commands;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class PauseCommand extends AbstractCommand implements EHICommand{
	private static final String XML_RANGE = "range";
	private static final String XML_PREV = "prev";
	private static final String XML_NEXT = "next";
	private static final String XML_PAUSE = "pause";
	private static final String XML_PREV_TYPE = "prevType";
	private static final String XML_NEXT_TYPE = "nextType";
	public static final String[] TYPES = {"Edit", "Debug", "Run", "IO", "Exception", 
										  "Request", "Web", "Save", "Gained Focus", 
										  "Lost Focus", "Terminate", "Difficulty", 
										  "Move Caret", "Open File", "Select", "Compile", 
										  "LocalChecks", "Other"}; 
	public static final String[] RANGES = {"1s-2s","2s-5s","5s-10s","10s-20s","20s-30s",
											"30s-1m","1m-2m","2m-5m","5m-10m","10m-20m",
											"20m-30m","30m-1h",">1h"};

	private String prev, next, range, prevType, nextType;
	private long pause;

	public PauseCommand() {}
	
	public PauseCommand(EHICommand prev, EHICommand next, long pause) {
		this.prev = prev.getCommandType();
		this.next = next.getCommandType();
		this.pause = pause;
		this.range = getRange(pause);
		prevType = getType(prev);
		nextType = getType(next);
	}
	
	public String getRange(long pause) {
		String range = "";
		long s = 1000;
		if (pause < 2*s) {
			range = RANGES[0];
		} else if (pause < 5*s) {
			range = RANGES[1];
		} else if (pause < 10*s) {
			range = RANGES[2];
		} else if (pause < 20*s) {
			range = RANGES[3];
		} else if (pause < 30*s) {
			range = RANGES[4];
		} else if (pause < 60*s) {
			range = RANGES[5];
		} else if (pause < 2*60*s) {
			range = RANGES[6];
		} else if (pause < 5*60*s) {
			range = RANGES[7];
		} else if (pause < 10*60*s) {
			range = RANGES[8];
		} else if (pause < 20*60*s) {
			range = RANGES[9];
		}else if (pause < 30*60*s) {
			range = RANGES[10];
		} else if (pause < 60*60*s) {
			range = RANGES[11];
		} else {
			range = RANGES[12];
		}
		return range;
	}
	
	private String getType(EHICommand command) {
		if (command instanceof InsertStringCommand || command instanceof Insert ||
				command instanceof CopyCommand ||
				command instanceof Delete ||
				command instanceof Replace || command instanceof PasteCommand ||
				command instanceof AssistCommand) {
			return TYPES[0];
		}
		if (command instanceof RunCommand && (command.getAttributesMap().get("kind").equals("HitBreakPoint") || command.getAttributesMap().get("type").equals("Debug"))) {
			return TYPES[1];
		}
		if (command instanceof RunCommand && command.getAttributesMap().get("type").equals("Run")) {
			return TYPES[2];
		}
		if (command instanceof RunCommand || command instanceof ConsoleOutput) {
			return TYPES[3];
		}
		if (command instanceof ExceptionCommand) {
			return TYPES[4];
		}
		if (command instanceof RequestHelpCommand || command instanceof GetHelpCommand) {
			return TYPES[5];
		}
		if (command instanceof WebCommand) {
			return TYPES[6];
		}
		if (command instanceof EclipseCommand) {
			String id = command.getAttributesMap().get(EclipseCommand.XML_ID_ATTR).toLowerCase();
			if (id.contains("delete")) {
				return TYPES[0];
			}
			if (id.contains("save")) {
				return TYPES[7];
			}
			if (id.contains("terminate")) {
				return TYPES[10];
			}
			if (id.contains("line_start") || id.contains("line_down") || id.contains("line_up") || id.contains("column")) {
				return TYPES[12];
			}
		}
		if (command instanceof ShellCommand) {
			String type = command.getAttributesMap().get("type").toLowerCase();
			if (type.contains("gained_focus")) {
				return TYPES[8];
			}
			if (type.contains("lost_focus") || type.contains("minimized")) {
				return TYPES[9];
			}
		}
		if (command instanceof DifficultyCommand) {
			return TYPES[11];
		}
		if (command instanceof MoveCaretCommand) {
			return TYPES[12];
		}
		if (command instanceof FileOpenCommand) {
			return TYPES[13];
		}
		if (command instanceof SelectTextCommand) {
			return TYPES[14];
		}
		if (command instanceof CompilationCommand) {
			return TYPES[15];
		}
		if (command instanceof LocalCheckCommand) {
			return TYPES[16];
		}
		return TYPES[17];
	}
	
	@Override
	public boolean execute(IEditorPart target) {
		return false;
	}

	@Override
	public void dump() {
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put(XML_RANGE, range);
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new TreeMap<String, String>(Comparator.reverseOrder());
		dataMap.put(XML_PREV, prev);
		dataMap.put(XML_NEXT, next);
		dataMap.put(XML_PAUSE, pause+"");
		dataMap.put(XML_PREV_TYPE, prevType);
		dataMap.put(XML_NEXT_TYPE, nextType);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "PauseCommand";
	}

	@Override
	public String getName() {
		return "Pause";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCategory() {
		return EHEventRecorder.UserMacroCategoryName;
	}

	@Override
	public String getCategoryID() {
		return EHEventRecorder.UserMacroCategoryID;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(XML_PREV)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			prev = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_NEXT)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			next = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_PAUSE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			pause = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_RANGE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			range = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_PREV_TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			prevType = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_NEXT_TYPE)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			nextType = textNode.getTextContent();
		}
		if (range == null && pause != 0) {
			range = getRange(pause);
		}
	}

}
