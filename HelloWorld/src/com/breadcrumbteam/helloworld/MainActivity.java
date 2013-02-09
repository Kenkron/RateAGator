package com.breadcrumbteam.helloworld;

import java.io.Console;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

//TEST: koceskik

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**this method is called when the Greet Me button is pressed
	 * What causes this method to be called?
	 * 		In the file res/layout/activity_main.xml (Which
	 * 		is the main UI layout file), I gave the greetMe
	 * 		button the following property:
	 * 		android:onClick="greetMe"*/
	public void greetMe(View v){
		//You have two options here.  There's the standard
		//System.out.println:
		System.out.println("Hello World");
		//and there's the Android specific Log.println:
		Log.println(Log.INFO, "Message", "Hello Developer");
		//the log option has some added benefits such as:
		//--it can specify the priority/type of the message
		//--it outputs to a log file that can be reviewed
		//       if the debugger is not hooked up.
		
		//on the emulator (or a connected android)
		//both of these should display a message in the LogCat
		//(which should typically be below this window
		//in the eclipse UI)
	}
	
	/**this method is called when the exit button is pressed
	 * What causes this method to be called?
	 * 		In the file res/layout/activity_main.xml (Which
	 * 		is the main UI layout file), I gave the exit
	 * 		button the following property:
	 * 		android:onClick="endProgram";*/
	public void endProgram(View v){
		finish();
		//I should mention that this may not reset the UI.
		//Android apps are somewhat designed to act like they
		//never stop running.
	}

}
