package com.example.banking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class MainMenu extends AppCompatActivity {


    public static final String Exiting = "checking_key";
    public static final String SAVINGS_KEY = "savings_key";
    String rString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle extras = getIntent().getExtras();
        //Add activity to the list of all activities
        StartUpScreen.allActivities.add(this);

        if (extras != null) {
            rString = extras.getString("stringReference");
            Toast.makeText(MainMenu.this, rString, Toast.LENGTH_LONG).show();
        }




        Button checking_BT = (Button) findViewById(R.id.checkingButton);
        Button savings_BT = (Button) findViewById(R.id.savingsButton);
        Button transfer_BT = (Button) findViewById(R.id.transferButton);


        checking_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent checkingIntent = new Intent(MainMenu.this, Transactions.class);

                checkingIntent.putExtra("key", Exiting); //key used to store checking balance
                checkingIntent.putExtra("title", "Checking Account"); //title for transaction activity

                startActivity(checkingIntent);
            }
        });


        savings_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent savingsIntent = new Intent(MainMenu.this, Transactions.class);

                savingsIntent.putExtra("key", SAVINGS_KEY);
                savingsIntent.putExtra("title", "Savings Account");
                startActivity(savingsIntent);
            }
        });


        transfer_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent transferIntent = new Intent(MainMenu.this, Transfer.class);

                startActivity(transferIntent);
            }
        });
    }


}
