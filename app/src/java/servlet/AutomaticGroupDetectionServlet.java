package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.GroupDetectionManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;
import model.Group;
import model.GroupDetectionResults;
import model.GroupLocationIDComparator;
import model.GroupMember;
import model.GroupMemberEmailMacaddressComparator;
import model.GroupSizeTimeSpentComparator;

/**
 * Validates date and time entered and sends result or error messages to jsp to display
 * 
 */
public class AutomaticGroupDetectionServlet extends HttpServlet {

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
            // However, only redirect if this is not a json request
            if (!isJsonRequest) {
                response.sendRedirect("index.jsp");
                return;
            }
        }

        PrintWriter out = response.getWriter();
        RequestDispatcher rd = request.getRequestDispatcher("automatic_group_detection.jsp");
        try {
            GroupDetectionManager groupDetectionManager = new GroupDetectionManager();
            ArrayList<String> outputList = new ArrayList<String>();
            GroupDetectionResults groupDetectionResults = null;
            String timestamp = request.getParameter("date");
            String tokenParameter = request.getParameter("token");
            Timestamp ts_startDate = null;
            Timestamp ts_endDate = null;
            
            //check for date and time input error
            if (timestamp == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (timestamp.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_DATE);
            } else {
                timestamp = timestamp.trim();
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
                        String secretToken = "g4t7!_1234567890"; 
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
           // if there is no error
            if (outputList.isEmpty()) {
                boolean isTimeValid = true;
                if (isJsonRequest) {
                    String[] validTime = timestamp.split("T");
                    if (validTime.length != 2) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                        isTimeValid = false;
                    } else {
                        timestamp = timestamp.replace('T', ' ');
                    }
                }
                if (isTimeValid) {
                    try {
                        //Date format to convert user datetime to date object
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(timestamp);
                        String yr = timestamp.substring(0, timestamp.indexOf("-"));
                        int year = Integer.parseInt(yr);

                        if (year > 9999 || year < 1000) {
                            if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                                outputList.add(ErrorUtility.INVALID_DATE);
                            }
                        } else if (!sdf.format(date).equals(timestamp)) {
                            if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                                outputList.add(ErrorUtility.INVALID_DATE);
                            }
                        } else {
                            ts_endDate = Timestamp.valueOf(timestamp);
                            ts_startDate = new Timestamp(ts_endDate.getTime() - (15 * 60 * 1000 - 1000));
                        }
                    } catch (ParseException e) {
                        if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        }
                    } catch (NumberFormatException e) {
                        if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        }
                    }
                }
            }

            if (outputList.isEmpty()) {
                // set starting and ending timestamps
                Timestamp endDate = Timestamp.valueOf(timestamp);
                Timestamp startDate = new Timestamp(endDate.getTime() - (15 * 60 * 1000));
                groupDetectionResults = groupDetectionManager.retrieveGroupDetectionResults(startDate, endDate); // placeholder retrieval method
                if (isJsonRequest) {
                    // Process JSON output 
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "success");
                    jsonOutputObject.addProperty("total-users", groupDetectionResults.getTotalUsers());
                    jsonOutputObject.addProperty("total-groups", groupDetectionResults.getGroupList().size());

                    JsonArray arr = new JsonArray();
                    ArrayList<Group> groupList = groupDetectionResults.getGroupList();
                    //sort the result in order according to group size and time spent according for Json output
                    Collections.sort(groupList, new GroupSizeTimeSpentComparator());
                    if (groupList.isEmpty()) {
                        jsonOutputObject.add("groups", arr);
                    }
                    HashMap<String,String> userList = groupDetectionResults.getUserList();
                    for (Group group : groupList) {
                        JsonObject firstLayer = new JsonObject();
                        ArrayList<String> memberList = group.getGroupMembers();
                        ArrayList<GroupMember> groupMembers = new ArrayList<GroupMember>();
                        for (String member : memberList) {
                            groupMembers.add(new GroupMember(member, userList.get(member)));
                        }
                        //sort the result in order according to the mac-address and email according for Json output
                        Collections.sort(groupMembers, new GroupMemberEmailMacaddressComparator());
                        firstLayer.addProperty("size", groupMembers.size());
                        firstLayer.addProperty("total-time-spent", group.getTotalTime());
                        arr.add(firstLayer);
                        jsonOutputObject.add("groups", arr);
                        JsonArray arr1 = new JsonArray();

                        for (GroupMember groupMember : groupMembers) {
                            JsonObject secondLayer = new JsonObject();
                            String email = groupMember.getEmail();
                            String macAdd = groupMember.getMac_Address();
                            secondLayer.addProperty("email", email);
                            secondLayer.addProperty("mac-address", macAdd);
                            arr1.add(secondLayer);
                        }
                        firstLayer.add("members", arr1);
                        JsonArray arr2 = new JsonArray();
                        
                        LinkedHashMap finalLocations = group.getLocations();

                        // gets all the entries of the location map out
                        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(finalLocations.entrySet());

                        // sort the list of map entries according to location id and time spent at the locations for Json output
                        Collections.sort(entries, new GroupLocationIDComparator());
                        
                        for (Map.Entry<String, Integer> location : entries) {
                            JsonObject thirdLayer = new JsonObject();
                            String loc = location.getKey();
                            int timeSpent = location.getValue();
                            thirdLayer.addProperty("location", loc);
                            thirdLayer.addProperty("time-spent", timeSpent);
                            arr2.add(thirdLayer);
                        }
                        firstLayer.add("locations", arr2);
                    }
                    out.println(gson.toJson(jsonOutputObject));
                } else {
                    request.setAttribute("date", timestamp);
                    request.setAttribute("groupDetectionResults", groupDetectionResults);
                    rd.forward(request, response);
                }
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