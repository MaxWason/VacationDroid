package com.jkpg.jurgen.nl.vacationdroid.core.search;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkpg.jurgen.nl.vacationdroid.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchableActivityFragment extends Fragment {

    public SearchableActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }
}
