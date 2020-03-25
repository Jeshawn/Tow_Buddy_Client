package com.Tow_Buddy;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.Nullable;
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

public class TowBuddyRepository extends AppCompatActivity implements Runnable
{
    private Context context;
    private String _employeePhoneNumber;
    private int ticketNumberFromCustomerHistory;
    public void run()
    {

    }
    //Creates a new employee in the Employee table
    public TowBuddyRepository(String Name, String PhoneNumber, String EmployeeId)
    {

    }
    //Creates a new Tow Buddy order in the CustomerHistory table
    public TowBuddyRepository(String Name, //Called from Customer side
                              String PhoneNumber,
                              String TimeReceived,
                              String Coordinates,
                              Context context)
    {
        try
        {
            this.context = context;
            String jsonString = "{\"name\":\"" + Name + "\", \"phoneNumber\":\"" + PhoneNumber + "\", \"timeReceived\":\"" + TimeReceived + "\", \"coordinates\":\"" + Coordinates + "\"}";
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

    private void sortForAppropriateEmployee() //Get logged in employees, sort through next for tow
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
            JSONArray jsonArray = new JSONArray(jsonDataFromDatabase);
            JSONObject tempJSONObject; //This is an array of each employee entry
            int lastAssignedTicketNumber = 500000;
            this.ticketNumberFromCustomerHistory = this.getLargestTicketNumber();
            for(int i = 0; i < jsonArray.length(); i++) //If there is no record of a ticket number, assign it to whoever's turn it is
            {
                tempJSONObject = jsonArray.getJSONObject(i);
                if((tempJSONObject.get("CurrentTicketNumberAssigned")).toString()  == "null")                 //Did this employee get a ticket yet?
                {
                    this._employeePhoneNumber =  tempJSONObject.getString("EmployeePhoneNumber");   //Everybody has a first time getting a ticket.
                    sendCoordinates(this._employeePhoneNumber);                                           //If no ticket, send coordinates to this phone number and update table
                    return;
                }
                if (i == (jsonArray.length() - 1))
                {
                    for (int g = 0; g < jsonArray.length(); g++) //Everybody's got a ticket number, so assign it in the proper order
                    {
                        tempJSONObject = jsonArray.getJSONObject(g);
                        if(tempJSONObject.getInt("CurrentTicketNumberAssigned") < lastAssignedTicketNumber) //If the employee could be up next
                        {
                            this._employeePhoneNumber = tempJSONObject.getString("EmployeePhoneNumber");
                            lastAssignedTicketNumber = tempJSONObject.getInt("CurrentTicketNumberAssigned");
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

    public void sendCoordinates(String employeePhoneNumber) //Send coordinates to employee
    {
        try
        {
            String EMAIL_SUBJECT = "Test Send Email via SMTP";
            String EMAIL_TEXT = MainActivity.message;
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
            message.setSubject("New Tow from sender: " + MainActivity.customerPhoneNumber);

            // content
            message.setText(MainActivity.message);

            Transport.send(message);

            runOnUiThread(new Dialog_CoordinateSendingSuccess(this.context));
        }
        catch (Exception exception)
        {
            Log.e("EmailError", exception.toString());
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
    private int getLargestTicketNumber() //Gets the largest ticket number in the database to provided an accurate one
    {
        @Nullable
        int largestTicketNumber = 0;
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
            JSONArray jsonArray1 = new JSONArray(response.toString());
            JSONObject jsonObject = jsonArray1.getJSONObject(0);
            largestTicketNumber = jsonObject.getInt("MAX(TicketNumber)");
        }
        catch(Exception exception)
        {
            Log.e("TicketNumberError", exception.toString());
        }

        return largestTicketNumber;
    }


}


