package analyzer.logAnalyzer;

public class InsertsAndDeletes {
	public int numberOfInserts = 0;
	public int insertLength = 0;
	public int numberOfDeletes = 0;
	public int deleteLength = 0;
	public void makeInvalid() {
		numberOfInserts = -1;
		insertLength = -1;
		numberOfDeletes = -1;
		deleteLength = -1;
	}
}
