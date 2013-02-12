package com.breadcrumbteam.rateagator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ProfessorPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO make a layout for ProfessorPage
		setContentView(R.layout.activity_main);
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
