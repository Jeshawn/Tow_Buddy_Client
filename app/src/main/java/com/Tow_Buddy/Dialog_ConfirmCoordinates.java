package com.Tow_Buddy;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class Dialog_ConfirmCoordinates extends DialogFragment
{
    @Override
    @SuppressWarnings("deprecation")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.Coordinate_Alert_Box_Message)
                .setPositiveButton(R.string.Accept_Coordinate_Send, new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog,
                            int id)
                    {
                        CoordinateSender coordinateSender = new CoordinateSender(getContext());
                        new Thread(coordinateSender).start();

                    }
                })
                .setNegativeButton(R.string.Decline_Coordinate_Send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }



}







