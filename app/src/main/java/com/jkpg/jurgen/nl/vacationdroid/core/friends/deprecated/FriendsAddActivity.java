package com.jkpg.jurgen.nl.vacationdroid.core.friends.deprecated;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;

import java.util.ArrayList;
import java.util.List;

public class FriendsAddActivity extends AppCompatActivity {

    //
    //
    //===================================UNUSED because you can't get a list of all users=====================================
    //
    //


    private ListView listView;
    private SharedPreferences pref;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_add_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = this.getSharedPreferences("vacation", Context.MODE_PRIVATE);
        username = pref.getString("username", null);
        String[] values = populateListWithUsers();

        listView = (ListView) findViewById(R.id.list_add_friend_id);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                boolean success = addFriend(itemValue);

                // Show Alert
                if (success)
                    Toast.makeText(getApplicationContext(), "  Added Friend : " + itemValue, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private boolean addFriend(String friendUsername){

        APIJsonCall dashcall = new APIJsonCall("users/" + username + "/" + friendUsername, "POST", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
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
        return true;
    }

    private String[] populateListWithUsers() {
        String[] returnValue;
        //get friends list
        try {
            returnValue = getUsersWeb();
        } catch (Exception e) { //TODO does it actually catch though?
            //web error, use cached data
            returnValue = new String[]{"Cached data only"};
        }

        if (returnValue.length == 0) returnValue = new String[]{"No users registered :("};

        //return list
        return returnValue;
    }

    private String[] getUsersWeb() {

        final List<String> listFinal = new ArrayList<String>();

        APIJsonCall dashcall = new APIJsonCall("users", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.get("friends").getAsJsonArray();
                    for (JsonElement anArr : arr) {
                        Log.i("users",anArr.getAsString());
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
