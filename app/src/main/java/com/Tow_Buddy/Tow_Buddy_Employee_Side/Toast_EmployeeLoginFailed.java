package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toast_EmployeeLoginFailed extends Activity implements Runnable {
    private Context context;

    public Toast_EmployeeLoginFailed(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        Toast toast = Toast.makeText(this.context,
                "Login failed, please try again.",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.show();
    }
}