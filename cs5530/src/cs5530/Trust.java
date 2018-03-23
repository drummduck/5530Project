package cs5530;
import java.util.Scanner;
import java.sql.*;

public class Trust{
  
  
  //This method implements the trust recordings defined in part 8 of System Functionality
  public static void declareTrust(Connector2 con, String login){
    String login1 = login;
    String cmd="";
    String query = "";
    String login2 = "";
    
    while(true) {
      System.out.println("Please enter the username of whom you would like to assign a value of trust to:");
      
      Scanner scanner = new Scanner(System.in);
      if((cmd = scanner.nextLine()).equals("0")) return;
      else if(cmd.equals(login1)){ 
        System.out.println("You cannot rate yourself,");
      }else{
        login2 = cmd;
        query = String.format("select * from UU where loginName = '%s'", login2);
        try{
          ResultSet rs = con.stmt.executeQuery(query);

          if(!rs.next()){
           if(notValid("Username doesn't exist.")) return;      
          }else{
            query = String.format("select * from user_Rating where loginName1 = '%s' and loginName2 = '%s'",login1,login2);
            try{
              rs = con.stmt.executeQuery(query);
              
              Boolean update = false;
              if(rs.next()){
                update = true;
                if(rs.getInt("trust") == 1){
                  System.out.println("You currently trust " + login2 + ",\nPlease carefully select one of the following:");
                }else if(rs.getInt("trust") == 0){
                  System.out.println("You currently do not trust " + login2 + ",\nPlease carefully select one of the following:");
                }
              }else{
                System.out.println("You have yet to rate " + login2 + ",\nPlease carefully select one of the following:");
              }
              Boolean finished = false;
              while(!finished){
                
                System.out.println("1: I trust this user");
                System.out.println("0: I do not trust this user");
                int a = 2;
                try{ a = scanner.nextInt();
                }catch(Exception e){}
                if(a == 1 || a == 0){
                  finished = true;
                }
                if(finished){
                  try{
                    
                    if(update){
                      query = String.format("update user_Rating set trust = '%d' where loginName1 = '%s' and loginName2 = '%s'",a,login1,login2);
                    }else{
                      query = String.format("insert into user_Rating (trust,loginName1,loginName2) values ('%d','%s','%s')",a,login1,login2);
                    }
                    
                    con.stmt.executeUpdate(query);
                    System.out.println("Rating complete, thank you for your feedback.\nNow returning to main menu...");
                    return;
                  }catch(SQLException e){
                    System.out.println("Error in updating, please try again.");
                  }
                }else{
                  if(notValid("Not a valid response."))return;
                }
              }
              
            }catch(Exception e){
              System.out.println("An error occurred, please try again.");
            }
          }
        }catch(Exception e){
          System.out.println("An error occurred, please try again.");
        }
      }
    }
  }
  
  
  
  //Helper method for asking the user to continue after an invalid response
  public static Boolean notValid(String message){
    Scanner scanner = new Scanner(System.in);
    System.out.println(message);
    String cmd;
    while(true) {
      System.out.println(System.getProperty("line.separator") + "Would you like to try again? Y or N?");
      if((cmd = scanner.nextLine()).equals("Y")) break;
      else if(cmd.equals("0")) return true;
      else if(cmd.equals("N")) return true;
      else System.out.println(System.getProperty("line.separator") + "Not a valid option");
    }
    return false;
  }
}