package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Login {
	
	static String login(Connector con, String loginName, Boolean isDriver, String password) {
		String query = "";
		
		if(!checkLoginInfo(loginName, password, isDriver)) {
			return "Information entered was in the wrong format \n "
					+ "Please enter a password with at least 8 characters \n "
					+ "Please enter a digit greater than 1 for driver ID \n "
					+ "Please enter a login name with at least 1 non digit character '<BR>";
		}
		
		 if (!isDriver) query = "select * from UU where loginName = " + "\"" + loginName + "\"" + " and password = " + "\"" + password + "\"";
		 else query = "select * from UD where ID = " + loginName + " and password = " + "\"" + password + "\"";
		 
	     try {
			ResultSet rs = con.stmt.executeQuery(query);
			if(rs.next()) {
				if(!isDriver) return "<b>" + rs.getString("loginName") + "<\b>";
				else return "<b>" + rs.getString("loginName") + ", " + rs.getString("ID") + "<\b>";
			}
		} catch (SQLException e) {
			return "<b>" + "Error finding user" + "<\b>";
		}
		return "";
	}
	
	 public static boolean checkLoginInfo(String loginName, String password, Boolean driver) {
		  Boolean loginNameOkay = true;
		  Boolean passwordOkay = true;

		  if (!driver) {
		   if (!Pattern.matches("^(?=.*[a-z]).+$", loginName))
		    loginNameOkay = false;
		  } else {
		   try {
		    Integer.parseInt(loginName);
		   } catch (NumberFormatException e) {
		    loginNameOkay = false;
		   }
		  }
		  if (!Pattern.matches(".{8,}", password))
		   passwordOkay = false;

		  if (!loginNameOkay || !passwordOkay)
		   return false;
		  return true;
		 }
	
}
