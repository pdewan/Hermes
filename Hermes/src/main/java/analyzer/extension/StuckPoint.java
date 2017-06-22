package analyzer.extension;

import java.util.Date;

public interface StuckPoint extends Comparable<StuckPoint>{
	public Date getDate();
	public void setDate(Date date);
	public String getType();
	public void setType(String type);
	public String toText();
	
}
