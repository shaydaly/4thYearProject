package com.carvis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mysampleapp.R;

public class UserStatActivity extends AppCompatActivity {

    UserStat userStat ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stat);

        Bundle b = getIntent().getExtras();
        userStat  = (UserStat)b.get("userStat");

        try {
            System.out.println(userStat.getAverageJourneyTime());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
