package cs5530;

import java.lang.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

 // TODO:Figure out some stupid random algorithm to give users awards.....

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
  System.out.println("4: Rate UC");
  System.out.println("5: Rate UU");
  System.out.println("6: Rate feedback");
  System.out.println("7: Search for UC");
  System.out.println("8: Degree of seperation from user");
  System.out.println("9: Statistics");
  System.out.println("10: Useful feedbacks");
  System.out.println("11: Top user award rankings (Admin only)");
 }

 public static void driverMenu() {
  System.out.println(ls + "Welcome driver " + firstName + " " + lastName + ls);
  System.out.println("What can we do for you?");
  System.out.println("0: Logout");
  System.out.println("1: Register a new car");
  System.out.println("2: Update car information");
 }

 enum LoginState {
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
    if (scanner.hasNextLine())
     choice = scanner.nextLine();
    else
     continue;
    try {
     c = Integer.parseInt(choice);
    } catch (Exception e) {
     System.out.println("Please enter a valid option");
     if(loginState == LoginState.DRIVERMENU) driverMenu();
     else if(loginState == LoginState.USERMENU) userMenu();
     else displayMenu();
     continue;
    }
    if (c < 0 | c > 11) {
     System.out.println("Please enter a valid option");
     if(loginState == LoginState.DRIVERMENU) driverMenu();
     else if(loginState == LoginState.USERMENU) userMenu();
     else displayMenu();
     continue;
    } else if (c == 0) {
     if (loginState == LoginState.USERMENU || loginState == LoginState.DRIVERMENU) {
      clearUser();
      displayMenu();
      loginState = LoginState.MENU;
     } else {
      clearUser();
      System.out.println("Good Bye");
      return;
     }
    } else if (c == 1) {
     if (loginState == LoginState.DRIVERMENU) {
      registerCar(con, scanner);
      driverMenu();
     } else if (loginState == LoginState.USERMENU) {
      reserve(con, scanner);
      userMenu();
     } else if (loginState == LoginState.MENU) {
      loginState = LoginState.LOGGINGIN;
      if (Login(con, scanner, false)) {
       loginState = LoginState.USERMENU;
       userMenu();
      } else {
       clearUser();
       loginState = LoginState.MENU;
       displayMenu();
      }
     }
    } else if (c == 2) {
     if (loginState == LoginState.DRIVERMENU) {
      updateCar(con, scanner);
      driverMenu();
     } else if (loginState == LoginState.USERMENU) {
      recordRides(con, scanner);
      userMenu();
     } else {
      if (Login(con, scanner, true)) {
       loginState = LoginState.DRIVERMENU;
       driverMenu();
      } else {
       clearUser();
       loginState = LoginState.MENU;
       displayMenu();
      }
     }
    } else if (c == 3) {
     if (loginState == LoginState.USERMENU) {
      declareFavorite(con, scanner);
      userMenu();
     }
     if (!register(con, scanner, false)) {
      clearUser();
      loginState = LoginState.MENU;
      displayMenu();
     } else {
      System.out.println(ls + "User successfully created! Returning to main menu!");
      loginState = LoginState.MENU;
      clearUser();
      displayMenu();
     }
     System.out.println("Please enter a valid option");
     if(loginState == LoginState.DRIVERMENU) driverMenu();
     else if(loginState == LoginState.USERMENU) userMenu();
    } else if (c == 4) {
     if(loginState == LoginState.USERMENU) {
      feedbackRecording(con, scanner);
      userMenu();
     }
     else if(loginState == LoginState.MENU){
      if (!register(con, scanner, true)) {
       clearUser();
       loginState = LoginState.MENU;
       displayMenu();
      } else {
       loginState = LoginState.MENU;
       clearUser();
       displayMenu();
      }
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    } else if (c == 5) {
     if(loginState == LoginState.DRIVERMENU) driverMenu();
     else if(loginState == LoginState.USERMENU){ 
       Trust.declareTrust(con,loginName);
       userMenu();
     }else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    } else if (c == 6) {
     if(loginState == LoginState.USERMENU) {
      rateFeedback(con, scanner);
      userMenu();
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    }else if (c == 7) {
     if(loginState == LoginState.USERMENU) {
      Browse.browser(con);
      userMenu();
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    }else if (c == 8) {
     if(loginState == LoginState.USERMENU) {
      Separation.firstDegree(con);
      userMenu();
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    }
    else if(c == 9) {
     if(loginState == LoginState.USERMENU) {
      statistics(con, scanner);
      userMenu();
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    } else if (c == 10) {
     if(loginState == LoginState.USERMENU) {
     usefulFeedbacks(con, scanner);
     userMenu();
     }
     else {
      System.out.println("Please enter a valid option");
      if(loginState == LoginState.DRIVERMENU) driverMenu();
      else if(loginState == LoginState.USERMENU) userMenu();
     }
    }
    else if (c == 11) {
      if(loginState == LoginState.USERMENU) {
       if(loginName.equals("admin")){
         Award.findBest(con);
       }else
         System.out.println("Must be an admin to access this feature\nFor grading please login as:\nUsername = 'admin'\nPassword = 'password'\nThank you.");
     userMenu();
     }
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

 public static boolean Login(Connector2 con, Scanner scanner, Boolean driver) throws IOException {
  String cmd = "";
  String query = "";
  while (true) {
   try {

    if (!driver)
     System.out.println(ls + "Please enter your login name.");
    else
     System.out.println(ls + "Please enter your driver ID.");
    if ((cmd = scanner.nextLine()).equals("0"))
     return false;
    else
     loginName = cmd;
    System.out.println(ls + "Please enter your password.");
    if ((cmd = scanner.nextLine()).equals("0"))
     return false;
    else
     password = cmd;
    if (!checkLoginInfo(loginName, password, driver)) {
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return false;
      else if (cmd.equals("N"))
       return false;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    } else {
     if (!driver)
      query = "select * from UU where loginName = " + "\"" + loginName + "\"" + " and password = "
        + "\"" + password + "\"";
     else
      query = "select * from UD where ID = " + loginName + " and password = " + "\"" + password
        + "\"";

     ResultSet rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (rs.next()) {
      loginName = rs.getString("loginName");
      firstName = rs.getString("firstName");
      lastName = rs.getString("lastName");
      if (driver) {
       ID = rs.getInt("ID");
       isDriver = true;
      }
      return true;
     } else {
      if (!driver) {
       System.out.println(ls
         + "We could not find a user with those credentials. Would you like to try again? Y or N?");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return false;
        else if (cmd.equals("N"))
         return false;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      } else {
       System.out.println(ls
         + "We could not find a driver with those credentials. Would you like to try again? Y or N?");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return false;
        else if (cmd.equals("N"))
         return false;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }
     }
    }
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue with your lookup." + ls);
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return false;
     else if (cmd.equals("N"))
      return false;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   }
  }
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

  if (!passwordOkay)
   System.out.println("Please enter a password with at least 8 characters");
  if (driver && !loginNameOkay)
   System.out.println("Please enter a digit greater than 1 for driver ID");
  if (!driver && !loginNameOkay)
   System.out.println("Please enter a login name with at least 1 non digit character");

  if (!loginNameOkay || !passwordOkay)
   return false;
  return true;
 }

 public static boolean register(Connector2 con, Scanner scanner, Boolean driver) {
  String cmd = "";
  String query = "";
  ArrayList<String> hours;
  Boolean loginCheck = false;
  Boolean passwordCheck = false;
  Boolean tryAgain = false;

  while (true) {
   tryAgain = false;
   if (driver) {
    System.out.println(
      ls + "Do you already have a user account you would like to register as a driver? Y or N?");
    if ((cmd = scanner.nextLine()).equals("Y")) {
     try {
      if (Login(con, scanner, false)) {
       query = String.format("select * from UD where loginName = '%s'", loginName);
       ResultSet rs = con.stmt.executeQuery(query);
       ResultSetMetaData rsmd = rs.getMetaData();
       if (rs.next()) {
        System.out.println(ls
          + "A driver with that login name already exists. Would you like to try again? Y or N?");
        clearUser();
        if ((cmd = scanner.nextLine()).equals("Y")) {
         continue;
        } else if (cmd.equals("N"))
         return false;
       } else {
        
        query = String.format(
          "insert into UD (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
          address, phoneNumber, firstName, lastName, loginName, password);
        con.stmt.executeUpdate(query);
        query = String.format("select * from UD where loginName = '%s'", loginName);
        rs = con.stmt.executeQuery(query);
        rs.next();
        ID = rs.getInt("ID");
        System.out.println(ls + "Your driver ID is " + ID);

        if ((hours = setHours(con, scanner)).isEmpty()) {
         //remove driver
         query = String.format("delete from UD where ID = %d", ID);
         con.stmt.executeUpdate(query);
         clearUser();
         System.out.println(ls + "You must add hours to register as a driver" + ls);
         while (true) {
          System.out.println(ls + "Would you like to try again? Y or N?");
          if ((cmd = scanner.nextLine()).equals("Y"))
           break;
          else if (cmd.equals("0"))
           return false;
          else if (cmd.equals("N"))
           return false;
          else
           System.out.println(ls + "Not a valid option");
         }
         continue;
        }

        for (String s : hours) {
         String startTime = "";
         String endTime = "";

         startTime = s.substring(0, 5);
         endTime = s.substring(6);

         query = String.format(
           "INSERT INTO hours_Of_Operation (start, end, ID) VALUES ('%s', '%s', %d)",
           startTime, endTime, ID);
         try {
          con.stmt.executeUpdate(query);
         } catch (SQLException e) {
          query = String.format("delete from UD where ID = %d", ID);
          clearUser();
          con.stmt.executeUpdate(query);          
          System.out.println(ls + "There was an issue adding your hours" + ls);
          while (true) {
           System.out.println(ls + "Would you like to try again? Y or N?");
           if ((cmd = scanner.nextLine()).equals("Y")) {
            tryAgain = true;
            break;
           } else if (cmd.equals("0"))
            return false;
           else if (cmd.equals("N"))
            return false;
           else
            System.out.println(ls + "Not a valid option");
          }
          break;
         }

        }
        if (tryAgain)
         continue;
        
        System.out.println("Registration complete!");
        
        return true;
       }
      } else return false;
     } catch (IOException e) {
      e.printStackTrace();
      System.out.println(ls + "There was an issue registering your user" + ls);
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return false;
       else if (cmd.equals("N"))
        return false;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     } catch (SQLException e) {
      e.printStackTrace();

      System.out.println(ls + "There was an issue registering your user" + ls);
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return false;
       else if (cmd.equals("N"))
        return false;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    }

    else if (cmd.equals("N")) {
     System.out.println(ls
       + "Please enter a login name you would like to use. (must have at least one character that isn't a number)");
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      loginName = cmd;
     System.out.println(ls + "Please enter a password that has at least eight characters.");
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      password = cmd;
     System.out.println(ls + "Please enter your phone number e.g. (123)456-7890");
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      phoneNumber = cmd;
     System.out.println(ls + "Please enter your firstName");
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      firstName = cmd;
     System.out.println(ls + "Please enter your lastName");
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      lastName = cmd;
     System.out.println(ls + "Please enter an address" + ls);
     if ((cmd = scanner.nextLine()).equals("0")) {
      return false;
     } else
      address = cmd;
     if (!checkInfo(false)) {
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return false;
       else if (cmd.equals("N"))
        return false;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     } else {
      if ((hours = setHours(con, scanner)).isEmpty()) {
       System.out.println(ls + "You must add hours to register as a driver" + ls);
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return false;
        else if (cmd.equals("N"))
         return false;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }

      try {
       query = String.format(
         "select loginName FROM UU where loginName = '%s' UNION select loginName FROM UD WHERE loginName = '%s'",
         loginName, loginName);
       ResultSet rs = con.stmt.executeQuery(query);
       ResultSetMetaData rsmd = rs.getMetaData();
       if (rs.next()) {
        System.out.println(ls + "A user/driver with that login name already exists.");
        while (true) {
         scanner.nextLine();
         System.out.println(ls + "Would you like to try again? Y or N?");
         if ((cmd = scanner.nextLine()).equals("Y"))
          break;
         else if (cmd.equals("0"))
          return false;
         else if (cmd.equals("N"))
          return false;
         else
          System.out.println(ls + "Not a valid option");
        }
        continue;
       } else {
        query = String.format(
          "insert into UU (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
          address, phoneNumber, firstName, lastName, loginName, password);
        con.stmt.executeUpdate(query);
        query = String.format(
          "insert into UD (address, phoneNumber, firstName, lastName, loginName, password) values ('%s', '%s', '%s', '%s', '%s', '%s')",
          address, phoneNumber, firstName, lastName, loginName, password);
        con.stmt.executeUpdate(query);

        query = String.format("select * from UD where loginName = '%s'", loginName);
        rs = con.stmt.executeQuery(query);
        if (rs.next()) {
         ID = rs.getInt("ID");
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
          System.out.println(ls + "There was an issue adding your hours" + ls);
          while (true) {
           System.out.println(ls + "Would you like to try again? Y or N?");
           if ((cmd = scanner.nextLine()).equals("Y")) {
            tryAgain = true;
            break;
           } else if (cmd.equals("0"))
            return false;
           else if (cmd.equals("N"))
            return false;
           else
            System.out.println(ls + "Not a valid option");
          }
          break;
         }

        }

        if (tryAgain)
         continue;

        System.out.println(
          ls + "You are now a registered user and driver! Your driver ID is " + ID);
        return true;
       }
      } catch (SQLException e) {
       System.out.println(ls + "There was an issue adding your driver" + ls);
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return false;
        else if (cmd.equals("N"))
         return false;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }
     }
    } else if (cmd.equals("0"))
     return false;
    else
     System.out.println(ls + "Please answer Y or N or quit with 0" + ls);
   }

   // THIS IS FOR REGISTERING IN AS USER
   else {
    System.out.println(ls
      + "Please enter a login name you would like to use. (must have at least one character that isn't a number)");
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     loginName = cmd;
    System.out.println(ls + "Please enter a password that has at least eight characters.");
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     password = cmd;
    System.out.println(ls + "Please enter your phone number e.g. (123)456-7890");
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     phoneNumber = cmd;
    System.out.println(ls + "Please enter your firstName");
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     firstName = cmd;
    System.out.println(ls + "Please enter your lastName");
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     lastName = cmd;
    System.out.println(ls + "Please enter an address" + ls);
    if ((cmd = scanner.nextLine()).equals("0")) {
     return false;
    } else
     address = cmd;
    if (!checkInfo(false)) {
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return false;
      else if (cmd.equals("N"))
       return false;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    } else {
     try {
      query = String.format("select * from UU where loginName = '%s' or phoneNumber = '%s'",
        loginName, phoneNumber);
      ResultSet rs = con.stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      if (rs.next()) {
       System.out.println(ls + "loginName or phoneNumber exists already.");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return false;
        else if (cmd.equals("N"))
         return false;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      } else {
       query = String.format(
         "INSERT INTO UU (loginName, password, phoneNumber, address, firstName, lastName) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
         loginName, password, phoneNumber, address, firstName, lastName);
       con.stmt.executeUpdate(query);
       return true;
      }
     } catch (SQLException e) {
      System.out.println(ls + "There was an issue adding your user" + ls);
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return false;
       else if (cmd.equals("N"))
        return false;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    }
   }
  }
 }

 public static void clearUser() {
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

 public static boolean checkInfo(Boolean driver) {
  Boolean phoneNumberOkay = true;
  Boolean passwordOkay = true;
  Boolean firstNameOkay = true;
  Boolean lastNameOkay = true;
  Boolean loginNameOkay = true;
  Boolean addressOkay = true;

  if (!Pattern.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", phoneNumber))
   phoneNumberOkay = false;
  if (!Pattern.matches(".{8,}", password))
   passwordOkay = false;
  if (!Pattern.matches("^[\\p{L} .'-]+$", firstName))
   firstNameOkay = false;
  if (!Pattern.matches("^[\\p{L} .'-]+$", lastName))
   lastNameOkay = false;
  if (!Pattern.matches("^(?=.*[a-z]).+$", loginName))
   loginNameOkay = false;
  // Not sure if we can really restrict this
  // if(!Pattern.matches("^[a-zA-Z0-9\\s,'-]*$", address)) addressOkay = false;

  if (!phoneNumberOkay)
   System.out.println("Please enter different format for phone number, e.g. (123)456-7890");
  if (!passwordOkay)
   System.out.println("Please enter a password with at least 8 characters");
  if (!firstNameOkay)
   System.out.println("Please enter a valid first name format");
  if (!lastNameOkay)
   System.out.println("Please enter a valid last name format");
  if (!addressOkay)
   System.out.println("Please enter a valid address");
  if (driver && !loginNameOkay)
   System.out.println("Please enter a digit greater than 1 for driver ID");
  if (!driver && !loginNameOkay)
   System.out.println("Please enter a login name with at least 1 non digit character");
  if (!phoneNumberOkay || !passwordOkay || !firstNameOkay || !lastNameOkay || !addressOkay || !loginNameOkay) {
   clearUser();
   return false;
  } else
   return true;
 }

 public static void recordRides(Connector2 con, Scanner scanner) {
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

  while (true) {
   tryAgain = false;
   System.out.println(ls + "What was the date of your ride? e.g.(YYYY-MM-DD)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    date = cmd;

   System.out.println(ls + "What was the time? e.g.(HH:MM)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    time = cmd;

   System.out.println(ls + "How much did it cost? e.g.(12.50)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   cost = cmd;

   System.out.println(ls + "How far did you go? (In Miles)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   dist = cmd;

   System.out.println(ls + "How many people went?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   numOfPeople = cmd;

   System.out.println(ls + "What UC did you ride with? (alphanumeric of length 17).");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   vin = cmd;

   if (!checkRide(date, time, dist, cost, numOfPeople, vin)) {
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   } else {
    try {
     query = String.format("select * from UC where VIN = '%s'", vin);
     ResultSet rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (!rs.next()) {
      System.out.println(ls + "Vehicle does not exist");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     query = String.format("select * from Rides where date = '%s' AND VIN = '%s'", date + " " + time,
       vin);
     rs = con.stmt.executeQuery(query);
     rsmd = rs.getMetaData();
     if (rs.next()) {
      System.out.println(ls + "A ride with that vehicle was already recorded at this time");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     query = String.format("select c.ID, d.ID, h.start, h.end from UC c, UD d, hours_Of_Operation h where c.VIN = '%s' AND c.ID = d.ID AND d.ID = h.ID AND h.start <= TIME('%s') AND h.end >= TIME('%s')",
       vin, time, time);
     rs = con.stmt.executeQuery(query);
     rsmd = rs.getMetaData();
     if (!rs.next()) {
      System.out.println(ls + "That UC is not available during that time");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue adding your ride" + ls);
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    Ride ride = new Ride(dist, date, cost, numOfPeople, vin);
    Boolean exists = false;
    for(Ride r: rides) {
     if(r.toString().equals(ride.toString())) {
      exists = true;
      System.out.println("That ride exists already in your list");
     }
    }
    if(!exists)rides.add(ride);
    while (true) {
     System.out.println(ls + "Would you like to add another ride? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N")) {
      if (rides.isEmpty())
       return;
      else if (!approveRides(rides, scanner)) {
       rides.clear();
       while (true) {
        System.out.println(ls + "Would you like to try adding rides again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y")) {
         tryAgain = true;
         break;
        } else if (cmd.equals("0"))
         return;
        else if (cmd.equals("N"))
         return;
        else
         System.out.println(ls + "Not a valid option");
       }
       if (tryAgain)
        break;
      } else {
       for (Ride r : rides) {
        query = String.format(
          "INSERT INTO Rides (dist, date, cost, numOfPeople, VIN, loginName) VALUES (%d, '%s', %.2f, %d, '%s', '%s')",
          Integer.parseInt(dist), date + " " + time, Float.parseFloat(cost),
          Integer.parseInt(numOfPeople), vin, loginName);
        try {
         con.stmt.executeUpdate(query);
         query = String.format("SELECT tripID, date FROM Rides ORDER BY tripID DESC LIMIT %d", rides.size());
         ResultSet rs = con.stmt.executeQuery(query);
         while(rs.next()) {
          System.out.println(String.format("Your tripID for the trip on %s is: %d", rs.getDate("date").toString(), rs.getInt("tripID")));
         }
         
         return;
        } catch (SQLException e) {
         rides.clear();
         System.out.println(ls + "There was an issue adding your rides" + ls);
         while (true) {
          System.out.println(ls + "Would you like to try adding rides again? Y or N?");
          if ((cmd = scanner.nextLine()).equals("Y")) {
           tryAgain = true;
           break;
          } else if (cmd.equals("0"))
           return;
          else if (cmd.equals("N"))
           return;
          else
           System.out.println(ls + "Not a valid option");
         }
         if (tryAgain)
          break;
        }
       }
      }
     } else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   }
  }
 }

 public static boolean approveRides(ArrayList<Ride> rides, Scanner scanner) {
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

 public static boolean checkRide(String date, String time, String dist, String cost, String numOfPeople, String vin) {
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

  if(!costOkay || !dateOkay || !timeOkay || !distOkay || !numOfPeopleOkay || !vinOkay) return false;
  
  return true;
 }

 public static void reserve(Connector2 con, Scanner scanner) {
  Boolean approved = false;
  String cmd = "";
  String query = "";
  String date = "";
  String vin = "";
  String time = "";
  ArrayList<String> reservations = new ArrayList<String>();
  while (true) {
   approved = false;
   System.out.println(ls + "What is the date of this reservation? e.g.(YYYY-MM-DD)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    date = cmd;

   System.out.println(ls + "What time? e.g.(HH:MM)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    time = cmd;

   System.out.println(ls + "Which car would you like to reserve? Please enter a VIN");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    vin = cmd;

   if (!reserveCheck(date, time, vin)) {
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   } else {
    try {
     query = String.format("select * from Reservations where VIN = '%s' and date = '%s'", vin, date + " " + time);
     ResultSet rs = con.stmt.executeQuery(query);
     if (rs.next()) {
      System.out.println(ls + "Reservation already made for that vehicle during that time");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     query = String.format("select * from UC where VIN = '%s'", vin);
     rs = con.stmt.executeQuery(query);
     if (!rs.next()) {
      System.out.println(ls + "Vehicle does not exist");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    } catch (SQLException e) {
     reservations.clear();
     System.out.println(ls + "There was an issue adding your reservation" + ls);
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    if (!reservations.contains(String.format("UC VIN: %s, DateTime: %s", vin, date + " " + time))) {
     reservations.add(String.format("UC VIN: %s, DateTime: %s", vin, date + " " + time));
     query = String.format("select r.VIN, count(*) as count from Rides r where r.loginName in (select distinct loginName from Rides where VIN = '%s') and r.VIN != '%s' group by r.VIN order by count", vin, vin);
     try {
      ResultSet rs = con.stmt.executeQuery(query);
      while(rs.next()) {
       System.out.println("***********OTHER UBER CARS YOU MIGHT LIKE*************");
       System.out.println("    VIN:  " + rs.getString("VIN"));
      }
     } catch(SQLException e) {
      System.out.println(ls + "There was an issue adding your reservation" + ls);
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    }
    while (true) {
     System.out.println(ls + "Would you like to add another reservation? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N")) {
      if (reservations.isEmpty())
       return;
      else {
       if (approveReservations(reservations, scanner)) {
        approved = true;
        break;
       } else {
        while (true) {
         System.out.println(ls + "Would you like to try adding reservations again? Y or N?");
         if ((cmd = scanner.nextLine()).equals("Y"))
          break;
         else if (cmd.equals("0"))
          return;
         else if (cmd.equals("N"))
          return;
         else
          System.out.println(ls + "Not a valid option");
        }
        reservations.clear();
        break;
       }
      }
     } else
      System.out.println(ls + "Not a valid option");
    }
    if (!approved)
     continue;
   }

   if (approved) {
    try {
     for (String s : reservations) {
      query = String.format(
        "INSERT into Reservations (VIN, loginName, date) VALUES ('%s', '%s', '%s')",
        s.substring(8, 25), loginName, s.substring((s.length() - 1) - 16));
      con.stmt.executeUpdate(query);
     }
     
     System.out.println("Reservations added!");
     
     //Here we do suggestions on other vehicles
    } catch (SQLException e) {
     reservations.clear();
     System.out.println(ls + "There was an issue adding your reservations" + ls);
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) {
       break;
      } else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   }
   return;
  }
 }

 public static boolean reserveCheck(String date, String time, String vin) {
  Boolean dateOkay = true;
  Boolean vinOkay = true;
  Boolean timeOkay = true;

  if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
   vinOkay = false;
  if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date))
   dateOkay = false;
  if (!Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time))
   timeOkay = false;

  if (!vinOkay)
   System.out.println("Please enter an alphanumeric value with 17 characters for VIN");
  if (!dateOkay)
   System.out.println("Please enter a date in this format: YYYY-MM-DD");
  if (!timeOkay)
   System.out.println("Please enter a time in this format: HH:MM");

  if (!vinOkay || !dateOkay || !timeOkay)
   return false;
  return true;
 }

 public static boolean approveReservations(ArrayList<String> reservations, Scanner scanner) {
  String cmd = "";
  System.out.println(ls + "These are your reservations:");
  for (String s : reservations)
   System.out.println(s);

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

 public static void registerCar(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String vin = "";
  String category = "";
  String make = "";
  String model = "";
  String year = "";

  while (true) {
   System.out.println(ls + "What is the VIN of your vehicle?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    vin = cmd;

   System.out.println(ls + "What is the category of your vehicle? (economy, comfort, or luxury)");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    category = cmd;

   System.out.println(ls + "What is the make of your vehicle?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    make = cmd;

   System.out.println(ls + "What is the model of your vehicle?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    model = cmd;

   System.out.println(ls + "What is the year of your vehicle?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    year = cmd;

   if (!checkCarRegistration(scanner, vin, category, year)) {
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   } else {
    query = String.format("select * from UC where VIN = '%s'", vin);
    ResultSet rs;
    try {
     rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (rs.next()) {
      System.out.println(ls + "Vehicle already exists");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue registering your vehicle" + ls);
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) {
       break;
      } else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    query = String.format(
      "INSERT INTO UC (VIN, ID, category, make, model, year) VALUES ('%s', '%s', '%s', '%s', '%s', %d)",
      vin, ID, category, make, model, Integer.parseInt(year));
    try {
     con.stmt.executeUpdate(query);
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue registering your vehicle" + ls);
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) {
       break;
      } else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    while (true) {
     System.out.println(ls + "Would you like to add another vehicle? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   }
  }
 }

 public static boolean checkCarRegistration(Scanner scanner, String vin, String category, String year) {
  Boolean vinOkay = true;
  Boolean categoryOkay = true;
  Boolean yearOkay = true;

  if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
   vinOkay = false;
  if (!category.equalsIgnoreCase("economy") && !category.equalsIgnoreCase("comfort")
    && !category.equalsIgnoreCase("luxury"))
   categoryOkay = false;
  try {
   Integer.parseInt(year);
  } catch (NumberFormatException e) {
   yearOkay = false;
  }

  if (!vinOkay)
   System.out.println("Please enter an alphanumeric value with 17 characters for VIN");
  if (!categoryOkay)
   System.out.println("Please enter economy, comfort, or luxury");
  if (!yearOkay)
   System.out.println("Please enter a valid year e.g.(1991");

  if (!vinOkay || !categoryOkay || !yearOkay)
   return false;

  return true;
 }

 public static void updateCar(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String vin = "";
  while (true) {
   System.out.println(ls + "What vehicle would you like to update information on?");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    vin = cmd;
   if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) {
    System.out.println(ls + "Please enter an alphanumeric value with 17 characters for VIN" + ls
      + "Would you like to try again? Y or N?");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   } else {
    query = String.format("Select * from UC where ID = %s AND VIN = %s", ID, vin);
    try {
     ResultSet rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (!rs.next()) {
      System.out.println(ls + "Vehicle does not exist or does not belong to you");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue with your vehicle lookup");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }

    System.out.println(ls + "What information would you like to modify on it today?");
    System.out.println("1: VIN");
    System.out.println("2: Category");
    System.out.println("3: Make");
    System.out.println("4: Model");
    System.out.println("5: Year");
    cmd = scanner.nextLine();
    switch (cmd) {
    case ("0"):
     return;
    case ("1"): {
     String newVin = "";
     System.out.println(ls + "What would you like to change your VIN to?");
     if ((cmd = scanner.nextLine()).equals("0"))
      return;
     else
      newVin = cmd;
     if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) {
      System.out.println(ls + "Please enter an alphanumeric value with 17 characters for VIN" + ls);
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     } else {
      query = String.format("Select * from UC where VIN = '%s'", newVin);
      try {
       ResultSet rs = con.stmt.executeQuery(query);
       ResultSetMetaData rsmd = rs.getMetaData();
       if (rs.next()) {
        System.out.println(ls + "Vehicle exists already");
        while (true) {
         System.out.println(ls + "Would you like to try again? Y or N?");
         if ((cmd = scanner.nextLine()).equals("Y"))
          break;
         else if (cmd.equals("0"))
          return;
         else if (cmd.equals("N"))
          return;
         else
          System.out.println(ls + "Not a valid option");
        }
        continue;
       }
      } catch (SQLException e) {
       System.out.println(ls + "There was an issue with your vehicle update");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return;
        else if (cmd.equals("N"))
         return;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }

      query = String.format("UPDATE UC SET VIN = '%s' where VIN = '%s'", newVin, vin);
      try {
       con.stmt.executeUpdate(query);
      } catch (SQLException e) {
       System.out.println(ls + "There was an issue with your vehicle update");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return;
        else if (cmd.equals("N"))
         return;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }
     }
     break;
    }
    case ("2"): {
     String category = "";
     System.out.println(ls + "What would you like to change your Category to?");
     if ((cmd = scanner.nextLine()).equals("0"))
      return;
     else
      category = cmd;
     if (!category.equalsIgnoreCase("economy") && !category.equalsIgnoreCase("comfort")
       && !category.equalsIgnoreCase("luxury")) {
      System.out.println("Please enter economy, comfort, or luxury");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     } else {
      query = String.format("UPDATE UC SET category = '%s' where VIN = '%s'", category, vin);
      try {
       con.stmt.executeUpdate(query);
      } catch (SQLException e) {
       System.out.println(ls + "There was an issue with your vehicle update");
       while (true) {
        System.out.println(ls + "Would you like to try again? Y or N?");
        if ((cmd = scanner.nextLine()).equals("Y"))
         break;
        else if (cmd.equals("0"))
         return;
        else if (cmd.equals("N"))
         return;
        else
         System.out.println(ls + "Not a valid option");
       }
       continue;
      }
     }

     break;
    }
    case ("3"): {
     String make = "";
     System.out.println(ls + "What would you like to change your Make to?");
     if ((cmd = scanner.nextLine()).equals("0"))
      return;
     else
      make = cmd;
     query = String.format("UPDATE UC SET make = '%s' where VIN = '%s'", make, vin);
     try {
      con.stmt.executeUpdate(query);
     } catch (SQLException e) {
      System.out.println(ls + "There was an issue with your vehicle update");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     break;
    }
    case ("4"): {
     String model = "";
     System.out.println(ls + "What would you like to change your Model to?");
     if ((cmd = scanner.nextLine()).equals("0"))
      return;
     else
      model = cmd;
     query = String.format("UPDATE UC SET model = '%s' where VIN = '%s'", model, vin);
     try {
      con.stmt.executeUpdate(query);
     } catch (SQLException e) {
      System.out.println(ls + "There was an issue with your vehicle update");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     break;
    }
    case ("5"): {
     String year = "";
     System.out.println(ls + "What would you like to change your Year to?");
     if ((cmd = scanner.nextLine()).equals("0"))
      return;
     else
      year = cmd;
     try {
      Integer.parseInt(year);
     } catch (NumberFormatException e) {
      System.out.println("Please enter a valid year e.g.(1991");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     query = String.format("UPDATE UC SET year = %d where VIN = '%s'", Integer.parseInt(year), vin);
     try {
      con.stmt.executeUpdate(query);
     } catch (SQLException e) {
      System.out.println(ls + "There was an issue with your vehicle update");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }

     break;
    }
    default: {
     System.out.println("Not a valid option");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    }
    while (true) {
     System.out.println(ls + "Would you like to make another change? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
   }
  }
 }

 public static void declareFavorite(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String vin = "";
  while (true) {
   System.out.println(
     "What vehicle would you like to make your favorite? Please enter an alphanumerical value of length 17");
   if ((cmd = scanner.nextLine()).equals("0"))
    return;
   else
    vin = cmd;

   if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) {
    System.out.println(ls + "Please enter an alphanumeric value with 17 characters for VIN" + ls
      + "Would you like to try again? Y or N?");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0"))
      return;
     else if (cmd.equals("N"))
      return;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   } else {
    query = String.format("Select * from UC where VIN = '%s'", vin);
    try {
     ResultSet rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (!rs.next()) {
      System.out.println(ls + "Vehicle does not exist");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y"))
        break;
       else if (cmd.equals("0"))
        return;
       else if (cmd.equals("N"))
        return;
       else
        System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue with your vehicle lookup");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }

    query = String.format("Select * from Favorites where loginName = '%s'", loginName);

    try {
     ResultSet rs = con.stmt.executeQuery(query);
     ResultSetMetaData rsmd = rs.getMetaData();
     if (!rs.next()) {
      query = String.format("INSERT INTO Favorites (loginName, VIN) VALUES ('%s', '%s')", loginName,
        vin);
      con.stmt.executeUpdate(query);
     } else {
      query = String.format("UPDATE Favorites SET vin = '%s' where loginName = '%s'", vin, loginName);
      con.stmt.executeQuery(query);
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue with your vehicle lookup or update");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y"))
       break;
      else if (cmd.equals("0"))
       return;
      else if (cmd.equals("N"))
       return;
      else
       System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   }
   return;
  }
 }

 public static ArrayList<String> setHours(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String timeStart = "";
  String timeEnd = "";
  ArrayList<String> times = new ArrayList<String>();
  System.out.println(ls + "You must set your work hours to register");
  while (true) {
   System.out.println(ls + "What time can you start? e.g.(HH:MM)");
   if ((cmd = scanner.nextLine()).equals("0")) {
    times.clear();
    return times;
   } else
    timeStart = cmd;

   System.out.println(ls + "What time do you want to end your shift? e.g.(HH:MM)");
   if ((cmd = scanner.next()).equals("0")) {
    times.clear();
    return times;
   } else
    timeEnd = cmd;

   if (!checkHours(timeStart, timeEnd, times)) {
    scanner.nextLine();
    while (true) {
     System.out.println(ls + "Would you like to try adding another time slot? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y"))
      break;
     else if (cmd.equals("0")) {
      times.clear();
      return times;
     } else if (cmd.equals("N"))
      return times;
     else
      System.out.println(ls + "Not a valid option");
    }
    continue;
   }

   times.add(timeStart + " " + timeEnd);

   while (true) {
    scanner.nextLine();
    System.out.println(ls + "Would you like to add another time slot? Y or N?");
    if ((cmd = scanner.nextLine()).equals("Y"))
     break;
    else if (cmd.equals("0")) {
     times.clear();
     return times;
    } else if (cmd.equals("N"))
     return times;
    else
     System.out.println(ls + "Not a valid option");
   }
   continue;
  }
 }

 private static Boolean checkHours(String startTime, String endTime, ArrayList<String> times) {
  Boolean startTimeOkay = true;
  Boolean endTimeOkay = true;
  String startHour = "";
  String endHour = "";
  String startMinute = "";
  String endMinute = "";
  int startH = 0;
  int startM = 0;
  int endH = 0;
  int endM = 0;

  if (!Pattern.matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", startTime))
   startTimeOkay = false;
  if (!Pattern.matches("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", endTime))
   endTimeOkay = false;

  if (!startTimeOkay)
   System.out.println(ls + "Please enter a starting time in this format: HH:MM");
  if (!endTimeOkay)
   System.out.println("Please enter an ending time in this format: HH:MM");

  if (!startTimeOkay || !endTimeOkay)
   return false;

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
   System.out.println(ls + "Your starting time cannot be greater than your ending time");
   return false;
  } else if (startH == endH) {
   if (startM >= endM) {
    System.out.println(ls + "Your starting time cannot be greater than your ending time");
    return false;
   }
  }

  if (times.isEmpty())
   return true;

  for (String s : times) {
   String sHourStart = "";
   String sMinStart = "";
   String sHourEnd = "";
   String sMinEnd = "";
   int sHStart = 0;
   int sHEnd = 0;
   int sMStart = 0;
   int sMEnd = 0;

   System.out.println(s);

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

   if (startH > sHStart) {
    if (startH < sHEnd) {
     System.out.println(ls + "You already added a schedule during this time");
     return false;
    } else if (startH == sHEnd) {
     if (startM > sMStart) {
      if (startM < sMEnd || startM == sMEnd)
       System.out.println(ls + "You already added a schedule during this time");
      return false;
     } else if (startM <= sMStart) {
      System.out.println(ls + "You already added a schedule during this time");
      return false;
     }
    }
   }
   if (startH < sHStart) {
    if (endH > sHStart) {
     System.out.println(ls + "You already added a schedule during this time");
     return false;
    } else if (endH == sHStart) {
     if (endM < sMEnd) {
      if (endM > sMStart || endM == sMStart)
       System.out.println(ls + "You already added a schedule during this time");
      return false;
     } else if (endM >= sMEnd) {
      System.out.println(ls + "You already added a schedule during this time");
      return false;
     }
    }

   }
   if (startH == sHStart) {
    if (startM > sMStart) {
     System.out.println(ls + "You already added a schedule during this time");
     return false;
    }
    if (startM < sMStart) {
     if (endM > sMStart)
      System.out.println(ls + "You already added a schedule during this time");
     return false;
    }
   }
  }

  return true;
 }
 
 public static void feedbackRecording(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String vin = "";
  String rating = "";
  String comment = "";
  Boolean addComment = false;
  
  while(true) {
   addComment = false;
   System.out.println("Which vehicle would you like feedback to?");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else vin = cmd;
   
   System.out.println("What rating would you like to give them? (0-10)");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else rating = cmd;
   
   while (true) {
    System.out.println(ls + "Would you like to add a comment? Y or N?");
    if ((cmd = scanner.nextLine()).equals("Y")) {
     System.out.println(ls + "Enter your comment (less than 300 characters)");
     if((cmd = scanner.nextLine()).equals("0")) return;
     else comment = cmd;
     addComment = true;
     break;
    }
    else if (cmd.equals("0")) return;
    else if (cmd.equals("N")) break;
    else System.out.println(ls + "Not a valid option");
   }
   
   if(!checkFeedback(vin, rating, comment)) {
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;
   }
   else {
    query = String.format("select * from Feedback f, UC_Rating u where f.feedbackID = u.feedbackID and u.VIN = '%s' and f.loginName = '%s'", vin, loginName);
    try {
     ResultSet rs = con.stmt.executeQuery(query);
     if(rs.next()) {
      System.out.println(ls + "You have already given a review for this vehicle");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y")) break;
       else if (cmd.equals("0")) return;
       else if (cmd.equals("N")) return;
       else System.out.println(ls + "Not a valid option");
      }
      continue;
     }
     else {
      Calendar calendar = Calendar.getInstance();
          java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
          System.out.println(date.toString());
      query = String.format("INSERT INTO Feedback (comment, score, loginName, date) VALUES ('%s', %d, '%s', '%s')", addComment ? comment : "", Integer.parseInt(rating), loginName, date.toString());
      con.stmt.executeUpdate(query);
      query = "SELECT feedbackID FROM Feedback ORDER BY feedbackID DESC LIMIT 1";
      rs = con.stmt.executeQuery(query);
      if(rs.next()) System.out.println("Your feedback ID is: " + rs.getInt("feedbackID"));
      else throw new SQLException();
      query = String.format("INSERT INTO UC_Rating (feedbackID, VIN) VALUES (%d, '%s')", rs.getInt("feedbackID"), vin);
      con.stmt.executeUpdate(query);
      return;
     }
    } catch (SQLException e) {
     System.out.println(ls + "There was an issue adding your review");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;     
    }
   }
  }
 }
 
 public static Boolean checkFeedback(String vin, String rating, String comment) {
  
  Boolean vinOkay = true;
  Boolean ratingOkay = true;
  Boolean commentOkay = true;
  int ratingInt = -1;
  
  if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) vinOkay = false;
  try {
   ratingInt = Integer.parseInt(rating);
  }
  catch(NumberFormatException e) {
   ratingOkay = false;
   System.out.println("You must enter an integer between 1-10 for your rating");
  }
  
  if(ratingInt < 1 || ratingInt > 10) {
   ratingOkay = false;
   System.out.println("You must enter an integer between 1-10 for your rating");
  }
 
  if(comment.length() > 300) {
   commentOkay = false;
   System.out.println("Comment must be less than 300 characters");
  }
  
  if(!vinOkay || !ratingOkay || !commentOkay) return false;
   
  return true;
 }
 
 public static void rateFeedback(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String feedbackID = "";
  String rating = "";
  
  while(true) {
   query = "select feedbackID, comment from Feedback";
   ResultSet rs;
   try {
    rs = con.stmt.executeQuery(query);
    System.out.println("**********UBER CAR FEEDBACK************");
    while(rs.next()) {
     System.out.println("ID:" + rs.getInt("feedbackID") + "                 " + "Comment: " + rs.getString("comment"));
    }
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue doing feedback rating");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;     
   }
   
   System.out.println(ls + "Which rating would you like to give feedback to? (give the id number)");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else feedbackID = cmd;
   
   try {
    Integer.parseInt(feedbackID);
   } catch(NumberFormatException e) {
    System.out.println(ls + "ID entered must be an integer");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue; 
   }
   
   query = String.format("select loginName from Feedback where feedbackID = %d", Integer.parseInt(feedbackID));
   try {
    rs = con.stmt.executeQuery(query);
    if(rs.next()){
     if(rs.getString("loginName").equals(loginName)){
      System.out.println("You cannot rate your own vehicle feedback");
      while (true) {
       System.out.println(ls + "Would you like to try again? Y or N?");
       if ((cmd = scanner.nextLine()).equals("Y")) break;
       else if (cmd.equals("0")) return;
       else if (cmd.equals("N")) return;
       else System.out.println(ls + "Not a valid option");
      }
      continue;
     }
    }
    else {
     System.out.println("feedback ID does not exist");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   } catch (SQLException e) {
    System.out.println("There was an issue looking up feedback");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;
   }
   
   System.out.println(ls + "Give a rating out of 3: 0 being 'useless', 1 being 'useful', 2 being 'very useful'");
   rating = cmd = scanner.nextLine();
   
   try {
    int ratingInt = Integer.parseInt(rating);
    if(ratingInt < 0 || ratingInt > 2) {
     System.out.println(ls + "Rating entered must be between 0 and 2");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   } catch(NumberFormatException e) {
    System.out.println(ls + "Rating entered must be an integer");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue; 
   }
   query = String.format("INSERT INTO feedback_Rating (loginName, feedbackID, rating) VALUES ('%s', %d, %d)", loginName, Integer.parseInt(feedbackID), Integer.parseInt(rating));
   try {
    con.stmt.executeUpdate(query);
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue adding your rating");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue; 
   }
   System.out.println("Feedback successfully rated");
   return;
  }
 }
 
 public static void usefulFeedbacks(Connector2 con, Scanner scanner) {
  String cmd = "";
  String query = "";
  String ID = "";
  String ratingsAmount = "";
  
  while(true) {
   query = "select ID, firstName, lastName from UD";
   ResultSet rs;
   try {
    rs = con.stmt.executeQuery(query);
    System.out.println("**********UBER CAR FEEDBACK************");
    while(rs.next()) {
     System.out.println("ID: " + rs.getInt("ID") + "  First Name: " + rs.getString("firstName") + "  Last Name: " + rs.getString("lastName"));
    }
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue during drivers loading");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;     
   }
   
   System.out.println(ls + "Which driver would you like to see useful feedbacks from? (Please give the ID)");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else ID = cmd;
   
   try {
    Integer.parseInt(cmd);
   }
   catch(NumberFormatException e) {
    System.out.println(ls + "ID entered must be an integer");
    while (true) {
     System.out.println(ls + "Would you like to try again? Y or N?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;
   }
   
   query = String.format("SELECT * FROM UD where ID = %d", Integer.parseInt(ID));
   try {
    rs = con.stmt.executeQuery(query);
    if(!rs.next()) {
     System.out.println(ls + "Driver does not exist");
     while (true) {
      System.out.println(ls + "Would you like to try again? Y or N?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;    
    }
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue looking up that driver");
    while (true) {
     System.out.println(ls + "Would you like to try again?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;    
   } 
   
   System.out.println(ls + "How many useful ratings would you like to see from this driver? (Enter an integer)");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else ratingsAmount = cmd;
   
   try {
    int amount = Integer.parseInt(ratingsAmount);
    if(amount < 1) {
     System.out.println(ls + "Number must be positive");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   } catch(NumberFormatException e) {
    System.out.println(ls + "Amount must be an integer");
    while (true) {
     System.out.println(ls + "Would you like to try again?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue; 
   }
   
   query = String.format("select s.feedbackID, s.comment, avg(s.rating)  as average  from (select f.feedbackID, f.comment, r.rating from Feedback f, feedback_Rating r, UC_Rating c, UC u where c.VIN = u.VIN and u.ID = %d and f.feedbackID = c.feedbackID and f.feedbackID = r.feedbackID) as s group by s.feedbackID order by average desc limit %d;", Integer.parseInt(ID), Integer.parseInt(ratingsAmount));
   try {
    rs = con.stmt.executeQuery(query);
    while(rs.next())
    System.out.println(String.format("Feedback ID: %d          Comment: %s", rs.getInt("feedbackID"), rs.getString("comment")));
    
   } catch (SQLException e) {
    System.out.println(ls + "There was an issue finding what you needed");
    while (true) {
     System.out.println(ls + "Would you like to try again?");
     if ((cmd = scanner.nextLine()).equals("Y")) break;
     else if (cmd.equals("0")) return;
     else if (cmd.equals("N")) return;
     else System.out.println(ls + "Not a valid option");
    }
    continue;
   }
   return;
  }
 }
 
 public static void statistics(Connector2 con, Scanner scanner) {
  String query = "";
  String cmd = "";
  String amount = "";
  
  
  while(true) {
   System.out.println(ls + "What statistics would you like to see?");
   System.out.println("1: Popular UC's");
   System.out.println("2: Expensive UC's");
   System.out.println("3: Highly Rated UD's");
   if((cmd = scanner.nextLine()).equals("0")) return;
   else if(cmd.equals("1")) {
    System.out.println(ls + "How many would you like to see?");
    if((cmd = scanner.nextLine()).equals("0")) return;
    else amount = cmd;
    try {
     Integer.parseInt(amount);
     query = String.format("select r.VIN, count(r.VIN) as count, u.category from UC u, Rides r where r.VIN = u.VIN group by VIN order by count desc limit %d;", Integer.parseInt(amount));
     ResultSet rs = con.stmt.executeQuery(query);
     System.out.println("********MOST POPULAR UC's PER CATEGORY*********");
     while(rs.next()){
      System.out.println(String.format("VIN: %s       Category: %s", rs.getString("VIN"), rs.getString("category")));
     }
    } catch(NumberFormatException e) {
     System.out.println(ls + "Must enter an integer");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    } catch(SQLException e) {
     System.out.println(ls + "There was an issue with your query");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
    
   }
   else if(cmd.equals("2")) {
    System.out.println(ls + "How many would you like to see?");
    if((cmd = scanner.nextLine()).equals("0")) return;
    else amount = cmd;
    try {
     Integer.parseInt(amount);
     query = String.format("select r.VIN, avg(r.cost) as avg, u.category from UC u, Rides r where r.VIN = u.VIN group by VIN order by avg desc limit %d;", Integer.parseInt(amount));
     ResultSet rs = con.stmt.executeQuery(query);
     System.out.println("********MOST EXPENSIVE UC's PER CATEGORY*********");
     while(rs.next()){
      System.out.println(String.format("VIN: %s       Category: %s", rs.getString("VIN"), rs.getString("category")));
     }
    } catch(NumberFormatException e) {
     System.out.println(ls + "Must enter an integer");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    } catch(SQLException e) {
     System.out.println(ls + "There was an issue with your query");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   }
   else if(cmd.equals("3")) {
    System.out.println(ls + "How many would you like to see?");
    if((cmd = scanner.nextLine()).equals("0")) return;
    else amount = cmd;
    try {
     Integer.parseInt(amount);
     query = String.format("select avg(users.score) avg, users.ID, users.firstName, users.lastName, users.category from (select c.ID, d.firstName, f.score, c.category, d.lastName from UC c, UD d, Feedback f, UC_Rating u where f.feedbackID = u.feedbackID and u.VIN = c.VIN and c.ID = d.ID order by c.category) as users group by category, ID order by avg, category desc limit %d", Integer.parseInt(amount));
     ResultSet rs = con.stmt.executeQuery(query);
     System.out.println("********TOP RATED UD's*********");
     while(rs.next()){
      System.out.println(String.format("ID: %d       First Name: %s       Last Name: %s        Category: %s", rs.getInt("ID"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("category")));
     }
    } catch(NumberFormatException e) {
     System.out.println(ls + "Must enter an integer");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    } catch(SQLException e) {
     System.out.println(ls + "There was an issue with your query");
     while (true) {
      System.out.println(ls + "Would you like to try again?");
      if ((cmd = scanner.nextLine()).equals("Y")) break;
      else if (cmd.equals("0")) return;
      else if (cmd.equals("N")) return;
      else System.out.println(ls + "Not a valid option");
     }
     continue;
    }
   }
  }
 }
}