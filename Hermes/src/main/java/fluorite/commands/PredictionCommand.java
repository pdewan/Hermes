package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;
import fluorite.model.StatusConsts;

public class PredictionCommand extends EHAbstractCommand{
	
	public enum PredictionType
	{
		MakingProgress, Indeterminate, HavingDifficulty
	}
	
	public PredictionCommand(PredictionType predictionType)
	{
		mPredictionType = predictionType;
	}
	
	public PredictionCommand() {
		// TODO Auto-generated constructor stub
	}

	PredictionType mPredictionType;

	public PredictionType getPredictionType() {
		return mPredictionType;
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		if ((attr = commandElement.getAttributeNode("prediction_type")) != null) {
			if(attr.getValue().equals("MakingProgress"))
			{
				mPredictionType = PredictionType.MakingProgress;
			}
//			if(attr.getValue().equals("Indeterminate"))
			if(attr.getValue().equals(StatusConsts.INDETERMINATE))

			{
				mPredictionType = PredictionType.Indeterminate;
			}
			if(attr.getValue().equals("HavingDifficulty"))
			{
				mPredictionType = PredictionType.HavingDifficulty;
			}
		}
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("prediction_type", mPredictionType.toString());
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return "PredictionCommand";
	}

	@Override
	public String getName() {
		return mPredictionType.toString();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
