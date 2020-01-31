package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Josh Williamson
 */
public class LessonSelection  {
    
    private HashMap<String, Lesson> chosenLessons;
    private String userId;
    
    private DataSource ds = null;
    
    private ResultSet rs = null;
    private Statement st = null;
    
    public LessonSelection() {
        System.out.println("NO ARGS LESSON SELECTION CONSTRUCTOR >>>");
    }

    public LessonSelection(String userId) {
        
        chosenLessons = new HashMap<String, Lesson>();
        this.userId = userId;

        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/cwdb");

            Connection connection = ds.getConnection();
            if (connection != null) {
                st = connection.createStatement();
                //retrieves all current bookings from database
                String query = "SELECT * FROM `cw`.`lessons_booked` WHERE  `clientid`="+userId+";";
                rs = st.executeQuery(query);
                
                while (rs.next()) {
                    //finds each lesson from it's ID previously found in query above
                    String lessonId = rs.getString("lessonid");
                    System.out.println("LESSON ID READ >>>" + lessonId);
                    query = "SELECT description, startDateTime, endDateTime, level, lessonid "
                            + "FROM `cw`.`lessons` WHERE lessonid =\""+lessonId+"\";";
                    ResultSet lessonRs = connection.createStatement().executeQuery(query);
                    //adds lesson to class variable
                    if (lessonRs.next()) {
                        Lesson newLesson = new Lesson(
                            lessonRs.getString("description"),
                            lessonRs.getTimestamp("startDateTime"),
                            lessonRs.getTimestamp("endDateTime"),
                            lessonRs.getInt("level"),
                            lessonRs.getString("lessonid"));
                        addLesson(newLesson);
                    }
                    lessonRs.close();
                }
                st.close();
            }
        } catch(Exception e){
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
        }
    }
    
    public void updateBooking(String clientId) {
        Object[] lessonKeys = chosenLessons.keySet().toArray();
        
        try {
            deleteUserBookings(clientId);
            
            Connection dbConnection = ds.getConnection();
            //adds each lesson into the database
            for (Object lessonKey : lessonKeys) {
                String lessonId = (String) lessonKey;

                System.out.println(" >>> Lesson ID is : " + lessonId);  
                PreparedStatement insertQuery = dbConnection.prepareStatement(
                        "INSERT INTO cw.lessons_booked (clientid, lessonid) VALUES (?,?)");
                insertQuery.setString(1, clientId);
                insertQuery.setString(2, lessonId);
                insertQuery.executeUpdate();
            }
            System.out.println("ADEED NEW USER BOOKINGS >>> ");
            
        } catch (SQLException ex) {
                System.out.println("Exception is ;"+ex + ": message is " + ex.getMessage());
        }
    }
    
    /**
     * deletes all bookings for a user from the database
     * 
     * @param clientId 
     */
    public void deleteUserBookings(String clientId) {
        try {
            Connection dbConnection = ds.getConnection();
            PreparedStatement deleteQuery = dbConnection.prepareStatement(
                        "DELETE FROM cw.lessons_booked WHERE  `clientid`=?;");
            deleteQuery.setString(1, clientId);
            deleteQuery.executeUpdate();
            System.out.println("BLATTED YOUR USER BOOKINGS >>> " + clientId);
        } catch (SQLException ex) {
                System.out.println("Exception is ;"+ex + ": message is " + ex.getMessage());
        }  
    }

    /**
     * @return the items
     */
    public Set <Entry <String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
       
        Lesson i = new Lesson(l);
        //restricts lessons to 3 or less
        if (chosenLessons.size() < 3) {
            this.chosenLessons.put(l.getId(), i);
        }
    }
    
    public void removeAll(String userId) {
        chosenLessons.clear();
        deleteUserBookings(userId);
    }

    public Lesson getLesson(String id){
        return this.chosenLessons.get(id);
    }
    
    public int getNumChosen(){
        return this.chosenLessons.size();
    }

    public String getusername() {
        return this.userId;
    }

}
