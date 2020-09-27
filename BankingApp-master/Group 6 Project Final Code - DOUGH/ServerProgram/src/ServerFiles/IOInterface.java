package ServerFiles;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;


public class IOInterface {
    //Sockets for writing output and reading input
    private Socket socket = null;
    private DataInputStream socketIn = null;
    private DataOutputStream socketOut = null;

    //Public and private key for asymmetrical encryption and ciphers
    //If this is the client then it will never know the server's public key
    private PublicKey asymmetricalPublicKey = null;
    private PrivateKey asymmetricalPrivateKey = null;
    private Cipher decryptCipherAsymmetrical = null;
    private Cipher encryptCipherAsymmetrical = null;

    //Session symmetrical key and cyphers
    private SecretKeySpec sessionKey = null;
    private Cipher encryptCipherSession = null;
    private Cipher decryptCipherSession = null;

    private PublicKey trustedSigningPublicKey = null;
    private String trustedCertIssuer = "trustedIssuer";


    //Constructor boolean variable to tell whether it is being used by the server or the client
    public IOInterface(Socket socket, boolean server) throws Exception {

        this.socket = socket;
        byte[] pubBytes = {48, -126, 1, -72, 48, -126, 1, 44, 6, 7, 42, -122, 72, -50, 56, 4, 1, 48, -126, 1, 31, 2, -127, -127, 0, -3, 127, 83, -127, 29, 117, 18, 41, 82, -33, 74, -100, 46, -20, -28, -25, -10, 17, -73, 82, 60, -17, 68, 0, -61, 30, 63, -128, -74, 81, 38, 105, 69, 93, 64, 34, 81, -5, 89, 61, -115, 88, -6, -65, -59, -11, -70, 48, -10, -53, -101, 85, 108, -41, -127, 59, -128, 29, 52, 111, -14, 102, 96, -73, 107, -103, 80, -91, -92, -97, -97, -24, 4, 123, 16, 34, -62, 79, -69, -87, -41, -2, -73, -58, 27, -8, 59, 87, -25, -58, -88, -90, 21, 15, 4, -5, -125, -10, -45, -59, 30, -61, 2, 53, 84, 19, 90, 22, -111, 50, -10, 117, -13, -82, 43, 97, -41, 42, -17, -14, 34, 3, 25, -99, -47, 72, 1, -57, 2, 21, 0, -105, 96, 80, -113, 21, 35, 11, -52, -78, -110, -71, -126, -94, -21, -124, 11, -16, 88, 28, -11, 2, -127, -127, 0, -9, -31, -96, -123, -42, -101, 61, -34, -53, -68, -85, 92, 54, -72, 87, -71, 121, -108, -81, -69, -6, 58, -22, -126, -7, 87, 76, 11, 61, 7, -126, 103, 81, 89, 87, -114, -70, -44, 89, 79, -26, 113, 7, 16, -127, -128, -76, 73, 22, 113, 35, -24, 76, 40, 22, 19, -73, -49, 9, 50, -116, -56, -90, -31, 60, 22, 122, -117, 84, 124, -115, 40, -32, -93, -82, 30, 43, -77, -90, 117, -111, 110, -93, 127, 11, -6, 33, 53, 98, -15, -5, 98, 122, 1, 36, 59, -52, -92, -15, -66, -88, 81, -112, -119, -88, -125, -33, -31, 90, -27, -97, 6, -110, -117, 102, 94, -128, 123, 85, 37, 100, 1, 76, 59, -2, -49, 73, 42, 3, -127, -123, 0, 2, -127, -127, 0, -39, 69, -65, 30, 102, 102, 102, -97, -15, 86, -4, 56, 4, 109, -13, 49, -17, 40, 22, 114, -126, -11, -21, -54, 41, -66, -73, 66, -126, 66, -98, -98, -56, -115, -125, -106, 113, -78, 115, -77, -59, -37, -48, -103, 86, 41, 53, 91, 67, -60, 42, 5, 39, 55, -11, 86, 82, 0, -67, 103, -92, 63, 94, 13, 111, 55, 72, 26, -65, -54, 125, 85, -127, -54, 55, 73, -20, -103, -20, -112, 120, -19, -119, -116, 95, -86, 45, 38, -82, 86, -58, -114, -116, 84, 42, -95, -55, 34, -13, 42, -10, 18, 52, 42, 42, 63, 21, -120, -20, 30, -43, 44, 116, -90, -67, -101, -127, -27, 121, -10, 12, 117, -21, -100, -41, 120, 108, -120};
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");

            this.trustedSigningPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubBytes));

            this.socketOut = new DataOutputStream(socket.getOutputStream());
            this.socketIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));


            //If this is the server then generate asymmetrical key for later use and instantiate ciphers.
            //This still has client code in it so that it can be used for testing.
            if (server == true) {


                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair keyPair = kpg.generateKeyPair();

                this.asymmetricalPrivateKey = keyPair.getPrivate();
                this.asymmetricalPublicKey = keyPair.getPublic();

                //this.decryptCipherAsymmetrical = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                this.decryptCipherAsymmetrical = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                this.decryptCipherAsymmetrical.init(Cipher.DECRYPT_MODE, asymmetricalPrivateKey);
            }

            //if it is the client then generate secret key for later use and instantiate ciphers.
            else {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(256);
                this.sessionKey = new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES");


                this.decryptCipherSession = Cipher.getInstance("AES/ECB/PKCS5Padding");
                this.encryptCipherSession = Cipher.getInstance("AES/ECB/PKCS5Padding");

                this.encryptCipherSession.init(Cipher.ENCRYPT_MODE, sessionKey);
                this.decryptCipherSession.init(Cipher.DECRYPT_MODE, sessionKey);
            }
        } catch (Exception e) {
            //Catch exception add some info and pass it on up to be caught again.
            //I've found this makes it very easy to trace where bugs happen and fix them.
            throw new Exception("Error From IOInterface constructor:  " + e);
        }


    }

    //These methods are overloaded so that if you pass encryption as true or don't pass a value for it it encrypts
    //If you pass false then it doesn't

    //Functions to send and receive bytes over the network either encrypted or not
    public void sendBytes(byte[] input, boolean encrypt) throws Exception {
        if (encrypt == false) {
            socketOut.writeInt(input.length);
            socketOut.write(input);
        } else sendBytes(input);
    }

    public byte[] receiveBytes(boolean encrypt) throws Exception {
        int length;
        byte[] byteArr;

        if (encrypt == false) {
            length = socketIn.readInt();
            byteArr = new byte[length];
            socketIn.readFully(byteArr, 0, length);
            return byteArr;
        } else return receiveBytes();
    }


    //Defaults to sending them encrypted
    public void sendBytes(byte[] input) throws Exception {
        //Encryption has to be done in 16bit chunks. If it's less then it pads it, if it's more then you run into problems
        byte[] bytesOut = {};
        byte[] temp = {};
        int inputLength = input.length;
        int divBy16 = inputLength / 16;
        int modBy16 = inputLength % 16;
        int msgCount = divBy16;

        try {

            if (modBy16 > 0)
                msgCount++;

            socketOut.writeInt(msgCount);


            for (int i = 0; i < divBy16; i++) {
                temp = Arrays.copyOfRange(input, i * 16, (i + 1) * 16);
                bytesOut = encryptCipherSession.doFinal(temp);
                sendBytes(bytesOut, false);
            }
            if (modBy16 > 0) {

                temp = Arrays.copyOfRange(input, divBy16 * 16, (divBy16 * 16 + modBy16 + 1));
                bytesOut = encryptCipherSession.doFinal(temp);
                sendBytes(bytesOut, false);
            }

        } catch (Exception e) {
            throw new Exception("Error in sendBytes(byte[] " + e);
        }

    }

    //defaults to receiving encrypted
    public byte[] receiveBytes() throws Exception {

        byte[] byteArr = {};
        byte[] temp = {};
        byte[] bytesIn = {};

        ByteArrayOutputStream byteArrayOutputStream;
        int msgCount;
        try {

            msgCount = socketIn.readInt();
            for (int i = 0; i < msgCount; i++) {
                temp = receiveBytes(false);

                bytesIn = decryptCipherSession.doFinal(temp);

                byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(byteArr);
                byteArrayOutputStream.write(bytesIn);
                byteArr = byteArrayOutputStream.toByteArray();
            }


            return byteArr;
        } catch (Exception e) {
            throw new Exception("Error in receiveBytes() " + e);
        }
    }

    //The AES Key is sent using RSA, these two methods send and receive bytes using RSA encryption
    //Outside of these two methods things are mostly passed encrypted with AES
    public void sendBytesAsymmetrcal(byte[] input) throws Exception {
        try {
            encryptCipherAsymmetrical.update(input);
            byte[] bytesOut = encryptCipherAsymmetrical.doFinal();
            socketOut.writeInt(bytesOut.length);
            socketOut.write(bytesOut);
        } catch (Exception e) {
            throw new Exception("Error in sendBytesAsymmetrical " + e);
        }

    }

    public byte[] receiveBytesAsymmetrical() throws Exception {
        int length;
        byte[] byteArr;

        try {
            length = socketIn.readInt();
            byteArr = new byte[length];
            socketIn.readFully(byteArr, 0, length);
            return decryptCipherAsymmetrical.doFinal(byteArr);
        } catch (Exception e) {
            throw new Exception("Error in receiveBytesAsymmetrical " + e);
        }

    }

    //These next two methods are for sending and receiving strings.
    //They just convert them to bytes send them and reverse to receive.
    //They are passed encrypted.
    public void sendString(String input) throws Exception {
        try {
            socketOut.writeInt(input.length());
            sendBytes(input.getBytes());
        } catch (Exception e) {
            throw new Exception("Error in sendString " + e);
        }
    }

    public String receiveString() throws Exception {
        String out;
        int length;
        try {
            length = socketIn.readInt();
            return new String(receiveBytes()).substring(0, length);
        } catch (Exception e) {
            throw new Exception("Error in receiveString " + e);
        }
    }


    //These two methods are for sending and receiving string arrays.
    //They send them encrypted
    public void sendStringArray(String[] input) throws Exception {
        byte[] cont = {1};
        byte[] stop = {0};
        try {
            for (int i = 0; i < input.length; i++) {
                //Send the continue signal so that the receiving function knows that another string is coming
                sendBytes(cont);
                sendString(input[i]);
            }
            sendBytes(stop);
        } catch (Exception e) {
            throw new Exception("Error in sendStringArray " + e);
        }
    }

    public String[] receiveStringArray() throws Exception {
        ArrayList<String> arrList = new ArrayList<>();
        boolean continueRecieving = true;
        byte[] cont = {1};

        try {

            while (continueRecieving) {
                //because byte arrays are padded when sent you need to check the first byte only
                //check if it should continue receiving strings
                if (receiveBytes()[0] == cont[0])
                    arrList.add(receiveString());
                else
                    continueRecieving = false;
            }

            return Arrays.copyOf(arrList.toArray(), arrList.size(), String[].class);
        } catch (Exception e) {
            throw new Exception("Error in receiveStringArray " + e);
        }

    }

    //These next two methods send and recieve certificate objects
    public void sendCertificate(CertificateAuth certIn) throws Exception {
        try {
            sendStringArray(certIn.serialize());
            socketOut.writeInt(certIn.getSignature().length);
            sendBytes(certIn.getSignature());
        } catch (Exception e) {
            throw new Exception("Error sending Certificate: " + e);
        }

    }

    public CertificateAuth receiveCertificate() throws Exception {
        CertificateAuth certOut = new CertificateAuth();
        String[] arrIn;
        byte[] byteIn;
        int sigLength = 0;
        try {
            arrIn = receiveStringArray();
            sigLength = socketIn.readInt();
            byteIn = receiveBytes();
            certOut.deSerialize(arrIn, Arrays.copyOfRange(byteIn, 0, sigLength));
        } catch (Exception e) {
            throw new Exception("Error receiving Certificate: " + e);
        }
        return certOut;
    }


    //The protocol to exchange keys from the server side
    public void exchangeKeysServer(CertificateAuth certIn) throws Exception {
        byte[] sessionKeyByte = null, asmKeyBytes;

        String result;
        try {
            //send the asymmetrical public key unencrypted

            sendBytes(this.asymmetricalPublicKey.getEncoded(), false);

            //receive the symmetrical key encrypted with the public key
            sessionKeyByte = receiveBytesAsymmetrical();
            //Set the session key up with ciphers
            setSessionKeyCiphersFromBytes(sessionKeyByte);

            sendCertificate(certIn);
            result = receiveString();


        } catch (Exception e) {
            throw new Exception("Error in exchangeKeysServer " + e);
        }
    }

    //the protocol to exchange keys from the client side
    public void exchangeKeysClient() throws Exception {
        byte[] keyIn = null;
        CertificateAuth certIn;
        String ipAddr;
        boolean sigGood = false, infoGood = false;
        try {
            //receive RSA public key as bytes in an unencrypted format
            keyIn = receiveBytes(false);
            //set key as member variable and initialize cypher
            setAsymmetricalPublicKeyCiphersFromBytes(keyIn);
            //send the session key encrypted with the public key
            sendBytesAsymmetrcal(this.sessionKey.getEncoded());

            certIn = receiveCertificate();
            ipAddr = this.socket.getInetAddress().toString().substring(1);

            sigGood = certIn.verifySig(trustedSigningPublicKey);
            infoGood = certIn.verifyInfo(trustedCertIssuer, ipAddr);
            if (sigGood && infoGood)
                sendString("Success");
            else {
                if (!sigGood && infoGood)
                    sendString("Failure - Sig Bad, Info Good");
                if (sigGood && !infoGood)
                    sendString("Failure - Sig Good, Info Bad");
                if (!sigGood && !infoGood)
                    sendString("Failure - Sig Bad, Info Bad");
            }


        } catch (Exception e) {
            throw new Exception("Error in exchangeKeysClient " + e);
        }
    }

    //Close resources
    public void closeSockets() throws Exception {

        try {
            if (this.socket != null)
                this.socket.close();

            if (this.socketIn != null)
                this.socketIn.close();

            if (this.socketOut != null)
                this.socketOut.close();
        } catch (Exception e) {
            throw new Exception("Error Closing sockets: "+e);
        }
    }

    //When the asymetrical public key is recieved as bytes this method takes it in
    //and sets it as the member variable
    private void setAsymmetricalPublicKeyCiphersFromBytes(byte[] keyIn) throws Exception {

        try {
            //convert public key to usable form
            X509EncodedKeySpec XKey = new X509EncodedKeySpec(keyIn);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.asymmetricalPublicKey = kf.generatePublic(XKey);

            //Initialize encryptCypher with public Key
            //this.encryptCipherAsymmetrical = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            this.encryptCipherAsymmetrical = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            this.encryptCipherAsymmetrical.init(Cipher.ENCRYPT_MODE, asymmetricalPublicKey);
        } catch (Exception e) {
            throw new Exception("Error in setAsymmetricalPublicKeyFromBytes" + e);
        }
    }

    //When the session key is received as bytes this method takes them in,
    //converts them and sets up the key member variable
    private void setSessionKeyCiphersFromBytes(byte[] keyIn) throws Exception {

        try {
            //convert session key to usable form
            this.sessionKey = new SecretKeySpec(keyIn, "AES");

            //Initialize Cyphers with public Key
            this.decryptCipherSession = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.encryptCipherSession = Cipher.getInstance("AES/ECB/PKCS5Padding");

            this.encryptCipherSession.init(Cipher.ENCRYPT_MODE, sessionKey);
            this.decryptCipherSession.init(Cipher.DECRYPT_MODE, sessionKey);
        } catch (Exception e) {
            throw new Exception("Error in setSessionKeyFromBytes" + e);
        }
    }


}
