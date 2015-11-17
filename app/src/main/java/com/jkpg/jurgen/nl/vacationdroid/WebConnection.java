package com.jkpg.jurgen.nl.vacationdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class WebConnection {

    Context context;
    String baseURL = "http://localhost:49207/api/v1/";
    //String baseURL = "http://vacationrest-dev.elasticbeanstalk.com/api/v1/";
    String loginURL = baseURL + "/token";
    String registerURL = baseURL + "/accounts";
    String token;
    SharedPreferences pref;
    Gson gson = new Gson();
    String output;


    public WebConnection(Context c) {
        context = c;
        pref = c.getSharedPreferences("vacationDroid", c.MODE_PRIVATE);
    }

    public void setToken(String token) {
        this.token = token;
    }
    public boolean isTokenSet() {
        return (token != null);
    }


    //returns access token if valid, else returns null
    public String APILogin(String username, String pw) {

        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    //prep connection
                    URL url = new URL(loginURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    //fill body
                    OutputStream formdatastream = connection.getOutputStream();
                    OutputStreamWriter formwriter = new OutputStreamWriter(formdatastream);
                    String formdata = "username="+params[0]+"&password="+params[1]+"&grant_type=password";
                    formwriter.write(formdata);
                    formwriter.close();

                    //send response
                    int status = connection.getResponseCode();
                    InputStream stream = connection.getInputStream();
                    Scanner scan = new Scanner(stream);
                    String out = "";
                    while(scan.hasNext()) {
                        out += scan.next();
                    }
                    scan.close();
                    JsonObject json = new JsonObject();
                    json = (JsonObject)new JsonParser().parse(out);
                    String token = json.get("token").getAsString();

                    return token;

                } catch(Exception e) {
                    Log.e("WEB ERROR", e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                output = s;
            }
        };
        task.execute(username, pw);
        String token = output;
        return token;

}

    //returns access token
    public String APIRegister(String username, String pw, String email) {
        try {
            URL url = new URL(registerURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");

            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("password", pw);
            data.addProperty("email", email);

            OutputStream ostream = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(ostream);
            writer.write(data.getAsString());
            writer.close();

            int response = connection.getResponseCode();
            InputStream istream = connection.getInputStream();
            Scanner scan = new Scanner(istream);
            String out = "";
            while(scan.hasNext()) {
                out += scan.next();
            }

            String token = APILogin(username, pw);
            
            return token;

        } catch(Exception e) {
            Log.e("WEB ERROR", e.getMessage());
            return null;
        }
    }



}
