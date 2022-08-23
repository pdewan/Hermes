package difficultyPrediction.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AComprehensiveCategories extends AnA1WebCommandCategories{
	 private CommandCategory[] relevantComprehensiveCategores;

	 private CommandCategory[] additionsToRelevantComprehensiveCategores = {
			CommandCategory.COPY,
		 	CommandCategory.PASTE,
		 	CommandCategory.COMPILE,
		 	CommandCategory.LONGEST_DELETE,
		    CommandCategory.LONGEST_INSERT,
		    CommandCategory.INSERT_RATE,
		    CommandCategory.REMOVE_RATE,
		    CommandCategory.FOCUS_RATE,
		    CommandCategory.NAVIGATION_RATE,
		    CommandCategory.WEB_SEARCH_LENGTH
	 };
	 private CommandName[] deletionsToInsertCategory = { 
				CommandName.CopyCommand,          
				CommandName.PasteCommand
		};
	 private CommandName[] newCategories = { 
				CommandName.CopyCommand,          
				CommandName.PasteCommand,
				CommandName.PauseCommand,
				CommandName.ExceptionCommand,
				CommandName.ConsoleOutput,
				CommandName.LocalChecksRawCommand


				
		};
	 protected void initializeCommandCategoryNames() {
			super.initializeCommandCategoryNames();
			
			categoriesToNames[CommandCategory.COPY.ordinal()] = "copyPercentage";
			categoriesToNames[CommandCategory.PASTE.ordinal()] = "pastePercentage";
			categoriesToNames[CommandCategory.COMPILE.ordinal()] = "compilePercentage";
			categoriesToNames[CommandCategory.LONGEST_DELETE.ordinal()] = "longestDelete";
			categoriesToNames[CommandCategory.LONGEST_INSERT.ordinal()] = "longestInsert";
			categoriesToNames[CommandCategory.INSERT_RATE.ordinal()] = "insertRate";
			categoriesToNames[CommandCategory.REMOVE_RATE.ordinal()] = "deleteRate";
			categoriesToNames[CommandCategory.DEBUG_RATE.ordinal()] = "debugRate";
			categoriesToNames[CommandCategory.NAVIGATION_RATE.ordinal()] = "navigationRate";
			categoriesToNames[CommandCategory.FOCUS_RATE.ordinal()] = "focusRate";
			categoriesToNames[CommandCategory.WEB_SEARCH_LENGTH.ordinal()] = "webSearchLength";
			categoriesToNames[CommandCategory.UNRESOLVED_EXCEPTIONS.ordinal()] = "unresolvedExceptions";
			categoriesToNames[CommandCategory.NO_GROWTH_CHECKS.ordinal()] = "noGrowthChecks";


			categoriesToNames[CommandCategory.NUM_PAUSES_1.ordinal()] = "numPauses1";


//			categoriesToNames[CommandCategory.EXCEPTIONS_PER_RUN.ordinal()] = "exceptions";


//			categoriesToNames[CommandCategory.REMOVE.ordinal()] = "removeTextPercentage";

		}
	 @Override
	    public CommandCategory[] getOrderedRelevantCommandCategories() {
		 	if (relevantComprehensiveCategores == null) {
		 		CommandCategory[] anA1WebRelevantCategories = super.getOrderedRelevantCommandCategories();
		 		relevantComprehensiveCategores = new CommandCategory[anA1WebRelevantCategories.length + additionsToRelevantComprehensiveCategores.length];
		 		for (int i = 0; i < anA1WebRelevantCategories.length; i++) {
		 			relevantComprehensiveCategores[i] = anA1WebRelevantCategories[i];
		 		}
		 		for (int i = 0; i < additionsToRelevantComprehensiveCategores.length; i++) {
		 			relevantComprehensiveCategores[anA1WebRelevantCategories.length +i] = 
		 				additionsToRelevantComprehensiveCategores[i];
		 		}
		 	}
			return relevantComprehensiveCategores;
		}
	 
	
	 @Override
		protected CommandName[] insertCategory() {
			List<CommandName> resultList = new ArrayList();
			resultList.addAll(Arrays.asList(super.insertCategory()));
			resultList.removeAll(Arrays.asList(deletionsToInsertCategory));
			return resultList.toArray(new CommandName[] {});
		}
	 
	 protected void mapCategories() {
		 super.mapCategories();
		 map(CommandName.CopyCommand, CommandCategory.COPY);
		 map(CommandName.PasteCommand, CommandCategory.PASTE);


		 
		}
}
