package com.jkpg.jurgen.nl.vacationdroid.core.memoryList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.memory.MemoryActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationAdapter;

public class MemoryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;

        Intent intent = getIntent();
        memoryID = intent.getIntExtra("id",-1);
        Log.d("IDMEMORY", memoryID+"");

        setContentView(R.layout.memory_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

        protected void onStart(){
            super.onStart();
            final Activity a = this;
        APIJsonCall memcall = new APIJsonCall("memories/"+memoryID, "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    title = obj.get("title").getAsString();
                    Log.d("TITLE", title);
                    a.setTitle(title);
                    desc = obj.get("description").getAsString() + " in "+ obj.get("place").getAsString()
                            +" at  "+obj.get("time").getAsString();
                    Log.d("DESCRIPTION", desc);
                    TextView descView = (TextView) findViewById(R.id.MemoryDescription);
                    descView.setText(desc);
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        memcall.execute(new JsonObject());


        GridView gridview = (GridView) findViewById(R.id.gridview);
        //gridview.setAdapter(new VacationAdapter(this, gridview));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                goToMemoryActivity();
            }
        });
    }

    private void goToMemoryActivity(){
        Intent intent = new Intent(MemoryListActivity.this, MemoryActivity.class);
        intent.putExtra("test","blblblblblblbl");
        intent.putExtra("fileName",getMemoryFileName());
        startActivity(intent);
    }

    private String getMemoryFileName(){
        return "Cool file name";
    }

    private Context c;
    private String title;
    private String desc;
    private int memoryID;
}
