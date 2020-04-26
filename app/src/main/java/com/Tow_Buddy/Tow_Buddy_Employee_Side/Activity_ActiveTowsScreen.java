package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.Tow_Buddy.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Activity_ActiveTowsScreen extends AppCompatActivity implements Runnable
{
    private ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_tows);
        getApplicationContext();
        new Thread(this).start();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void run()
    {
        populateTows();
    }
    public void populateTows()
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
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream())
            {

                int employeeId = this.getIntent().getIntExtra("EmployeeId", 0);
                String input = "{\"employeeId\":\""
                        + employeeId
                        + "\"}";
                byte[] outputByteArray = input.getBytes();
                outputStream.write(outputByteArray, 0, outputByteArray.length);
            }
            StringBuilder response = new StringBuilder();
            String responseLine;
            Log.i("Response", httpURLConnection.getResponseMessage());
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
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
        String towData;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //ClipData clip = ClipData.newPlainText("TowCoordinates", );
       // clipboard.setPrimaryClip(clip);
    }
}
