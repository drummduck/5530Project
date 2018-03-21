package cs5530;

import java.lang.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.*;

public class UberSkool {
	
	static String phoneNumber = "";
	static String password = "";
	static String firstName = "";
	static String lastName = "";
	static String loginName = "";
	static String address = "";
	static int ID = -1;
	static Boolean isDriver = false;
	static LoginState loginState = LoginState.MENU;
	
	static String ls = System.getProperty("line.separator");

	
	//TODO:Figure out some stupid random algorithm to give users awards.....

	/**
	 * @param args
	 */
	public static void displayMenu() {
		System.out.println(ls + "Welcome to UberSkool bruh!" + ls);
		System.out.println("What would you like to do today?");
		System.out.println("0: Quit Program");
		System.out.println("1: Login as registered user");
		System.out.println("2: Login as registered driver");
		System.out.println("3: Register as user");
		System.out.println("4: Register as driver");
		System.out.println("Please enter the number next to the option you want.");
	}
	
	public static void userMenu() {
		System.out.println(ls + "Welcome user " + firstName + " " + lastName + ls);
		System.out.println("What can we do for you?");
		System.out.println("0: Logout");
		System.out.println("1: Record reservations");
		System.out.println("2: Record rides");
		System.out.println("3: Declare a favorite UC");
		System.out.println("4: Rate user feedback");
		System.out.println("5: Rate user");
		System.out.println("6: Search for UC");
		System.out.println("7: Degree of seperation from user");
		System.out.println("8: Statistics");
	}
	
	public static void driverMenu(){
		System.out.println(ls + "Welcome driver " + firstName + " " + lastName + ls);
		System.out.println("What can we do for you?");
		System.out.println("0: Logout");
		System.out.println("1: Register a new car");
		System.out.println("2: Update car information");
	}
	
