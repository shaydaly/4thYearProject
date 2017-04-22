package com.CARVISAPP.demo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;

import com.carvis.CarvisFireBaseMessagingService;
import com.carvis.NewListJourney;
import com.carvis.OnSwipeTouchListener;
import com.carvis.SpeedCameraMap;
import com.carvis.TrackSpeedActivity;
import com.carvis.UserStatistics;
import com.carvis.VolleyService;
import com.CARVISAPP.R;
import com.CARVISAPP.SplashActivity;

import java.util.ArrayList;
import java.util.Random;

public class HomeDemoFragment extends DemoFragmentBase {


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    int count, modulo;

    CognitoUserPoolsSignInProvider provider;
    VolleyService v ;
    Context context;
    public static TextView textViewObj;
    TextView overSpeedDayTV, roadsToAvoid, todayTrafficIncident, daysSinceOverSpeed, numIncidentsReported ;
    BroadcastReceiver mBroadcastReceiver;
    Random random ;
    ArrayList<TextView> textViews;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
       provider = new CognitoUserPoolsSignInProvider(context);


        return inflater.inflate(R.layout.fragment_demo_home, container, false);

    }

    @Override
    public void onPause(){
        super.onPause();


        if(daysSinceOverSpeed!= null){
            daysSinceOverSpeed.setText("");
        }
        if(roadsToAvoid!= null){
            roadsToAvoid.setText("");
        }
        if(overSpeedDayTV!= null){
            overSpeedDayTV.setText("");
        }
        if(todayTrafficIncident!= null){
            todayTrafficIncident.setText("");
        }
        if(numIncidentsReported!= null){
            numIncidentsReported.setText("");
        }
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onStop(){
        super.onStop();
//        context.unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onResume(){
        super.onResume();
        //getPermissions();
         count = 0;

        modulo = 0;
        getPermissions();
        refreshToken();



        Intent firebaseIntent = new Intent(context, CarvisFireBaseMessagingService.class);
        context.startService(firebaseIntent);

        daysSinceOverSpeed = (TextView) getActivity().findViewById(R.id.daysSinceOverSpeed);
//        daysSinceOverSpeed.setText("hello`1");
        roadsToAvoid = (TextView) getActivity().findViewById(R.id.roadsToAvoid);
//        roadsToAvoid.setText("hello2");
        overSpeedDayTV = (TextView) getActivity().findViewById(R.id.overSpeedDay);
        todayTrafficIncident = (TextView) getActivity().findViewById(R.id.todayTrafficIncident);
        todayTrafficIncident.setMovementMethod(new ScrollingMovementMethod());
//        todayTrafficIncident.setText("hello4");
        numIncidentsReported = (TextView)getActivity().findViewById(R.id.numTrafficReported);
//        numIncidentsReported.setText("hell5");

        textViews = new ArrayList<>();
        textViews.add(daysSinceOverSpeed);
        textViews.add(roadsToAvoid);
        textViews.add(overSpeedDayTV);
        textViews.add(todayTrafficIncident);
        textViews.add(numIncidentsReported);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String greeting  = "Hi " + provider.getUserName()+". \n";
               // String greeting  = "";
                try {
                    //int days = PreferenceManager.getDefaultSharedPreferences(context).getInt("daysSinceOverSpeed", 0);

                    int days = intent.getIntExtra("daysSinceOverSpeed", 0);
                    String displayMessage = "";
                    String over2="";
                    if (days != -99) {
                        String over1 = getString(R.string.daysSinceLastOverSpeed1);
                        if(days==1) {
                            over2 = getString(R.string.daysSinceLastOverSpeed3);
                        }
                        else{
                            over2 = getString(R.string.daysSinceLastOverSpeed2);
                        }
//                        String over2 = getString(R.string.daysSinceLastOverSpeed2);
                        displayMessage = greeting + over1 + " " + days + " " + over2 + ". ";
                        if (days < 3) {
                            displayMessage = displayMessage + getString(R.string.slowingDown);
                        } else {
                            displayMessage = displayMessage + "\n" + getString(R.string.wellDone);
                        }

                    }
                    else {
                        displayMessage= greeting +displayMessage+ getString(R.string.noOverSpeeds);
                    }
                    daysSinceOverSpeed.setText(displayMessage);
                }
                catch(Exception e) {
                }
                try {
                    String day = intent.getStringExtra("overSpeedDay");
                    String overSpeedDay = PreferenceManager.getDefaultSharedPreferences(context).getString("overSpeedDate", "");
                    String textOutput = "";
                    if (!day.equals("NA")) {
                        textOutput = greeting + getString(R.string.overSpeedDay)
                                + " " + day + ". " + getString(R.string.alternativeRoute);
                    } else {
                        textOutput = greeting + getString(R.string.noOverSpeedDay);
                    }
                    overSpeedDayTV.setText(textOutput);
                }
                catch (Exception ex) {

                }
                try {

                    //int numTrafficIncidentsReported = PreferenceManager.getDefaultSharedPreferences(context).getInt("numTrafficIncidentsReported", 0);
                    int numTrafficIncidentsReported = intent.getIntExtra("numTrafficIncidentsReported", 0);
                    String output =  greeting +numTrafficIncidentsReported + " " ;
                    if(numTrafficIncidentsReported ==1){
                        output+= getString(R.string.numTrafficIncidents2);
                    }
                    else {
                        output+= getString(R.string.numTrafficIncidents);
                    }
                    if (numTrafficIncidentsReported > 0) {
                        output += ". " + getString(R.string.thanks);
                    } else {
                        output += ". " + getString(R.string.promtTraffic);
                    }
                    numIncidentsReported.setText(output);
                }
                catch(Exception e) {

                }

                try{
                    String output ="";
                    ArrayList<String> addresses = intent.getStringArrayListExtra("trafficAddresses");
                    //String output ="";
                    if(addresses!=null) {
                        if (addresses.size() != 0) {
                            output += getString(R.string.roadsWithOverspeed) + "\n\n";
                            for (String a : addresses) {
                                output += a + "\n";

                            }
                        } else {
                            output += getString(R.string.noRoadsWithOverspeed);
                        }
                    }
                    else{
                        output += getString(R.string.noRoadsWithOverspeed);
                    }
//                        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
//
//                        textView.setTypeface(boldTypeface);
//
                    todayTrafficIncident.setText(greeting+output);

                    try {
                        ArrayList<String> roadAddresses = intent.getStringArrayListExtra("addresses");
                        String output2 = "";

                        if (roadAddresses != null) {
                            if (roadAddresses.size() != 0) {
                                output2 +=getString(R.string.pastWeekRoad) + "\n\n";
                                for (String a : roadAddresses) {
                                    output2 += a + "\n";
                                }
                            } else {
                                output2 = getString(R.string.noPastWeekRoad);
                            }
                            roadsToAvoid.setText(greeting+""+output2);
                        }
                        else{
                            roadsToAvoid.setText(greeting+" There are no roads you have used where bad traffic has been reported");
                        }
                    }
                    catch (Exception e){

                    }
//                        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
//
//                        textView.setTypeface(boldTypeface);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };


        IntentFilter filter = new IntentFilter(VolleyService.DAYS_OVER_SPEED);
        filter.addAction(VolleyService.OVERSPEEDDAY);
        filter.addAction(VolleyService.NUMTRAFFICINCIDENTS);
        filter.addAction(VolleyService.ROADSWITHINCIDENTS);
        filter.addAction(VolleyService.ROADSTOAVOID);
        getActivity().registerReceiver(mBroadcastReceiver, filter);



       random = new Random();
        int randomNumber = random.nextInt(5-1) + 1;

        showTextView(randomNumber);
        //textView = (TextView) getActivity().findViewById(R.id.daysSinceOverSpeed);
        //textView = (TextView) getActivity().findViewById(R.id.daysSinceOverSpeed);
        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.messageContainer);
        layout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.wtf("Swipe right", "Swipe left");
                count--;

                modulo = (count % 5);
                if(modulo<0)
                    modulo+=5;
                showTextView(modulo);
//                Toast.makeText(context, "modul---"+String.valueOf(modulo), Toast.LENGTH_SHORT).show();
            }


        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            Log.wtf("Swipe right", "Swipe right");
            count++;
            modulo = (count % 5);
            if(modulo<0)
                modulo+=5;
            showTextView(modulo);
//            Toast.makeText(context, "modul---"+String.valueOf(modulo), Toast.LENGTH_SHORT).show();
            // Put your logic here for text visibility and for timer like progress bar for 5 second and setText
        }

    });


        if(SplashActivity.justSignedin) {
            v = new VolleyService(context);
            v.getDaysSinceLastOverSpeed(provider, context);
        }
        SplashActivity.justSignedin = false;

        registerButtons();


