package com.example.seamus.carvis;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



public class NewLoginActivity extends Activity {
    public static String EXTRA_MESSAGE="seamus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
    }


    public void LoggedIn(View view) {
        Intent intent = new Intent(this, LoggedInActivity.class);
        EditText editText = (EditText) findViewById(R.id.email);
        String username = editText.getText().toString();
        editText = (EditText)  findViewById(R.id.password);
        String password= editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, username);
        if(username.equals("shay")&&password.equals("shay1")) {
            startActivity(intent);
        }
        else{

        }
    }
}

