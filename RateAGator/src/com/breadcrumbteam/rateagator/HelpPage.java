package com.breadcrumbteam.rateagator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HelpPage extends Activity {

	TextView help = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("got to help page", "sfalfjdasd");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.help_page);

	}
	public static void goToLink(View v) {
		int vId = v.getId();
		Context c = v.getContext();
		if (vId == R.id.goCourses){
			Uri uri = Uri.parse("http://www.registrar.ufl.edu/soc/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			c.startActivity(intent);
		}
		else if (vId == R.id.goHelp) {
			/**
			 * This will crash on any other page then main activity at the moment
			 * This needs to be corrected it is here for testing for the time being
			 */
			Intent intent = new Intent(c, HelpPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			c.startActivity(intent);
		}
		else if (vId == R.id.goHome) {
			Intent intent = new Intent(c, MainActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    
		    c.startActivity(intent);
		}
		else if (vId == R.id.goIsis) {
			Uri uri = Uri.parse("https://www.isis.ufl.edu/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			c.startActivity(intent);
		}
	}

}
