package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Media;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;

import org.json.JSONObject;

import java.util.ArrayList;

public class MemoryActivity extends AppCompatActivity {

    private Context c;
    private int memoryID;
    private MemoryAdapter adapter;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        1);


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (resultCode != RESULT_OK) return;
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

                    /// use btemp Image file
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
                        Media m = new Media(id, memid, fileUrl);
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

    private void onMemoryDescriptionPress(View v){
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
