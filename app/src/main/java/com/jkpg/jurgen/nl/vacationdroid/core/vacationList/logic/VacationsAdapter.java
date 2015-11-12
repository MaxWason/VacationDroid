package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkpg.jurgen.nl.vacationdroid.R;

import java.util.ArrayList;

public class VacationsAdapter<VacationsDummy> extends ArrayAdapter<VacationsDummy> {

    Context context;
    ArrayList<VacationsDummy> data;
    int viewid;

    public VacationsAdapter(Context context, int viewID, ArrayList<VacationsDummy> data) {
        super(context, viewID, data);
        this.context = context;
        this.data = data;
        this.viewid = viewID;
    }

    //TODO: not actually getting the vacation data, going to the else block
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VacationsHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(viewid, parent, false);

            holder = new VacationsHolder();
            holder.name = (TextView)row.findViewById(R.id.dashNameVacations);
            holder.description = (TextView)row.findViewById(R.id.dashDescVacations);
            holder.img = (ImageView)row.findViewById(R.id.dashImg);

            row.setTag(holder);
        }
        else
        {
            holder = (VacationsHolder)row.getTag();
        }

        VacationsDummy f = data.get(position);
        holder.name.setText("VacName");
        holder.description.setText("Filler Description");

        return row;
    }

    class VacationsHolder {
        public TextView name;
        public TextView description;
        public ImageView img;

        public VacationsHolder() {

        }
    }
}
