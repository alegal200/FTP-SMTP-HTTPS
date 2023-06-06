package be.mail;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
public class Mailmaker {

    public static void Mailsend(String mail) throws MessagingException {
        System.out.println("CREATION D UN MAIL");
        Properties prop2s = new Properties();
            try {
                String fichier = Thread.currentThread().getContextClassLoader().getResource("").getPath() +"param.prop";
                prop2s.load(new FileInputStream(fichier));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }




            String toEmail = mail ;
            toEmail = toEmail.replaceAll("%40", "@");
            String subject = "How to connect to the FTP server";
            String body = "data : \n IP : "+ prop2s.getProperty("ServerIp") +" \n user: aaa \n password : aaa \n " +
                          "please use ftp on windows shell and use this command : \n - open \t  \n - ls \n - lcd \n - get XXXXX \n - put XXXXX     \n \n \n          " +
                          " THANKS \n ALEGAL \n " ;

            String username =  prop2s.getProperty("mail");
            String password =  prop2s.getProperty("passMail"); // GENERATE BY GMAIL

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }});

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("Email send");

        }
}
