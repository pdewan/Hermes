package difficultyPrediction;

import java.sql.Date;

public interface StatusInformation {

	public abstract double getEditRatio();

	public abstract double getDebugRatio();

	public abstract double getRemoveRatio();

	public abstract double getNavigationRatio();

	public abstract double getFocusRatio();

	public abstract String getPredictedClass();

	public abstract String getPrediction();

	public abstract Date getTimeStamp();

	public abstract StatusKind getStatusKind();

	public abstract String getUserName();

	public abstract String getUserId();

	public abstract void setEditRatio(double editRatio);

	public abstract void setDebugRatio(double debugRatio);

	public abstract void setRemoveRatio(double removeRatio);

	public abstract void setNavigationRatio(double navigationRatio);

	public abstract void setFocusRatio(double focusRatio);

	public abstract void setPredictedClass(String predictedClass);

	public abstract void setPrediction(String prediction);

	public abstract void setTimeStamp(Date timeStamp);

	public abstract void setStatusKind(StatusKind statusKind);

	public abstract void setUserName(String userName);

	public abstract void setUserId(String userId);

	// can be pasted onto any class that needs to be JSON serialized
	public abstract String toString();

}