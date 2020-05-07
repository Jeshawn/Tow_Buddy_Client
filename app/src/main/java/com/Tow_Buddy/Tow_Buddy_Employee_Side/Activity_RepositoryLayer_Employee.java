package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
public class Activity_RepositoryLayer_Employee extends AppCompatActivity implements Runnable //Network on Main Thread exception without Runnable
{

    private String employeeName, employeePhoneNumber;
    private int employeeId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getApplicationContext();
        this.employeeName = getIntent().getStringExtra("EmployeeName");
        this.employeePhoneNumber = getIntent().getStringExtra("EmployeePhoneNumber");
        this.employeeId = getIntent().getIntExtra("EmployeeId", 0);
        new Thread(this).start(); //Done to run this on its own thread, keeping the networking code away from the Main Thread
    }

    @Override
    public void run()
    {
        attemptLogin();
    }
    public void attemptLogin()
    {
        if(employeeSignedInToday())
        {
            Looper.getMainLooper().prepare();
            runOnUiThread(new Toast_SuccessfullyLoggedIn(this));
            Intent towScreenIntent = new Intent(this, Activity_ActiveTowsScreen.class);
            towScreenIntent.putExtra("EmployeeId", getIntent().getIntExtra("EmployeeId", 0));
            startActivity(towScreenIntent);
        }
        else
        {
            boolean loginSuccessful = false;
            try
            {
                URL url = new URL("http://35.182.176.62:8080/employeeLogin");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                try (OutputStream outputStream = httpURLConnection.getOutputStream())
                {
                    String input = "{\"employeeName\":\""
                            +
                            getIntent().getStringExtra("EmployeeName")
                            + "\", \"employeePhoneNumber\":\""
                            + getIntent().getStringExtra("EmployeePhoneNumber")
                            + "\", \"employeeId\":\""
                            + getIntent().getIntExtra("EmployeeId", 0)
                            + "\"}";
                    byte[] outputString = input.getBytes();
                    outputStream.write(outputString, 0, outputString.length);
                }
                StringBuilder response = new StringBuilder();
                String responseLine;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((responseLine = bufferedReader.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
                Integer serverStatusCode =  httpURLConnection.getResponseCode();
                if(serverStatusCode == 200)
                {
                    httpURLConnection.disconnect();
                    loginSuccessful = true;
                    Log.i("Success", serverStatusCode.toString());
                    Looper.getMainLooper().prepare();
                    runOnUiThread(new Toast_SuccessfullyLoggedIn(this));
                    Intent towScreenIntent = new Intent(this, Activity_ActiveTowsScreen.class);
                    towScreenIntent.putExtra("EmployeeId", getIntent().getIntExtra("EmployeeId", 0));
                    startActivity(towScreenIntent);
                }
                if(loginSuccessful == false)
                {
                    new Thread(new Toast_EmployeeLoginFailed(this)).start();
                    throw new Exception("Response from server status was not 200");
                }
            }
            catch (Exception exception)
            {
                Log.e("EmployeeRepositoryError", exception.toString());
            }
        }
    }
    public void attemptLogout()
    {
        try
        {
            URL url = new URL("http://35.182.176.62:8080/deleteEmployee");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("DEL");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                String input = "{\"employeeName\":\""
                        + getIntent().getStringExtra("EmployeeName")
                        + "\"}".getBytes();
                byte[] outputString = input.getBytes();
                outputStream.write(outputString, 0, outputString.length);
            }
            StringBuilder response = new StringBuilder();
            String responseLine;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((responseLine = bufferedReader.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            String jsonDataFromDatabase = response.toString();
            Log.i("Success", jsonDataFromDatabase);
            httpURLConnection.disconnect();
        }
        catch (Exception exception)
        {
            Log.e("EmployeeRepositoryError", exception.toString());
        }
    }
    private boolean employeeSignedInToday() //If employee is logged in, return true.
    {
        boolean existsInDatabase = false;
        try
        {
            URL url = new URL("http://35.182.176.62:8080/getLoggedInEmployees");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            String jsonDataFromDatabase = response.toString();
            httpURLConnection.disconnect();
            JSONArray jsonArray = new JSONArray(jsonDataFromDatabase);
            int userInputIdNumber = (getIntent().getIntExtra("EmployeeId", 0));
            JSONObject tempJSONObject;
            int employeeIdFromDatabase; //Will store an id from EmployeeSignInOrder table

            for (int i = 0; i < jsonArray.length(); i++) //Iterates through response for employeeId number
            {
                tempJSONObject = jsonArray.getJSONObject(i);
                employeeIdFromDatabase = tempJSONObject.getInt("EmployeeId");
                if (employeeIdFromDatabase == userInputIdNumber)
                {
                    existsInDatabase = true;
                    this.getIntent().putExtra("EmployeeId", employeeIdFromDatabase);
                    break;
                }
            }
        }
        catch (Exception exception)
        {
            Log.e("EmployeeRepositoryError", exception.toString());
        }
        return existsInDatabase;
    }
}
