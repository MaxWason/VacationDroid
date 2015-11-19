package com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withImage;

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

/**
 * Created by Jurgen on 10/30/2015.
 */
public class FriendAdapterImage<Friend> extends ArrayAdapter<Friend> {

    Context context;
    ArrayList<Friend> data;
    int viewid;

    public FriendAdapterImage(Context context, int viewID, ArrayList<Friend> data) {
        super(context, viewID, data);
        this.context = context;
        this.data = data;
        this.viewid = viewID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(viewid, parent, false);

            holder = new FriendHolder();
            holder.name = (TextView)row.findViewById(R.id.dashName);
            holder.vacation = (TextView)row.findViewById(R.id.dashVacation);
            holder.img = (ImageView)row.findViewById(R.id.dashImg);

            row.setTag(holder);
        }
        else
        {
            holder = (FriendHolder)row.getTag();
        }

        Friend f = data.get(position);
        holder.name.setText("dummy");
        holder.vacation.setText("dummyvacation");

        return row;
    }
    class FriendHolder {
        public TextView name;
        public TextView vacation;
        public ImageView img;

        public FriendHolder() {

        }
    }
}

