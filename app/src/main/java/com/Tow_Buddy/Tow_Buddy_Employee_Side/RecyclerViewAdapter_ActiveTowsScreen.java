package com.Tow_Buddy.Tow_Buddy_Employee_Side;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Tow_Buddy.R;

import java.util.ArrayList;

public class RecyclerViewAdapter_ActiveTowsScreen extends RecyclerView.Adapter<RecyclerViewAdapter_ActiveTowsScreen.CustomViewHolder>
{
    private ArrayList<String> arrayListOfAssignedTows;
    private Context context;

    public RecyclerViewAdapter_ActiveTowsScreen(Context context, ArrayList<String> arrayListOfAssignedTows)
    {
        this.arrayListOfAssignedTows = arrayListOfAssignedTows;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.active_tows_ui_rows, parentViewGroup, false);


        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int positionInArray) {
    customViewHolder.customerCoordinates.setText(arrayListOfAssignedTows.get(positionInArray));
    }

    @Override
    public int getItemCount() {
        return arrayListOfAssignedTows.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView customerCoordinates, customerDetails;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            customerCoordinates = itemView.findViewById(R.id.textView_CustomerCoordinates);
            customerDetails = itemView.findViewById(R.id.textView_CustomerDetails);
        }
    }
}
