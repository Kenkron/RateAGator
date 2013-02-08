package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	/**this method is called when the search button is pressed
	 * What causes this method to be called?
	 * 		In the file res/layout/activity_main.xml (Which
	 * 		is the main UI layout file), I gave the search
	 * 		button the following property:
	 * 		android:onClick="search";*/
	public void search(View view){  	
		String text=((EditText)findViewById(R.id.searchBar)).getText().toString();	  	
  	 	Log.println(Log.INFO, "Update", "Searching: "+text);
  	 	String[] searchResults=getSearchResults(text);
  	 	for (String result:searchResults){
  	 		System.out.println(result);
  	 	}
	}
	
	public String[] getSearchResults(String input) {

		//initialize names
		//--- Can be done by prepopulating an array from the database
		//--- when the application is started
		String[] names = {
		"Aaron", "Abbey", "Abbie", "Abby", "Abigail",
		"Ada", "Adah", "Adaline", "Adam", "Addie",
		"Adela", "Adelaida", "Adelaide", "Adele", "Adelia",
		"Adelia", "Adelina", "Adeline", "Adell", "Adella",
		"Adelle", "Adena", "Adina", "Adria", "Adrian", 
		"Adriana", "Adriane", "Adrianna", "Adrianne", "Adrien" };

		ArrayList<String> matches = new ArrayList<String>();

		//check if input string is subset of any names

		//this loop iterates through all names
		for(int i = 0; i < names.length; i++) {

			//this loop checks if input is the beginning subset of the name
			for(int j = 0; j < input.length(); j++) {
				if (input.charAt(j) == names[i].charAt(j) ) {
						
					if(input.length() == (j + 1)) {
						//System.out.println(names[i]);	
						matches.add(names[i]);
					}
				}
				else {
					break;
				}

			}
		}
		String[] matchesArray = new String[matches.size()];
		matches.toArray(matchesArray);
		return matchesArray;
	}

}
