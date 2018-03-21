package cs5530;

public class Ride {
	private String dist;
	private String date;
	private String cost;
	private String numOfPeople;
	private String VIN;
	
	Ride(String dist, String date, String cost, String numOfPeople, String VIN){
		this.dist = dist;
		this.date = date;
		this.cost = cost;
		this.numOfPeople = numOfPeople;
		this.VIN = VIN;
	}
	
	public String getDist(){
		return dist;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getCost(){
		return cost;
	}
	
	public String getNumOfPeople(){
		return numOfPeople;
	}
	
	public String getVIN(){
		return VIN;
	}
	
	@Override
	public String toString() {
		String output;
		output = String.format("Date: %s, Vehicle: %s, Distance Traveled: %s, Cost: %s, Number Of People: %s", date, VIN, dist, cost, numOfPeople);
		return output;
	}
}

