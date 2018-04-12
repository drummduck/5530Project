package cs5530;

import java.util.regex.Pattern;

public class FeedbackRecordings {

	public static String recordFeedback() {
		return "";
	}
	
	private static Boolean checkFeedback(String vin, String rating, String comment) {

		Boolean vinOkay = true;
		Boolean ratingOkay = true;
		Boolean commentOkay = true;
		int ratingInt = -1;

		if (!Pattern.matches("^[a-zA-Z0-9]*$", vin) || vin.length() != 17)
			vinOkay = false;
		try {
			ratingInt = Integer.parseInt(rating);
		} catch (NumberFormatException e) {
			ratingOkay = false;
			System.out.println("You must enter an integer between 1-10 for your rating");
		}

		if (ratingInt < 1 || ratingInt > 10) {
			ratingOkay = false;
			System.out.println("You must enter an integer between 1-10 for your rating");
		}

		if (comment.length() > 300) {
			commentOkay = false;
			System.out.println("Comment must be less than 300 characters");
		}

		if (!vinOkay || !ratingOkay || !commentOkay)
			return false;

		return true;
	}
}
