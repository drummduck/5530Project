package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Reserve {

	static ArrayList<String> reservationsString = new ArrayList<String>();
	static ArrayList<String> resDates = new ArrayList<String>();
	static ArrayList<String> resVins = new ArrayList<String>();
	static String errorReturn = "Information entered was in the wrong format, the vehicle didn't exist, or the reservation already existed \n"
			+ "For the date please enter in this format: 'YYYY-MM-DD HH:MM' \n"
			+ "For the VIN please enter an alphanumeric of length 17"
			+ "For multiple rides please seperate each entry with a comma"
			+ "Must have same amount of entries for every field '<BR>";

	public static String ReserveRides(String datesIn, String vinsIn, Connector con) {
		String query = "";
		String[] dates = datesIn.split(",");
		String[] vins = vinsIn.split(",");

		if (dates.length != vins.length)
			return errorReturn;

		for (int i = 0; i <= dates.length; i++) {
			if (!reserveCheck(dates[i].split(",")[0], dates[i].split(",")[1], vins[i])) {
				reservationsString.clear();
				resDates.clear();
				resVins.clear();
				return errorReturn;
			}
			try {
				query = String.format("select * from UC where VIN = '%s'", vins[i]);
				ResultSet rs = con.stmt.executeQuery(query);
				if (!rs.next()) {
					reservationsString.clear();
					resDates.clear();
					resVins.clear();
					return errorReturn;
				}

				query = String.format("select * from Reservations where VIN = '%s' and date = '%s'", vins[i],
						dates[i].split(",")[0] + " " + dates[i].split(",")[1]);
				rs = con.stmt.executeQuery(query);
				if (rs.next()) {
					reservationsString.clear();
					resDates.clear();
					resVins.clear();
					return errorReturn;
				}
			} catch (SQLException e) {
				reservationsString.clear();
				resDates.clear();
				resVins.clear();
				return errorReturn;
			}

			reservationsString.add(String.format("UC VIN: %s, DateTime: %s", vins[i],
					dates[i].split(",")[0] + " " + dates[i].split(",")[1]));
			resDates.add(dates[i]);
			resVins.add(vins[i]);
		}

		String returnString = "Do these reservations look okay to you?";
		for (String s : reservationsString)
			returnString = returnString + s;

		return returnString + "'<BR>";
	}

	private static boolean reserveCheck(String date, String time, String vin) {
		Boolean dateOkay = true;
		Boolean vinOkay = true;
		Boolean timeOkay = true;

		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
			vinOkay = false;
		if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date))
			dateOkay = false;
		if (!Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time))
			timeOkay = false;

		if (!vinOkay || !dateOkay || !timeOkay)
			return false;

		return true;
	}

	public static String approveReservations(Boolean yesOrNo, String loginName, Connector con) {
		String query = "";
		String returnString = "***********OTHER UBER CARS YOU MIGHT LIKE************* \n";

		if (!yesOrNo) {
			reservationsString.clear();
			resDates.clear();
			resVins.clear();
			return "";
		}

		for (int i = 0; i <= resDates.size() - 1; i++) {
			query = String.format("INSERT into Reservations (VIN, loginName, date) VALUES ('%s', '%s', '%s')",
					resVins.get(i), loginName, resDates.get(i));
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				reservationsString.clear();
				resDates.clear();
				resVins.clear();
				return errorReturn;
			}
		}

		Boolean recommendExists = false;

		for (int i = 0; i <= resVins.size() - 1; i++) {
			if (recommendExists)
				break;
			query = String.format(
					"select r.VIN, count(*) as count from Rides r where r.loginName in (select distinct loginName from Rides where VIN = '%s') and r.VIN != '%s' group by r.VIN order by count",
					resVins.get(i), resVins.get(i));
			try {
				ResultSet rs = con.stmt.executeQuery(query);
				while (rs.next()) {
					recommendExists = true;
					returnString = returnString + "    VIN:  " + rs.getString("VIN") + "\n";
				}
			} catch (SQLException e) {
				reservationsString.clear();
				resDates.clear();
				resVins.clear();
				return errorReturn;
			}

		}
		return returnString + "'<BR>";
	}
}
