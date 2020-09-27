package ServerFiles;

import java.util.Scanner;

//By: Levi Pfantz
//This class handles the user interface for the Server program

public class CLInterface extends Thread {


    //It contains static error logs and count that can be added to from anywhere in the program
    public static String errorLog="";

    public static int errorCount=0;

    private DatabaseManager databaseManager;

public CLInterface(DatabaseManager databaseManagerIn){
    this.databaseManager=databaseManagerIn;
}

public void run(){
    Scanner scan=new Scanner(System.in);
    String in="";
    boolean validInput=false;

    prompt();

    //Loop allows the user to select a number of options
    while(true){
        validInput=false;
        in=scan.nextLine();

        if(in.equals("1")){
            validInput=true;
            prompt();
        }


        if(in.equals("2")){
            validInput=true;
            System.out.println("ID | First Name | Last Name | Account # | User Name | Email | Address | Password Hash |");
            databaseManager.viewAll();
            System.out.println("Press Enter to continue");
            scan.nextLine();
            prompt();
        }

        if(in.equals("3")){
            validInput=true;
            System.out.print("Input User ID: ");
            in=scan.nextLine();
            int id=-1;
            try{
                id=Integer.parseInt(in);
                System.out.print("Checking Value = $");
                databaseManager.printCheckingBal(id);
                System.out.print(" | Savings Value = $");
                databaseManager.printSavingBal(id);
                System.out.print(" | ");
                databaseManager.printUser(id);
            }catch (Exception e){
                System.out.println("Invalid sellection!");
            }
            System.out.println("Press Enter to continue");
            scan.nextLine();
            prompt();
        }

        if(in.equals("4")){
            validInput=true;
            System.out.println("Question ID | Sender Name | Sender Email | Subject | Message | Answer |");
            databaseManager.viewQuestions();
            System.out.println("Press Enter to continue");
            scan.nextLine();
            prompt();
        }

        if(in.equals("5")){
            validInput=true;

            int id;
            System.out.print("Enter Question ID You would like to Answer: ");
            try{
                id=Integer.parseInt(scan.nextLine());
                System.out.print("Question: ");
                databaseManager.printQuestion(id);
                System.out.print("Your answer: ");
                databaseManager.answerQuestion(id, scan.nextLine());
            }
            catch(Exception e){System.out.println("Invalid sellection!");}
            System.out.println("Press Enter to continue");
            scan.nextLine();

            prompt();
        }

        if(in.equals("6")){
            validInput=true;
            System.out.println(CLInterface.errorLog);

            System.out.println("Press Enter to continue");
            scan.nextLine();
            prompt();
        }

        if(in.equals("7"))
            System.exit(0);

        if(validInput==false){
            System.out.println("Hey! That isn't valid input! Try again: ");
            prompt();
        }

    }


}

//This is a method to print out a prompt and information
private void prompt(){
    System.out.println("Welcome to the database management command line Interface");
    System.out.println("There are currently: "+ServerThread.getOpenConnections()+ " Devices connected");
    System.out.println("Number of Non-Disconnect Errors: "+CLInterface.errorCount);
    System.out.println("Here are your options menu, type in just the number for your selection");
    System.out.println("Refresh Information: 1,  View all users: 2, View Specific User: 3, View Questions: 4,Answer Question: 5, View Event Log: 6 Shut down Server: 7");
    System.out.println("Input: ");
}





}
