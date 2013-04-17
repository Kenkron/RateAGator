package com.breadcrumbteam.rateagator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RateProfessorPage extends Activity {

	public static final String INTENT_PROFESSOR_FIRST_NAME = "first name";
	public static final String INTENT_PROFESSOR_LAST_NAME = "last name";
	public static final String INTENT_COURSE_NUMBER = "course number";
	private String fName = null;
	private String lName = null;
	private String cCode = null;
	private RatingBar[] ratingBar = null;

	/** displays the rating currently on display */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_professor_page);

		fName = getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME);
		lName = getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME);
		cCode = getIntent().getStringExtra(INTENT_COURSE_NUMBER);
		
		ViewGroup container = (ViewGroup) (findViewById(R.id.rateProfessorFieldList));
		ratingBar = new RatingBar[Rating.FIELD_NAMES.length];
		for(int i = 0;i<Rating.FIELD_NAMES.length;i++) {
			TextView newRatingLabel = new TextView(this);
			newRatingLabel.setText(Rating.FIELD_NAMES[i]);
			
			LinearLayout fullRate = new LinearLayout(this);
			fullRate.setOrientation(LinearLayout.HORIZONTAL);
			
			ratingBar[i] = new RatingBar(this, null, android.R.attr.ratingBarStyle);
			ratingBar[i].setNumStars(5);
			ratingBar[i].setRating(3);
			ratingBar[i].setStepSize(1);
			
			fullRate.addView(newRatingLabel);
			fullRate.addView(ratingBar[i]);
			container.addView(fullRate);
		}
		LinearLayout fullRate = new LinearLayout(this);
		fullRate.setOrientation(LinearLayout.HORIZONTAL);
		
		Button submit = new Button(this);
		submit.setText("Submit Rating");
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Rating r = new Rating(1);
				for(int i = 0;i<Rating.FIELD_NAMES.length;i++) {
					r.addResponseValue(ratingBar[i].getRating());
				}
				DBConnector.addRatings(fName,lName,cCode, r);
				if(DBConnector.hasErrorOccurred()) {
					Toast.makeText(getBaseContext(), "DBConnection problem", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(), "Rating added", Toast.LENGTH_SHORT).show();
				}
			}
		});
		fullRate.addView(submit);
		container.addView(fullRate);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}