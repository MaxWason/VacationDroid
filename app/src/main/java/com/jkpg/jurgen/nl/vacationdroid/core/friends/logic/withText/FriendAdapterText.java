package com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withText;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jkpg.jurgen.nl.vacationdroid.R;

import java.util.ArrayList;


public class FriendAdapterText<Friend> extends ArrayAdapter<Friend> {

    Context context;
    ArrayList<Friend> data;
    int viewid;

    public FriendAdapterText(Context context, int viewID, ArrayList<Friend> data) {
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

            row.setTag(holder);
        }
        else
        {
            holder = (FriendHolder)row.getTag();
        }

        Friend f = data.get(position);
        holder.name.setText("filler name");

        return row;
    }
class FriendHolder {
    public TextView name;

    public FriendHolder() {

    }
}
}

