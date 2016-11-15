package com.example.seamus.carvis;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoggedInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);


        Intent intent = getIntent();
        String message = intent.getStringExtra(NewLoginActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message+" is logged in");

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_logged_in);
        layout.addView(textView);
    }
}
