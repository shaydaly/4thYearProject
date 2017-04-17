package com.carvis;
import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysampleapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MobileArrayAdapter extends  ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> journeys;
    private final ArrayList<String> timestamps;
    private final ArrayList<String> durations;


    public MobileArrayAdapter(Context context, ArrayList<String> journeys, ArrayList<String> timestamps,ArrayList<String> durations ) {
       super(context, R.layout.activity_list_journey, journeys);
        this.context = context;
        this.journeys = journeys;
        this.timestamps = timestamps;
        this.durations = durations;

    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            View rowView = inflater.inflate(R.layout.activity_list_journey, parent, false);
        try {
            TextView journeyIDtextView = (TextView) rowView.findViewById(R.id.journeyID);
            //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
            journeyIDtextView.setText(journeys.get(position));
            journeyIDtextView.setTextColor(Color.BLACK);

            TextView timestampView = (TextView) rowView.findViewById(R.id.timestamp);
            //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
            timestampView.setText(timestamps.get(position));


            TextView durationsView = (TextView) rowView.findViewById(R.id.duration);
            //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
            durationsView.setText(durations.get(position));
            return rowView;
        }
        catch (Exception e){
            Log.wtf("mobilearray", e.getMessage());
        }
        return rowView;
    }
}
