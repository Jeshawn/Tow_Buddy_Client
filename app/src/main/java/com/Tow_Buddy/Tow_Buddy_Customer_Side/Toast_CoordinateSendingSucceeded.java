package com.Tow_Buddy.Tow_Buddy_Customer_Side;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toast_CoordinateSendingSucceeded extends Activity implements Runnable
{
    private Context context;
    public Toast_CoordinateSendingSucceeded(Context context)
    {
        this.context = context;
    }
    @Override
    public void run()
    {
        Toast toast = Toast.makeText(this.context,
                "Send succeeded!",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.show();
    }
}
