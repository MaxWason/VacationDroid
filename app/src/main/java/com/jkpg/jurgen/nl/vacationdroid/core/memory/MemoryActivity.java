package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class MemoryActivity extends AppCompatActivity {

    private Context c;
    private String title;
    private String desc;
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
                    /// use btemp Image file
                    Log.d("Image", bitmap.toString());
                    APIPictureCall picture = new APIPictureCall(memoryID, bitmap, this) {
                        @Override
                        public void JsonCallback(JsonObject obj) {
                            Log.d("IMGJASON", obj.toString());
                        }
                    };
                    picture.execute(new JsonObject());
                }
                break;
        }

    }

    protected void onStart() {
        super.onStart();
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new MemoryAdapter(c, gridview, this, memoryID);

        gridview.setAdapter(adapter);


        final Activity a = this;
        APIJsonCall memcall = new APIJsonCall("memories/" + memoryID, "GET", this) {//get the info from the memory
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    title = obj.get("title").getAsString();
                    Log.d("TITLE", title);
                    a.setTitle(title);
                    desc = obj.get("description").getAsString() + " in " + obj.get("place").getAsString()
                            + " at  " + obj.get("time").getAsString();
                    Log.d("DESCRIPTION", desc);
                    TextView descView = (TextView) findViewById(R.id.MemoryDescription);
                    descView.setText(desc);
                } catch (Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        memcall.execute(new JsonObject());
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


}
