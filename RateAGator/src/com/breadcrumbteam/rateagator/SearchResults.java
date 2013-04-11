package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
	public static final String INTENT_USERNAME = "username";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresults);
	    /**
	     * Keeps screen in portrait mode
	     */
		
		//sets up help text
	    MainActivity.setupBottomButtonHelpListeners(this);
		MainActivity.setHelp(this, R.id.searchButton, "Search for a Professor or Course.");
		
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Load in search results
		Bundle b = this.getIntent().getExtras();
		ArrayList<String> searchResults = b.getStringArrayList(INTENT_RESULTS);

		EditText query = (EditText) findViewById(R.id.searchBar);
		query.setText(this.getIntent().getStringExtra(INTENT_QUERY));

		ViewGroup resultsList = (ViewGroup) findViewById(R.id.searchResultsList);
		for (String name : searchResults) {
			Log.d("result list", name);
			Button currentResult = new Button(this);
			currentResult.setText(name);
			currentResult.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			currentResult.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String text = ((Button) v).getText().toString();
					if (text.contains(", ")) {
						Log.d("SearchResults", "clicked on " + text);

						String[] nameArray = text.split(", ");

						// nameArray[1] is last name, [0] is first name
						goToProfessor(nameArray[1], nameArray[0]);
					} else {
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
		intent.putExtra(ListPage.INTENT_COURSE_SET, DBConnector.getProfessor(firstName, lastName));
		this.startActivity(intent);
	}

	public void goToCourse(String courseCode) {
		Intent intent = new Intent(this, ListPage.class);
		intent.putExtra(ListPage.INTENT_COURSE_SET, DBConnector.getCourseSetByCode(courseCode));
		intent.putExtra(INTENT_USERNAME, this.getIntent().getStringExtra(INTENT_USERNAME));
		this.startActivity(intent);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}
