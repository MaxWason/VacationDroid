package com.jkpg.jurgen.nl.vacationdroid;

import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by Jurgen on 11/5/2015.
 */
public class WebConnection {

    Context context;
    String baseURL = "";
    Gson gson = new Gson();


    public WebConnection(Context c) {
        context = c;
    }

    //returns access token if valid, else returns null
    public String APILogin(String username, String pw) {
        return null;
    }

    //returns access token
    public String APIRegister(String username, String pw, String email) {
        return null;
    }



}
