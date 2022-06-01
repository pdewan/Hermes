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
	REMOVE,
	INSERT,
	WEB_LINK_TIMES,
	REMOVE_CLASS,
	// we may want a very different set of features for which the names above have no meaning
	COMMAND_RATE,
	DEBUG_RATE,
	EDIT_RATE,
	INSERT_RATE,
	NAVIGATION_RATE,
	FOCUS_RATE,
	REMOVE_RATE,
	EXCEPTIONS_PER_RUN,
	WEB_VISIT,
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
