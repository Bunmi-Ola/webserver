/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author beama
 */
public class SQLSelectService extends Service {

    /**
     * @return the pageWriter
     */
    public DataOutputStream getPageWriter() {
        return pageWriter;
    }

    /**
     * @param pageWriter the pageWriter to set
     */
    public void setPageWriter(DataOutputStream pageWriter) {
        this.pageWriter = pageWriter;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        String rs = getRequestString();
        String field="";
        field = rs.substring(rs.indexOf("Field=")+6,rs.indexOf("&Submit"));
        String criteria=rs.substring(rs.indexOf("Criteria=")+9,rs.indexOf("&Field="));
         criteria = "'%" + criteria + "%'";
        String sqlcommand = String.format("select * from employee where %s like %s ", field,criteria);
        //System.out.println(sql);
        return sqlcommand;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the requestString
     */
    public String getRequestString() {
        return requestString;
    }

    /**
     * @param requestString the requestString to set
     */
    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }
    Connection conn;
    Statement stat;
    ResultSet rs;
    ResultSetMetaData rsmd;
    private DataOutputStream pageWriter;
    private String requestString;
    private String sql;
    public SQLSelectService(String requestString, DataOutputStream pageWriter)
    {
        setRequestString(requestString);
        setPageWriter(pageWriter);
    }
    void doWork()
    {
         try {
             //connect
             try {
                 DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                 this.conn = DriverManager.getConnection("jdbc:oracle:thin:@bisoracle.siast.sk.ca:1521:ACAD", "cistu001", "Bingol81");
                 
             } catch (SQLException e) {
                 //handle exception here
                 logErrorFile("cant connect to SQL Server"); 
                 
             }
             //output the first part of query page
             pageWriter.writeBytes("<html>\n" +
                     "\n" +
                     "<head>\n" +
                     "<title>Comp 233, Query</title>\n" +
                     "</head>\n" +
                     "\n" +
                     "<body>\n" +
                     "<p align='center'><font face='Arial' size='13' color=\"#0000FF\">Service AAAA Page</font></p>\n" +
                     "\n" +
                     "<p>\n" +
                     "<form action='doSERVICE' method='GET'>\n" +
                     "&nbsp;\n" +
                     "<table border=\"0\" width=\"100%\" id=\"table1\">\n" +
                     "	<tr>\n" +
                     "		<td>\n" +
                     "		<p align=\"right\"><font face=\"Arial\">Search Criteria</font></td>\n" +
                     "		<td>\n" +
                     "<input type='text' name='Criteria' size='15'></td>\n" +
                     "	</tr>\n" +
                     "	<tr>\n" +
                     "		<td>\n" +
                     "		<p align=\"right\"><font face=\"Arial\">First Name</font></td>\n" +
                     "		<td>\n" +
                     "<input type='radio' name='Field' value='FirstName' checked></td>\n" +
                     "	</tr>\n" +
                     "	<tr>\n" +
                     "		<td>\n" +
                     "		<p align=\"right\"><font face=\"Arial\">Last Name</font></td>\n" +
                     "		<td>\n" +
                     "<input type='radio' name='Field' value='LastName'></td>\n" +
                     "	</tr>\n" +
                     "	<tr>\n" +
                     "		<td>\n" +
                     "		<p align=\"right\"><font face=\"Arial\">Job Code</font></td>\n" +
                     "		<td>\n" +
                     "<input type='radio' name='Field' value='JobCode'></td>\n" +
                     "	</tr>\n" +
                     "	<tr>\n" +
                     "		<td>\n" +
                     "		<p align=\"right\"><font face=\"Arial\">Employee ID</font></td>\n" +
                     "		<td>\n" +
                     "<input type='radio' name='Field' value='empid'></td>\n" +
                     "	</tr>\n" +
                     "	<tr>\n" +
                     "		<td colspan=\"2\">\n" +
                     "		<p align=\"center\">\n" +
                     "<input type='Submit' name='Submit' value='Run Service'></td>\n" +
                     "	</tr>\n" +
                     "</table>\n" +
                     "<!---->\n" +
                     "<p><br>\n" +
                     "<br>\n" +
                     "<br>\n" +
                     "<br>\n" +
                     "&nbsp; </p>\n" +
                     "</form>");
             //execute sql command
             stat = conn.createStatement();
             rs = stat.executeQuery(getSql());
             rsmd =rs.getMetaData();
             
             
                    pageWriter.writeBytes("<table style='border:1px solid red;'>");
            rsmd = rs.getMetaData();
            int col = rsmd.getColumnCount();
            pageWriter.writeBytes("<tr>");//output columnn names
            for (int q = 0; q < col; q++) {
                pageWriter.writeBytes("<td style='border:1px solid red;'>");
                pageWriter.writeBytes(rsmd.getColumnName(q+1));
                
                pageWriter.writeBytes("</td style='border:1px solid red;'>");
            }
            pageWriter.writeBytes("</tr>");//================================================================
            //Second part of table rows from table retrieved form result set
            while (rs.next()) {
                pageWriter.writeBytes("<tr>");
                for (int i = 0; i < col; i++) {
                    try {
                        pageWriter.writeBytes("<td style='border:1px solid red;'>");
                        pageWriter.writeBytes(rs.getString(i + 1));
                        pageWriter.writeBytes("</td style='border:1px solid red;'>");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                pageWriter.writeBytes("</tr>");
            }
            pageWriter.writeBytes("</table>");
             
             
             
             //output second page of query page
             pageWriter.writeBytes("</p>\n" +
                     "</body>\n" +
                     "\n" +
                     "</html>");
             
             //close pageWriter
             pageWriter.close();
         } catch (IOException ex) {
           
             try {
                 //handle exception here
                 logErrorFile("Cant write to file" + ex.toString());// asks to surround in try catch
             } catch (IOException ex1) {
                 
                 Logger.getLogger(SQLSelectService.class.getName()).log(Level.SEVERE, null, ex1);
             }
           
        } catch (SQLException ex) {
           //handle exception here
        
        }
    }
         public static void logErrorFile(String s) throws IOException {    
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        BufferedWriter log = new BufferedWriter(new FileWriter("requestsanderrors.txt", true));
        log.append("\n"+timeStamp + "    " + s + "\n");
        log.close();
    }
}
