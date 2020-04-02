package com.Tow_Buddy.Tow_Buddy_Customer_Side;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


public class Dialog_CoordinateSendingFailed extends Activity implements Runnable
{
    private Context context;
    public Dialog_CoordinateSendingFailed(Context context)
    {
     this.context = context;
    }
    @Override
    public void run()
    {

        Toast toast = Toast.makeText(this.context,
                "Send failed, please try again.",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.show();
    }
}
