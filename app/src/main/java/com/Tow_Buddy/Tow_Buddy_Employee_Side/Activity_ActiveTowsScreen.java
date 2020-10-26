package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.Tow_Buddy.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Activity_ActiveTowsScreen extends AppCompatActivity
{
    private ArrayList<String> arrayList;
    RecyclerView recyclerView;
    private RecyclerViewAdapter_ActiveTowsScreen recyclerViewAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_tows_ui_rows);
        this.arrayList = this.getIntent().getStringArrayListExtra("arrayListFromDatabase");
        this.recyclerView = findViewById(R.id.recyclerView_activeTows);
        this.recyclerViewAdapter = new RecyclerViewAdapter_ActiveTowsScreen(this, this.arrayList);
        this.recyclerView.setAdapter(this.recyclerViewAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getApplicationContext();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    //Button for copying customer coordinates to a clipboard
    private void copyCoordinates()
    {
        String towData;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //ClipData clip = ClipData.newPlainText("TowCoordinates", );
       // clipboard.setPrimaryClip(clip);
    }
}
