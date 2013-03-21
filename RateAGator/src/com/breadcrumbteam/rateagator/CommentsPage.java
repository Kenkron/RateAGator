package com.breadcrumbteam.rateagator;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CommentsPage extends Activity {
	

	public ArrayList<String> comments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments_page);


		comments = DBConnector.getComments(getIntent().getStringExtra("fName"),
				getIntent().getStringExtra("lName"), getIntent().getStringExtra("courseNum"));
		//TODO: check DBConnector.hasErrorOccurred()


		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentsList);
		for (int i = 0; i < comments.size(); i++) {
			TextView newComment = new TextView(this);
			newComment.setText(comments.get(i));
			newComment.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			commentsList.addView(newComment);
		}

	}


	public void addComment(View view){
		ViewGroup commentsList = (ViewGroup)findViewById(R.id.commentsList);
		EditText box = (EditText)findViewById(R.id.commentBox);
		String comment = box.getText().toString();
		
		TextView addedComment = new TextView(this);
		addedComment.setText(comment);
		addedComment.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		commentsList.addView(addedComment);
		
		
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(box.getWindowToken(), 0);

		DBConnector.addComment(getIntent().getStringExtra("fName"), getIntent().getStringExtra("lName"),
				getIntent().getStringExtra("courseNum"), comment);
		//TODO: check DBConnector.hasErrorOccurred()

		box.setText("");
		Toast message = Toast.makeText(this, "Comment added", Toast.LENGTH_LONG);
		message.show();
	}

}
