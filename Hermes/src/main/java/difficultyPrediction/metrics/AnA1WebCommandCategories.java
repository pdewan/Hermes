package difficultyPrediction.metrics;

public class AnA1WebCommandCategories extends AnA1CommandCategories{
	 private CommandCategory[] relevantCategoresA1Web ;
	
	 @Override
	    public CommandCategory[] getOrderedRelevantCommandCategories() {
		 	if (relevantCategoresA1Web == null) {
		 		CommandCategory[] anA1RelevantCategories = super.getOrderedRelevantCommandCategories();
		 		relevantCategoresA1Web = new CommandCategory[anA1RelevantCategories.length + 1];
		 		for (int i = 0; i < anA1RelevantCategories.length; i++) {
		 			relevantCategoresA1Web[i] = anA1RelevantCategories[i];
		 		}
		 		relevantCategoresA1Web[anA1RelevantCategories.length] = CommandCategory.WEB_LINK_TIMES;
		 	}
			return relevantCategoresA1Web;
		}
//	 protected void mapCategories() {
//		 super.mapCategories();
//		 map(aCommand, aFeatureName);
//		}
}
