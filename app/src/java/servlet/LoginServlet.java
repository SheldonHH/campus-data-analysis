package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dataManager.UserManager;
import is203.JWTUtility;
import java.util.ArrayList;
import java.util.Collections;
import model.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;

/**
 * Validates users' logging in and directs non-admin user to main page, admin
 * user to admin panel, or invalid users to login page and displays error
 * messages
 *
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/log_in.do", "/json/authenticate"})
public class LoginServlet extends HttpServlet {

    private String admin = "admin";
    private String adminPassword = "ranonmmb";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();
            session.setAttribute("secretToken", "g4t7!_1234567890");
            // Retrieve the URL STRING TO CHECK FOR JSON STRING
            String uri = request.getScheme() + "://"
                    + request.getServerName()
                    + request.getRequestURI()
                    + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            ArrayList<String> outputList = new ArrayList<String>();
            boolean isJsonRequest = uri.contains("/json");

            // check for empty username or password fields
            if (password == null) {
                outputList.add("missing password");
            } else if (password.trim().equals("")) {
                outputList.add("blank password");
            } else {
                password = password.trim();
            }

            if (username == null) {
                outputList.add("missing username");
            } else if (username.trim().equals("")) {
                outputList.add("blank username");
            } else {
                username = username.trim();
            }

            // Check for empty values
            if (!outputList.isEmpty()) {
                if (isJsonRequest) {
                    //call upon gson method to generate error message                                         
                    processJsonPrinting(false, outputList, out);
                } else {
                    request.setAttribute("username", username);
                    RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                    request.setAttribute("error", "Please complete all fields.");
                    view.forward(request, response);
                }

            } else {
                // check for valid admin login
                if (admin.equals(username) && adminPassword.equals(password)) {
                    User user = new User(adminPassword, admin);

                    if (!isJsonRequest) {
                        session.setAttribute("user", user);
                        response.sendRedirect("admin");
                        return;
                    } else {
                        String sharedSecret = (String) session.getAttribute("secretToken");
                        session.setAttribute("user", user);
                        String generatedToken = JWTUtility.sign(sharedSecret, username);
                        session.setAttribute("generatedToken", generatedToken);
                        outputList.add(generatedToken);
                        // Only when I know I got a valid user the I will pass in TRUE as a boolean value.
                        processJsonPrinting(true, outputList, out);
                        //Add a return here to stop the method once it is done processing
                        return;
                    }
                }

                User user = UserManager.retrieveValidUser(username); // retrieve user if exists or null if not

                if (user != null && user.getPassword().equals(password)) {

                    if (!isJsonRequest) {
                        session.setAttribute("user", user);
                        response.sendRedirect("admin");
                    } else {
                        outputList.add(ErrorUtility.INVALID_PASSWORD);
                        // pseudo token generated by the team
                        // Only when I know I got a valid user the I will pass in TRUE as a boolean value.
                        processJsonPrinting(false, outputList, out);
                    }
                } else {
                    if (!isJsonRequest) {
                        request.setAttribute("username", username);
                        RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                        request.setAttribute("error", "Invalid username/password");
                        view.forward(request, response);
                    } else {
                        outputList.add("invalid username/password");
                        processJsonPrinting(false, outputList, out);
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    /**
     *
     * @param isSuccessful
     * @param outputList
     * @param out
     */
    public void processJsonPrinting(boolean isSuccessful, ArrayList<String> outputList, PrintWriter out) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);

        JsonObject status = new JsonObject();
        if (!isSuccessful) {
            status.addProperty("status", "error");
            status.add("messages", gson.toJsonTree(outputList));
            out.println(gson.toJson(status));
        } else {
            status.addProperty("status", "success");
            status.addProperty("token", outputList.get(0));
            out.println(gson.toJson(status));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}