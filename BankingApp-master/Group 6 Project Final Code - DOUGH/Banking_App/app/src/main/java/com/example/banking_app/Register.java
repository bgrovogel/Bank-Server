package com.example.banking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Add this to the list of all activities
        StartUpScreen.allActivities.add(this);

        mDisplayDate = (TextView) findViewById(R.id.dateofbirth);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Register.this,
                        android.R.style.Theme_Black_NoTitleBar,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
        Toast.makeText(Register.this, "Just fill out information to start your account today!", Toast.LENGTH_SHORT).show();
    }

    public void Submit(View v) {
        String userName = ((EditText) findViewById(R.id.UserNameET)).getText().toString();
        String email = ((EditText) findViewById(R.id.EmailET)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.FirstNameET)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.LastNameET)).getText().toString();
        String enterPassword = ((EditText) findViewById(R.id.EnterPasswordET)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.ConfirmPasswordET)).getText().toString();
        String address = ((EditText) findViewById(R.id.addressET)).getText().toString();
        String SSN = ((EditText) findViewById(R.id.SSNET)).getText().toString();
        String dob = ((TextView) findViewById(R.id.dateofbirth)).getText().toString();


        String invalid = "Invalid: ";
        boolean valid = true;

        String[] serverReply;

        //Check if all fields are not empty
        if (userName.length() < 1) {
            valid = false;
            invalid += "User Name, ";
        }

        if (email.length() < 1) {
            valid = false;
            invalid += "Email, ";
        }

        if (firstName.length() < 1) {
            valid = false;
            invalid += "First Name, ";
        }

        if (lastName.length() < 1) {
            valid = false;
            invalid += "Last Name, ";
        }

        if (enterPassword.length() < 1) {
            valid = false;
            invalid += "1st Password Field, ";
        }

        if (confirmPassword.length() < 1) {
            valid = false;
            invalid += "2nd Password Field, ";
        }

        if (address.length() < 1) {
            valid = false;
            invalid += "Address, ";
        }

        if (SSN.length() !=9) {
            valid = false;
            invalid += "SSN, ";
        }

        if (dob.equals("Select Date Of Birth") || dob.length() < 1) {
            valid = false;
            invalid += "Date of Birth, ";
        }

        if (!enterPassword.equals(confirmPassword)) {
            valid = false;
            invalid += "Passwords must Match!, ";
        }

        //If they are then prepare message to send user
        if (valid == false)
            invalid = invalid.substring(0, invalid.length() - 2);

        if (valid) {
            //If all fields are filled out then send message to the server
            try {

                IOInterfaceStatic.sendStringArray(new String[]{"register",firstName,lastName,email,
                                                                address, dob, SSN, userName,
                                                                enterPassword, confirmPassword});
                //receive return info from server
                serverReply = IOInterfaceStatic.receiveStringArray();


            } catch (Exception e) {
                //If there is a server error then close this activity and relaunch the startup screen to
                //reset the connection
                Log.d("IOError", "Error In sending registration Info: " + e.toString());
                Toast.makeText(Register.this, "Server connection error! Please try again later!", Toast.LENGTH_LONG).show();
                Intent restart = new Intent(Register.this, StartUpScreen.class);
                startActivity(restart);
                finish();
                return;
            }
            //If it succeeded then close this activity and launch a login activity
            if(serverReply[0].equals("success")&&serverReply[1].equals("register")){
                startActivity(new Intent(Register.this, MainActivity.class));
                finish();}
            else{
                //if it fails but the server tried to execute the right command then try again
                if(serverReply[0].equals("failure")&&serverReply[1].equals("register"))
                    Toast.makeText(Register.this, serverReply[2], Toast.LENGTH_LONG).show();
                else {
                    //If it fails becasue the server sent a reply for a different query then close app
                    Toast.makeText(Register.this, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
                    StartUpScreen.killApp();
                }


            }
        } else {
            //If any of the fields were empty then send a message to the user telling them tha
            Toast.makeText(Register.this, invalid, Toast.LENGTH_LONG).show();
        }



    }

}
