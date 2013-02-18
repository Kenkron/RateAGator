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
	
	//private static ArrayList<String> names = new ArrayList<String>();
	//private static ArrayList<String> paramList = new ArrayList<String>();
	
	public static ArrayList<String> allProfessorNames = new ArrayList<String>(); //for autosearch, not returned: access statically
	private static Professor theProfessor = null;	//returned in getProfessor()
	private static Course theCourse = null;			//might be returned in getCourses()
	private static ArrayList<Evaluation> evals = new ArrayList<Evaluation>();
	
	private static String fName;
	private static String lName;
	private static String cCode;
	
	public static void setBaseContext(Context basContext) {
		baseContext = basContext; 
	}
	public static Context getBaseContext() {
		return baseContext;
	}
	//http post
	private static InputStream httpPost(String postURL) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postURL);
			//httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			is = entity.getContent();
			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("DBConnector", "HTTP Post server error: bad response");
			}
		}
		catch(Exception e) {
			Log.d("DBConnector", "HTTP Post server error: death");
			Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
		}
		return is;//this isn't necessarily needed since it can be accessed statically
	}
	//Convert response to String
	private static String convertResponseToString(InputStream is) {
		String responseString = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			responseString = sb.toString();
		}
		catch(Exception e) {
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		return responseString;
	}
	
	public static void initializeAllProfessors() {
		try {
			long patience = 5000;
			long startTime = System.currentTimeMillis();
			Thread t = new Thread(new GetAllProfessorNames());
			t.start();

			while(t.isAlive()) {
				t.join(2000);	//Wait max of 2sec
				if(((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {//if it outlasts patience, auto join()
					t.interrupt();
					t.join();
				}
			}
		}
		catch(InterruptedException e) {
			Log.d("DBConnector.initializeAllProfessors()", "Server encountered an error");
			e.printStackTrace();
		}
	}
	private static class GetAllProfessorNames implements Runnable {
		@Override
		public void run() {
			String postURL = scriptLocation + "/getAllProfessorNames.php";

			httpPost(postURL);//returns InputStream is, but is accessed statically next
			
			result = convertResponseToString(is);
			
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
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetProfessorConnect(fName, lName));
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
		private static ArrayList<String> nameList = new ArrayList<String>();
		public GetProfessorConnect(String fName, String lName) {
			if(fName != null) nameList.add("name1=" + fName.trim());
			if(lName != null) nameList.add("name2=" + lName.trim());
		}
		@Override
		public void run() {
			String postURL = scriptLocation + "/getProfessor.php";
			//add parameters to the URL
			if(!nameList.isEmpty()) {
				postURL += "?";
				for(int i = 0;i<nameList.size();i++) {
					postURL += nameList.get(i);
					if(i+1 < nameList.size()) {
						postURL += "&";
					}
				}
			}
			
			httpPost(postURL);//returns InputStream is, but is accessed statically next
			
			result = convertResponseToString(is);
			
			//JSON decode, add to list
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				if(jArray.length() > 0) {
					theProfessor = new Professor(fName, lName);	//create the Professor
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
	public static ArrayList<Evaluation> getEvaluations(String fName, String lName, String cCode) throws InterruptedException {//fName, lName, cCode
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetEvaluationsConnect(fName, lName, cCode));
		t.start();

		while (t.isAlive()) {
			t.join(1000);	//Wait max of 1sec
			if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {//if it outlasts patience, auto join()
				t.interrupt();
				t.join();
			}
		}
        return evals;
	}
	private static class GetEvaluationsConnect implements Runnable {
		private static ArrayList<String> paramList = new ArrayList<String>();
		public GetEvaluationsConnect(String fName, String lName, String cCode) {
			paramList.add("fname=" + fName.trim());
			paramList.add("lname=" + lName.trim());
			paramList.add("ccode=" + cCode.trim());
		}
		@Override
		public void run() {
			String postURL = scriptLocation + "/getEvals.php";
			if(!paramList.isEmpty()) {	//add parameters to the URL
				postURL += "?";
				for(int i = 0;i<paramList.size();i++) {
					postURL += paramList.get(i);
					if(i+1 < paramList.size()) {
						postURL += "&";
					}
				}
			}
			
			httpPost(postURL);//returns InputStream is, but is accessed statically next
			
			result = convertResponseToString(is);
			
			//JSON decode, add to list
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				evals.clear();
				for(int i = 0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					Evaluation currentEvaluation = new Evaluation(json_data.getInt("Responded"));
					for(int j = 1;j<=10;j++) {
						currentEvaluation.addResponseValue(json_data.getDouble("R" + j));
					}
					evals.add(currentEvaluation);
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
