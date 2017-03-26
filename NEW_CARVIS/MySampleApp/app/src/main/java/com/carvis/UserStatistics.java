package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.mysampleapp.R;

public class UserStatistics extends AppCompatActivity {
    Context context;
    VolleyService volleyService;
    CognitoUserPoolsSignInProvider provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("user statistics called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        goToUserStats();
    }
//    @Override
//    public void onBackPressed() {
//        Intent myIntent = new Intent(context, com.mysampleapp.MainActivity.class);
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(myIntent);
//    }

//    @Override
//    protected void onStop(){
//        super.onStop();
//    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        //goToUserStats();
//        //super.onStop();
//    }
//
    @Override
    protected void onPause(){
        super.onStop();
    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//    }

    public void goToUserStats(){
        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);
        volleyService.getUserStatistics(provider.getUserName());
        super.onStop();
    }
}
