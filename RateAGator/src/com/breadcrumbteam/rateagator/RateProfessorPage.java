package com.breadcrumbteam.rateagator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RateProfessorPage extends Activity {

	public static final String INTENT_PROFESSOR_FIRST_NAME = "first name";
	public static final String INTENT_PROFESSOR_LAST_NAME = "last name";
	public static final String INTENT_COURSE_NUMBER = "course number";
	public static final String INTENT_USERNAME = "username";
	private String fName = null;
	private String lName = null;
	private String cCode = null;
	private String username = null;
	private RatingBar[] ratingBar = null;

	/** displays the rating currently on display */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_professor_page);

		fName = getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME);
		lName = getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME);
		cCode = getIntent().getStringExtra(INTENT_COURSE_NUMBER);
		username = this.getIntent().getStringExtra(INTENT_USERNAME);
		
		for(int i = 1; i < 10;i++) {
			// Get generated id for ratingBar
			int barId = getResources().getIdentifier("ratingBar" + i, "id", getPackageName() );
			// Get generated id for textView
			int textId = getResources().getIdentifier("ratingText" + i, "id", getPackageName() );
			
			TextView newRatingLabel = (TextView) findViewById(textId);
			newRatingLabel.setText(Rating.FIELD_NAMES[i - 1] + " : ");
			
			
			RatingBar newRatingBar = (RatingBar) findViewById(barId);
			newRatingBar.setNumStars(5);
			newRatingBar.setRating(3);
			newRatingBar.setStepSize(1);
		}
		
		Button submit = (Button) findViewById(R.id.submitRating);
		submit.setText("Submit Rating");
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Rating r = new Rating(1);
				for(int i = 1; i < 10; i++) {
					// Get generated id for ratingBar
					int barId = getResources().getIdentifier("ratingBar" + i, "id", getPackageName() );
					RatingBar newRatingBar = (RatingBar) findViewById(barId);
					r.addResponseValue(newRatingBar.getRating());
				}
				DBConnector.addRatings(fName,lName,cCode, r);
				if(DBConnector.hasErrorOccurred()) {
					Toast.makeText(getBaseContext(), "DBConnection problem", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(), "Rating added", Toast.LENGTH_SHORT).show();
					Course targetCourse = DBConnector.getCourseSetByCode(cCode).courseList.get(0);
					Intent intent = new Intent(getBaseContext(), EvaluationPage.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(EvaluationPage.INTENT_COURSE, targetCourse);
					intent.putExtra(INTENT_USERNAME, username);
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
					getBaseContext().startActivity(intent);
				}
			}
		});
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}