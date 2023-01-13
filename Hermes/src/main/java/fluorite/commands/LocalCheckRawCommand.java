package fluorite.commands;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fluorite.model.EHEventRecorder;

public class LocalCheckRawCommand extends AbstractCommand implements EHICommand{
	protected String csvRow;
//	final static String FAIL_DECLINE = "fail_decline";
//	final static String FAIL_GROWTH = "fail_growth";
	final static String CSV_ROW = "CSVRow";

	
	public LocalCheckRawCommand() {}
	
	public LocalCheckRawCommand(String aCSVRow) {
		csvRow = aCSVRow;
//		csvRow = "Local Checks Raw";

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
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(CSV_ROW, csvRow);
		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "LocalChecksRawCommand";
	}

	@Override
	public String getName() {
		return "LocalChecksRaw";
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
	
	public String getCSVRow() {
		return csvRow;
	}
	
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		NodeList nodeList = null;

		if ((nodeList = commandElement.getElementsByTagName(CSV_ROW)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			csvRow = textNode.getTextContent();
		}
		
	}
}
