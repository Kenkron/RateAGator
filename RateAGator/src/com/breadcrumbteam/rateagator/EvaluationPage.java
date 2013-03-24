package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EvaluationPage extends Activity {

	public static final String INTENT_PROFESSOR_FIRST_NAME = "first name";
	public static final String INTENT_PROFESSOR_LAST_NAME = "last name";
	public static final String INTENT_COURSE_NUMBER = "course number";

	/** displays the evaluation currently on display */
	Evaluation shownEvaluation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.professor_course_eval);

		Log.d("EvaluationPage",
				"name (first): "
						+ getIntent().getStringExtra(
								INTENT_PROFESSOR_FIRST_NAME));
		Log.d("EvaluationPage",
				"name (last): "
						+ getIntent()
								.getStringExtra(INTENT_PROFESSOR_LAST_NAME));
		Log.d("EvaluationPage",
				"course: " + getIntent().getStringExtra(INTENT_COURSE_NUMBER));
		shownEvaluation = DBConnector.getEvaluations(
				getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME),
				getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME),
				getIntent().getStringExtra(INTENT_COURSE_NUMBER));

		if (shownEvaluation == null || DBConnector.hasErrorOccurred()) {
			Toast.makeText(getBaseContext(),
					"Error Accessing Evaluations Database", Toast.LENGTH_LONG)
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
		}

		// fill out textbooks part
		ArrayList<String> textbooks = DBConnector.getTextbooks(getIntent()
				.getStringExtra(INTENT_COURSE_NUMBER));
		if (textbooks != null) {
			//makes sure there are textbooks before loading them
			Log.i("testing", textbooks.toString());
			LinearLayout ll = (LinearLayout) findViewById(R.id.textbookList);
			TextView tv;
			for (int i = 0; i < textbooks.size(); i++) {
				tv = new TextView(this);
				tv.setText(textbooks.get(i));
				tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				ll.addView(tv);
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