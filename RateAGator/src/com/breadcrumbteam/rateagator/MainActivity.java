package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static ArrayList<String> searchResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		DBConnector.initProfessorsAndCourses();
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

	/**
	 * this method is called when the exit button is pressed What causes this
	 * method to be called? In the file res/layout/activity_main.xml (Which is
	 * the main UI layout file), I gave the exit button the following property:
	 * android:onClick="endProgram";
	 */
	public void endProgram(View v) {
		finish();
		// I should mention that this may not reset the UI.
		// Android apps are somewhat designed to act like they
		// never stop running.
	}

	public void search(View view) {
		setProgressBarIndeterminateVisibility(true);
		String text = ((EditText) this.findViewById(R.id.searchBar)).getText()
				.toString().trim();
		MainActivity.performSearch(view, text, this);
	}
	/**
	 * This are methods for bottom bar
	 * 
	 */
	//Basically useless while still in main activity, testing for now
	public void home(View v) {
		/*
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
	    startActivity(intent);
	    */ 
		
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

	/** This method is called when the search button is pressed */
	public static void performSearch(View view, String text, Activity parent) {
		Log.d("MainActivity", "Searching: " + text);

		// ASSUMPTION: No professor name contains digits
		boolean containsDigit = false;
		// checks if search contains unacceptable characters
		for (int i = 0; i < text.length(); i++) {
			if (Character.isDigit(text.charAt(i))) {
				containsDigit = true;
			}
			if (!Character.isLetterOrDigit(text.charAt(i))) {
				new AlertDialog.Builder(parent)
						.setTitle("Oops")
						.setMessage(
								"Search value contains a non alphanumeric character. Revise your search and try again")
						.setNeutralButton("Close", null).show();
				return;
			}
		}

		searchResults = new ArrayList<String>();
		if (containsDigit) { // know for sure it's a course
			searchCourses(text);
		} else if (text.length() == 3) { // could be a professor or a course
											// prefix
			// get courses
			searchCourses(text);
			// get names
			searchProfessors(text);
		} else { // just professor names
			searchProfessors(text);
		}

		// switches to search results activity
		Intent intent = new Intent(parent, SearchResults.class);
		intent.putStringArrayListExtra("names", searchResults);
		intent.putExtra("query", text);
		parent.startActivity(intent);
	}

	private static void searchCourses(String text) {
		ArrayList<String[]> courses = new ArrayList<String[]>();
		for (int i = 0; i < DBConnector.allCourseCodes.size(); i++) {
			courses.add(new String[] { DBConnector.allCourseCodes.get(i),
					Integer.toString(i) });
		}
		getSearchResults(text, courses, true);
	}

	private static void searchProfessors(String text) {
		ArrayList<String[]> lastNames = new ArrayList<String[]>();
		ArrayList<String[]> firstNames = new ArrayList<String[]>();

		// fills in the first and last name arrayLists
		for (int i = 0; i < DBConnector.allProfessorNames.size(); i++) {
			lastNames.add(new String[] { DBConnector.allProfessorNames.get(i),
					Integer.toString(i) });
			// splits the lastName pair by ", " to make the firstName arrayList
			firstNames.add(new String[] { lastNames.get(i)[0].split(", ")[1],
					Integer.toString(i) });
		}

		// sorts the firstName (lastName is already sorted)
		firstNames = mergeSort(firstNames);

		// generates the searchResults and puts them in searchResults array
		getSearchResults(text, lastNames, false);
		getSearchResults(text, firstNames, false);
	}

	public static ArrayList<String[]> mergeSort(ArrayList<String[]> array) {
		if (array.size() <= 1) {
			return array;
		}
		int middle = array.size() / 2;
		ArrayList<String[]> left = new ArrayList<String[]>();
		ArrayList<String[]> right = new ArrayList<String[]>();
		ArrayList<String[]> returnList = new ArrayList<String[]>();

		// copy elements to left and right array
		for (int i = 0; i < middle; i++) {
			left.add(array.get(i));
		}
		for (int i = middle; i < array.size(); i++) {
			right.add(array.get(i));
		}

		left = mergeSort(left);
		right = mergeSort(right);
		returnList = merge(left, right);

		return returnList;
	}

	private static ArrayList<String[]> merge(ArrayList<String[]> left,
			ArrayList<String[]> right) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		int i = 0;
		int j = 0;
		while (i < left.size() && j < right.size()) {
			if (left.get(i)[0].compareToIgnoreCase(right.get(j)[0]) <= 0) {
				result.add(left.get(i));
				i++;
			} else {
				result.add(right.get(j));
				j++;
			}
		}
		while (i < left.size()) {
			result.add(left.get(i));
			i++;
		}
		while (j < right.size()) {
			result.add(right.get(j));
			j++;
		}
		return result;
	}

	// //////Static Methods////////

	/**
	 * Fills the given array lists ('names and 'SearchResults') based on the
	 */
	public static void getSearchResults(String input,
			ArrayList<String[]> names, boolean isCourse) {

		// performs a binary search to find a match with the input
		// characteristics

		int start = 0;
		int end = names.size();
		int current = 0;
		boolean foundStart = false;
		boolean keepGoingUp = true;
		ArrayList<String> originalList;
		originalList = isCourse ? DBConnector.allCourseCodes
				: DBConnector.allProfessorNames;

		while (!foundStart) {
			if (end < start) {
				break;
			}
			current = (start + end) / 2;
			for (int i = 0; i < input.length(); i++) {
				if (Character.toLowerCase(names.get(current)[0].charAt(i)) == (Character
						.toLowerCase(input.charAt(i)))) {
					if (i == input.length() - 1) {
						// iterate up until there is no longer a complete match
						// a good test case for this would to see which "Robert"
						// shows up first
						while (keepGoingUp) {
							for (int j = 0; j < input.length(); j++) {
								// if its at the first index, avoids
								// arrayOutOfBoundsException
								if (current == 0) {
									keepGoingUp = false;
									break; // should fix 'a' problem
								}
								if (Character.toLowerCase(names
										.get(current - 1)[0].charAt(j)) == (Character
										.toLowerCase(input.charAt(j)))) {
									if (j == input.length() - 1) {
										current = current - 1;
										break;
									}
								} else {
									keepGoingUp = false;
								}
							}
						}
						foundStart = true;
						break;
					}
				} else if (Character.toLowerCase(names.get(current)[0]
						.charAt(i)) < Character.toLowerCase(input.charAt(i))) {
					start = current + 1;
					break;
				} else {
					end = current - 1;
					break;
				}
			}
		}

		if (!foundStart) {
			// a match was never found
		} else {
			boolean inputMatches = true;
			while (inputMatches) {
				int maxCharacters = Math.min(input.length(),
						names.get(current)[0].length());
				for (int i = 0; i < maxCharacters; i++) {
					if (Character.toLowerCase(names.get(current)[0].charAt(i)) == (Character
							.toLowerCase(input.charAt(i)))) {
						if (i == maxCharacters - 1) {
							searchResults.add(originalList.get(Integer
									.parseInt(names.get(current)[1])));
							// if its at the last index, avoids
							// arrayOutOfBoundsException
							if (current == originalList.size() - 1) {
								inputMatches = false;
								break;
							}
							current = current + 1;
							break;
						}
					} else {
						inputMatches = false;
					}
				}
			}
		}
		return;
	}

}
