package difficultyPrediction.metrics;

public class AnA0WebCommandCategories extends AnA0CommandCategories{
	 CommandCategory[] relevantCategoresA0Web ;
	
	 @Override
	    public CommandCategory[] getOrderedRelevantCommandCategories() {
		 	if (relevantCategoresA0Web == null) {
		 		CommandCategory[] anA0RelevantCategories = super.getOrderedRelevantCommandCategories();
		 		relevantCategoresA0Web = new CommandCategory[anA0RelevantCategories.length + 1];
		 		for (int i = 0; i < anA0RelevantCategories.length; i++) {
		 			relevantCategoresA0Web[i] = anA0RelevantCategories[i];
		 		}
		 		relevantCategoresA0Web[anA0RelevantCategories.length] = CommandCategory.WEB_LINK_TIMES;
		 	}
			return relevantCategoresA0Web;
		}

}
