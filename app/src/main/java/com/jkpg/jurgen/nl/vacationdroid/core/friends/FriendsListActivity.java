package com.jkpg.jurgen.nl.vacationdroid.core.friends;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.VacationListActivity;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.User;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {

    private ListView listView;
    private SharedPreferences pref;
    private String username;

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend);
        setSupportActionBar(toolbar);

        //add friend
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPopupWindowAddFriend();
            }
        });

        //can I actually call this here onCreate?
        pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        username = pref.getString("username", null);
        ArrayList<String> values = new ArrayList<>();

        getFriendsWeb(new ArrayList<String>());

        DBConnection db = new DBConnection(this);
        ArrayList<User> users = db.getFriends();

        for(User u: users) {
            values.add(u.username);
        }

        if(values.size() == 0)
            values.add("No friends man, I'm sorry. We could grab a beer if you want!");

        listView = (ListView) findViewById(R.id.list_friend_id);

        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(arrayAdapter);

        //go to friend's page on short click
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

        //delete friend if long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                //remove the friend
                removeFriend(itemValue);

                return true; //makes sure long click is the only one called
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    private void doPopupWindowAddFriend(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add Friend");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = input.getText().toString();

                //add friend
                addFriend(name);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled. Do nothing.
            }
        });

        alert.show();
    }

    private void addFriend(final String friendUsername){
        final JsonObject friend = new JsonObject();
        friend.addProperty("username",friendUsername);
        APIJsonCall dashcall = new APIJsonCall("users/" + username + "/friends/" , "POST", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    //notify user and update list if successful
                    Toast.makeText(getApplicationContext(), "  Added Friend : " + friendUsername, Toast.LENGTH_SHORT).show();
                    arrayAdapter.add(friendUsername);
                    arrayAdapter.notifyDataSetChanged();
                } catch (Exception E) {
                    Toast.makeText(getApplicationContext(), "  Couldn't add friend : " + friendUsername, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("WEB ERROR", E.getMessage());
                    } catch (Exception ex){
                        Log.e("WEB ERROR", "No error message received!");
                    }
                }
            }
        };
        dashcall.execute(friend);
    }

    private void removeFriend(final String friendName){
        APIJsonCall dashcall = new APIJsonCall("users/" + username + "/friends/" + friendName, "DELETE", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    //update the list and notify the user if successful
                    Toast.makeText(getApplicationContext(), "  Removed Friend : " + friendName, Toast.LENGTH_LONG).show();
                    arrayAdapter.remove(friendName);
                    arrayAdapter.notifyDataSetChanged();
                } catch (Exception E) {
                    try {
                        Toast.makeText(getApplicationContext(), "  Couldn't remove friend : " + friendName, Toast.LENGTH_LONG).show();
                        Log.e("WEB ERROR", E.getMessage());
                    } catch (Exception ex){
                        Toast.makeText(getApplicationContext(), "  Couldn't remove friend : " + friendName, Toast.LENGTH_LONG).show();
                        Log.e("WEB ERROR", "No error message received!");
                    }
                }
            }
        };
        dashcall.execute(new JsonObject());
    }


    private ArrayList<String> populateListWithFriends() {
        ArrayList<String> returnValue = new ArrayList<String>();

        //get friends list
        try {
            returnValue = getFriendsWeb(returnValue);
        } catch (Exception e) {
            //web error, use cached data
            returnValue.add("Cached data only");
        }

        if (returnValue.size() == 0) returnValue.add("No friends :(");

        //return list
        return returnValue;
    }

    private ArrayList<String> getFriendsWeb(final ArrayList<String> initList) {
        final Context c = this;
        APIJsonCall dashcall = new APIJsonCall("users/" + username + "/friends", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.get("list").getAsJsonArray();
                    DBConnection db = new DBConnection(c);

                    for (JsonElement anArr : arr) {
                        JsonObject j = (JsonObject)anArr;
                        initList.add(j.get("username").getAsString());
                        User u = new User(j.get("id").getAsInt(), j.get("username").getAsString());
                        db.addOrUpdateUser(u);
                    }
                    arrayAdapter.notifyDataSetChanged();
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
        return initList;
    }
}
