package com.example.banking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class StartUpScreen extends AppCompatActivity {

    //These static variables track activities so that they can be killed later.
    public static ArrayList<Activity> startUpActivity=new ArrayList<>();
    //Killing all activities kills the app
    public static ArrayList<Activity> allActivities=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_screen);

        //Android theoretically wants you to do all of your networking off of the main thread.
        //The fear is that it will slow down the app if it is trying to do networking on the main thread.
        //I am overriding it here so that I can later do networking on the main thread. After
        //testing there is only one place where the app encounters this problem when we do
        //networking on the main thread. I believe this may be because of the operations being called.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //add this activity to both lists for convenient killing later
        startUpActivity.add(this);
        allActivities.add(this);

        //This is the one place where doing networking on the main thread can cause problems
        //so here we do it on a new thread
        new Thread() {
            public void run() {
                try {
                    //Change Ip Here
                    //These methods setup the IOInterface and connect to it
                    IOInterfaceStatic.setIpAddr("192.168.0.238");
                    IOInterfaceStatic.closeSockets();
                    IOInterfaceStatic.initialize();
                } catch (Exception e) {
                    Log.d("IOError", "Error In connecting: " + e.toString());
                    Looper.prepare();
                    Toast.makeText(StartUpScreen.this, "Connection to Server failed! Please try again later!", Toast.LENGTH_LONG).show();
                }
            }
        }.start();

    }

    public void Login(View v) {
        startActivity(new Intent(StartUpScreen.this, MainActivity.class));
    }

    public void ContactUs(View v) {
        startActivity(new Intent(StartUpScreen.this, ContactUs.class));
    }

    public void Help(View v) {
        startActivity(new Intent(StartUpScreen.this, Help.class));
    }

    public void Register(View v) {
        startActivity(new Intent(StartUpScreen.this, Register.class));
    }

    //Kill all activities killing app
    public static void killApp(){
        for(Activity act:allActivities)
            act.finish();
    }

    //Kill all start screen activities
    public static void killStartScrens(){
        for(Activity act:startUpActivity)
            act.finish();
    }

}
