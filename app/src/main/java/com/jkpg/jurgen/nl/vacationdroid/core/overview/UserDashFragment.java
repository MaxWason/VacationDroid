package com.jkpg.jurgen.nl.vacationdroid.core.overview;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.core.vacation.VacationActivity;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

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

        updateView();

    }

    public void updateView() {
        username = (TextView) getActivity().findViewById(R.id.userdashUsername);
        vtitle = (TextView) getActivity().findViewById(R.id.userdashVacationTitle);
        vdesc = (TextView) getActivity().findViewById(R.id.userdashVacationDescription);

        SharedPreferences sp = getActivity().getSharedPreferences("vacation", Context.MODE_PRIVATE);
        String un = sp.getString("username", "error");

        DBConnection db = new DBConnection(getActivity());

        username.setText(un);

        try {
            Vacation firstVac = db.getUserVacations(sp.getString("username", "error")).get(0);
            vtitle.setText(firstVac.title);
            vdesc.setText(firstVac.description);
        }catch(IndexOutOfBoundsException e) {
            Log.d("index", e.getMessage());
            vtitle.setText("no vacation to show");
            vdesc.setText("create a new vacation to see it here");
        }
    }
}
