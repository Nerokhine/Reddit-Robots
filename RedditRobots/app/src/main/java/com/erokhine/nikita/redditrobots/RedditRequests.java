package com.erokhine.nikita.redditrobots;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Nikita on 2016-07-16.
 */


public class RedditRequests extends AsyncTask<RequestsParameters, Integer, String> {
    private DisplayMessageActivity activity;
    private String method;


    protected String doInBackground(RequestsParameters ... parameters) {
        RequestsParameters params = parameters[0];

        String url = params.url;
        String output = "";
        //this.type = params.type;
        this.method = params.method;
        this.activity = params.activity;

        try {
            if(activity.stop) {
                Thread.sleep(activity.stopTime);
            }
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            if(params.type.equals("post")) {
                con.setRequestMethod("POST");
            }else{
                con.setRequestMethod("GET");
            }

            //headers
            for(int x = 0; x < params.headers.length; x++){
                con.setRequestProperty(params.headers[x].name, params.headers[x].value);
            }


            String urlParameters = params.payload;

            if(params.type.equals("post")) {
                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }

            //int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print num requests left and sleep if you are going over

            System.out.println("Remaining: " + con.getHeaderField("X-Ratelimit-Remaining"));
            System.out.println("Time till reset: " + con.getHeaderField("X-Ratelimit-Reset"));
            if(con.getHeaderField("X-Ratelimit-Remaining") != null && Double.parseDouble(con.getHeaderField("X-Ratelimit-Remaining")) < 10){
                activity.stop = true;
                activity.stopTime = Integer.parseInt(con.getHeaderField("X-Ratelimit-Reset")) * 1000;
            }

            output = response.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //return result

        return output;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(String result) {
        activity.numActiveThreads -= 1;

        //showDialog("Downloaded " + result + " bytes");
        //Intent intent = getIntent();
        //try {
            /*if(this.type.equals("post")){
                activity.updateText(result);
            }else{
                activity.updateText2(result);
            }*/
        /*java.lang.reflect.Method method;
        try {
            method = activity.getClass().getMethod(this.method);
            method.invoke(result);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }*/
        try {
            if(this.method.equals("updateText")){
                activity.updateText(result);
            }else if(this.method.equals("updateText2")){
                activity.updateText2(result);
            }else if(this.method.equals("parseCommentThreadMain")){
                JSONArray obj = new JSONArray(result);
                result = obj.getJSONObject(1).getJSONObject("data").getString("children");
                activity.linkID = obj.getJSONObject(0).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("name");
                activity.parseCommentThread(result);
                activity.updateTextView();
                System.out.println("Num active threads: " + activity.numActiveThreads);
                activity.finished = true;
            }else if(this.method.equals("parseCommentThreadMore")){
                JSONObject obj = new JSONObject(result);
                result = obj.getJSONObject("json").getJSONObject("data").getString("things");
                activity.parseCommentThread(result);
                activity.updateTextView();
            }else{
                System.out.println(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}
    }
}