//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.i("volley received","");
//
//                String greeting  = "Hi " + provider.getUserName();
//                try {
//                    if (intent.getAction().equals(VolleyService.DAYS_OVER_SPEED)) {
//                        int days = PreferenceManager.getDefaultSharedPreferences(context).getInt("daysSinceOverSpeed", 0);
//                        String displayMessage="";
//                        if(days!= -99) {
//                            String over1 = getString(R.string.daysSinceLastOverSpeed1);
//                            String over2 = getString(R.string.daysSinceLastOverSpeed2);
//                            displayMessage = greeting + ". \n" + over1 + " " + days + " " + over2 + ". ";
//
//
//                            if (days < 3) {
//                                displayMessage = displayMessage + getString(R.string.slowingDown);
//                            } else {
//                                displayMessage = displayMessage + "\n" + getString(R.string.wellDone);
//                            }
//
//                        }
//                        else{
//                            displayMessage = greeting + ". \nYou have no speeding incidents recorded. Congratulations";
//                        }
//                        textView.setText(displayMessage);
//                    }
//
//                    else if(intent.getAction().equals(VolleyService.OVERSPEEDDAY)){
//                        String overSpeedDay =  PreferenceManager.getDefaultSharedPreferences(context).getString("overSpeedDate", "");
//                        String textOutput ="";
//                        if(!overSpeedDay.equals("NA")) {
//                             textOutput = greeting + ". " + getString(R.string.overSpeedDay)
//                                    + " " + overSpeedDay + ". " + getString(R.string.alternativeRoute);
//                        }
//                        else{
//                            textOutput = greeting + getString(R.string.noOverSpeeds);
//                        }
//                        textView.setText(textOutput);
//                    }
//
//                    else if(intent.getAction().equals(VolleyService.NUMTRAFFICINCIDENTS)){
//                        int numTrafficIncidentsReported = PreferenceManager.getDefaultSharedPreferences(context).getInt("numTrafficIncidentsReported", 0);
//                        String output = greeting+". "+numTrafficIncidentsReported+" "+getString(R.string.numTrafficIncidents);
//                        if(numTrafficIncidentsReported > 0){
//                            output+=". "+getString(R.string.thanks);
//                        }
//                        else{
//                            output+=". "+getString(R.string.promtTraffic);
//                        }
//                        textView.setText(output);
//                    }
//
//                    else if(intent.getAction().equals(VolleyService.ROADSWITHINCIDENTS)){
//                        ArrayList<String> addresses = intent.getStringArrayListExtra("addresses");
//                        String output ="";
//                        if(addresses.size()!= 0) {
//                            output += getString(R.string.roadsWithOverspeed) + "\n\n";
//                            for (String a : addresses) {
//                                output += a + "\n";
//                            }
//                        }
//                        else{
//                            output+= getString(R.string.noRoadsWithOverspeed);
//                        }
////                        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
////
////                        textView.setTypeface(boldTypeface);
////
//                        textView.setText(greeting+"\n"+output);
//                    }
//                    else if(intent.getAction().equals(VolleyService.ROADSTOAVOID)){
//                        ArrayList<String> addresses = intent.getStringArrayListExtra("addresses");
//                        String output ="";
//                        if(addresses.size()!= 0) {
//                            output += getString(R.string.pastWeekRoad)+"\n";
//                            for (String a : addresses) {
//                                output += a + "\n";
//                            }
//                        }
//                        else{
//                            output = getString(R.string.noPastWeekRoad);
//                        }
////                        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
////
////                        textView.setTypeface(boldTypeface);
////
//                        textView.setText(greeting+"\n"+output);
//                    }
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//            }
//        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

