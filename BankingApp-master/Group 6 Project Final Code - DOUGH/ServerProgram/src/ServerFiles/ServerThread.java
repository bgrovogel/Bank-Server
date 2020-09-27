package ServerFiles;


import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;


public class ServerThread extends Thread {

    private IOInterface ioInterface;
    private Socket socket;
    private PrivateKey trustedSigningPrivateKey;
    private CertificateAuth certificate;

    //change IP here
    private String ipAddr = "192.168.0.238";

    private String userId = "";

    private static int openConnections = 0;
    
    private DatabaseManager databaseManager;


    //The constructor just sets up the IOInterface that will be used later in run
    public ServerThread(Socket socket, DatabaseManager databaseManagerIn) {
        byte[] privBytes = {48, -126, 1, 75, 2, 1, 0, 48, -126, 1, 44, 6, 7, 42, -122, 72, -50, 56, 4, 1, 48, -126, 1, 31, 2, -127, -127, 0, -3, 127, 83, -127, 29, 117, 18, 41, 82, -33, 74, -100, 46, -20, -28, -25, -10, 17, -73, 82, 60, -17, 68, 0, -61, 30, 63, -128, -74, 81, 38, 105, 69, 93, 64, 34, 81, -5, 89, 61, -115, 88, -6, -65, -59, -11, -70, 48, -10, -53, -101, 85, 108, -41, -127, 59, -128, 29, 52, 111, -14, 102, 96, -73, 107, -103, 80, -91, -92, -97, -97, -24, 4, 123, 16, 34, -62, 79, -69, -87, -41, -2, -73, -58, 27, -8, 59, 87, -25, -58, -88, -90, 21, 15, 4, -5, -125, -10, -45, -59, 30, -61, 2, 53, 84, 19, 90, 22, -111, 50, -10, 117, -13, -82, 43, 97, -41, 42, -17, -14, 34, 3, 25, -99, -47, 72, 1, -57, 2, 21, 0, -105, 96, 80, -113, 21, 35, 11, -52, -78, -110, -71, -126, -94, -21, -124, 11, -16, 88, 28, -11, 2, -127, -127, 0, -9, -31, -96, -123, -42, -101, 61, -34, -53, -68, -85, 92, 54, -72, 87, -71, 121, -108, -81, -69, -6, 58, -22, -126, -7, 87, 76, 11, 61, 7, -126, 103, 81, 89, 87, -114, -70, -44, 89, 79, -26, 113, 7, 16, -127, -128, -76, 73, 22, 113, 35, -24, 76, 40, 22, 19, -73, -49, 9, 50, -116, -56, -90, -31, 60, 22, 122, -117, 84, 124, -115, 40, -32, -93, -82, 30, 43, -77, -90, 117, -111, 110, -93, 127, 11, -6, 33, 53, 98, -15, -5, 98, 122, 1, 36, 59, -52, -92, -15, -66, -88, 81, -112, -119, -88, -125, -33, -31, 90, -27, -97, 6, -110, -117, 102, 94, -128, 123, 85, 37, 100, 1, 76, 59, -2, -49, 73, 42, 4, 22, 2, 20, 95, 3, 1, 84, 120, -45, -102, 35, 126, 65, -68, -76, 78, 99, 17, -128, 36, -60, -65, -119};
        openConnections++;
        this.databaseManager=databaseManagerIn;
        try {
            this.socket = socket;


            KeyFactory keyFactory = KeyFactory.getInstance("DSA");

            this.trustedSigningPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privBytes));

            this.certificate = new CertificateAuth("trustedIssuer", ipAddr);
            this.certificate.sign(this.trustedSigningPrivateKey);

            //instantiate ioInterface as a server
            this.ioInterface = new IOInterface(socket, true);


        } catch (Exception e) {
            CLInterface.errorCount++;
            CLInterface.errorLog+="Exception in ServerThred Constructor: " + e.toString()+"\n";
        }


    }

    //The run method that actually runs on a new thread
    @Override
    public void run() {


        String[] commandIn, commandResult;
        String usrName, psd;
        


        try {





            //Setup connection with client and exchange keys
            ioInterface.exchangeKeysServer(this.certificate);

            //The first loop, it only accepts a few commands from the app including:
            //Login, register and contact Us.
            //The philosophy is that minimal validation of sent data is done on server side
            //Most of it is done client side. If something is invalid then the server just doesn't
            //do anything and it lets the client know
            while (true) {

                commandIn = ioInterface.receiveStringArray();

                if(commandIn[0].equals("login")){
                    commandResult = this.databaseManager.login(commandIn);
                    ioInterface.sendStringArray(commandResult);
                    if(commandResult[0].equals("success"))
                        break;
                }

                if(commandIn[0].equals("register")){
                    commandResult = this.databaseManager.register(commandIn);
                    ioInterface.sendStringArray(commandResult);
                }

                if(commandIn[0].equals("contact")){
                    commandResult = this.databaseManager.contact(commandIn);
                    ioInterface.sendStringArray(commandResult);
                }


            }


            //2nd Loop only does withdrawals, deposits and retrieves balance
            boolean validIn;
            while(true){
                validIn=false;
                commandIn=ioInterface.receiveStringArray();
                if(commandIn[0].equals("balanceReq")){
                    commandResult=this.databaseManager.getBalance();
                    ioInterface.sendStringArray(commandResult);
                    validIn=true;
                }

                if(commandIn[0].equals("deposit")){
                    if(commandIn[1].equals("checking_key"))
                        commandResult=this.databaseManager.checkingDep(commandIn);
                    if(commandIn[1].equals("savings_key"))
                        commandResult=this.databaseManager.savingsDep(commandIn);
                    ioInterface.sendStringArray(commandResult);
                    validIn=true;
                }

                if(commandIn[0].equals("withdraw")){
                    if(commandIn[1].equals("checking_key"))
                        commandResult=this.databaseManager.checkingWithdraw(commandIn);
                    if(commandIn[1].equals("savings_key"))
                        commandResult=this.databaseManager.savingsWithdraw(commandIn);
                    ioInterface.sendStringArray(commandResult);
                    validIn=true;
                }

                if(validIn==false)
                    ioInterface.sendStringArray(new String[] {"failure","invalid command"});
            }


        } catch (Exception e) {
            try {

                ioInterface.closeSockets();
            } catch (Exception e1) {
            }
            CLInterface.errorLog+="Disconect: in ServerThread run method: " + e.toString()+"\n";
        }

        openConnections--;
    }

    public static int getOpenConnections() {
        return openConnections;
    }

}
