package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.Tow_Buddy.R;

public class EmployeeLoginPage extends AppCompatActivity
{
    public void employeeLogin(View view)
    {
        Intent intent = new Intent(this, EmployeeRepositoryLayer.class);
        EditText employeePhoneNumber =  findViewById(R.id.EmployeePhoneNumber);
        EditText employeeName = findViewById(R.id.EmployeeName);
        EditText employeeId = findViewById(R.id.EmployeeIdNumber);
        if (employeePhoneNumber.getText().toString().length() != 10) {
            incorrectPhoneNumberFormat();
            return;
        }
        String phoneNumber = employeePhoneNumber.getText().toString();
        intent.putExtra("employeePhoneNumber", phoneNumber);
        intent.putExtra("employeeName", employeeName.getText().toString());
        intent.putExtra("employeeId", employeeId.toString());
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
