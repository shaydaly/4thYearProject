package com.carvis;

import android.content.Context;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);
        volleyService.getUserStatistics(provider.getUserName());
    }
}
