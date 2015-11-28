package com.jkpg.jurgen.nl.vacationdroid.core.vacation;

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


            final int vacid=3;//first Antoine's vacation
            APIJsonCall memcall = new APIJsonCall("vacations/"+vacid+"/memories", "GET", this) {
                @Override
                public void JsonCallback(JsonObject obj) {
                    try {
                        Log.d("MEMORYLIST", obj.toString());
                        JsonArray arrMemories = obj.getAsJsonArray("list");
                        JsonObject ml = arrMemories.get(0).getAsJsonObject();
                        gridview.setAdapter(new VacationAdapter(mContext, gridview, ml));
                    } catch(Exception E) {
                        Log.e("WEB ERROR", E.getMessage());
                    }
                }
            };
             memcall.execute(new JsonObject());

        Intent intent = getIntent();
        String title = intent.getStringExtra("vacationTitle");
        String desc = intent.getStringExtra("vacationDesc");
        this.setTitle(title);
        TextView descView = (TextView) findViewById(R.id.VacationDescription);
        descView.setText(desc);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                goToMemoryList(position);
            }
        });
    }

    public void goToMemoryList(int position){
        Intent intent = new Intent(VacationActivity.this, MemoryListActivity.class);
        intent.putExtra("id", (int)1);
        startActivity(intent);
    }


    private String title;
    private String desc;
    private GridView gridview=null;
    private Context mContext;
}
