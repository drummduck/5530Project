package cs5530;

import java.util.Scanner;
import java.sql.*;
import java.util.TreeSet;

public class Award{
  
  public static void findBest(Connector2 con){
    Scanner scan = new Scanner(System.in);
    String query = "";
    int count = 0;
    System.out.println("Display the most trusted users and then the users with the most useful feedbacks:");
    System.out.println("How many top users would you like to see?");
    try{
      count = scan.nextInt();
      topTrust(count,con);
      topUseful(count,con);
    }catch(Exception e){
      System.out.println("Not a valid option");
    }       
  }
  
  public static void topTrust(int count, Connector2 con){
    if (count == 0) return;
    else if(count>1){
      System.out.println("Top " + count + " users with the highest net trust value:");
    }else if(count == 1){
      System.out.println("User with the highest net trust value:");
    }
    String query = String.format("SELECT loginName2, ((Select count(*) FROM user_Rating b where trust = 1 and a.loginName2 = b.loginName2) - (Select count(*) FROM user_Rating c where trust = 0 and a.loginName2 = c.loginName2)) as rating FROM user_Rating a Group by loginName2 order by rating desc;");
    try{
      ResultSet rs = con.stmt.executeQuery(query);
      for(int i = 0; i < count; i++){
        if(rs.next())
          System.out.println(rs.getString("loginName2") + ", Net trust: " + rs.getInt("rating"));
        else
          break;
      }
    }catch(Exception e){
      System.out.println("An error occurred.");
    }
  }
  public static void topUseful(int count, Connector2 con){
    if (count == 0) return;
    else if(count>1){
      System.out.println("Top " + count + " users with the most useful feedback:");
    }else if(count == 1){
      System.out.println("User with the most useful feedback:");
    }
    String query = String.format("SELECT a.loginName, AVG(rating) as r FROM Feedback a join feedback_Rating b on a.feedbackID = b.feedbackID Group By a.loginName order by r desc;");
    try{
      ResultSet rs = con.stmt.executeQuery(query);
      for(int i = 0; i < count; i++){
        if(rs.next())
          System.out.println(rs.getString("a.loginName") + ", Average feedback rating: " + rs.getDouble("r"));
        else
          break;
      }
    }catch(Exception e){
      System.out.println("An error occurred.");
    }
  
  }
}  