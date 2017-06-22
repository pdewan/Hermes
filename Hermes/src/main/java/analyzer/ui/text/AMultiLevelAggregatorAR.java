package analyzer.ui.text;


import java.awt.GridBagConstraints;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AMultiLevelAggregatorAR implements ExecutableCommand{
	public Object execute(Object theFrame) {


		ObjectEditor.setAttribute(AMultiLevelAggregator.class,  AttributeNames.STRETCHABLE_BY_PARENT, true);
		ObjectEditor.setAttribute(AMultiLevelAggregator.class,  AttributeNames.LAYOUT, AttributeNames.GRID_BAG_LAYOUT);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "SegmentLength", AttributeNames.STRETCHABLE_BY_PARENT, false);

		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "WebLinks", AttributeNames.CONTAINER_WIDTH, 300);
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "WebLinks", AttributeNames.CONTAINER_HEIGHT, 40);

		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "*", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IN_BORDER);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "DownloadFolder", AttributeNames.CONTAINER_BACKGROUND, Color.PINK);

//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "TextEditor", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
//		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "Diff", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
//		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.SCROLLED, true);			
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.SCROLLED, true);

		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.COMPONENT_WIDTH, 650);
//		ObjectEditor.setPropertyAttribute(AMainProjectStepper.class, "Transcript", AttributeNames.COMPONENT_HEIGHT, 240);
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.ADD_ANCHOR_CONSTRAINT, GridBagConstraints.PAGE_END);
//		ObjectEditor.setPropertyAttribute(AMainProjectStepper.class, "Transcript", AttributeNames.ADD_WEIGHT_Y_CONSTRAINT, 1.0);
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.ADD_FILL_CONSTRAINT, GridBagConstraints.BOTH);
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.STRETCHABLE_BY_PARENT, true);
		ObjectEditor.setPropertyAttribute(AMultiLevelAggregator.class, "Segment", AttributeNames.ADD_WEIGHT_Y_CONSTRAINT, 1.0);

		return null;
	}
}
