package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class FeedbackRecordings {

	static String errorReturn = "Information entered was in the wrong format, the vehicle didn't exist, or you have already left a review \n"
			+ " For vin please enter an alphanumeric value with 17 characters \n "
			+ " For rating please enter an integer between 1-10 \n"
			+ " Comment has to be less than 300 characters '<BR>";
	
	public static String recordFeedback(String vin, String rating, String comment, Connector con, String loginName) {
		int feedBackID = -1;
		String query = "";
		
		if(!checkFeedback(vin, rating, comment)) return errorReturn;
		
		query = String.format("select * from Feedback f, UC_Rating u where f.feedbackID = u.feedbackID and u.VIN = '%s' and f.loginName = '%s'", vin, loginName);
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				 return errorReturn;
				}
				Calendar calendar = Calendar.getInstance();
				java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
				System.out.println(date.toString());
				query = String.format(
						"INSERT INTO Feedback (comment, score, loginName, date) VALUES ('%s', %d, '%s', '%s')",
						comment, Integer.parseInt(rating), loginName, date.toString());
				con.stmt.executeUpdate(query);
				query = "SELECT feedbackID FROM Feedback ORDER BY feedbackID DESC LIMIT 1";
				rs = con.stmt.executeQuery(query);
				if (rs.next()) feedBackID = rs.getInt("feedbackID");
				else throw new SQLException();
				query = String.format("INSERT INTO UC_Rating (feedbackID, VIN) VALUES (%d, '%s')", feedBackID, vin);
				con.stmt.executeUpdate(query);
			}
		catch(SQLException e) {
			return errorReturn;
		}
		
		return "Your feedback ID is " + feedBackID;
	}
	
	private static Boolean checkFeedback(String vin, String rating, String comment) {

		Boolean vinOkay = true;
		Boolean ratingOkay = true;
		Boolean commentOkay = true;
		int ratingInt = -1;

		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
			vinOkay = false;
		try {
			ratingInt = Integer.parseInt(rating);
		} catch (NumberFormatException e) {
			ratingOkay = false;
			System.out.println("You must enter an integer between 1-10 for your rating");
		}

		if (ratingInt < 1 || ratingInt > 10) {
			ratingOkay = false;
			System.out.println("You must enter an integer between 1-10 for your rating");
		}

		if (comment.length() > 300) {
			commentOkay = false;
			System.out.println("Comment must be less than 300 characters");
		}

		if (!vinOkay || !ratingOkay || !commentOkay)
			return false;

		return true;
	}
}
