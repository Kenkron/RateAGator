package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class SearchResults extends Activity {

	/**
	 * Identifies the name of the search query string in the intent
	 */
	public static final String INTENT_QUERY = "query";

	/**
	 * Identifies the name of the list of search results in the intent
	 */
	public static final String INTENT_RESULTS = "names";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresults);
		
		//Load in search results
		Bundle b = this.getIntent().getExtras();
		ArrayList<String> searchResults = b.getStringArrayList(INTENT_RESULTS);
		
		EditText query=(EditText) findViewById(R.id.searchBar);
		query.setText(this.getIntent().getStringExtra(INTENT_QUERY));
		
		ViewGroup resultsList=(ViewGroup) findViewById(R.id.searchResultsList);
		for (String name:searchResults){
			Log.d("result list", name);
			Button currentResult=new Button(this);
			currentResult.setText(name);
			currentResult.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			currentResult.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String text=((Button)v).getText().toString();
					if(text.contains(", ")) {
						Log.d("SearchResults","clicked on "+text);
						
						String[] nameArray = text.split(", ");
						
						//nameArray[1] is last name, [0] is first name
						goToProfessor(nameArray[1], nameArray[0]);
					}
					else {
						goToCourse(text);
					}
					
				}
			});
			resultsList.addView(currentResult);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		((EditText) this.findViewById(R.id.searchBar)).clearFocus();
	}

	public void search(View view) {
		String text = ((EditText) this.findViewById(R.id.searchBar)).getText()
				.toString();
		MainActivity.performSearch(view, text, this);
	}

	public void goToProfessor(String firstName, String lastName) {
		Intent intent = new Intent(this, ListPage.class);
		intent.putExtra(ListPage.INTENT_FIRST_NAME, firstName);
		intent.putExtra(ListPage.INTENT_LAST_NAME, lastName);
		this.startActivity(intent);
	}
	
	public void goToCourse(String courseCode) {
		Intent intent = new Intent(this, ListPage.class);
		intent.putExtra(ListPage.INTENT_COURSE_CODE, courseCode);
		this.startActivity(intent);
	}
	/**
	 * This are methods for bottom bar
	 * 
	 */
	public void home(View v) {
		Intent intent = new Intent(SearchResults.this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
	    startActivity(intent);	
	}
	
	public void isis(View v) {
		Uri uri = Uri.parse("https://www.isis.ufl.edu/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void registrar(View v) {
		Uri uri = Uri.parse("http://www.registrar.ufl.edu/soc/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	public void help(View v) {
		Uri uri = Uri.parse("http://gizmodo.com/5909262/how-to-use-android");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	/**
	 * ^
	 */

}
