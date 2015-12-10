package com.jkpg.jurgen.nl.vacationdroid.core.vacation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.memory.MemoryActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Media;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;

import java.util.ArrayList;

/**
 * Created by Antoine on 09/11/2015.
 */
    public class VacationAdapter extends BaseAdapter {

        private Context mContext;
        private Activity a;
        private int vacID;
        private GridView gv;
        private ArrayList<String> am = new ArrayList<String>();
        private ArrayList<Memory> memorylist = new ArrayList<Memory>();

        public VacationAdapter(Context c, GridView gridview, Activity ac, int vacId) {
            a = ac;
            mContext = c;
            gv=gridview;
            vacID=vacId;


            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {
                    goToMemory(position, a);
                }
            });

            updateView();
        }

    public void updateView() {
        DBConnection db = new DBConnection(mContext);
        memorylist = db.getMemoriesByVacation(vacID);
        am = new ArrayList<>();
        for(Memory m:memorylist) {
            am.add(m.title);
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

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,R.drawable.placeholder2,R.drawable.placeholder,
        };

        public void goToMemory(int position, Activity a){
            final int positioN = position;
            final Activity ac = a;
            final Intent intent = new Intent(a, MemoryActivity.class);


            DBConnection db = new DBConnection(a);
            ArrayList<Memory> mems = db.getMemoriesByVacation(vacID);

            Log.d("MEMORY",mems.size() + "");
            int memoryId = mems.get(position).id;
            Log.d("ID", "" + memoryId);
            intent.putExtra("id", memoryId);
            ac.startActivity(intent);

        }

        private void deleteMemory(int pos,GridView gridview){
            DBConnection db = new DBConnection(a);
            final int memoryId = db.getMemoriesByVacation(vacID).get(pos).id;
            Log.d("position", pos+"");
            Log.d("idmemory",memoryId+"");

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage("Are you sure you want to delete this memory ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            APIJsonCall memcall = new APIJsonCall("memories/"+memoryId, "DELETE", mContext) {
                                @Override
                                public void JsonCallback(JsonObject obj) {
                                    Toast.makeText(mContext, "  Memory deleted  ", Toast.LENGTH_LONG).show();
                                    //update local db here
                                }
                            };
                            memcall.execute(new JsonObject());
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // Create the AlertDialog object and return it
            builder.show();
            setNormalClickEvent(gridview);
        }

        public void setDeletionClickEvent(final GridView gridview){
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {
                    deleteMemory(position, gridview);
                }
            });
        }

        public void setNormalClickEvent(GridView gridview){
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {
                    goToMemory(position, a);
                }
            });
        }
    }
