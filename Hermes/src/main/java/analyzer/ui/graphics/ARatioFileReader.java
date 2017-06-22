package analyzer.ui.graphics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import analyzer.AWebLink;
import analyzer.WebLink;
import util.annotations.Column;
import util.annotations.ComponentWidth;
import util.annotations.Row;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;

public class ARatioFileReader implements RatioFileReader {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private RatioFileComponents ratioFeatures = new ARatioFileComponents();
	List<RatioFileComponents> ratioFeaturesList;
	

	private String path = "";
	private JFileChooser fileChooser;

	@Row(1)
	@Column(0)
	@ComponentWidth(120)
	public void chooseFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showOpenDialog(fileChooser);
		File file = fileChooser.getSelectedFile();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			path = file.getAbsolutePath(); // finds the file path if a file is
											// chosen.
		}
		if (path != null && path != "") {
			propertyChangeSupport.firePropertyChange("file", "", path);
			readFile(path);
		}
	}

	@Row(1)
	@Column(1)
	@ComponentWidth(350)
	public String getPath() {
		return path;
	}

	public void readFile(String fileName) {
		ratioFeaturesList = new ArrayList();
		BufferedReader br = null;
		String row = "";

		try {
			br = new BufferedReader(new FileReader(fileName));
			propertyChangeSupport.firePropertyChange(START_RATIOS,
					null, "marker");
			while ((row = br.readLine()) != null) {
				readRow(row);
			}
			propertyChangeSupport.firePropertyChange(END_RATIOS,
					null, "marker");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void readRow(String row) {
		// TODO: fix oldRatioFeatures
		RatioFeatures oldRatioFeatures = new ARatioFeatures();
		ratioFeatures = new ARatioFileComponents(); // creating a new feature each time for text UI
		String[] parts = row.split(",");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy H:mm:ss");
		ratioFeatures.setEditRatio(Double.parseDouble(parts[1].trim()));
		ratioFeatures.setInsertionRatio(Double.parseDouble(parts[2].trim()));
		ratioFeatures.setDeletionRatio(Double.parseDouble(parts[3].trim()));
		ratioFeatures.setDebugRatio(Double.parseDouble(parts[4].trim()));
		ratioFeatures.setNavigationRatio(Double.parseDouble(parts[5].trim()));
		ratioFeatures.setFocusRatio(Double.parseDouble(parts[6].trim()));
		ratioFeatures.setRemoveRatio(Double.parseDouble(parts[7].trim()));
		ratioFeatures.setPredictedStatus(Integer.parseInt(parts[8].trim()));
		ratioFeatures.setActualStatus(Integer.parseInt(parts[9].trim()));
		ratioFeatures.setDifficultyType(parts[10].trim());
		if (parts[11].equalsIgnoreCase(" null")) {
			ratioFeatures.setWebLinkList(null);
		} else {
			List <WebLink> list = new ArrayList<WebLink>();
			for (int i = 10; i < parts.length; i++) {
				String[] searchAndUrl = parts[i].split("\t");
				if (searchAndUrl.length > 1) {
					list.add(new AWebLink(searchAndUrl[0], searchAndUrl[1]));
				} else {
					// System.out.println(searchAndUrl[0]);
				}
			}
			ratioFeatures.setWebLinkList(list);
		}
		try {
			ratioFeatures.setSavedTimeStamp(format.parse(parts[0]).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		ratioFeaturesList.add(ratioFeatures);
		propertyChangeSupport.firePropertyChange(NEW_RATIO,
//		propertyChangeSupport.firePropertyChange("newRatioFeatures",
				oldRatioFeatures, ratioFeatures);
	}
	
//	public List<RatioFileComponents> getRatioFeaturesList() {
//		return ratioFeaturesList;
//	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.addPropertyChangeListener(aListener);
	}

}