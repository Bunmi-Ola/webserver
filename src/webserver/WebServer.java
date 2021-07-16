/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

/**
 *
 * @author barclay7342
 */
import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
//we're not going to use, but you will
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class WebServer extends JFrame {

    /**
     * @return the turnoff
     */
    public static boolean isTurnoff() {
        return turnoff;
    }

    /**
     * @param aTurnoff the turnoff to set
     */
    public static void setTurnoff(boolean aTurnoff) {
        turnoff = aTurnoff;
    }

   static ServerSocket requestListener;
   static Socket requestHandler;
   static Scanner requestReader;
   static String HTTPMessage;
   static Scanner pageReader;
   static DataOutputStream pageWriter;
   static String requestedFile;
   static int HTTP_PORT = 12346;
   private static boolean turnoff = false;
   static JTextArea text;
   static ExecutorService responses;
    public WebServer() {
     
        super("WebServer");
        setLayout(new BorderLayout());
        text = new JTextArea();
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(text);
        scroll.setSize(100, 100);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);
        JButton accept = new JButton("Accept");
        JButton stop = new JButton("Stop");
        Panel p = new Panel();
        
        p.add(stop); //panel to stop the connection
        
        p.add(accept); //panel to accept connection
        add(p, BorderLayout.SOUTH);
        setSize(500, 500);
        setVisible(true);
        stop.addActionListener(new ActionListener() { //button to stop listening
            public void actionPerformed(ActionEvent ae) {
               // handle stop server here
               text.append("Stopped +\n");
               setTurnoff(true);
            }
        }
        );
         accept.addActionListener(new ActionListener() { //Button to listen
            public void actionPerformed(ActionEvent ae) {
              text.append("Started +\n");
              setTurnoff(false);
            }
        }
        );
        //Exit on close. X button on browser
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void start()
    {
        while(true) //infinite loop
            {
            try {
                requestHandler = requestListener.accept();//Allows listening from client
                if(requestHandler!=null)
                {
                    //delcaration of Responder which is a thread to be run in responses threadpool
                    Responder res = new Responder(requestHandler, isTurnoff(),text);
                    responses.execute(res);
                }
            } catch (IOException ex) {
                //handle exception here
            }
            }
    }
    
    public static void main(String[] args) {
    //start() joptiopne to specify httpporst declare new threadpool to run runnables(Responders)
        //Declare Threadpool
         responses = Executors.newFixedThreadPool(100);
        //declare new object WebServer
        WebServer server = new WebServer();
        //Specify httpport
        try {
            HTTP_PORT
                    = Integer.parseInt(JOptionPane.showInputDialog("Enter Server HTTP port"));
        } catch (Exception e) {
             try {
                 logErrorFile(e.toString());
                 //handle exception here. write it  to logfile
             } catch (IOException ex) {
                 Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
             }
        } 
       try {
           
           requestListener = new ServerSocket(HTTP_PORT);
       } catch (IOException ex) {
         
       }
        //start the program 
        start();   
        }
         public static void logErrorFile(String s) throws IOException {    
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        BufferedWriter log = new BufferedWriter(new FileWriter("requestsanderrors.txt", true));
        log.append("\n"+timeStamp + "    " + s + "\n");
        log.close();
    }
}
