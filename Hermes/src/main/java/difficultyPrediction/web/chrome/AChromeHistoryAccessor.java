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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.web.WebFeatures;
import fluorite.commands.WebVisitCommand;
import fluorite.model.EHEventRecorder;

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
		if (targetFile != null && targetFile.exists()) {
			targetFile.delete();
		}
	}
	public static void closeConnections() throws SQLException {
		if (resultSet !=null)
		resultSet.close();
		if (statement != null)
		statement.close();
		if (connection != null)
		connection.close();
	}

	public static void copyTargetFile() throws IOException {
		targetFile = new File(targetFileName);
		try {
		deleteTargetFile();

		Path aFrom = Paths.get(sourceFile.getAbsolutePath());
		Path aTo = Paths.get(targetFile.getAbsolutePath());
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };
		java.nio.file.Files.copy(aFrom, aTo, options);
		} catch (Exception e) {
//			System.err.println("Could not copy chrome history:" + e.getMessage());
		}
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

	public static void composeQuery(long aChromeStartTimeRange, long aChromeEndTimeRange) {
//		query = String.format("SELECT title, visit_count, last_visit_time, "
//				+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time > "
//				+ aChromeStartTimeRange);
		query = String.format("SELECT title, visit_count, last_visit_time, URL FROM urls where last_visit_time >= %s AND last_visit_time <= %s",
				aChromeStartTimeRange, aChromeEndTimeRange );
//		where last_visit_time >= "
//				+ aChromeTime  + " AND last_visit_time <= "  + aChromeTime2);
	
	}

	public static void executeQuery() throws SQLException {
		resultSet = statement.executeQuery(query);
	}
	

	static String previousTimestring = "";
	static long previousTime = 0;

	static Pattern yesPattern;
	static Pattern noPattern;
	static List emptyList = new ArrayList();
	static List<String> technicalTerms = emptyList;
	static List<String> nonTechnicalTerms = emptyList;
	
	public static void setTerms (List<String> aTechnicalTerms, List<String> aNonTechnicalTerms) {
		technicalTerms = aTechnicalTerms;
		nonTechnicalTerms = aNonTechnicalTerms;
	}

	public static void makePatterns() {
		if (yesPattern == null) {
			// StringBuffer aRegex = new StringBuffer();
//			List<String> aYesStrings = HelperConfigurationManagerFactory.getSingleton().getTechnicalTerms();
//			List<String> aYesStrings = HelperConfigurationManagerFactory.getSingleton().getTechnicalTerms();

			yesPattern = toPattern(technicalTerms);
		}
		if (noPattern == null) {
//			List<String> aNoStrings = HelperConfigurationManagerFactory.getSingleton().getNonTechnicalTerms();
//			List<String> aNoStrings = nonTechnicalTerms;

			noPattern = toPattern(nonTechnicalTerms);
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
	
	public static long toChromeTime (long aUnixTime) {
		return aUnixTime*1000 + CHROME_MINUS_UNIX;
	}
	
	public static long toUnixTime (long aChromeTime) {
		return (aChromeTime - CHROME_MINUS_UNIX)/1000;
	}

	public static void processResultSet(long aStartTime, WebFeatures aWebFeatures) throws SQLException {
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
//			long aUnixTime = (aVisitTime - CHROME_MINUS_UNIX)/1000;
			long aUnixTime = toUnixTime(aVisitTime);

			aDate.setTime(aUnixTime);
			int aVisitCount = Integer.parseInt(resultSet.getString("visit_count"));
			String aTitle = resultSet.getString("title");
			String aURL = resultSet.getString("url");
			

			if (previousTime == aVisitTime) { 
//				&& aTitle.equals(previousTitle)) {
//				System.out.println(aDate + ":Ignoring duplicate visit" + aTitle);
				continue;
			}
			aNumURLs++;
			if (isGoogleSearch(aTitle)) {
				aNumGoogleSearches++;
				aSearchLength++;
				
			} else {
				aPreviousVisitWasASearch = false;
//				if (aMaxSearchLength < aSearchLength) {
//					System.out.println("New Max search Length " + aSearchLength);
//				}
				aMaxSearchLength = Math.max(aMaxSearchLength, aSearchLength);
				aSearchLength = 0;
			}
				
			
			PageVisit aPageVisit = new PageVisit(aUnixTime, aTitle, aVisitCount, aURL);
			if (!trackURL(aPageVisit)) {
//				Tracer.info(AChromeHistoryAccessor.class, aDate + "Ignoring non technical visit " + aPageVisit);

				continue;
			}
			
			WebVisitCommand aWebVisitCommand = new WebVisitCommand(aTitle, aURL, aVisitCount);
			EHEventRecorder.getInstance().recordCommand(aWebVisitCommand);

//
//			String aPageVisitString = aTitle + " " + resultSet.getString("visit_count") + " "
//					+ resultSet.getString("url");
//			pageVisits.add(aPageVisitString);

			previousTitle = aTitle;
			previousTime = aVisitTime;
//			Tracer.info(AChromeHistoryAccessor.class, aDate + "Adding  technical visit " + aPageVisit);

			aPageVisits.add(aPageVisit);

		}
		aWebFeatures.setPageVisits(aPageVisits);
		aWebFeatures.setNumPagesVisited(aNumURLs);
		aWebFeatures.setNumWebSearches(aNumGoogleSearches);
		aMaxSearchLength = Math.max(aMaxSearchLength, aSearchLength);

		aWebFeatures.setMaxSearchLength(aMaxSearchLength);
		closeConnections();
		deleteTargetFile();
	}
	
	public static void processURLs(WebFeatures aWebFeatures) {
		if (DifficultyPredictionSettings.isReplayMode()) {
			
			return; // we have t look at stored web accesses
		}
		try {
			if (aWebFeatures == null) {
				return;
			}
			initializeSource();
			long aUnixStartTime = aWebFeatures.getUnixStartTime();
			if (!hasSourceChanged(aUnixStartTime))
				return;
			initializeTargetFileName();
			initializeJDBCFileName();
			copyTargetFile();
			connectToJDBCFileName();
//			long aChromeStartTimeRange = aUnixStartTime*1000 + CHROME_MINUS_UNIX;
			long aChromeStartTimeRange = toChromeTime(aUnixStartTime);
			long anElapsedTime = aWebFeatures.getElapsedTime();
			long aUnixEndTime = aUnixStartTime + anElapsedTime;
//			long aChromeEndTimeRange = toChromeTime(aRatioFeatures.getUnixStartTime() + aRatioFeatures.getElapsedTime());
			long aChromeEndTimeRange = toChromeTime(aUnixEndTime);

			composeQuery(aChromeStartTimeRange, aChromeEndTimeRange);
			executeQuery();
			processResultSet(aChromeStartTimeRange, aWebFeatures);
			
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
	

//	public static void main(String[] args) {
//		Tracer.showInfo(true);
//		long aTime = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L; // last 90 dats
//		long aTime2 = aTime + 3*24 * 60 * 60 * 1000L;
//		WebFeatures aWebFetaures = new AWebFeatures();	
//		aWebFetaures.setUnixStartTime(aTime);
////		aWebFetaures.setElapsedTime(3*24 * 60 * 60 * 1000L);
//		aWebFetaures.setElapsedTime(aTime2-aTime);
//
//		processURLs(aWebFetaures);
//		for (PageVisit aPageVisit:aWebFetaures.getPageVisits()) {
//			System.out.println(aPageVisit);
//		}
//		
//		Date aCurrentDate = new Date(aTime);
//		
//		initializeTargetFileName();
//
//		// final String JDBC_FILE = "jdbc:sqlite:" + HISTORY_COPY;
//		final String JDBC_FILE = "jdbc:sqlite:" + "/" + targetFileName;
//
//	
//		System.out.println("current date" + aCurrentDate);
//		long aChromeTime = aTime * 1000 + CHROME_MINUS_UNIX;
//		long aChromeTime2 = aTime2 * 1000 + CHROME_MINUS_UNIX;
//
//		Connection connection = null;
//		ResultSet resultSet = null;
//		String aPreviousTitle = "";
//		Date aPreviousDate = null;
//		Statement statement = null;
//
//		try {
//			String anOSName = System.getProperty("os.name");
//			String aSourceFileName = "";
//			String aUserName = System.getProperty("user.name");
//
//			if (anOSName.contains("Windows")) {
//				aSourceFileName = String.format(HISTORY_WINDOWS_REL_NAME, aUserName);
//			} else if (anOSName.contains("Mac")) {
//				aSourceFileName = String.format(HISTORY_MAC_REL_NAME, aUserName);
//
//			} else if (anOSName.contains("Linux")) {
//				aSourceFileName = String.format(HISTORY_UNIX_REL_NAME, aUserName);
//
//			} else {
//				return;
//			}
//
//			File aSourceFile = new File(aSourceFileName);
//
//			if (!aSourceFile.exists()) {
//				System.exit(0);
//			}
//			long aTimeStamp = aSourceFile.lastModified();
//			Date aDate = new Date(aTimeStamp);
//			System.out.println("History File Timestamp:" + aTimeStamp + " date:" + aDate);
//
//			File aTargetFile = new File(targetFileName);
//
//			if (aTargetFile.exists()) {
//				aTargetFile.delete();
//			}
//			Path aFrom = Paths.get(aSourceFile.getAbsolutePath());
//			Path aTo = Paths.get(aTargetFile.getAbsolutePath());
//			CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
//					StandardCopyOption.COPY_ATTRIBUTES };
//			java.nio.file.Files.copy(aFrom, aTo, options);
//
//			Class.forName("org.sqlite.JDBC");
//			connection = DriverManager.getConnection(
//					// "jdbc:sqlite:/home/username/.config/chromium/Default/History"
//					JDBC_FILE);
//			statement = connection.createStatement();
//			String aQuery = "SELECT title, visit_count, last_visit_time, "
//					// + "datetime(last_visit_time / 1000000 +
//					// (strftime('%s', '1601-01-01')), 'unixepoch',
//					// 'localtime'), URL FROM urls where visit_count >
//					// 0");
//					+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time >= "
//					+ aChromeTime  + " AND last_visit_time <= "  + aChromeTime2;
//			resultSet = statement
//					// .executeQuery ("SELECT * FROM urls where visit_count >
//					// 0");
//					// .executeQuery ("SELECT title, visit_count,
//					// last_visit_time, URL FROM urls where visit_count > 0");
//					// .executeQuery ("SELECT title, visit_count,
//					// datetime(last_visit_time / 1000000 + (strftime('%s',
//					// '1601-01-01')), 'unixepoch'), URL FROM urls where
//					// visit_count > 0");
//					// .executeQuery ("SELECT title, visit_count,
//					// datetime(last_visit_time / 1000000 + (strftime('%s',
//					// '1601-01-01', 'localtime')), URL FROM urls where
//					// visit_count > 0");
//					// .executeQuery ("SELECT title, visit_count,
//					// datetime(last_visit_time / 1000000 + (strftime('%s',
//					// '1601-01-01')), 'unixepoch'), URL FROM urls where
//					// visit_count > 0");
//					.executeQuery("SELECT title, visit_count, last_visit_time, "
//							// + "datetime(last_visit_time / 1000000 +
//							// (strftime('%s', '1601-01-01')), 'unixepoch',
//							// 'localtime'), URL FROM urls where visit_count >
//							// 0");
//							+ "datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime'), URL FROM urls where last_visit_time >= "
//							+ aChromeTime  + " AND last_visit_time <= "  + aChromeTime2);
//
//			while (resultSet.next()) {
//				// String aTimeString = resultSet.getString("last_visit_time");
//				String aTimeString = resultSet.getString(
//						"datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch', 'localtime')");
//				// 2019-07-29 11:04:26
//				String aTitle = resultSet.getString("title");
//				System.out.println("aChrome date:" + aTimeString);
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//				Date date = df.parse(aTimeString);
//
//				if (date.equals(aPreviousDate) && aTitle.equals(aPreviousTitle)) {
//					System.out.println("duplicate item");
//				}
//				System.out.println(date);
//
//				long aUnixTime = date.getTime();
//				String aChromeTimeMicrosecsString = resultSet.getString("last_visit_time");
//				long aChromeTimeMicrosecs = Long.parseLong(aChromeTimeMicrosecsString);
//				long aUnixTimeMicrosecs = aUnixTime * 1000;
//				long aTimeDifference = aChromeTimeMicrosecs - aUnixTimeMicrosecs;
//				System.out.println("Chrrome - Unix:" + aTimeDifference);
//				Date aParsedDate = new Date(aUnixTime);
//				System.out.println("Date1" + aParsedDate);
//				long aCalculatedUnixTime = (aChromeTimeMicrosecs - CHROME_MINUS_UNIX) / 1000;
//				aParsedDate.setTime(aCalculatedUnixTime);
//				System.out.println("Date1" + aParsedDate);
//
//				// System.out.println("Java Date = " + date.toString());
//				// System.out.println("Java Date as a 'long' value = " +
//				// date.getTime()); // Returns the number of milliseconds since
//				// Jan
//				// long aTime = Long.parseLong(aTimeString);
//				// long anAdjustedTime = (aTime - 11644473600000L)/1000;
//
//				// long anAdjustedTime = (aTime - 11644473600000L)/1000;
//				// long anAdjustedTime = (aTime/1000 );
//
//				// aDate = new Date (anAdjustedTime);
//				// String aTitle = resultSet.getString("title");
//				System.out.println(" [ " + " title " + resultSet.getString("title") + ", visit count "
//						+ resultSet.getString("visit_count") +
//						// ", visit time " + aTimeString +
//						", visit time " + aParsedDate +
//
//						// ", visit date " + aDate +
//						", URL [" + resultSet.getString("url") + "]" + "]");
//				aPreviousTitle = aTitle;
//				aPreviousDate = date;
//
//			}
//			if (aTargetFile.exists()) {
//				aTargetFile.delete();
//			}
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		finally {
//			try {
//				resultSet.close();
//				statement.close();
//				connection.close();
//			}
//
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
