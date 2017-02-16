package com.carvis;
import android.content.Context;
import android.graphics.Color;
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


    public MobileArrayAdapter(Context context, ArrayList<String> journeys, ArrayList<String> timestamps) {
       super(context, R.layout.activity_list_journey, journeys);
        this.context = context;
        this.journeys = journeys;
        this.timestamps = timestamps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_list_journey, parent, false);
        TextView journeyIDtextView = (TextView) rowView.findViewById(R.id.journeyID);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        journeyIDtextView.setText(journeys.get(position));
        journeyIDtextView.setTextColor(Color.BLACK);

        TextView timestampView = (TextView) rowView.findViewById(R.id.timestamp);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        timestampView.setText(timestamps.get(position));
        timestampView.setTextColor(Color.RED);

        // Change icon based on name
//        String s = values.get(position);
//
//        System.out.println(s);

//        if (s.equals("WindowsMobile")) {
//            imageView.setImageResource(R.drawable.windowsmobile_logo);
//        } else if (s.equals("iOS")) {
//            imageView.setImageResource(R.drawable.ios_logo);
//        } else if (s.equals("Blackberry")) {
//            imageView.setImageResource(R.drawable.blackberry_logo);
//        } else {
//            imageView.setImageResource(R.drawable.android_logo);
//        }

        return rowView;
    }
}
