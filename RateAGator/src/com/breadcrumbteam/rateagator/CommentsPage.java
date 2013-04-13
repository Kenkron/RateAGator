package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.content.DialogInterface;

public class CommentsPage extends Activity {
	

	public ArrayList<String> comments;
	TextView noComments = null;
	boolean bgFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments_page);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		comments = DBConnector.getComments(getIntent().getStringExtra("fName"),
				getIntent().getStringExtra("lName"), getIntent().getStringExtra("courseNum"));
		//TODO: check DBConnector.hasErrorOccurred()


		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentsList);
		
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
	


	public void addComment(View view){
		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentsList);
		EditText box = (EditText)findViewById(R.id.commentBox);
		String comment = box.getText().toString();
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if (MainActivity.getUsername() == null) {
			mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);
			Toast error = Toast.makeText(this, "You must sign in to comment", Toast.LENGTH_LONG);
			error.show();
			return;
		}
		
		comment = comment.trim();
		if (comment.equals("")) {
			box.setText("");
			mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);
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
		
		
		
		mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);

		DBConnector.addComment(getIntent().getStringExtra("fName"), getIntent().getStringExtra("lName"),
				getIntent().getStringExtra("courseNum"), comment);
		//TODO: check DBConnector.hasErrorOccurred()

		box.setText("");
		Toast message = Toast.makeText(this, "Comment added", Toast.LENGTH_LONG);
		message.show();
	}

}
