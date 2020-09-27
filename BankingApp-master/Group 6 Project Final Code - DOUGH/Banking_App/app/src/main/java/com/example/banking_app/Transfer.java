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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class Transfer extends AppCompatActivity {


    public static final String MY_BALANCE = "My_Balance";
    public static final String CHECKING_KEY = "checking_key";
    public static final String SAVINGS_KEY = "savings_key";

    public String rBalanceC;
    public String rBalanceS;
    public DecimalFormat currency = new DecimalFormat("$###,##0.00"); //decimal formatting
    TextView cBalanceTV, sBalanceTV;
    public double cBalanceD;
    public double sBalanceD;
    public double cNewBalance;
    public double sNewBalance;
    public double TransferEntered;
    int transferChoice;

    private String[] balanceReq, serverReply;

    SharedPreferences.Editor myEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        Bundle extras = getIntent().getExtras();

        //add activity to list of all activities
        StartUpScreen.allActivities.add(this);

        //retrieve account balances from server
        try {
            IOInterfaceStatic.sendStringArray(new String[]{"balanceReq"});
            balanceReq = IOInterfaceStatic.receiveStringArray();
        } catch (Exception e) {
            Log.d("IOError", "Error In Retrieving Transfer Balance: " + e.toString());
            Toast.makeText(Transfer.this, "Error In Retrieving Transfer Balance: ", Toast.LENGTH_LONG).show();
            Intent restart = new Intent(Transfer.this, StartUpScreen.class);
            startActivity(restart);
            finish();
            return;
        }

        //handle unexpected results
        if (!(balanceReq[0].equals("success") && balanceReq[1].equals("balanceReq"))) {
            Toast.makeText(Transfer.this, "Major Error! Exiting...", Toast.LENGTH_LONG).show();
            for (Activity act : StartUpScreen.allActivities)
                act.finish();
        }

        //set balance based on server reply
        rBalanceC=balanceReq[2];
        rBalanceS=balanceReq[3];

        cBalanceTV = findViewById(R.id.cBalanceTextView);
        cBalanceD = Double.parseDouble(String.valueOf(rBalanceC));
        cBalanceTV.setText(currency.format(cBalanceD));

        sBalanceTV = findViewById(R.id.sBalanceTextView);
        sBalanceD = Double.parseDouble(String.valueOf(rBalanceS));
        sBalanceTV.setText(currency.format(sBalanceD));

        final EditText TransferET = findViewById(R.id.TransferEditText);
        final Spinner TransferS = findViewById(R.id.TransferSpinner);
        Button transferB= findViewById(R.id.TransferButton);

        transferB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String[] serverReplyChecking, serverReplySavings;

                if (!TextUtils.isEmpty(TransferET.getText())) {
                    TransferEntered = Double.parseDouble(String.valueOf(TransferET.getText()));
                    transferChoice = TransferS.getSelectedItemPosition();

                    switch (transferChoice) {
                        case 0:
                            //If the user is transferring from checking to saving and has sufficient funds
                            //then use methods from the Transactions class to add and remove money from acounts
                            if (cBalanceD >= TransferEntered) {


                                serverReplyChecking=Transactions.withdraw("checking_key",TransferEntered,Transfer.this, "When Trying to transfer from checking to savings: ");
                                serverReplySavings=Transactions.deposit("savings_key",TransferEntered,Transfer.this, "When Trying to transfer from checking to savings: ");

                                if (serverReplySavings[0].equals("failure") || serverReplyChecking[0].equals("failure")) {
                                    return;
                                }
                                cNewBalance=Double.parseDouble(serverReplyChecking[3]);
                                cBalanceTV.setText(currency.format(cNewBalance));
                                cBalanceD = cNewBalance;

                                 sNewBalance=Double.parseDouble(serverReplySavings[3]);
                                sBalanceTV.setText(currency.format(sNewBalance));
                                sBalanceD = sNewBalance;

                                TransferEntered = 0;
                            }
                            else {
                                noFundsMsg();
                            }
                            return;

                        case 1:
                            if (sBalanceD >= TransferEntered) {



                                serverReplySavings=Transactions.withdraw("savings_key",TransferEntered,Transfer.this, "When Trying to transfer from savings to checking: ");
                                serverReplyChecking=Transactions.deposit("checking_key",TransferEntered,Transfer.this, "When Trying to transfer from savings to checking: ");

                                if (serverReplySavings[0].equals("failure") || serverReplyChecking[0].equals("failure")) {
                                    return;
                                }

                                sNewBalance=Double.parseDouble(serverReplySavings[3]);
                                sBalanceTV.setText(currency.format(sNewBalance));
                                sBalanceD = sNewBalance;


                                cNewBalance=Double.parseDouble(serverReplyChecking[3]);
                                cBalanceTV.setText(currency.format(cNewBalance));
                                cBalanceD = cNewBalance;


                                TransferEntered = 0;
                            }
                            else {
                                noFundsMsg();
                            }
                            return;
                    }
                }

                else {
                    noAmountMsg();
                }
            }//end if
        });
    }

    protected void onPause()
    {
        super.onPause();
        myEditor = getSharedPreferences(MY_BALANCE, MODE_PRIVATE).edit();
        myEditor.putString(CHECKING_KEY, String.valueOf(cBalanceD));
        myEditor.putString(SAVINGS_KEY, String.valueOf(sBalanceD));
        myEditor.apply();
    }
    public  void noFundsMsg() {
        Toast.makeText(Transfer.this, "Insufficient funds! Please enter a valid transfer amount and try again!", Toast.LENGTH_LONG).show();
    }
    public  void noAmountMsg() {
        Toast.makeText(Transfer.this, "Nothing entered! Please enter transfer amount and try again!", Toast.LENGTH_LONG).show();
    }
}
