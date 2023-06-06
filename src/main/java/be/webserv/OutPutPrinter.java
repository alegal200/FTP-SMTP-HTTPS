package be.webserv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class OutPutPrinter {
    private OutputStream o ;
    private String page ;


    public OutPutPrinter(OutputStream out){
        o = out ;
        page = "";
    }

    private void startPage() throws IOException {

        page += "<!DOCTYPE html>\r\n";
        page += "<html> \r\n";
        page += "<body> \r\n";
        head();
        page += "<h1 class=\"center\" >VACANCER PORTORICO </h1> \r\n";

    }

    private void head() {
        page +="<head>" ;
        page +="<meta charset=\"UTF-8\" />";
        page +=" <title>vac page</title>";
        page += "<style> ";
        page += " p {text-align:center;"+
                "        color:#006400 ; font-size:2em ;font-weight: bold ;  }";
        page +=".center {text-align:center;}\n";
        page +="body{background: linear-gradient(90deg, orange 20%, red , purple);   }";

        page += "</style> ";
        page +="</head>" ;
    }

    private void endPage() throws IOException {
        page += "</body>\r\n";
        page += "</html>\r\n";
        page += "\r\n\r\n";

    }
    public void close() throws IOException  {
        o.close();
    }

    public void  firstPage() throws IOException {
        this.startPage();
        page += "<p>Saisisez votre adress mail</p> \r\n";
        page += "<form class=\"center\" method=\"POST\" action=\"https://localhost:8080\"  > \r\n";
        page += "<input  name=\"mail\"> \r\n";
        page += "<input type=\"submit\" value=\"Send\"> \r\n";
        page += "<form> \r\n";
        this.endPage();
        this.send();
    }





    public void send()throws IOException {

        int nbrcar  = page.getBytes().length;

        // System.out.println("/"+nbrcar+"/");

        page = ("Content-Length: "+nbrcar  +"\r\n\r\n" ) +page ;
        page = "HTTP/1.1 200 ok "+"\r\n" + page;
        o.write(page.getBytes());
        o.flush();
        page = "";

    }



}