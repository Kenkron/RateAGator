package com.breadcrumbteam.rateagator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
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
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}

}