package com.jkpg.jurgen.nl.vacationdroid.core.memory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.media.MediaActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Media;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Antoine on 29/11/2015.
 */

public class MemoryAdapter extends BaseAdapter {

    private Context mContext;
    private Activity a;
    private int memoryID;
    private GridView gv;
    private ArrayList<String> am = new ArrayList<String>();
    private ArrayList<Media> medialist = new ArrayList<>();

    public MemoryAdapter(Context c, GridView gridview, Activity ac, int memoryId) {
        a = ac;
        mContext = c;
        this.memoryID = memoryId;
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                goToMemoryActivity(position,a);
            }
        });
        updateView();
    }

    public void updateView() {
        DBConnection db = new DBConnection(mContext);
        medialist = db.getMediasByMemory(memoryID);
        am = new ArrayList<>();
        for(Media m:medialist) {
            am.add(m.fileurl);
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return am.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if(medialist.get(position).type.equals("picture"))
            Picasso.with(mContext).load(medialist.get(position).fileurl).into(imageView);
        else {
            imageView.setImageResource(mThumbIds[position]);
        }

        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,
    };

    public void goToMemoryActivity(int position, Activity a){
        final int positioN = position;
        final Activity ac = a;

        DBConnection db = new DBConnection(ac);
        ArrayList<Media> medias = db.getMediasByMemory(memoryID);

        Intent intent = new Intent(a, MediaActivity.class);

        intent.putExtra("url", medias.get(position).fileurl);
        intent.putExtra("type", medias.get(position).type);
        ac.startActivity(intent);

    }

}

