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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.jkpg.jurgen.nl.vacationdroid.R;

public class VacationListActivity extends AppCompatActivity {

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) //TODO: make a new vacation
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

}
