package be.ftp;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;


public class FtpData implements Runnable {
    private     PrintWriter respMdgToClient ;
    private  Socket socket ;
    private String comande ;
    private String endCmd ;
    private static String currDirectory = "src\\main\\resources";
    private boolean passive ;
    private int dataPort ;

    public FtpData(int dataPort, PrintWriter respMdgToClient, String cmd, String endCmd, Boolean passive) {
        this.respMdgToClient = respMdgToClient ;
        this.comande= cmd;
        this.passive = passive;
        this.dataPort = dataPort;
        this.endCmd = endCmd ;
        try {
            String fichier = Thread.currentThread().getContextClassLoader().getResource("").getPath() +"param.prop";
            Properties props = new Properties();
            props.load(new FileInputStream(fichier));
            currDirectory = props.getProperty("resourceFile");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void run() {
            switch (comande) {
                case "LIST":
                case "NLST":
                    respMdgToClient.println("150 Opening ASCII mode data transfer for " + comande);

                    this.socket = createDataSocket();

                    if (socket == null) {
                        respMdgToClient.println("425 No data connection was established");
                        return;
                    }

                    nlstHandler();

                    break;

                case "RETR":


                    this.socket = createDataSocket();

                    if (socket == null) {
                        respMdgToClient.println("425 No data connection was established");
                        return;
                    }

                    File f = new File(currDirectory + "\\" + endCmd);

                    if (!f.exists()) {
                        respMdgToClient.println("550 File does not exist");
                    }

                    respMdgToClient.println("150 Opening BINARY mode data transfer for " + comande);
                    BufferedOutputStream fout = null;
                    BufferedInputStream fin = null;
                    try {
                        // create streams
                        fout = new BufferedOutputStream(socket.getOutputStream());
                        fin = new BufferedInputStream(new FileInputStream(f));
                    } catch (Exception e) {
                        System.out.println("erreur d envoi du fichier");
                    }

                    byte[] buf = new byte[1024];
                    int l = 0;
                    try {
                        while ((l = fin.read(buf, 0, 1024)) != -1) {
                            fout.write(buf, 0, l);
                        }
                    } catch (IOException e) {
                        System.out.println("Could not read from or write to file streams");
                        e.printStackTrace();
                    }

                    // close streams
                    try {
                        fin.close();
                        fout.close();
                    } catch (IOException e) {
                        System.out.println("Could not close file streams");
                        e.printStackTrace();
                    }

                    respMdgToClient.println("226 File transfer successful. Closing data connection.");


                    break;
                case "STOR":


                    this.socket = createDataSocket();

                    if (socket == null) {
                        respMdgToClient.println("425 No data connection was established");
                        return;
                    }
                    BufferedOutputStream fout2 = null;
                    BufferedInputStream fin2 = null;
                    File f2 = new File(currDirectory + "\\"+endCmd);

                    respMdgToClient.println("150 Opening BINARY mode data transfer for "+comande );

                    try {
                        // create streams
                        fout2 = new BufferedOutputStream(new FileOutputStream(f2));
                        fin2 = new BufferedInputStream(socket.getInputStream());
                    } catch (Exception e) {
                        System.out.println("Could not create file streams");
                    }

                    // write file with buffer
                    byte[] buf2 = new byte[1024];
                    int m = 0;
                    try {
                        while ((m = fin2.read(buf2, 0, 1024)) != -1) {
                            fout2.write(buf2, 0, m);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // close streams
                    try {
                        fin2.close();
                        fout2.close();
                    } catch (IOException e) {
                        System.out.println("Could not close file streams");
                        e.printStackTrace();
                    }


                    respMdgToClient.println("226 File transfer successful. Closing data connection.");

                    break;
            }



    }

    private void nlstHandler() {
        String[] fi = null ;
        if(endCmd.equals("LIST"))
            fi = nlstHelper(null);
        else
            fi = nlstHelper(endCmd);

        if (fi == null)
            respMdgToClient.println("550 no files");
         else{
           // respMdgToClient.println("125 Opening ASCII mode data connection for file list.");

            try {
                BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream(), 1024);

                for (int i = 0; i < fi.length; i++) {
                    output.write( (fi[i]  +"\n" ) .getBytes())  ;
                    output.flush();
                    System.out.println("-"+fi[i]);
                }
                output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



            respMdgToClient.println("226 Transfer complete.");

        }


    }

    private String[] nlstHelper(String args ) {
        String filename = currDirectory;
       /* if (args != null) {
            filename = filename +"/" + args;
        }
        */

        File f = new File(filename);

        if (f.exists() && f.isDirectory()) {
            return f.list();
        } else if (f.exists() && f.isFile()) {
            String[] allFiles = new String[1];
            allFiles[0] = f.getName();
            return allFiles;
        } else {
            return null;
        }
    }

    private Socket createDataSocket() {
        Socket socket1 = null;
        if(passive){
            try (ServerSocket serverSocket = new ServerSocket(this.dataPort)) {
                socket1 = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                socket1 = new Socket("127.0.0.1",this.dataPort );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return  socket1 ;
    }
}
