package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
 * @author Josh Williamson
 */
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;
   
   private Connection dbConnection = null;
   private String dbUrl = "jdbc:mysql://localhost/cw";
   
   HttpSession session = null;
   
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
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
        HttpSession session = request.getSession(false);
        
        if (action.equals("/addUser")) {
            String username = request.getParameter("newUsername");
            users.addUser(username, request.getParameter("newPassword"));
            setWebSession(request, username);
            dispatcher = this.getServletContext().getRequestDispatcher("/lessonTimetable.jspx");
        }
        if (action.equals("/login")) {
            String username = request.getParameter("username");
            if (users.isValid(username, request.getParameter("password"))) {
                System.out.println("LOGIN SUCCESS >>>");
                
                setWebSession(request, username);
                dispatcher = this.getServletContext().getRequestDispatcher("/lessonTimetable.jspx");
            } else {
                dispatcher = this.getServletContext().getRequestDispatcher("/loginFailed.jsp");
            }
        }

        dispatcher.forward(request, response);
    }
    
    private void setWebSession(HttpServletRequest request, String username) {
        session = request.getSession();
        session.setAttribute("user", username);
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
