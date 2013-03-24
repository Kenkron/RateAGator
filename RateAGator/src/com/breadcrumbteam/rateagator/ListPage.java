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
import android.widget.TextView;

public class ListPage extends Activity {

	/**labels for the intent fields*/
	public static final String 
			INTENT_FIRST_NAME = "first name",
			INTENT_LAST_NAME = "last name",
			INTENT_COURSE_CODE = "course code";

	Professor currentProfessor;
	ArrayList<Professor> professorList;

	/**the following variables store the professor name locally
	 * These variables are a workaround, and should be removed
	 * after dbconnector */
	private static String fname;
	private static String lname;
	private static String courseCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO make a layout for ProfessorPage
		
		setContentView(R.layout.professor_page);
		Bundle extras = getIntent().getExtras();
		boolean isCourse = false;
		if(extras.size() > 1) { //it's a professor list
			fname=getIntent().getStringExtra(INTENT_FIRST_NAME);
			lname=getIntent().getStringExtra(INTENT_LAST_NAME);
			

			currentProfessor = DBConnector.getProfessor(fname, lname);
			//TODO: check DBConnector.hasErrorOccurred()

			//Set the header to professor name
			((TextView)this.findViewById(R.id.professorPageLabel)).setText("Professor: "+fname+" "+lname);
		}
		else { //it's a course list
			isCourse = true;
			courseCode = extras.getString(INTENT_COURSE_CODE);

			professorList = DBConnector.getCourse(courseCode);
			//TODO: check DBConnector.hasErrorOccurred()

			//Set the header to course name
			((TextView)this.findViewById(R.id.professorPageLabel)).setText(courseCode);
		}

		ViewGroup resultsList = (ViewGroup) findViewById(R.id.professorEvalList);
		if (isCourse) {
			for(Professor p : professorList) {
				Log.d("ProfessorPage", "ProfessorList: " + p.fName + " " + p.lName);
				Button currentResult = new Button(this);
				currentResult.setText(p.lName + ", " + p.fName);
				setButton(currentResult);
				resultsList.addView(currentResult);
			}
			
		}
		else {
			for (Course c : currentProfessor.courseList) {
				Log.d("ProfessorPage", "CourseList: " + c.courseName);
				Button currentResult = new Button(this);
	
				//if it has a course name, it definitely has a course number
				if (c.courseName != null && c.courseName.length() > 0) {
					currentResult.setText(c.courseName + " : " + c.courseNum);
				}
				//if it has a course number it is valid
				else if (c.courseNum != null && c.courseNum.length() > 0) {
					currentResult.setText(c.courseNum);
				}
				setButton(currentResult);
				resultsList.addView(currentResult);
			}
		}
	}
	
	public void setButton(Button b) {
		b.setLayoutParams(new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = ((Button) v).getText().toString();
				if(text.contains(", ")) { //it is a professor
					lname = text.split(", ")[0];
					fname = text.split(", ")[1];
					Log.d("SearchResults", "clicked on " + text);
				}
				else { //is a course
					courseCode = text;
					if (courseCode.contains(" : ")){
						courseCode=courseCode.split(" : ")[1];
					}
					Log.d("SearchResults", "clicked on " + courseCode);
				}
				goToEvaluationPage();
			}
		});
	}

	public void goToEvaluationPage(){
		Intent intent=new Intent(this,EvaluationPage.class);
		intent.putExtra(EvaluationPage.INTENT_COURSE_NUMBER, courseCode);
		intent.putExtra(EvaluationPage.INTENT_PROFESSOR_FIRST_NAME, fname);
		intent.putExtra(EvaluationPage.INTENT_PROFESSOR_LAST_NAME, lname);
		this.startActivity(intent);
	}
	
	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}
