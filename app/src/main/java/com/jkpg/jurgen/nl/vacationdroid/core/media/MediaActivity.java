package com.jkpg.jurgen.nl.vacationdroid.core.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.jkpg.jurgen.nl.vacationdroid.R;
import com.squareup.picasso.Picasso;

public class MediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        this.setTitle("Media");

        editViews();
    }

    private void editViews(){
        Intent intent = getIntent();
        String s = intent.getStringExtra("url");
        Log.d("IMAGE ID", s);
        TextView tv = (TextView) findViewById(R.id.txtView);
        ImageView iv = (ImageView) findViewById(R.id.imgView);
        VideoView vv = (VideoView) findViewById(R.id.mediaView);

        Picasso.with(this)
                .load(s)
                .into(iv);

//        iv.setVisibility(View.GONE);
        vv.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
//        tv.setText(s);
    }
}
