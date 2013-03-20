package com.breadcrumbteam.rateagator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import android.net.ConnectivityManager;
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
	
	//public static String text = null;//my standard returner
	//private static JSONArray jArray;
	//private static String result = null;
	private static InputStream is = null;
	private static StringBuilder sb = null;
	
	private static boolean errorOccurred = false; 
	
	//private static ArrayList<String> names = new ArrayList<String>();
	//private static ArrayList<String> paramList = new ArrayList<String>();
	
	public static ArrayList<String> allProfessorNames = new ArrayList<String>(); //for autosearch, not returned: access statically
	public static ArrayList<String> allCourseCodes = new ArrayList<String>(); //for autosearch, not returned: access statically
	private static Professor theProfessor = null;	//returned in getProfessor()
	private static Course theCourse = null;			//might be returned in getCourses()
	private static ArrayList<Evaluation> evals = new ArrayList<Evaluation>();
	private static ArrayList<Professor> professors = new ArrayList<Professor>();
	
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
	private static synchronized InputStream httpPost(String postURL) {
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postURL);
			//httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			is = entity.getContent();
			if(response.getStatusLine().getStatusCode() != 200) {
				Log.d("DBConnector", "HTTP Post server error: bad response");
			}
		}
		catch(Exception e) {
			errorOccurred = true;
			Log.d("DBConnector", "HTTP Post server error: death");
			//Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
		}
		return is;//this isn't necessarily needed since it can be accessed statically
	}
	//Convert response to String
	private static synchronized String convertResponseToString(InputStream is) {
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
			errorOccurred = true;
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		return responseString;
	}
	private static String convertParamList(ArrayList<String> x) {
		String paramURL = "";
		if(!x.isEmpty()) {
			paramURL += "?";
			for(int i = 0;i<x.size();i++) {
				paramURL += x.get(i);
				if(i+1 < x.size()) {
					paramURL += "&";
				}
			}
		}
		return paramURL;
	}
	
	public static boolean CheckInternet(Context context) 
	{
	    ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    return wifi.isConnected() || mobile.isConnected();
	}
	
	public static void initProfessorsAndCourses() {
		do {
			errorOccurred = false;
			try {
				long patience = 5000;
				long startTime = System.currentTimeMillis();
				Thread t1 = new Thread(new GetAllProfessorNames());
				Thread t2 = new Thread(new GetAllCourseCodes());
				t1.start();
				t2.start();

				t1.join();
				t2.join();
				/*
				while(t1.isAlive()) {
					t1.join(2000);	//Wait max of 2sec
					if(((System.currentTimeMillis() - startTime) > patience) && t1.isAlive()) {//if it outlasts patience, auto join()
						t1.interrupt();
						t1.join();
					}
				}
				while(t2.isAlive()) {
					t2.join(2000);	//Wait max of 2sec
					if(((System.currentTimeMillis() - startTime) > patience) && t2.isAlive()) {//if it outlasts patience, auto join()
						t2.interrupt();
						t2.join();
					}
				}*/
			}
			catch(InterruptedException e) {
				errorOccurred = true;
				Log.d("DBConnector.initProfessorsAndCourses()", "Server encountered an error interrupted");
				e.printStackTrace();
			}
			catch(Exception e) {
				errorOccurred = true;
				Log.d("DBConnector.initProfessorsAndCourses()", "Server encountered an error exception");
				e.printStackTrace();
			}
			
			if(errorOccurred) {
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					/* IGNORE */
				}
			}
		} while(errorOccurred);
	}
	private static class GetAllProfessorNames implements Runnable {
		@Override
		public void run() {
			String postURL = scriptLocation + "/getAllProfessorNames.php";

			InputStream is = httpPost(postURL);//returns InputStream is, but is accessed statically next
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject json_data=null;
				for(int i=0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					allProfessorNames.add(json_data.getString("LastName") + ", " + json_data.getString("FirstName")); 
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	private static class GetAllCourseCodes implements Runnable {
		@Override
		public void run() {
			String postURL = scriptLocation + "/getAllCourseCodes.php";

			InputStream is = httpPost(postURL);//returns InputStream is, but is accessed statically next
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}

			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject json_data=null;
				for(int i=0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					allCourseCodes.add(json_data.getString("CourseCode")); 
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}

	//
	//get Professor
	//
	public static Professor getProfessor(String fName, String lName) throws InterruptedException {
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetProfessorConnect(fName, lName));
		t.start();

		t.join();
		if(errorOccurred) {//return a null object if an errorOccurred
			return null;
		}
		return theProfessor;
	}
	private static class GetProfessorConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public GetProfessorConnect(String fName, String lName) {
			if(fName != null) paramList.add("name1=" + fName.trim().replaceAll(" ", "%20"));
			if(lName != null) paramList.add("name2=" + lName.trim().replaceAll(" ", "%20"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getProfessor.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);//returns InputStream is, but is accessed statically next
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
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
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	//
	// get Course
	//
	public static ArrayList<Professor> getCourse(String cCode) throws InterruptedException {
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		professors.clear();
		Thread t = new Thread(new GetCourseConnect(cCode));
		t.start();

		t.join();
		if(errorOccurred) {//return a null object if an errorOccurred
			return null;
		}
		return professors;
	}
	private static class GetCourseConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public GetCourseConnect(String cCode) {
			if(cCode != null) paramList.add("name1=" + cCode.trim().replaceAll(" ", "%20"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getProfessor.php" + convertParamList(paramList);

			InputStream is = httpPost(postURL);//returns InputStream is, but is accessed statically next
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject json_data = null;
				if(jArray.length() > 0) {
					for(int i = 0;i<jArray.length();i++) {
						json_data = jArray.getJSONObject(i);
						String fName = json_data.getString("FirstName");
						String lName = json_data.getString("LastName");
						theProfessor = new Professor(fName, lName);//create the Professor
						
						String courseName = json_data.getString("CourseName");
						Course currentCourse;
						if(courseName.equals("NULL")) {
							currentCourse = new Course(null, json_data.getString("CourseCode"));
						}
						else {
							currentCourse = new Course(courseName, json_data.getString("CourseCode"));
						}
							
						theProfessor.addCourse(currentCourse);
						professors.add(theProfessor);
					}
				}
				else {
					professors = null;
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	
	//
	//get Evaluations
	//
	public static ArrayList<Evaluation> getEvaluations(String fName, String lName, String cCode) throws InterruptedException {
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetEvaluationsConnect(fName, lName, cCode));
		t.start();

		t.join();
		if(errorOccurred) {
			return null;
		}
        return evals;
	}
	private static class GetEvaluationsConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public GetEvaluationsConnect(String fName, String lName, String cCode) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getEvals.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
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
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	
	//
	//getRatings
	//
	public static ArrayList<Rating> getRatings(String fName, String lName, String cCode) throws InterruptedException {
		ArrayList<Rating> ratings = new ArrayList<Rating>();
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetRatingsConnect(fName, lName, cCode, ratings));
		t.start();

		t.join();
		if(errorOccurred) {
			return null;
		}
        return ratings;
	}
	private static class GetRatingsConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		private ArrayList<Rating> ratings = null;
		public GetRatingsConnect(String fName, String lName, String cCode, ArrayList<Rating> ratings) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			this.ratings = ratings;
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getRatings.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);
			if(errorOccurred) {
				return;
			}
			
			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject json_data = null;
				for(int i = 0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					Rating currentRating = new Rating(json_data.getInt("Responded"));
					
					for(int j = 0;j<=Rating.DB_FIELD_NAMES.length;j++) {
						currentRating.addResponseValue(json_data.getDouble(Rating.DB_FIELD_NAMES[j]));
					}
					ratings.add(currentRating);
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	
	//
	//setRatings
	//
	public static void setRatings(String fName, String lName, String cCode, Rating newRating, Rating oldRating) throws InterruptedException {
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new SetRatingsConnect(fName, lName, cCode, newRating, oldRating));
		t.start();

		t.join();
		if(errorOccurred) {
			return;//TODO: test for fail
		}
	}
	private static class SetRatingsConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public SetRatingsConnect(String fName, String lName, String cCode, Rating newRating, Rating oldRating) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			
			int responseCount = oldRating.getTotalRatingResponses();
			paramList.add("Response=" + (responseCount+1));
			for(int i = 0;i<Rating.DB_FIELD_NAMES.length;i++) {
				double oldVal = oldRating.getRatingResponses()[i];
				double newVal = (oldVal * responseCount + newRating.getRatingResponses()[i]) / (responseCount+1);
				paramList.add(Rating.DB_FIELD_NAMES[i] + "=" + newVal);
			}
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/addRatings.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);
			if(errorOccurred) {
				return;
			}
		}
	}
	
	//
	//getComments
	//
	public static ArrayList<String> getComments(String fName, String lName, String cCode) throws InterruptedException {
		ArrayList<String> comments = new ArrayList<String>();
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new GetCommentsConnect(fName, lName, cCode, comments));
		t.start();

		t.join();
		if(errorOccurred) {
			return null;
		}
		return comments;
	}
	private static class GetCommentsConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		private ArrayList<String> comments = null;
		public GetCommentsConnect(String fName, String lName, String cCode, ArrayList<String> comments) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			this.comments = comments;
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getComments.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);

			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			
			//JSON decode, add to list
			try {
				JSONArray jArray = new JSONArray(result);
				JSONObject json_data = null;
				for(int i = 0;i<jArray.length();i++) {
					json_data = jArray.getJSONObject(i);
					String currentComment = json_data.getString("Comment");
					comments.add(currentComment);
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
			catch (ParseException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	
	
	//
	//postComment
	//
	public static void addComment(String fName, String lName, String cCode, String comment) throws InterruptedException {
		errorOccurred = false;
		long patience = 5000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new AddCommentConnect(fName, lName, cCode, comment));
		t.start();

		t.join();
		if(errorOccurred) {
			return;//TODO: test for fail
		}
	}
	private static class AddCommentConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public AddCommentConnect(String fName, String lName, String cCode, String comment) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			paramList.add("comment=" + comment.trim().replaceAll(" ", "%20").replaceAll("&", "%26"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/addComment.php" + convertParamList(paramList);
			
			InputStream is = httpPost(postURL);
		}
	}
	
	public static boolean hasErrorOccurred(){
		return errorOccurred;
	}
}
