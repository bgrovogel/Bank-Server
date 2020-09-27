package ServerFiles;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import Database.*;

//THis class has the job of handling communication with Lolars code
public class DatabaseManager {

    //Once the user is logged in this stores their user ID for future queries.
    private int ID;

    //Instantiate objects used to communicate with the data base
    private CustomersTable customers = new CustomersTable(); //create a connection to each customers
    private CheckingTable checking = new CheckingTable(); // create a connection to checking table
    private SavingTable savings = new SavingTable(); //create a connection to saving table
    private CreditTable credit = new CreditTable(); //create a connection to credit table
    private QnATable qnATable = new QnATable();

    public DatabaseManager() {
    }


    //Checks username and password against database and returns whether it was valid.
    //If valid it retrieves user ID and stores it in class variable for future use.

    public String[] login(String[] input) {
        String[] out = {};
        boolean except = false;

        String usrNameIn = input[1], pswdIn = input[2];
        try {
            this.ID = customers.getCustomerID(input[1], hashStringAsString(input[2]));
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in Database Manager Login method: " + e.toString() + "\n";
            return new String[]{"failure", "login"};
        }

        return new String[]{"success", "login"};
    }


    //register new user in database and return success or failure
    //It also does just a little input validation
    public String[] register(String[] in) {
        boolean valid = true;
        try {
            for (String s : in) {
                if (s.length() < 1)
                    valid = false;
            }

            if (!in[8].equals(in[9]))
                valid = false;

            if (valid)
                customers.addNewCustomers(in[1], in[2], in[3], in[4], in[5], in[6], in[7], hashStringAsString(in[8]), this.checking, this.savings, this.credit);


        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error Registering User: " + e.toString() + "\n";
            ;
            valid = false;
        }

        if (valid)
            return new String[]{"success", "register", in[7] + " registered Succesfully!"};
        else
            return new String[]{"failure", "register", "Invalid Input!"};
    }

    //Adds a new entry into the database for the String array sent when the user
    //fills out the contact us section of the app
    public String[] contact(String[] in) {
        try {
            qnATable.addQuestion(in[1], in[2], in[3], in[4]);
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in contact method: " + e.toString() + "\n";
            return new String[]{"failure", "contact", "Error Sending Message"};
        }
        return new String[]{"success", "contact", "Thanks for your message!"};
    }

    //Retrieve user balance
    public String[] getBalance() {
        String in;
        double checkingBalance = 0, savingsBalance = 0;
        try {

            in = checking.getAcctValue(this.ID);
            if (in.substring(0, 1).equals("$"))
                in = in.substring(1);
            checkingBalance = Double.parseDouble(in);

            in = savings.getAcctValue(this.ID);
            if (in.substring(0, 1).equals("$"))
                in = in.substring(1);
            savingsBalance = Double.parseDouble(in);

        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error retrieving account balance" + e.toString() + "\n";
            return new String[]{"failure", "balanceReq"};
        }
        return new String[]{"success", "balanceReq", Double.toString(checkingBalance), Double.toString(savingsBalance)};
    }

    //print checking balance
    public void printCheckingBal(int IDIn) throws Exception {
        String in;
        in = checking.getAcctValue(IDIn);
        if (in.substring(0, 1).equals("$"))
            in = in.substring(1);
        System.out.print(Double.parseDouble(in));

    }

    //print saving balance
    public void printSavingBal(int IDIn) throws Exception {
        String in;
        in = savings.getAcctValue(IDIn);
        if (in.substring(0, 1).equals("$"))
            in = in.substring(1);
        System.out.print(Double.parseDouble(in));

    }

    //Deposit into the users checking account
    public String[] checkingDep(String[] in) {
        double amountIn = Double.parseDouble(in[2]);
        double oldBal = 0, newBal = 0;
        String oldBalS = "";

        try {

            oldBalS = checking.getAcctValue(ID);
            if (oldBalS.substring(0, 1).equals("$"))
                oldBalS = oldBalS.substring(1);
            oldBal = Double.parseDouble(oldBalS);
            newBal = oldBal + amountIn;
            checking.deposite(ID, amountIn);
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in Depositing to Checking: " + e.toString() + "\n";
            return new String[]{"failure", "deposit", "checking_key", Double.toString(oldBal)};
        }

        return new String[]{"success", "deposit", "checking_key", Double.toString(newBal)};
    }

