package ServerFiles;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.time.LocalDate;

//By: Levi Pfantz

//This class is the certificates that the server sends to the client to verify it's identity
//It is identical to the one included with the app

public class CertificateAuth {

    //It verifies the issuer, the ip address and that it is for a valid time frame
    //It also verifies signature based off of those things
    private String issuer;
    private String ip;
    private LocalDate created; //
    private LocalDate expires;
    private byte[] signature;

    public CertificateAuth(){}

    public CertificateAuth(String issuer, String ip) {
        this.issuer = issuer;
        this.ip = ip;
        this.created = LocalDate.now();
        this.expires = LocalDate.now().plusMonths(3);
    }

    //This function
    public boolean verifyInfo(String issuerIn, String ipIn){
        //If it is the same date or after when the certificate expires return false
        if(LocalDate.now().compareTo(this.expires)>=0)
            return false;
        //If it is before the certificate is created return false;
        if(LocalDate.now().compareTo(this.created)<0)
            return false;
        if(!this.issuer.equals(issuerIn) || !this.ip.equals(ipIn))
            return false;

        //else return true
        return true;
    }


    //Sign the certificate using issuer, ip, and time
    public void sign(PrivateKey keyIn) throws Exception{
        try{
            Signature signatureFromKeyIn;
            signatureFromKeyIn = Signature.getInstance("SHA256WithDSA");
            signatureFromKeyIn.initSign(keyIn, new SecureRandom());
            signatureFromKeyIn.update(issuer.getBytes("UTF-8"));
            signatureFromKeyIn.update(ip.getBytes("UTF-8"));
            signatureFromKeyIn.update(created.toString().getBytes("UTF-8"));
            signatureFromKeyIn.update(expires.toString().getBytes("UTF-8"));
            this.signature=signatureFromKeyIn.sign();

        }
        catch(Exception e){
            throw new Exception("Error signing Certificate: "+e);
        }
    }

    //verify signature of the certificate
    public boolean verifySig(PublicKey keyIn)throws Exception{

        try{
            Signature signatureFromKeyIn;
            signatureFromKeyIn = Signature.getInstance("SHA256WithDSA");
            signatureFromKeyIn.initVerify(keyIn);
            signatureFromKeyIn.update(this.issuer.getBytes("UTF-8"));
            signatureFromKeyIn.update(this.ip.getBytes("UTF-8"));
            signatureFromKeyIn.update(this.created.toString().getBytes("UTF-8"));
            signatureFromKeyIn.update(this.expires.toString().getBytes("UTF-8"));

            if(signatureFromKeyIn.verify(this.signature))
                return true;
        }
        catch(Exception e) {
            throw new Exception("Error Verifying Signature: "+e);
        }
        return false;
    }

    public String toString(){
        return this.issuer+"+"+this.ip+"+"+this.created+"+"+this.expires;
    }

    //Convert it to a string array (without signature)
    public String[] serialize(){
        String[] out={this.issuer, this.ip, this.created.toString(), this.expires.toString()};
        return out;
    }

    //Turn it from a string array produced form the previous method and set the objects
    //attributes based on that.
    public void deSerialize(String[] in, byte[] sig) throws Exception{
        CertificateAuth out=new CertificateAuth();

        try{
            setIssuer(in[0]);
            setIp(in[1]);
            setCreated(LocalDate.parse(in[2].substring(0,10)));
            setExpires(LocalDate.parse(in[3].substring(0,10)));

            setSignature(sig);}

        catch(Exception e){throw new Exception("Error in De Serializing Certificate: "+e);}


    }


    //Setter and Getters
    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getExpires() {
        return expires;
    }

    public void setExpires(LocalDate expires) {
        this.expires = expires;
    }


}