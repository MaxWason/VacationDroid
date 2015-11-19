package com.jkpg.jurgen.nl.vacationdroid.core.network;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jurgen on 11/17/2015.
 */
public abstract class APITokenCall extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        loginCallback(s);
    }

    public abstract void loginCallback (String token);

    @Override
    protected String doInBackground(String... params) {
        try {
            //prep connection
            String apiurl = "http://vacationrest-dev.elasticbeanstalk.com/api/v1/token";
            URL url = new URL(apiurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
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
            if(json.has("access_token")){
                return json.get("access_token").getAsString();
            }

            return null;

        } catch(Exception e) {
            Log.e("WEB ERROR", e.getMessage());
            return null;
        }
    }
}
