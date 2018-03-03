package cs5530;

import java.lang.*;
import java.sql.*;
import java.io.*;

public class testdriver2 {

	/**
	 * @param args
	 */
	public static void displayMenu() {
		System.out.println("        Welcome to UberSkool bruh!     ");
		System.out.println("What would you like to do today?");
		System.out.println("1. Login as registered user");
		System.out.println("2. Login as registered driver");
		System.out.println("3. Register as user");
		System.out.println("4. Register as driver");
		System.out.println("Please enter the number next to the option you want.");
	}
	
	public static void userMenu() {
		
	}
	
	public static void driverMenu(){
		
	}
	
	enum LoginState{
		MENU, LOGGINGIN, LOGGEDIN, REGISTERING
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Example for cs5530");
		Connector2 con = null;
		String choice;
		String cname;
		String dname;
		LoginState loginState = LoginState.MENU;
		String sql = null;
		int c = 0;
		try {
			con = new Connector2();
			System.out.println("Database connection established");

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				displayMenu();
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
					if(Login(in)) {
						
					}
					else {
						
					}
				} else if (c == 2) {
					System.out.println("please enter your query below:");
					while ((sql = in.readLine()) == null && sql.length() == 0)
						System.out.println(sql);
					ResultSet rs = con.stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();
					int numCols = rsmd.getColumnCount();
					while (rs.next()) {
						// System.out.print("cname:");
						for (int i = 1; i <= numCols; i++)
							System.out.print(rs.getString(i) + "  ");
						System.out.println("");
					}
					System.out.println(" ");
					rs.close();
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
	
	public static boolean Login(BufferedReader in)
	{
		String phoneNumber = "";
		String password = "";
		String yesNo = "";
		Boolean loggingIn = true;
		while(loggingIn)
		{
			try {
				System.out.println("Please enter your phone number.");
				while ((phoneNumber = in.readLine()) == null && phoneNumber.length() == 0);
				//Query phone number
					//If successful, continue
					//Else
					 System.out.println("Would you like to try again? Y or N?");
					 while ((yesNo = in.readLine()) == null && yesNo.length() == 0);
					 if(yesNo.equalsIgnoreCase("Y")) continue;
					 else return false;
				System.out.println("Please enter your password.");
				while((password = in.readLine()) == null && password.length() == 0);
				//Query phone number and password
					//If successful, return true
					//Else
					 System.out.println("Would you like to try again? Y or N?");
					 while ((yesNo = in.readLine()) == null && yesNo.length() == 0);
					 if(yesNo.equalsIgnoreCase("Y")) continue;
					 else return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
}
