package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class EvaluationPage extends Activity{

	public static final String INTENT_PROFESSOR_FIRST_NAME="first name";
	public static final String INTENT_PROFESSOR_LAST_NAME="last name";
	public static final String INTENT_COURSE_NUMBER="course number";
	
	/**displays the evaluation currently on display*/
	Evaluation shownEvaluation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.professor_course_eval);
		ArrayList<Evaluation> evals=null;
		
			Log.d("EvaluationPage","name (first): "+getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME));
			Log.d("EvaluationPage","name (last): "+getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME));
			Log.d("EvaluationPage","course: "+getIntent().getStringExtra(INTENT_COURSE_NUMBER));
			evals=DBConnector.getEvaluations(
					getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME),
					getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME), 
					getIntent().getStringExtra(INTENT_COURSE_NUMBER));
			//TODO: check DBConnector.hasErrorOccurred()
			
		if (evals==null||evals.size()==0){
			Toast.makeText(getBaseContext(), "Error Accessing Evaluations Database", Toast.LENGTH_LONG).show();
			finish();
		}else{
			shownEvaluation=evals.get(0);
			
			((TextView)findViewById(R.id.professorEvalLabel)).setText(
					getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME)+" "+
					getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME));
			
			((TextView)findViewById(R.id.courseEvalLabel)).setText(
					getIntent().getStringExtra(INTENT_COURSE_NUMBER));
			
			
			for (int i=0;i<shownEvaluation.getResponses().length;i++){
				double rating=shownEvaluation.getResponses()[i];
				
				ViewGroup container=(ViewGroup)(findViewById(R.id.evaluationFieldList));
				
				LinearLayout fullEval=new LinearLayout(this);
				
				fullEval.setOrientation(LinearLayout.HORIZONTAL);
				
				TextView newEvalAmount=new TextView(this);
				newEvalAmount.setText(""+rating);
				newEvalAmount.setGravity(Gravity.RIGHT);
				
				TextView newEvalLabel=new TextView(this);
				newEvalLabel.setText(Evaluation.FIELD_NAMES[i]+": ");
				
				fullEval.addView(newEvalLabel);
				fullEval.addView(newEvalAmount);
				
				if (i!=7&&i!=8)
					container.addView(fullEval);
			}
		}
	}
	
	public void goToComments(View view) {
		Intent intent = new Intent(this, CommentsPage.class);
		intent.putExtra("courseNum", getIntent().getStringExtra(INTENT_COURSE_NUMBER));
		intent.putExtra("fName", getIntent().getStringExtra(INTENT_PROFESSOR_FIRST_NAME));
		intent.putExtra("lName", getIntent().getStringExtra(INTENT_PROFESSOR_LAST_NAME));
  	 	this.startActivity(intent);
	}
	/**
	 * This are methods for bottom bar
	 * 
	 */
	public void home(View v) {
		Intent intent = new Intent(EvaluationPage.this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
	    startActivity(intent);
	}
	
	public void isis(View v) {
		Uri uri = Uri.parse("https://www.isis.ufl.edu/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void registrar(View v) {
		Uri uri = Uri.parse("http://www.registrar.ufl.edu/soc/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	public void help(View v) {
		Uri uri = Uri.parse("http://gizmodo.com/5909262/how-to-use-android");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	/**
	 * ^
	 */
	
}
