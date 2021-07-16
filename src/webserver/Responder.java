/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import static webserver.WebServer.requestReader;

/**
 *
 * @author beama
 */
public class Responder implements Runnable{

    /**
     * @return the requestHandler
     */
    public Socket getRequestHandler() {
        return requestHandler;
    }

    /**
     * @param requestHandler the requestHandler to set
     */
    public void setRequestHandler(Socket requestHandler) {
        this.requestHandler = requestHandler;
    }

    /**
     * @return the turnoff
     */
    public boolean isTurnoff() {
        return turnoff;
    }

    /**
     * @param turnoff the turnoff to set
     */
    public void setTurnoff(boolean turnoff) {
        this.turnoff = turnoff;
    }

    /**
     * @return the txt
     */
    public JTextArea getTxt() {
        return txt;
    }

    /**
     * @param txt the txt to set
     */
    public void setTxt(JTextArea txt) {
        this.txt = txt;
    }
    private Socket requestHandler; //this is a socket
    private boolean turnoff;
    private JTextArea txt;
    private String HTTPMessage;//first line of requeststring,
    private String requestedFile = "";
    private DataOutputStream pageWriter;
    private Scanner pageReader;
    
    
    public Responder(Socket requestHandler,boolean turnoff, JTextArea txt)
    {
        setRequestHandler(requestHandler);
        setTurnoff(turnoff);
        setTxt(txt);
    }
    public void run()
    {
        if(isTurnoff()==false)
        {
            try {
                requestReader = new Scanner(
                        new InputStreamReader(getRequestHandler().getInputStream()));//receives msg
                pageWriter = new DataOutputStream( getRequestHandler().getOutputStream());//Sends out
                int lineCount = 0;
                
                do{
                    lineCount++; //This will be used later
                    HTTPMessage = requestReader.nextLine();
                    txt.append(HTTPMessage+"\n");
                    //writing it to file
                    if (lineCount ==1){
                        //path
                        logErrorFile(HTTPMessage);
                        if(HTTPMessage.indexOf("doSERVICE")>-1)
                        {
                            SQLSelectService sqls = new SQLSelectService(HTTPMessage,pageWriter);
                            sqls.doWork();
                            break;
                        }
                        else
                        {
                      
                        requestedFile = "Webroot\\"
                                + HTTPMessage.substring(5,HTTPMessage.indexOf("HTTP/1.1")-1);
                         if(requestedFile.equals("Webroot\\") || requestedFile.equals("Webroot\\subdir") || requestedFile.equals("Webroot\\subdir/"))
                                 {
                                     requestedFile="Webroot\\default.htm";
                                 }
                        }
                    }
                    ;
                } while(HTTPMessage.length() != 0);
//        
            } catch (IOException ex) {
                try {
                    logErrorFile(ex.toString());
                } catch (IOException ex1) {
                    Logger.getLogger(Responder.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
                       
       //getting file and outputing to webpage
        if(!requestedFile.equals(""))
        {
       try{
           try
           {
                pageReader = new Scanner (new File(requestedFile));//Webroot\gffgssdf
           }
           catch(FileNotFoundException fnfe)
           {
               pageReader = new Scanner (new File("util\\Error404.html"));
               logErrorFile("Error 404. File Not Found");
           }
        while (pageReader.hasNext()){
            String s = pageReader.nextLine();
            System.out.println(s);
            pageWriter.writeBytes(s);
        }
        //tell the browser we're done writing to it.
        pageReader.close();
        pageWriter.close();
        requestHandler.close();
       } catch(Exception e){
           System.out.println(e.toString());
       }
        
        
        }}
      
    }
   
     public static void logErrorFile(String s) throws IOException {    
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        BufferedWriter log = new BufferedWriter(new FileWriter("requestsanderrors.txt", true));
        log.append("\n"+timeStamp + "    " + s + "\n");
        log.close();
    }
     
    }
    

