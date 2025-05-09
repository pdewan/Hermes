package fluorite.commands;

import java.util.Map;

public interface ITreeDataCommand extends EHICommand {

	Object getRootElement();
	Object[] getChildren(Object parentElement);
	
	String getTagName(Object element);
	Map<String, String> getAttrMap(Object element);
	
}