//        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getActivity()     , "El Bluetooth debe estar activado para continuar", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        }
    }



    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DemoListAdapter adapter = new DemoListAdapter(getActivity());
        adapter.addAll(DemoConfiguration.getDemoFeatureList());



//        textViewObj = (TextView) view.findViewById(R.id.daysSinceOverSpeed);


        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final DemoConfiguration.DemoFeature item = adapter.getItem(position);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    final Fragment fragment = DemoInstructionFragment.newInstance(item.name);

                    activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, item.name)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                    // Set the title for the fragment.
                    final ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(item.titleResId);
                    }
                }
            }
        });
    }

    private static final class DemoListAdapter extends ArrayAdapter<DemoConfiguration.DemoFeature> {
        private LayoutInflater inflater;

        public DemoListAdapter(final Context context) {
            super(context, R.layout.list_item_icon_text_with_subtitle);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.list_item_icon_text_with_subtitle, parent, false);
                holder = new ViewHolder();
                holder.iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);
                holder.titleTextView = (TextView) view.findViewById(R.id.list_item_title);
                holder.subtitleTextView = (TextView) view.findViewById(R.id.list_item_subtitle);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }

            DemoConfiguration.DemoFeature item = getItem(position);
            holder.iconImageView.setImageResource(item.iconResId);
            holder.titleTextView.setText(item.titleResId);
            holder.subtitleTextView.setText(item.subtitleResId);

            return view;
        }
    }

    private static final class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView subtitleTextView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.wtf("request permission", " result");
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

        }
    }




    public void registerButtons(){

        RelativeLayout startRel = (RelativeLayout) getActivity().findViewById(R.id.startJourneyRelative);
        startRel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(getActivity(), TrackSpeedActivity.class);
               getActivity().startActivity(myIntent);
            }
        });

        RelativeLayout prevRel = (RelativeLayout) getActivity().findViewById(R.id.prevJourneyRel);
        prevRel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(getActivity(), NewListJourney.class);
                getActivity().startActivity(myIntent);
            }
        });


        RelativeLayout mapRel = (RelativeLayout) getActivity().findViewById(R.id.mapRel);
        mapRel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(getActivity(), SpeedCameraMap.class);
                getActivity().startActivity(myIntent);
            }
        });

        RelativeLayout statRel = (RelativeLayout) getActivity().findViewById(R.id.statsRel);
        statRel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               Intent myIntent = new Intent(getActivity(), UserStatistics.class);
