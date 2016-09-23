package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.HeatmapManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.*;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;
import model.HeatmapResult;

/**
 * Validates date and time entered and sends result or error messages to jsp to
 * display
 *
 */
public class HeatmapServlet extends HttpServlet {

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

        // Retrieve the uri to check if it is JSON
        String uri = request.getScheme() + "://"
                + request.getServerName()
                + request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        boolean isJsonRequest = uri.contains("/json");

        HttpSession session = request.getSession();
        // If a non-logged in user tries to access this servlet, redirect back
        if (session.getAttribute("user") == null) {
            // However, only redirect if this is not a JSON request
            if (!isJsonRequest) {
                response.sendRedirect("index.jsp");
                return;
            }
        }

        PrintWriter out = response.getWriter();
        try {
            ArrayList<String> outputList = new ArrayList<String>();
            String level = request.getParameter("floor");
            String endDate = request.getParameter("date");
            String tokenParameter = request.getParameter("token");
            HeatmapManager heatmapManager = new HeatmapManager();
            String sqlFloor = "";
            String startDate = "";

            // Check if either of the fields are empty
            if (endDate == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (endDate.trim().equals("")) {
                outputList.add(ErrorUtility.BLANK_DATE);
            } else {
                endDate = endDate.trim();
            }

            if (level == null) {
                outputList.add(ErrorUtility.MISSING_FLOOR);
            } else if (level.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_FLOOR);
            } else {
                level = level.trim();
            }

            // If this is a JSON request, check the token
            if (isJsonRequest) {
                if (tokenParameter == null) {
                    outputList.add(ErrorUtility.MISSING_TOKEN);
                } else if (tokenParameter.trim().isEmpty()) {
                    outputList.add(ErrorUtility.BLANK_TOKEN);
                } else {
                    tokenParameter = tokenParameter.trim();
                    try {
                        String secretToken = (String) session.getAttribute("secretToken");
                        if (isJsonRequest) {
                            JWTUtility.verify(tokenParameter, "g4t7!_1234567890");
                        } else {
                            outputList.add(ErrorUtility.INVALID_TOKEN);
                        }
                    } catch (JWTException e) {
                        outputList.add(ErrorUtility.INVALID_TOKEN);
                    }
                }
            }

            // To be used for processing later
            if (outputList.isEmpty()) {
                try {
                    int floor = Integer.parseInt(level);
                    if (floor > 5 || floor < 0) {
                        outputList.add(ErrorUtility.INVALID_FLOOR);
                    } else {
                        if (floor == 0) {
                            sqlFloor += "B1";
                        } else {
                            sqlFloor = ("L" + floor);
                        }
                    }
                } catch (NumberFormatException e) {
                    outputList.add(ErrorUtility.INVALID_FLOOR);
                }
                boolean isTimeValid = true;
                if (isJsonRequest) {
                    String[] validTime = endDate.split("T");
                    if (validTime.length != 2) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                        isTimeValid = false;
                    } else {
                        endDate = endDate.replace('T', ' ');
                    }
                }

                if (isTimeValid) {
                    //Date format to convert user datetime to date object
                    Date date = null;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = sdf.parse(endDate);
                        String yr = endDate.substring(0, endDate.indexOf("-"));
                        int year = Integer.parseInt(yr);

                        if (year > 9999 || year < 1000) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        } else if (!sdf.format(date).equals(endDate)) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        } else {
                            // end time in milliseconds
                            long endTimeMilliseconds = date.getTime();
                            // Start time in milliseconds (minus 15mins)
                            long startTime = endTimeMilliseconds - (15 * 60 * 1000);
                            Date d = new Date(startTime);
                            startDate = sdf.format(d);
                        }
                    } catch (ParseException e) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    } catch (NumberFormatException e) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    }
                }
            }

            if (outputList.isEmpty()) {
                // If there are no errors, retrieve heat map result
                ArrayList<HeatmapResult> heatmapResult = heatmapManager.retrieveHeatmapResult(sqlFloor, startDate, endDate);
                if (isJsonRequest) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "success");
                    JsonArray jsonArray = new JsonArray();
                    for (HeatmapResult hmResult : heatmapResult) {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty("semantic-place", hmResult.getSemanticPlace());
                        jsonObj.addProperty("num-people", hmResult.getNumPeople());
                        jsonObj.addProperty("crowd-density", hmResult.getDensity());
                        jsonArray.add(jsonObj);
                    }
                    jsonOutputObject.add("heatmap", gson.toJsonTree(jsonArray));
                    out.println(gson.toJson(jsonOutputObject));

                } else {
                    request.setAttribute("floor", sqlFloor);
                    request.setAttribute("date", endDate);
                    request.setAttribute("heatmapResult", heatmapResult);
                    RequestDispatcher rd = request.getRequestDispatcher("heatmap.jsp");
                    rd.forward(request, response);
                }

            } else {
                Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);
                if (isJsonRequest) {

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "error");
                    jsonOutputObject.add("messages", gson.toJsonTree(outputList));
                    out.println(gson.toJson(jsonOutputObject));

                } else {
                    request.setAttribute("outputList", outputList);
                    RequestDispatcher rd = request.getRequestDispatcher("heatmap.jsp");
                    rd.forward(request, response);
                }
            }
        } finally {
            out.close();
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
