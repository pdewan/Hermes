package fluorite.util;

public class StringUtil {
	
	public static String removeUserName(String fullName) {
		String userName = "";
		if (fullName.contains("(")) {
			int indexOfFirstParen = fullName.indexOf("(");
			int indexOfLastParen = fullName.indexOf(")");

			userName = fullName.substring(indexOfFirstParen + 1,
					indexOfLastParen);
			System.out.println(userName);
		}
		return userName;
	}
	
	public static boolean isNullOrEmpty(String aString) {
		return aString == null || aString.isEmpty();
	}

}
