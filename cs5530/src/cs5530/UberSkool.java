package cs5530;

import java.lang.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		System.out.println("Welcome user " + firstName + " " + lastName + ls);
		System.out.println("What can we do for you?");
		System.out.println("0: Logout");
		System.out.println("1: Record a reservation");
		System.out.println("2: Record a ride");
		System.out.println("3: Declare a favorite UC");
		System.out.println("4: Rate user feedback");
		System.out.println("5: Rate user");
		System.out.println("6: Search for UC");
		System.out.println("7: Degree of seperation from user");
		System.out.println("8: Statistics");
	}
	
	public static void driverMenu(){
		System.out.println("Welcome driver " + firstName + " " + lastName + ls);
		System.out.println("What can we do for you?");
		System.out.println("");
		System.out.println("");
		System.out.println("");
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
					clearUser();
					System.out.println("Good Bye");
					return;
				} else if (c == 1) {
					if(loginState == LoginState.USERMENU) {
						reserve(con, scanner);
						userMenu();
					}
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
				} else if (c == 2) {
					if(Login(con, scanner, true)){
						loginState = LoginState.DRIVERMENU;
						driverMenu();
					}
					else {
						clearUser();
						loginState = LoginState.MENU;
						displayMenu();
					}
				} else if(c == 3){
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
			e.printStackTrace();
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
				if(!checkInfo(driver)){
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
						//Put error
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
						e.printStackTrace();
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
		
		if(driver) {
			if(!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber)) phoneNumberOkay = false;
			if(!Pattern.matches(".{8,}", password)) passwordOkay = false;
			if(!Pattern.matches("/^[a-z ,.'-]+$/i", firstName)) firstNameOkay = false;
			if(!Pattern.matches("/^[a-z ,.'-]+$/i", lastName)) lastNameOkay = false;
			if(loginState == LoginState.REGISTERING) {
				if(!Pattern.matches("^(?=.*[a-z]).+$", loginName)) loginNameOkay = false;
			}
			else {
				try {
					Integer.parseInt(loginName);
				} catch (Exception e) {
					loginNameOkay = false;
				}
			}
			if(!Pattern.matches("^[a-zA-Z0-9\\s,'-]*$", address)) addressOkay = false;
		}
		
		else {
			if(!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber)) phoneNumberOkay = false;
			if(!Pattern.matches(".{8,}", password)) passwordOkay = false;
			if(!Pattern.matches("^[\\p{L} .'-]+$", firstName)) firstNameOkay = false;
			if(!Pattern.matches("^[\\p{L} .'-]+$", lastName)) lastNameOkay = false;
			if(!Pattern.matches("^(?=.*[a-z]).+$", loginName)) loginNameOkay = false;
			//Not sure if we can really restrict this if(!Pattern.matches("^[a-zA-Z0-9\\s,'-]*$", address)) addressOkay = false;
		}
		
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
	
	public static void recordRide(Connector2 con, Scanner scanner){
		String cmd = "";
		String query = "";
		String date = "";
		String time = "";
		String dist = "";
		String numOfPeople = "";
		String car = "";
		int cost = -1;
		
		while(true)
		{
			System.out.println(ls + "When would you like to reserve the vehicle? e.g.(YYYY-MM-DD)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else date = cmd;
			
			System.out.println(ls + "What time? e.g.(HH:MM)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else date = cmd;
			
			System.out.println(ls + "How far will you be going? (In Miles)");
			if((cmd = scanner.nextLine()).equals("0")) return;
			dist = cmd;
			
			System.out.println(ls + "How many people will be going?");
			if((cmd = scanner.nextLine()).equals("0")) return;
			numOfPeople = cmd;
			
			System.out.println(ls + "What UC would you like to reserve? Please enter VIN.");
			if((cmd = scanner.nextLine()).equals("0")) return;
			car = cmd;
			
			if(!checkRide(date, time, dist, numOfPeople, car)){
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
				//Check if driver available during that time and if
			}
		}
	}
	
	public static boolean checkRide(String date, String time, String dist, String numOfPeople, String Car){
		Boolean dateOkay = true;
		Boolean timeOkay = true;
		Boolean distOkay = true;
		Boolean numOfPeopleOkay = true;
		Boolean carOkay = true;
		
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
		if(!Pattern.matches("^[a-zA-Z0-9]*$", date) || date.length() != 17) carOkay = false;
		
		if(!dateOkay) System.out.println("Please enter a date in this format: YYYY-MM-DD");
		if(!timeOkay) System.out.println("Please enter a time in this format: HH:MM");
		if(!distOkay) System.out.println("Please enter an integer for distance in miles");
		if(!numOfPeopleOkay) System.out.println("Please enter an integer for the number of people");
		if(!carOkay) System.out.println("Please enter an alphanumeric of length 17 for VIN");
		
		return false;
	}
	
	public static void reserve(Connector2 con, Scanner scanner) {
		String cmd = "";
		String query = "";
		String vin = "";
		while(true)
		{
			System.out.println("Which car would you like to reserve? Please enter a VIN");
			if((cmd = scanner.nextLine()).equals("0")) return;
			else if(!Pattern.matches("^[a-zA-Z0-9]*$", cmd) || cmd.length() != 17) {
				System.out.println(ls + "Please enter an alphanumeric of length 17 for VIN");
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
				vin = cmd;
				try {	
					query = String.format("INSERT INTO Reserves (VIN, loginName) VALUES ('%s', '%s')", vin, loginName);
					con.stmt.executeUpdate(query);
				}
				catch (SQLException e) {
					e.printStackTrace();
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
				
				while(true) {
					System.out.println(ls + "Would you like to try again? Y or N?");
					if((cmd = scanner.nextLine()).equals("Y")) break;
					else if(cmd.equals("0")) return;
					else if(cmd.equals("N")) return;
					else System.out.println(ls + "Not a valid option");
				}
			}
		}
	}
}
