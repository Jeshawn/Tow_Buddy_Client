package com.Tow_Buddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class WelcomeScreen extends AppCompatActivity
{
    public String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
    }
    public void beginMainActivity(View view)
    {
        try {
            EditText customerPhoneNumber =  findViewById(R.id.customerPhoneNumber);
            EditText customerName = findViewById(R.id.customerName);
            Intent intent = new Intent(this, MainActivity.class);
            if (customerPhoneNumber.getText().toString().length() != 10) {
                incorrectPhoneNumberFormat();
                return;
            }
            String phoneNumber = customerPhoneNumber.getText().toString();
            intent.putExtra("customerPhoneNumber", phoneNumber);
            intent.putExtra("customerName", customerName.getText().toString());
            startActivity(intent);
        }
        catch(Exception exception)
        {
            Log.e("Error", exception.toString());
        }


    }
    public void incorrectPhoneNumberFormat()
    {
        EditText phoneNumberString = (EditText)findViewById(R.id.customerPhoneNumber);
        phoneNumberString.setText("");
        phoneNumberString.setHint("Use 10 digit format.");
    }

}