    //Deposit into users savings account
    public String[] savingsDep(String[] in) {
        double amountIn = Double.parseDouble(in[2]);
        double oldBal = 0, newBal = 0;
        String oldBalS = "";

        try {

            oldBalS = savings.getAcctValue(ID);
            if (oldBalS.substring(0, 1).equals("$"))
                oldBalS = oldBalS.substring(1);
            oldBal = Double.parseDouble(oldBalS);
            newBal = oldBal + amountIn;
            savings.deposite(ID, amountIn);
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in Depositing to Savingsg: " + e.toString() + "\n";
            return new String[]{"failure", "deposit", "savings_key", Double.toString(oldBal)};
        }

        return new String[]{"success", "deposit", "savings_key", Double.toString(newBal)};
    }

    //Withdraw from users checking account
    //If it is an invalid amount it won't do it and will send that back to the app
    public String[] checkingWithdraw(String[] in) {
        double amountIn = Double.parseDouble(in[2]);
        double oldBal = 0, newBal = 0;
        String oldBalS = "";

        try {

            oldBalS = checking.getAcctValue(ID);
            if (oldBalS.substring(0, 1).equals("$"))
                oldBalS = oldBalS.substring(1);
            oldBal = Double.parseDouble(oldBalS);
            newBal = oldBal - amountIn;
            if (oldBal >= amountIn)
                checking.widthdraw(ID, amountIn);
            else
                return new String[]{"failure", "withdaw", "checking_key", Double.toString(oldBal)};
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in Depositing to Savingsg: " + e.toString() + "\n";
            return new String[]{"failure", "withdraw", "checking_key", Double.toString(oldBal)};
        }

        return new String[]{"success", "withdraw", "checking_key", Double.toString(newBal)};
    }

    //Withdraw from users savings account
    //If it is an invalid amount it won't do it and will send that back to the app
    public String[] savingsWithdraw(String[] in) {
        double amountIn = Double.parseDouble(in[2]);
        double oldBal = 0, newBal = 0;
        String oldBalS = "";

        try {

            oldBalS = savings.getAcctValue(ID);
            if (oldBalS.substring(0, 1).equals("$"))
                oldBalS = oldBalS.substring(1);
            oldBal = Double.parseDouble(oldBalS);
            newBal = oldBal - amountIn;
            if (oldBal >= amountIn)
                savings.widthdraw(ID, amountIn);
            else
                return new String[]{"failure", "withdraw", "savings_key", Double.toString(oldBal)};
        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog += "Error in Depositing to Savingsg: " + e.toString() + "\n";
            return new String[]{"failure", "withdraw", "savings_key", Double.toString(oldBal)};
        }

        return new String[]{"success", "withdraw", "savings_key", Double.toString(newBal)};
    }

    //View all customers
    public void viewAll() {
        try {
            customers.printCustomersTable();
        } catch (Exception e) {
            CLInterface.errorLog += "Error in Command Line Thread viewing all users: " + e.toString() + "\n";
            CLInterface.errorCount++;
        }
    }

    //print specific user
    public void printUser(int id) {

        try {
            customers.printCustomerInfo(id);
        } catch (Exception e) {
            CLInterface.errorLog += "Error in Command Line Thread viewing user " +id+": "+ e.toString() + "\n";
            CLInterface.errorCount++;
        }
    }

    //view all questions sent by user
    public void viewQuestions(){
        try {
            qnATable.printQnATable();
        } catch (Exception e) {
            CLInterface.errorLog += "Error in Command Line Thread viewing Questions: "+ e.toString() + "\n";
            CLInterface.errorCount++;
        }
    }

    //print specific question by id
    public void printQuestion(int id)throws Exception{
        qnATable.printQuestion(id);
    }

    public void answerQuestion(int id, String ans)throws Exception{
        qnATable.answerQuestion(id, ans);
    }


    //hash a string, takes the byte output and returns it as a string value
    private static String hashStringAsString(String input) throws Exception {
        String out = "";
        MessageDigest digest;
        byte[] byteHash;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byteHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < byteHash.length; i++) {
                out += byteHash[i];
                out += ",";
            }
        } catch (Exception e) {
            throw new Exception("Problem hashing String input: " + e);
        }

        return out;
    }

    //this method didn't end up getting used but it takes the output of the last method and
    //converts it into bytes.
    private static byte[] hashStringToByteArr(String in) throws Exception {
        ArrayList<Byte> arrayList = new ArrayList<>();
        byte[] out = {};

        int lastIndex = 0, index;

        try {
            while (true) {
                index = in.indexOf(",", lastIndex);
                if (index == -1)
                    break;
                arrayList.add(Byte.parseByte(in.substring(lastIndex, index)));
                lastIndex = index + 1;
            }


            out = new byte[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++)
                out[i] = arrayList.get(i);
        } catch (Exception e) {
            throw new Exception("Problem converting hash string into byte Array: " + e);
        }
        return out;
    }
}
