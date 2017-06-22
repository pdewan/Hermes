package difficultyPrediction;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JFileChooser;

import config.HelperConfigurationManagerFactory;
import difficultyPrediction.metrics.CommandClassificationSchemeName;
import difficultyPrediction.predictionManagement.ClassifierSpecification;
import difficultyPrediction.predictionManagement.OversampleSpecification;
import util.annotations.Column;
import util.annotations.ComponentWidth;
import util.annotations.Label;
import util.annotations.Row;
import util.annotations.Visible;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
//import bus.uigen.hermes.HermesObjectEditorProxy;
//import bus.uigen.models.AFileSetterModel;
//import bus.uigen.models.FileSetterModel;

public class AClassificationParameters implements ClassificationParameters {
	static ClassificationParameters instance;	
	ClassifierSpecification classifierSpecification;
	OversampleSpecification oversampleSpecification;

	String arffFileName;
	static OEFrame classificationFrame;
//	static Object classificationFrame;

	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	// FileSetterModel arffFileName;
	public AClassificationParameters() {
		// arffFileName = new AFileSetterModel(JFileChooser.FILES_ONLY);
		// arffFileName.setText(HelperConfigurationManagerFactory.getSingleton().getARFFFileName());
		arffFileName = HelperConfigurationManagerFactory.getSingleton()
				.getARFFFileName();
	}

	

	@Visible(false)
	public static ClassificationParameters getInstance() {
		if (instance == null) {
			instance = new AClassificationParameters();
		}
		return instance;
	}

	public static void createUI() {
		// OEFrame predictionFrame = ObjectEditor.edit(getInstance())
		if (classificationFrame != null) {
//			instance.reset();
			return;

		}
		classificationFrame = ObjectEditor.edit(getInstance());

		classificationFrame.setSize(450, 120);
		
//		classificationFrame = HermesObjectEditorProxy.edit(getInstance(), 450, 120);

	}

	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.addPropertyChangeListener(arg0);
	}

	// @Visible(false)
	@Row(0)
	@Column(0)
	@ComponentWidth(300)
	@Label("Model:")
	// @Override
	// public FileSetterModel getARFFFileName() {
	// // if (arffFileName == null) {
	// // arffFileName =
	// HelperConfigurationManagerFactory.getSingleton().getARFFFileName();
	// // }
	// return arffFileName;
	// }
	@Override
	public String getARFFFileName() {
		// if (arffFileName == null) {
		// arffFileName =
		// HelperConfigurationManagerFactory.getSingleton().getARFFFileName();
		// }
		return arffFileName;
	}

	// @Visible(false)
	@Row(1)
	@Column(0)
	@ComponentWidth(100)
	@Label("Classifier:")
	@Override
	public ClassifierSpecification getClassifierSpecification() {
		if (classifierSpecification == null) {
			classifierSpecification = HelperConfigurationManagerFactory
					.getSingleton().getClassifierSpecification();
		}
		return classifierSpecification;
	}

	// @Visible(false)
	@Row(1)
	@Column(1)
	@ComponentWidth(100)
	@Label("Oversampling:")
	@Override
	public OversampleSpecification getOversampleSpecification() {
		if (oversampleSpecification == null) {
			oversampleSpecification = HelperConfigurationManagerFactory
					.getSingleton().getOversampleSpecification();
		}
		return oversampleSpecification;
	}

	@Override
	public void setClassifierSpecification(ClassifierSpecification newVal) {
		classifierSpecification = newVal;
	}

	public void setOversampleSpecification(OversampleSpecification newVal) {
		oversampleSpecification = newVal;
	}
	@Override
	public void setARFFFileName (String newVal) {
		arffFileName = newVal;
	}
	
	public static void main (String[] anArgs) {
		ClassificationParameters aClassificationParameters = AClassificationParameters.getInstance();
		AClassificationParameters.createUI();
	}
	
}
