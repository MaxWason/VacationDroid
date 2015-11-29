package com.jkpg.jurgen.nl.vacationdroid.core.vacation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.memoryList.MemoryListActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;

public class VacationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Intent intent = getIntent();
        vacID = intent.getIntExtra("id",-1);
        Log.d("IDVACATION", vacID+"");


        setContentView(R.layout.vacation_activity);
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


        gridview = (GridView) findViewById(R.id.gridview);
    }

    protected void onStart(){
        super.onStart();
        final Activity a = this;
        APIJsonCall vaccall = new APIJsonCall("vacations/"+vacID, "GET", this) {//3 is id for Antoine's first vacation
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("VACATION", obj.toString());
                    title = obj.get("title").getAsString();
                    a.setTitle(title);
                    desc = obj.get("description").getAsString() + " at "+ obj.get("place").getAsString()
                            +". Date : "+obj.get("start").getAsString()+" / "+obj.get("end").getAsString();
                    TextView descView = (TextView) findViewById(R.id.VacationDescription);
                    descView.setText(desc);
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        vaccall.execute(new JsonObject());

        APIJsonCall memcall = new APIJsonCall("vacations/"+vacID+"/memories", "GET", this) {//3 is id for Antoine's first vacation
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    JsonArray arrMemories = obj.getAsJsonArray("list");
                    Log.d("MEMORYLIST", arrMemories.toString());
                    gridview.setAdapter(new VacationAdapter(mContext, gridview, arrMemories, a, vacID));
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        memcall.execute(new JsonObject());


    }

    private String title;
    private String desc;
    private GridView gridview=null;
    private Context mContext;
    private int vacID;
}
