package com.Tow_Buddy.Tow_Buddy_Customer_Side;

import android.content.Context;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CoordinateSender extends AppCompatActivity implements Runnable
{
    private String phoneToSendCoordinatesTo = "3135862702@tmomail.net";
    private Context context;

    public CoordinateSender(Context context)
    {
        this.context = context;
    }
    @Override
    public void run()
    {
        try {
            Looper.getMainLooper().prepare();
            TowBuddyRepository towBuddyRepository = new TowBuddyRepository(
                    MainActivity.customerName,
                    MainActivity.customerPhoneNumber,
                    new Date().toString(),
                    MainActivity.latLongForDatabase,
                    this.context);
            new Thread(towBuddyRepository).start();
        }
        catch (Exception exception)
        {
            Log.e("Error", exception.toString());
            try
            {
                Looper.getMainLooper().prepare();
                runOnUiThread(new Dialog_CoordinateSendingFailed(context));
            }
            catch (Exception e)
            {
                Log.e("SuperError:", e.toString());
            }

        }

    }

}
