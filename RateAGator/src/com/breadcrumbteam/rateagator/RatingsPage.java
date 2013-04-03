package com.breadcrumbteam.rateagator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RatingsPage extends Activity {

	public static final String INTENT_PROFESSOR_FIRST_NAME = "first name";
	public static final String INTENT_PROFESSOR_LAST_NAME = "last name";
	public static final String INTENT_COURSE_NUMBER = "course number";

	/** displays the rating currently on display */
	Rating shownRating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.professor_course_rating);

		Log.d("RatingsPage",
				"name (first): "
						+ getIntent().getStringExtra(
								INTENT_PROFESSOR_FIRST_NAME));
		Log.d("RatingsPage",
				"name (last): "
						+ getIntent()
								.getStringExtra(INTENT_PROFESSOR_LAST_NAME));
		Log.d("RatingsPage",
				"course: " + getIntent().getStringExtra(INTENT_COURSE_NUMBER));
		shownRating = DBConnector.getRating(
				getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME),
				getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME),
				getIntent().getStringExtra(INTENT_COURSE_NUMBER));

		if (shownRating == null || DBConnector.hasErrorOccurred()) {
			Toast.makeText(getBaseContext(),
					"Error Accessing Ratings Database", Toast.LENGTH_LONG)
					.show();
			finish();
		} else {

			((TextView) findViewById(R.id.professorEvalLabel))
					.setText(getIntent().getStringExtra(
							INTENT_PROFESSOR_FIRST_NAME)
							+ " "
							+ getIntent().getStringExtra(
									INTENT_PROFESSOR_LAST_NAME));
			((TextView) findViewById(R.id.courseEvalLabel)).setText(getIntent()
					.getStringExtra(INTENT_COURSE_NUMBER));

			for (int i = 0; i < shownRating.getRatingResponses().length; i++) {
				double rating = shownRating.getRatingResponses()[i];
				rating = ((int) (rating*100))/100.0;

				ViewGroup container = (ViewGroup) (findViewById(R.id.ratingFieldList));

				LinearLayout fullRating = new LinearLayout(this);

				fullRating.setOrientation(LinearLayout.HORIZONTAL);

				TextView newRatingAmount = new TextView(this);
				newRatingAmount.setText("" + rating);
				newRatingAmount.setGravity(Gravity.RIGHT);

				TextView newRatingLabel = new TextView(this);
				newRatingLabel.setText(Evaluation.FIELD_NAMES[i] + ": ");

				fullRating.addView(newRatingLabel);
				fullRating.addView(newRatingAmount);

				if (i != 7 && i != 8)
					container.addView(fullRating);
			}
		}

	}

	public void goToComments(View view) {
		Intent intent = new Intent(this, CommentsPage.class);
		intent.putExtra("courseNum",
				getIntent().getStringExtra(INTENT_COURSE_NUMBER));
		intent.putExtra("fName",
				getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME));
		intent.putExtra("lName",
				getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME));
		this.startActivity(intent);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}