package cs5530;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Rides {

	
	public static String RecordRide(String datesIn, String timesIn, String distsIn, String costsIn, String numOfPeoplesIn, String vinsIn) {
		String[] dates = datesIn.split(",");
		String[] times = timesIn.split(",");
		String[] dists = distsIn.split(",");
		String[] costs = costsIn.split(",");
		String[] numOfPeoples = numOfPeoplesIn.split(",");
		String[] vins = vinsIn.split(",");
		
		return "";
	}
	
	private static boolean approveRides(ArrayList<Ride> rides, Scanner scanner) {
		String cmd = "";
		System.out.println(ls + "These are your rides:");
		for (Ride r : rides)
			System.out.println(r.toString());

		while (true) {
			System.out.println(ls + "Do these look okay to you? Y or N?");
			if ((cmd = scanner.nextLine()).equals("Y"))
				return true;
			else if (cmd.equals("0"))
				return false;
			else if (cmd.equals("N"))
				return false;
			else
				System.out.println(ls + "Not a valid option");
		}
	}

	private static boolean checkRide(String date, String time, String dist, String cost, String numOfPeople, String vin) {
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

		if (!costOkay)
			System.out.println("Please enter a dollar amount format e.g.(12.50)");
		if (!dateOkay)
			System.out.println("Please enter a date in this format: YYYY-MM-DD");
		if (!timeOkay)
			System.out.println("Please enter a time in this format: HH:MM");
		if (!distOkay)
			System.out.println("Please enter an integer for distance in miles");
		if (!numOfPeopleOkay)
			System.out.println("Please enter an integer for the number of people");
		if (!vinOkay)
			System.out.println("Please enter an alphanumeric of length 17 for VIN");

		if (!costOkay || !dateOkay || !timeOkay || !distOkay || !numOfPeopleOkay || !vinOkay)
			return false;

		return true;
	}
	
}
