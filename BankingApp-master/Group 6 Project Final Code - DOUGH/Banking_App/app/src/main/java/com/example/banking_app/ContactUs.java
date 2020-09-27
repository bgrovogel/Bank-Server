package com.example.banking_app;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        //Add this to the list of all activates.
        StartUpScreen.allActivities.add(this);
    }

    public void Send(View v) {
        String message = ((TextView) findViewById(R.id.MessageET)).getText().toString();
        String email = ((TextView) findViewById(R.id.EmailET)).getText().toString();
        String name = ((TextView) findViewById(R.id.NameET)).getText().toString();
        String subject = ((TextView) findViewById(R.id.SubjectET)).getText().toString();

        String usrMessage = "You forgot: ";
        boolean valid = true;

        String[] serverReply={};

        //ensure all fields are filled out
        if (name.length() < 1) {
            valid = false;
            usrMessage += "Your Name, ";
        }

        if (email.length() < 1) {
            valid = false;
            usrMessage += "Your Email, ";
        }

        if (subject.length() < 1) {
            valid = false;
            usrMessage += "Your Subject, ";
        }

        if (message.length() < 1) {
            valid = false;
            usrMessage += "Your Message, ";
        }

        //If not all fields are filled out prepare to send message to user telling them so
        if (valid == false)
            usrMessage = usrMessage.substring(0, usrMessage.length() - 2);

        //If valid then send message to server
        if (valid) {

            try {
                IOInterfaceStatic.sendStringArray(new String[]{"contact",name,email,subject,message});
                serverReply=IOInterfaceStatic.receiveStringArray();
                Toast.makeText(ContactUs.this, serverReply[2], Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.d("IOError", "Error In sending Contact us Info: " + e.toString());
                Toast.makeText(ContactUs.this, "Server connection error! Please try again later!", Toast.LENGTH_LONG).show();
                valid = false;
            }

            //If the server is sending a reply for a different query then something has gone really wrong.
            //Close the app. This never happened in bug testing.
            if(!serverReply[1].equals("contact")){
                Toast.makeText(ContactUs.this, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
                StartUpScreen.killApp();
            }


            //If it was successful then return to start up screen
            if (valid&&serverReply[0].equals("success"))
                startActivity(new Intent(ContactUs.this, StartUpScreen.class));
        } else {
            //If not all fields were filled out then tell user this and do nothing else
            Toast.makeText(ContactUs.this, usrMessage, Toast.LENGTH_LONG).show();
        }
    }
}
