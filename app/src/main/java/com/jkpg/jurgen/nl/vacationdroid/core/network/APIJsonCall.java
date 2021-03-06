package com.jkpg.jurgen.nl.vacationdroid.core.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
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

    public abstract void JsonCallback(JsonObject obj);

    @Override
    protected JsonObject doInBackground(JsonObject... params) {
        int responsecode = 0;
        try {
            URL url = new URL(fullURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            JsonObject data = params[0];

            if(type == "POST" || type == "PUT") {
                OutputStream ostream = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(ostream);
                writer.write(data.toString());
                writer.close();
            }

            int response = 0;
            try {
                response = connection.getResponseCode();
            } catch(Exception e) {
                JsonObject created = new JsonObject();
                Log.d("Network", "no network connection");
                created.addProperty("error", "no network");
                return created;
            }
            Log.d("Network call", fullURL + " - " + response + ": " + connection.getResponseMessage());
            responsecode = response;

            if(response == 400) {
                JsonObject created = new JsonObject();
                Log.d("Network", "post call returned 400");
                created.addProperty("error", "faulty request");
                return created;
            }
            //if it has been successfully created it will not return anything, so we create a message
            if(type == "POST" && (response == 201 || response == 200)) {
                JsonObject created = new JsonObject();
                Log.d("Network", "post call returned 201");
                created.addProperty("created", "element has been created successfully");
                return created;

            }
            if(type == "PUT" && response == 200) {
                JsonObject created = new JsonObject();
                Log.d("Network", "put call returned 200");
                created.addProperty("updated", "element has been updated successfully");
                return created;
            }

            if(type == "DELETE" && response == 200) {
                JsonObject created = new JsonObject();
                Log.d("Network", "delete call returned 200");
                created.addProperty("deleted", "element has been deleted successfully");
                return created;
            }

            InputStream istream = connection.getInputStream();
            Scanner scan = new Scanner(istream);
            String out = "";
            while (scan.hasNext()) {
                out += scan.nextLine();
            }

            JsonElement json;
            JsonParser parser = new JsonParser();
            json = parser.parse(out);
            connection.disconnect();

            if(json.isJsonArray()) {
                JsonObject j = new JsonObject();
                j.add("list", json);
                return j;
            } else {
                return json.getAsJsonObject();
            }


        } catch (Exception e) {
            try {
                Log.e("WEB ERROR", e.getMessage() + " code: " + responsecode);
            } catch (Exception ex){
                Log.e("WEB ERROR", "No error message received!" + " code: " + responsecode);
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(JsonObject jsonObject) {
        JsonCallback(jsonObject);
    }
}
