package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toast_SuccessfullyLoggedIn extends Activity implements Runnable {
    private Context context;

    public Toast_SuccessfullyLoggedIn(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        Toast toast = Toast.makeText(this.context,
                "You are now logged in!",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.show();
    }
}