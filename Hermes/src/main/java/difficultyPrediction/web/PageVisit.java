package difficultyPrediction.web;

public class PageVisit {
	
	public String title;
	public int numVisits;
	public String url;
	public long unixTime;
	public PageVisit(long aUnixTime, String aTitle, int aNumVisits, String aURL) {
		unixTime = aUnixTime;
		title = aTitle;
		numVisits = aNumVisits;
		url = aURL;
	}
	public String toString() {
		return unixTime + " " + numVisits +  " " + title + " " + url;
	}
	

}
