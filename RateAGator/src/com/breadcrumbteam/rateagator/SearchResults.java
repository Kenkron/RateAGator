package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResults extends Activity {

	/**Identifies the name of the search query string
	 * in the intent*/
	public static final String INTENT_QUERY="query";
	
	/**Identifies the name of the list of search results
	 * in the intent*/
	public static final String INTENT_RESULTS="names";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresults);
		
		//Load in search results
		Bundle b = this.getIntent().getExtras();
		ArrayList<String> searchResults = b.getStringArrayList(INTENT_RESULTS);
		
		//TextView query=(TextView) findViewById(R.id.searchQueryLabel);
		//query.setText("Searched for: "+this.getIntent().getStringExtra(INTENT_QUERY));
		
		ViewGroup resultsList=(ViewGroup) findViewById(R.id.searchResultsList);
		for (String name:searchResults){
			Log.d("result list", name);
			Button currentResult=new Button(this);
			currentResult.setText(name);
			currentResult.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			currentResult.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("SearchResults","clicked on "+((Button)v).getText());
				}
			});
			resultsList.addView(currentResult);
		}
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
