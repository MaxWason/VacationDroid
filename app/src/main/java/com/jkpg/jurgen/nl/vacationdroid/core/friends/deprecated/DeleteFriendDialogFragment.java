package com.jkpg.jurgen.nl.vacationdroid.core.friends.deprecated;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jkpg.jurgen.nl.vacationdroid.R;

public class DeleteFriendDialogFragment extends DialogFragment {

    //==========================================Unnecessary feature for now, come back to it later if there is time=======================

    //https://developer.android.com/guide/topics/ui/dialogs.html

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_friend)
                .setPositiveButton(R.string.delete_friend, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete friend
                        mListener.onDialogPositiveClick(DeleteFriendDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go back
                        mListener.onDialogNegativeClick(DeleteFriendDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

