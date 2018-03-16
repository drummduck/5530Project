package cs5530;

import java.lang.*;
import java.sql.*;
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
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
	
	public static void driverMenu(){
		System.out.println("Welcome driver " + firstName + " " + lastName + ls);
		System.out.println("What can we do for you?");
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
	
	enum LoginState{
		MENU, LOGGINGIN, LOGGEDIN, REGISTERING, USERMENU, DRIVERMENU
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connector con = null;
		String choice;
		String cname;
		String dname;
		String sql = null;
		int c = 0;
		try {
			con = new Connector();
			System.out.println("Database connection established");

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			displayMenu();

			while (true) {
				while ((choice = in.readLine()) == null && choice.length() == 0);
				try {
					c = Integer.parseInt(choice);
				} catch (Exception e) {

					continue;
				}
				if (c < 0 | c > 4) {
					System.out.println("Not a valid option, please select from the following:");
					System.out.println("0: Quit Program");
					System.out.println("1: Login as registered user");
					System.out.println("2: Login as registered driver");
					System.out.println("3: Register as user");
					System.out.println("4: Register as driver" + ls);
					continue;
				}
				else if (c == 0) {
					clearUser();
					System.out.println("Good Bye");
					break;
				} else if (c == 1) {
					loginState = LoginState.LOGGINGIN;
					if(Login(con, false)) {
						loginState = LoginState.USERMENU;
						userMenu();
					}
					else{
						loginState = LoginState.MENU;
						displayMenu();
					}
				} else if (c == 2) {
					if(Login(con, true)){
						loginState = LoginState.DRIVERMENU;
						driverMenu();
					}
					else {
						loginState = LoginState.MENU;
						displayMenu();
					}
				} else if(c == 3){
					if(!register(con, false)) {
						loginState = LoginState.MENU;
						displayMenu();
					}
					else {
						userMenu();
					}
				} else if(c == 4){
					if(!register(con, true)) {
						loginState = LoginState.MENU;
						displayMenu();
					}
					else {
						driverMenu();
					}
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
	
	public static boolean Login(Connector con, Boolean driver) throws IOException
	{
		Boolean loginCheck = false;
		Boolean passwordCheck = false;
		Boolean IDLogin = false;
		String yesNo = "";
		String query = "";
		Scanner scanner = new Scanner(System.in);
		while(true)
		{
			try {
				if(!passwordCheck){
					if(!loginCheck) {
						if(!driver) System.out.println(ls +"Please enter your login name.");
						else System.out.println(ls +"Please enter your login Name or ID.");
					}
					loginName = scanner.nextLine();
					if(loginName.equals("0")){
						clearUser();
						scanner.close();
						return false;
					} else if(!driver){
						if(!Pattern.matches("^(?=.*[a-z]).+$", loginName)){
							loginCheck = true;
							loginName = "";
							System.out.println(ls + "Please enter a loginName that has at least one character that isnt a number or enter 0 to return to main menu.");
							continue;
						} else loginCheck = false;
					}else if(driver){
						if(Pattern.matches("^(?=.*[a-z]).+$", loginName)) loginCheck = false;
						else if (Pattern.matches("^[1-9]\\d*$", loginName)) {
							IDLogin = true;
							loginCheck = false;
						} else{
							loginCheck = true;
							loginName = "";
							System.out.println(ls + "If logging in with loginName it must be at least one character that isnt a number, if logging in with an ID it must only by an integer, or enter 0 to return to main menu.");
							continue;
						}
					}
				}
				if(!passwordCheck) System.out.println(ls + "Please enter your password.");
				password = scanner.nextLine();
				if(password.equals("0")){
					clearUser();
					scanner.close();
					return false;
				} else if(!Pattern.matches(".{8,}", password)) {
					passwordCheck = true;
					System.out.println(ls + "Please enter a password with minimum of eight characters or enter 0 to return to main menu.");
					continue;
				} else {
					if(!driver)query = "select * from UU where loginName = " + "\"" + loginName + "\"" + " and password = " + "\"" + password + "\"";
					else {
						if(IDLogin) 	query = "select * from UD where ID = " + loginName + " and password = " + "\"" + password + "\"";
						else query = "select * from UD where loginName = " + "\"" + loginName + "\"" + " and password = " + "\"" + password + "\"";
					}
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rs.next()) { 
						 firstName = rs.getString("firstName"); 
						 lastName = rs.getString("lastName");
						 if(driver) {
							 ID = rs.getInt("ID");
							 isDriver = true;
						 }
						 loginState = LoginState.LOGGEDIN;
						 scanner.close();
						 return true;
						} else {
						if(!driver) System.out.println(ls + "We could not find a user with those credentials. Please enter a login name or enter 0 to return to main menu");
						else System.out.println(ls + "We could not find a user with those credentials. Please enter a login name or ID, or enter 0 to return to main menu");
						passwordCheck = false;
						loginCheck = true;
						continue;
					}
				}
			} catch (SQLException e) {
				System.out.println(ls + "There was an issue with your lookup." + ls);
				if(!driver) System.out.println(ls + "Please enter a login name or enter 0 to return to main menu");
				else System.out.println(ls + "Please enter a login name or ID, or enter 0 to return to main menu");
				passwordCheck = false;
				loginCheck = true;
				continue;
			}
		}
	}
	
	public static boolean register(Connector con, Boolean driver) {
		Scanner scanner = new Scanner(System.in);
		String cmd = "";
		String query;
		Boolean loginCheck = false;
		Boolean passwordCheck = false;
		while(true) {
			if(driver) {
				if(!loginCheck && !passwordCheck)System.out.println(ls +"Do you already have a user account you would like to register as a driver? Y or N?");
				if(!loginCheck && !passwordCheck && (cmd = scanner.nextLine()).equals("Y")) {
					try {
						if(Login(con, driver)){
							query = String.format("select * from UD where loginName = %s", loginName);
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
								query = String.format("insert into UD (address, phone, firstName, lastName, loginName, password) values (%s, %s, %s, %s, %s, %s)",
										address, phoneNumber, firstName, lastName, loginName, password);
								con.stmt.executeQuery(query);
								query = String.format("select * from UD where loginName = %s", loginName);
								rs = con.stmt.executeQuery(query);
								rsmd = rs.getMetaData();
								ID = rs.getInt("ID");
								System.out.println("Your driver ID is " + ID);
								return true;
							}		
						} else {
							System.out.println(ls + "User does not exist, Would you like to try again? Y or N?");
							if((cmd = scanner.nextLine()).equals("Y")){
								continue;
							}
							else if(cmd.equals("N")) return false;
						}
					} catch (IOException e) {
						//Put error
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(loginCheck || passwordCheck || cmd.equals("N")){
					if(!loginCheck && !passwordCheck)System.out.println(ls +"Please enter a login name you would like to use. (must have at least one character that isn't a number)");
					if(!passwordCheck && (cmd = scanner.nextLine()).equals("0")) {
						clearUser();
						return false;
						}
					else if(!Pattern.matches("^(?=.*[a-z]).+$", cmd) && !passwordCheck){
						loginCheck = true;
						loginName = "";
						System.out.println(ls + "Please enter a loginName that has at least one character that isnt a number or enter 0 to return to main menu.");
						continue;
					} else if(!passwordCheck){
						loginCheck = false;
						loginName = cmd;
					}
						System.out.println(ls + "Please enter a password that has at least eight characters or enter 0 to return to main menu.");
						if(!Pattern.matches(".{8,}" ,cmd = scanner.nextLine())){
							System.out.println(ls + "Please enter a password with minimum of eight characters or enter 0 to return to main menu.");
							password = "";
							passwordCheck = true;
							continue;
						}
						else {
							passwordCheck = false;
							password = cmd;
						}
						System.out.println(ls + "Please enter your phone number");
						System.out.println(ls + "Please enter your firstName");
						System.out.println(ls + "Please enter your lastName");
						System.out.println(ls + "Please enter an address");
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
}
