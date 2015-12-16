package com.jkpg.jurgen.nl.vacationdroid.core.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jurgen on 12/13/2015.
 */
public abstract class APIVideoCall extends AsyncTask<JsonObject, String, JsonObject> {

    String fullURL;
    String token;
    File video;
    int memid;
    Context c;

    String attachmentName = "file";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";

    public APIVideoCall(int MemoryId, File f, Context c) {
        this.fullURL = "http://vacationrest-dev.elasticbeanstalk.com/api/v1/memories/"+ MemoryId + "/videos";

        memid = MemoryId;
        video = f;
        this.c = c;
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
            String vcodec = obj.get("videoCodec").getAsString();
            int vbr = obj.get("videoBitRate").getAsInt();
            int width = obj.get("width").getAsInt();
            int height = obj.get("height").getAsInt();
            int frames = obj.get("frameRate").getAsInt();
            String acodec = obj.get("audioCodec").getAsString();
            int abr = obj.get("audioBitRate").getAsInt();
            int channels = obj.get("channels").getAsInt();
            int samplingRate = obj.get("samplingRate").getAsInt();
            //bitrate, channels, samplingRate
            newUrl +=
                    "?fileUrl="+fileUrl+
                            "&container="+container +
                            "&videoCodec="+vcodec +
                            "&videoBitRate="+vbr +
                            "&width="+width +
                            "&height="+height+
                            "&frameRate="+frames+
                            "&audioCodec="+acodec+
                            "&audioBitRate="+abr+
                            "&channels="+channels +
                            "&samplingRate="+samplingRate;


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

            FileInputStream fis = new FileInputStream(video);

            byte[] bytes = new byte[(int)video.length()];
            fis.read(bytes);
            request.write(bytes);


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
                Log.d("Network", "video post call returned 201 or 200");
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