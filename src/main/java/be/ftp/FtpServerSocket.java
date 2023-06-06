


package be.ftp;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FtpServerSocket {


        public FtpServerSocket() {
            try {
                try {
                    String fichier = Thread.currentThread().getContextClassLoader().getResource("").getPath() +"param.prop";
                    Properties props = new Properties();
                    props.load(new FileInputStream(fichier));
                    String data= props.getProperty("passApp");
                    System.setProperty("javax.net.ssl.keyStore",props.getProperty("keystore"));
                    System.setProperty("javax.net.ssl.keyStorePassword",props.getProperty("PassKeystore"));


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ServerSocketFactory serversocketfactory = (ServerSocketFactory) ServerSocketFactory.getDefault();
                ServerSocket ss21 = (ServerSocket) serversocketfactory.createServerSocket(21); //21
                ExecutorService executor = Executors.newFixedThreadPool(3);


                int nbrthread = 0 ;
                while (true) {
                    nbrthread++;
                    System.out.println("*waiting new connection");
                    Socket ts21 = ss21.accept();
                    System.out.println("*new connection");
                    FtpRunable  work1 = new FtpRunable(ts21 , nbrthread );
                    nbrthread++;
                    executor.execute(work1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

}
