package com.jkpg.jurgen.nl.vacationdroid.core.memoryList;

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
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.memory.MemoryActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;

import java.util.ArrayList;

/**
 * Created by Antoine on 29/11/2015.
 */

public class MemoryListAdapter extends BaseAdapter {

    private Context mContext;
    private Activity a;
    private int memoryID;
    private GridView gv;
    private ArrayList<String> am = new ArrayList<String>();

    public MemoryListAdapter(Context c, GridView gridview, JsonArray arrFiles, Activity ac, int memoryId) {
        a = ac;
        mContext = c;
        gv=gridview;
        memoryID=memoryId;
        if (arrFiles != null) {
            for (int i=0;i<arrFiles.size();i++){
                am.add(arrFiles.get(i).toString());
            }
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                goToMemoryActivity(position,a);
            }
        });
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,
    };

    public void goToMemoryActivity(int position, Activity a){
        final int positioN = position;
        final Activity ac = a;
        final Intent intent = new Intent(a, MemoryActivity.class);
        APIJsonCall filecall = new APIJsonCall("memories/"+memoryID+"/media-objects", "GET", a) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    JsonArray arrFiles = obj.getAsJsonArray("list");
                    JsonObject ml = arrFiles.get(positioN).getAsJsonObject();
                    Log.d("FILE", ml.toString());
                    final String fileUrl = ml.get("fileUrl").getAsString();
                    Log.d("url", "" + fileUrl);
                    intent.putExtra("url", fileUrl);
                    ac.startActivity(intent);
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        filecall.execute(new JsonObject());

    }

}

