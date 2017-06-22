package difficultyPrediction;

import analyzer.AnAnalyzer;
import analyzer.AnAnalyzerParameters;
import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AClassificationParametersAR implements ExecutableCommand{
	public Object execute(Object theFrame) {
//		ObjectEditor.setAttribute(APredictionParameters.class,  AttributeNames.LAYOUT, AttributeNames.GRID_BAG_LAYOUT);
//		ObjectEditor.setPropertyAttribute(APredictionParameters.class, "ARFFFileName", AttributeNames.STRETCHABLE_BY_PARENT, true);
		
		ObjectEditor.setPropertyAttribute(AClassificationParameters.class, "ClassifierSpecification", AttributeNames.LABEL_WIDTH, 100);
		ObjectEditor.setPropertyAttribute(AClassificationParameters.class, "OversampleSpecification", AttributeNames.LABEL_WIDTH, 100);

		ObjectEditor.setPropertyAttribute(AClassificationParameters.class, "ARFFFileName", AttributeNames.LABEL_WIDTH, 100);

		ObjectEditor.setPropertyAttribute(APredictionParameters.class, "ARFFFileName", AttributeNames.CONTAINER_WIDTH, 425);
		ObjectEditor.setPropertyAttribute(APredictionParameters.class, "ARFFFileName", AttributeNames.CONTAINER_HEIGHT, 25);

//		ObjectEditor.setAttribute(APredictionParameters.class,  AttributeNames.STRETCHABLE_BY_PARENT, true);


////		ObjectEditor.setAttribute(AnAnalyzer.class,  AttributeNames.STRETCHABLE_BY_PARENT, true);
////		ObjectEditor.setAttribute(AnAnalyzerParameters.class,  "Participants", AttributeNames.GRID_BAG_LAYOUT);
//		// controlling container size as component width and height are controlled
//		// and automatic computation fails
//		ObjectEditor.setPropertyAttribute(APrediction.class, "Participants", AttributeNames.LABELLED, false);
//		ObjectEditor.setPropertyAttribute(AnAnalyzerParameters.class, "CurrentParticipant", AttributeNames.LABELLED, false);
//
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "AnalyzerParameters", AttributeNames.CONTAINER_HEIGHT, 100);
//
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "SegmentLength", AttributeNames.STRETCHABLE_BY_PARENT, false);
//
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "LogsFolder", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "DownloadFolder", AttributeNames.CONTAINER_BACKGROUND, Color.PINK);
//
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "TextEditor", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);
////		ObjectEditor.setPropertyAttribute(AnAnalyzer.class, "Diff", AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IS_LEFT);


		return null;
	}
}
