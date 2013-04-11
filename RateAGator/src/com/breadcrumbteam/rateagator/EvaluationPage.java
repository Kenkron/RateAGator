package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EvaluationPage extends Activity {

	public static final String INTENT_COURSE="course", 
			INTENT_USERNAME = "username", 
			INTENT_EVALUATION = "eval",
			INTENT_RATING = "rating";
	
	public static String username = "";

	/**the course for which data is shown*/
	Course currentCourse;
	
	/** displays the evaluation currently on display */
	Evaluation shownEvaluation;
	Rating shownRating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.professor_course_eval);
	    /**
	     * Keeps screen in portrait mode
	     */
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	    //sets up help text
	    MainActivity.setupBottomButtonHelpListeners(this);

		((Button)findViewById(R.id.goComments)).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "click to submit a comment", Toast.LENGTH_LONG).show();
				Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(250);
				return false;
			}
		});
		
		username = this.getIntent().getStringExtra(INTENT_USERNAME);
		Log.i("#tardif", "username is: " + username);

		currentCourse=((Course)getIntent().getSerializableExtra(INTENT_COURSE));
		
		Log.d("EvaluationPage",
				"name (first): "
						+ currentCourse.professorFirstName);
		Log.d("EvaluationPage",
				"name (last): "
						+ currentCourse.professorLastName);
		Log.d("EvaluationPage",
				"course: " 
						+ currentCourse.courseNum);
		
		/////////////Show the Evaluations////////////////
		shownEvaluation = (Evaluation) getIntent().getSerializableExtra(INTENT_EVALUATION);

			((TextView) findViewById(R.id.professorLabel))
					.setText(currentCourse.professorFirstName
							+ " "
							+ currentCourse.professorLastName + " "
							+ currentCourse.courseNum);
			
			for (int i = 0; i < shownEvaluation.getResponses().length; i++) {
				double rating = shownEvaluation.getResponses()[i];
				rating = ((int) (rating*100))/100.0;

				ViewGroup container = (ViewGroup) (findViewById(R.id.evaluationFieldList));

				LinearLayout fullEval = new LinearLayout(this);

				fullEval.setOrientation(LinearLayout.HORIZONTAL);

				TextView newEvalAmount = new TextView(this);
				newEvalAmount.setText("" + rating);
				newEvalAmount.setGravity(Gravity.RIGHT);

				TextView newEvalLabel = new TextView(this);
				newEvalLabel.setText(Evaluation.FIELD_NAMES[i] + ": ");

				fullEval.addView(newEvalLabel);
				fullEval.addView(newEvalAmount);

				if (i != 7 && i != 8)
					container.addView(fullEval);
			}
		
		
		///////////////// Get the ratings////////////////
		shownRating = (Rating) getIntent().getSerializableExtra(INTENT_RATING);
				
		if (shownRating == null) {
			Toast.makeText(getBaseContext(),
					"Error Accessing Ratings Database", Toast.LENGTH_LONG)
					.show();
			//finish();
		} else {

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
				newRatingLabel.setText(Rating.FIELD_NAMES[i] + ": ");

				fullRating.addView(newRatingLabel);
				fullRating.addView(newRatingAmount);

				if (i != 7 && i != 8)
					container.addView(fullRating);
			}
		}

		////////////////TEXTBOOKS//////////////////
		
		// fill out textbooks part
		ArrayList<String> textbooks = DBConnector.getTextbooks(currentCourse.courseNum);
		if (textbooks != null) {
			//makes sure there are textbooks before loading them
			Log.i("testing", textbooks.toString());
			LinearLayout ll = (LinearLayout) findViewById(R.id.textbookList);
			TextView tv;
			for (int i = 0; i < textbooks.size(); i++) {
				String currentLink = textbooks.get(i);
				if(currentLink.charAt(0) == 'h') {
					String bookName = currentLink.split("com/")[1].split("/")[0].replace("-", " ");
					tv = new TextView(this);
					String linkFormat = "<a href=\"" + currentLink + "\">" + bookName + "</a>";
					tv.setText(Html.fromHtml(linkFormat));
					tv.setMovementMethod(LinkMovementMethod.getInstance());
					tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					ll.addView(tv);
				}
			}
		}
	}

	public void goToRatings(View view) {
		Intent intent = new Intent(this, RatingsPage.class);
		intent.putExtra(RatingsPage.INTENT_COURSE_NUMBER,
				currentCourse.courseNum);
		intent.putExtra(RatingsPage.INTENT_PROFESSOR_FIRST_NAME,
				currentCourse.professorFirstName);
		intent.putExtra(RatingsPage.INTENT_PROFESSOR_LAST_NAME,
				currentCourse.professorLastName);
		this.startActivity(intent);
	}
	
	public void goToComments(View view) {
		Intent intent = new Intent(this, CommentsPage.class);
		intent.putExtra("courseNum",currentCourse.courseNum);
		intent.putExtra("fName",currentCourse.professorFirstName);
		intent.putExtra("lName",currentCourse.professorLastName);
		this.startActivity(intent);
	}

	public void goToLink(View v) {
		MainActivity.goToLink(v);
	}
}

