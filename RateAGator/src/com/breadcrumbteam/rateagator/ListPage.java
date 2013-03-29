package com.breadcrumbteam.rateagator;

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

/**
 * a list of professor+course ratings based on a selected search result
 */
public class ListPage extends Activity {

	/** labels for the intent fields */
	public static final String INTENT_COURSE_SET = "courseSet";

	/** the currently handled course set */
	CourseSet currentCourseSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO make a layout for ProfessorPage

		setContentView(R.layout.professor_page);
		currentCourseSet = (CourseSet) getIntent().getSerializableExtra(
				INTENT_COURSE_SET);

		((TextView) this.findViewById(R.id.professorPageLabel))
				.setText(currentCourseSet.setName);

		ViewGroup resultsList = (ViewGroup) findViewById(R.id.professorEvalList);
		
		for (Course c : currentCourseSet.courseList) {
			Log.d("ListPage", "CourseList: " + c.courseName);
			Button currentResult = new Button(this);

			switch (currentCourseSet.type) {
			case ProfessorSet:
				// if it has a course name, it definitely has a course number
				if (c.courseName != null && c.courseName.length() > 0) {
					currentResult.setText(c.courseName + " : " + c.courseNum);
				}
				// if it has a course number it is valid
				else if (c.courseNum != null && c.courseNum.length() > 0) {
					currentResult.setText(c.courseNum);
				}
				break;
			case CourseSet:
				currentResult.setText(c.professorLastName+", "+c.professorFirstName);
			}
			setButton(currentResult, c);
			resultsList.addView(currentResult);
		}
	}

	/**
	 * initializes a button based on the given button object and a number
	 * indicating the this button's course position in the courseList array
	 */
	public void setButton(Button b, final Course target) {
		b.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToEvaluationPage(target);
			}
		});
	}

	public void goToEvaluationPage(Course targetCourse) {
		Intent intent = new Intent(this, EvaluationPage.class);
		intent.putExtra(EvaluationPage.INTENT_COURSE, targetCourse);
		this.startActivity(intent);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}
