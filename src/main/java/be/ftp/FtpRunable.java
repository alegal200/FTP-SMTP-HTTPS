package be.ftp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class FtpRunable implements  Runnable {


    private Socket servsocket ;
    private boolean hasQuit =  false ;

    private FtpHandler myHandler ;
    private  PrintWriter respMdgToClient ;
    private boolean passiveOn = true ;
    private int dataPort  = 0 ;
    public FtpRunable(Socket ssocket , int nbthread){
        servsocket = ssocket ;
        dataPort = 1025 + 49 + nbthread;
    }

    @Override
    /**
     *  this Thread will create streams , say Welcome to FTP server , call parse executor
     */
    public void run() {

        try {
            String s ;
            //dataPort = 1025 + 49 ;
            myHandler =  new FtpHandler();
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(servsocket.getInputStream()));
            respMdgToClient = new PrintWriter(servsocket.getOutputStream(),true); // auto flush
            respMdgToClient.println("220 Welcome to the Alegal200 FTP server");

            while (! hasQuit ) {
                s = buffRead.readLine();

                if(  s != null  && !s.isEmpty() )
                    executecmd(s);
            }
        }catch (Exception e){
            System.out.println("*ERROR");
            System.out.println(e.getMessage());
        }
    }

    /**
     *  this parser will launch every action related to a cmd
     * @param cmdLine : it the cmd line receve by the client
     */
    private void executecmd(String cmdLine) {
        System.out.println("--->"+cmdLine);
        String startCmd  , endCmd ="";
        startCmd = cmdLine.split(" ")[0];
        endCmd = cmdLine.replaceAll(startCmd+" ","");
        System.out.println("->"+startCmd+" &&"+endCmd +"->"+cmdLine);

        String response = "555 ERROR";

        switch (startCmd){
            case"USER":
                response =  myHandler.HandlerUSER(endCmd);
            break;
            case"PASS":
                response = myHandler.HandlerPASS(endCmd);
            break;
            case"SYST":
                response = myHandler.HandlerSYST(endCmd);
                break;
            case"FEAT":
                response = myHandler.HandlerFEAT(endCmd);
                break;
            case"PWD":
                response = myHandler.HandlerPWD(endCmd);
                break;
            case"TYPE":
                response = myHandler.HandlerTYPE(endCmd);
                break;
            case"PASV":
                response = myHandler.HandlerPASV(endCmd ,dataPort);
                this.passiveOn = true ;
                break;
            case"PORT":
                response = myHandler.HandlerPORT(endCmd);
                if(response.startsWith("#")) {
                    dataPort = Integer.parseInt(response.split("#")[1]);
                    response = "200 PORT command successful. Set to "+ this.dataPort + ". Consider using PASV.";
                    passiveOn = false ;

                }
                break;
            case"LIST": case"NLST":
                response=  myHandler.HandlerLOGGED() ;
                if(response == null ) {// all is ok
                    Thread dataThread = new Thread(new FtpData(dataPort,respMdgToClient,"NLST",endCmd , passiveOn ));
                    dataThread.start();
                }
                break;
            case"RETR":
                response = myHandler.HandlerLOGGED() ;
                if(response == null ) {// all is ok
                    Thread dataThread = new Thread(new FtpData(dataPort,respMdgToClient,"RETR",endCmd , passiveOn ));
                    dataThread.start();
                }
                break;
            case"STOR":
                response = myHandler.HandlerSTOR(endCmd) ;
                if(response == null ) {// all is ok
                    Thread dataThread = new Thread(new FtpData(dataPort,respMdgToClient,"STOR",endCmd , passiveOn ));
                    dataThread.start();
                }
                break;


            default:
            //    System.out.println("->"+startCmd+" &&"+endCmd +"->"+cmdLine);

        }
        System.out.println("////"+response);
        if(response != null)
            respMdgToClient.println(response);

    }
}
