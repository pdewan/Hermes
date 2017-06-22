package analyzer.extension;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ACSVParser implements CSVParser{

	private BufferedReader stream;
	private char delimiter;

	public ACSVParser() {
		delimiter=',';

	}

	public void start(String filename) {
		try {
			stream=new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stop() {
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getNextLine() {
		try {
			return stream.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public int read() {
		try {
			return stream.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}

	public String readTillNextDelimiter() {
		StringBuilder read=new StringBuilder();

		int next=read();
		
		//read 1st char and if it is a ','. Then, ignore it
		if(next != this.delimiter)
			read.append((char) next);
		
		while ((next= read()) != -1 && next != this.delimiter) {
			//first the next char is a , ignore it if it is the first one
			read.append((char) next);
			
		} 

		return (next==-1 || (read.length()==2 && next==44))? null:read.toString();
	}

	/*Settings Stuff*/
	public void setDelimiter(char newDelimiter) {
		this.delimiter=newDelimiter;

	}
	
	public static void main(String[] args) {
//		CSVParser p=new ACSVParser();
//		
//		p.start("data/GroundTruth/Stuckpoints.csv");
//		
//		String r;
//		while((r=p.readTillNextDelimiter()) != null) {
//			System.out.println(r);
//			
//		}
		
		try {
			System.out.println(new SimpleDateFormat("hh:mm a").parse("10:24 PM"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
