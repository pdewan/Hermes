package difficultyPrediction.web;

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
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioFeatures;

/**
 * Adapted from http://www.javaworkspace.com/connectdatabase/connectSQLite.do
 * Date: 09/25/2012
 * 
 * Download sqlite-jdbc-<>.jar from
 * http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC, and compile: javac
 * GetChromiumHistory.java run: java -classpath ".:sqlite-jdbc-3.7.2.jar"
 * GetChromiumHistory
 */
public class AChromeHistoryAccessor {

	public static final long CHROME_MINUS_UNIX = 11644473600000000L;
	/**
	 * @author www.javaworkspace.com
	 * 
	 */
	static final String HISTORY_LOCAL_FILE = "AppData//Local//Google//Chrome//User Data//Default//History";
	static final String HISTORY_WINDOWS_REL_NAME = "C:\\Users\\%s\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\History";
	static final String HISTORY_UNIX_REL_NAME = "/home/%s/.config/google-chrome/default/History";
	static final String HISTORY_MAC_REL_NAME = "Users/%s/Library/Application Support/Google/Chrome/Default/History";
	static final String HISTORY_COPY_REL_NAME = "ChromeHistoryCopy";

	static String targetFileName, sourceFileName;
	static File sourceFile;
	static String homeDirectory;
	static String JDBCFileName;
	static String query;
	static Statement statement;
	static Connection connection;
	static ResultSet resultSet;
//	static List<PageVisit> pageVisits = new ArrayList();

	public static void initializeTargetFileName() {
		if (targetFileName != null) {
			return;
		}
		homeDirectory = System.getProperty("user.home");
		targetFileName = homeDirectory + "/" + HISTORY_COPY_REL_NAME;
		// }
	}

	static File targetFile;

	public static void deleteTargetFile() {
		if (targetFile.exists()) {
			targetFile.delete();
		}
	}
	public static void closeConnections() throws SQLException {
		resultSet.close();
		statement.close();
		connection.close();
	}

	public static void copyTargetFile() throws IOException {
		targetFile = new File(targetFileName);
		deleteTargetFile();

		Path aFrom = Paths.get(sourceFile.getAbsolutePath());
		Path aTo = Paths.get(targetFile.getAbsolutePath());
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };
		java.nio.file.Files.copy(aFrom, aTo, options);
	}

	public static void initializeJDBCFileName() {
		if (JDBCFileName != null) {
			return;
		}
		JDBCFileName = "jdbc:sqlite:" + "/" + targetFileName;

	}

	public static void connectToJDBCFileName() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection(
				// "jdbc:sqlite:/home/username/.config/chromium/Default/History"
				JDBCFileName);
		statement = connection.createStatement();
	}

	public static void initializeSource() {
		if (sourceFileName != null)
			return;
		String anOSName = System.getProperty("os.name");
		String aSourceFileName = "";
		String aUserName = System.getProperty("user.name");

		if (anOSName.contains("Windows")) {
			aSourceFileName = String.format(HISTORY_WINDOWS_REL_NAME, aUserName);
		} else if (anOSName.contains("Mac")) {
			aSourceFileName = String.format(HISTORY_MAC_REL_NAME, aUserName);

		} else if (anOSName.contains("Linux")) {
			aSourceFileName = String.format(HISTORY_UNIX_REL_NAME, aUserName);

		} else {
			return;
		}
		sourceFileName = aSourceFileName;
		sourceFile = new File(aSourceFileName);

	}
	
	public static boolean hasSourceChanged(long aUnixStartTime) {
		return sourceFile.lastModified() > aUnixStartTime;
	}

	public static void composeQuery(long aChromeStartTimeRange) {
//		query = String.format("SELECT title, visit_count, last_visit_time, "
//				+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time > "
//				+ aChromeStartTimeRange);
		query = String.format("SELECT title, visit_count, last_visit_time, URL FROM urls where last_visit_time > "
				+ aChromeStartTimeRange);
	
	}

	public static void executeQuery() throws SQLException {
		resultSet = statement.executeQuery(query);
	}
	

	static String previousTimestring = "";
	static long previousTime = 0;

	static Pattern yesPattern;
	static Pattern noPattern;

	public static void makePatterns() {
		if (yesPattern == null) {
			// StringBuffer aRegex = new StringBuffer();
			List<String> aYesStrings = HelperConfigurationManagerFactory.getSingleton().getTechnicalTerms();
			yesPattern = toPattern(aYesStrings);
		}
		if (noPattern == null) {
			List<String> aNoStrings = HelperConfigurationManagerFactory.getSingleton().getNonTechnicalTerms();
			noPattern = toPattern(aNoStrings);
		}

	}

