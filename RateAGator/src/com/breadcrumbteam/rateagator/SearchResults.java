package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
		
		
		if (searchResults.size()>0){
			TextView t = (TextView) findViewById(R.id.searchResult1);
			t.setText(searchResults.get(0));
		}
		if (searchResults.size()>1){
			TextView t = (TextView) findViewById(R.id.searchResult2);
			t.setText(searchResults.get(1));
		}
		if (searchResults.size()>2){
			TextView t = (TextView) findViewById(R.id.searchResult3);
			t.setText(searchResults.get(2));
		}
		
		//ViewGroup resultsList=(ViewGroup) findViewById(R.id.searchResultsList);
		/*for (String name:searchResults){
			Log.d("result list", name);
			Button currentResult=new Button(this);
			currentResult.setText(name);
			currentResult.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			resultsList.addView(currentResult);
		}*/
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
