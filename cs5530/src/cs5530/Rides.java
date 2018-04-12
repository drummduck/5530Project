package cs5530;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Rides {

	static ArrayList<Ride> rides = new ArrayList<Ride>();
	static String errorReturn = "Information entered was in the wrong format, the vehicle didn't exist, or the ride already existed \n"
			+ "For cost please enter a dollar amount format e.g.(12.50) \n"
			+ "For the date please enter in this format: 'YYYY-MM-DD HH:MM' \n"
			+ "For distance please enter an integer in miles \n" 
			+ "For the number of people please enter an integer \n"
			+ "For the VIN please enter an alphanumeric of length 17"
			+ "For multiple rides please seperate each entry with a comma"
			+ "Must have same amount of entries for every field '<BR>";

	public static String RecordRide(String datesIn, String distsIn, String costsIn, String numOfPeoplesIn,
			String vinsIn, Connector con) {
		String[] dates = datesIn.split(",");
		String[] dists = distsIn.split(",");
		String[] costs = costsIn.split(",");
		String[] numOfPeoples = numOfPeoplesIn.split(",");
		String[] vins = vinsIn.split(",");
		String query = "";

		if (dates.length == dists.length && dists.length == costs.length && numOfPeoples.length == vins.length) {
			for (int i = 0; i <= dates.length; i++) {
				if (!checkRide(dates[i].split("\\s+")[0], dates[i].split("\\s+")[1], dists[i], costs[i], numOfPeoples[i], vins[i])) {
					rides.clear();
					return errorReturn;
				}

				try {
					query = String.format("select * from UC where VIN = '%s'", vins[i]);
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if (!rs.next()) {
						rides.clear();
						return errorReturn;
					}
					query = String.format("select * from Rides where date = '%s' AND VIN = '%s'",
							dates[i].split(",")[0] + " " + dates[i].split("\\s+")[1], vins[i]);
					rs = con.stmt.executeQuery(query);
					rsmd = rs.getMetaData();
					if (rs.next()) {
						rides.clear();
						return errorReturn;
					}
					query = String.format(
							"select c.ID, d.ID, h.start, h.end from UC c, UD d, hours_Of_Operation h where c.VIN = '%s' AND c.ID = d.ID AND d.ID = h.ID AND h.start <= TIME('%s') AND h.end >= TIME('%s')",
							vins[i], dates[i].split("\\s+")[1], dates[i].split("\\s+")[1]);
					rs = con.stmt.executeQuery(query);
					rsmd = rs.getMetaData();
					if (!rs.next()) {
						rides.clear();
						return errorReturn;
					}
				} catch (SQLException e) {
					rides.clear();
					return errorReturn;
				}

				rides.add(new Ride(dists[i], dates[i], costs[i], numOfPeoples[i], vins[i]));
			}
			String returnRides = "Do these rides look okay to you? \n";
			for (Ride r : rides)
				returnRides = returnRides + r.toString() + "\n";
			return returnRides + "'<BR>";
		}

		return "";
	}

	public static String approveRides(Boolean yesOrNo, Connector con, String loginName) {
		String query = "";
		String tripString = "";

		if (!yesOrNo) {
			rides.clear();
			return "";
		}

		for (Ride r : rides) {
			query = String.format(
					"INSERT INTO Rides (dist, date, cost, numOfPeople, VIN, loginName) VALUES (%d, '%s', %.2f, %d, '%s', '%s')",
					Integer.parseInt(r.getDist()), r.getDate() + " " + r.getDate().split("\\s+")[1],
					Float.parseFloat(r.getCost()), Integer.parseInt(r.getNumOfPeople()), r.getVIN(), loginName);
			try {
				con.stmt.executeUpdate(query);
				query = String.format("SELECT tripID, date FROM Rides ORDER BY tripID DESC LIMIT %d", rides.size());
				ResultSet rs = con.stmt.executeQuery(query);
				while (rs.next()) {
					tripString = tripString + String.format("Your tripID for the trip on %s is: %d \n",
							rs.getDate("date").toString(), rs.getInt("tripID"));
				}
			} catch (SQLException e) {
				rides.clear();
				return errorReturn;
			}
		}
		return tripString + "'<BR>";
	}

	private static boolean checkRide(String date, String time, String dist, String cost, String numOfPeople,
			String vin) {
		Boolean dateOkay = true;
		Boolean timeOkay = true;
		Boolean distOkay = true;
		Boolean numOfPeopleOkay = true;
		Boolean vinOkay = true;
		Boolean costOkay = true;

		if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date))
			dateOkay = false;
		if (!Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time))
			timeOkay = false;
		try {
			Integer.parseInt(dist);
		} catch (NumberFormatException e) {
			distOkay = false;
		}
		try {
			Integer.parseInt(numOfPeople);
		} catch (NumberFormatException e) {
			numOfPeopleOkay = false;
		}
		try {
			Float.parseFloat(cost);
		} catch (NumberFormatException e) {
			costOkay = false;
		}
		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
			vinOkay = false;

		if (!costOkay || !dateOkay || !timeOkay || !distOkay || !numOfPeopleOkay || !vinOkay)
			return false;

		return true;
	}

}
