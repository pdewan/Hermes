package analyzer;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AnAnalyzerAR implements ExecutableCommand{
	public Object execute(Object theFrame) {


//		ObjectEditor.setAttribute(AnAnalyzer.class,  AttributeNames.STRETCHABLE_BY_PARENT, true);
		ObjectEditor.setAttribute(AnAnalyzer.class,  AttributeNames.LAYOUT, AttributeNames.GRID_BAG_LAYOUT);
		// controlling container size as component width and height are controlled
		// and automatic computation fails
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.STRETCHABLE_BY_PARENT, true);

//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.CONTAINER_WIDTH, 250);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.CONTAINER_HEIGHT, 100);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.CONTAINER_HEIGHT, 168);
		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.CONTAINER_HEIGHT, 260);


//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "SegmentLength", AttributeNames.STRETCHABLE_BY_PARENT, false);

//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "LogsFolder", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "DownloadFolder", AttributeNames.CONTAINER_BACKGROUND, Color.PINK);

//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "TextEditor", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "Diff", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);


		return null;
	}
}
