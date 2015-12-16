package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;


public class VacationsItem extends Fragment implements AbsListView.OnItemClickListener {

    //variables for knowing who to make this fragment for
    private boolean useUser;
    private boolean deleting=false;
    private String userName;
    private String friendName;

    //The fragment's ListView/GridView.
    private AbsListView mListView;
    final ArrayList<Vacation> vacationArrayList = new ArrayList<Vacation>();
    //The Adapter which will be used to populate the ListView/GridView with Views.
    private ArrayAdapter mAdapter;

    //Mandatory empty constructor
    public VacationsItem() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the data for who to make this fragment for
        useUser = getActivity().getIntent().getBooleanExtra("displayUser", true);
        if (useUser)
            userName = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE).getString("username", null);
        else
            friendName = getActivity().getIntent().getStringExtra("friendName");

        //get the vacations
        getVacations();

        //make the adapter take the vacation list
        mAdapter = new VacationsAdapter(getActivity(), R.layout.fragment_vacation_list_dash, vacationArrayList);
    }

    public void updateWithNewData(ArrayList<Vacation> newData){
        mAdapter = new VacationsAdapter(getActivity(), R.layout.fragment_vacation_list_dash, newData);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyList() {
        getVacations();
        mAdapter.notifyDataSetChanged();
    }
    /**
     * Gets a list of vacations depending on if the user or a friend needs them.
     */
    private void getVacations(){

        //if using the user get the data for them otherwise get it for the friend
        String personToGetDataFor = useUser ? userName : friendName;

        DBConnection db = new DBConnection(getActivity());
        vacationArrayList.clear();
        vacationArrayList.addAll(db.getUserVacations(personToGetDataFor));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(deleting==false) {
            Intent gotoVacation = new Intent(getActivity(), VacationActivity.class);
            int newid = vacationArrayList.get((int) position).id;

            gotoVacation.putExtra("id", newid);
            startActivity(gotoVacation);
        }else{
            final int pos = position;
            final Context mContext = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            final int vacID = vacationArrayList.get(pos).id;

            Log.d("Position",pos+"");
            Log.d("VacID",vacID+"");


            builder.setMessage("Are you sure you want to delete this vacation ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            APIJsonCall memcall = new APIJsonCall("vacations/"+vacID, "DELETE", mContext) {
                                @Override
                                public void JsonCallback(JsonObject obj) {
                                    Toast.makeText(mContext, "  Vacation deleted  ", Toast.LENGTH_LONG).show();
                                    deleting=false;
                                    DBConnection db = new DBConnection(getActivity());
                                    db.deleteFromTable(vacID, "vacations");
                                    notifyList();
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

        }
    }

    public void setDeleteClickEvent(){
        deleting=true;
    }
}
