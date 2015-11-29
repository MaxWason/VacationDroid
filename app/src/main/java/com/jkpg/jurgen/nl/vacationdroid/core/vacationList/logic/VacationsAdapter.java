package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

public class VacationsAdapter extends ArrayAdapter<Vacation> {

    Context context;
    ArrayList<Vacation> data;
    int viewid;

    public VacationsAdapter(Context context, int viewID, ArrayList<Vacation> data) {
        super(context, viewID, data);
        this.context = context;
        this.data = data;
        this.viewid = viewID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(viewid, parent, false);
        }

        Vacation v = data.get(position);

        if (v != null){

            //actual items
            ((TextView)view.findViewById(R.id.dashNameVacations)).setText(data.get(position).title);
            ((TextView)view.findViewById(R.id.dashDescVacations)).setText(data.get(position).description);


            //temporary items
//            ((ImageView)view.findViewById(R.id.dashImg)).setImage(something); //TODO: get last memory and get an image there
            //TODO: populate entirely
        }

        return view;
    }
}
