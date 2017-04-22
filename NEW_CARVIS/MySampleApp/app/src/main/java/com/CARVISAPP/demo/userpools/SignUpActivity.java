//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.14
//
package com.CARVISAPP.demo.userpools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.CARVISAPP.R;
import com.CARVISAPP.util.ViewHelper;

/**
 * Activity to prompt for account sign up information.
 */
public class SignUpActivity extends Activity {
    /** Log tag. */
    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();
    Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = getApplicationContext();


    }

    /**
     * Retrieve input and return to caller.
     * @param view the Android View
     */
    public void signUp(final View view) {
        final String username = ViewHelper.getStringValue(this, R.id.signup_username);
        final String password = ViewHelper.getStringValue(this, R.id.signup_password);
        final String givenName = ViewHelper.getStringValue(this, R.id.signup_given_name);
        final String email = ViewHelper.getStringValue(this, R.id.signup_email);
        final String phone = "+353"+ViewHelper.getStringValue(this, R.id.signup_phone);



        Log.d(LOG_TAG, "username = " + username);
        Log.d(LOG_TAG, "given_name = " + givenName);
        Log.d(LOG_TAG, "email = " + email);
        Log.d(LOG_TAG, "phone = " + phone);

        final Intent intent = new Intent();
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.USERNAME, username);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PASSWORD, password);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.GIVEN_NAME, givenName);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.EMAIL_ADDRESS, email);
        intent.putExtra(CognitoUserPoolsSignInProvider.AttributeKeys.PHONE_NUMBER, phone);

        EditText emailText = (EditText)findViewById(R.id.signup_email);
        EditText phoneNumberText = (EditText)findViewById(R.id.signup_phone);
        EditText passwordText  = (EditText)findViewById(R.id.signup_password);
        EditText usernameText  = (EditText)findViewById(R.id.signup_username);


        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||TextUtils.isEmpty(givenName)  || TextUtils.isEmpty(phone)){
            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        }

       else  if(password.length()<8){
            passwordText.setError("Password must be at least 8 characters");

        }

        else if (phone.length()!=14){
            Log.wtf("phone", String.valueOf(phone.length()));
            phoneNumberText.setError("Please enter a valid mobile number");
        }

        else if(!containsDigit(password)){
            passwordText.setError("Password must contain a digit");
        }

        else if(password.equals(password.toLowerCase())){
            passwordText.setError("Password must contain upper/lower case letters");
        }

        else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailText.setError("enter a valid email address");
        }




        else{
            setResult(RESULT_OK, intent);
            //Customer c = new Customer(givenName,username,email,phone);
            // c.AddCustomerToDB(context);

////        Intent i = new Intent();
////        //i.putExtra("CustomerBundle", c);
//        intent.putExtra("CustomerBundle", (Parcelable) c);

            Toast.makeText(context, "An email has been sent to your account", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public  boolean containsDigit(String s) {
        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
        }
        return false;
    }
}
