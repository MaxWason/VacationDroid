package com.jkpg.jurgen.nl.vacationdroid.core.overview;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;

import org.json.JSONObject;

/**
 * Created by Jurgen on 10/27/2015.
 */
public class UserDashFragment extends Fragment {

    private TextView username =null;
    private TextView vtitle =null;
    private TextView vdesc = null;

    public UserDashFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_dash, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        username = (TextView) getActivity().findViewById(R.id.userdashUsername);
        vtitle = (TextView) getActivity().findViewById(R.id.userdashVacationTitle);
        vdesc = (TextView) getActivity().findViewById(R.id.userdashVacationDescription);

        SharedPreferences pref = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
        String name = pref.getString("username", null);

        APIJsonCall dashcall = new APIJsonCall("users/"+name, "GET", getActivity()) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    username.setText(obj.get("username").getAsString());
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        dashcall.execute(new JsonObject());

        APIJsonCall vaccall = new APIJsonCall("vacations", "GET", getActivity()) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("JASON", obj.toString());
                    JsonArray arr = obj.getAsJsonArray("list");
                    JsonObject v1 = arr.get(0).getAsJsonObject();
                    vtitle.setText(v1.get("title").getAsString());
                    vdesc.setText(v1.get("description").getAsString());
                } catch(Exception E) {
                    Log.e("WEB ERROR", E.getMessage());
                }
            }
        };
        vaccall.execute(new JsonObject());
    }

    public String getVacationTitle(){
        return vtitle.getText().toString();
    }

    public String getVacationDesc(){
        return vdesc.getText().toString();
    }

}
