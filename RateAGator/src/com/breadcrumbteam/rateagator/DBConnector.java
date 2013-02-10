package com.breadcrumbteam.helloworld;

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

import android.net.ParseException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DBConnector {
	static String text = null;
	private static JSONArray jArray;
	private static String result = null;
	private static InputStream is = null;
	private static StringBuilder sb = null;
	public static String ipAddress = "10.34.183.57";
	public static String scriptLocation = "http://" + ipAddress + "/home/RateAGator";
	public static ArrayList<String> names = new ArrayList<String>();
	
	public static String getTestConnection() throws InterruptedException {
		long patience = 4000;
		long startTime = System.currentTimeMillis();
		Thread t = new Thread(new TestConnection());
        t.start();

        //threadMessage("Waiting for MessageLoop thread to finish");
        while (t.isAlive()) {	// loop until MessageLoop thread exits
            //threadMessage("Still waiting...");
            t.join(1000);		// Wait maximum of 1 second for MessageLoop thread to finish
            if (((System.currentTimeMillis() - startTime) > patience)
                  && t.isAlive()) {
                //threadMessage("Tired of waiting!");
                t.interrupt();
                // Shouldn't be long now -- wait indefinitely
                t.join();
            }
        }
        //threadMessage("Finally!");
        text = names.toString();
        return text;
	}
	
	private static class TestConnection implements Runnable {
		@Override
		public void run() {
			ArrayList<NameValuePair> namevaluepairs = new ArrayList<NameValuePair>();
			try
			{
				//http post
				HttpClient httpclient = new DefaultHttpClient();
				
				HttpPost httppost = new HttpPost(scriptLocation + "/test.php");
				//HttpPost httppost = new HttpPost("http://www.google.com");
				
				//httppost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
				
				HttpResponse response = httpclient.execute(httppost);
				
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				if (response.getStatusLine().getStatusCode() != 200) 
				{
					Log.d("MyApp", "Server encountered an error");
				}
			}
			catch(Exception e)
			{
				//TODO figure out what to do here instead
				//getBaseContext() is only useful in Main Activity
				//Toast.makeText(getBaseContext(),e.toString() ,Toast.LENGTH_LONG).show();
				
			}
			
			//Convert response to String, set result
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line = null;

				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			}
			catch(Exception e)
			{
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			
			try
			{
				jArray = new JSONArray(result);
				JSONObject json_data=null;
				for(int i=0;i<jArray.length();i++)
				{
					json_data = jArray.getJSONObject(i);
					names.add(json_data.getString("name"));
					result += "\n" + jArray.getJSONObject(i); 
				}
			}
			catch(JSONException e1)
			{
				e1.printStackTrace();
			}
			catch (ParseException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	
	
}
