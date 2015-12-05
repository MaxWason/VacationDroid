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
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

public class VacationActivity extends AppCompatActivity {

    private GridView gridview=null;
    private Context mContext;
    private int vacID;





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
        final TextView vtitle = (TextView)findViewById(R.id.vacationTitle);
        final TextView vplace = (TextView)findViewById(R.id.vacationPlace);
        final TextView vdate = (TextView)findViewById(R.id.vacationDate);
        final TextView vdesc = (TextView)findViewById(R.id.vacationDescription);

        DBConnection db = new DBConnection(this);
        Vacation v = db.getVacationById(vacID);

        vtitle.setText(v.title);
        vplace.setText(v.place);
        vdate.setText(v.start + " to " + v. end);
        vdesc.setText(v.description);


        APIJsonCall memcall = new APIJsonCall("vacations/"+vacID+"/memories", "GET", this) {//3 is id for Antoine's first vacation
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    JsonArray arrMemories = obj.getAsJsonArray("list");
                    Log.d("MEMORYLIST", arrMemories.toString());
                    gridview.setAdapter(new VacationAdapter(mContext, gridview, arrMemories, a, vacID));

                    DBConnection db = new DBConnection(a);
                    ArrayList<Memory> memories = new ArrayList<>();
                    Gson gson = new Gson();
                    for(JsonElement el:arrMemories) {
                        Memory m = gson.fromJson(el, Memory.class);
                        m.vacationid = vacID;
                        db.addOrUpdateMemory(m);
                    }


                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        memcall.execute(new JsonObject());


    }


}
