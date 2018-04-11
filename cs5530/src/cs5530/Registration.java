package cs5530;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Registration {

	public static String register(Connector con, String loginName, String password, String phone, String firstName,
			String lastName, String timesIn, Boolean isDriver, String address, String phoneNumber) {

		if (!checkInfo(isDriver, phone, password, firstName, lastName, loginName, isDriver, con)) {

			if (isDriver) {
				return "Information entered was in the wrong format \n "
						+ "Please enter a phone number in the given format: (xxx)xxx-xxxx \n"
						+ "Please enter a password with at least 8 characters \n"
						+ "Please enter a driver ID greater than 1 \n '<BR>";
			}

			return "Information entered was in the wrong format \n "
					+ "Please enter a phone number in the given format: (xxx)xxx-xxxx \n"
					+ "Please enter a password with at least 8 characters \n"
					+ "Please enter a loginName with at least 1 non-digit character \n '<BR>";
		}
		
		if(isDriver) {
			if(!checkHours(timesIn, con, loginName, address, phoneNumber, firstName, lastName, password)) {
				return "Information entered was in the wrong format \n "
						+ "Please enter hours in the following format 'HH:MM,HH:MM' for start and end times \n"
						+ "If you would like to add multiple times, add a ';' in between each entry. '<BR>";
			}
			
			
			String query = String.format("select * from UD where loginName = '%s'", loginName);
			try {
			ResultSet rs = con.stmt.executeQuery(query);
			rs.next();
			int ID = rs.getInt("ID");
			return "Your driver ID is " + ID;
			}
			catch(SQLException e) {
				
			}
		}	
		// success
		return "";
	}

	private static Boolean checkHours(String timesIn, Connector con, String loginName, String address, String phoneNumber, String firstName, String lastName, String password) {
		String[] times;
		ArrayList<String> timesStart = new ArrayList<String>();
		ArrayList<String> timesEnd = new ArrayList<String>();
		ArrayList<String> OkayTimes = new ArrayList<String>();

		try {
			times = timesIn.split(";");
			for (String s : times) {
				timesStart.add(s.split(",")[0]);
				timesEnd.add(s.split(",")[1]);
				if (!Pattern.matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", timesStart.get(timesStart.size() - 1)))
					return false;
				if (!Pattern.matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", timesEnd.get(timesStart.size() - 1)))
					return false;
			}
		} catch (Exception e) {
			return false;
		}

		if (timesStart.size() != timesEnd.size())
			return false;

		String startTime = "";
		String endTime = "";
		String startHour = "";
		String endHour = "";
		String startMinute = "";
		String endMinute = "";
		int startH = 0;
		int startM = 0;
		int endH = 0;
		int endM = 0;

		for (int i = 0; i < timesStart.size(); i++) {

			startTime = timesStart.get(i);
			endTime = timesEnd.get(i);

			String sHourStart = "";
			String sMinStart = "";
			String sHourEnd = "";
			String sMinEnd = "";
			int sHStart = 0;
			int sHEnd = 0;
			int sMStart = 0;
			int sMEnd = 0;

			if (startTime.substring(0, 2).charAt(0) == '0')
				startHour = Character.toString(startTime.charAt(1));
			else
				startHour = startTime.substring(0, 2);

			if (startTime.substring(3, 5).charAt(0) == '0')
				startMinute = Character.toString(startTime.charAt(4));
			else
				startMinute = startTime.substring(3, 5);

			if (endTime.substring(0, 2).charAt(0) == '0')
				endHour = Character.toString(endTime.charAt(1));
			else
				endHour = endTime.substring(0, 2);

			if (endTime.substring(3, 5).charAt(0) == '0')
				endMinute = Character.toString(endTime.charAt(4));
			else
				endMinute = endTime.substring(3, 5);

			startH = Integer.parseInt(startHour);
			startM = Integer.parseInt(startMinute);

			endH = Integer.parseInt(endHour);
			endM = Integer.parseInt(endMinute);

			if (startH > endH) {
				// System.out.println(ls + "Your starting time cannot be greater than your
				// ending time");
				return false;
			} else if (startH == endH) {
				if (startM >= endM) {
					// System.out.println(ls + "Your starting time cannot be greater than your
					// ending time");
					return false;
				}
			}

			for (int j = 0; j < OkayTimes.size(); j++) {

				String s = OkayTimes.get(j);

				if (s.substring(0, 2).charAt(0) == '0')
					sHourStart = Character.toString(s.charAt(1));
				else
					sHourStart = s.substring(0, 2);

				if (s.substring(3, 5).startsWith("0"))
					sMinStart = Character.toString(s.charAt(4));
				else
					sMinStart = s.substring(3, 5);

				if (s.substring(6, 8).startsWith("0"))
					sHourEnd = Character.toString(s.charAt(7));
				else
					sHourEnd = s.substring(6, 8);

				if (s.substring(9, 11).startsWith("0"))
					sMinEnd = Character.toString(s.charAt(8));
				else
					sMinEnd = s.substring(9, 11);

				sHStart = Integer.parseInt(sHourStart);
				sHEnd = Integer.parseInt(sHourEnd);
				sMStart = Integer.parseInt(sMinStart);
				sMEnd = Integer.parseInt(sMinEnd);

				if (startH == sHStart && endH == sHEnd && startM == sMStart && endM == sMEnd)
					continue;

				if (startH > sHStart) {
					if (startH < sHEnd) {
						return false;
					} else if (startH == sHEnd) {
						if (startM > sMStart) {

							if (startM < sMEnd || startM == sMEnd)
								return false;
						} else if (startM <= sMStart) {
							return false;
						}
					}
				}
				if (startH < sHStart) {
					if (endH > sHStart) {
						return false;
					} else if (endH == sHStart) {
						if (endM < sMEnd) {
							if (endM > sMStart || endM == sMStart)
								return false;
						} else if (endM >= sMEnd) {
							return false;
						}
					}

				}
				if (startH == sHStart) {
					if (startM > sMStart) {
						return false;
					}
					if (startM < sMStart) {
						if (endM > sMStart)
							return false;
					}
				}
			}
			OkayTimes.add(sHourStart + ":" + sMinStart + " " + sHourEnd + ":" + sMinEnd);
		}
		
			String query = "";
			query = String.format(
					"insert into UD (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
					address, phoneNumber, firstName, lastName, loginName, password);
			try {
			con.stmt.executeUpdate(query);
			}
			catch(SQLException e) {
				return false;
			}
			if(!addHours(con, OkayTimes, loginName)) {
				return false;
			}

		return true;
	}

	private static boolean checkInfo(Boolean driver, String phoneNumber, String password, String firstName,
			String lastName, String loginName, Boolean isDriver, Connector con) {
		String query = "";
		String cmd = "";
		Boolean phoneNumberOkay = true;
		Boolean passwordOkay = true;
		Boolean loginNameOkay = true;

		if (!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber))
			phoneNumberOkay = false;
		if (!Pattern.matches(".{8,}", password))
			passwordOkay = false;
		if (!Pattern.matches("^(?=.*[a-z]).+$", loginName))
			loginNameOkay = false;

		if (!phoneNumberOkay || !passwordOkay || !loginNameOkay) {
			return false;
		} else
			
			//check if user exists
			if(isDriver) {
				query = String.format("select * from UD where loginName = '%s'", loginName);
				ResultSet rs;
				try {
					rs = con.stmt.executeQuery(query);
					
					if (rs.next()) return false;
				}
				catch(SQLException e) {
					return false;
				}
			}
			else {
				query = String.format("select * from UU where loginName = '%s'", loginName);
				ResultSet rs;
				try {
					rs = con.stmt.executeQuery(query);
					
					if (rs.next()) return false;
				}
				catch(SQLException e) {
					return false;
				}
			}
			return true;
	}

	private static boolean addHours(Connector con, ArrayList<String> hours, String loginName) {
		String query = "";
		String cmd = "";
		int ID;
		
		try {
			ID = Integer.parseInt(loginName);
		}
		catch(NumberFormatException e){
			return false;
		}
		
		for (String s : hours) {
			String startTime = "";
			String endTime = "";

			startTime = s.substring(0, 5);
			endTime = s.substring(6);

			System.out.println("startTime: " + startTime + ", endTime: " + endTime);
			query = String.format(
					"INSERT INTO hours_Of_Operation (start, end, ID) VALUES ('%s', '%s', %d)",
					startTime, endTime, ID);

			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
					return false;
			}
		}
		return true;
	}
}
