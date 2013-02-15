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
  	 	
  	 	ArrayList<String> searchResults=getSearchResults(text);
  	 	
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
	
	public ArrayList<String> getSearchResults(String input) {

		//initialize names
		/* NOTE: this will eventually be done by fetching all the 
		 * professor names upon application initialization
		 */
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
						matches.add(names[i]);
					}
				}
				else {
					break;
				}
			}
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
