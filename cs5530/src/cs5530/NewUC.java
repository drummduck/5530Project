package cs5530;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NewUC {

	static String errorReturn = "Information entered was in the wrong format, or the vehicle already existed \n"
			+ "For vin please enter an alphanumeric value with 17 characters \n"
			+ "For the category please enter 'economy', 'comfort', or 'luxury' \n"
			+ "Please enter a valid year: e.g.(1991) '<BR>";
	static String errorUpdateReturn = "Information entered was in the wrong format, or the vehicle didn't exist \n"
			+ "For vin please enter an alphanumeric value with 17 characters \n"
			+ "For the category please enter 'economy', 'comfort', or 'luxury' \n"
			+ "Please enter a valid year: e.g.(1991) '<BR>";

	public static String registerCar(String vin, String category, String make, String model, String year, String ID,
			Connector con) {
		String query = "";

		if (!checkCarRegistration(vin, category, year)) {
			return errorReturn;
		}

		query = String.format("select * from UC where VIN = '%s'", vin);
		ResultSet rs;
		try {
			rs = con.stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			if (rs.next()) {
				return errorReturn;
			}

			query = String.format(
					"INSERT INTO UC (VIN, ID, category, make, model, year) VALUES ('%s', '%s', '%s', '%s', '%s', %d)",
					vin, ID, category, make, model, Integer.parseInt(year));

			con.stmt.executeUpdate(query);

		} catch (SQLException e) {
			return errorReturn;
		}

		return "";
	}

	private static boolean checkCarRegistration(String vin, String category, String year) {
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

		if (!vinOkay || !categoryOkay || !yearOkay)
			return false;

		return true;
	}

	public static String updateCar(String vin, String newVin, String category, String make, String model, String year, Connector con) {
		
		String query = "";
		Boolean updateCategory = false;
		Boolean updateVin = false;
		Boolean updateMake = false;
		Boolean updateModel = false;
		Boolean updateYear = false;
		
		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17) return errorReturn;
		
		query = String.format("select * from UC where VIN = '%s'", vin);
		ResultSet rs;
		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				return errorUpdateReturn;
			}
		}
		catch(SQLException e) {
			return errorUpdateReturn;
		}
		
		
		if(!newVin.isEmpty()) updateVin = true;
		if(!category.isEmpty()) updateCategory = true;
		if(!make.isEmpty()) updateMake = true;
		if(!model.isEmpty()) updateModel = true;
		if(!year.isEmpty()) updateYear = true;
		

		if(updateCategory) {
			if (!category.equalsIgnoreCase("economy") && !category.equalsIgnoreCase("comfort") && !category.equalsIgnoreCase("luxury")) 
				return errorUpdateReturn;			
			query = String.format("UPDATE UC SET category = '%s' where VIN = '%s'", category, vin);
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				return errorUpdateReturn;
			}
		}
		if(updateMake) {
			query = String.format("UPDATE UC SET make = '%s' where VIN = '%s'", newVin, vin);
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				return errorUpdateReturn;
			}
		}
		if(updateModel) {
			query = String.format("UPDATE UC SET model = '%s' where VIN = '%s'", newVin, vin);
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				return errorUpdateReturn;
			}
		}
		if(updateYear) {
			try {
				Integer.parseInt(year);
			} catch (NumberFormatException e) {
				return errorUpdateReturn;
			}			
			query = String.format("UPDATE UC SET year = '%d' where VIN = '%s'", Integer.parseInt(year), vin);
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				return errorUpdateReturn;
			}
		}
		if(updateVin) {
			if (!Pattern.matches("^[a-zA-Z0-9]*$", newVin) || newVin.length() != 17) return errorUpdateReturn;
			query = String.format("UPDATE UC SET VIN = '%s' where VIN = '%s'", newVin, vin);
			try {
				con.stmt.executeUpdate(query);
			} catch (SQLException e) {
				return errorUpdateReturn;
			}
		}
		
		return "";
	}
}
