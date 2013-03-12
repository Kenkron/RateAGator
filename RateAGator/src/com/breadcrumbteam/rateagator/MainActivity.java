package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		DBConnector.setBaseContext(this.getBaseContext());
		DBConnector.initializeAllProfessors();
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setProgressBarIndeterminateVisibility(false);
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

	public void search(View view){
		setProgressBarIndeterminateVisibility(true);
		String text=((EditText)this.findViewById(R.id.searchBar)).getText().toString();
		MainActivity.performSearch(view, text, this);
	}
	
	/**This method is called when the search button is pressed*/
	public static void performSearch(View view, String text, Activity parent){ 	
  	 	Log.d("MainActivity", "Searching: "+text);
  	 	
  	 	//checks if search contains unacceptable characters
  	 	for(int i = 0; i < text.length(); i++) {
  	 		if(!Character.isLetterOrDigit(text.charAt(i))) {
  	 			new AlertDialog.Builder(parent).setTitle("Oops").setMessage(
  	 					"Search value contains a non alphanumeric character. Revise your search and try again"
  	 					).setNeutralButton("Close", null).show();
  	 			return;
  	 		}
  	 	}
  	 	
  	 	//initialize all the arrayLists
        ArrayList<String> searchResults = new ArrayList<String>();
        ArrayList<String[]> lastNames = new ArrayList<String[]>();
        ArrayList<String[]> firstNames = new ArrayList<String[]>();
  	 	
        //fills in the first and last name arrayLists
        for(int i = 0; i < DBConnector.allProfessorNames.size(); i++) {
        	lastNames.add(new String[]{DBConnector.allProfessorNames.get(i), Integer.toString(i)});
            //splits the lastName pair by ", " to make the firstName arrayList
        	firstNames.add(new String[]{lastNames.get(i)[0].split(", ")[1], Integer.toString(i)});
        }        

        //sorts the firstName (lastName is already sorted)
        firstNames = mergeSort(firstNames);
        
        //generates the searchResults and puts them in searchResults array
        getSearchResults(text, lastNames, searchResults);
        getSearchResults(text, firstNames, searchResults);        
  	 	
  	 	//switches to search results activity
  	 	Intent intent = new Intent(parent, SearchResults.class);
  	 	intent.putStringArrayListExtra("names", searchResults);
  	 	intent.putExtra("query", text);
  	 	parent.startActivity(intent);
	}
	
	public static ArrayList<String[]> mergeSort(ArrayList<String[]> array) {
		if(array.size() <= 1) {
			return array;
		}
		int middle = array.size() / 2;
		ArrayList<String[]> left = new ArrayList<String[]>();
		ArrayList<String[]> right = new ArrayList<String[]>();
		ArrayList<String[]> returnList = new ArrayList<String[]>();
		
		//copy elements to left and right array
		for(int i = 0; i < middle; i++) {
			left.add(array.get(i));
		}
		for(int i = middle; i < array.size(); i++) {
			right.add(array.get(i));
		}
		
		
		left = mergeSort(left);
		right = mergeSort(right);
		returnList = merge(left, right);
		
		return returnList;
	}
	
	private static ArrayList<String[]> merge(ArrayList<String[]> left, ArrayList<String[]> right) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		int i = 0;
		int j = 0;
		while(i < left.size() && j < right.size() ) {
			if(left.get(i)[0].compareToIgnoreCase(right.get(j)[0]) <= 0) {
				result.add(left.get(i));
				i++;
			}
			else {
				result.add(right.get(j));
				j++;
			}
		}
		while(i < left.size() ) {
			result.add(left.get(i));
			i++;
		}
		while(j < right.size() ) {
			result.add(right.get(j));
			j++;
		}
		return result;
	}
	
	
	////////Static Methods////////
	
	/**Fills the given array lists ('names and 'SearchResults')
	 * based on the */
	public static void getSearchResults(String input, ArrayList<String[]> names, ArrayList<String> searchResults) {

		//performs a binary search to find a match with the input characteristics
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
				if(Character.toLowerCase(names.get(current)[0].charAt(i)) == (Character.toLowerCase(input.charAt(i))) ) {
					if (i == input.length() -1) {
						//iterate up until there is no longer a complete match
						//a good test case for this would to see which "Robert" shows up first
						while(keepGoingUp) {
							for(int j = 0; j < input.length(); j++) {
								//if its at the first index, avoids arrayOutOfBoundsException
								if(current == 0) {
									keepGoingUp = false;
									break; // should fix 'a' problem
								}
								if(Character.toLowerCase(names.get(current-1)[0].charAt(j)) == (Character.toLowerCase(input.charAt(j))) ) {
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
				else if (Character.toLowerCase(names.get(current)[0].charAt(i)) < Character.toLowerCase(input.charAt(i))) {
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
				int maxCharacters = Math.min(input.length(), names.get(current)[0].length());
				for(int i = 0; i < maxCharacters; i++) {
					if(Character.toLowerCase(names.get(current)[0].charAt(i)) == (Character.toLowerCase(input.charAt(i))) ) {
						if(i == maxCharacters - 1) {
							searchResults.add(DBConnector.allProfessorNames.get(Integer.parseInt(names.get(current)[1])));
							//if its at the last index, avoids arrayOutOfBoundsException
							if(current == DBConnector.allProfessorNames.size() - 1) {
								inputMatches = false;
								break;
							}
							current = current + 1;
							break;
						}
					}
					else {
						inputMatches = false;
					}
				}
			}
		}
		return;
	}

}
