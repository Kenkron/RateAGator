//This search function works by the search parameters being inputted after
//calling the function
// --- Example: java Search Ad ---
public class Search {
	public static void main(String[] args) {
		
		//initialize names
		String[] names = {
		"Aaron", "Abbey", "Abbie", "Abby", "Abigail",
		"Ada", "Adah", "Adaline", "Adam", "Addie",
		"Adela", "Adelaida", "Adelaide", "Adele", "Adelia",
		"Adelia", "Adelina", "Adeline", "Adell", "Adella",
		"Adelle", "Adena", "Adina", "Adria", "Adrian", 
		"Adriana", "Adriane", "Adrianna", "Adrianne", "Adrien" };
		
		/*
			Assumption: Strings are already sorted alphabetically
		*/

		//check if input string is subset of any names
		
		String input = args[0];
		System.out.println("Input string is " + input);

		//this loop iterates through all names
		for(int i = 0; i < names.length; i++) {

			//this loop checks if input is the beginning subset of the name
			for(int j = 0; j < input.length(); j++) {
				if (input.charAt(j) == names[i].charAt(j) ) {
					if(input.length() == (j + 1)) System.out.println(names[i]);	
				}
				else {
					break;
				}

			}
		}
	}
}
