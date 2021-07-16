/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adelegan2782
 */
public class test {
    public static void main(String[] args)
    {
        BufferedWriter log = null
                ;
        try {
            String requestString = "GET /doSERVICE?Criteria=Jo&Field=FirstName&Submit=Run+Service HTTP/1.1"
                    ;
//      String criteria = requestString.substring(requestString.indexOf("Criteria="), requestString.indexOf("&Field"));
//      String field = requestString.substring(requestString.indexOf("Field="), requestString.indexOf("&Submit"));
//        System.out.println(criteria);
//        String sql = String.format("select * from employee where %s='%s'",field,criteria);
//        System.out.println(sql);
            log = new BufferedWriter(new FileWriter("aaaaaaaaaaaaaaaa.txt", true));
            log.append("heeeeey bim");
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                log.close();
            } catch (IOException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
    

}