//	public static boolean matches(String aString) {
//		Matcher aNoMatcher = noPattern.matcher(aString);
//		Matcher aYesMatcher = yesPattern.matcher(aString);
//		return !aNoMatcher.matches() && aYesMatcher.matches();
//
//	}
	static StringBuffer combinedRegex = new StringBuffer();


	public static Pattern toPattern(List<String> aRegexes) {
		boolean aFirst = true;
		combinedRegex.setLength(0);
		for (String aRegexItem : aRegexes) {
			if (aFirst) {
				aFirst = false;
			} else {
				combinedRegex.append("|");
			}
			combinedRegex.append(".*" + aRegexItem + ".*");
		}
		return Pattern.compile(combinedRegex.toString());
	}
	public static boolean trackURL(PageVisit aPageVisit) {
		return trackURL(aPageVisit.title, aPageVisit.url, aPageVisit.numVisits);
	}
	public static boolean trackURL(String aTopic, String aURL, int aNumVisits) {
		makePatterns();
//		if (aTopic.contains("Basic Computing Systems Concepts: Hardware, OS, Design Patterns")) {
//			System.out.println("aTopic " + aTopic);
//		}
//		if (aURL.contains("An_Accession_of_Object_Oriented_Database")) {
//			System.out.println("URL " + aURL);
//		}
		Matcher aNoTopicMatcher = noPattern.matcher(aTopic);
		Matcher aYesTopicMatcher = yesPattern.matcher(aTopic);
		Matcher aNoURLMatcher = noPattern.matcher(aURL);
		Matcher aYesURLMatcher = yesPattern.matcher(aURL);
		return (!aNoTopicMatcher.matches() && 
				!aNoURLMatcher.matches())
				&& (
						aYesTopicMatcher.matches() |
						aYesURLMatcher.matches());
	}

	static String previousTitle = "";
	public static boolean isGoogleSearch(String aTopic) {
		return aTopic.contains("Google Search");
	}

	public static void processResultSet(long aStartTime, RatioFeatures aRatioFeatures) throws SQLException {
		int aNumURLs = 0;
//		pageVisits.clear();
		List<PageVisit> aPageVisits = new ArrayList();
		Date aDate = new Date();
		int aNumGoogleSearches = 0;
		int aSearchLength = 0;
		int aMaxSearchLength = 0;
		boolean aPreviousVisitWasASearch = false;
		while (resultSet.next()) {
//			String aVisitTimeString = ;
			long aVisitTime = Long.parseLong(resultSet.getString("last_visit_time"));
			long aUnixTime = (aVisitTime - CHROME_MINUS_UNIX)/1000;
			aDate.setTime(aUnixTime);
			int aVisitCount = Integer.parseInt(resultSet.getString("visit_count"));
			String aTitle = resultSet.getString("title");
			String aURL = resultSet.getString("url");
			

			if (previousTime == aVisitTime) { 
//				&& aTitle.equals(previousTitle)) {
				System.out.println(aDate + ":Ignoring duplicate visit" + aTitle);
				continue;
			}
			aNumURLs++;
			if (isGoogleSearch(aTitle)) {
				aNumGoogleSearches++;
				aSearchLength++;
				
			} else {
				aPreviousVisitWasASearch = false;
				if (aMaxSearchLength < aSearchLength) {
					System.out.println("New Max search Length " + aSearchLength);
				}
				aMaxSearchLength = Math.max(aMaxSearchLength, aSearchLength);
				aSearchLength = 0;
			}
				
			
			PageVisit aPageVisit = new PageVisit(aTitle, aVisitCount, aURL);
			if (!trackURL(aPageVisit)) {
				System.out.println(aDate + "Ignoring non technical visit" + aTitle + " " + aURL);

				continue;
			}
//
//			String aPageVisitString = aTitle + " " + resultSet.getString("visit_count") + " "
//					+ resultSet.getString("url");
//			pageVisits.add(aPageVisitString);

			previousTitle = aTitle;
			previousTime = aVisitTime;
			System.out.println(aDate + "Adding  technical visit " + aPageVisit);

			aPageVisits.add(aPageVisit);

		}
		aRatioFeatures.setPageVisits(aPageVisits);
		aRatioFeatures.setNumPagesVisited(aNumURLs);
		aRatioFeatures.setNumWebSearches(aNumGoogleSearches);
		aRatioFeatures.setMaxSearchLength(aMaxSearchLength);
		closeConnections();
		deleteTargetFile();
	}
	
	public static void processURLs(RatioFeatures aRatioFeatures, long aUnixStartTime) {
		
		try {
			initializeSource();
			if (!hasSourceChanged(aUnixStartTime))
				return;
			initializeTargetFileName();
			initializeJDBCFileName();
			copyTargetFile();
			connectToJDBCFileName();
			long aChromeStartTimeRange = aUnixStartTime*1000 + CHROME_MINUS_UNIX;

			composeQuery(aChromeStartTimeRange);
			executeQuery();
			processResultSet(aChromeStartTimeRange, aRatioFeatures);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnections();
				deleteTargetFile();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	

	public static void main(String[] args) {
		long aTime = System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000L; // last 90 dats
		RatioFeatures aRatioFetaures = new ARatioFeatures();		
		processURLs(aRatioFetaures, aTime);
		
		
		Date aCurrentDate = new Date(aTime);
		
		initializeTargetFileName();

		// final String JDBC_FILE = "jdbc:sqlite:" + HISTORY_COPY;
		final String JDBC_FILE = "jdbc:sqlite:" + "/" + targetFileName;

	
		System.out.println("current date" + aCurrentDate);
		long aChromeTime = aTime * 1000 + CHROME_MINUS_UNIX;

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
				aSourceFileName = String.format(HISTORY_WINDOWS_REL_NAME, aUserName);
			} else if (anOSName.contains("Mac")) {
				aSourceFileName = String.format(HISTORY_MAC_REL_NAME, aUserName);

			} else if (anOSName.contains("Linux")) {
				aSourceFileName = String.format(HISTORY_UNIX_REL_NAME, aUserName);

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

			File aTargetFile = new File(targetFileName);

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
							+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time > "
							+ aChromeTime);

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
				long aCalculatedUnixTime = (aChromeTimeMicrosecs - CHROME_MINUS_UNIX) / 1000;
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
