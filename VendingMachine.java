package com.vending.maven.eclipse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VendingMachine {
	public static void main( String[] args ) throws IOException, JSONException
	    {
		File auditFile;
		 try {
			 auditFile = new File("audit.txt");
			 if(auditFile.createNewFile()) System.out.println("Audit file succesfully created");
			 else System.out.println("Audit File already exists, new info will be ammended to end of current file");
		 } catch (IOException er) {
			 System.out.println("File could not be created");
		 }
		 FileWriter audit = new FileWriter("audit.txt", true);
		 audit.write("-Starting up Machine\n");
		 audit.write("-Beginning Initial Setup\n");
		 Scanner inp = new Scanner(System.in);
		 System.out.println("Input initial file to import items from: ");
		 String inputFile = inp.nextLine();
		 Item[][] machine = stockMachine(inputFile, audit);
		 audit.write("-Initial Setup Complete\n");
		 displayConsole(machine);
		 audit.write("-Starting Menu\n");
		 menu(machine, audit);
		 audit.write("-Program Terminated\n\n");
		 audit.close();
		 System.out.println("Audit file written");
		 inp.close();
		 System.exit(0);
	    }
	 
	//Custom class that contains item price, amount, and name
	public static class Item {
		String name;
		float price;
		Integer amount;
		
		public String getName() {
			return name;
		}
		public int getAmount() {
			return amount;
		}
		public float getPrice() {
			return price;
		}
		
	}
	
	//Handles selection of what should happen next, is recursive and only exits when told
	public static void menu(Item[][] machine, FileWriter audit) throws JSONException, IOException {
		Scanner inp = new Scanner(System.in);
		System.out.print("Would you like to (1) Make a Purchase, (2) Load in new products, (3) Display machine layout, or (4) quit the program? (Enter Number of Choice)");
		String menuSelect = inp.nextLine();
		if(menuSelect.equals("1")) {
			audit.write("-Taking Order\n");
			takeOrder(machine, audit);
		}
		else if(menuSelect.equals("2")) {
			System.out.print("Enter file to read from: ");
			String newInput = inp.nextLine();
			audit.write("-Restocking Machine\n");
			machine = stockMachine(newInput, audit);
		}
		else if(menuSelect.equals("3")) {
			audit.write("-Displaying Machine to Console\n");
			displayConsole(machine);
		}
		else if (menuSelect.equals("4")) {
			audit.write("-Quitting Menu\n");
			System.out.println("System shutting down");
			return;
		}
		else {
			System.out.println("Not a valid selection");
			audit.write("-Invalid Menu Selection Given: " + menuSelect);
		}
		menu(machine, audit);	
	}
	
	//Takes a json input file and fills it into a 2D array of Item objects
	public static Item[][] stockMachine(String fileName, FileWriter audit) throws JSONException, IOException {
		FileReader reader;
		Item[][] machine = new Item[0][0];
		try {
			reader = new FileReader(fileName); 
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject root = new JSONObject(tokener);
			JSONArray items = root.getJSONArray("items");
			audit.write("-Succesfully Read JSON File\n");
			int totalItems = items.length();	
				//Grabbing the config section of json in order to get size of vending machine, this assumes the
				//json format is consistant as it grabs characters
				String size = root.getJSONObject("config").toString();
				String[] configArray = size.split("[{:,}]");
				int columns = Integer.parseInt(configArray[2]);
				int rows = Integer.parseInt(configArray[4]);
				//The Vending machine will be a 2D array filled with a custom class "Item"
				machine = new Item[rows][columns];		
				//Putting all items into an array for easy access
				Item[] itemList = new Item[totalItems];
				for(int i = 0; i < totalItems; i++) {
					Item venItem = new Item();
					JSONObject tmpItem = items.getJSONObject(i);
					String tmpName = tmpItem.getString("name");
					int tmpAmount = tmpItem.getInt("amount");
					String tmpPrice = tmpItem.getString("price").substring(1);
					venItem.name = tmpName;
					venItem.amount = tmpAmount;
					venItem.price = Float.parseFloat(tmpPrice);	
					itemList[i] = venItem;
				}
				Item emptyItem = new Item();
				emptyItem.name = "Empty";
				emptyItem.amount = 0;
				emptyItem.price = 0.0f;
				//Filling up Machine
				int x = 0;
				for(int i = 0; i < columns; i++) {
					for(int j = 0; j < rows; j++) {
						if(x < totalItems) machine[j][i] = itemList[x];
						else machine[j][i] = emptyItem;
						x++;
					}
				}

		} catch (FileNotFoundException err) {
			audit.write("-Error Opening JSON File: File Not Found\n");
			System.out.println("File not Found");
			menu(machine, audit);
		}
		audit.write("-Machine Succesfully Stocked\n");
		System.out.println("Machine Succesfully Stocked");
		return machine;
	}
	
	//Takes user input and translates it into coordinates for 2D array
	public static void takeOrder(Item[][] machine, FileWriter audit) throws IOException {
		Scanner inp = new Scanner(System.in);
		audit.write("-Requesting User for Input\n");
		System.out.print("Please Enter Selection:");
		String selection = inp.nextLine();
		audit.write("-User Input = " + selection + "\n");
		System.out.println("Selected: " + selection);
		//Convert input to numbers to use in array
		int row = selection.charAt(0) - 65;
		int col = Character.getNumericValue(selection.charAt(1));
		if(row <= (machine[0].length - 1) && col <= (machine.length - 1)) {
			if(machine[col][row].name == "Empty") {
				audit.write("-Selected Item was Empty\n");
				System.out.println(selection + " is currently empty");
			}
			else if(machine[col][row].amount == 0) {
				audit.write("-Selected Item is Out of Stock\n");
				System.out.println(machine[col][row].name + " is currently out of stock");
			}
			else makePurchase(machine[col][row], audit);
		}
		else {
			audit.write("-User Gave Invalid Selection: " + selection + "\n");
			System.out.println("Not a valid selection");
			takeOrder(machine, audit);
		}
		
	}
	
	//Function that takes in payment and calculates change, also checks if payment is enough for product
	public static void makePurchase(Item selected, FileWriter audit) throws IOException {
		System.out.println("Item Selected: " + selected.name + " | Total Cost: $" + selected.price + " | " + selected.amount + " In Stock");
		audit.write("-Item Selected: " + selected.name + " | Total Cost: $" + selected.price + " | " + selected.amount + " in stock\n");
		Scanner inp = new Scanner(System.in);
		System.out.print("Enter payment in USD: ");
		audit.write("-Taking Payment from User\n");
		float payment = 0;
		try {
			payment = Float.parseFloat(inp.nextLine());
		} catch (NumberFormatException ex) {
			audit.write("-Invalid Payment Given\n");
			System.out.println("Not a valid payment");
		}
		if(payment >= selected.price){
			audit.write("-Valid Payment Given: " + payment + "\n");
			System.out.println(selected.name + " dispensed");
			audit.write("-" + selected.name + " Dispensed\n");
			float change =  payment - selected.price;
			float changeRounded = round(change); 
			if(change != 0) {
				audit.write("-$" + changeRounded + " Dispensed as Change\n");
				System.out.println("$" + changeRounded + " dispensed as change");
			}
			else {
				audit.write("-No change dispensed\n");
				System.out.println("No change dispensed");
			}
			selected.amount -= 1;
		}
		else if(payment < 0) { 
			System.out.println("The machine can not accept negative money");
			audit.write("-Negative Amount of Money was Input\n");
		}
		else {
			audit.write("-Not Enough Money Input for Transaction: " + payment + "\n");
			System.out.println("Not enough money to complete transaction");
			audit.write("-$" + payment + " Dispensed\n");
			System.out.println("$" + payment + " dispensed");
		}
		
		
		
	}
	
	//Displays machine array in a table format, as if it was the actual vending machine
	public static void displayConsole(Item[][] machine) {
		String cell;
		char tmpConvert;
		int x = 0;
		int columns = machine[0].length;
		int rows = machine.length;
		while(x < rows) {
			System.out.print("_____________________________________");
			x++;
		}
		System.out.println("");
		for(int i = 0; i < columns; i++) {
			System.out.print("|  ");
			for(int j = 0; j < rows; j++) {
				tmpConvert = (char)(i + 65); 
				cell = String.valueOf(tmpConvert) + j;
				System.out.printf("%-1s", cell);
				System.out.printf("%35s", machine[j][i].name + "-$" + machine[j][i].price +  "  |  ");
			}
			System.out.println("");
			x=0;
			while(x < rows) {
				System.out.print("_____________________________________");
				x++;
			}
			System.out.println("");
		}
	}
	
	//Rounds float to 2 digits for change, does not keep trailing zeroes (1.50 is displayed as 1.5 etc)
	public static float round(float number) {
	    int pow = 100;
	    float tmp = number * pow;
	    return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
	}
}
