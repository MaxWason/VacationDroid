package com.jkpg.jurgen.nl.vacationdroid.core.vacation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.jkpg.jurgen.nl.vacationdroid.core.memoryList.MemoryListActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;

import java.util.ArrayList;

/**
 * Created by Antoine on 09/11/2015.
 */
    public class VacationAdapter extends BaseAdapter {

        private Context mContext;
        private Activity a;
        private int memoryId;
        private GridView gv;
        private ArrayList<String> am = new ArrayList<String>();

        public VacationAdapter(Context c, GridView gridview, JsonArray arrMemory, Activity ac) {
            a = ac;
            mContext = c;
            gv=gridview;
            if (arrMemory != null) {
                for (int i=0;i<arrMemory.size();i++){
                    am.add(arrMemory.get(i).toString());
                }
            }

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {
                    goToMemoryList(position,a);
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

        public void goToMemoryList(int position, Activity a){
            final int positioN = position;
            final Activity ac = a;
            final Intent intent = new Intent(a, MemoryListActivity.class);
            APIJsonCall memcall = new APIJsonCall("vacations/"+3+"/memories", "GET", a) {//3 is id for Antoine's first vacation
                @Override
                public void JsonCallback(JsonObject obj) {
                    try {
                        JsonArray arrMemories = obj.getAsJsonArray("list");
                        JsonObject ml = arrMemories.get(positioN).getAsJsonObject();
                        Log.d("MEMORY", ml.toString());
                        memoryId = ml.get("id").getAsInt();
                        Log.d("ID", "" + memoryId);
                        intent.putExtra("id", memoryId);
                        ac.startActivity(intent);
                    } catch(Exception E) {
                        Log.e("WEB ERROR", E.getMessage());
                    }
                }
            };
            memcall.execute(new JsonObject());


        }
    }
