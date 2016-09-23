package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.BasicReportManager;
import dataManager.LocationLookupManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
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
import model.NextPlacesResults;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;

/**
 * Validates date and time, semantic place, and k-value entered and sends result
 * or error messages to jsp to display
 *
 */
public class BasicNextPlacesServlet extends HttpServlet {

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

        // Contains list of form inputs
        String k = request.getParameter("k");
        String location = request.getParameter("origin");
        String date = request.getParameter("date");
        String tokenParameter = request.getParameter("token");
        ArrayList<NextPlacesResults> resultsToDisplay = new ArrayList<NextPlacesResults>();
        // BasicReportManager provides 2 methods 1) previousretrievePreviousWindow 2) retrieveNextPlacesResult  
        BasicReportManager basicReportManager = new BasicReportManager();
        NextPlacesResults nextPlacesResult = null;
        // Contains list of errors discovered
        ArrayList<String> outputList = new ArrayList<String>();
        // Contains list of Mac addresses from the previous window
        ArrayList<String> previousMacAddresses = null;
        // Stores hashmap of semantic places and their respective count
        Map<String, Integer> locationResults = null;
        Map<String, Integer> sortedMap = null;
        HashMap<Integer, ArrayList<NextPlacesResults>> newResult = new HashMap<Integer, ArrayList<NextPlacesResults>>();

        // Total number of users belonging to the previous window
        int totalUserCount = 0;
        // Total number of users who visited another place.
        int totalUserVisit = 0;

        int kValue = 0;

        RequestDispatcher rd = request.getRequestDispatcher("basic_next_place.jsp");

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
                if (isJsonRequest) {
                    if (date.split("T").length != 2) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                        isValidTime = false;
                    } else {
                        date = date.replace('T', ' ');
                    }
                }

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

                //Check for invalid origin
                ArrayList<String> origins = new ArrayList<String>();

                // If is a json request , get the list of origins from the database, else get the list of origins from the request attribute "origins"
                if (isJsonRequest) {
                    origins = LocationLookupManager.retrieveAllSemanticPlaces();
                } else {
                    origins = (ArrayList<String>) request.getAttribute("origins");
                }
                if (origins != null && !origins.contains(location)) {
                    outputList.add(ErrorUtility.INVALID_ORIGIN);
                }

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
            }

            // If no errors , continue 
            if (outputList.isEmpty()) {

                Timestamp endDate = Timestamp.valueOf(date);
                Timestamp startDate = new Timestamp(endDate.getTime() - (15 * 60 * 1000 - 1000));

                //retrieve list of mac_addresses belonging to the previous window
                previousMacAddresses = basicReportManager.retrievePreviousWindow(location, startDate, endDate);

                //retrieve total number of users from the previous window 
                totalUserCount = previousMacAddresses.size();

                startDate = new Timestamp(endDate.getTime() + 1000);
                endDate = new Timestamp(endDate.getTime() + (15 * 60 * 1000));

                //retrieves NextPlaceResults - includes a hashmap of semantic places and user count  
                nextPlacesResult = basicReportManager.retrieveNextPlacesResult(previousMacAddresses, startDate, endDate);

                if (nextPlacesResult != null && nextPlacesResult.getCount() > 0) {

                    locationResults = nextPlacesResult.getResults();

                    if (locationResults != null) {

                        //creates a map container
                        HashMap<String, Integer> derivedMap = new HashMap<String, Integer>();
                        derivedMap.putAll(locationResults);

                        // derivedMap may be null 
                        Iterator<Integer> it = derivedMap.values().iterator();

                        while (it.hasNext()) {
                            totalUserVisit += it.next();
                        }

                        //sorts the hashMap in order 
                        sortedMap = sortHashMapByValuesDescending(derivedMap);

                        int rank = 1;
                        int previousValue = 0;
                        int currentValue = 0;
                        //converts the keySet into a String array to loop through
                        Set<String> s = sortedMap.keySet();

                        String[] array = s.toArray(new String[s.size()]);
                        if (array.length >= 1) {
                            previousValue = sortedMap.get(array[0]);
                        }
                        for (int i = 0; i < array.length; i++) {

                            // retrieve the number of users in each semantic place
                            currentValue = sortedMap.get(array[i]);
                            if (currentValue < previousValue) {
                                rank++;
                            }
                            if (rank <= kValue) {
                                if (newResult.get(rank) == null) {
                                    ArrayList<NextPlacesResults> nResult = new ArrayList<NextPlacesResults>();
                                    nResult.add(new NextPlacesResults(rank, array[i], currentValue));
                                    newResult.put(rank, nResult);
                                } else {
                                    ArrayList<NextPlacesResults> nResult = new ArrayList<NextPlacesResults>();
                                    nResult.add(new NextPlacesResults(rank, array[i], currentValue));
                                    newResult.put(rank, nResult);
                                }
                                resultsToDisplay.add(new NextPlacesResults(rank, array[i], currentValue));
                            } else {
                                break;
                            }
                            previousValue = currentValue;
                        }
                    }
                }
            }

            // If no errors, continue   
            if (outputList.isEmpty()) {
                if (!isJsonRequest) {
                    request.setAttribute("k", Integer.toString(kValue));
                    request.setAttribute("origin", location);
                    request.setAttribute("date", date);
                    request.setAttribute("nextPlacesResult", newResult);
                    request.setAttribute("totalUserCount", totalUserCount);
                    //retrieve total users who visited another place
                    request.setAttribute("totalUserVisit", totalUserVisit);
                    rd.forward(request, response);
                } else {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject results = new JsonObject();
                    results.addProperty("status", "success");
                    results.addProperty("total-users", totalUserCount);
                    results.addProperty("total-next-place-users", totalUserVisit);
                    JsonArray jsonArray = new JsonArray();

                    for (NextPlacesResults placeResults : resultsToDisplay) {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty("rank", placeResults.getRank());
                        jsonObj.addProperty("semantic-place", placeResults.getSemantic_place());
                        jsonObj.addProperty("count", placeResults.getCount());

                        jsonArray.add(jsonObj);
                    }
                    results.add("results", gson.toJsonTree(jsonArray));
                    out.println(gson.toJson(results));
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
                    rd.forward(request, response);
                }
            }

        } finally {
            out.close();
        }
    }

    /**
     *
     * @param passedMap
     * 
     * @return a LinkedHashpMap with values sorted in descending order
     */
    public LinkedHashMap sortHashMapByValuesDescending(Map passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.reverse(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = (Integer) valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = (String) keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Integer) val);
                    break;
                }
            }
        }
        return sortedMap;
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
