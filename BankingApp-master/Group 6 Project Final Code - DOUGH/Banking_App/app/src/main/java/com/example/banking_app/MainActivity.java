package com.example.banking_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

//This is the login activity
public class MainActivity extends AppCompatActivity {

    String UserName;
    String Password;
    int loginCounter = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Add this to the list of all activates.
        StartUpScreen.allActivities.add(this);


        Toast.makeText(MainActivity.this, "Welcome to D.O.U.G.H! Please enter your Username and Password.", Toast.LENGTH_SHORT).show();

        Button myButton = (Button) findViewById(R.id.loginButton);
        final EditText myUserName = (EditText) findViewById(R.id.usernameEditText);
        final EditText myPassword = (EditText) findViewById(R.id.passwordEditText);


        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                UserName = myUserName.getText().toString();
                Password = myPassword.getText().toString();
                String[] stringArr = {"Command", "Args"}, serverReply = {"failure"};
                boolean success = false;

                //Send the login info
                try {

                    IOInterfaceStatic.sendStringArray(new String[]{"login", UserName, Password});


                    //receive return info from server
                    serverReply = IOInterfaceStatic.receiveStringArray();
                //handle if there is an issue sending the information
                } catch (Exception e) {
                    Log.d("IOError", "Error In sending login Info: " + e.toString());
                    Toast.makeText(MainActivity.this, "Server connection error! Please try again later!", Toast.LENGTH_LONG).show();

                    //Close all startup screen activities
                    StartUpScreen.killStartScrens();

                    //Start new Startup activity to restart connection
                    Intent restart = new Intent(MainActivity.this, StartUpScreen.class);
                    startActivity(restart);
                    //Close this activity so the user can't back into it
                    finish();
                    return;
                }

                //check how many times unregistered user tried to log in with wrong credentials

                //check if username and password are correct
                if (serverReply[0].equals("success") && serverReply[1].equals("login")) {
                    success = true;
                    //Launch the next activity
                    Intent myIntent = new Intent(MainActivity.this, MainMenu.class);
                    myIntent.putExtra("stringReference", "Access Granted!");
                    //display menu activity screen
                    startActivity(myIntent);

                    //Kill all startup screen activites to that the user can't back into them
                    StartUpScreen.killStartScrens();
                    finish();
                }

                //If the server sends a reply for a different query close the app.
                if (!((serverReply[1].equals("login") && ((serverReply[0].equals("success") || serverReply[0].equals("failure")))))) {
                    Toast.makeText(MainActivity.this, "Major Error! Exiting...", Toast.LENGTH_LONG).show();

                    StartUpScreen.killApp();
                }

                //check login attempts counter
                else {

                    if (loginCounter != 1 && success == false) {
                        //unregistered user, display unregistered user msg and decrease login counter
                        loginCounter -= 1;

                        Toast.makeText(MainActivity.this, "Access Denied! Please try again.You have " + loginCounter + " attempt(s) remaining", Toast.LENGTH_LONG).show();
                    }//end else if
                    else {
                        //3 login attempts are up, close app
                        if (success == false) {
                            Toast.makeText(MainActivity.this, "Access Denied! Closing app!", Toast.LENGTH_LONG).show();
                            for (Activity act : StartUpScreen.allActivities)
                                act.finish();
                        }
                    }//end else
                }
            }//end onClick
        });//end setOnClickListener
    }
}
