package com.jkpg.jurgen.nl.vacationdroid.core.vacationList;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;

public class VacationListActivity extends AppCompatActivity {

    private boolean displayUser; //if you should display the data for the user or for a friend
    private String friendName; //if displaying the friend, this is the one to show
    private Context c;
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

        //craziness below here
        displayUser = intent.getBooleanExtra("displayUser", true);
        String nameToDisplay;

        SharedPreferences pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);

        if (displayUser){
            //get name of user
            nameToDisplay = pref.getString("username", null);
        }else{
            //get name of friend
            if (intent.getStringExtra("friendName") == null) //no name, for the unfinished test code
                nameToDisplay = "A Friend"; //generic name
            else
                nameToDisplay = intent.getStringExtra("friendName");
        }
        this.setTitle(nameToDisplay + "'s Vacations");

        //craziness ends
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }


    private void doMySearch(String query){
        //TODO: api web call here (with current user/friend as other search param)

    }

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

    private void createNewVacation(View v){
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
        title.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(from);

        final EditText to = new EditText(c);
        to.setHint("To");
        to.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(to);


        builder.setView(layout);
        builder.setMessage("Create a new vacation")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JsonObject newVac = new JsonObject();
                        newVac.addProperty("title",title.getText().toString());
                        newVac.addProperty("description",desc.getText().toString());
                        newVac.addProperty("place",place.getText().toString());
                        newVac.addProperty("start",from.getText().toString());
                        newVac.addProperty("end",to.getText().toString());


                        APIJsonCall vaccall = new APIJsonCall("vacations", "POST", c) {
                            @Override
                            public void JsonCallback(JsonObject obj) {
                                try {
                                    Log.d("MODIFIED", obj.toString());
                                    Toast.makeText(getApplicationContext(), "  Vacation created  ", Toast.LENGTH_LONG).show();
                                } catch (Exception E) {
                                    try {
                                        Log.e("WEB ERROR", E.getMessage());
                                    } catch (Exception ex){
                                        Log.e("WEB ERROR", "No error message received!");
                                    }
                                }
                            }
                        };
                        vaccall.execute(newVac);
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
