//package com.carvis;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.mysampleapp.R;
//
//public class ListJourney extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list_journey);
//    }
//}

package com.carvis;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.carvis.MobileArrayAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

public class ListJourney extends ListActivity {



    Context context;
    CognitoUserPoolsSignInProvider provider;
    Journey journey;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> js = new ArrayList();
        js.add("List1");
        js.add("List2");
        js.add("List3");
        js.add("List4");
        js.add("List5");
 //setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));


        journey = new Journey();



        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);



        journey.getUsersJourneys(context, provider.getUserName());
//        //journey.getUsersJourneys(context, provider.getUserName());
//        for(int i= 0; i<=50; i++ ){
//            System.out.println(i);
//        }
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

                //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
                try {
                    ArrayList<Journey> js = journey.getListOfJourneys(context);
                    ArrayList<String> timestamps = new ArrayList<String>();
                    for(int i = 0; i< js.size(); i++){
                        timestamps.add(js.get(i).getStart());
                    }
                    setListAdapter(new MobileArrayAdapter(context, timestamps

                    ));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }, 3000);



//        ArrayList<String > j ;
//        j = journey.getListOfJourneys(context);
//        setListAdapter(new MobileArrayAdapter(this, j));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
    }

}
