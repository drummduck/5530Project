package cs5530;

import java.util.Scanner;
import java.sql.*;

public class Trust {

	static String errorReturn = "Information entered was in the wrong format, or the user didnt exist \n"
			+ "Please enter yes or no for trusted '<BR>";

	// This method implements the trust recordings defined in part 8 of System
	// Functionality
	public static String declareTrust(Connector2 con, String loginName1, String loginName2, Boolean trusted) {
		String cmd = "";
		String query = "";

		query = String.format("select * from UU where loginName = '%s'", loginName2);
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				return errorReturn;
			} else {
				query = String.format("select * from user_Rating where loginName1 = '%s' and loginName2 = '%s'",
						loginName1, loginName2);
				rs = con.stmt.executeQuery(query);

				Boolean update = false;
				if (rs.next()) {
					query = String.format(
							"update user_Rating set trust = '%d' where loginName1 = '%s' and loginName2 = '%s'", a,
							loginName1, loginName2);
				} else {
					query = String.format(
							"insert into user_Rating (trust,loginName1,loginName2) values ('%d','%s','%s')", a, login1,
							loginName2);
				}
			}
		} catch (SQLException e) {
			return errorReturn;
		}

		return "Thank you for your feedback on " + loginName2 + "'<BR>";
	}
}