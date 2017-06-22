package difficultyPrediction.eventAggregation;

import java.util.ArrayList;

import fluorite.commands.EHICommand;

public class EventAggregatorArray extends ArrayList<EHICommand>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void addEvents(EHICommand event) {
		this.add(event);
	}
}
