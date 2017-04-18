//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.14
//
package com.CARVISAPP;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.carvis.VolleyService;

/**
 * Application class responsible for initializing singletons and other common components.
 */
public class Application extends MultiDexApplication {
    private static final String LOG_TAG = Application.class.getSimpleName();
    Context context;
    VolleyService volleyService;
    CognitoUserPoolsSignInProvider provider;

//    public static boolean justSignedin;
    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate();
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");

        context = getApplicationContext();
        volleyService = new VolleyService(context);
        provider = new CognitoUserPoolsSignInProvider(context);

//        justSignedin = true;
    }


    private void initializeApplication() {
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        System.out.println("on intialization");
        // ...Put any application-specific initialization logic here...
    }
}
