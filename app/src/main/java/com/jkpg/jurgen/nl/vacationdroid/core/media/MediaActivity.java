package com.jkpg.jurgen.nl.vacationdroid.core.media;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.jkpg.jurgen.nl.vacationdroid.R;
import com.squareup.picasso.Picasso;

public class MediaActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    MediaPlayer player;

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


    }

    @Override
    protected void onStart() {
        super.onStart();
        editViews();
    }

    private void editViews(){
        Intent intent = getIntent();
        String s = intent.getStringExtra("url");
        String type = intent.getStringExtra("type");
        Log.d("IMAGE ID", s);
        ImageView iv = (ImageView) findViewById(R.id.imgView);
        VideoView vv = (VideoView) findViewById(R.id.mediaView);

        if(type.equals("picture")) {
            Picasso.with(this)
                    .load(s)
                    .into(iv);

//        iv.setVisibility(View.GONE);
            vv.setVisibility(View.GONE);
        }
        if(type.equals("sound")) {
            try {

                player = new MediaPlayer();

                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Uri uri = Uri.parse(s);
                player.setDataSource(this, uri);
                player.prepareAsync();


            } catch (Exception e) {
                // TODO: handle exception
            }
            iv.setVisibility(View.GONE);
            vv.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
