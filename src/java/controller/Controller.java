package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.LessonTimetable;
import model.Users;

/**
 *
 * @author bastinl
 */
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;
   
   private Connection dbConnection = null;
   private String dbUrl = "jdbc:mysql://localhost/cw";
   
   @Override
    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         // TODO Attach the lesson timetable to an appropriate scope
        
    }
    
   @Override
    public void destroy() {
        
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String action = request.getPathInfo();
        
        if (action.equals("/addUser")) {
            addUser(request, response);
        }
        if (action.equals("/login")) {
            
        }

    }
    
    private void addUser(HttpServletRequest request, HttpServletResponse response) {
       String username = request.getParameter("newUsername");
       String password = request.getParameter("newPassword");
       
       if (dbConnection == null) {
           try {
               Class.forName("com.mysql.jdbc.Driver");
               dbConnection = DriverManager.getConnection(dbUrl, "root", "pass");
               
               String query = "INSERT INTO `cw`.`clients` (`username`, `password`) VALUES ('"+username+"', '"+password+"');";
               Statement stmt = dbConnection.createStatement();
               //ResultSet resultOne = 
               stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
               
               ResultSet genKeys = stmt.getGeneratedKeys();
               if (genKeys.next()) {
                   System.out.println("Added user >>> " + Integer.toString(genKeys.getInt(1)));
               }
               
           } catch (Exception ex) {
               System.out.println("Exception >>> "+ ex);
               System.out.println(">>> " + ex.getStackTrace());
           }
       }
   
   }

   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       try {
           processRequest(request, response);
       } catch (SQLException ex) {
           Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       try {
           processRequest(request, response);
       } catch (SQLException ex) {
           Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
       }
    }


    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
