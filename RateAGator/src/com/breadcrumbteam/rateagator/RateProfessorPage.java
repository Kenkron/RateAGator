package com.breadcrumbteam.rateagator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RateProfessorPage extends Activity {

	public static final String INTENT_PROFESSOR_FIRST_NAME = "first name";
	public static final String INTENT_PROFESSOR_LAST_NAME = "last name";
	public static final String INTENT_COURSE_NUMBER = "course number";

	/** displays the rating currently on display */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_professor_page);

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
		
		for(int i = 0;i<Rating.FIELD_NAMES.length;i++) {
			ViewGroup container = (ViewGroup) (findViewById(R.id.rateProfessorFieldList));
			LinearLayout fullRate = new LinearLayout(this);
			fullRate.setOrientation(LinearLayout.HORIZONTAL);
			TextView newRatingLabel = new TextView(this);
			newRatingLabel.setText(Rating.FIELD_NAMES[i]);
			
			RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyle);
			ratingBar.setNumStars(5);
			ratingBar.setRating(3);
			ratingBar.setStepSize(1);
			
			fullRate.addView(newRatingLabel);
			fullRate.addView(ratingBar);
			container.addView(fullRate);
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