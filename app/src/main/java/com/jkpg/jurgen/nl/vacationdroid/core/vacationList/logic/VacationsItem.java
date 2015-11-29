package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;


public class VacationsItem extends Fragment implements AbsListView.OnItemClickListener {

    //variables for knowing who to make this fragment for
    private boolean useUser;
    private String userName;
    private String friendName;

    //The fragment's ListView/GridView.
    private AbsListView mListView;

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
        ArrayList<Vacation> vacationsList = getVacations();

        //make the adapter take the vacation list
        mAdapter = new VacationsAdapter(getActivity(), R.layout.fragment_vacation_list_dash, vacationsList);
    }

    private void notifyList() {

    }
    /**
     * Gets a list of vacations depending on if the user or a friend needs them.
     * @return - the list of vacations
     */
    private ArrayList<Vacation> getVacations(){

        //if using the user get the data for them otherwise get it for the friend
        String personToGetDataFor = useUser ? userName : friendName;

        final ArrayList<Vacation> vacationArrayList = new ArrayList<Vacation>();
        //TODO: test this, specifically the JsonElement/Object structure and if it gets the correct data that way
        APIJsonCall dashcall = new APIJsonCall("users/"+personToGetDataFor+"/vacations", "GET", getActivity()) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.getAsJsonArray("list");
                    for (JsonElement aVac : arr) {
                        JsonObject aVacation = aVac.getAsJsonObject();
                        Vacation myVac = new Vacation(
                                aVacation.get("title").getAsString(),
                                aVacation.get("description").getAsString(),
                                aVacation.get("place").getAsString(),
                                aVacation.get("start").getAsInt(),
                                aVacation.get("end").getAsInt()
                        );
                        vacationArrayList.add(myVac);
                        Log.i("test",myVac.toString());
                    }
                    if (vacationArrayList.isEmpty()) //no vacations for that user
                        vacationArrayList.add(new Vacation("No Vacations!", "Add one to see it here.", "No Place", 0, 0));
                    mAdapter.notifyDataSetChanged();
                } catch (Exception E) {
                    try {
                        Log.e("WEB ERROR", E.getMessage());
                    } catch (Exception ex){
                        Log.e("WEB ERROR", "No error message received!");
                    }
                }
            }
        };
        dashcall.execute(new JsonObject());
        return vacationArrayList;
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
        //TODO: go to specific vacation here
    }
}
