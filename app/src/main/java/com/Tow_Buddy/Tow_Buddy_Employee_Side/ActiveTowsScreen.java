package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.Tow_Buddy.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ActiveTowsScreen extends AppCompatActivity
{
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    public void ActiveTowsScreen()
    {
        getActiveTows();
        this.arrayAdapter = new ArrayAdapter<String>(this, R.layout.active_tows, this.arrayList);
        ListView listView = (ListView) findViewById(R.id.ListOfTows);
        listView.setAdapter(arrayAdapter);
    }

    private void getActiveTows()
    {
        try
        {
            URL url = new URL("http://35.182.176.62:8080/assignedTows");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream())
            {
                String input = "{\"employeeId\":\""
                        + this.getIntent().getCharArrayExtra("employeeId").toString() + "\"";
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
            String jsonResponse = response.toString();
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                this.arrayList.add(jsonArray.getString(i));
            }


        }
        catch(Exception exception)
        {
            Log.e("ActiveTowScreenError", exception.toString());
        }

    }

    private void copyCoordinates()
    {

    }

}
