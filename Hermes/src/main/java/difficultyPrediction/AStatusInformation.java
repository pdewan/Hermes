package difficultyPrediction;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;

import org.codehaus.jackson.map.ObjectMapper;

public class AStatusInformation implements StatusInformation {
	public double editRatio;
	public double debugRatio;
	public double removeRatio;
	public double navigationRatio;
	public double focusRatio;
	public String predictedClass;
	public String prediction;
	public Date timeStamp;
	public StatusKind statusKind;
	public String userName;
	public String userId;
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getEditRatio()
	 */
	@Override
	public double getEditRatio() {
		return editRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getDebugRatio()
	 */
	@Override
	public double getDebugRatio() {
		return debugRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getRemoveRatio()
	 */
	@Override
	public double getRemoveRatio() {
		return removeRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getNavigationRatio()
	 */
	@Override
	public double getNavigationRatio() {
		return navigationRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getFocusRatio()
	 */
	@Override
	public double getFocusRatio() {
		return focusRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getPredictedClass()
	 */
	@Override
	public String getPredictedClass() {
		return predictedClass;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getPrediction()
	 */
	@Override
	public String getPrediction() {
		return prediction;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getTimeStamp()
	 */
	@Override
	public Date getTimeStamp() {
		return timeStamp;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getStatusKind()
	 */
	@Override
	public StatusKind getStatusKind() {
		return statusKind;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setEditRatio(double)
	 */
	@Override
	public void setEditRatio(double editRatio) {
		this.editRatio = editRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setDebugRatio(double)
	 */
	@Override
	public void setDebugRatio(double debugRatio) {
		this.debugRatio = debugRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setRemoveRatio(double)
	 */
	@Override
	public void setRemoveRatio(double removeRatio) {
		this.removeRatio = removeRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setNavigationRatio(double)
	 */
	@Override
	public void setNavigationRatio(double navigationRatio) {
		this.navigationRatio = navigationRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setFocusRatio(double)
	 */
	@Override
	public void setFocusRatio(double focusRatio) {
		this.focusRatio = focusRatio;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setPredictedClass(java.lang.String)
	 */
	@Override
	public void setPredictedClass(String predictedClass) {
		this.predictedClass = predictedClass;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setPrediction(java.lang.String)
	 */
	@Override
	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setTimeStamp(java.sql.Date)
	 */
	@Override
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setStatusKind(difficultyPrediction.StatusKind)
	 */
	@Override
	public void setStatusKind(StatusKind statusKind) {
		this.statusKind = statusKind;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setUserName(java.lang.String)
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#setUserId(java.lang.String)
	 */
	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}
	ObjectMapper mapper = new ObjectMapper();

	// can be pasted onto any class that needs to be JSON serialized
	/* (non-Javadoc)
	 * @see difficultyPrediction.StatusInformation#toString()
	 */
	@Override
	public String toString() {
		try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            System.out.println("Unable to write .json file");
            return "{}";
        }
	}
	
}
