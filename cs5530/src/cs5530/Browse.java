package cs5530;

import java.util.Scanner;
import java.sql.*;
import java.util.TreeSet;

public class Browse{
  
    public static void browser(Connector2 con){
      Scanner scan = new Scanner(System.in);
      String query = "";
      
      TreeSet<String> category = new TreeSet<String>();
      TreeSet<String> address = new TreeSet<String>();
      TreeSet<String> model = new TreeSet<String>();
      
      System.out.println("Welcome to UC browser, here you may search by a combination of category address or model");
      while(true) {
        System.out.println("You are currently searching by:");
        System.out.print("Category: ");
        for(String x : category){
          System.out.print(x + ", ");
        }
        System.out.print("\nAddress: ");
        for(String x : address){
          System.out.print(x + ", ");
        }
        System.out.print("\nModel: ");
        for(String x : model){
          System.out.print(x + ", ");
        }
        System.out.println("\nPlease select from the following:");
        System.out.println("1: add category");
        System.out.println("2: add address");
        System.out.println("3: add model");
        System.out.println("4: clear all");
        System.out.println("5: run search");
        System.out.println("0: exit");
        String option = scan.nextLine();
        switch(option){
          case"0":
            return;
          case "1":
            addC(category, con);
            break;
          case "2": 
            addA(address, con);
            break;
          case "3":       
            addM(model, con);
            break;
          case "4":
            category.clear();
            address.clear();
            model.clear();
            break;
          case "5":
            search(category, address, model, con);
            break;
          default:
            if(Trust.notValid("Not a valid option")) return;
        }
      }
    }
    public static void addC(TreeSet ts, Connector2 con){
      Scanner scan = new Scanner(System.in);
      while(true){
        System.out.println("Please enter a category of car you would like to search:");
        String entry = scan.nextLine();
        String query = String.format("select * from UC where category = '%s'",entry); 
        try{  
          ResultSet rs = con.stmt.executeQuery(query);
          if(rs.next()){
            ts.add(entry);
            return;
          }else{
            if(Trust.notValid("The category '" + entry + "' does not exists within our records.")) return;
          }
          
        }catch(Exception e){
          System.out.println("An error occurred.");
        }
      }
    }
    public static void addA(TreeSet ts, Connector2 con){
      Scanner scan = new Scanner(System.in);
      while(true){
        System.out.println("Please enter an address, you may search by City, State, or any substring of the address:");
        String entry = scan.nextLine();
        String x = "%" + entry + "%";
        String query = String.format("select * from UD a join UC b on a.id = b.id where address like '%s'",x); 
        try{  
          ResultSet rs = con.stmt.executeQuery(query);
          if(rs.next()){
            ts.add(entry);
            return;
          }else{
            if(Trust.notValid("The address '" + entry + "' does not exists within our records.")) return;
          }
          
        }catch(Exception e){
          System.out.println("An error occurred.");
        }
      }
    }
    public static void addM(TreeSet ts, Connector2 con){
      Scanner scan = new Scanner(System.in);
      while(true){
        System.out.println("Please enter the car model you would like to search:");
        String entry = scan.nextLine();
        String query = String.format("select * from UC where model = '%s'",entry); 
        try{  
          ResultSet rs = con.stmt.executeQuery(query);
          if(rs.next()){
            ts.add(entry);
            return;
          }else{
            if(Trust.notValid("The model '" + entry + "' does not exists within our records.")) return;
          }
          
        }catch(Exception e){
          System.out.println("An error occurred.");
        }
      }
      
    }
    public static void search(TreeSet<String> cat, TreeSet<String> add, TreeSet<String> mod, Connector2 con){
      ResultSet rs;
      if(cat.size() == 0 && add.size() == 0 && mod.size() == 0){
        System.out.println("No search parameters given, returning all cars:\n");
        
        String query = String.format("select b.vin, AVG(f.score) as average from UD a join UC b on a.id = b.id join UC_Rating r on b.vin = r.vin join Feedback f on f.feedbackID = r.feedbackID Group by b.vin order by average desc;");
        try{
        rs = con.stmt.executeQuery(query);
         while(rs.next()){
          System.out.println(rs.getString("VIN") + ", rating: " + rs.getString("average"));
        }
         System.out.println();
         return;
        }catch(Exception e){
          System.out.println("An error occurred.");
          return;
        }
        
      }else{
        String start = "select b.vin, AVG(f.score) as average from UD a join UC b on a.id = b.id join UC_Rating r on b.vin = r.vin join Feedback f on f.feedbackID = r.feedbackID where ";
        Boolean and = false;
        if(cat.size() != 0){
          and = true;
          start += "b.category = ";
          for(String x : cat){
            start += "'" + x + "' or "; 
          }
          start = start.substring(0, start.length() - 3);
        }
        if(add.size() != 0){
          if(and) start += "and ";
          and = true;
          start += "a.address like ";
          for(String x : add){
            start += "'%" + x + "%' or "; 
          }
          start = start.substring(0, start.length() - 3);
        }
        if(mod.size() != 0){
          if(and) start += "and ";
          start += "b.model = ";
          for(String x : mod){
            start += "'" + x + "' or "; 
          }
          start = start.substring(0, start.length() - 3);
        }    
        
        start += "Group by b.vin order by average desc";
        
        try{
          System.out.println("Searching...\n" + start);
          rs = con.stmt.executeQuery(start);
          System.out.println("Results:\n");
          while(rs.next()){
            System.out.println(rs.getString("VIN") + ", rating: " + rs.getString("average"));
          }
          System.out.println();
          return;
        }catch(Exception e){
          System.out.println("An error occurred.");
          return;
        }
      }
      
    }
}
