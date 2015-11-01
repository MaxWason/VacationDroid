package com.jkpg.jurgen.nl.vacationdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    LoginFragment login = new LoginFragment();
    RegisterFragment register = new RegisterFragment();

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

    }

    public void onNewAccountPress(View v) {

        getFragmentManager().beginTransaction()
                .hide(login)
                .show(register)
                .addToBackStack(null)
                .commit();
    }

    public void onLoginPress(View v) {
        //check credentials

        //get authentication token

        //go to overview (finish self so we're not added to the backstack)
        Intent gotoOverview = new Intent(this, OverviewActivity.class);
        startActivity(gotoOverview);
        finish();
    }

    @Override
    public void onBackPressed() {

        getFragmentManager().popBackStack();

        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
    }



}
