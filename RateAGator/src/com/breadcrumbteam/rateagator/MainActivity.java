package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DBConnector.setBaseContext(this.getBaseContext());
		DBConnector.initializeAllProfessors();
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**this method is called when the exit button is pressed
	 * What causes this method to be called?
	 * 		In the file res/layout/activity_main.xml (Which
	 * 		is the main UI layout file), I gave the exit
	 * 		button the following property:
	 * 		android:onClick="endProgram";*/
	public void endProgram(View v){
		finish();
		//I should mention that this may not reset the UI.
		//Android apps are somewhat designed to act like they
		//never stop running.
	}	

	//This method is called when the search button is pressed
	public void search(View view){  	
		String text=((EditText)findViewById(R.id.searchBar)).getText().toString();	  	
  	 	Log.d("MainActivity", "Searching: "+text);
        ArrayList<String> searchResults = new ArrayList<String>;
  	 	
        ArrayList<String> lastNames = DBConnector.allProfessorNames;
        //Eliminate last names from DBConnector.allProfessorNames

        ArrayList<String> firstNames = DBConnector.allProfessorNames;
        for(int i = 0; i < lastNames.size(); i++) {
            //splits the lastname, firstname pair by ", " and takes the second section
            firstNames.add(lastnames.get(i).split(", ")[1]); 
        }
        
        //Spits out all firstnames TODO delete this later
        for(int i = 0; i < firstNames.size(); i++) {
            Log.d("MainActivity", firstNames.get(i));
        }
        

        //Search last names
  	 	int[] lSearchResults=getSearchResults(text, lastNames);

        //Search first names
        int[] fSearchResults=getSearchResults(text, firstNames);

        //Combine results for first and last names
        //Perhaps for getSearchResults just return ints, combine them into one binary array
        //and then iterate through and print out results as find 1s in array
        for(int i = 0; i < DBConnector.size(); i++) {
            if( (lSearchResults[i] == 1) || (fSearchResults[i] == 1) ) {
                searchResults.add(DBConnector.allProfessorNames.get(i)); 
            }
        }
  	 	
  	 	//Prints out the searchresults to LogCat
  	 	for (String result:searchResults){
  	 		Log.d("MainActivity",result);
  	 	}
  	 	
  	 	//switches to search results activity
  	 	Intent intent = new Intent(this, SearchResults.class);
  	 	intent.putStringArrayListExtra("names", searchResults);
  	 	intent.putExtra("query", text);
  	 	this.startActivity(intent);
	}
	
	public int[] getSearchResults(String input, names) {

		int[] matches = new int[names.size()];

		//performs a binary search to find a match with the input characteristics
		//NOTE: breaks on searches of 'a' and 'z'
		//NOTE: does not search by first name yet
		int start = 0;
		int end = names.size();
		int current = 0;
		boolean foundStart = false;
		boolean keepGoingUp = true;
		
		while(!foundStart) {
			if(end < start) {
				break;
			}
			current = (start + end) / 2;
			for(int i = 0; i < input.length(); i++) {
				if(Character.toLowerCase(names.get(current).charAt(i)) == (Character.toLowerCase(input.charAt(i))) ) {
					if (i == input.length() -1) {
						//iterate up until there is no longer a complete match
						//a good test case for this would to see which "Robert" shows up first
						while(keepGoingUp) {
							for(int j = 0; j < input.length(); j++) {
								if(Character.toLowerCase(names.get(current-1).charAt(j)) == (Character.toLowerCase(input.charAt(j))) ) {
									if(j == input.length() - 1) {
										current = current - 1;
										break;
									}
								}
								else {
									keepGoingUp = false;
								}
							}
						}
						foundStart = true;
						break;
					}
				}
				else if (Character.toLowerCase(names.get(current).charAt(i)) < Character.toLowerCase(input.charAt(i))) {
					start = current + 1;
					break;
				}
				else {
					end = current - 1;
					break;
				}
			}
		}
		
		if(!foundStart) {
			//a match was never found
		}
		else {
			boolean inputMatches = true;
			while(inputMatches) {
				for(int i = 0; i < input.length(); i++) {
					if(Character.toLowerCase(names.get(current).charAt(i)) == (Character.toLowerCase(input.charAt(i))) ) {
						if(i == input.length() - 1) {
							matches[current] = 1;
							current = current + 1;
							break;
						}
					}
					else {
						inputMatches = false;
					}
				}
			}
			//add all names until input does not match
		}
		
		
		return matches;
	}
	
	public void startDBConnection(View view) {
		String a = "";
		EditText editText = (EditText) findViewById(R.id.searchBar);
		String person = editText.getText().toString();

		//the following code was committed to the repository broken
		/*try {
			a = DBConnector.getPerson(person);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		TextView textView1 = (TextView) findViewById(R.id.searchButton);
        textView1.setText(String.valueOf(a));
	}

}
