package com.jkpg.jurgen.nl.vacationdroid.core.vacationList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic.VacationsItem;

public class VacationListActivity extends AppCompatActivity implements VacationsItem.OnFragmentInteractionListener {

    //TODO: alter depending on if user's vactions or a friend's vacations

    private boolean displayUser; //if you should display the data for the user or for a friend
    private String friendName; //if displaying the friend, this is the one to show

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vacation_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();


        //craziness below here
        displayUser = intent.getBooleanExtra("displayUser", true);
        String nameToDisplay;

        SharedPreferences pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);

        if (displayUser){
            //get name of user

            nameToDisplay = pref.getString("username", null);
        }else{
            //get name of friend
            if (intent.getStringExtra("friendName") == null)
                nameToDisplay = "A Friend";
            else
                nameToDisplay = intent.getStringExtra("friendName");
        }
        this.setTitle(nameToDisplay+"'s Vacations");;

        //craziness ends
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void getUserData(){

//        final TextView username = (TextView) this.findViewById(R.id.userdashUsername);
//        final TextView vtitle = (TextView) this.findViewById(R.id.userdashVacationTitle);
//        final TextView vdesc = (TextView) this.findViewById(R.id.userdashVacationDescription);
//
//        SharedPreferences pref = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
//        String name = pref.getString("username", null);
//
//        APIJsonCall dashcall = new APIJsonCall("users/"+name, "GET", getActivity()) {
//            @Override
//            public void JsonCallback(JsonObject obj) {
//                try {
//                    Log.d("JASON", obj.toString());
//                    username.setText(obj.get("username").getAsString());
//                } catch(Exception E) {
//                    Log.e("WEB ERROR", E.getMessage());
//                }
//            }
//        };
//        dashcall.execute(new JsonObject());
//
//        APIJsonCall vaccall = new APIJsonCall("vacations", "GET", getActivity()) {
//            @Override
//            public void JsonCallback(JsonObject obj) {
//                try {
//                    Log.d("JASON", obj.toString());
//                    JsonArray arr = obj.getAsJsonArray("list");
//                    JsonObject v1 = arr.get(0).getAsJsonObject();
//                    vtitle.setText(v1.get("title").getAsString());
//                    vdesc.setText(v1.get("description").getAsString());
//                } catch(Exception E) {
//                    Log.e("WEB ERROR", E.getMessage());
//                }
//            }
//        };
//        vaccall.execute(new JsonObject());
    }


    private void doMySearch(String query){
        //TODO: api web call here (with current user/friend as other search param)
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchable, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_test).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        //TODO: handle cases
//        if (id == R.id.action_search) {
//            Intent intent = new Intent(VacationListActivity.this, SearchableActivity.class);
//            startActivity(intent);
//            return false; //true or false?
//        } else if (id == R.id.action_unfriend) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onFragmentInteraction(String id) {
        //TODO: go to specific vacation
        Intent intent = new Intent(VacationListActivity.this, VacationActivity.class);
        startActivity(intent);
    }
}
