package com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;


public class VacationsItem extends Fragment implements AbsListView.OnItemClickListener {

    private static final String USERNAME = "username";
    private static final String FRIEND_NAME = "friendName";

    private boolean useUser;
    private String userName;
    private String friendName;

    private OnFragmentInteractionListener mListener;


    //The fragment's ListView/GridView.
    private AbsListView mListView;

    //The Adapter which will be used to populate the ListView/GridView with Views.
    private ListAdapter mAdapter;

    //was static
    public VacationsItem newInstance(Boolean shouldUseUser, String param1, String param2) {
        VacationsItem fragment = new VacationsItem();
        Bundle args = new Bundle();
        useUser = shouldUseUser;
        if (useUser) args.putString(userName, param1);
        else args.putString(friendName, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VacationsItem() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        useUser = getActivity().getIntent().getBooleanExtra("displayUser", true);

        if (useUser)
            userName = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE).getString("username", null);
        else
            friendName = getActivity().getIntent().getStringExtra("friendName");

        ArrayList<Vacation> vacationsList = getVacations();

        if (vacationsList.isEmpty()){ //no vacations for that user
            vacationsList.add(new Vacation(-1,"No Vacations!", "Add one to see it here.", "No Place", 0, 0, -1));
        }

        mAdapter = new VacationsAdapter(getActivity(), R.layout.fragment_vacation_list_dash, vacationsList);

//        ArrayList vacations = new ArrayList<VacationsDummy>();
//        vacations.add(new VacationsDummy("Prague", "Best Vacation ever!"));
//        vacations.add(new VacationsDummy("Stockholm", "SWE is love SWE is life"));
//        vacations.add(new VacationsDummy("Some other place", "Filler text, yay!"));
//        vacations.add(new VacationsDummy("Krakow", "krakow, krakow"));
//        vacations.add(new VacationsDummy());
//        vacations.add(new VacationsDummy());
//        vacations.add(new VacationsDummy());
//        vacations.add(new VacationsDummy());
//        // friends = (OverviewActivity) getActivity().friends or something
//
//        mAdapter = new VacationsAdapter<VacationsDummy>(getActivity(), R.layout.fragment_vacation_list_dash, vacations);
    }

    private ArrayList<Vacation> getVacations(){
        ArrayList<Vacation> toReturn = new ArrayList<Vacation>();
        if (useUser){
            toReturn = getUserVacations(toReturn);
        }else{
            toReturn = getFriendVacations(toReturn);
        }
        return toReturn;
    }

    private ArrayList<Vacation> getUserVacations(final ArrayList<Vacation> vacs){

        APIJsonCall dashcall = new APIJsonCall("users/"+userName+"/vacations", "GET", getActivity()) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.getAsJsonArray("list");
                    for (JsonElement aVac : arr) {
                        JsonObject aVacation = aVac.getAsJsonObject();
                        Vacation myVac = new Vacation(
                                aVacation.get("id").getAsInt(),
                                aVacation.get("title").getAsString(),
                                aVacation.get("description").getAsString(),
                                aVacation.get("place").getAsString(),
                                aVacation.get("start").getAsInt(),
                                aVacation.get("end").getAsInt(),
                                aVacation.get("userId").getAsInt()
                        );
                        vacs.add(myVac);
                        Log.i("test",myVac.toString());
                    }
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
        return vacs;
    }

    private ArrayList<Vacation> getFriendVacations(final ArrayList<Vacation> vacs){

        APIJsonCall dashcall = new APIJsonCall("users/"+friendName+"/vacations", "GET", getActivity()) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.getAsJsonArray("list");
                    for (JsonElement aVac : arr) {
                        JsonObject aVacation = aVac.getAsJsonObject();
                        Vacation myVac = new Vacation(
                                aVacation.get("id").getAsInt(),
                                aVacation.get("title").getAsString(),
                                aVacation.get("description").getAsString(),
                                aVacation.get("place").getAsString(),
                                aVacation.get("start").getAsInt(),
                                aVacation.get("end").getAsInt(),
                                aVacation.get("userId").getAsInt()
                        );
                        vacs.add(myVac);
                        Log.i("test",myVac.toString());
                    }
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
        return vacs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(VacationsDummy. ->change stuff-> ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
