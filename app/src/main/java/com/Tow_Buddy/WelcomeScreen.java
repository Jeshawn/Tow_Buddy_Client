package com.Tow_Buddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.Tow_Buddy.Tow_Buddy_Customer_Side.Activity_SetLocation;
import com.Tow_Buddy.Tow_Buddy_Employee_Side.Activity_EmployeeLoginPage;

public class WelcomeScreen extends AppCompatActivity
{
    public String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        getApplicationContext();
    }
    public void beginMainActivity(View view)
    {
        try {
            EditText customerPhoneNumber =  findViewById(R.id.customerPhoneNumber);
            EditText customerName = findViewById(R.id.customerName);
            if (customerPhoneNumber.getText().toString().length() != 10) {
                incorrectPhoneNumberFormat();
                return;
            }
            String phoneNumber = customerPhoneNumber.getText().toString();
            Intent intent = new Intent(this, Activity_SetLocation.class);
            intent.putExtra("customerPhoneNumber", phoneNumber);
            intent.putExtra("customerName", customerName.getText().toString());
            startActivity(intent);
        }
        catch(Exception exception)
        {
            Log.e("Error", exception.toString());
        }
    }

    public void employeeLoggingIn(View view)
    {
         Intent intent = new Intent(this, Activity_EmployeeLoginPage.class);
         startActivity(intent);
    }
    public void incorrectPhoneNumberFormat()
    {
        EditText phoneNumberString = (EditText)findViewById(R.id.customerPhoneNumber);
        phoneNumberString.setText("");
        phoneNumberString.setHint("Use 10 digit format.");
    }

}
