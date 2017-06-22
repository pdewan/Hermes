package difficultyPrediction.featureExtraction;

import java.util.List;

import analyzer.WebLink;

public interface WebLinkListener {
	void newWebLink(WebLink aWebLink);
	void newWebLinks(List<WebLink> aWebLinks);

}
