package fabryka_pojazdow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Object <code>Factory</code> simulates Factory of vehicles.
 */
public class Factory
{
	/**
	 * Object <code>ProductionLine</code> simulates Production Line in the Factory.
	 */
	private class ProductionLine implements Runnable
	{
		private Vehicle vehicleToProduce;
		
		public ProductionLine(Vehicle vehicleToProduce)
		{
			this.vehicleToProduce = vehicleToProduce;
		}

		public void run()
		{
			try 
			{
				Thread.sleep((long) (vehicleToProduce.getProductionTime() * 1000));
				System.out.println("Produced: " + vehicleToProduce.getType());
				threads.remove(Thread.currentThread());
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	

	private ArrayList<Vehicle> typesOfVehicles;
	private double finalCost;
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private int maxThreads;
	
	public Factory()
	{
		this.typesOfVehicles = new ArrayList<Vehicle>();
		maxThreads = 3;
	}
	
	public Factory(ArrayList<Vehicle> vehicles, int maxThreads)
	{
		this.typesOfVehicles = vehicles;
		this.maxThreads = maxThreads;
	}
	
	public void addNewVehicle(Vehicle vehicle)
	{
		typesOfVehicles.add(vehicle);
	}
	
	public void addNewVehicle(String name, double productionTime, double cost)
	{
		typesOfVehicles.add(new Vehicle(name, productionTime, cost));
	}

	/**
	 * Turns on production lines.
	 * @param vehicleType - type of vehicle to produce.
	 * @throws InterruptedException
	 */
	public void produceVehicle(String vehicleType) throws InterruptedException
	{
		
		Vehicle vehicleToProduce = null;
		for(Vehicle vehicle : typesOfVehicles)
			if(vehicle.getType().equals(vehicleType)) 
				{
					vehicleToProduce = vehicle;
					break;
				}
		
		if(vehicleToProduce == null)
			System.out.println("Type " + vehicleType + " does not exist.");
		else
		{
			finalCost += vehicleToProduce.getCost();
			Thread t = new Thread(new ProductionLine(vehicleToProduce));
			
			while(threads.size() == maxThreads) Thread.sleep(1); // Waits for empty Production Line (thread).

			threads.add(t);
			t.start();
		}
		
	}
	
	/**
	 * Interprets the order and starts producing specify type of vehicle.
	 * @param order - xml file containing the order.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void realiseOrder(File order) throws ParserConfigurationException, SAXException, IOException, InterruptedException
	{
		ArrayList<String> typesToProduce = new ArrayList<String>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(order);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("item");

		for (int temp = 0; temp < nList.getLength(); temp++) 
		{
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element eElement = (Element) nNode;
				typesToProduce.add(eElement.getAttribute("type"));
			}
		}
		
		for(String type : typesToProduce)
				produceVehicle(type);
		
		while(!threads.isEmpty()) Thread.sleep(1);
		System.out.println(finalCost);

	}
	


	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InterruptedException
	{
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		vehicles.add(new Vehicle("car", 10, 1000));
		vehicles.add(new Vehicle("motorcycle", 5, 600));
		vehicles.add(new Vehicle("truck", 15, 2000));
		

		File order = new File("order.xml");
		Factory factory = new Factory(vehicles,3);
		factory.realiseOrder(order);
		
	}
}
