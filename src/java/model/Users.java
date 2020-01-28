/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author bastinl
 */
public class Users {
  
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    DataSource ds = null;
   
    public Users() {
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/cwdb");
        }
            catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
    }

    public boolean isValid(String username, String password) {
        try {
            Connection connection = ds.getConnection();
            
            if (connection != null) {
                String query = "SELECT `username`, `password` FROM cw.clients WHERE username = '"+username+"';";
                Statement stmt = connection.createStatement();
                ResultSet resultOne = stmt.executeQuery(query);

                if (resultOne.next()) {
                    if (password.equals(resultOne.getString("password"))) {
                        System.out.println("DB CREDS MATCHED >>>");
                        return true;
                    }
                } 
            }
        } catch(SQLException e) {    
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            return false;
        }
        
        return false;
    }
    
    public void addUser(String username, String password) {
        try {
            Connection connection = ds.getConnection();

            if (connection != null) {
                pstmt = connection.prepareStatement("INSERT INTO clients (username, password) VALUES (?,?)");
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int newUserId = pstmt.executeUpdate();

                if (newUserId > 0) {
                    System.out.println("Added user >>> " + Integer.toString(newUserId));
                } else {
                    System.out.println("User add failed >>> ");
                }
            }
        } catch(SQLException e) {
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());   
        }
    }
}
