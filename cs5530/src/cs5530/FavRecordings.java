package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class FavRecordings {

	static String errorReturn = "Information entered was in the wrong format, or the vehicle didn't exist '<BR>";
	
	public static String declareFavorite(String loginName, String vin, Connector con) {
		
		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) return errorReturn;
		
		String query = "select * from Favorites where loginName = " + loginName;
	
		ResultSet rs;
		try {
			rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				query = "UPDATE Favorites SET VIN = " + vin + " where loginName = " + loginName;
				con.stmt.executeUpdate(query);
			}
			else {
				query = String.format("INSERT INTO Favorites (loginName, VIN) VALUES ('%s', '%s')", loginName, vin);
				con.stmt.executeUpdate(query);
			}
		}
		catch(SQLException e) {
			return errorReturn;
		}
		
		return "";
	}
}
