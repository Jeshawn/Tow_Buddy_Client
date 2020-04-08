package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.Tow_Buddy.R;

public class Dialog_IncorrectCredentials extends DialogFragment
{
    @Override
    @SuppressWarnings("deprecation")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.IncorrectCredentials)
                .setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog,
                            int id)
                    {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
