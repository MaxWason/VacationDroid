package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.jkpg.jurgen.nl.vacationdroid.R;

public class MemoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_activity);
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

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        this.setTitle(fileName);


        editViews();
    }

    private void editViews(){
        Intent intent = getIntent();
        String s = intent.getStringExtra("test");

        TextView tv = (TextView) findViewById(R.id.txtView);
        ImageView iv = (ImageView) findViewById(R.id.imgView);
        VideoView vv = (VideoView) findViewById(R.id.mediaView);

        iv.setVisibility(View.GONE);
        vv.setVisibility(View.GONE);
        tv.setText(s);
    }
}
