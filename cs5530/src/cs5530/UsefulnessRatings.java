package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsefulnessRatings {

	static String errorReturn = "Information entered was in the wrong format, the feedbackID didn't exist, or it was your own vehicle. \n"
			+ "For rating please enter an integer between 0 and 2'<BR>";

	public static String viewUberDrivers(Connector con) {
		String query = "";
		String returnString = "";

		query = "select feedbackID, comment from Feedback";
		ResultSet rs;
		try {
			rs = con.stmt.executeQuery(query);
			System.out.println("**********UBER CAR FEEDBACK************");
			while (rs.next()) {
				returnString = returnString + "ID:" + rs.getInt("feedbackID") + "                 " + "Comment: " + rs.getString("comment") + "\n";
			}
		} catch (SQLException e) {
			return errorReturn;
		}
		return returnString;
	}

	public static String rateFeedback(Connector con, String feedbackID, String loginName, String rating) {

		String query = "";

		query = String.format("select loginName from Feedback where feedbackID = %d", Integer.parseInt(feedbackID));
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getString("loginName").equals(loginName)) {
					return errorReturn;
				} else {
					try {
						int ratingInt = Integer.parseInt(rating);
						if (ratingInt < 0 || ratingInt > 2) {
							return errorReturn;
						}
					} catch (NumberFormatException e) {
						return errorReturn;
					}
					query = String.format("INSERT INTO feedback_Rating (loginName, feedbackID, rating) VALUES ('%s', %d, %d)",
							loginName, Integer.parseInt(feedbackID), Integer.parseInt(rating));
					con.stmt.executeUpdate(query);

				}
			} else {
				return errorReturn;
			}
		} catch (SQLException e) {
			return errorReturn;
		}

		return "Feedback successfully rated";
	}
}
