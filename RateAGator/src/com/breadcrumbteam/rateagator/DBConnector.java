package com.breadcrumbteam.rateagator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

/**This object functions as an interface between the remote database and
 * the software on the local app.*/
public class DBConnector {
	private static final String scriptLocation = "http://sgiordano.com/rateAgator/";
	private static Context baseContext = null;
	
	public static String text = null;//my standard returner
	private static JSONArray jArray;
	private static String result = null;
	private static InputStream is = null;
	private static StringBuilder sb = null;
	
	public static ArrayList<String> names = new ArrayList<String>();
	public static ArrayList<String> paramList = new ArrayList<String>();
	public static ArrayList<Double> evals = new ArrayList<Double>();
	
	public static ArrayList<String> allProfessorNames = new ArrayList<String>(); //for autosearch, not returned: access statically
	private static Professor theProfessor = null;	//returned in getProfessor()
	private static Course theCourse = null;			//might be returned in getCourses()
	
	public static void setBaseContext(Context basContext) {
		baseContext = basContext; 
	}
	public static Context getBaseContext() {
		return baseContext;
	}
	
	public static void initializeAllProfessors() {
		try {
			long patience = 5000;
			long startTime = System.currentTimeMillis();
			Thread t = new Thread(new GetAllProfessorNames());
			t.start();

			while (t.isAlive()) {
				t.join(1000);	//Wait max of 1sec
				if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {//if it outlasts patience, auto join()
					t.interrupt();
					t.join();
				}
			}
		}
		catch(InterruptedException e) {
			Log.d("RateAGator DBConnector", "Server encountered an error");
			e.printStackTrace();//this would be a fatal error for Nick
		}
	}
	private static class GetAllProfessorNames implements Runnable {
		@Override
		public void run() {
			try {
				//http post
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost;
				httppost = new HttpPost(scriptLocation + "/getAllProfessorNames.php");
				
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				is = entity.getContent();
				if (response.getStatusLine().getStatusCode() != 200) {
					Log.d("RateAGator DBConnector.GetAllProfessorNames", "Server encountered an error");
				}
			}
			catch(Exception e) {
				Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
			}
			
			//Convert response to String, set result
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}
			catch(Exception e) {
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			
			//JSON decode, add to list
			try {
				jArray = new JSONArray(result);
				JSONObject json_data=null;
				for(int i=0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					allProfessorNames.add(json_data.getString("LastName") + ", " + json_data.getString("FirstName")); 
				}
			}
			catch(JSONException e1) {
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * get professor names
	 */
	public static Professor getProfessor(String fName, String lName) throws InterruptedException {
		names.clear();
		paramList.clear();
		if(fName != null) paramList.add("fname=" + fName.trim());
		if(lName != null) paramList.add("lname=" + lName.trim());
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetProfessorConnect());
		t.start();

		while (t.isAlive()) {
			t.join(1000);	//Wait max of 1sec
			if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {//if it outlasts patience, auto join()
				t.interrupt();
				t.join();
			}
		}
		return theProfessor;
	}
	private static class GetProfessorConnect implements Runnable {
		@Override
		public void run() {
			try {
				//http post
				HttpClient httpclient = new DefaultHttpClient();
				
				HttpPost httppost;
				
				String postURL = scriptLocation + "/getProfessor.php";
				
				//add parameters to the URL
				if(!paramList.isEmpty()) {
					postURL += "?";
					for(int i = 0;i<paramList.size();i++) {
						postURL += paramList.get(i);
						if(i+1 < paramList.size()) {
							postURL += "&";
						}
					}
				}
				
				httppost = new HttpPost(postURL);
				//httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				is = entity.getContent();
				if (response.getStatusLine().getStatusCode() != 200) {
					Log.d("MyApp", "Server encountered an error");
				}
			}
			catch(Exception e) {
				Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
			}
			
			//Convert response to String, set result
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}
			catch(Exception e) {
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			
			//JSON decode, add to list
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				if(jArray.length() > 0) {
					theProfessor = new Professor(paramList.get(0), paramList.get(1));	//create the Professor
					for(int i = 0;i<jArray.length();i++) {
						json_data = jArray.getJSONObject(i);
						String courseName = json_data.getString("CourseName");
						Course currentCourse;
						if(courseName.equals("NULL")) {
							currentCourse = new Course(null, json_data.getString("CourseCode"));
						}
						else {
							currentCourse = new Course(courseName, json_data.getString("CourseCode"));
						}
							
						theProfessor.addCourse(currentCourse);
					}
				}
				else {
					theProfessor = null;
				}
			}
			catch(JSONException e1) {
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
	}
	

	/*
	 * get evaluations
	 */
	public static void getEvaluations(String fName, String lName, String cCode) throws InterruptedException {//fName, lName, cCode
		names.clear();
		paramList.clear();
		paramList.add("fname=" + fName.trim());
		paramList.add("lname=" + lName.trim());
		paramList.add("ccode=" + cCode.trim());
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetEvaluationsConnect());
		t.start();

		while (t.isAlive()) {
			t.join(1000);	//Wait max of 1sec
			if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {//if it outlasts patience, auto join()
				t.interrupt();
				t.join();
			}
		}
        text = names.get(0);
	}
	private static class GetEvaluationsConnect implements Runnable {
		@Override
		public void run() {
			try {
				//http post
				HttpClient httpclient = new DefaultHttpClient();
				
				HttpPost httppost;
				
				String postURL = scriptLocation + "/getEvals.php";
				
				//add parameters to the URL
				if(!paramList.isEmpty()) {
					postURL += "?";
					for(int i = 0;i<paramList.size();i++) {
						postURL += paramList.get(i);
						if(i+1 < paramList.size()) {
							postURL += "&";
						}
					}
				}
				
				httppost = new HttpPost(postURL);
				
				HttpResponse response = httpclient.execute(httppost);
				
				HttpEntity entity = response.getEntity();
				
				is = entity.getContent();
				if (response.getStatusLine().getStatusCode() != 200) {
					Log.d("MyApp", "Server encountered an error");
				}
			}
			catch(Exception e) {
				Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
			}
			
			//Convert response to String, set result
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}
			catch(Exception e) {
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			
			//JSON decode, add to list
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				for(int i = 0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					evals.clear();
					for(int j = 1;j<=10;j++) {
						evals.add(Double.valueOf(json_data.getString("R" + j)));
					}
					//TODO: stuff evals into an Evals Object
				}
			}
			catch(JSONException e1) {
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
	}	
}
