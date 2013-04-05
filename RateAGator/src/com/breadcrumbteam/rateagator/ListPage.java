package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import com.breadcrumbteam.rateagator.CourseSet.SetType;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * a list of professor+course ratings based on a selected search result
 */
public class ListPage extends Activity {

	/** labels for the intent fields */
	public static final String INTENT_COURSE_SET = "courseSet";

	/** labels for the intent fields */
	public static final String INTENT_USERNAME = "username";

	/** the currently handled course set */
	CourseSet currentCourseSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO make a layout for ProfessorPage

		setContentView(R.layout.professor_page);
		currentCourseSet = (CourseSet) getIntent().getSerializableExtra(
				INTENT_COURSE_SET);
		
	    /**
	     * Keeps screen in portrait mode
	     */
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		((TextView) this.findViewById(R.id.professorPageLabel))
				.setText(currentCourseSet.setName);

		ViewGroup resultsList = (ViewGroup) findViewById(R.id.professorEvalList);

		if (currentCourseSet.courseList.size() > 1) {
			final Course averageCourse;
			final String username = this.getIntent().getStringExtra(
					INTENT_USERNAME);
			if (currentCourseSet.type == SetType.ProfessorSet) {
				averageCourse = new Course("", "Average");
				averageCourse.setCourseProfessor(
						currentCourseSet.courseList.get(0).professorFirstName,
						currentCourseSet.courseList.get(0).professorLastName);
			} else {
				averageCourse = new Course(
						currentCourseSet.courseList.get(0).courseName,
						currentCourseSet.courseList.get(0).courseNum);
				averageCourse.setCourseProfessor("", "Average");
			}
			Button averageResult = new Button(this);
			setButton(averageResult, averageCourse);
			averageResult.setText("Average");
			averageResult.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("ListPage", "average clicked");
					Intent intent = new Intent(v.getContext(),
							EvaluationPage.class);
					intent.putExtra(EvaluationPage.INTENT_COURSE, averageCourse);
					intent.putExtra(INTENT_USERNAME, username);
					ArrayList<Evaluation> allEvals = new ArrayList<Evaluation>();
					ArrayList<Rating> allRatings = new ArrayList<Rating>();
					for (Course c : currentCourseSet.courseList) {
						allEvals.add(DBConnector.getEvaluations(
								c.professorFirstName, c.professorLastName,
								c.courseNum));
						if (DBConnector.hasErrorOccurred()) {
							Toast.makeText(
									getBaseContext(),
									"Error Accessing Evaluations For "
											+ c.professorFirstName
											+ " teaching " + c.courseNum,
									Toast.LENGTH_SHORT).show();
						}
						allRatings.add(DBConnector.getRating(
								c.professorFirstName, c.professorLastName,
								c.courseNum));
						if (DBConnector.hasErrorOccurred()) {
							Toast.makeText(
									getBaseContext(),
									"Error Accessing Ratings For "
											+ c.professorFirstName
											+ " teaching " + c.courseNum,
									Toast.LENGTH_SHORT).show();
						}
					}
					if (allEvals.size() > 0 && allRatings.size() > 0) {
						intent.putExtra(EvaluationPage.INTENT_EVALUATION,
								Evaluation.mergeEvaluations(allEvals));
						intent.putExtra(EvaluationPage.INTENT_RATING,
								Rating.merge(allRatings));
						v.getContext().startActivity(intent);
					}else{
						Toast.makeText(
								getBaseContext(),
								"Error Accessing Averages For "
										+ currentCourseSet.setName,
								Toast.LENGTH_LONG).show();
					}
				}
			});
			resultsList.addView(averageResult);
		}

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
				currentResult.setText(c.professorLastName + ", "
						+ c.professorFirstName);
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
		intent.putExtra(INTENT_USERNAME,
				this.getIntent().getStringExtra(INTENT_USERNAME));
		intent.putExtra(EvaluationPage.INTENT_EVALUATION, DBConnector
				.getEvaluations(targetCourse.professorFirstName,
						targetCourse.professorLastName, targetCourse.courseNum));
		if (DBConnector.hasErrorOccurred()) {
			Toast.makeText(getBaseContext(), "Error Accessing Evaluations",
					Toast.LENGTH_LONG).show();
			return;
		}
		intent.putExtra(EvaluationPage.INTENT_RATING, DBConnector.getRating(
				targetCourse.professorFirstName,
				targetCourse.professorLastName, targetCourse.courseNum));
		if (DBConnector.hasErrorOccurred()) {
			Toast.makeText(getBaseContext(), "Error Accessing Ratings",
					Toast.LENGTH_LONG).show();
			return;
		}
		this.startActivity(intent);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}
