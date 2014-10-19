package util;

public class EscapedExpression {
	private static final String	HIBERNATE_ESCAPE_CHAR	= "\\";

	public static String escape(String value) {
		return value
				.replace("\\", HIBERNATE_ESCAPE_CHAR + "\\")
				.replace("_", HIBERNATE_ESCAPE_CHAR + "_")
				.replace("%", HIBERNATE_ESCAPE_CHAR + "%");
	}
}
