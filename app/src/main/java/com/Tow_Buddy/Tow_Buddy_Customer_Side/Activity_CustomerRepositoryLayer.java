package com.Tow_Buddy.Tow_Buddy_Customer_Side;

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
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Activity_CustomerRepositoryLayer extends AppCompatActivity implements Runnable
{
    private String customerName, customerPhoneNumber, timeReceived, coordinates,  _employeePhoneNumber;
    private Integer ticketNumberFromCustomerHistory;
    public void run()
    {
        newCustomer();
        updateCustomerHistory(getEmployeeId());
    }
    public Activity_CustomerRepositoryLayer()
    {

    }

    public Activity_CustomerRepositoryLayer(String _customerName, //Sets appropriate values
                                            String _customerPhoneNumber,
                                            String _timeReceived,
                                            String _coordinates)
    {
        this.customerName = _customerName;
        this.customerPhoneNumber = _customerPhoneNumber;
        this.timeReceived = _timeReceived;
        this.coordinates = _coordinates;
    }

    //Make a new customer in the CustomerHistory table
    //Calls sortForAppropriateEmployee method
    public void newCustomer()
    {
        try
        {
            String jsonString = "{\"name\":\"" + customerName + "\", \"phoneNumber\":\"" + customerPhoneNumber + "\", \"timeReceived\":\"" + timeReceived + "\", \"coordinates\":\"" + coordinates + "\"}";
            URL url = new URL("http://35.182.176.62:8080/newCustomer");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            try( OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                byte[] input = jsonString.getBytes();
                outputStream.write(input, 0, input.length);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = bufferedReader.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
            }

            catch(Exception exception)
            {
                Log.e("OutputStreamError", exception.toString());
            }
            httpURLConnection.disconnect();
            sortForAppropriateEmployee();
        }
        catch (IOException exception)
        {
            Log.e("RepositoryError", exception.toString());
        }
    }

    //Calls getLargestTicketNumber method
    private void sortForAppropriateEmployee()  //Get logged in employees, sort for next to get a tow assigned
    {
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
        JSONArray jsonArray = new JSONArray(jsonDataFromDatabase); //This is an array of each employee entry
        JSONObject employeeFromDatabase;
        Integer lastAssignedTicketNumber = 500000;

        this.ticketNumberFromCustomerHistory = this.getLargestTicketNumber();

        for(int i = 0; i < jsonArray.length(); i++) //If there is no record of a ticket number, assign it to whoever's turn it is
        {
            employeeFromDatabase = jsonArray.getJSONObject(i);
            if(((employeeFromDatabase.get("CurrentTicketNumberAssigned")).toString())  == "null")                 //Did this employee get a ticket yet?
            {
                this._employeePhoneNumber =  employeeFromDatabase.getString("EmployeePhoneNumber");   //Everybody has a first time getting a ticket.
                sendCoordinates(this._employeePhoneNumber);                                           //If no ticket, send coordinates to this phone number and update table
                return;
            }
            if (i == (jsonArray.length() - 1))
            {
                for (int g = 0; g < jsonArray.length(); g++) //Everybody's got a ticket number, so assign it in the proper order
                {
                    employeeFromDatabase = jsonArray.getJSONObject(g);
                    if(employeeFromDatabase.getInt("CurrentTicketNumberAssigned") < lastAssignedTicketNumber) //If the employee could be up next
                    {
                        this._employeePhoneNumber = employeeFromDatabase.getString("EmployeePhoneNumber");
                        lastAssignedTicketNumber = employeeFromDatabase.getInt("CurrentTicketNumberAssigned");
                        if(lastAssignedTicketNumber == null)
                        {
                            lastAssignedTicketNumber = 0;
                        }
                    }
                }

            }
        }

        sendCoordinates(this._employeePhoneNumber);
    }
    catch(Exception exception)
    {
        Log.e("EmployeeSortError", exception.toString());
    }
    }
    private int getLargestTicketNumber() //Gets the largest ticket number in the database to provide an accurate one
    {

        Integer largestTicketNumber = 0;
        try
        {
            URL url = new URL("http://35.182.176.62:8080/ticketNumber");
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
            JSONArray arrayOfTicketNumbers = new JSONArray(response.toString());
            JSONObject jsonObjectResponse = arrayOfTicketNumbers.getJSONObject(0);
            largestTicketNumber = jsonObjectResponse.getInt("MAX(TicketNumber)");
        }
        catch(Exception exception)
        {
            Log.e("TicketNumberError", exception.toString());
        }

        return largestTicketNumber;
    }

    public void sendCoordinates(String employeePhoneNumber) //Send coordinates to employee
    {
        {
            try {
                String EMAIL_SUBJECT = "Test Send Email via SMTP";
                String EMAIL_TEXT = Activity_SetLocation.message;
                Properties prop = System.getProperties();
                prop.put(
                        "mail.smtp.auth",
                        "true");
                prop.put(
                        "mail.smtp.port",
                        "587"); // default port 25
                prop.put(
                        "mail.smtp.host",
                        "smtp.mail.yahoo.com");
                prop.put(
                        "mail.smtp.starttls.enable",
                        "true");
                Session session = Session.getInstance(prop,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("towbuddy@yahoo.com", "mttbMTTB1113");
                            }
                        });
                MimeMessage message = new MimeMessage(session);

                // from
                message.setFrom(new InternetAddress("towbuddy@yahoo.com"));

                // to
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(
                                employeePhoneNumber + "@tmomail.net", //This should be implemented properly when employee carrier details are known
                                false));

                // subject
                message.setSubject("New Tow from sender: " + Activity_SetLocation.customerPhoneNumber);

                // content
                message.setText(Activity_SetLocation.message);

                Transport.send(message);

                this.getIntent().putExtra("coordinateSendSuccess", true);

                runOnUiThread(new Toast_CoordinateSendingSucceeded(this));

            } catch (Exception exception)
            {
                Log.e("EmailError", exception.toString());
            }
        }
        updateEmployeeTicketNumber();
    }

    private void updateEmployeeTicketNumber()//Sets the assigned ticket for specific employee in the database
    {
        String jsonString = "{\"ticketNumber\":\"" + this.ticketNumberFromCustomerHistory + "\", \"employeePhoneNumber\":\"" + this._employeePhoneNumber + "\"}";
        try
        {
            URL url = new URL("http://35.182.176.62:8080/setEmployeeTicketStatus");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            try( OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                byte[] input = jsonString.getBytes();
                outputStream.write(input, 0, input.length);
            }
            InputStreamReader inputStreamReader = null;
            if(httpURLConnection.getErrorStream() != null)
            {
                inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
            }
            else if(httpURLConnection.getInputStream() != null)
            {
                inputStreamReader = new InputStreamReader((httpURLConnection.getInputStream()));
            }

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            httpURLConnection.disconnect();
        }
        catch(Exception exception)
        {
            Log.e("UpdateError", exception.toString());

        }
    }

    private void updateCustomerHistory(Integer employeeId)//Sets AssignedEmployeeId in CustomerHistory
    {

        String jsonString = "{\"employeeId\":\"" + employeeId + "\", \"coordinates\":\"" + this.coordinates + "\"}";
        try
        {
            URL url = new URL("http://35.182.176.62:8080/updateCustomerHistory");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            try( OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                byte[] input = jsonString.getBytes();
                outputStream.write(input, 0, input.length);
            }
            InputStreamReader inputStreamReader = null;
            if(httpURLConnection.getErrorStream() != null)
            {
                inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream());
            }
            else if(httpURLConnection.getInputStream() != null)
            {
                inputStreamReader = new InputStreamReader((httpURLConnection.getInputStream()));
            }

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            httpURLConnection.disconnect();
        }
        catch(Exception exception)
        {
            Log.e("UpdateError", exception.toString());

        }
    }

    private Integer getEmployeeId() //Gets EmployeeId from Employee table
    {
        Integer employeeId = 0;
        try {
            URL url = new URL("http://35.182.176.62:8080/specificEmployee");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            String requestString = "{\"employeePhoneNumber\":\"" + this._employeePhoneNumber + "\"}";

            try(OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                byte[] output = requestString.getBytes();
                outputStream.write(output);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            employeeId = Integer.parseInt(response.toString());
        }
        catch(Exception exception)
        {
            Log.e("ActiveTowScreenError", exception.toString());
        }
        return employeeId;
    }
}


