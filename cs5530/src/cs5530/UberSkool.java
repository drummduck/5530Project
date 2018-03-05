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
	static String address = "";
	static LoginState loginState = LoginState.MENU;


	/**
	 * @param args
	 */
	public static void displayMenu() {
		System.out.println("Welcome to UberSkool bruh!");
		System.out.println("What would you like to do today?");
		System.out.println("1. Login as registered user");
		System.out.println("2. Login as registered driver");
		System.out.println("3. Register as user");
		System.out.println("4. Register as driver");
		System.out.println("Please enter the number next to the option you want.");
	}
	
	public static void userMenu() {
		System.out.println("Welcome " + firstName + " " + lastName);
		System.out.println("What can we do for you?");
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
	
	public static void driverMenu(){
		
	}
	
	enum LoginState{
		MENU, LOGGINGIN, LOGGEDIN, REGISTERING, USERMENU
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
				if (c < 1 | c > 4)
					continue;
				if (c == 1) {
					loginState = LoginState.LOGGINGIN;
					if(Login(con)) {
						loginState = LoginState.USERMENU;
						userMenu();
					}
					else{
						loginState = LoginState.MENU;
						displayMenu();
					}
				} else if (c == 2) {
					
				} else {
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
	
	public static boolean Login(Connector con) throws IOException
	{
		Boolean numberCheck = false;
		Boolean passwordCheck = false;
		String yesNo = "";
		String query = "";
		Scanner scanner = new Scanner(System.in);
		while(true)
		{
			try {
				if(!passwordCheck){
					if(!numberCheck) System.out.println("Please enter your phone number.");
					phoneNumber = scanner.nextLine();
					if(phoneNumber.equals("1")){
						clearUser();
						return false;
						}
					else if(!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber)){
						numberCheck = true;
						phoneNumber = "";
						System.out.println("Please enter a different format or enter 1 to return to main menu.");
						continue;
					}
					else numberCheck = false;
				}
				if(!passwordCheck) System.out.println("Please enter your password.");
				password = scanner.nextLine();
				if(password.equals("1")){
					clearUser();
					return false;
				}
				else if(!Pattern.matches(".{8,}", password)) {
					passwordCheck = true;
					System.out.println("Please enter a different format or enter 1 to return to main menu.");
					continue;
				}
				else {
					query = "select * from UU where phone = " + "\"" + phoneNumber + "\"" + " and password = " + "\"" + password + "\"";
					ResultSet rs = con.stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rs.next()) { 
						 String firstName = rs.getString("firstName"); 
						 String lastName = rs.getString("lastName");
						 loginState = LoginState.LOGGEDIN;
						 return true;
						}
					else {
						System.out.println("We could not find a user with those credentials. Please enter a phone number or enter 1 to return to main menu");
						passwordCheck = false;
						numberCheck = true;
						continue;
					}
				}
			} catch (SQLException e) {
				System.out.println("There was an issue with your lookup.");
				return false;
			}
		}
	}
	
	public static void clearUser(){
		String phoneNumber = "";
		String password = "";
		String firstName = "";
		String lastName = "";
		String address = "";
		LoginState loginState = LoginState.MENU;
	}
}
