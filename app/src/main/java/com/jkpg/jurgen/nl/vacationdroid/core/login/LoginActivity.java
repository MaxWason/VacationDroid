package com.jkpg.jurgen.nl.vacationdroid.core.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APITokenCall;
import com.jkpg.jurgen.nl.vacationdroid.core.overview.OverviewActivity;

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

        pref = getSharedPreferences("vacation", MODE_PRIVATE);

        //DEBUG TODO:this removes the saved credentials to test the login, remove for release version
//        SharedPreferences.Editor ed = pref.edit();
//        ed.remove("username");
//        ed.remove("password");
//        ed.remove("token");
//        ed.commit();
        //DEBUG

        //To remember...
        //user: maxwason
        //pass: superpassword

        ConnectivityManager m = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = m.getActiveNetworkInfo();
        if (info == null) {
            Toast.makeText(this, "no network detected, showing local data", Toast.LENGTH_LONG);
            gotoOverview();
            return;
        }

        //check if we already have a saved user or not
        if (pref.getString("username", null) != null) {
            //get a new token using the information in the preferences
            final String user = pref.getString("username", null);
            final String pw = pref.getString("password", null);
            final Context currentct = this;

            APITokenCall logincall = new APITokenCall() {
                @Override
                public void loginCallback(String token) {
                    if (token != null) {
                        //variables from the upper level scope that you want to use have to be final

                        //save token
                        SharedPreferences.Editor edit = pref.edit();

                        edit.putString("token", token);
                        edit.commit();

                        gotoOverview();
                    } else {
                        //display error message
                        Toast.makeText(currentct, "error logging in", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            logincall.execute(user, pw);
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

        Toast.makeText(this, "connecting", Toast.LENGTH_SHORT).show();

        TextView userview = (TextView) findViewById(R.id.loginUsername);
        TextView pwview = (TextView) findViewById(R.id.loginPassword);
        final String user = userview.getText().toString();
        final String pw = pwview.getText().toString();
        final Context currentct = this;
        Log.d("Login", "Username & Pw entered: " + user + " - " + pw);
        //check credentials

        login(user, pw);
    }


    public void onRegisterPress(View v) {
        //register new account

        Toast.makeText(this, "Creating", Toast.LENGTH_SHORT).show();

        TextView userview = (TextView) findViewById(R.id.registerUsername);
        TextView pwview = (TextView) findViewById(R.id.registerPassword);
        TextView emailview = (TextView) findViewById(R.id.registerEmail);

        final String user = userview.getText().toString();
        final String pw = pwview.getText().toString();
        final String email = emailview.getText().toString();

        final Context currentct = this;

        JsonObject registerJson = new JsonObject();
        registerJson.addProperty("username", user);
        registerJson.addProperty("password", pw);
        registerJson.addProperty("email", email);

        APIJsonCall registercall = new APIJsonCall("accounts", "POST", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                if(obj.has("created")) {
                    //successfully created
                    Toast.makeText(currentct, "created", Toast.LENGTH_SHORT).show();
                    //get a token for this new information
                    login(user, pw);
                }
            }
        };
        registercall.execute(registerJson);
    }

    protected void login(String username, String pass) {
        final String user = username;
        final String pw = pass;
        final Context currentct = this;

        APITokenCall logincall = new APITokenCall() {
            @Override
            public void loginCallback(String token) {
                if (token != null) {
                    //variables from the onLoginPress scope that you want to use have to be final
                    SharedPreferences.Editor edit = pref.edit();

                    edit.putString("username", user);
                    edit.putString("password", pw);
                    edit.putString("token", token);
                    edit.commit();

                    gotoOverview();
                } else {
                    //display error message
                    Toast.makeText(currentct, "Invalid info", Toast.LENGTH_LONG).show();
                }
            }
        };
        logincall.execute(user, pw);
    }

    @Override
    public void onBackPressed() {
        onStop(); //you never need to go back from the log-in page
    }
}