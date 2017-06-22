package analyzer.extension;

import java.util.Date;

public class AStuckInterval implements StuckInterval{
	@Override
	public String toString() {
		return "AStuckInterval [participant=" + participant + ", date=" + date
				+ ", barrierType=" + barrierType + ", surmountability="
				+ surmountability + "]";
	}
	private String participant;
	private Date date;
	private String barrierType;
	private String surmountability;
	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getBarrierType() {
		return barrierType;
	}
	public void setBarrierType(String barrierType) {
		this.barrierType = barrierType;
	}
	public String getSurmountability() {
		return surmountability;
	}
	public void setSurmountability(String surmountability) {
		this.surmountability = surmountability;
	}
	@Override
	public int compareTo(StuckInterval o) {
		return this.date.compareTo(o.getDate());
		
	}
	@Override
	public String toText() {
		return this.barrierType
				+", "+this.surmountability;
		
	}
}
