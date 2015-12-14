package com.jkpg.jurgen.nl.vacationdroid.core.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Jurgen on 11/26/2015.
 */

//APIPicturecall is always a POST, you can GET images with a jsoncall because it just returns a url
    //
public abstract class APIPictureCall extends AsyncTask<JsonObject, String, JsonObject> {

    String fullURL;
    String token;
    Bitmap image;
    int memid;

    String attachmentName = "file";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";

    public APIPictureCall(int MemoryId, Bitmap b, Context c) {
        this.fullURL = "http://vacationrest-dev.elasticbeanstalk.com/api/v1/memories/"+ MemoryId + "/pictures";

        memid = MemoryId;
        image = b;

        SharedPreferences pref = c.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        token = pref.getString("token", "");
    }

    public abstract void JsonCallback(JsonObject obj);

    @Override
    protected JsonObject doInBackground(JsonObject... params) {
        int responsecode = 0;
        try {
            String newUrl = fullURL;

            JsonObject obj = params[0];

            String fileUrl = obj.get("fileUrl").getAsString();
            String container = obj.get("container").getAsString();
            int width = obj.get("width").getAsInt();
            int height = obj.get("height").getAsInt();

            newUrl += "?fileUrl="+fileUrl+"&container="+container+"&width="+width+"&height="+height;


            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);
            connection.addRequestProperty("Authorization", "Bearer " + token);

            DataOutputStream request = new DataOutputStream(
                    connection.getOutputStream());

            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    this.attachmentName + "\";filename=\"" +
                    fileUrl + "\"" + this.crlf);
            request.writeBytes(this.crlf);

            image.compress(Bitmap.CompressFormat.JPEG, 90, request);

            request.writeBytes(this.crlf);
            request.writeBytes(this.twoHyphens + this.boundary +
                    this.twoHyphens + this.crlf);

            request.flush();
            request.close();

            int response = connection.getResponseCode();

            Log.d("Network call", fullURL + " - " + response + ": " + connection.getResponseMessage());
            responsecode = response;

            //if it has been successfully created it will not return anything, so we create a message
            if(response == 201 || response == 200) {
                JsonObject created = new JsonObject();
                Log.d("Network", "post call returned 201 or 202");
                created.addProperty("created", "element has been created successfully");
                return created;
            } else {
                Log.e("WEB ERROR", connection.getResponseMessage() + " " + response);
                return null;
            }


        } catch (Exception e) {
            Log.e("WEB ERROR", e.getMessage().toString() + " code: " + responsecode);
            return null;
        }
    }

    @Override
    protected void onPostExecute(JsonObject jsonObject) {
        JsonCallback(jsonObject);
    }
}
