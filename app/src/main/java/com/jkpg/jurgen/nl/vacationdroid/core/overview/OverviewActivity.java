package com.jkpg.jurgen.nl.vacationdroid.core.overview;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;

import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.account.AccountActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.FriendsListActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.Friend;
import com.jkpg.jurgen.nl.vacationdroid.core.friends.logic.withImage.FriendItemImage;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.core.vacationList.VacationListActivity;

import java.net.URI;
import java.util.ArrayList;


public class OverviewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FriendItemImage.OnFragmentInteractionListener {

    ArrayList<Friend> friends = new ArrayList<>();

    @Override
    public void onFragmentInteraction(String id) { //for the friendItems

        //if pressed on the "friend's vacations" text
//        Intent gotoVacationList = new Intent(this, VacationListActivity.class);
//        gotoVacationList.putExtra("displayUser", false); //friend's page to display
//        gotoVacationList.putExtra("friendId", getFriendId());
//        startActivity(gotoVacationList);

        //if pressed on a specific vacation
        Intent intent = new Intent(OverviewActivity.this, VacationActivity.class);
        intent.putExtra("vacationName", getVacationName());
        startActivity(intent);
    }

    private String getVacationName(){
        //get the name of the vacation to display in the new VacationActivity
        return "placeholder";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        1);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (resultCode != RESULT_OK) return;
        switch (resultCode) {

            case RESULT_OK:
                if (data != null) {

                    Uri uri = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    }catch (Exception e) {
                        bitmap = null;
                        Log.e("FILE", e.getMessage());
                    }
                    /// use btemp Image file
                    Log.d("Image", bitmap.toString());

                }
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
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

    public void onVacationListUserPress(View v){
        Intent gotoVacationList = new Intent(this, VacationListActivity.class);
        startActivity(gotoVacationList); //just go as a user
    }

    public void onVacationListFriendPress(View v){
        Intent gotoVacationList = new Intent(this, VacationListActivity.class);
        gotoVacationList.putExtra("displayUser", false); //friend's page to display
        gotoVacationList.putExtra("friendName", getFriendName());
        startActivity(gotoVacationList);
    }

    private String getFriendName(){
        //get the list element clicked on
        //get that element's friend's name
        //return that name
        return "someFriend";//random placeholder
    }
}
