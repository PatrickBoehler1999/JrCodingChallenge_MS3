# JrCodingChallenge_MS3
Vending Machine Application made for coding challenge.

# How to Run

  In order to run the program it must be imported into an IDE, I used eclipse 2022 however visual studio code will also work (others should as well however these were the only ones I checked). If json.jar is set up on the PC then it may be possible to run the program from the console, however when I tried this I received import errors for org.json.
  
  -The program will ask for an initial import file, if the file is in the same directory as the java program it may be entered as "input.json", if it is not the path must also be given.
  
  -Once imported the console will display a table showing the vending machine and give options for what can be done from this point.
  
  -If audit.txt already exists the new run of the program is appended to the end of the current log. If the file does not exist it is created.
  
  -Inputting a "4" in the menu will terminate the program and write to the audit.txt file. (If program is not terminated this way the audit.txt will not update)

# Description of Approach

  The first step I took in solving the coding challenge was importing the json file in a way that could be easily used. This was also the biggest challenge for me, since although I have previously imported text files I have never used a JSON as input. I began by researching the best way to read the specific formatting being used in the JSON file and decided on using org.json since it had a good amount of documentation and is in the Maven repository. I added in a org.json dependency to the pom.xml file in order to use these functions. I then went through step by step completing methods that each accomplish a single function of the vending machine. Below is a description of what each method does.
  
  stockMachine was the first method I wrote since it was necessary to import the file before doing anything else. This function works by creating a fileReader of the input file, then separating it into a JSONArray of all the items, and a JSONObject of the dimensions. Each JSONObject in the items array is then converted into a custom class made for this program which I titled "Item". The Item class has three properties: name (String), amount (Integer), and price (Float). The class contains functions to grab these details however they ended up not being used. In order to store all this data the stockMachine method uses nested for loops to create a 2D array with dimensions grabbed from the JSON file. Since the items being added from the input may not match the number of slots in the vending machine the nested loops fill any extra spaces with an "empty" Item. Once the 2D array is filled the method prints to the console and audit file that the machine was successfully stocked and returns the user to the menu method.
  
  The menu method is where the recursion of the program takes place. The user is prompted to input a number 1-4 and the method will call another method based on what this input is. 1 will allow the user to make a purchase, 2 will allow the user to restock the machine with another input file, 3 displays the machine layout, and 4 exits the menu and terminates the program. Every selection made is also written out to the audit file. Most methods will return the user to the menu upon completion.
  
  takeOrder is a method written to grab a user input and convert it to a position within the 2D array storing Items. It is a simple method that will convert the first character given to it's ASCII value then to the corresponding integer. The second value is already an integer so it is just taken normally. An example conversion would be A2 -> 02. If a valid selection is given in the takeOrder method, it then gives the item from that position in the 2D array to the makePurchase method.
  
  makePurchase is where the program will do the necessary math between the cost of the item and the payment given. The method will start by displaying all info attached to the item selected; which includes name, price, and amount. It then prompts the user to input their payment in USD value, the program expects it to come in the format of 'x.xx', or just 'x' if no change. makePurchase then checks if the payment is greater than cost and calculates change to return if applicable. If the purchase is valid and completed it outputs that the item was dispensed and also if any change was given in return.
  
  displayConsole is a method used in order to display the 2D array in an easy to read tabular format. Within the table the position, item name, and item price are displayed. The method adjusts the formatting of the table based on the dimensions of the array, so a 4x8 sized array will look different than a 6x6 sized array.
  
  round is a method used to make a float only display two decimal places, this is for the returned change to match USD format.
  
  # Known Issues
  
  -If the machine is restocked then an Index out of bounds error appears when terminating the program.
  
  -org.json will not correctly import if program is run from the console
