package difficultyPrediction.web;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.util.FileUtils;


/**
 Adapted from http://www.javaworkspace.com/connectdatabase/connectSQLite.do
 Date: 09/25/2012

 Download sqlite-jdbc-<>.jar from http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC, and
 compile: javac GetChromiumHistory.java
 run:     java -classpath ".:sqlite-jdbc-3.7.2.jar" GetChromiumHistory
*/
public class AChromeHistoryAccessor {
	
	public static final long CHROME_MINUS_UNIX = 11644473600000000L;
	/**
	 * @author www.javaworkspace.com
	 * 
	 */
	
	    public static void main (String[] args) 
	    {
	    	final String HISTORY_FILE = "C:\\Users\\dewan\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\History";
	    	final String HISTORY_COPY = "D:\\History";

	    	final String JDBC_FILE = "jdbc:sqlite:" + HISTORY_COPY;

	    Connection connection = null;
	    ResultSet resultSet = null;
	    Statement statement = null;

	    try 
	        {
	    	File aSourceFile = new File(HISTORY_FILE);
	    	if (!aSourceFile.exists()) {
	    		System.exit(0);
	    	}
	    	long aTimeStamp = aSourceFile.lastModified();
	    	Date aDate = new Date(aTimeStamp);
	    	System.out.println ("History File Timestamp:" + aTimeStamp + " date:" + aDate);

	    	File aTargetFile = new File(HISTORY_COPY);
	    	if (aTargetFile.exists()) {
	    		aTargetFile.delete();
	    	}
	    	Path aFrom = Paths.get(aSourceFile.getAbsolutePath());
	    	Path aTo = Paths.get(aTargetFile.getAbsolutePath());
	    	CopyOption[] options = new CopyOption[]{
	    	  StandardCopyOption.REPLACE_EXISTING,
	    	  StandardCopyOption.COPY_ATTRIBUTES
	    	}; 
	    	java.nio.file.Files.copy(aFrom, aTo, options);
	    	
	    	
//	    	File aFile = new File(HISTORY_FILE);
//	    	boolean anExists = aFile.exists();
	        Class.forName ("org.sqlite.JDBC");
	        connection = DriverManager
	            .getConnection (
//	            		"jdbc:sqlite:/home/username/.config/chromium/Default/History"
	            		JDBC_FILE
	            		);
	        statement = connection.createStatement ();
	        resultSet = statement
//	            .executeQuery ("SELECT * FROM urls where visit_count > 0");
//            .executeQuery ("SELECT title, visit_count, last_visit_time, URL FROM urls where visit_count > 0");
//            .executeQuery ("SELECT title, visit_count, datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch'), URL FROM urls where visit_count > 0");
//	              .executeQuery ("SELECT title, visit_count, datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01', 'localtime')), URL FROM urls where visit_count > 0");
//	              .executeQuery ("SELECT title, visit_count, datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch'), URL FROM urls where visit_count > 0");
	              .executeQuery ("SELECT title, visit_count, last_visit_time, "
	              		+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where visit_count > 0");


	        while (resultSet.next ()) 
	            {
//	        	String aTimeString = resultSet.getString("last_visit_time");
	        	String aTimeString = resultSet.getString("datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime')");
//	        	2019-07-29 11:04:26
	        	System.out.println ("aChrome date:" + aTimeString);
	        	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            Date date = df.parse(aTimeString);
	            System.out.println(date);
	            long aUnixTime = date.getTime();
	            String aChromeTimeMicrosecsString = resultSet.getString("last_visit_time");
	            long aChromeTimeMicrosecs = Long.parseLong(aChromeTimeMicrosecsString);
	            long aUnixTimeMicrosecs = aUnixTime*1000;
	            long aTimeDifference = aChromeTimeMicrosecs - aUnixTimeMicrosecs;
	            System.out.println("Chrrome - Unix:" + aTimeDifference);
	            Date aParsedDate = new Date(aUnixTime);
	            System.out.println("Date1" + aParsedDate);
	            long aCalculatedUnixTime = (aChromeTimeMicrosecs - CHROME_MINUS_UNIX)/1000;
	            aParsedDate.setTime(aCalculatedUnixTime);
	            System.out.println("Date1" + aParsedDate);

//	            System.out.println("Java Date = " + date.toString());
//	            System.out.println("Java Date as a 'long' value = " + date.getTime());  // Returns the number of milliseconds since Jan
//	        	long aTime = Long.parseLong(aTimeString);
//	        	long anAdjustedTime = (aTime - 11644473600000L)/1000;

//	        	long anAdjustedTime = (aTime - 11644473600000L)/1000;
//	        	long anAdjustedTime = (aTime/1000 );

//	        	aDate = new Date (anAdjustedTime); 
//	        	String aTitle = resultSet.getString("title");
	            System.out.println (
	            			" [ " +
	            			" title " + resultSet.getString ("title") + 	            			
	                        ", visit count " + resultSet.getString ("visit_count") + 	                        
//	                        ", visit time " + aTimeString + 
	                        ", visit time " + aParsedDate + 

//	                         ", visit date " + aDate + 
	            			", URL [" + resultSet.getString ("url") + "]" +
	            		"]");
	            }
	        } 

	    catch (Exception e) 
	        {
	        e.printStackTrace ();
	        } 

	    finally 
	        {
	        try 
	            {
	            resultSet.close ();
	            statement.close ();
	            connection.close ();
	            } 

	        catch (Exception e) 
	            {
	            e.printStackTrace ();
	            }
	        }
	    }
	}

