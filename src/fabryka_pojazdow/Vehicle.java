package fabryka_pojazdow;

public class Vehicle 
{
	private String type;
	private double productionTime;
	private double cost;
	
	public Vehicle(String name, double productionTime, double cost)
	{
		this.type = name;
		this.productionTime = productionTime;
		this.cost = cost;
	}

	public String getType() 
	{
		return type;
	}

	public double getProductionTime() 
	{
		return productionTime;
	}

	public double getCost() 
	{
		return cost;
	}
		
}
