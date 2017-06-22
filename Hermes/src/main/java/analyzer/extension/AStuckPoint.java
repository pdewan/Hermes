package analyzer.extension;

import java.util.Date;

public class AStuckPoint implements StuckPoint{
	@Override
	public String toString() {
		return "AStuckPoint [date=" + date + ", type=" + type + "]";
	}
	private Date date;
	private String type;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int compareTo(StuckPoint o) {
		return this.date.compareTo(o.getDate());
		
	}
	@Override
	public String toText() {
		return this.type;

	}
	
	
	
}
