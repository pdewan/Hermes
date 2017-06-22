package analyzer;

import java.util.Date;

public interface BrowseHistoryListener {
	void newBrowseLine(String aLine);
	void newBrowseEntries(Date aDate, String aSearchString, String aURL);
	void finishedBrowserLines();


}
