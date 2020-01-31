package model;

import java.sql.PreparedStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Josh Williamson
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
                rs = stmt.executeQuery(query);

                if (rs.next()) {
                    if (password.equals(rs.getString("password"))) {
                        System.out.println("DB CREDS MATCHED >>>");
                        return true;
                    }
                } 
                rs.close();
            }
        } catch(SQLException e) {    
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            return false;
        }
        
        return false;
    }
    
    public Integer addUser(String username, String password) {
        try {
            Connection connection = ds.getConnection();

            if (connection != null) {
                pstmt = connection.prepareStatement("INSERT INTO clients (username, password) VALUES (?,?)");
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                
                 int newUserId = getUserId(username);

                if (newUserId > 0) {
                    System.out.println("Added user >>> " + Integer.toString(newUserId));
                    return newUserId;
                } else {
                    System.out.println("User add failed >>> ");
                }
            }
        } catch(SQLException e) {
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());   
        }
        return null;
    }

    public int getUserId(String username) throws SQLException {
        Connection connection = ds.getConnection();
        String query = "SELECT `clientid` FROM cw.clients WHERE username = '"+username+"';";
        ResultSet userResults = connection.createStatement().executeQuery(query);
        if (userResults.next()) {
            return userResults.getInt("clientid");   
        }
        throw new SQLException("Client ID not found");
    }
}