	enum LoginState{
		MENU, LOGGINGIN, REGISTERING, USERMENU, DRIVERMENU
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connector2 con = null;
		String choice;
		String cname;
		String dname;
		String sql = null;
		int c = 0;
		try {
			con = new Connector2();
			System.out.println("Database connection established");

			Scanner scanner = new Scanner(System.in);
			displayMenu();

			while (true) {
				if(scanner.hasNextLine()) choice = scanner.nextLine();
				else continue;
				try {
					c = Integer.parseInt(choice);
				} catch (Exception e) {
					System.out.println("Not a valid option, please select from the following:");
					System.out.println("0: Quit Program");
					System.out.println("1: Login as registered user");
					System.out.println("2: Login as registered driver");
					System.out.println("3: Register as user");
					System.out.println("4: Register as driver" + ls);
					continue;
				}
				if (c < 0 | c > 4) {
					System.out.println("Not a valid option, please select from the following:");
					System.out.println("0: Quit Program");
					System.out.println("1: Login as registered user");
					System.out.println("2: Login as registered driver");
					System.out.println("3: Register a user");
					System.out.println("4: Register a driver" + ls);
					continue;
				}
				else if (c == 0) {
					if(loginState == LoginState.USERMENU || loginState == LoginState.DRIVERMENU) {
						clearUser();
						displayMenu();
						loginState = LoginState.MENU;
					}
					else {
						clearUser();
						System.out.println("Good Bye");
						return;
					}
				} else if (c == 1) {
					if(loginState == LoginState.DRIVERMENU) {
						registerCar(con, scanner);
						driverMenu();
					}
					else if(loginState == LoginState.USERMENU) {
						reserve(con, scanner);
						userMenu();
					}
					else if(loginState == LoginState.MENU) {
						loginState = LoginState.LOGGINGIN;
						if(Login(con, scanner, false)) {
							loginState = LoginState.USERMENU;
							userMenu();
						}
						else{
							clearUser();
							loginState = LoginState.MENU;
							displayMenu();
						}
					}
				} else if (c == 2) {
					if(loginState == LoginState.DRIVERMENU) {
						updateCar(con,scanner);
						driverMenu();
					}
					else if(loginState == LoginState.USERMENU) {
						recordRides(con, scanner);
						userMenu();
					}
					else {
						if(Login(con, scanner, true)){
							loginState = LoginState.DRIVERMENU;
							driverMenu();
						}
						else {
							clearUser();
							loginState = LoginState.MENU;
							displayMenu();
						}
					}
				} else if(c == 3){
					if(loginState == LoginState.USERMENU) {
						
					}
					if(!register(con, scanner, false)) {
						clearUser();
						loginState = LoginState.MENU;
						displayMenu();
					}
					else {
						System.out.println(ls + "User successfully created! Returning to main menu!");
						loginState = LoginState.MENU;
						clearUser();
						displayMenu();
					}
				} else if(c == 4){
					if(!register(con, scanner, true)) {
						clearUser();
						loginState = LoginState.MENU;
						displayMenu();
					}
					else {
						System.out.println(ls + "Driver successfully created! Returning to main menu!");
						loginState = LoginState.MENU;
						clearUser();
						displayMenu();					}
				}
					else {
					System.out.println("EoM");
					con.stmt.close();

					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Either connection error or query execution error!");
		} finally {
			if (con != null) {
				try {
					con.closeConnection();
					System.out.println("Database connection terminated");
				}

				catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	public static boolean Login(Connector2 con, Scanner scanner, Boolean driver) throws IOException
	{
		String cmd = "";
		String query = "";
		while(true)
		{
			try {

				if(!driver) System.out.println(ls +"Please enter your login name.");
				else System.out.println(ls +"Please enter your driver ID.");
				if((cmd = scanner.nextLine()).equals("0")) return false;
				else loginName = cmd;
				System.out.println(ls + "Please enter your password.");
				if((cmd = scanner.nextLine()).equals("0")) return false;
				else password = cmd;
				if(!checkLoginInfo(loginName, password, driver)){
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) break;
						else if(cmd.equals("0")) return false;
						else if(cmd.equals("N")) return false;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
				else {
					if(!driver)query = "select * from UU where loginName = " + "\"" + loginName + "\"" + " and password = " + "\"" + password + "\"";
					else query = "select * from UD where ID = " + loginName + " and password = " + "\"" + password + "\"";
					
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rs.next()) { 
						 firstName = rs.getString("firstName"); 
						 lastName = rs.getString("lastName");
						 if(driver) {
							 ID = rs.getInt("ID");
							 isDriver = true;
						 }
						 loginState = LoginState.USERMENU;
						 return true;
						} else {
						if(!driver){
							System.out.println(ls + "We could not find a user with those credentials. Would you like to try again? Y or N?");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return false;
								else if(cmd.equals("N")) return false;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						else {
							System.out.println(ls + "We could not find a driver with those credentials. Would you like to try again? Y or N?");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return false;
								else if(cmd.equals("N")) return false;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
					}
				}
			} catch (SQLException e) {
				System.out.println(ls + "There was an issue with your lookup." + ls);
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return false;
					else if(cmd.equals("N")) return false;
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
		}
	}
	
	public static boolean checkLoginInfo(String loginName, String password, Boolean driver) {
		Boolean loginNameOkay = true;
		Boolean passwordOkay = true;
		
		if(!driver) {
			if(!Pattern.matches("^(?=.*[a-z]).+$", loginName)) loginNameOkay = false;
		}
		else {
			try {
				Integer.parseInt(loginName);
			}
			catch(NumberFormatException e) {
				loginNameOkay = false;
			}
		}
		if(!Pattern.matches(".{8,}", password)) passwordOkay = false;

		if(!passwordOkay) System.out.println("Please enter a password with at least 8 characters");
		if(driver && !loginNameOkay) System.out.println("Please enter a digit greater than 1 for driver ID");
		if(!driver && !loginNameOkay) System.out.println("Please enter a login name with at least 1 non digit character");
		
		if(!loginNameOkay || !passwordOkay) return false;
		return true;
	}
	
	public static boolean register(Connector2 con, Scanner scanner, Boolean driver) {
		String cmd = "";
		String query = "";
		Boolean loginCheck = false;
		Boolean passwordCheck = false;
		while(true) {
			if(driver) {
				System.out.println(ls +"Do you already have a user account you would like to register as a driver? Y or N?");
				if((cmd = scanner.nextLine()).equals("Y")) {
					try {
						if(Login(con, scanner, driver)){
							query = String.format("select * from UD where loginName = '%s'", loginName);
							ResultSet rs = con.stmt.executeQuery(query);
							ResultSetMetaData rsmd = rs.getMetaData();
							if(rs.next()) { 
								System.out.println(ls + "A driver with that login name already exists. Would you like to try again? Y or N?");
								clearUser();
								if((cmd = scanner.nextLine()).equals("Y")){
									continue;
								}
								else if(cmd.equals("N")) return false;
							}
							else {
								query = String.format("insert into UD (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
										address, phoneNumber, firstName, lastName, loginName, password);
								con.stmt.executeQuery(query);
								query = String.format("select * from UD where loginName = %s", loginName);
								rs = con.stmt.executeQuery(query);
								rsmd = rs.getMetaData();
								ID = rs.getInt("ID");
								System.out.println("Your driver ID is " + ID);
								return true;
							}		
						} else return false;
						
					} catch (IOException e) {
						System.out.println(ls + "There was an issue registering your user" + ls);
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return false;
							else if(cmd.equals("N")) return false;
							else System.out.println(ls + "Not a valid option");
						}
						continue;					
					}	 catch (SQLException e) {
							System.out.println(ls + "There was an issue registering your user" + ls);
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return false;
								else if(cmd.equals("N")) return false;
								else System.out.println(ls + "Not a valid option");
							}
							continue;					
						}
				}
				else if(cmd.equals("N")){
					System.out.println(ls +"Please enter a login name you would like to use. (must have at least one character that isn't a number)");
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
						}
					else loginName = cmd;
					System.out.println(ls + "Please enter a password that has at least eight characters.");
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
					}
					else password = cmd;
					System.out.println(ls + "Please enter your phone number e.g. (123)-456-7890");
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
					}
					else phoneNumber = cmd;
					System.out.println(ls + "Please enter your firstName");
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
					}
					else firstName = cmd;
					System.out.println(ls + "Please enter your lastName");
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
					}
					else lastName = cmd;
					System.out.println(ls + "Please enter an address" + ls);
					if((cmd = scanner.nextLine()).equals("0")) {
						return false;
					}
					else address = cmd;
					if(!checkInfo(false)) {
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return false;
							else if(cmd.equals("N")) return false;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
					else {
						try {
							query = String.format("select * from UD where loginName = '%s'", loginName);
							ResultSet rs = con.stmt.executeQuery(query);
							ResultSetMetaData rsmd = rs.getMetaData();
							if(rs.next()) { 
								System.out.println(ls + "A driver with that login name already exists.");
								while(true) {
									System.out.println(ls + "Would you like to try again? Y or N?");
									if((cmd = scanner.nextLine()).equals("Y")) break;
									else if(cmd.equals("0")) return false;
									else if(cmd.equals("N")) return false;
									else System.out.println(ls + "Not a valid option");
								}
								continue;
							}
							else {
								query = String.format("insert into UD (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
										address, phoneNumber, firstName, lastName, loginName, password);
								con.stmt.executeQuery(query);
								query = String.format("select * from UD where loginName = %s", loginName);
								rs = con.stmt.executeQuery(query);
								rsmd = rs.getMetaData();
								ID = rs.getInt("ID");
								System.out.println("Your driver ID is " + ID);
								return true;
							}	
						}
						catch (SQLException e) {
							System.out.println(ls + "There was an issue adding your driver" + ls);
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return false;
								else if(cmd.equals("N")) return false;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
					}
				}
				else if(cmd.equals("0")) return false;
				else System.out.println(ls + "Please answer Y or N or quit with 0" + ls);
			}
			
			//THIS IS FOR LOGGING IN AS USER
			else {
				System.out.println(ls +"Please enter a login name you would like to use. (must have at least one character that isn't a number)");
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
					}
				else loginName = cmd;
				System.out.println(ls + "Please enter a password that has at least eight characters.");
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
				}
				else password = cmd;
				System.out.println(ls + "Please enter your phone number e.g. (123)456-7890");
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
				}
				else phoneNumber = cmd;
				System.out.println(ls + "Please enter your firstName");
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
				}
				else firstName = cmd;
				System.out.println(ls + "Please enter your lastName");
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
				}
				else lastName = cmd;
				System.out.println(ls + "Please enter an address" + ls);
				if((cmd = scanner.nextLine()).equals("0")) {
					return false;
				}
				else address = cmd;
				if(!checkInfo(false)) {
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) break;
						else if(cmd.equals("0")) return false;
						else if(cmd.equals("N")) return false;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
				else {
					try {
						query = String.format("select * from UU where loginName = '%s' or phoneNumber = '%s'", loginName, phoneNumber);
						ResultSet rs = con.stmt.executeQuery(query);
						ResultSetMetaData rsmd = rs.getMetaData();
						if(rs.next()) { 
							System.out.println(ls + "loginName or phoneNumber exists already.");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return false;
								else if(cmd.equals("N")) return false;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						else {
							query = String.format("INSERT INTO UU (loginName, password, phoneNumber, address, firstName, lastName) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
									loginName, password, phoneNumber, address, firstName, lastName);
							con.stmt.executeUpdate(query);
							return true;
						}	
					}
					catch (SQLException e) {
						System.out.println(ls + "There was an issue adding your user" + ls);
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return false;
							else if(cmd.equals("N")) return false;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
				}
			}
		}
	}
	
	
	public static void clearUser(){
		phoneNumber = "";
		password = "";
		firstName = "";
		lastName = "";
		loginName = "";
		address = "";
		ID = -1;
		isDriver = false;
		loginState = LoginState.MENU;
	}
	
	public static boolean checkInfo(Boolean driver)
	{
		Boolean phoneNumberOkay = true;
		Boolean passwordOkay = true;
		Boolean firstNameOkay = true;
		Boolean lastNameOkay = true;
		Boolean loginNameOkay = true;
		Boolean addressOkay = true;
		
		if(!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber)) phoneNumberOkay = false;
		if(!Pattern.matches(".{8,}", password)) passwordOkay = false;
		if(!Pattern.matches("^[\\p{L} .'-]+$", firstName)) firstNameOkay = false;
		if(!Pattern.matches("^[\\p{L} .'-]+$", lastName)) lastNameOkay = false;
		if(!Pattern.matches("^(?=.*[a-z]).+$", loginName)) loginNameOkay = false;
		//Not sure if we can really restrict this if(!Pattern.matches("^[a-zA-Z0-9\\s,'-]*$", address)) addressOkay = false;
	
		
		if(!phoneNumberOkay) System.out.println("Please enter different format for phone number, e.g. (123)456-7890");
		if(!passwordOkay) System.out.println("Please enter a password with at least 8 characters");
		if(!firstNameOkay) System.out.println("Please enter a valid first name format");
		if(!lastNameOkay) System.out.println("Please enter a valid last name format");
		if(!addressOkay) System.out.println("Please enter a valid address");
		if(driver && !loginNameOkay) System.out.println("Please enter a digit greater than 1 for driver ID");
		if(!driver && !loginNameOkay) System.out.println("Please enter a login name with at least 1 non digit character");
		if(!phoneNumberOkay || !passwordOkay || !firstNameOkay || !lastNameOkay || !addressOkay || !loginNameOkay) {
			clearUser();
			return false;
		}
		else return true;
	}
	
	public static void recordRides(Connector2 con, Scanner scanner){
		String cmd = "";
		String query = "";
		String date = "";
		String time = "";
		String dist = "";
		String numOfPeople = "";
		Boolean tryAgain = false;
		String vin = "";
		String cost = "";
		ArrayList<Ride> rides = new ArrayList<Ride>();
		
		
		while(true)
		{
			System.out.println(ls + "What was the date of your ride? e.g.(YYYY-MM-DD)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else date = cmd;
			
			System.out.println(ls + "What was the time? e.g.(HH:MM)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else time = cmd;
			
			System.out.println(ls + "How much did it cost? e.g.(12.50)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			cost = cmd;
			
			System.out.println(ls + "How far did you go? (In Miles)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			dist = cmd;
			
			System.out.println(ls + "How many people went?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			numOfPeople = cmd;
			
			System.out.println(ls + "What UC did you ride with? (alphanumeric of length 17).");
			if((cmd = scanner.nextLine()).equals("0")) return;
			vin = cmd;
			
			if(!checkRide(date, time, dist, cost, numOfPeople, vin)){
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
			else {
				try {
					query = String.format("select * from UC where VIN = '%s' or date = '%s'", vin);
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(!rs.next()) { 
						System.out.println(ls + "Vehicle does not exist");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
					query = String.format("select * from Rides where date = '%s' AND VIN = '%s'", date + " " + time, vin);
					rs = con.stmt.executeQuery(query);
					rsmd = rs.getMetaData();
					if(!rs.next()) { 
						System.out.println(ls + "A ride with that vehicle was already recorded at this time");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
					query = String.format("select c.ID, d.ID, h.start, h.end from UC c, UD d, hours_Of_Operation h where c.VIN = '%s' AND c.ID = d.ID AND d.ID = h.ID AND h.start <= '%s' AND h.end >= '%s'", vin, time, time);
					rs = con.stmt.executeQuery(query);
					rsmd = rs.getMetaData();
					if(!rs.next()) { 
						System.out.println(ls + "A UC is not available during that time");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
				}
				catch (SQLException e) {
					System.out.println(ls + "There was an issue adding your ride" + ls);
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) break;
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
				rides.add(new Ride(dist, date, cost, numOfPeople, vin));
				while(true) {
					System.out.println(ls + "Would you like to add another ride? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) {
						if(rides.isEmpty()) return;
						else if(!approveRides(rides, scanner)) {
							rides.clear();
							while(true) {
								System.out.println(ls + "Would you like to try adding rides again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) {
									tryAgain = true;
									break;
								}
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							if(tryAgain) break;
						}
						else {
							for(Ride r: rides) {
								query = String.format("INSERT INTO rides (dist, date, cost, numOfPeople, VIN, loginName) VALUES (%d, %s, %.2f, %d, %s, %s)", Integer.parseInt(dist), date + " " + time, Float.parseFloat(cost), Integer.parseInt(numOfPeople), vin, loginName);
								try {
									con.stmt.executeUpdate(query);
								} catch (SQLException e) {
									rides.clear();
									System.out.println(ls + "There was an issue adding your rides" + ls);
									while(true) {
										System.out.println(ls + "Would you like to try adding rides again? Y or N?");
										if((cmd = scanner.nextLine()).equals("Y")) {
											tryAgain = true;
											break;
										}
										else if(cmd.equals("0")) return;
										else if(cmd.equals("N")) return;
										else System.out.println(ls + "Not a valid option");
									}
									if(tryAgain) break;
								}
							}
						}
					}
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
		}
	}
	
	public static boolean approveRides(ArrayList<Ride> rides, Scanner scanner) {
		String cmd = "";
		System.out.println(ls + "These are your rides:");
		for(Ride r: rides) System.out.println(r.toString());

		while(true) {
			System.out.println(ls + "Do these look okay to you? Y or N?");
			if((cmd = scanner.nextLine()).equals("Y")) return true;
			else if(cmd.equals("0")) return false;
			else if(cmd.equals("N")) return false;
			else System.out.println(ls + "Not a valid option");
		}
	}
	
	public static boolean checkRide(String date, String time, String dist, String cost, String numOfPeople, String Car){
		Boolean dateOkay = true;
		Boolean timeOkay = true;
		Boolean distOkay = true;
		Boolean numOfPeopleOkay = true;
		Boolean carOkay = true;
		Boolean costOkay = true;
		
		if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date)) dateOkay = false;
		if(!Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time)) timeOkay = false;
		try {
		Integer.parseInt(dist);
		}
		catch(NumberFormatException e) {
			distOkay = false;
		}
		try {
			Integer.parseInt(numOfPeople);
		}
		catch(NumberFormatException e){
			numOfPeopleOkay = false;
		}
		try {
			Float.parseFloat(cost);
		}
		catch(NumberFormatException e) {
			costOkay = false;
		}
		if(!Pattern.matches("\\d{1,3}[,\\.]?(\\d{1,2})?", time)) timeOkay = false;
		
		if(!Pattern.matches("^[a-zA-Z0-9]*$", date) || date.length() != 17) carOkay = false;
		if(!costOkay) System.out.println("Please enter a dollar amount format e.g.(12.50)");
 		if(!dateOkay) System.out.println("Please enter a date in this format: YYYY-MM-DD");
		if(!timeOkay) System.out.println("Please enter a time in this format: HH:MM");
		if(!distOkay) System.out.println("Please enter an integer for distance in miles");
		if(!numOfPeopleOkay) System.out.println("Please enter an integer for the number of people");
		if(!carOkay) System.out.println("Please enter an alphanumeric of length 17 for VIN");
		
		return false;
	}
	
	public static void reserve(Connector2 con, Scanner scanner) {
		Boolean approved = false;
		String cmd = "";
		String query = "";
		String date = "";
		String vin = "";
		String time = "";
		ArrayList<String> reservations = new ArrayList<String>();
		while(true)
		{
			System.out.println(ls + "What is the date of this reservation? e.g.(YYYY-MM-DD)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else date = cmd;
			
			System.out.println(ls + "What time? e.g.(HH:MM)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else time = cmd;
			
			System.out.println("Which car would you like to reserve? Please enter a VIN");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else vin = cmd;
			
			
			if(!reserveCheck(date, time, vin)) {
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
			else{
				try {
					query = String.format("select * from Reservations where VIN = '%s' or date = '%s'", vin, date);
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rs.next()) { 
						System.out.println(ls + "Reservation already made for that vehicle during that time");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
					query = String.format("select * from UC where VIN = '%s'", vin);
					rs = con.stmt.executeQuery(query);
					rsmd = rs.getMetaData();
					if(!rs.next()) { 
						System.out.println(ls + "Vehicle does not exist");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
				}
				catch (SQLException e) {
					reservations.clear();
					System.out.println(ls + "There was an issue adding your reservation" + ls);
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) break;
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
				if(!reservations.contains(String.format("UC VIN: %s, DateTime: %s", vin, date + " " + time)))
				reservations.add(String.format("UC VIN: %s, DateTime: %s", vin, date + " " + time));
				while(true) {
					System.out.println(ls + "Would you like to add another reservation? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) {
						if(reservations.isEmpty()) return;
						else{
							if(approveReservations(reservations, scanner)){
							approved = true;
							break;
							}
							else {
								while(true) {
									System.out.println(ls + "Would you like to try adding reservations again? Y or N?");
									if((cmd = scanner.nextLine()).equals("Y")) break;
									else if(cmd.equals("0")) return;
									else if(cmd.equals("N")) return;
									else System.out.println(ls + "Not a valid option");
								}
								reservations.clear();
								break;
							}
						}
					}
					else System.out.println(ls + "Not a valid option");
				}
				if(!approved) continue;
			}
			
			if(approved) {
				try {
					for(String s: reservations) {
						query = String.format("INSERT into Reservations (VIN, loginName, date) VALUES ('%s', '%s', '%s')", s.substring(8,24), loginName, s.substring((s.length()-1)-15));
						con.stmt.executeUpdate(query);
					}
				}
				catch (SQLException e) {
					reservations.clear();
					System.out.println(ls + "There was an issue adding your reservations" + ls);
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) {
							break;
						}
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
			}
				
		}
	}
	
	public static boolean reserveCheck(String date, String time, String vin){
		Boolean dateOkay = true;
		Boolean vinOkay = true;
		Boolean timeOkay = true;
		
		if(!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) vinOkay = false;
		if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date)) dateOkay = false;
		if(!Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time)) timeOkay = false;
		
		if(!vinOkay) System.out.println("Please enter an alphanumeric value with 17 characters for VIN");
		if(!dateOkay) System.out.println("Please enter a date in this format: YYYY-MM-DD");
		if(!timeOkay) System.out.println("Please enter a time in this format: HH:MM");

		if(!vinOkay || !dateOkay || !timeOkay) return false;
		return true;
	}
	
	public static boolean approveReservations(ArrayList<String> reservations, Scanner scanner){
		String cmd = "";
		System.out.println(ls + "These are your reservations:");
		for(String s: reservations) System.out.println(s);

		while(true) {
			System.out.println(ls + "Do these look okay to you? Y or N?");
			if((cmd = scanner.nextLine()).equals("Y")) return true;
			else if(cmd.equals("0")) return false;
			else if(cmd.equals("N")) return false;
			else System.out.println(ls + "Not a valid option");
		}
	}
	
	public static void registerCar(Connector2 con, Scanner scanner) {
		String cmd = "";
		String query = "";
		String vin = "";
		String category = "";
		String make = "";
		String model = "";
		String year = "";
		
		while(true) {
			System.out.println(ls + "What is the VIN of your vehicle?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else vin = cmd;
			
			System.out.println(ls + "What is the category of your vehicle? (economy, confort, or luxury)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else category = cmd;
			
			System.out.println(ls + "What is the make of your vehicle?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else make = cmd;
			
			System.out.println(ls + "What is the model of your vehicle?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else model = cmd;
			
			System.out.println(ls + "What is the year of your vehicle?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else year = cmd;
			
			if(!checkCarRegistration(scanner, vin, category, year)) {
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
			else {
				query = String.format("select * from UC where VIN = '%s'", vin);
				ResultSet rs;
				try {
					rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rs.next()) { 
						System.out.println(ls + "Vehicle already exists");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
					}
				}
				catch(SQLException e) {
					System.out.println(ls + "There was an issue registering your vehicle" + ls);
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) {
							break;
						}
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
				query = String.format("INSERT INTO UC (VIN, ID, category, make, model, year) VALUES (%s, %s, %s, %s, %s, %d", vin, ID, category, make, model, Integer.parseInt(year));
				try {
					 con.stmt.executeUpdate(query);
				}
				catch(SQLException e) {
					System.out.println(ls + "There was an issue registering your vehicle" + ls);
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) {
							break;
						}
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
			}
		}
	}
	
	public static boolean checkCarRegistration(Scanner scanner, String vin, String category, String year) {
		Boolean vinOkay = true;
		Boolean categoryOkay = true;
		Boolean yearOkay = true;
		
		if(!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) vinOkay = false;
		if(!category.equalsIgnoreCase("economy") && !category.equalsIgnoreCase("comfort") && !category.equalsIgnoreCase("luxury")) categoryOkay = false;
		try {
			Integer.parseInt(year);
		}
		catch(NumberFormatException e){
			yearOkay = false;
		}
		
		if(!vinOkay) System.out.println("Please enter an alphanumeric value with 17 characters for VIN");
		if(!categoryOkay) System.out.println("Please enter economy, comfort, or luxury");
		if(!yearOkay) System.out.println("Please enter a valid year e.g.(1991");
		
		if(!vinOkay || !categoryOkay || !yearOkay) return false;
		
		return true;
	}
	
	public static void updateCar(Connector2 con, Scanner scanner) {
		String cmd = "";
		String query = "";
		String vin = "";
		while(true) {
			System.out.println(ls + "What vehicle would you like to update information on?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else vin = cmd;
			if(!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) {
				System.out.println(ls + "Please enter an alphanumeric value with 17 characters for VIN" + ls + "Would you like to try again? Y or N?");
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}
				continue;
			}
			else {
				query = String.format("Select * from UC where ID = %s AND VIN = %s", ID, vin);
				try {
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(!rs.next()) { 
						System.out.println(ls + "Vehicle does not exist or does not belong to you");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;
					}
				}
				catch(SQLException e) {
					System.out.println(ls + "There was an issue with your vehicle lookup");
					while(true) {
						System.out.println(ls + "Would you like to try again? Y or N?");
						if((cmd = scanner.nextLine()).equals("Y")) break;
						else if(cmd.equals("0")) return;
						else if(cmd.equals("N")) return;
						else System.out.println(ls + "Not a valid option");
					}
					continue;
				}
		
				System.out.println("What information would you like to modify on it today?");
				System.out.println("1: VIN");
				System.out.println("2: Category");
				System.out.println("3: Make");
				System.out.println("4: Model");
				System.out.println("5: Year");
				cmd = scanner.nextLine();
				switch(cmd) {
					case("0"): return;
					case("VIN"):{
						String newVin = "";
						System.out.println(ls + "What would you like to change your VIN to?");
						if((cmd = scanner.nextLine()).equals("0")) return;
						else newVin = cmd;
						if(!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) {
							System.out.println(ls + "Please enter an alphanumeric value with 17 characters for VIN" + ls);
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						else {
							query = String.format("Select * from UC where VIN = '%s'", newVin);
							try {
								ResultSet rs = con.stmt.executeQuery(query);
								ResultSetMetaData rsmd = rs.getMetaData();
								if(rs.next()) { 
									System.out.println(ls + "Vehicle exists already");
									while(true) {
										System.out.println(ls + "Would you like to try again? Y or N?");
										if((cmd = scanner.nextLine()).equals("Y")) break;
										else if(cmd.equals("0")) return;
										else if(cmd.equals("N")) return;
										else System.out.println(ls + "Not a valid option");
									}
									continue;
								}
							}
							catch(SQLException e) {
								System.out.println(ls + "There was an issue with your vehicle update");
								while(true) {
									System.out.println(ls + "Would you like to try again? Y or N?");
									if((cmd = scanner.nextLine()).equals("Y")) break;
									else if(cmd.equals("0")) return;
									else if(cmd.equals("N")) return;
									else System.out.println(ls + "Not a valid option");
								}
								continue;
							}
							
							query = String.format("UPDATE UC SET VIN = '%s' where VIN = '%s'", newVin, vin);
							try {
								con.stmt.executeUpdate(query);
							}
							catch(SQLException e) {
								System.out.println(ls + "There was an issue with your vehicle update");
								while(true) {
									System.out.println(ls + "Would you like to try again? Y or N?");
									if((cmd = scanner.nextLine()).equals("Y")) break;
									else if(cmd.equals("0")) return;
									else if(cmd.equals("N")) return;
									else System.out.println(ls + "Not a valid option");
								}
								continue;
							}
						}
						break;
					}
					case("Category"):{
						String category = "";
						System.out.println(ls + "What would you like to change your Category to?");
						if((cmd = scanner.nextLine()).equals("0")) return;
						else category = cmd;
						if(!category.equalsIgnoreCase("economy") && !category.equalsIgnoreCase("comfort") && !category.equalsIgnoreCase("luxury")) {
							System.out.println("Please enter economy, comfort, or luxury");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						else {
							query = String.format("UPDATE UC SET category = '%s' where VIN = '%s'", category, vin);
							try {
								con.stmt.executeUpdate(query);
							}
							catch(SQLException e) {
								System.out.println(ls + "There was an issue with your vehicle update");
								while(true) {
									System.out.println(ls + "Would you like to try again? Y or N?");
									if((cmd = scanner.nextLine()).equals("Y")) break;
									else if(cmd.equals("0")) return;
									else if(cmd.equals("N")) return;
									else System.out.println(ls + "Not a valid option");
								}
								continue;
							}
						}
						
						break;
					}
					case("Make"):{
						String make = "";
						System.out.println(ls +"What would you like to change your Make to?");
						if((cmd = scanner.nextLine()).equals("0")) return;
						else make = cmd;
						query = String.format("UPDATE UC SET make = '%s' where VIN = '%s'", make, vin);
						try {
							con.stmt.executeUpdate(query);
						}
						catch(SQLException e) {
							System.out.println(ls + "There was an issue with your vehicle update");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						break;
					}
					case("Model"):{
						String model = "";
						System.out.println(ls +"What would you like to change your Model to?");
						if((cmd = scanner.nextLine()).equals("0")) return;
						else model = cmd;
						query = String.format("UPDATE UC SET model = '%s' where VIN = '%s'", model, vin);
						try {
							con.stmt.executeUpdate(query);
						}
						catch(SQLException e) {
							System.out.println(ls + "There was an issue with your vehicle update");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						break;
					}
					case("Year"):{
						String year = "";
						System.out.println(ls +"What would you like to change your Year to?");
						if((cmd = scanner.nextLine()).equals("0")) return;
						else year = cmd;
						try {
							Integer.parseInt(year);
						}
						catch(NumberFormatException e){
							System.out.println("Please enter a valid year e.g.(1991");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
						query = String.format("UPDATE UC SET year = %d where VIN = '%s'", year, vin);
						try {
							con.stmt.executeUpdate(query);
						}
						catch(SQLException e) {
							System.out.println(ls + "There was an issue with your vehicle update");
							while(true) {
								System.out.println(ls + "Would you like to try again? Y or N?");
								if((cmd = scanner.nextLine()).equals("Y")) break;
								else if(cmd.equals("0")) return;
								else if(cmd.equals("N")) return;
								else System.out.println(ls + "Not a valid option");
							}
							continue;
						}
					
						break;
					}
					default:{
						System.out.println("Not a valid option");
						while(true) {
							System.out.println(ls + "Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")) break;
							else if(cmd.equals("0")) return;
							else if(cmd.equals("N")) return;
							else System.out.println(ls + "Not a valid option");
						}
						continue;					
					}
				}
				while(true) {
					System.out.println(ls + "Would you like to make another change? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}			
			}
		}
	}
	
	public static void declareFavorite(Connector2 con, Scanner scanner) {
		
	}
}
