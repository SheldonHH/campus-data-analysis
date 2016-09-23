package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataManager.BasicReportManager;
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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BasicBreakdownObject;
import model.ErrorUtility;

/**
 * Validates date and time and elements entered and sends result or error
 * messages to jsp to display
 *
 */
public class BasicBreakdownServlet extends HttpServlet {

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
        if (session.getAttribute("user") == null) {
            if (!isJsonRequest) {
                response.sendRedirect("index.jsp");
                return;
            }
        }

        PrintWriter out = response.getWriter();
        RequestDispatcher rd = request.getRequestDispatcher("basic_breakdown.jsp");
        boolean isSuccessful = false;

        try {

            ArrayList<String> outputList = new ArrayList<String>();
            String[] order = request.getParameterValues("order");
            String date = request.getParameter("date");
            String tokenParameter = request.getParameter("token");

            HashMap<Integer, BasicBreakdownObject> countAndBreakdownObject = null;
            Timestamp ts_startDate = null;
            Timestamp ts_endDate = null;

            // Check if it is a Json Request
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

            // Check if the order is missing, blank ,or invalid 
            if (order == null) {
                outputList.add(ErrorUtility.MISSING_ORDER);
            } else if (order[0].trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_ORDER);
            } else {

            }
            // Check if date is missing or blank
            if (date == null) {
                outputList.add(ErrorUtility.MISSING_DATE);
            } else if (date.trim().isEmpty()) {
                outputList.add(ErrorUtility.BLANK_DATE);
            } else {
                date = date.trim();
            }

//NOTE : If errors above are present and Json Request is being called,-----------------CODE WILL STOP HERE ----------------- bottom validation is no longer needed 
            //Check validity of date parameter format
            if (outputList.isEmpty()) {

                //If Json Date Parameter doesn't contain a T, dateContainsT= false
                boolean dateContainsT = true;

                if (isJsonRequest) {
                    if (date.split("T").length != 2) {
                        outputList.add(ErrorUtility.INVALID_DATE);
                    } else {
                        date = date.replace('T', ' ');
                    }
                }

                // If no errors, continue verifying the Date Validity  
                if (dateContainsT) {

                    try {
                        //Date format to convert user datetime to date object
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sdf.setLenient(false);
                        Date parsedDate = sdf.parse(date);
                        String yr = date.substring(0, date.indexOf("-"));
                        int year = Integer.parseInt(yr);

                        if (year > 9999 || year < 1000) {
                            if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                                outputList.add(ErrorUtility.INVALID_DATE);
                            }
                        } else if (!sdf.format(parsedDate).equals(date)) {
                            if (!outputList.contains(ErrorUtility.INVALID_DATE)) {
                                outputList.add(ErrorUtility.INVALID_DATE);
                            }
                        } else {
                            ts_endDate = Timestamp.valueOf(date);
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

                //Date is Validated
                //Check if any order values are invalid                     
                if (isJsonRequest) {

                    // Check that order input doesn't have a mixture of valid and empty orders  -> e.g.  year,, / gender, / ,,,school 
                    if (order[0].contains(",")) {
                        String[] splitString = order[0].split(",");
                        if (splitString.length == 0) {
                            outputList.add(ErrorUtility.INVALID_ORDER);
                        } // Invalid order if order input contains commas but has only 1 selection to begin with 
                        else if (splitString.length == 1 && splitString[0].length() >= 1) {
                            outputList.add(ErrorUtility.INVALID_ORDER);
                        } else if (splitString.length > 1) {
                            //Invalid order if order input has more than 1 selection and more commas than required                        
                            if (order[0].length() - order[0].replaceAll(",", "").trim().length() >= splitString.length) {
                                outputList.add(ErrorUtility.INVALID_ORDER);
                            }
                        }
                    }
                    order = order[0].split(",");

                } else {

                    if (order[1].trim().isEmpty() && order[2].trim().isEmpty()) {
                        String chosenOrder = order[0];
                        order = new String[1];
                        order[0] = chosenOrder;
                    } else if (!order[1].trim().isEmpty() && order[2].trim().isEmpty()) {
                        String chosenOrder1 = order[0];
                        String chosenOrder2 = order[1];
                        order = new String[2];
                        order[0] = chosenOrder1;
                        order[1] = chosenOrder2;
                    } else if (order[1].trim().isEmpty() && !order[2].trim().isEmpty()) {
                        String chosenOrder1 = order[0];
                        String chosenOrder2 = order[2];
                        order = new String[2];
                        order[0] = chosenOrder1;
                        order[1] = chosenOrder2;
                    }
                }

                if (order.length == 1) {

                    if (!order[0].equals("gender") && !order[0].equals("school") && !order[0].equals("year")) {
                        if (!outputList.contains(ErrorUtility.INVALID_ORDER)) {
                            outputList.add(ErrorUtility.INVALID_ORDER);
                        }
                    }
                } else {
                    outerloop:
                    for (int i = 0; i < order.length; i++) {

                        String toCheck = order[i];
                        if (toCheck.trim().length() != 0) {

                            for (int j = i + 1; j < order.length; j++) {
                                String itemToCheck = order[j];
                                if (itemToCheck == null || (!itemToCheck.equals("gender") && !itemToCheck.equals("school") && !itemToCheck.equals("year"))) {
                                    if (!outputList.contains(ErrorUtility.INVALID_ORDER)) {
                                        outputList.add(ErrorUtility.INVALID_ORDER);
                                        break outerloop;
                                    }
                                } else if (itemToCheck.equals(toCheck)) {
                                    if (!outputList.contains(ErrorUtility.INVALID_ORDER)) {
                                        outputList.add(ErrorUtility.INVALID_ORDER);
                                        break outerloop;
                                    }
                                }
                            }
                        } else {
                            if (!outputList.contains(ErrorUtility.INVALID_ORDER)) {
                                outputList.add(ErrorUtility.INVALID_ORDER);
                                break outerloop;
                            }
                        }
                    }
                }
            }

            // All validated checks have passed
            if (outputList.isEmpty()) {
                
                BasicReportManager brm= new BasicReportManager();
                
                if (order.length == 1) {
                    countAndBreakdownObject = brm.retrieveBreakdownReport(order[0], ts_startDate, ts_endDate);
                    request.setAttribute("NoOfElements", 1);
                    request.setAttribute("countAndBreakdownObject", countAndBreakdownObject);
                } else if (order.length > 1) {
                    if (order.length == 2) {
                        request.setAttribute("NoOfElements", 2);
                        countAndBreakdownObject = brm.retrieveBreakdownReport(order[0], order[1], ts_startDate, ts_endDate);
                        request.setAttribute("countAndBreakdownObject", countAndBreakdownObject);

                    } else {
                        request.setAttribute("NoOfElements", 3);
                        countAndBreakdownObject = brm.retrieveBreakdownReport(order[0], order[1], order[2], ts_startDate, ts_endDate);
                        request.setAttribute("countAndBreakdownObject", countAndBreakdownObject);
                    }
                }
                
                
                if (isJsonRequest) {
                    //retrieve collection of BasicBreakdownObject values from countAndBreakdownObject hashmap
                    BasicBreakdownObject[] bboArray = countAndBreakdownObject.values().toArray(new BasicBreakdownObject[1]);
                    ArrayList<BasicBreakdownObject> bboArrayList = bboArray[0].getBreakdown();

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    JsonObject results = new JsonObject();

                    //Set status property to display success
                    results.addProperty("status", "success");
                    JsonElement breakdownObject = gson.toJsonTree(bboArrayList);

                    if (order.length == 1) {

                        JsonArray breakdownFor1Order = new JsonArray();

                        for (int i = 0; i < bboArrayList.size(); i++) {
                            JsonObject obj = new JsonObject();
                            BasicBreakdownObject bbobject = bboArrayList.get(i);
                            if (("year").equals(order[0])) {
                                int yearNum = Integer.parseInt(bbobject.getElement());
                                obj.addProperty(order[0], yearNum);
                            } else {
                                obj.addProperty(order[0], bbobject.getElement());
                            }
                            obj.addProperty("count", bbobject.getCount());
                            breakdownFor1Order.add(obj);
                            results.add("breakdown", breakdownFor1Order);
                        }

                    } else if (order.length == 2) {

                        JsonArray outerBreakdownFor2Orders = new JsonArray();

                        String outerOrderName = order[0];
                        String innerOrderName = order[1];

                        for (int i = 0; i < bboArrayList.size(); i++) {
                            JsonObject outerOrderObject = new JsonObject();
                            if (("year").equals(outerOrderName)) {
                                int yearNum = Integer.parseInt(bboArrayList.get(i).getElement());
                                outerOrderObject.addProperty(outerOrderName, yearNum);
                            } else {
                                outerOrderObject.addProperty(outerOrderName, bboArrayList.get(i).getElement());
                            }

                            JsonArray innerBreakdownFor2Orders = new JsonArray();
                            ArrayList<BasicBreakdownObject> innerBreakdownArray = bboArrayList.get(i).getBreakdown();
                            int countInnerBreakdown = 0;

                            for (int j = 0; j < innerBreakdownArray.size(); j++) {
                                BasicBreakdownObject innerBreakdownObject = innerBreakdownArray.get(j);
                                JsonObject innerOrderObject = new JsonObject();
                                if (("year").equals(innerOrderName)) {
                                    int yearNum = Integer.parseInt(innerBreakdownObject.getElement());
                                    innerOrderObject.addProperty(innerOrderName, yearNum);
                                } else {
                                    innerOrderObject.addProperty(innerOrderName, innerBreakdownObject.getElement());
                                }
                                innerOrderObject.addProperty("count", innerBreakdownObject.getCount());
                                countInnerBreakdown += innerBreakdownObject.getCount();
                                innerBreakdownFor2Orders.add(innerOrderObject);
                            }

                            outerOrderObject.addProperty("count", countInnerBreakdown);
                            outerOrderObject.add("breakdown", innerBreakdownFor2Orders);
                            outerBreakdownFor2Orders.add(outerOrderObject);
                        }

                        results.add("breakdown", outerBreakdownFor2Orders);

                    } else if (order.length == 3) {

                        JsonArray layer1BreakdownFor3Orders = new JsonArray();

                        String layer1OrderName = order[0];
                        String layer2OrderName = order[1];
                        String layer3OrderName = order[2];

                        for (int i = 0; i < bboArrayList.size(); i++) {
                            JsonObject layer1Object = new JsonObject();
                            if (("year").equals(layer1OrderName)) {
                                int yearNum = Integer.parseInt(bboArrayList.get(i).getElement());
                                layer1Object.addProperty(layer1OrderName, yearNum);
                            } else {
                                layer1Object.addProperty(layer1OrderName, bboArrayList.get(i).getElement());
                            }

                            int count1 = 0;
                            ArrayList<BasicBreakdownObject> layer2BreakdownArray = bboArrayList.get(i).getBreakdown();
                            JsonArray secondLayerJsonArray = new JsonArray();

                            for (int layer2 = 0; layer2 < layer2BreakdownArray.size(); layer2++) {

                                JsonObject layer2Object = new JsonObject();
                                if (("year").equals(layer2OrderName)) {
                                    int yearNum = Integer.parseInt(layer2BreakdownArray.get(layer2).getElement());
                                    layer2Object.addProperty(layer2OrderName, yearNum);
                                } else {
                                    layer2Object.addProperty(layer2OrderName, layer2BreakdownArray.get(layer2).getElement());
                                }

                                int count2 = 0;
                                ArrayList<BasicBreakdownObject> layer3BreakdownArray = layer2BreakdownArray.get(layer2).getBreakdown();
                                JsonArray lastLayerJsonArray = new JsonArray();

                                for (int layer3 = 0; layer3 < layer3BreakdownArray.size(); layer3++) {
                                    JsonObject layer3Object = new JsonObject();
                                    if (("year").equals(layer3OrderName)) {
                                        int yearNum = Integer.parseInt(layer3BreakdownArray.get(layer3).getElement());
                                        layer3Object.addProperty(layer3OrderName, yearNum);
                                    } else {
                                        layer3Object.addProperty(layer3OrderName, layer3BreakdownArray.get(layer3).getElement());
                                    }

                                    layer3Object.addProperty("count", layer3BreakdownArray.get(layer3).getCount());
                                    lastLayerJsonArray.add(layer3Object);
                                    count2 += layer3BreakdownArray.get(layer3).getCount();
                                }
                                layer2Object.addProperty("count", count2);
                                count1 += count2;
                                layer2Object.add("breakdown", lastLayerJsonArray);
                                secondLayerJsonArray.add(layer2Object);
                            }

                            layer1Object.addProperty("count", count1);
                            layer1Object.add("breakdown", secondLayerJsonArray);
                            layer1BreakdownFor3Orders.add(layer1Object);
                        }
                        results.add("breakdown", layer1BreakdownFor3Orders);
                    }
                    out.println(gson.toJson(results));

                    //processGsonRequest(order, bboArray[0].getBreakdown(), outputList, out);
                
                } else {
                    request.setAttribute("order", order);
                    request.setAttribute("date", date);
                    rd.forward(request, response);
                }

                // If there are errors
            } else {
                // Sort the errors alphabetically
                Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);
                if (!isJsonRequest) {
                    request.setAttribute("outputList", outputList);
                    rd.forward(request, response);
                } else {

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    JsonObject results = new JsonObject();

                    //Set status property to display error 
                    results.addProperty("status", "error");
                    Collections.sort(outputList, String.CASE_INSENSITIVE_ORDER);
                    JsonElement errors = gson.toJsonTree(outputList);
                    results.add("messages", errors);
                    out.println(gson.toJson(results));
                    //processGsonRequest(order, null, outputList, out);

                }
            }
        } catch (Exception e) {
            out.println(e.getMessage());
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
