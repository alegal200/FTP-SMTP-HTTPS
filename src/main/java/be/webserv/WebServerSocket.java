package be.webserv;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServerSocket {

    //Note this class is there to create a listening socket in ssl, I generated
    // keytool -genkey -keystore alex.jks -keyalg RSA
    // keytool -exportcert -alias server -keystore server_keystore.jks -file alex.cer


    public WebServerSocket() {
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


            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket ss = (SSLServerSocket) sslserversocketfactory.createServerSocket(8080);
            ExecutorService executor = Executors.newFixedThreadPool(3);
            while (true) {
                System.out.println("waiting new connection");
                Socket ts = ss.accept();
                System.out.println(" new connection");
                HTTPServerRunable w = new HTTPServerRunable(ts);
                executor.execute(w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}