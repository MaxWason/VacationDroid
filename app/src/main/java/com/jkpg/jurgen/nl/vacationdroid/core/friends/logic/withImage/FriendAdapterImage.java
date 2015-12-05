package com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withImage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.User;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

/**
 * Created by Jurgen on 10/30/2015.
 */
public class FriendAdapterImage extends ArrayAdapter<User> {

    Context context;
    ArrayList<User> data;
    int viewid;

    public FriendAdapterImage(Context context, int viewID, ArrayList<User> data) {
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

        User u = data.get(position);
        holder.name.setText(u.username);

        DBConnection db = new DBConnection(getContext());
        try {
            Vacation firstv = db.getUserVacations(u.username).get(0);
            holder.vacation.setText(firstv.title);
        }catch (IndexOutOfBoundsException e) {
            holder.vacation.setText("no vacation");
        }

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

