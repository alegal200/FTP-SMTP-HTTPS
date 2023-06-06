package be.ftp;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class FtpHandler {

    public String currentUser ;
    private String currentSalt ;
    private String currentHash ;
    private String PasswordFile = Thread.currentThread().getContextClassLoader().getResource("").getPath() +"FTPdata.prop" ;
    private String currDirectory = "/" ;
    private  boolean logged = false ;
    private boolean permission = false ;
    private boolean hasDataSocket = false ;

    private int dataport ;
    private ServerSocket dataSocket ;
    private Socket dataConnection ;
    private PrintWriter dataOutWriter ;


        //System.out.println(DigestUtils.sha256Hex("aaa"+"salt2"));

    /**
     *  Hendler of user cmd ->
     *          230 User logged in, proceed.
     *       +  530 Not logged in.
     *       +  331 User name okay, need password.
     *       +  332 Need account for login.
     *          532 Need account for storing files.
     *
     *
     * @param User -> value of the user
     * @return -> cmd send to client to explain what's happend
     */
    public  String HandlerUSER(String User){
        try {
            currentUser = User ;
            Properties props = new Properties();
            props.load(new FileInputStream(PasswordFile));
            String data= props.getProperty(User);
            if(data == null ) // error -> user are not register in the "database"
                return "332 Need account for login.";
            String[] identifiant =  data.split(",");
            currentSalt = identifiant[0];
            currentHash =  identifiant[1];
            permission =  (identifiant[2].equals("1") );
                return "331 User name okay, need password.";

        } catch (IOException e) {
            return "530 Not logged in."; // file not open
        }

    }

    /**
     *               230 User logged in, proceed.
     *               530 Not logged in.
      * @param pass
     * @return
     */
    public   String HandlerPASS(String pass) {
           String receveHashPass = DigestUtils.sha256Hex(pass+ currentSalt);
            if(receveHashPass.equals(currentHash)) {
                logged = true ;
                return "230 User logged in, proceed";
            }
            return "530 Not logged in.";
    }


    public String HandlerSYST(String endCmd) {
        if(logged)
            return "215 compact FTP ALEGAL server";
        return "530 Not logged in.";
    }

    public String HandlerFEAT(String endCmd) {
        if(logged)
            return "211-Extensions supported:\n211 END";
        return "530 Not logged in.";
    }

    public String HandlerPWD(String endCmd) {
        if(logged)
            return "257 \"" + currDirectory + "\"";
        return "530 Not logged in.";
    }

    public String HandlerTYPE(String type) {
        if(!logged)
            return "530 Not logged in.";
        if(type.equals("I"))
            return "200 Command OK";
        else
            return "504 Command not implemented yet";
    }

    public String HandlerPASV(String endCmd , int dataPort) {
        if(!logged) return "530 Not logged in.";
        String myIp = "127.0.0.1";
        String myIpSplit[] = myIp.split("\\.");
   //     openDataConnectionPassive(dataPort);
        int p1 = dataPort / 256;
        int p2 = dataPort % 256;
        return ("227 Entering Passive Mode (" + myIpSplit[0] + "," + myIpSplit[1] + "," + myIpSplit[2] + "," + myIpSplit[3] + "," + p1 + "," + p2 + ")");
    }

    public String HandlerPORT(String endCmd ) {
        if(!logged) return "530 Not logged in.";


        String rep ="#";
        if(endCmd.length() > 1) {
            String[] port = endCmd.split(",");
            if (port.length == 6) {
                rep += Integer.parseInt(port[4]) * 256 + Integer.parseInt(port[5]);
                rep+="#";
                rep += port[0] + "." + port[1] + "." + port[2] + "." + port[3];
                return  rep ;
            } else {
                return "501 Syntax error in parameters or arguments.";
            }
        }
        else
            return ("501 Syntax error in parameters or arguments.");

    }


    public String HandlerLOGGED() {
        if(!logged) return "530 Not logged in.";
        return null;

    }

    public String HandlerSTOR(String filename) {
        if(!logged) return "530 Not logged in.";
        if(filename == null || filename.equals("STOR")) return "501 No filename given";
        if(!permission) return"550 Access denied";
        return null ;
    }


}
