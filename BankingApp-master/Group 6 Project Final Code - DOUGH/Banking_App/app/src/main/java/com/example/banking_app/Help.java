package com.example.banking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //Add this to the list of all activates.
        StartUpScreen.allActivities.add(this);
    }

    public void Login(View v) {
        startActivity(new Intent(Help.this, MainActivity.class));
    }
    public void ContactUs(View v) {
        startActivity(new Intent(Help.this, ContactUs.class));
    }
    public void Register (View v){
        startActivity(new Intent(Help.this, Register.class));
        }
    }

