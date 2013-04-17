package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
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

	public ArrayList<String> comments;
	TextView noComments = null;
	boolean bgFlag;

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

			LinearLayout ratingBarLayout = new LinearLayout(this);
			android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
			ratingBarLayout.setGravity(Gravity.LEFT);
			ratingBarLayout.setLayoutParams(params);
			ratingBarLayout.addView(ratingBar);


			TextView evalLabel = new TextView(this);
			evalLabel.setText(Evaluation.FIELD_NAMES[i] + ": ");

			LinearLayout evalLabelLayout = new LinearLayout(this);
			evalLabelLayout.setGravity(Gravity.RIGHT);
			evalLabelLayout.setLayoutParams(params);
			evalLabelLayout.addView(evalLabel);

			fullEval.addView(evalLabelLayout);
			fullEval.addView(ratingBarLayout);
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
			if(shownRating.getTotalRatingResponses() > 0) {
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

					LinearLayout ratingBarLayout = new LinearLayout(this);
					ratingBarLayout.setGravity(Gravity.LEFT);
					android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
					ratingBarLayout.setLayoutParams(params);
					ratingBarLayout.addView(ratingBar);


					TextView ratingLabel = new TextView(this);
					ratingLabel.setText(Rating.FIELD_NAMES[i] + ": ");

					LinearLayout ratingLabelLayout = new LinearLayout(this);
					ratingLabelLayout.setGravity(Gravity.RIGHT);
					ratingLabelLayout.setLayoutParams(params);
					ratingLabelLayout.addView(ratingLabel);

					fullRating.addView(ratingLabelLayout);
					fullRating.addView(ratingBarLayout);
					container.addView(fullRating);
				}
			}
			else {
				container = (LinearLayout) (findViewById(R.id.ratingFieldList));
				container.setOrientation(LinearLayout.VERTICAL);
				LinearLayout fullRating = new LinearLayout(this);
				TextView ratingLabel = new TextView(this);
				ratingLabel.setText("There aren't any ratings for this professor yet. ");
				fullRating.addView(ratingLabel);
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
			if(!currentCourse.courseNum.equals("Average")) {
				container.addView(rateButton);
			}
		}

		////////////////TEXTBOOKS//////////////////

		// fill out textbooks part
		ArrayList<String> textbooks = DBConnector.getTextbooks(currentCourse.professorFirstName, currentCourse.professorLastName, currentCourse.courseNum);
		if (textbooks != null) {
			//makes sure there are textbooks before loading them
			Log.i("testing", textbooks.toString());
			LinearLayout ll = (LinearLayout) findViewById(R.id.textbookList);
			TextView tv = new TextView(this);
			tv.setText("Textbook Links");
			tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			ll.addView(tv);
			//TextView tv;
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




		/////////////Show the Comments////////////////
		comments = DBConnector.getComments(currentCourse.professorFirstName, currentCourse.professorLastName, currentCourse.courseNum);
		//TODO: check DBConnector.hasErrorOccurred()

		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentFieldList);

		if (comments == null) {
			noComments = new TextView(this);
			noComments.setText("No comments yet.");
			noComments.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			commentsList.addView(noComments);
		}
		else {
			bgFlag = true;
			for (int i = 0; i < comments.size(); i++) {
				String[] uc = comments.get(i).split(":");
				TextView newComment = new TextView(this);
				newComment.setText(uc[1]);
				newComment.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

				if (bgFlag){
					newComment.setBackgroundColor(Color.GRAY);
				}
				bgFlag = !bgFlag;
				newComment.setTypeface(Typeface.DEFAULT_BOLD);
				newComment.setTextSize(20);
				commentsList.addView(newComment);
			}
		}
	}
	
	public void addCommentPopup(View view) {
		final EditText box = new EditText(this);
		box.setHint("Type a comment!");
		box.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Add a comment");
		adb.setView(box);
		adb.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addComment(box.getText().toString());
				dialog.dismiss();
			}
		});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		if (MainActivity.getUsername() == null) {
			LayoutInflater factory = LayoutInflater.from(this);
			final View alertTextAreas = factory.inflate(R.layout.alert_text_areas, null);
			AlertDialog.Builder signInAlert = new AlertDialog.Builder(this);
			signInAlert.setView(alertTextAreas);
			signInAlert.setTitle("Log in to add comment");
			signInAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText usernameView = (EditText) alertTextAreas.findViewById(R.id.username);
					EditText passwordView = (EditText) alertTextAreas.findViewById(R.id.password);
					String username = usernameView.getText().toString().trim();
					String password = passwordView.getText().toString().trim();
					boolean isValid = DBConnector.isUFStudent(username, password);
					if (!isValid) {
						if(!username.equals("")) {
							Toast.makeText(getBaseContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						MainActivity.setUsername(username);
						adb.show();
					}
				}
			});
			signInAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			signInAlert.show();
		}
		else
			adb.show();
	}

	public void addComment(String comment){
		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentFieldList);
		//EditText box = (EditText)findViewById(R.id.commentBox);
		//InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);LayoutInflater factory = LayoutInflater.from(context);
		/*
		if (MainActivity.getUsername() == null) {
			//mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);
			Toast error = Toast.makeText(this, "You must sign in to comment", Toast.LENGTH_LONG);
			error.show();
			return;
		}
		*/
		
		comment = comment.trim();
		if (comment.equals("")) {
			//box.setText("");
			//mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);
			Toast error = Toast.makeText(this, "You must first enter a comment", Toast.LENGTH_LONG);
			error.show();
			return;
		}
		
		if (noComments != null) {
			commentsList.removeView(noComments);
		}
		
		TextView addedComment = new TextView(this);
		addedComment.setText(comment);
		addedComment.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		addedComment.setTypeface(Typeface.DEFAULT_BOLD);
		addedComment.setTextSize(20);
		if (bgFlag) {
			addedComment.setBackgroundColor(Color.GRAY);
		}
		commentsList.addView(addedComment);
		
		
		
		//mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);
		DBConnector.deleteComment(currentCourse.professorFirstName, currentCourse.professorLastName, currentCourse.courseNum);//remove users previous comment
		DBConnector.addComment(currentCourse.professorFirstName, currentCourse.professorLastName, currentCourse.courseNum, comment);
		//TODO: check DBConnector.hasErrorOccurred()

		//box.setText("");
		Toast message = Toast.makeText(this, "Comment added", Toast.LENGTH_LONG);
		message.show();
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

