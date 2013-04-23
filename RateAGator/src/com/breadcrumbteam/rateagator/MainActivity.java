package com.breadcrumbteam.rateagator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static ArrayList<String> searchResults;
	private static final String usernameLocation = "usernameFile";
	private static final String doNotDisplayLocation = "doNotDisplayFile";
	private static String username = null;
	private static boolean displayUsernameMenu = true;
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String s) {
		username = s;
	}
	private Thread t1 = null;
	private Thread t2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		/**
		 * Keeps screen in portrait mode
		 */
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		username = readFileContents(usernameLocation);
		String temp = readFileContents(doNotDisplayLocation);
		if(temp == null) {
			displayUsernameMenu = true;
		}
		else {
			displayUsernameMenu = Boolean.valueOf(temp);
		}
		t1 = new Thread(new DBConnector.GetAllProfessorNames());
		t2 = new Thread(new DBConnector.GetAllCourseCodes());
		t1.start();
		t2.start();

		setContentView(R.layout.activity_main);

		//setup help text
		setupBottomButtonHelpListeners(this);
		setHelp(this, R.id.searchButton, "Search for a Professor or Course.");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.reset:
	            resetUsername();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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

	public void search(final View view) {
		final Activity parentActivity = this;
		final String text = ((EditText) this.findViewById(R.id.searchBar)).getText().toString().trim();
		if (text.equals("")) {
			Toast.makeText(getBaseContext(), "Search value was null, try again", Toast.LENGTH_SHORT).show();
			return;
		}
		if(username == null && displayUsernameMenu) {
			LayoutInflater factory = LayoutInflater.from(this);
			final View alertTextAreas = factory.inflate(R.layout.alert_text_areas_checkbox, null);
			AlertDialog.Builder signInAlert = new AlertDialog.Builder(this);
			signInAlert.setTitle("Sign in with GatorLink username/password");
			signInAlert.setView(alertTextAreas);
			signInAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText usernameView = (EditText) alertTextAreas.findViewById(R.id.username);
					EditText passwordView = (EditText) alertTextAreas.findViewById(R.id.password);
					CheckBox checkBox = (CheckBox) alertTextAreas.findViewById(R.id.checkBox1);
					String username = usernameView.getText().toString().trim();
					String password = passwordView.getText().toString().trim();
					boolean isValid = DBConnector.isUFStudent(username, password);
					if(isValid) {
						MainActivity.username = username;
						writeFileContents(usernameLocation, username);
					}
					else {
						if(!username.equals("")) {
							Toast.makeText(getBaseContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
						}
					}
					displayUsernameMenu = !checkBox.isChecked();
					writeFileContents(doNotDisplayLocation, String.valueOf(displayUsernameMenu));
					
					joinThreads(view, text, parentActivity);
				}
			});
			signInAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					CheckBox checkBox = (CheckBox) alertTextAreas.findViewById(R.id.checkBox1);
					displayUsernameMenu = !checkBox.isChecked();
					writeFileContents(doNotDisplayLocation, String.valueOf(displayUsernameMenu));
					joinThreads(view, text, parentActivity);
				}
			});
			signInAlert.show();
		}
		else {
			joinThreads(view, text, parentActivity);
		}
	}
	/**
	 * This are methods for bottom bar
	 * 
	 */
	//Basically useless while still in main activity, testing for now
	public static void goToLink(View v) {
		int vId = v.getId();
		Context c = v.getContext();
		if (vId == R.id.goCourses){
			Uri uri = Uri.parse("http://www.registrar.ufl.edu/soc/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			c.startActivity(intent);
		}
		else if (vId == R.id.goHelp) {
			/**
			 * This will crash on any other page then main activity at the moment
			 * This needs to be corrected it is here for testing for the time being
			 */
			Intent intent = new Intent(c, HelpPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			c.startActivity(intent);
		}
		else if (vId == R.id.goHome) {
			Intent intent = new Intent(c, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
			c.startActivity(intent);
		}
		else if (vId == R.id.goIsis) {
			Uri uri = Uri.parse("https://www.isis.ufl.edu/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			c.startActivity(intent);
		}
	}

	public void joinThreads(View view, String text, Activity parentActivity) {
		setProgressBarIndeterminateVisibility(true);
		try {
			t1.interrupt();
			t2.interrupt();
			DBConnector.interrupted = true;
			t1.join();
			t2.join();
			DBConnector.interrupted = false;
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		if(DBConnector.allCourseCodes.size() > 0 && DBConnector.allCourseCodes.size() > 0) {
			MainActivity.performSearch(view, text, parentActivity);
		}
		else {
			Toast.makeText(getBaseContext(), "Error Accessing Database", Toast.LENGTH_LONG).show();
			t1 = new Thread(new DBConnector.GetAllProfessorNames());
			t2 = new Thread(new DBConnector.GetAllCourseCodes());
			t1.start();
			t2.start();
		}
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
			if(text.charAt(i) == ' ') { // to fool the character checker
				continue;
			}
			if (!Character.isLetterOrDigit(text.charAt(i))) {
				new AlertDialog.Builder(parent)
				.setTitle("Oops")
				.setMessage(
						"Valid characters are A-Z, 0-9 and [space]. Revise your query and try again")
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
		intent.putExtra("username", username);
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

		boolean containsSpace = false;
		// Check for space (for fullName search)
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				containsSpace = true;
				break;
			}
		}

		if(containsSpace) {
			// Conduct fullname search
			getSearchResults(text.split(" ")[1], lastNames, false);
			Log.i("#tardif", searchResults.toString());
			String inputFirstName = text.split(" ")[0];
			ArrayList<String> trimmedResults = new ArrayList<String>();
			for(int i = 0; i < searchResults.size(); i++) {
				if(searchResults.get(i).split(", ")[1].equalsIgnoreCase(inputFirstName) ){
					trimmedResults.add(searchResults.get(i));
				}
			}
			searchResults = trimmedResults;
			return;
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
			int maxSearchChar = Math.min(input.length(), names.get(current)[0].length());
			for (int i = 0; i < maxSearchChar; i++) {
				if (Character.toLowerCase(names.get(current)[0].charAt(i)) == (Character
						.toLowerCase(input.charAt(i)))) {
					if (i == maxSearchChar - 1) {
						// iterate up until there is no longer a complete match
						// a good test case for this would to see which "Robert"
						// shows up first
						while (keepGoingUp) {
							for (int j = 0; j < maxSearchChar; j++) {
								// if its at the first index, avoids
								// arrayOutOfBoundsException
								if (current == 0) {
									keepGoingUp = false;
									break; // should fix 'a' problem
								}
								try {
									if (Character.toLowerCase(names.get(current - 1)[0].charAt(j)) == (Character.toLowerCase(input.charAt(j)))) {
										if (j == maxSearchChar - 1) {
											current = current - 1;
											break;
										}
									} else {
										keepGoingUp = false;
									}
								} catch (Exception e) {
									keepGoingUp = false;
								}
							}
						}
						foundStart = true;
						break;
					}
				} else if (Character.toLowerCase(names.get(current)[0].charAt(i)) < Character.toLowerCase(input.charAt(i))) {
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
							//TODO: remove this next line
							Log.i("#tardif", originalList.get(Integer
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

	public void clearFileContents(String fileLocation) {
		writeFileContents(fileLocation, null);
	}
	public void writeFileContents(String fileLocation, String content) {
		if(content == null) content = "";
		File file = new File(getFilesDir(), fileLocation);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String readFileContents(String fileLocation) {
		File file = new File(getFilesDir(), fileLocation);

		String myData = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine = br.readLine()) != null) {
				myData = myData + strLine;
			}
			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		if(myData.equals("")) return null;

		return myData;
	}
	
	public void resetUsername() {
		username = null;
		displayUsernameMenu = true;
		writeFileContents(usernameLocation, null);
		writeFileContents(doNotDisplayLocation, null);		
	}

	
	public static void setupBottomButtonHelpListeners(final Activity activity){
		setHelp(activity,R.id.goHome,"Return to Main Menu");
		setHelp(activity,R.id.goCourses,"Go to UF Registrar");
		setHelp(activity,R.id.goIsis,"Go to ISIS");
		setHelp(activity,R.id.goHelp,"View Help Page");
	}
	
	/**Applies help text to a View that will
	 * appear when the View is long-pressed.
	 * 
	 * @param activity: the activity with the button
	 * @param id: the id of the View (found in R)
	 * @param helpText: the text to display for help*/
	public static void setHelp(final Activity activity, final int id, final String helpText){
		((View)activity.findViewById(id)).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), helpText, Toast.LENGTH_LONG).show();
				Vibrator vib = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(250);
				return true;
			}
		});
		((View)activity.findViewById(id)).setLongClickable(true);
	}
}
