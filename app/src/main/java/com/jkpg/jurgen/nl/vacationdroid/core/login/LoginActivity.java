package com.jkpg.jurgen.nl.vacationdroid.core.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jkpg.jurgen.nl.vacationdroid.core.overview.OverviewActivity;
import com.jkpg.jurgen.nl.vacationdroid.R;

public class  LoginActivity extends AppCompatActivity {

    LoginFragment login = new LoginFragment();
    RegisterFragment register = new RegisterFragment();
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if we already have credentials or not
        //if we do, skip to overviewActivity

        setContentView(R.layout.login_activity);

        getFragmentManager().beginTransaction()
                .add(R.id.loginContainer, login, "loginFrag")
                .add(R.id.loginContainer, register, "registerFrag")
                .hide(register)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        pref = getPreferences(MODE_PRIVATE);

        if(pref.getString("username", null) != null) {
            Intent gotoOverview = new Intent(this, OverviewActivity.class);
            startActivity(gotoOverview);
            finish();
        }
    }

    public void gotoOverview() {
        Intent gotoOverview = new Intent(this, OverviewActivity.class);
        startActivity(gotoOverview);
        finish();
    }
    public void onNewAccountPress(View v) {

        //show the register fragment
        getFragmentManager().beginTransaction()
                .hide(login)
                .show(register)
                .addToBackStack(null)
                .commit();
    }

    public void onLoginPress(View v) {
        //get data from view

        TextView userview = (TextView)findViewById(R.id.loginUsername);
        TextView pwview = (TextView)findViewById(R.id.loginPassword);
        String user = userview.getText().toString();
        String pw = pwview.getText().toString();

        Log.d("Login", "Username & Pw entered: " + user + " - " + pw);
        //check credentials

        /*
        String token = Webconnection.APILogin(user, pw);
        if(token == null) do error things, break
        */

        //save data to preferences

        /*
        SharedPreferences.Editor edit = pref.edit();

        edit.putString("username", user);
        edit.putString("password", pw);
        edit.putString("token", token);
        */

        //go to overview (finish self so we're not added to the backstack)
        gotoOverview();
    }

    public void onRegisterPress(View v) {
        //register new account

        //get token for new account

        //goto overview activity
        gotoOverview();
    }
    @Override
    public void onBackPressed() {

        getFragmentManager().popBackStack();

        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
    }



}
