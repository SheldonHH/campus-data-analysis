package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataManager.BasicReportManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.CompanionsResult;
import model.ErrorUtility;

/**
 * Validates date and time, mac-address, and k-value entered and sends result or
 * error messages to jsp to display
 *
 */
public class BasicCompanionsServlet extends HttpServlet {

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
        try {

            // Retrieve all the parameters passed from JSON / form
            String kValue = request.getParameter("k");
            String mac_address = request.getParameter("mac-address");
            String endDate = request.getParameter("date");
            String tokenParameter = request.getParameter("token");
            ArrayList<String> outputList = new ArrayList<String>();

            Date date = null;
            String startDate = "";
            int processKvalue = 0;
            // Validate k value
            if (kValue == null || "".equals(kValue)) {
                processKvalue = 3;
            }
            // Validate if mac-address has been provided
            if (mac_address == null) {
                outputList.add(ErrorUtility.MISSING_MACADDRESS);
            } else if ("".equals(mac_address)) {
                outputList.add(ErrorUtility.BLANK_MACADDRESS);
            }
            // Validate if timestamp has been provided
            if (endDate == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (endDate.isEmpty()) {
                outputList.add(ErrorUtility.BLANK_DATE);
            }
            // If it is a json request, perform token check
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

            if (outputList.isEmpty()) {
                // validate the time
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

                // validate mac address
                if (mac_address.length() != 40) {
                    outputList.add(ErrorUtility.INVALID_MACADDRESS);
                } else {
                    // Check that characters are valid
                    String hexdecimals = "1234567890abcedfABCEDF";
                    for (int j = 0; j < mac_address.length(); j++) {
                        String checkChar = "" + mac_address.charAt(j);
                        if (hexdecimals.indexOf(checkChar) == -1) {
                            outputList.add(ErrorUtility.INVALID_MACADDRESS);
                            break;
                        }
                    }
                }

                // validate the k value
                try {
                    if (processKvalue != 3) {
                        processKvalue = Integer.parseInt(kValue);
                        if (processKvalue < 1 || processKvalue > 10) {
                            outputList.add(ErrorUtility.INVALID_K);
                        }
                    }
                } catch (NumberFormatException e) {
                    outputList.add(ErrorUtility.INVALID_K);
                }

            }

            ArrayList<CompanionsResult> jsonTempCompanionResult = null;
            HashMap<Integer, ArrayList<CompanionsResult>> processedCompanionResult = null;

            if (outputList.isEmpty()) {
                // If there are no errors, process the retrieval of the copanion result
                BasicReportManager basicReportManager = new BasicReportManager();
                ArrayList<CompanionsResult> companionResult = null;
                jsonTempCompanionResult = new ArrayList<CompanionsResult>();
                processedCompanionResult = new HashMap<Integer, ArrayList<CompanionsResult>>();

                companionResult = basicReportManager.retrieveCompanionResult(mac_address, startDate, endDate);
                int max = Integer.MAX_VALUE;
                int rank = 1;

                // Begin ranking the companions based on timespent
                for (int i = 0; i < companionResult.size(); i++) {
                    int timespent = companionResult.get(i).getTimeSpent();
                    CompanionsResult result = companionResult.get(i);
                    // If rank is not equals to the K value specified and timespent is lesser than the current max timespent
                    if (rank != (processKvalue + 1) && timespent < max) {

                        result.setRank(rank++);
                        jsonTempCompanionResult.add(result);
                        ArrayList<CompanionsResult> nResult = new ArrayList<CompanionsResult>();
                        nResult.add(result);
                        processedCompanionResult.put(rank - 1, nResult);
                        max = timespent;
                    } else if (timespent == max) {
                        // For companions that have similar timespent as the previous processed companion
                        result.setRank(rank - 1);
                        jsonTempCompanionResult.add(result);
                        ArrayList<CompanionsResult> companions = processedCompanionResult.get(rank - 1);
                        companions.add(result);
                        processedCompanionResult.put(rank - 1, companions);
                    } else {
                        break;
                    }
                }
            }

            // If there are errors
            if (!outputList.isEmpty()) {
                // Sort alphabetically first
                Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);

                // If this is a JSON request
                if (isJsonRequest) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "error");
                    jsonOutputObject.add("messages", gson.toJsonTree(outputList));
                    out.println(gson.toJson(jsonOutputObject));
                } else {
                    request.setAttribute("outputList", outputList);
                    RequestDispatcher rd = request.getRequestDispatcher("basic_companions.jsp");
                    rd.forward(request, response);
                }
            } else {
                if (isJsonRequest) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", "success");

                    JsonArray jsonArray = new JsonArray();
                    for (CompanionsResult newCompanionResult : jsonTempCompanionResult) {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty("rank", newCompanionResult.getRank());
                        jsonObj.addProperty("companion", newCompanionResult.getEmail());
                        jsonObj.addProperty("mac-address", newCompanionResult.getMacAddress());
                        jsonObj.addProperty("time-together", newCompanionResult.getTimeSpent());
                        jsonArray.add(jsonObj);
                    }

                    jsonOutputObject.add("results", gson.toJsonTree(jsonArray));
                    out.println(gson.toJson(jsonOutputObject));
                } else {
                    request.setAttribute("k", kValue);
                    request.setAttribute("mac-address", mac_address);
                    request.setAttribute("date", endDate);
                    request.setAttribute("result", processedCompanionResult);
                    request.setAttribute("outputList", outputList);
                    RequestDispatcher rd = request.getRequestDispatcher("basic_companions.jsp");
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
