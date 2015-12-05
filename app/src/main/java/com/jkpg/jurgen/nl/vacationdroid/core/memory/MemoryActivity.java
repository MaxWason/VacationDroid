package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIPictureCall;
import com.squareup.picasso.Picasso;

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
