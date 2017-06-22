package analyzer.extension;

import java.util.Date;

public interface StuckInterval extends Comparable<StuckInterval>{
	
	public void setParticipant(String participant);
	public Date getDate();
	public void setDate(Date date);
	public String getBarrierType();
	public void setBarrierType(String barrierType);
	public String getSurmountability();
	public void setSurmountability(String surmountability);
	public String toText();
}
