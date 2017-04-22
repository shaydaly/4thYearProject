package com.carvis;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.CARVISAPP.R;

public class UserStatistics extends AppCompatActivity {
    Context context;
    VolleyService volleyService;
    CognitoUserPoolsSignInProvider provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("user statistics called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

    }
    @Override
    protected void onResume(){
        super.onResume();
        goToUserStats();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }

    public void goToUserStats(){
        super.onStop();
        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);
        volleyService.getUserStatistics(provider);
    }
}
