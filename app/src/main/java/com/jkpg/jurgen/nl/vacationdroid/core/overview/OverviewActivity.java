package com.jkpg.jurgen.nl.vacationdroid.core.overview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.account.AccountActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.FriendsListActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.Friend;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withImage.FriendItemImage;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.VacationListActivity;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.User;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;
import java.util.List;


public class OverviewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FriendItemImage.OnFragmentInteractionListener {

    ArrayList<Friend> friends = new ArrayList<>();

    @Override
    public void onFragmentInteraction(String id) { //for the friendItems
//
//        //if pressed on the "friend's vacations" text
////        Intent gotoVacationList = new Intent(this, VacationListActivity.class);
////        gotoVacationList.putExtra("displayUser", false); //friend's page to display
////        gotoVacationList.putExtra("friendId", getFriendId());
////        startActivity(gotoVacationList);
//
//        //if pressed on a specific vacation
//        Intent intent = new Intent(OverviewActivity.this, VacationActivity.class);
//        intent.putExtra("vacationName", getVacationName());
//        startActivity(intent);
    }

    private String getVacationName() {
        //get the name of the vacation to display in the new VacationActivity
        return "placeholder";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = getSharedPreferences("vacation", MODE_PRIVATE);
        final String username = sp.getString("username", "notfound");


        fetchOwnInfo(username);
        fetchUserVacations(username);
        fetchFriends(username);

    }

    private void fetchFriends(String username) {
        final Context c = this;
        APIJsonCall friendcall = new APIJsonCall("users/" + username + "/friends", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                if (!obj.has("error")) {

                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.get("list").getAsJsonArray();
                    DBConnection db = new DBConnection(c);

                    for (JsonElement anArr : arr) {
                        JsonObject j = (JsonObject) anArr;
                        User u = new User(j.get("id").getAsInt(), j.get("username").getAsString());
                        db.addOrUpdateUser(u);
                    }
                    //Log.d("USER", db.getFriends().size() + db.getFriends().size() != 0 ? db.getFriends().get(0).username : "no friends");
                    fetchFriendVacations();
                }

                updateFriendView();
                updateUserDash();
            }
        };
        friendcall.execute(new JsonObject());
    }

    public void fetchUserVacations(final String username) {

        final Context c = this;

        APIJsonCall dbvac = new APIJsonCall("users/" + username + "/vacations", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                if (obj != null && !obj.has("error")) {
                    JsonArray arr = obj.getAsJsonArray("list");
                    Gson gson = new Gson();
                    DBConnection db = new DBConnection(c);
                    for (JsonElement el : arr) {
                        Vacation v = gson.fromJson(el, Vacation.class);
                        v.user = username;
                        Log.d("DBVAC call", v.title);
                        db.addOrUpdateVacation(v);
                    }
                    ArrayList<Vacation> vacs = db.getVacations();
                    //Log.d("db select", "size: " + vacs.size() + " First item: " + (vacs.size() == 0 ? "None" : vacs.get(0).title));
                }
                updateFriendView();
                updateUserDash();
            }
        };
        dbvac.execute(new JsonObject());
    }

    private void fetchOwnInfo(String username) {
        final Context c = this;

        APIJsonCall dbuservac = new APIJsonCall("users/" + username, "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                if (!obj.has("error")) {

                    Log.d("JASON", obj.toString());
                    Integer id = obj.get("id").getAsInt();
                    String un = obj.get("username").getAsString();
                    DBConnection db = new DBConnection(c);
                    User u = new User(id, un);
                    db.addOrUpdateUser(u);

                    fetchUserVacations(un);
                }
                updateUserDash();
                updateFriendView();
            }
        };
        dbuservac.execute(new JsonObject());
    }

    public void fetchFriendVacations() {
        DBConnection db = new DBConnection(this);

        List<User> friends = db.getFriends();

        for (User u : friends) {
            fetchUserVacations(u.username);
        }

    }

    private void updateFriendView() {
        FriendItemImage friendfrag = (FriendItemImage) getFragmentManager().findFragmentById(R.id.friendDashFragment);
        friendfrag.updateView();
    }

    private void updateUserDash() {
        UserDashFragment dashfrag = (UserDashFragment) getFragmentManager().findFragmentById(R.id.userDashFragment);
        dashfrag.updateView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // TODO: Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_account) {
            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.add_memory) {

        } else if (id == R.id.add_vacation) {

        } else if (id == R.id.view_friends) {
            startActivity(new Intent(this, FriendsListActivity.class));
//        } else if (id == R.id.add_friend) { //Deprecated, friends are added in view_friends
//            startActivity(new Intent(this, FriendsAddActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onVacationListUserPress(View v) {
        Intent gotoVacationList = new Intent(this, VacationListActivity.class);
        startActivity(gotoVacationList); //just go as a user
    }

    private String getFriendName() {
        //get the list element clicked on
        //get that element's friend's name
        //return that name
        return "someFriend";//random placeholder
    }
}
