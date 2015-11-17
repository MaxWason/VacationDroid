package com.jkpg.jurgen.nl.vacationdroid.core.search;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListAdapter;

import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.logic.VacationsItem;

public class SearchableActivity extends AppCompatActivity implements VacationsItem.OnFragmentInteractionListener {//ListActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.searchable_activity);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

//    @Override
//    public void setListAdapter(ListAdapter adapter){
//        //TODO: set view here from the search results
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        //whenever a key is pressed it is assumed the user is searching
//        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    private void doMySearch(String query){
        //TODO: api web call here (with current user/friend as other search param)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchable_test, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_test).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public void onFragmentInteraction(String id) {
        Intent intent = new Intent(SearchableActivity.this, VacationActivity.class);
        startActivity(intent);
    }

}
