package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.ConnectionManager;
import dataManager.GroupDetectionManager;
import dataManager.LocationLookupManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;
import model.Group;
import model.GroupDetectionResults;
import model.PopularPlacesResult;

/**
 * Validates date and time and k-value entered and sends result or error
 * messages to jsp to display
 *
 */
public class GroupPopularPlacesServlet extends HttpServlet {

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
            String kValue = request.getParameter("k");
            String endDate = request.getParameter("date");
            String tokenParameter = request.getParameter("token");
            Date date = null;
            String startDate = "";
            GroupDetectionManager groupDetectionMgr = new GroupDetectionManager();
            GroupDetectionResults groupDetectionResults = null;
            ArrayList<String> outputList = new ArrayList<String>();
            ArrayList<PopularPlacesResult> popularPlacesResult = new ArrayList<PopularPlacesResult>();
            ArrayList<PopularPlacesResult> newPopularPlacesResult = new ArrayList<PopularPlacesResult>();
            HashMap<Integer, ArrayList<PopularPlacesResult>> newResult = new HashMap<Integer, ArrayList<PopularPlacesResult>>();
            int processKvalue = 0;

            // Check kValue
            if (kValue == null || kValue.trim().equals("")) {
                processKvalue = 3;
            } else {
                kValue = kValue.trim();
            }

            // Check the time 
            if (endDate == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (endDate.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_DATE);
            } else {
                endDate = endDate.trim();
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
                        String secretToken = "g4t7!_1234567890"; //(String) session.getAttribute("secretToken");
                        if (secretToken != null) {
                            JWTUtility.verify(tokenParameter, secretToken);
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

                try {
                    // Parse the above value into an integer for ranking later on
                    if (processKvalue != 3) {
                        processKvalue = Integer.parseInt(kValue);
                    }
                    if (processKvalue < 1 || processKvalue > 10) {
                        outputList.add(ErrorUtility.INVALID_K);
                    }
                } catch (NumberFormatException e) {
                    outputList.add(ErrorUtility.INVALID_K);
                }
            }

            if (outputList.isEmpty()) {
                Timestamp startTime = Timestamp.valueOf(startDate);
                Timestamp endTime = Timestamp.valueOf(endDate);
                // If there are no errors, retrieve popular places
                groupDetectionResults = groupDetectionMgr.retrieveGroupDetectionResults(startTime, endTime);
                ArrayList<Group> groupList = groupDetectionResults.getGroupList();

                for (Group group : groupList) {
                    String finalLocation = "";
                    LinkedHashMap<String, Integer> finalLocations = group.getLocations();
                    Iterator<String> iter = finalLocations.keySet().iterator();

                    while (iter.hasNext()) {
                        finalLocation = iter.next();
                    }
                    try {
                        int locationID = Integer.parseInt(finalLocation);
                        Connection conn = ConnectionManager.getConnection();
                        HashMap<Integer, String> locationHashMap = LocationLookupManager.retrieveAllLocationLookupData(conn);
                        // semantic place for one group
                        String semantic_place = locationHashMap.get(locationID);
                        boolean isFound = false;
                        for (PopularPlacesResult place : popularPlacesResult) {
                            if (place.getSemantic_place().equals(semantic_place)) {
                                isFound = true;
                                int count = place.getCount();
                                place.setCount(++count);
                            }
                        }
                        if (!isFound) {
                            popularPlacesResult.add(new PopularPlacesResult(semantic_place, 1));
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                int max = Integer.MAX_VALUE;
                int rank = 1;
                Collections.sort(popularPlacesResult);
                for (int i = 0; i < popularPlacesResult.size(); i++) {
                    int count = popularPlacesResult.get(i).getCount();
                    // while the rank is not the maximum rank specified and is lesser than the previous
                    // count which was processed
                    if (rank != (processKvalue + 1) && count < max) {
                        PopularPlacesResult result = popularPlacesResult.get(i);
                        result.setRank(rank++);
                        newPopularPlacesResult.add(result);
                        ArrayList<PopularPlacesResult> nResult = new ArrayList<PopularPlacesResult>();
                        nResult.add(result);
                        newResult.put(rank - 1, nResult);
                        max = count;

                        // If the count is equals to the previous count processed
                    } else if (count == max) {
                        PopularPlacesResult result = popularPlacesResult.get(i);
                        result.setRank(rank - 1);
                        ArrayList<PopularPlacesResult> popularPlaces = newResult.get(rank - 1);
                        popularPlaces.add(result);
                        newResult.put(rank - 1, popularPlaces);
                        newPopularPlacesResult.add(result);
                    } else {
                        // If smaller than previous count and if larger than ranking specified, exit
                        break;
                    }
                }
            }

            // If there are no errors
            if (outputList.isEmpty()) {
                if (isJsonRequest) {
                    // Process JSON output 
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "success");
                    JsonArray jsonArray = new JsonArray();
                    for (PopularPlacesResult popularResult : newPopularPlacesResult) {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty("rank", popularResult.getRank());
                        jsonObj.addProperty("semantic-place", popularResult.getSemantic_place());
                        jsonObj.addProperty("count", popularResult.getCount());
                        jsonArray.add(jsonObj);
                    }
                    jsonOutputObject.add("results", gson.toJsonTree(jsonArray));
                    out.println(gson.toJson(jsonOutputObject));
                } else {
                    request.setAttribute("k", kValue);
                    request.setAttribute("date", endDate);
                    request.setAttribute("groupPopularPlacesResult", newResult);
                    RequestDispatcher rd = request.getRequestDispatcher("group_popular_place.jsp");
                    rd.forward(request, response);
                }
                // If there are errors
            } else {
                // Sort the errors alphabetically
                Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);
                if (isJsonRequest) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "error");
                    jsonOutputObject.add("messages", gson.toJsonTree(outputList));
                    out.println(gson.toJson(jsonOutputObject));
                } else {
                    request.setAttribute("outputList", outputList);
                    RequestDispatcher rd = request.getRequestDispatcher("group_popular_place.jsp");
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
