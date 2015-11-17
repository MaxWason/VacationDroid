package com.jkpg.jurgen.nl.vacationdroid.core.friends;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import com.jkpg.jurgen.nl.vacationdroid.R;

public class FriendsViewActivity extends AppCompatActivity implements DeleteFriendDialogFragment.NoticeDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
    }

    //https://developer.android.com/guide/topics/ui/menus.html#context-menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_friend, menu);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
