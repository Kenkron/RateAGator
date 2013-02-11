package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SearchResults extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresults);
		
		//Load in search results
		Bundle b = this.getIntent().getExtras();
		ArrayList<String> searchResults = b.getStringArrayList("names");
		
		//Set textView value to search results
		/* Note: this only outputs the first search result, but I feel that working
		 * more on this will infringe on UI's territory, so it's up to you guys to 
		 * create a list of textviews and stuff and lay it out how you want
		 */
		
		TextView t = (TextView) findViewById(R.id.searchResult1);
		t.setText(searchResults.get(0));

	}
	
	//This method is called when button is pressed
	public void getCourseData(View view){  	
		
  	 	//get the information contained in the link you clicked on
		
		//getEvaluations
	
		//getRatings and getComments will be in the second sprint
  	 	
  	 	//switches to courseData activity
  	 	
		/*		This is just how I started the SearchResults activity from MainActivity
		 * 		There's probably a better name than CourseData for that class so going to
		 * 		get input from group
		 * 
		 * 		Intent intent = new Intent(this, SearchResults.class);  	 	
		 * 		this.startActivity(intent);
		 */
	}

}
