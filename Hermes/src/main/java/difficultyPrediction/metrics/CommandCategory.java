package difficultyPrediction.metrics;
/*
 * We are assuming all commands are mapped into these 6 categories.
 * Perhaps these whould be called one, two, three, or we should have OTHER!, OTHER2, OTHER_3 at
 * some point
 */
//public enum CommandCategory {
//	DEBUG,
////	SEARCH,
//	EDIT_OR_INSERT,
//	NAVIGATION,
//	FOCUS,
//	REMOVE,
//	// we may want a very different set of features for which the names above have no meaning
//	DEBUG_TIME,
//	EDIT_OR_INSERT_TIME,
//	NAVIGATION_TIME,
//	FOCUS_TIME,
//	REMOVE_TIME,
//	EXEEPTIONS_PER_RUN,
//	OTHER_1,
//	OTHER_2,
//	OTHER_3,
//	OTHER_4,
//	OTHER_5,
//	OTHER_6,
//	OTHER_7,
//	OTHER_8,
//	OTHER_9,
//	OTHER
//
//}
public enum CommandCategory {
	NAVIGATION,
	DEBUG,
//	SEARCH,
	FOCUS,
	EDIT,
	REMOVE, // delete
	INSERT,
	WEB_LINK_TIMES,
	REMOVE_CLASS,
	// we may want a very different set of features for which the names above have no meaning
	// are these features or command categories
	COMMAND_RATE,
	DEBUG_RATE,
	EDIT_RATE,
	INSERT_RATE,
	NAVIGATION_RATE,
	FOCUS_RATE,
	REMOVE_RATE,
	EXCEPTIONS_PER_RUN,
	WEB_VISIT, 
	WEB_SEARCH_LENGTH,
	// start Nils
	COPY,
	PASTE,
	LONGEST_DELETE, // longest delete, a feature not category
	LONGEST_INSERT, // longest insert, a feature not category
	COMPILE,
	// end Nils	
	UNRESOLVED_EXCEPTIONS,
	NO_GROWTH_CHECKS,
	NUM_PAUSES_1,
	NUM_PAUSES_2,
	NUM_PAUSES_3,
	NUM_PAUSES_4,
	NUM_PAUSES_5,
	NUM_PAUSES_6,
	NUM_PAUSES_7,
	NUM_PAUSES_8,
	NUM_PAUSES_9,
	NUM_PAUSES_10,
	// localcheck
	PAUSE,
	LOCAL_CHECK,
	OTHER_1,
	OTHER_2,
	OTHER_3,
	OTHER_4,
	OTHER_5,
	OTHER_6,
	OTHER_7,
	OTHER_8,
	OTHER_9,
	OTHER

}
