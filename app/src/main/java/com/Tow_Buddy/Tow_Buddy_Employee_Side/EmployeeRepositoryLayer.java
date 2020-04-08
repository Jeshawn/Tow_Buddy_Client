package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployeeRepositoryLayer extends AppCompatActivity
{
    public void attemptLogin()
    {
        if(!checkForDatabaseEntry())
        {
            badLogin();
        }
        else
        {
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
                            + this.getIntent().getCharArrayExtra("employeeName").toString()
                            + "\", \"employeePhoneNumber\":\""
                            + this.getIntent().getCharArrayExtra("employeePhoneNumber").toString()
                            + this.getIntent().getCharArrayExtra("employeeId").toString()
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
                        + this.getIntent().getCharArrayExtra("employeeId").toString()
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
    private boolean checkForDatabaseEntry() {
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
            JSONObject tempJSONObject;
            for (int i = 0; i < jsonArray.length(); i++) //If there is no record of a ticket number, assign it to whoever's turn it is
            {
                tempJSONObject = jsonArray.getJSONObject(i);
                if (tempJSONObject.getInt("EmployeeId") == this.getIntent().getIntExtra("employeeId", -1))
                {
                    existsInDatabase = true;
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
    public void badLogin()
    {
        Dialog_IncorrectCredentials dialog_incorrectCredentials = new Dialog_IncorrectCredentials();
        dialog_incorrectCredentials.show(getSupportFragmentManager(), "IncorrectLogin");
    }
}
