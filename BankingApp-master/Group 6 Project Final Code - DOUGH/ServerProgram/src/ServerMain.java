import java.net.ServerSocket;
import java.net.Socket;

import Database.CheckingTable;
import Database.CreditTable;
import Database.CustomersTable;
import Database.SavingTable;
import ServerFiles.*;

import javax.xml.crypto.Data;


public class ServerMain {


    public static void main(String[] args) {


        Socket socket;
        ServerSocket server=null;

        //Create Server on port 5000
        //It needs to be created outside of the while loop because it can only be created once
        try{server= new ServerSocket(5000);}
        catch(Exception e){System.err.println("Error creating ServerSocket: "+e);}
        DatabaseManager databaseManager=new DatabaseManager();
        (new CLInterface(databaseManager)).start();

        //This while loop runs until the program is stopped. It waits for a device to connect
        //Then when it does it takes the connection and hands it off to a new thread. This way an
        //arbitrary amount of clients can be served at the same time. Each client will have unique keys
        //for secure encryption.



        while (true) {
            try {


                //Accept connection
                socket = server.accept();

                //Hand connection to new thread
                (new ServerThread(socket, databaseManager)).start();


            } catch (Exception e) {
                System.err.println("Error in main function, Shutting down: " + e);
                System.exit(-1);
            }
        }
    }
}
