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
            addA(address);
            break;
          case "3":       
            addM(model);
            break;
          case "4":
            category.clear();
            address.clear();
            model.clear();
            break;
          case "5":
            search(category, address, model);
            break;
          default:
            if(Trust.notValid("Not a valid option")) return;
        }
      }
    }
    public static void addC(TreeSet ts, Connector2 con){
      Scanner scan = new Scanner(System.in);
      while(true){
        System.out.println("Please enter a type of car you would like to search:");
        String entry = scan.nextLine();
        String query = String.format("select * from UC where category = '%s'",entry); 
        try{  
          ResultSet rs = con.stmt.executeQuery(query);
          if(rs.next()){
            ts.add(entry);
          }else{
            if(Trust.notValid("The category " + entry + " does not exists within our records.")) return;
          }
          
        }catch(Exception e){
          System.out.println("An error occurred.");
        }
      }
    }
    public static void addA(TreeSet ts){
      Scanner scan = new Scanner(System.in);
      String entry = scan.nextLine();
    }
    public static void addM(TreeSet ts){
      Scanner scan = new Scanner(System.in);
      String entry = scan.nextLine();
      
    }
    public static void search(TreeSet cat, TreeSet add, TreeSet mod){}
}
