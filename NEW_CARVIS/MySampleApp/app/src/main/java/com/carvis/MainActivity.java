package com.carvis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mysampleapp.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//              display the app icon at the top
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setIcon(R.drawable.ic_launcher);

//    public void goToLogin(View view) {
//        Intent intent = new Intent(this, NewLoginActivity.class);
//        startActivity(intent);
//    }

//    public void goToRegisterScreen(View view) {
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
//    }

    public void goToSpeed(View view){
        Intent intent = new Intent(this, TrackSpeedActivity.class);
        startActivity(intent);
    }

    public void goToJourneys(View view){
        Intent intent = new Intent (this, NewListJourney.class);
        startActivity(intent);
    }

//    public void goToHttp(View view){
//        Intent intent = new Intent(this, TestHttp.class);
//        startActivity(intent);
//    }

}


