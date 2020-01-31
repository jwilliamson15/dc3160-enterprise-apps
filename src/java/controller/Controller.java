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
import model.Lesson;
import model.LessonSelection;

import model.LessonTimetable;
import model.Users;

/**
 *
 * @author Josh Williamson
 */
public class Controller extends HttpServlet {

    private final static String LOGIN_PAGE = "/login.jsp";
    private final static String LESSON_TIMETABLE_PAGE = "/lessonTimetable.jspx";
    private final static String LOGIN_FAILED_PAGE = "/loginFailed.jsp";
    private final static String VIEW_BOOKINGS_PAGE = "/viewBookings.jspx";
    
   private Users users;
   private LessonTimetable availableLessons;
   
   HttpSession session = null;
   RequestDispatcher dispatcher = null;
   
   @Override
    public void init() {
        users = new Users();
        availableLessons = new LessonTimetable();
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
        dispatcher = this.getServletContext().getRequestDispatcher(LOGIN_PAGE);
        HttpSession session = request.getSession(false);
        
        if (action.equals("/addUser")) {
            String username = request.getParameter("newUsername");
            Integer userId = users.addUser(username, request.getParameter("newPassword"));
            setWebSession(request, userId);
            dispatch(LESSON_TIMETABLE_PAGE, request, response);
        }
        if (action.equals("/login")) {
            String username = request.getParameter("username");
            if (users.isValid(username, request.getParameter("password"))) {
                System.out.println("LOGIN SUCCESS >>>");
                
                int userId = users.getUserId(username);
                setWebSession(request, userId);
                dispatch(LESSON_TIMETABLE_PAGE, request, response);
            } else {
                dispatch(LOGIN_FAILED_PAGE, request, response);
            }
        }
        if (action.equals("/bookLesson")) {
            System.out.println("BOOK LESSON ACTION >>>");
            Lesson lesson = availableLessons.getLesson(request.getParameter("lessonId"));
            
            LessonSelection bookedLessons = getBookingsFromSession();
            bookedLessons.addLesson(lesson);
            session.setAttribute("bookings", bookedLessons);
            
            dispatch(VIEW_BOOKINGS_PAGE, request, response);
        }
        if (action.equals("/saveBooking")) {
            System.out.println("SAVE BOOKING ACTION >>>");
            
            LessonSelection bookedLessons = getBookingsFromSession();
            bookedLessons.updateBooking((String) session.getAttribute("user"));
            
            dispatch(LESSON_TIMETABLE_PAGE, request, response);
        }
        if (action.equals("/cancelBooking")) {
            System.out.println("REMOVING BOOKING >>>");
            LessonSelection bookedLessons = getBookingsFromSession();
            bookedLessons.removeAll((String) session.getAttribute("user"));
            session.setAttribute("bookings", bookedLessons);
            
            dispatch(VIEW_BOOKINGS_PAGE, request, response);
        }
        
        //links used for navbar and rediretion
        if (action.equals("/timetable")){
            dispatch(LESSON_TIMETABLE_PAGE, request, response);
        }
        if (action.equals("/bookings")) {
            dispatch(VIEW_BOOKINGS_PAGE, request, response);
        }
        if (action.equals("/logout")) {
            session.invalidate();
            dispatch(LOGIN_PAGE, request, response);
        }
        if (action.equals("/loginFailed")) {
            session.invalidate();
            dispatch(LOGIN_PAGE, request, response);
        }

        
    }
    
    private void dispatch(String dispatchPath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher = this.getServletContext().getRequestDispatcher(dispatchPath);
        dispatcher.forward(req, resp);
    }
    
    private void setWebSession(HttpServletRequest request, Integer userId) {
        String userIdStr = Integer.toString(userId);
        session = request.getSession();
        session.setAttribute("user", userIdStr);
        
        LessonSelection bookedLessons = new LessonSelection(userIdStr);
        session.setAttribute("bookings", bookedLessons);
    }
    
    private LessonSelection getBookingsFromSession() {
        return (LessonSelection) session.getAttribute("bookings");
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
