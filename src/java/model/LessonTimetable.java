package model;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 *
 * @author Josh Williamson
 */
public class LessonTimetable {

  private Connection connection=null;
  
  private ResultSet rs = null;
  private Statement st = null;
  
  private Map lessons = null;
  
  private DataSource ds = null;
    
    public LessonTimetable() {
        System.out.println("NO ARGS LESSON TIMETABLE CONSTRUCTOR >>>");
        // You don't need to make any changes to the try/catch code below
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/cwdb");
        } catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
        
        try {
            connection = ds.getConnection();
             try {
                if (connection != null) {
                    st = connection.createStatement();
                    String query = "SELECT description, startDateTime, endDateTime, level, lessonid FROM cw.lessons;";
                    rs = st.executeQuery(query);
                    
                    lessons = new HashMap<String, Lesson>();
                    while (rs.next()) {
                        lessons.put(rs.getString("lessonid"), 
                                new Lesson(rs.getString("description"), 
                                        rs.getTimestamp("startDateTime"),
                                        rs.getTimestamp("endDateTime"),
                                        rs.getInt("level"),
                                        rs.getString("lessonid")));
                    }
                }
            } catch(SQLException e) {
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            } finally {
                st.close();
                connection.close();
            }
        } catch(Exception e){
             System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
        }
    }
   
    /**
     * @return the items
     */
    public Lesson getLesson(String itemID) {
        return (Lesson)this.lessons.get(itemID);
    }

    public Map getLessons() {
        return this.lessons;
    }
    
}
