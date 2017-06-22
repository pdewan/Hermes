package difficultyPrediction.eventAggregation;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import fluorite.commands.EHICommand;
public class AnEventAggregatorDetails {
	
	public List<EHICommand> actions;
	public long startTimeStamp;
	
	public AnEventAggregatorDetails () {
		actions = new ArrayList<EHICommand>();
	}
	
	public AnEventAggregatorDetails(List<EHICommand> actions) {
		this.actions = new ArrayList<EHICommand>();
		for(EHICommand event : actions) {
			this.actions.add(event);
		}
	}
	ObjectMapper mapper = new ObjectMapper();

	// can be pasted onto any class that needs to be JSON serialized
		public String toString() {
//			try {
//	            StringWriter writer = new StringWriter();
//	            mapper.writeValue(writer, this);
//	            return writer.toString();
//	        } catch (Exception e) {
//	            System.out.println("Unable to write .json file");
//	            
//	            return super.toString();
//	        }
			return "EventAggregation (" + startTimeStamp + ", " + actions.toString() + ")";
		}
}
