package cs5530;

import java.util.Scanner;
import java.sql.*;


public class Separation{
  public static void firstDegree(Connector2 con){
    Scanner scan = new Scanner(System.in);
    String query = "";
    System.out.println("Provide two usernames to determine the degree of separation.");
    String login1 = "";
    String login2 = "";

    while(true){
      System.out.println("Enter the first username:");
      login1 = scan.nextLine();
      query = String.format("select * from UU where loginName = '%s'", login1);
      try{
        ResultSet rs = con.stmt.executeQuery(query);
        if(!rs.next()){
          if(Trust.notValid("Username doesn't exist.")) return; 
        }else
          break;
        
      }catch(Exception e){
      
      
    }
    }
    while(true){
      System.out.println("Enter the second username:");
      login2 = scan.nextLine();
      query = String.format("select * from UU where loginName = '%s'", login2);
      try{
        ResultSet rs = con.stmt.executeQuery(query);
        if(!rs.next()){
          if(Trust.notValid("Username doesn't exist.")) return; 
        }else
          break;
        
      }catch(Exception e){
    }
    }
    query = String.format("SELECT * from Favorites a JOIN Favorites b on a.vin = b.vin where a.loginName = '%s' and b.loginName = '%s'",login1,login2);
    try{
        ResultSet rs = con.stmt.executeQuery(query);
        if(rs.next()){
          System.out.println("User's " + login1 + " and " + login2 + " are 1-degree away.");
        }else
          System.out.println("User's " + login1 + " and " + login2 + " are further than 1-degree away or have no connection."); 
    }catch(Exception e){}
    
  }
}
  
  

