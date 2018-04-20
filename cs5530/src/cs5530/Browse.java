package cs5530;

import java.util.Scanner;
import java.sql.*;
import java.util.TreeSet;

public class Browse {

	static String errorReturn = "Information entered was in the wrong format \n"
			+ "For category please enter economy, comfort, or luxury'<BR>";

	public static String browser(Connector2 con, String categoryIn, String modelIn, String addressIn) {
		Scanner scan = new Scanner(System.in);
		String query = "";

		String[] categoriesIn = categoryIn.split(",");
		String[] modelsIn = modelIn.split(",");
		String[] addressesIn = addressIn.split(",");

		TreeSet<String> category = new TreeSet<String>();
		TreeSet<String> address = new TreeSet<String>();
		TreeSet<String> model = new TreeSet<String>();

		for (String s : categoriesIn) {
			if (!addC(category, con, s))
				return errorReturn;
		}
		for (String s : addressesIn) {
			if (!addA(address, con, s))
				return errorReturn;
		}
		for (String s : modelsIn) {
			if (!s.equalsIgnoreCase("economy") && !s.equalsIgnoreCase("comfort")
					&& !s.equalsIgnoreCase("luxury"))
				return errorReturn;
			if (!addM(model, con, s))
				return errorReturn;
		}

		ResultSet rs;
		String returnString = "";
		if (category.size() == 0 && address.size() == 0 && model.size() == 0) {

			query = String.format(
					"select b.vin, AVG(f.score) as average from UD a join UC b on a.id = b.id join UC_Rating r on b.vin = r.vin join Feedback f on f.feedbackID = r.feedbackID Group by b.vin order by average desc;");
			try {
				rs = con.stmt.executeQuery(query);
				while (rs.next()) {
					returnString = returnString + rs.getString("VIN") + ", rating: " + rs.getString("average");
				}
			} catch (Exception e) {
				return errorReturn;
			}

		} else {
			String start = "select b.vin, AVG(f.score) as average from UD a join UC b on a.id = b.id join UC_Rating r on b.vin = r.vin join Feedback f on f.feedbackID = r.feedbackID where ";
			Boolean and = false;
			if (category.size() != 0) {
				and = true;
				start += "b.category = ";
				for (String x : category) {
					start += "'" + x + "' or ";
				}
				start = start.substring(0, start.length() - 3);
			}
			if (address.size() != 0) {
				if (and)
					start += "and ";
				and = true;
				start += "a.address like ";
				for (String x : address) {
					start += "'%" + x + "%' or ";
				}
				start = start.substring(0, start.length() - 3);
			}
			if (model.size() != 0) {
				if (and)
					start += "and ";
				start += "b.model = ";
				for (String x : model) {
					start += "'" + x + "' or ";
				}
				start = start.substring(0, start.length() - 3);
			}

			start += "Group by b.vin order by average desc";

			try {
				System.out.println("Searching...\n" + start);
				rs = con.stmt.executeQuery(start);
				System.out.println("Results:\n");
				while (rs.next()) {
					returnString = returnString + rs.getString("VIN") + ", rating: " + rs.getString("average");
				}
			} catch (Exception e) {
				return errorReturn;
			}
		}

		return returnString;
	}

	public static boolean addC(TreeSet ts, Connector2 con, String entry) {
		String query = String.format("select * from UC where category = '%s'", entry);
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				ts.add(entry);
				return true;
			} 
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean addA(TreeSet ts, Connector2 con, String entry) {
		Scanner scan = new Scanner(System.in);
		String x = "%" + entry + "%";
		String query = String.format("select * from UD a join UC b on a.id = b.id where address like '%s'", x);
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				ts.add(entry);
				return true;
			} 
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static boolean addM(TreeSet ts, Connector2 con, String entry) {
		Scanner scan = new Scanner(System.in);

		String query = String.format("select * from UC where model = '%s'", entry);
		try {
			ResultSet rs = con.stmt.executeQuery(query);
			if (rs.next()) {
				ts.add(entry);
				return true;
			} 
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
