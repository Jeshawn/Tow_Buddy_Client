package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.Tow_Buddy.R;

public class Activity_EmployeeLoginPage extends AppCompatActivity
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getApplicationContext();
        setContentView(R.layout.employee_login_page);

    }
    public void employeeLogin(View view)
    {
        Intent intent = new Intent(this, Activity_EmployeeRepositoryLayer.class);
        EditText employeeName = findViewById(R.id.EmployeeName);
        EditText employeePhoneNumber =  findViewById(R.id.EmployeePhoneNumber);
        EditText employeeId = findViewById(R.id.EmployeeIdNumber);
        if (employeePhoneNumber.getText().toString().length() != 10)
        {
            incorrectPhoneNumberFormat();
            return;
        }
        intent.putExtra("EmployeeName", employeeName.getText().toString());
        intent.putExtra("EmployeePhoneNumber", employeePhoneNumber.getText().toString());
        intent.putExtra("EmployeeId", Integer.parseInt(employeeId.getText().toString()));
        startActivity(intent);

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
