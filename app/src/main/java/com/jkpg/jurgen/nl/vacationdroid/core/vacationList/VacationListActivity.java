package com.jkpg.jurgen.nl.vacationdroid.core.vacationList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic.VacationsItem;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

public class VacationListActivity extends AppCompatActivity {

    private boolean displayUser; //if you should display the data for the user or for a friend
    private String friendName; //if displaying the friend, this is the one to show
    private Context c;
    String nameToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        c = this;

        setContentView(R.layout.vacation_list_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new vacation
                createNewVacation(view);
            }
        });

        Intent intent = getIntent();

        //modify the display depending on who opened the activity
        displayUser = intent.getBooleanExtra("displayUser", true);
        SharedPreferences pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);

        if (displayUser) {
            //get name of user
            nameToDisplay = pref.getString("username", null);
        }else{
            //hide fab
            fab.setVisibility(FloatingActionButton.GONE);
            //get name of friend
            if (intent.getStringExtra("friendName") == null) //no name, for the unfinished test code
                nameToDisplay = "A Friend"; //generic name
            else
                nameToDisplay = intent.getStringExtra("friendName");
        }
        this.setTitle(nameToDisplay + "'s Vacations");

        syncData(nameToDisplay);

    }

    private void syncData(final String username) {
        final Context c = this;

        APIJsonCall dbvac = new APIJsonCall("users/" + username + "/vacations", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                if(obj != null && !obj.has("error")) {
                    JsonArray arr = obj.getAsJsonArray("list");
                    Gson gson = new Gson();
                    DBConnection db = new DBConnection(c);
                    for (JsonElement el : arr) {
                        Vacation v = gson.fromJson(el, Vacation.class);
                        v.user = username;
                        Log.d("DBVAC call", v.title);
                        db.addOrUpdateVacation(v);
                    }
                }

                //update view
                updateListView();
            }
        };
        dbvac.execute(new JsonObject());
    }

    private void updateListView() {
        VacationsItem list = (VacationsItem) getFragmentManager().findFragmentById(R.id.fragment_vac_item);
        list.notifyList();
    }

    /**
     * Searches for a memory by the title. The domain searched is the current user.
     * It returns the vacation that the memory searched for is in, so that the view is still compatible.
     * The reason we did it this way was that this seemed to be the only realistic use case,
     * and the exact implementation wasn't specified in the requirements.
     *
     * @param query - the title of the memory to search for
     */
    private void doMySearch(String query) {

        final Activity a = this;

        //empty search string, return immediately
        if (query.isEmpty()){
            updateListView();
            return;
        }

        //get the database and initialize the vacations to return
        DBConnection db = new DBConnection(a);
        ArrayList<Vacation> returnVacations = new ArrayList<Vacation>();

        //loop through to get the correct vacations
        ArrayList<Vacation> allVacations = db.getUserVacations(nameToDisplay); //depends on current user
        for (Vacation vacation : allVacations) {
            ArrayList<Memory> memories = db.getMemoriesByVacation(vacation.getId());
            for (Memory memory : memories) {
                Vacation tempVac = db.getVacationById(memory.vacationid);
                if (memory.title.equals(query.trim()) && (!returnVacations.contains(tempVac))) { //if search equal to the memory title and it is not in the vacation list already
                    returnVacations.add(tempVac);
                }
            }
        }

        if (returnVacations.size() > 0) {
            VacationsItem list = (VacationsItem) getFragmentManager().findFragmentById(R.id.fragment_vac_item);
            list.updateWithNewData(returnVacations);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchable, menu);
        if(displayUser) {
            inflater.inflate(R.menu.menu_vacation_list, menu);
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_vacation_list).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                doMySearch(s);
                return false; //collapses keyboard if false
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.deleteVacation:
                selectVacationToDelete();
                break;
            default:
                break;
        }

        return true;
    }

    private void createNewVacation(View v) {
        //get username from preferences
        SharedPreferences sp = getSharedPreferences("vacation", MODE_PRIVATE);
        final String name = sp.getString("username", "error");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText title = new EditText(c);
        title.setHint("Title");
        layout.addView(title);

        final EditText desc = new EditText(c);
        desc.setHint("Description");
        layout.addView(desc);

        final EditText place = new EditText(c);
        place.setHint("Place");
        layout.addView(place);

        final EditText from = new EditText(c);
        from.setHint("From");
        from.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(from);

        final EditText to = new EditText(c);
        to.setHint("To");
        to.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(to);

        builder.setView(layout);
        builder.setMessage("Create a new vacation")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (title.getText().toString().matches("") ||
                                desc.getText().toString().matches("") ||
                                place.getText().toString().matches("") ||
                                to.getText().toString().matches("") ||
                                from.getText().toString().matches("")) {
                            Toast.makeText(getApplicationContext(), "  Fill all the fields  ", Toast.LENGTH_LONG).show();
                        } else {
                            JsonObject newVac = new JsonObject();
                            newVac.addProperty("title", title.getText().toString());
                            newVac.addProperty("description", desc.getText().toString());
                            newVac.addProperty("place", place.getText().toString());
                            newVac.addProperty("start", from.getText().toString());
                            newVac.addProperty("end", to.getText().toString());

                            APIJsonCall vaccall = new APIJsonCall("vacations", "POST", c) {
                                @Override
                                public void JsonCallback(JsonObject obj) {
                                    if (!obj.has("error")) {


                                        Log.d("MODIFIED", obj.toString());
                                        Toast.makeText(getApplicationContext(), "  Vacation created  ", Toast.LENGTH_LONG).show();
                                    }
                                    syncData(nameToDisplay);
                                }
                            };
                            vaccall.execute(newVac);
                        }
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

    private void selectVacationToDelete(){
        VacationsItem list = (VacationsItem) getFragmentManager().findFragmentById(R.id.fragment_vac_item);
        list.setDeleteClickEvent();
    }
}
