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
import org.apache.http.message.BasicNameValuePair;
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

	private static boolean errorOccurred = false;
	public static boolean hasErrorOccurred(){
		return errorOccurred;
	}
	public static boolean interrupted = false;

	public static ArrayList<String> allProfessorNames = new ArrayList<String>(); //for autosearch, not returned: access statically
	public static ArrayList<String> allCourseCodes = new ArrayList<String>(); //for autosearch, not returned: access statically
	private static Professor theProfessor = null;	//returned in getProfessor()
	private static ArrayList<Evaluation> evals = new ArrayList<Evaluation>();
	private static ArrayList<Professor> professors = new ArrayList<Professor>();
	private static Rating rating = null;

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
				Log.w("DBConnector", "HTTP Post server error: bad response");
			}
		}
		catch(Exception e) {
			errorOccurred = true;
			Log.e("DBConnector", "HTTP Post server error: death");
			//Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
		}
		return is;
	}
	//Convert response to String
	private static synchronized String convertResponseToString(InputStream is) {
		String responseString = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
			StringBuilder sb = new StringBuilder();
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
			Log.e("DBConnector log_tag", "Error converting result " + e.toString());
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

	public static boolean CheckInternet(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return wifi.isConnected() || mobile.isConnected();
	}

	public static void initProfessorsAndCourses() {
		do {//used to ensure that we get results before displaying page
			errorOccurred = false;
			try {
				Thread t1 = new Thread(new GetAllProfessorNames());
				Thread t2 = new Thread(new GetAllCourseCodes());
				t1.start();
				t2.start();

				t1.join();
				t2.join();
			}
			catch(InterruptedException e) {
				errorOccurred = true;
				Log.e("DBConnector.initProfessorsAndCourses()", "Thread interrupted");
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
	public static class GetAllProfessorNames implements Runnable {
		@Override
		public void run() {
			String postURL = scriptLocation + "/getAllProfessorNames.php";

			errorOccurred = false;
			InputStream is = httpPost(postURL);
			if(errorOccurred) return;

			String result = convertResponseToString(is);
			if(errorOccurred) return;

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
		}
	}
	public static class GetAllCourseCodes implements Runnable {
		@Override
		public void run() {
			String postURL = scriptLocation + "/getAllCourseCodes.php";

			errorOccurred = false;
			InputStream is = httpPost(postURL);
			if(errorOccurred) return;

			String result = convertResponseToString(is);
			if(errorOccurred) return;

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
		}
	}

	//
	//get Professor
	//
	public static Professor getProfessor(String fName, String lName) {
		errorOccurred = false;
		Thread t = new Thread(new GetProfessorConnect(fName, lName));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {//return a null object if an errorOccurred
			return null;
		}
		return theProfessor;
	}
	private static class GetProfessorConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		private String fName, lName;
		public GetProfessorConnect(String fName, String lName) {
			if(fName != null) paramList.add("name1=" + fName.trim().replaceAll(" ", "%20"));
			if(lName != null) paramList.add("name2=" + lName.trim().replaceAll(" ", "%20"));
			this.fName = fName;
			this.lName = lName;
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
		}
	}
	//
	// get Course
	//
	public static ArrayList<Professor> getCourse(String cCode) {
		errorOccurred = false;
		professors.clear();
		Thread t = new Thread(new GetCourseConnect(cCode));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {//return a null object if an errorOccurred
			return null;
		}
		return professors;
	}
	private static class GetCourseConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		private ArrayList<String> ccodeList = new ArrayList<String>();
		public GetCourseConnect(String cCode) {
			if(cCode != null) paramList.add("name1=" + cCode.trim().replaceAll(" ", "%20"));
			if(cCode != null) ccodeList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getProfessor.php" + convertParamList(paramList);
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
				if(jArray.length() > 0) {
					for(int i = 0;i<jArray.length();i++) {
						json_data = jArray.getJSONObject(i);
						String fName = json_data.getString("FirstName");
						String lName = json_data.getString("LastName");
						Professor currentProfessor = new Professor(fName, lName);//create the Professor

						String courseName = json_data.getString("CourseName");
						Course currentCourse;
						if(courseName.equals("NULL")) {
							currentCourse = new Course(null, json_data.getString("CourseCode"));
						}
						else {
							currentCourse = new Course(courseName, json_data.getString("CourseCode"));
						}

						currentProfessor.addCourse(currentCourse);
						professors.add(currentProfessor);
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
		}
	}

	//
	//get Evaluations
	//
	public static Evaluation getEvaluations(String fName, String lName, String cCode) {
		errorOccurred = false;
		Thread t = new Thread(new GetEvaluationsConnect(fName, lName, cCode));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {
			return null;
		}
		return Evaluation.mergeEvaluations(evals);
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
		}
	}

	//
	//getRatings
	//
	public static Rating getRating(String fName, String lName, String cCode) {
		errorOccurred = false;
		Thread t = new Thread(new GetRatingConnect(fName, lName, cCode));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {
			return null;
		}
		return rating;
	}
	private static class GetRatingConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public GetRatingConnect(String fName, String lName, String cCode) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
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
				//Only one Rating should be returned
				json_data = jArray.getJSONObject(0);
				rating = new Rating(json_data.getInt("Responded"));

				for(int j = 0;j<Rating.DB_FIELD_NAMES.length;j++) {
					rating.addResponseValue(json_data.getDouble(Rating.DB_FIELD_NAMES[j]));
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}

	//
	//setRatings
	//
	public static void setRatings(String fName, String lName, String cCode, Rating newRating, Rating oldRating) {
		errorOccurred = false;
		Thread t = new Thread(new SetRatingsConnect(fName, lName, cCode, newRating, oldRating));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {
			return;
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
		}
	}

	//
	//getComments
	//
	public static ArrayList<String> getComments(String fName, String lName, String cCode) {
		ArrayList<String> comments = new ArrayList<String>();
		errorOccurred = false;
		Thread t = new Thread(new GetCommentsConnect(fName, lName, cCode, comments));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
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
					String currentComment = json_data.getString("Comment");
					comments.add(currentComment);
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}


	//
	//postComment
	//
	public static void addComment(String fName, String lName, String cCode, String comment) {
		errorOccurred = false;
		Thread t = new Thread(new AddCommentConnect(fName, lName, cCode, comment));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {
			return;
		}
	}
	private static class AddCommentConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		public AddCommentConnect(String fName, String lName, String cCode, String comment) {
			paramList.add("fname=" + fName.trim().replaceAll(" ", "%20"));
			paramList.add("lname=" + lName.trim().replaceAll(" ", "%20"));
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			paramList.add("comment=" + comment.trim().replaceAll(" ", "%20").replaceAll("&", "%26").replaceAll("\"", "%22"));
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/addComment.php" + convertParamList(paramList);

			InputStream is = httpPost(postURL);
		}
	}

	//
	//getTextbooks
	//
	public static ArrayList<String> getTextbooks(String cCode) {
		ArrayList<String> textbooks = new ArrayList<String>();
		errorOccurred = false;
		Thread t = new Thread(new GetTextbooksConnect(cCode, textbooks));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		if(errorOccurred) {
			return null;
		}
		return textbooks;
	}
	private static class GetTextbooksConnect implements Runnable {
		private ArrayList<String> paramList = new ArrayList<String>();
		private ArrayList<String> textbooks = null;
		public GetTextbooksConnect(String cCode, ArrayList<String> textbooks) {
			paramList.add("ccode=" + cCode.trim().replaceAll(" ", "%20"));
			this.textbooks = textbooks;
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = scriptLocation + "/getTextbooks.php" + convertParamList(paramList);

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
					String currentComment = json_data.getString("Book");
					textbooks.add(currentComment);
				}
			}
			catch(JSONException e1) {
				errorOccurred = true;
				e1.printStackTrace();
			}
		}
	}
	
	//
	//check if a username and password corresponds to a UF Student
	//
	public static boolean isUFStudent(String username, String password) {
		errorOccurred = false;
		Thread t = new Thread(new TestShibboleth(username, password));
		t.start();

		try {
			t.join();
		}
		catch (InterruptedException e) {
			errorOccurred = true;
			e.printStackTrace();
		}
		return !errorOccurred;
	}
	private static class TestShibboleth implements Runnable {
		private String username = null;
		private String password = null;
		public TestShibboleth(String username, String password) {
			this.username = username;
			this.password = password;
		}
		@Override
		public void run() {
			//add parameters to the URL
			String postURL = "https://login.ufl.edu/idp/Authn/UserPassword";

			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(postURL);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("j_username", username));
				nameValuePairs.add(new BasicNameValuePair("j_password", password));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				is = entity.getContent();
				if(response.getStatusLine().getStatusCode() != 200) {
					Log.w("DBConnector", "HTTP Post server error: bad response");
				}
			}
			catch(Exception e) {
				errorOccurred = true;
				Log.e("DBConnector", "HTTP Post server error: death");
			}

			if(errorOccurred) {
				return;
			}

			String result = convertResponseToString(is);
			if(errorOccurred) {
				return;
			}
			if(result.contains("incorrect")) {
				errorOccurred = true;
			}
		}
	}
}
