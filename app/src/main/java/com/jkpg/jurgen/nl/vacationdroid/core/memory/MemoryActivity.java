package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIPictureCall;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APISoundCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Media;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MemoryActivity extends AppCompatActivity {

    private Context c;
    private int memoryID;
    private MemoryAdapter adapter;
    private final int RETURN_CODE_IMG = 0;
    private final int RETURN_CODE_AUD = 1;
    private final int RETURN_CODE_VID = 2;
    private GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;

        Intent intent = getIntent();
        memoryID = intent.getIntExtra("id", -1);
        Log.d("IDMEMORY", memoryID + "");

        setContentView(R.layout.memory_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Activity a = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent();

                AlertDialog.Builder builder = new AlertDialog.Builder(a);
                String[] ar = {"Picture", "Sound", "Video"};
                builder.setTitle("Choose file type")
                        .setItems(ar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case (0):
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);

                                        startActivityForResult(
                                                Intent.createChooser(intent, "Complete action using"),
                                                RETURN_CODE_IMG);


                                        break;
                                    case (1):
                                        intent.setType("audio/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);

                                        startActivityForResult(
                                                Intent.createChooser(intent, "Complete action using"),
                                                RETURN_CODE_AUD);


                                        break;
                                    case (2):
                                        intent.setType("video/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);

                                        startActivityForResult(
                                                Intent.createChooser(intent, "Complete action using"),
                                                RETURN_CODE_VID);
                                        break;
                                }
                            }
                        });
                builder.show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case (RETURN_CODE_IMG):
                switch (resultCode) {

                    case RESULT_OK:
                        if (data != null) {

                            Uri uri = data.getData();
                            Bitmap bitmap;

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            } catch (Exception e) {
                                bitmap = null;
                                Log.e("FILE", e.getMessage());
                            }

                            JsonObject json = new JsonObject();
                            json.addProperty("fileUrl", "file.jpg");
                            json.addProperty("container", uri.getPath());
                            json.addProperty("width", bitmap.getWidth());
                            json.addProperty("height", bitmap.getHeight());

                            Log.d("Image", bitmap.toString());
                            APIPictureCall picture = new APIPictureCall(memoryID, bitmap, this) {
                                @Override
                                public void JsonCallback(JsonObject obj) {
                                    Log.d("IMGJASON", obj.toString());
                                }
                            };
                            picture.execute(json);
                        }
                        break;
                }
                break;
            case (RETURN_CODE_AUD):
            case RESULT_OK:
                if (data != null) {

                    Uri uri = data.getData();
                    File audio;


                    Cursor cursor = null;
                    try {
                        String[] proj = {MediaStore.Audio.Media.DATA};
                        cursor = this.getContentResolver().query(uri, proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);
                        cursor.moveToFirst();
                        String path = cursor.getString(column_index);
                        audio = new File(path);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                    JsonObject json = new JsonObject();
                    json.addProperty("fileUrl", "sound.mp3");
                    json.addProperty("container", uri.getPath());
                    json.addProperty("duration", "100");
                    json.addProperty("codec", "AAC");
                    json.addProperty("bitrate", "320");
                    json.addProperty("channels", "2");
                    json.addProperty("samplingRate", "320");

                    Log.d("Image", audio.toString());
                    APISoundCall picture = new APISoundCall(memoryID, audio, this) {
                        @Override
                        public void JsonCallback(JsonObject obj) {

                        }
                    };
                    picture.execute(json);
                }
                break;
            case (RETURN_CODE_VID):

                break;
        }


    }


    protected void onStart() {
        super.onStart();
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new MemoryAdapter(c, gridview, this, memoryID);

        gridview.setAdapter(adapter);

        DBConnection db = new DBConnection(this);
        Memory m = db.getMemoryById(memoryID);

        String title = m.title;
        String desc = m.description + " in " + m.place + " at  " + m.time;

        TextView descView = (TextView) findViewById(R.id.MemoryDescription);

        descView.setText(desc);

        setTitle(title);


        final Activity a = this;

        final Context c = this;
        APIJsonCall filescall = new APIJsonCall("memories/" + memoryID + "/media-objects", "GET", this) {//get the list of medias for a given memory
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    JsonArray arrFiles = obj.getAsJsonArray("list");
                    Log.d("FILESLIST", arrFiles.toString());

                    DBConnection db = new DBConnection(c);

                    ArrayList<Media> medias = new ArrayList<>();
                    for (JsonElement el : arrFiles) {
                        JsonObject e = el.getAsJsonObject();
                        int id = e.get("id").getAsInt();
                        int memid = memoryID;
                        String fileUrl = e.get("fileUrl").getAsString();
                        String type = "";
                        if (e.has("channels")) {
                            type = "sound";
                        }
                        if (e.has("width")) {
                            type = "picture";
                        }
                        if (e.has("frameRate")) {
                            type = "video";
                        }
                        Media m = new Media(id, memid, fileUrl, type);
                        db.addOrUpdateMedia(m);
                    }
                    adapter.updateView();
                    gridview.invalidateViews();

                } catch (Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        filescall.execute(new JsonObject());

    }

    private void onMemoryDescriptionPress(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Edit Description");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String desc = input.getText().toString();

                TextView descView = (TextView) findViewById(R.id.MemoryDescription);
                descView.setText(desc);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled. Do nothing.
            }
        });

        alert.show();
    }


}
