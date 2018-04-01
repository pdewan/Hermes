package analyzer;

import java.util.Date;
import java.util.List;

import fluorite.commands.WebVisitCommand;

public interface BrowseHistoryListener {
	void newBrowseLine(String aLine);
	void newBrowseEntries(Date aDate, String aSearchString, String aURL);
	void newBrowserCommands(List<WebVisitCommand> aCommands);
	void finishedBrowserLines();


}
