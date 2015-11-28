package com.jkpg.jurgen.nl.vacationdroid.core.friends;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withImage.FriendItemImage;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withText.FriendItemText;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.VacationListActivity;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity implements DeleteFriendDialogFragment.NoticeDialogListener {

    private ListView listView;
    private SharedPreferences pref;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("test", "should start addFriend activity?");
                startActivity(new Intent(view.getContext(), FriendsAddActivity.class));
            }
        });

        pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        username = pref.getString("username", null);
        String[] values = populateListWithFriends();

        listView = (ListView) findViewById(R.id.list_friend_id);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                //go to that friend's vacationList page
                Intent gotoVacationList = new Intent(view.getContext(), VacationListActivity.class);
                gotoVacationList.putExtra("displayUser", false); //friend's page to display
                gotoVacationList.putExtra("friendName", itemValue);
                startActivity(gotoVacationList);
            }
        });
    }

    //https://developer.android.com/guide/topics/ui/menus.html#context-menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_friend, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_unfriend_description) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    private String[] populateListWithFriends() {
        String[] returnValue;
        //get friends list
        try {
            returnValue = getFriendsWeb();
        } catch (Exception e) {
            //web error, use cached data
            returnValue = new String[]{"Cached data only"};
        }

        if (returnValue.length == 0) returnValue = new String[]{"No friends :("};

        //return list
        return returnValue;
    }

    private String[] getFriendsWeb() {

        final List<String> listFinal = new ArrayList<String>();

        APIJsonCall dashcall = new APIJsonCall("users/" + username + "/friends", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.get("friends").getAsJsonArray();
                    for (JsonElement anArr : arr) {
                        listFinal.add(anArr.getAsString()); //TODO format as: username
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

        return listFinal.toArray(new String[listFinal.size()]);
    }
}
