package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.ConnectionManager;
import dataManager.GroupDetectionManager;
import dataManager.GroupReportManager;
import dataManager.LocationLookupManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;
import model.Group;
import model.GroupDetectionResults;
import model.GroupNextPlacesResults;

/**
 * Validates date and time, semantic place, and k-value entered and sends result
 * or error messages to jsp to display
 *
 */
public class GroupNextPlacesServlet extends HttpServlet {

    private static HashMap<Integer, String> locationLookupMap = null;

    

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        RequestDispatcher rd = request.getRequestDispatcher("group_next_places.jsp");

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

        //Contains list of form inputs
        String k = request.getParameter("k");
        String location = request.getParameter("origin");
        String date = request.getParameter("date");
        String tokenParameter = request.getParameter("token");

        //Contains list of errors discovered
        ArrayList<String> outputList = new ArrayList<String>();

        int kValue = 0;
        try {

            if (date == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (date.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_DATE);
            } else {
                date = date.trim();
            }

            if (location == null) {
                outputList.add(ErrorUtility.MISSING_ORIGIN);
                // Blank and not missing because string "<parametername>=" are still found in the request when submit button is pressed
            } else if (location.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_ORIGIN);
            } else {
                location = location.trim();
            }
            if (k == null || k.trim().equals("")) {
                kValue = 3;
            } else {
                k = k.trim();

            }

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

            //NOTE : If errors above are present and Json Request is being called,-----------------CODE WILL STOP HERE ----------------- bottom validation is no longer needed 
            if (outputList.isEmpty()) {

                boolean isValidTime = true;
                //IF is Json request, ensure "T" is present in the date parameter value .
                if (isJsonRequest) {
                    if (date.split("T").length != 2) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                        isValidTime = false;
                    } else {
                        date = date.replace('T', ' ');
                    }
                }

