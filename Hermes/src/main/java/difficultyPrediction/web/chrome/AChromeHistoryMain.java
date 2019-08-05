package difficultyPrediction.web.chrome;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.util.FileUtils;

import config.HelperConfigurationManagerFactory;
import difficultyPrediction.web.WebFeatures;
import util.trace.Tracer;

/**
 * Adapted from http://www.javaworkspace.com/connectdatabase/connectSQLite.do
 * Date: 09/25/2012
 * 
 * Download sqlite-jdbc-<>.jar from
 * http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC, and compile: javac
 * GetChromiumHistory.java run: java -classpath ".:sqlite-jdbc-3.7.2.jar"
 * GetChromiumHistory
 */
public class AChromeHistoryMain {

	
	

	public static void main(String[] args) {
		AChromeHistoryAccessor.setTerms(
				HelperConfigurationManagerFactory.getSingleton().getTechnicalTerms(),
HelperConfigurationManagerFactory.getSingleton().getNonTechnicalTerms());
		Tracer.showInfo(true);
		long aTime = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L; // last 90 dats
		long aTime2 = aTime + 3*24 * 60 * 60 * 1000L;
		WebFeatures aWebFetaures = new AWebFeatures();	
		aWebFetaures.setUnixStartTime(aTime);
//		aWebFetaures.setElapsedTime(3*24 * 60 * 60 * 1000L);
		aWebFetaures.setElapsedTime(aTime2-aTime);

		AChromeHistoryAccessor.processURLs(aWebFetaures);
		for (PageVisit aPageVisit:aWebFetaures.getPageVisits()) {
			System.out.println(aPageVisit);
		}
		
		Date aCurrentDate = new Date(aTime);
		
		AChromeHistoryAccessor.initializeTargetFileName();

		// final String JDBC_FILE = "jdbc:sqlite:" + HISTORY_COPY;
		final String JDBC_FILE = "jdbc:sqlite:" + "/" + AChromeHistoryAccessor.targetFileName;

	
		System.out.println("current date" + aCurrentDate);
		long aChromeTime = aTime * 1000 + AChromeHistoryAccessor.CHROME_MINUS_UNIX;
		long aChromeTime2 = aTime2 * 1000 + AChromeHistoryAccessor.CHROME_MINUS_UNIX;

		Connection connection = null;
		ResultSet resultSet = null;
		String aPreviousTitle = "";
		Date aPreviousDate = null;
		Statement statement = null;

		try {
			String anOSName = System.getProperty("os.name");
			String aSourceFileName = "";
			String aUserName = System.getProperty("user.name");

			if (anOSName.contains("Windows")) {
				aSourceFileName = String.format(AChromeHistoryAccessor.HISTORY_WINDOWS_REL_NAME, aUserName);
			} else if (anOSName.contains("Mac")) {
				aSourceFileName = String.format(AChromeHistoryAccessor.HISTORY_MAC_REL_NAME, aUserName);

			} else if (anOSName.contains("Linux")) {
				aSourceFileName = String.format(AChromeHistoryAccessor.HISTORY_UNIX_REL_NAME, aUserName);

			} else {
				return;
			}

			File aSourceFile = new File(aSourceFileName);

			if (!aSourceFile.exists()) {
				System.exit(0);
			}
			long aTimeStamp = aSourceFile.lastModified();
			Date aDate = new Date(aTimeStamp);
			System.out.println("History File Timestamp:" + aTimeStamp + " date:" + aDate);

			File aTargetFile = new File(AChromeHistoryAccessor.targetFileName);

			if (aTargetFile.exists()) {
				aTargetFile.delete();
			}
			Path aFrom = Paths.get(aSourceFile.getAbsolutePath());
			Path aTo = Paths.get(aTargetFile.getAbsolutePath());
			CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES };
			java.nio.file.Files.copy(aFrom, aTo, options);

			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
					// "jdbc:sqlite:/home/username/.config/chromium/Default/History"
					JDBC_FILE);
			statement = connection.createStatement();
			String aQuery = "SELECT title, visit_count, last_visit_time, "
					// + "datetime(last_visit_time / 1000000 +
					// (strftime('%s', '1601-01-01')), 'unixepoch',
					// 'localtime'), URL FROM urls where visit_count >
					// 0");
					+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time >= "
					+ aChromeTime  + " AND last_visit_time <= "  + aChromeTime2;
			resultSet = statement
					// .executeQuery ("SELECT * FROM urls where visit_count >
					// 0");
					// .executeQuery ("SELECT title, visit_count,
					// last_visit_time, URL FROM urls where visit_count > 0");
					// .executeQuery ("SELECT title, visit_count,
					// datetime(last_visit_time / 1000000 + (strftime('%s',
					// '1601-01-01')), 'unixepoch'), URL FROM urls where
					// visit_count > 0");
					// .executeQuery ("SELECT title, visit_count,
					// datetime(last_visit_time / 1000000 + (strftime('%s',
					// '1601-01-01', 'localtime')), URL FROM urls where
					// visit_count > 0");
					// .executeQuery ("SELECT title, visit_count,
					// datetime(last_visit_time / 1000000 + (strftime('%s',
					// '1601-01-01')), 'unixepoch'), URL FROM urls where
					// visit_count > 0");
					.executeQuery("SELECT title, visit_count, last_visit_time, "
							// + "datetime(last_visit_time / 1000000 +
							// (strftime('%s', '1601-01-01')), 'unixepoch',
							// 'localtime'), URL FROM urls where visit_count >
							// 0");
							+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time >= "
							+ aChromeTime  + " AND last_visit_time <= "  + aChromeTime2);

