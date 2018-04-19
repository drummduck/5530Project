package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsefulFeedback {
	
	static String errorReturn = "Information entered was in the wrong format, the feedbackID didn't exist, or it was your own vehicle. \n"
			+ "For rating please enter an integer between 0 and 2'<BR>";

	public static String viewDrivers(Connector con) {
		String query = "";
		String returnString = "";

		query = "select ID, firstName, lastName from UD";
		ResultSet rs;
		try {
			rs = con.stmt.executeQuery(query);
			System.out.println("**********UBER CAR FEEDBACK************");
			while (rs.next()) {
				returnString = returnString + "ID: " + rs.getInt("ID") + "  First Name: " + rs.getString("firstName")
						+ "  Last Name: " + rs.getString("lastName");
			}
		} catch (SQLException e) {
			return errorReturn;
		}
		return returnString;
	}
	
	public static String viewUsefulFeedback(Connector con, String ID, String amount) {
		String query = "";
		String returnString = "";
		
		try {
			Integer.parseInt(ID);
			Integer.parseInt(amount);
		}
		catch(NumberFormatException e) {
			return errorReturn;
		}
		
		query = String.format("SELECT * FROM UD where ID = %d", Integer.parseInt(ID));
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				return errorReturn;
			}
			else {
				query = String.format(
						"select s.feedbackID, s.comment, avg(s.rating)  as average  from (select f.feedbackID, f.comment, r.rating from Feedback f, feedback_Rating r, UC_Rating c, UC u where c.VIN = u.VIN and u.ID = %d and f.feedbackID = c.feedbackID and f.feedbackID = r.feedbackID) as s group by s.feedbackID order by average desc limit %d;",
						Integer.parseInt(ID), Integer.parseInt(amount));
					rs = con.stmt.executeQuery(query);
					while (rs.next())
						returnString = returnString + String.format("Feedback ID: %d          Comment: %s", rs.getInt("feedbackID"),
								rs.getString("comment") + "\n");

			}
		} catch (SQLException e) {
			return errorReturn;
		}
		return returnString;
	}
}
