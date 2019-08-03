package difficultyPrediction.web;

public class PageVisit {
	
	public String title;
	public int numVisits;
	public String url;
	public PageVisit(String aTitle, int aNumVisits, String aURL) {
		title = aTitle;
		numVisits = aNumVisits;
		url = aURL;
	}
	public String toString() {
		return numVisits +  " " + title + " " + url;
	}
	

}