//               //Intent myIntent = new Intent(getActivity(), CameraRecorder.class);
                getActivity().startActivity(myIntent);
            }
        });

//        Button startJourney = (Button) getActivity().findViewById(R.id.startJourney);
//        startJourney.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getActivity(), TrackSpeedActivity.class);
//                getActivity().startActivity(myIntent);
//            }
//        });
//        Button prevJourneys = (Button) getActivity().findViewById(R.id.prevJourney);
//        prevJourneys.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getActivity(), NewListJourney.class);
//                getActivity().startActivity(myIntent);
//            }
//        });
//
//        Button speedCameraMap = (Button) getActivity().findViewById(R.id.speedCameraMap);
//        speedCameraMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getActivity(), SpeedCameraMap.class);
//                getActivity().startActivity(myIntent);
//            }
//        });
//
//        Button userSettings = (Button) getActivity().findViewById(R.id.statistics);
//        userSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getActivity(), UserStatistics.class);
//               //Intent myIntent = new Intent(getActivity(), CameraRecorder.class);
//                getActivity().startActivity(myIntent);
//            }
//        });
    }


    private void getPermissions(){
        int PERMISSION_ALL = 1;

        if(!hasPermissions(context, INITIAL_PERMS)){
            ActivityCompat.requestPermissions(getActivity(), INITIAL_PERMS, PERMISSION_ALL);
        }

    }


    private void showTextView(int tvNum){
        for(TextView tv : textViews){
            tv.setVisibility(View.INVISIBLE);
        }

        if(tvNum == 0){
            overSpeedDayTV.setVisibility(View.VISIBLE);
//            Log.wtf("",String.valueOf(overSpeedDayTV.getText()));
        }

        else if(tvNum ==1){
            roadsToAvoid.setVisibility(View.VISIBLE);
//            Log.wtf("",String.valueOf(roadsToAvoid.getText()));
        }
        else if(tvNum ==2) {
            todayTrafficIncident.setVisibility(View.VISIBLE);
//            Log.wtf("",String.valueOf(todayTrafficIncident.getText()));
        }
        else if(tvNum ==3) {
            daysSinceOverSpeed.setVisibility(View.VISIBLE);
//            Log.wtf("",String.valueOf(daysSinceOverSpeed.getText()));
        }
        else if(tvNum ==4) {
            numIncidentsReported.setVisibility(View.VISIBLE);
//            Log.wtf("",String.valueOf(numIncidentsReported.getText()));
        }
    }


    private void refreshToken(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                provider.refreshToken();
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }






    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
