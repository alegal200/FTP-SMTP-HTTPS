package org.example;

import be.ftp.FtpServerSocket;
import be.mail.Mailmaker;
import be.webserv.WebServerSocket;

import javax.mail.MessagingException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");


        Thread ftp = new Thread() {
            public void run() {
                FtpServerSocket a = new FtpServerSocket() ;
            }
        };
        Thread web = new Thread() {
            public void run() {

                WebServerSocket s = new WebServerSocket();
            }
        };
        web.start();
        ftp.start();
/*
        try {
            Mailmaker.Mailsend("samuel.hiard@hepl.be");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
*/

        System.out.println("you are dead");
    }

}