			while (resultSet.next()) {
				// String aTimeString = resultSet.getString("last_visit_time");
				String aTimeString = resultSet.getString(
						"datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime')");
				// 2019-07-29 11:04:26
				String aTitle = resultSet.getString("title");
				System.out.println("aChrome date:" + aTimeString);
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date date = df.parse(aTimeString);

				if (date.equals(aPreviousDate) && aTitle.equals(aPreviousTitle)) {
					System.out.println("duplicate item");
				}
				System.out.println(date);

				long aUnixTime = date.getTime();
				String aChromeTimeMicrosecsString = resultSet.getString("last_visit_time");
				long aChromeTimeMicrosecs = Long.parseLong(aChromeTimeMicrosecsString);
				long aUnixTimeMicrosecs = aUnixTime * 1000;
				long aTimeDifference = aChromeTimeMicrosecs - aUnixTimeMicrosecs;
				System.out.println("Chrrome - Unix:" + aTimeDifference);
				Date aParsedDate = new Date(aUnixTime);
				System.out.println("Date1" + aParsedDate);
				long aCalculatedUnixTime = (aChromeTimeMicrosecs - AChromeHistoryAccessor.CHROME_MINUS_UNIX) / 1000;
				aParsedDate.setTime(aCalculatedUnixTime);
				System.out.println("Date1" + aParsedDate);

				// System.out.println("Java Date = " + date.toString());
				// System.out.println("Java Date as a 'long' value = " +
				// date.getTime()); // Returns the number of milliseconds since
				// Jan
				// long aTime = Long.parseLong(aTimeString);
				// long anAdjustedTime = (aTime - 11644473600000L)/1000;

				// long anAdjustedTime = (aTime - 11644473600000L)/1000;
				// long anAdjustedTime = (aTime/1000 );

				// aDate = new Date (anAdjustedTime);
				// String aTitle = resultSet.getString("title");
				System.out.println(" [ " + " title " + resultSet.getString("title") + ", visit count "
						+ resultSet.getString("visit_count") +
						// ", visit time " + aTimeString +
						", visit time " + aParsedDate +

						// ", visit date " + aDate +
						", URL [" + resultSet.getString("url") + "]" + "]");
				aPreviousTitle = aTitle;
				aPreviousDate = date;

			}
			if (aTargetFile.exists()) {
				aTargetFile.delete();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
