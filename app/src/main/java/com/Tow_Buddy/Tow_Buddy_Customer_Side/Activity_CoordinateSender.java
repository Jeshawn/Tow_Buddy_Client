package com.Tow_Buddy.Tow_Buddy_Customer_Side;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;

public class Activity_CoordinateSender extends AppCompatActivity implements Runnable
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getApplicationContext();
    }
    @Override
    public void run()
    {
        try {
            Looper.getMainLooper().prepare();
            Activity_RepositoryLayer_Customer towBuddyRepository = new Activity_RepositoryLayer_Customer(
                    Activity_SetLocation.customerName,
                    Activity_SetLocation.customerPhoneNumber,
                    new Date().toString(),
                    Activity_SetLocation.latLongForDatabase);
            new Thread(towBuddyRepository).start();
        }
        catch (Exception exception)
        {
            Log.e("Error", exception.toString());
            try
            {
                Looper.getMainLooper().prepare();
                runOnUiThread(new Toast_CoordinateSendingFailed(this));
            }
            catch (Exception e)
            {
                Log.e("SuperError:", e.toString());
            }

        }

    }

}
