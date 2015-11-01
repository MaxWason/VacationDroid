package com.jkpg.jurgen.nl.vacationdroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jurgen on 10/27/2015.
 */
public class UserDashFragment extends Fragment {

    public UserDashFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_dash, container, false);
    }
}
