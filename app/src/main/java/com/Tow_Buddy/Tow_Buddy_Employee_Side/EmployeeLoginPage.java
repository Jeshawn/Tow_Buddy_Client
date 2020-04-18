package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.Tow_Buddy.R;

public class EmployeeLoginPage extends AppCompatActivity implements Runnable
{
    public static String static_name, static_phoneNumber, static_employeeIdNumber;
    public void run()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_login_page);
    }
    public void employeeLogin(View view)
    {
        Intent intent = new Intent(this, ActiveTowsScreen.class);
        EditText employeeName = findViewById(R.id.EmployeeName);
        EditText employeePhoneNumber =  findViewById(R.id.EmployeePhoneNumber);
        EditText employeeId = findViewById(R.id.EmployeeIdNumber);
        if (employeePhoneNumber.getText().toString().length() != 10)
        {
            incorrectPhoneNumberFormat();
            return;
        }
        static_name = employeeName.getText().toString();
        static_phoneNumber = employeePhoneNumber.getText().toString();
        static_employeeIdNumber = employeeId.getText().toString();
        getMainLooper().prepare();
        new Thread(new EmployeeRepositoryLayer()).start();
    }
    public void employeeLogout(View view)
    {

    }
    public void incorrectPhoneNumberFormat()
    {
        EditText phoneNumberString = (EditText)findViewById(R.id.EmployeePhoneNumber);
        phoneNumberString.setText("");
        phoneNumberString.setHint("Use 10 digit format.");
    }
}
