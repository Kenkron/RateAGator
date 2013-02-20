package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class ProfessorPage extends Activity {

	/**labels for the intent fields*/
	public static final String 
			INTENT_FIRST_NAME = "first name",
			INTENT_LAST_NAME = "last name";

	Professor currentProfessor;

	/**the following variables store the professor name locally
	 * These variables are a workaround, and should be removed
	 * after dbconnector */
	String fname,lname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO make a layout for ProfessorPage
		setContentView(R.layout.professor_page);

		fname=getIntent().getStringExtra(INTENT_FIRST_NAME);
		lname=getIntent().getStringExtra(INTENT_LAST_NAME);
				
		try {
			currentProfessor = DBConnector.getProfessor(getIntent()
					.getStringExtra(INTENT_FIRST_NAME), getIntent()
					.getStringExtra(INTENT_LAST_NAME));
		} catch (InterruptedException E) {
			// I have no Idea how to handle an interrupted exception
		}

		//label the professor in the course list
		((TextView)this.findViewById(R.id.professorPageLabel)).setText(fname+" "+lname);
		
		ViewGroup resultsList = (ViewGroup) findViewById(R.id.professorEvalList);
		
		for (Course c : currentProfessor.courseList) {
			Log.d("ProfessorPage", "CourseList: " + c.courseName);
			Button currentResult = new Button(this);

			boolean courseIsValid=true;

			if (c.courseName != null && c.courseName.length() > 0)
				currentResult.setText(c.courseName + " : " + c.courseNum);
			else if (c.courseNum != null && c.courseNum.length() > 0) 
				currentResult.setText(c.courseNum);
			else
				courseIsValid=false;

			if (courseIsValid) {
				currentResult.setLayoutParams(new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				currentResult.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String text = ((Button) v).getText().toString();
						String courseNum="";
						if (text.contains(" : ")){
							courseNum=text.substring(text.indexOf(" :")+3);
						}else{
							courseNum=text;
						}
						Log.d("SearchResults", "clicked on " + courseNum);
						
						goToEvaluationPage(courseNum);
					}
				});
				resultsList.addView(currentResult);
			}
		}
	}

	public void goToEvaluationPage(String courseNumber){
		Intent intent=new Intent(this,EvaluationPage.class);
		intent.putExtra(EvaluationPage.INTENT_COURSE_NUMBER, courseNumber);
		intent.putExtra(EvaluationPage.INTENT_PROFESSOR_FIRST_NAME, fname);
		intent.putExtra(EvaluationPage.INTENT_PROFESSOR_LAST_NAME, lname);
		this.startActivity(intent);
	}

}
