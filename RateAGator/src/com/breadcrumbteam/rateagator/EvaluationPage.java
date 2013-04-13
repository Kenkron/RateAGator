package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.Layout.Alignment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
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
				float rating = (float) shownEvaluation.getResponses()[i];
				rating = (float) (Math.round(rating*100.0)/100.0);

				LinearLayout container = (LinearLayout) (findViewById(R.id.evaluationFieldList));
				container.setOrientation(LinearLayout.VERTICAL);
				
				LinearLayout fullEval = new LinearLayout(this);

				RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
				ratingBar.setNumStars(5);
				ratingBar.setRating(rating);
				ratingBar.setStepSize(0.1f);
				

				TextView newEvalLabel = new TextView(this);
				newEvalLabel.setText(Evaluation.FIELD_NAMES[i] + ": ");
				newEvalLabel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));

				fullEval.addView(newEvalLabel);
				fullEval.addView(ratingBar);

				//if (i != 7 && i != 8)
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
			LinearLayout container = null;
			for (int i = 0; i < shownRating.getRatingResponses().length; i++) {
				float rating = (float) shownRating.getRatingResponses()[i];
				
				//puts in 2 decimal points only
				rating = (float) (Math.round(rating*100.0)/100.0);

				container = (LinearLayout) (findViewById(R.id.ratingFieldList));
				container.setOrientation(LinearLayout.VERTICAL);
				LinearLayout fullRating = new LinearLayout(this);

				RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
				ratingBar.setNumStars(5);
				ratingBar.setRating(rating);
				ratingBar.setStepSize(0.1f);

				TextView newRatingLabel = new TextView(this);
				newRatingLabel.setText(Rating.FIELD_NAMES[i] + ": ");
				newRatingLabel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));

				fullRating.addView(newRatingLabel);
				fullRating.addView(ratingBar);
				container.addView(fullRating);
			}
			final Context context = this;
			Button rateButton = new Button(this);
			rateButton.setText("Rate Professor");
			rateButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(MainActivity.getUsername() != null) {
						goToRateProfessor(v);
					}
					else {
						//Toast.makeText(getBaseContext(), "Must be logged in", Toast.LENGTH_SHORT).show();
						LayoutInflater factory = LayoutInflater.from(context);
						final View alertTextAreas = factory.inflate(R.layout.alert_text_areas, null);
						AlertDialog.Builder signInAlert = new AlertDialog.Builder(context);
						signInAlert.setView(alertTextAreas);
						signInAlert.setTitle("Log in to Rate Professor");
						signInAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								EditText usernameView = (EditText) alertTextAreas.findViewById(R.id.username);
								EditText passwordView = (EditText) alertTextAreas.findViewById(R.id.password);
								String username = usernameView.getText().toString().trim();
								String password = passwordView.getText().toString().trim();
								boolean isValid = DBConnector.isUFStudent(username, password);
								if(isValid) {
									goToRateProfessor(null);
								}
								else {
									if(!username.equals("")) {
										Toast.makeText(getBaseContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
						signInAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						});
						signInAlert.show();
					}
				}
			});
			container.addView(rateButton);
		}

		////////////////TEXTBOOKS//////////////////
		
		// fill out textbooks part
		ArrayList<String> textbooks = DBConnector.getTextbooks(currentCourse.professorFirstName, currentCourse.professorLastName, currentCourse.courseNum);
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
	
	public void goToRateProfessor(View view) {
		Intent intent = new Intent(this, RateProfessorPage.class);
		intent.putExtra(RatingsPage.INTENT_COURSE_NUMBER,
				currentCourse.courseNum);
		intent.putExtra(RatingsPage.INTENT_PROFESSOR_FIRST_NAME,
				currentCourse.professorFirstName);
		intent.putExtra(RatingsPage.INTENT_PROFESSOR_LAST_NAME,
				currentCourse.professorLastName);
		this.startActivity(intent);
	}

}