                // Validates the date format of the date parameter
                if (isValidTime) {
                    try {
                        //Date format to convert user datetime to date object
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date parsedDate = sdf.parse(date);
                        String yr = date.substring(0, date.indexOf("-"));
                        int year = Integer.parseInt(yr);

                        if (year > 9999 || year < 1000) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        } else if (!sdf.format(parsedDate).equals(date)) {
                            outputList.add(ErrorUtility.INVALID_DATE);
                        }
                    } catch (ParseException e) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    } catch (NumberFormatException e) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    } catch (IllegalArgumentException e) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    }
                }

                // Validates the k-value input parameter i.e. must be proper integer >0 and <=10
                try {
                    // Parse the above value into an integer for ranking later on
                    if (kValue != 3) {
                        kValue = Integer.parseInt(k);
                    }
                    if (kValue < 1 || kValue > 10) {
                        outputList.add(ErrorUtility.INVALID_K);
                    }
                } catch (NumberFormatException e) {
                    outputList.add(ErrorUtility.INVALID_K);
                }

                // Validates the origin parameter selected 
                ArrayList<String> origins = new ArrayList<String>();

                // If is a json request , get the list of origins from the database, else get the list of origins from the request attribute "origins"
                origins = LocationLookupManager.retrieveAllSemanticPlaces();

                if (origins != null && !origins.contains(location)) {
                    outputList.add(ErrorUtility.INVALID_ORIGIN);
                }

            }

            // ERROR CHECKS FOR PARAMETER INPUTS ARE FINISHED  --  INPUT GROUP NEXT PLACES SERVLET CODE 
            // RANKS EACH LOCATION ACCORDING TO NUM OF GROUPS PRESENT AND RETURNS ARRAYLIST OF GROUPNEXTPLACES RESULTS TO THE JSP
            ArrayList<Group> groupsWithValidSemanticPlace = new ArrayList<Group>();
            int nextPlacesGroupsCount = 0;
            int previousWindowGroupsCount = 0;
            ArrayList<GroupNextPlacesResults> resultsToDisplay = new ArrayList<GroupNextPlacesResults>();
            HashMap<Integer, ArrayList<GroupNextPlacesResults>> newResult = new HashMap<Integer, ArrayList<GroupNextPlacesResults>>();
            if (outputList.isEmpty()) {
                Timestamp endDate = Timestamp.valueOf(date);
                Timestamp startDate = new Timestamp(endDate.getTime() - (15 * 60 * 1000));
                GroupDetectionManager groupManager = new GroupDetectionManager();
                GroupDetectionResults previousWindowGroupResults = groupManager.retrieveGroupDetectionResults(startDate, endDate);
                //Retrieve groups from previous window 
                groupsWithValidSemanticPlace = retrieveGroupsWithLastLocation(previousWindowGroupResults.getGroupList(), location);
                previousWindowGroupsCount = groupsWithValidSemanticPlace.size();
                // If no groups with valid semantic place from the above ArrayList, method ends with no results.
                if (groupsWithValidSemanticPlace.size() > 0) {
                    GroupReportManager groupReportManager = new GroupReportManager();
                    // IF THE NEXTWINDOW GROUP RESULTS IS NULL RIGHT ->  
                    HashMap<String, Integer> nextWindowGroupResults = groupReportManager.retrieveNextPlacesResults(groupsWithValidSemanticPlace, endDate, new Timestamp(endDate.getTime() + (15 * 60 * 1000)));

                    if (nextWindowGroupResults != null) {
                        // SORTS HASHMAP OF LOCATIONS IN DESCENDING ORDER OF NUM OF GROUPS PRESENT
                        List<Map.Entry<String, Integer>> sortedList = entriesSortedByValues(nextWindowGroupResults);
                        LinkedHashMap<String, Integer> sortedNextPlacesResults = new LinkedHashMap<String, Integer>();
                        Iterator<Map.Entry<String, Integer>> itSortedList = sortedList.iterator();
                        while (itSortedList.hasNext()) {
                            Map.Entry<String, Integer> entry = itSortedList.next();
                            sortedNextPlacesResults.put(entry.getKey(), entry.getValue());
                        }

                        // ITERATE THROUGH THE SORTED NEXT PLACES RESULTS AND INCLUDE A RANK BESIDE EACH SEMANTIC PLACE 
                        int rank = 0;
                        int tempCount = Integer.MAX_VALUE;
                        Iterator<String> it = sortedNextPlacesResults.keySet().iterator();
                        while (it.hasNext()) {
                            String semantic_place = it.next();
                            int count = sortedNextPlacesResults.get(semantic_place);
                            nextPlacesGroupsCount += count;
                            if (count < tempCount) {
                                rank++;
                                tempCount = count;
                            }
                            if (newResult.get(rank) == null) {
                                ArrayList<GroupNextPlacesResults> nResult = new ArrayList<GroupNextPlacesResults>();
                                nResult.add(new GroupNextPlacesResults(rank, semantic_place, count));
                                newResult.put(rank, nResult);
                            } else {
                                ArrayList<GroupNextPlacesResults> nResult = new ArrayList<GroupNextPlacesResults>();
                                nResult.add(new GroupNextPlacesResults(rank, semantic_place, count));
                                newResult.put(rank, nResult);
                            }
                            GroupNextPlacesResults results = new GroupNextPlacesResults(rank, semantic_place, count);
                            resultsToDisplay.add(results);
                        }
                    }
                }
            }
            if (!isJsonRequest) {
                if (outputList.isEmpty()) {
                    request.setAttribute("k", Integer.toString(kValue));
                    request.setAttribute("origin", location);
                    request.setAttribute("date", date);
                }
                request.setAttribute("outputList", outputList);
                request.setAttribute("nextPlacesResults", newResult);
                request.setAttribute("totalGroupsFound", previousWindowGroupsCount);
                request.setAttribute("totalUsersWhoVisitedNextPlace", nextPlacesGroupsCount);
                rd.forward(request, response);
                return;
            } else {
                HashMap<Integer, String> locationIDSemanticPlaceConverter = LocationLookupManager.retrieveAllLocationLookupData(ConnectionManager.getConnection());
                if (outputList.isEmpty()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject results = new JsonObject();
                    results.addProperty("status", "success");
                    results.addProperty("total-groups", groupsWithValidSemanticPlace.size());
                    results.addProperty("total-next-place-groups", nextPlacesGroupsCount);

                    JsonArray jsonArray = new JsonArray();

                    if (resultsToDisplay.size() > 0) {
                        for (GroupNextPlacesResults placeResults : resultsToDisplay) {
                            JsonObject jsonObj = new JsonObject();
                            jsonObj.addProperty("rank", placeResults.getRank());
                            jsonObj.addProperty("semantic-place", placeResults.getSemantic_place());
                            jsonObj.addProperty("num-groups", placeResults.getNum_groups());
                            jsonArray.add(jsonObj);
                        }
                    }
                    results.add("results", gson.toJsonTree(jsonArray));
                    out.println(gson.toJson(results));
                } else {
                    Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "error");
                    jsonOutputObject.add("messages", gson.toJsonTree(outputList));
                    out.println(gson.toJson(jsonOutputObject));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     *
     * @param groupList
     * @param location
     *
     * @return groups with last location in a form of ArrayList<Group>
     */
    public static ArrayList<Group> retrieveGroupsWithLastLocation(ArrayList<Group> groupList, String location) {
        ArrayList<Group> groupReturnList = new ArrayList<Group>();
        try {
            locationLookupMap = LocationLookupManager.retrieveAllLocationLookupData(ConnectionManager.getConnection());

            for (Group group : groupList) {
                // Retrieve all the current group locations
                LinkedHashMap<String, Integer> locations = group.getLocations();
                Iterator<String> locationIterator = locations.keySet().iterator();
                // Loop through all this group's locations
                String lastLocation = "";
                while (locationIterator.hasNext()) {
                    lastLocation = locationIterator.next();
                }
                if (locationLookupMap.get(Integer.parseInt(lastLocation)).equals(location)) {
                    groupReturnList.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return groupReturnList;
    }
    
    /**
     *
     * @param passedMap
     *
     * @return list of sorted values in descending order in a form of
     * LinkedHashMap<String, Integer>
     */
    static <String, Integer extends Comparable<? super Integer>>
            List<Map.Entry<String, Integer>> entriesSortedByValues(Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );
        return sortedEntries;
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
