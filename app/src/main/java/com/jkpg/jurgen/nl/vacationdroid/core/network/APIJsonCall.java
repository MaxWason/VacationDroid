package com.jkpg.jurgen.nl.vacationdroid.core.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jurgen on 11/17/2015.
 */
public abstract class APIJsonCall extends AsyncTask<JsonObject, String, JsonObject> {

    String fullURL;
    String type;
    String token;
    public APIJsonCall(String urlSuffix, String type, Context c) {
        this.fullURL = "http://vacationrest-dev.elasticbeanstalk.com/api/v1/" + urlSuffix;
        this.type = type;

        SharedPreferences pref = c.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        token = pref.getString("token", "");
    }

    public abstract void JsonCallback(JSONObject obj);

    @Override
    protected JsonObject doInBackground(JsonObject... params) {
        try {
            URL url = new URL(fullURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("grant_token", token);
            JsonObject data = params[0];

            OutputStream ostream = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(ostream);
            writer.write(data.toString());
            writer.close();

            int response = connection.getResponseCode();
            //if it has been successfully created it will not return anything, so we create a message
            if(type == "POST" && response == 201) {
                JsonObject created = new JsonObject();
                created.addProperty("created", "element has been created successfully");
                return created;

            }

            InputStream istream = connection.getInputStream();
            Scanner scan = new Scanner(istream);
            String out = "";
            while (scan.hasNext()) {
                out += scan.next();
            }

            JsonObject json = new JsonObject();
            JsonParser parser = new JsonParser();
            json = (JsonObject) parser.parse(out);

            return json;

        } catch (Exception e) {
            Log.e("WEB ERROR", e.getMessage().toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JsonObject jsonObject) {

    }
}
