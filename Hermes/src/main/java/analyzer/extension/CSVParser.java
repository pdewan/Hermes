package analyzer.extension;

public interface CSVParser {

	public void start(String filename);

	public void stop();

	public String getNextLine();

	public int read();

	public String readTillNextDelimiter();

	public void setDelimiter(char newDelimiter);
	
}
