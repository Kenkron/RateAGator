package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class ProfessorPage extends Activity {

	//the labels for the intent fields
	public static final String INTENT_FIRST_NAME="first name";
	public static final String INTENT_LAST_NAME="last name";
	
	Professor currentProfessor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//TODO make a layout for ProfessorPage
		setContentView(R.layout.professor_page);
		
		try{
		currentProfessor=DBConnector.getProfessor(
				getIntent().getStringExtra(INTENT_FIRST_NAME), 
				getIntent().getStringExtra(INTENT_LAST_NAME));
		}catch (InterruptedException E){
			//I have no Idea how to handle an interrupted exception
		}
		
		ViewGroup resultsList=(ViewGroup)findViewById(R.id.professorEvalList);
		
		for (Course c:currentProfessor.courseList){
			Log.d("ProfessorPage", "CourseList: "+c.courseName);
			Button currentResult=new Button(this);
			currentResult.setText(c.courseName);
			currentResult.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			currentResult.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String text=((Button)v).getText().toString();
					Log.d("SearchResults","clicked on "+text);
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
