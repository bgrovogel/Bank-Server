package com.example.banking_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Transactions extends AppCompatActivity {

    public static final String MY_BALANCE = "My_Balance";


    public String rKey, rTitle; //data received from menu activity defining whether its used for
                                //checking or saving
    public double BalanceD;
    public double DepositEntered;
    public double NewBalance;
    public double WithdrawEntered;
    TextView BalanceTV, TitleTV;
    public DecimalFormat currency = new DecimalFormat("$###,##0.00");
    SharedPreferences.Editor myEditor;

    private String[] balanceReq, serverReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        final Bundle extras = getIntent().getExtras();
        StartUpScreen.allActivities.add(this);

        if (extras != null) {
            rKey = extras.getString("key");
            rTitle = extras.getString("title");
        }

        //Set title text
        TitleTV = (TextView) findViewById(R.id.titleTextView);
        TitleTV.setText(rTitle);

        //Request account balance from server and handle if the request fails
        try {
            IOInterfaceStatic.sendStringArray(new String[]{"balanceReq"});
            balanceReq = IOInterfaceStatic.receiveStringArray();
        } catch (Exception e) {
            Log.d("IOError", "Error In Retrieving " + rKey + " Balance: " + e.toString());
            Toast.makeText(Transactions.this, "Error In Retrieving " + rKey + " Balance: ", Toast.LENGTH_LONG).show();
            Intent restart = new Intent(Transactions.this, StartUpScreen.class);
            startActivity(restart);
            finish();
            return;
        }

        if (!(balanceReq[0].equals("success") && balanceReq[1].equals("balanceReq"))) {
            Toast.makeText(Transactions.this, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
            for (Activity act : StartUpScreen.allActivities)
                act.finish();
        }

        //Extract balance depending on what account the compact activity is being used for
        BalanceTV = (TextView) findViewById(R.id.BalanceTextView);
        if (rKey.equals("checking_key")) {
            BalanceD = Double.parseDouble(String.valueOf(balanceReq[2]));
            BalanceTV.setText(String.valueOf(currency.format(Double.parseDouble(balanceReq[2]))));
        } else {
            BalanceD = Double.parseDouble(String.valueOf(balanceReq[3]));
            BalanceTV.setText(String.valueOf(currency.format(Double.parseDouble(balanceReq[3]))));
        }


        Button DepositB = (Button) findViewById(R.id.DepositButton);
        final EditText DepositET = (EditText) findViewById(R.id.DepositEditText);


        DepositB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(DepositET.getText())) {
                    DepositEntered = Double.parseDouble(String.valueOf(DepositET.getText()));

                    //Call deposit method (defined bellow) with arguments and get server reply
                    serverReply = deposit(rKey, DepositEntered, Transactions.this, "");

                    //Setup new balance (if it failed it will use the old balance)
                    NewBalance = Double.parseDouble(serverReply[3]);
                    BalanceTV.setText(String.valueOf(currency.format(NewBalance)));
                    BalanceD = NewBalance;
                    DepositEntered = 0;
                } else {
                    Toast.makeText(Transactions.this, "Please enter deposit amount and try again!", Toast.LENGTH_LONG).show();
                }
                DepositET.setText(null);
            }
        });


        Button WithdrawB = (Button) findViewById(R.id.WithdrawButton);
        final EditText WithdrawET = (EditText) findViewById(R.id.WithdrawEditText);
        WithdrawB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(WithdrawET.getText())) {
                    WithdrawEntered = Double.parseDouble(String.valueOf(WithdrawET.getText()));

                    //check if user has sufficient funds. This is also double checked server side
                    if (BalanceD >= WithdrawEntered) {

                        //call withdraw method. If it fails then a toast will be called in the withdraw
                        //function so the onclick listener call just end
                        serverReply = withdraw(rKey, WithdrawEntered, Transactions.this, "");
                        if (serverReply[0].equals("failure") && serverReply[1].equals("withdraw") && serverReply[2].equals("insufficient")) {
                            return;
                        }

                        //Setup new balance (if it failed it will use the old balance)
                        NewBalance = Double.parseDouble(serverReply[3]);
                        BalanceTV.setText(String.valueOf(currency.format(NewBalance)));
                        BalanceD = NewBalance;
                        WithdrawEntered = 0;

                    //Handle if there are insufficient funds
                    } else
                        Toast.makeText(Transactions.this, "Insufficient funds! Please enter a valid withdraw amount and try again!", Toast.LENGTH_LONG).show();
                } else {
                    //handle if the user didn't input a value
                    Toast.makeText(Transactions.this, "Nothing entered! Please enter withdraw amount and try again!", Toast.LENGTH_SHORT).show();
                }
                WithdrawET.setText(null);
            }
        });
    }

    //Method to withdraw funds and query the server
    public static String[] withdraw(String rKey, Double WithdrawEntered, AppCompatActivity trans, String extraNote) {
        String[] serverReply = {};

        //Send request to server
        try {
            IOInterfaceStatic.sendStringArray(new String[]{"withdraw", rKey, Double.toString(WithdrawEntered)});
            serverReply = IOInterfaceStatic.receiveStringArray();
        //handle if an exception is thrown. This most likely means that the app disconnected from the server
        //So to handle this just start the start up screen to restart the connection. The user may still be able
        //to back to other activities however it will simply throw more errors and the app will key sending them
        //to the StartupScreen activity
        } catch (Exception e) {
            Log.d("IOError", extraNote + "Error withdrawing from " + rKey + " " + e.toString());
            Toast.makeText(trans, extraNote + "Error In withdrawing from " + rKey + " ", Toast.LENGTH_LONG).show();
            Intent restart = new Intent(trans, StartUpScreen.class);
            trans.startActivity(restart);
            trans.finish();
            return new String[]{};
        }
        //If the specific error of their being insufficient funds then make a toast telling the user that
        //and return
        if (serverReply[0].equals("failure") && serverReply[1].equals("withdraw") && serverReply[2].equals("insufficient")) {
            Log.d("IOError", "Triggered");
            Toast.makeText(trans, "Insufficient funds! Please enter a valid withdraw amount and try again!", Toast.LENGTH_LONG).show();
            return serverReply;
        } else {
            //Otherwise if there is any other sort of error then something is going very wrong.
            //Close the app
            Log.d("IOError", Arrays.toString(serverReply));
            if (!(serverReply[0].equals("success") && serverReply[1].equals("withdraw") && serverReply[2].equals(rKey))) {
                Toast.makeText(trans, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
                StartUpScreen.killApp();
                return new String[]{};
            }
        }
        return serverReply;
    }

    //basically same as the lsat method but without handling insufficient funds
    public static String[] deposit(String rKey, Double DepositEntered, AppCompatActivity trans, String extraNote) {
        String[] serverReply = {};
        try {
            IOInterfaceStatic.sendStringArray(new String[]{"deposit", rKey, Double.toString(DepositEntered)});
            serverReply = IOInterfaceStatic.receiveStringArray();

        } catch (Exception e) {
            Log.d("IOError", extraNote + "Error Depositing to " + rKey + " " + e.toString());
            Toast.makeText(trans, extraNote + "Error In depositing to " + rKey + " ", Toast.LENGTH_LONG).show();
            Intent restart = new Intent(trans, StartUpScreen.class);
            trans.startActivity(restart);
            trans.finish();
            return new String[]{};
        }


        if (!(serverReply[0].equals("success") && serverReply[1].equals("deposit") && serverReply[2].equals(rKey))) {

            Toast.makeText(trans, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
            StartUpScreen.killApp();
            return new String[]{};

        }
        return serverReply;


    }

    protected void onPause() {

        super.onPause();
        myEditor = getSharedPreferences(MY_BALANCE, MODE_PRIVATE).edit();
        myEditor.putString(rKey, String.valueOf(BalanceD));
        myEditor.apply();
    }
}



