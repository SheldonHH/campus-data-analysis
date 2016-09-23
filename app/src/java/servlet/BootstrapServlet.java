package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import controller.RecordValidationController;
import dataManager.ConnectionManager;
import dataManager.ProcessRecordsManager;
import is203.JWTException;
import is203.JWTUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ErrorUtility;
import model.FileRecordError;
import model.FileRecordErrorComparator;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

/**
 * Validates files uploaded and bootstraps or sends error messages to jsp to
 * display
 *
 */
public class BootstrapServlet extends HttpServlet {

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

            // Store the successful and unsuccessful result into the hashmap
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            ArrayList<String> outputList = new ArrayList<String>();
            boolean fileExist = false;

            // Check if it is a Json Request
            // Remember to change this once functional
            // Still disabled. Unsure on how to proceed
            // Verify the content type
            boolean isValidBootstrap = false;

            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                outputList.add("error");
                outputList.add("Invalid request, Please upload a valid file.");
            } else {
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                List fileItems = null;
                try {
                    // Parse the request to get file items.
                    fileItems = upload.parseRequest(new ServletRequestContext(request));

                    HashMap<String, DiskFileItem> parametersMap = new HashMap<String, DiskFileItem>();
                    for (int i = 0; i < fileItems.size(); i++) {
                        DiskFileItem item = (DiskFileItem) fileItems.get(i);
                        parametersMap.put(item.getFieldName(), item);
                    }

                    // Check if this is a JSON request
                    if (isJsonRequest) {
                        // Check if the passed parameters contains a token
                        // Validate the token parameter
                        if (parametersMap.keySet().contains("token")) {
                            DiskFileItem tokenString = parametersMap.get("token");
                            String tokenParameter = tokenString.getString();

                            if (tokenParameter.trim().isEmpty()) {
                                outputList.add("error");
                                outputList.add(ErrorUtility.BLANK_TOKEN);
                            } else {
                                tokenParameter = tokenParameter.trim();
                                try {
                                    String secretToken = (String) session.getAttribute("secretToken");
                                    if (isJsonRequest) {
                                        String user = JWTUtility.verify(tokenParameter, "g4t7!_1234567890");
                                    } else {
                                        outputList.add("error");
                                        outputList.add(ErrorUtility.INVALID_TOKEN);
                                    }
                                } catch (JWTException e) {
                                    outputList.add("error");
                                    outputList.add(ErrorUtility.INVALID_TOKEN);
                                }
                            }
                        } else {
                            outputList.add("error");
                            outputList.add(ErrorUtility.MISSING_TOKEN);
                        }
                    }

                    // Process the uploaded file items
                    if (parametersMap.get("bootstrap-file") == null) {
                        outputList.add("error");
                        outputList.add(ErrorUtility.BLANK_BOOTSTRAPFILE);
                    }

                    if (outputList.isEmpty()) {
                        DiskFileItem bootstrapFileItem = parametersMap.get("bootstrap-file");
                        String fileName = bootstrapFileItem.getName();
                        //Retrieve the extension for checking
                        String checkExtension = "";
                        try {
                            checkExtension = fileName.substring(fileName.length() - 4);

                            // Check if extension is .zip!
                            if (checkExtension.equals(".zip")) {

                                // Retrieves the demographics.csv file method -> Check retrieve specific file method
                                InputStream demographicsInputStream = retrieveSpecificFile(bootstrapFileItem, "demographics.csv");
                                BufferedReader brDemographics = new BufferedReader(new InputStreamReader(demographicsInputStream));

                                InputStream locationLookupInputStream = retrieveSpecificFile(bootstrapFileItem, "location-lookup.csv");
                                BufferedReader brLocationLookUp = new BufferedReader(new InputStreamReader(locationLookupInputStream));

                                InputStream locationInputStream = retrieveSpecificFile(bootstrapFileItem, "location.csv");
                                BufferedReader brLocation = new BufferedReader(new InputStreamReader(locationInputStream));

                                // Check if the files exist!                                    
                                if (demographicsInputStream != null && locationLookupInputStream != null && locationInputStream != null) {
                                    fileExist = true;
                                    Connection conn = ConnectionManager.getConnection();
                                    // If all files exist, wipe the database
                                    ProcessRecordsManager.wipeDatabase(conn);

                                    // Individually process these files in the order specified in the Wiki
                                    RecordValidationController demographicsController = processDemographics(conn, brDemographics);
                                    RecordValidationController locationLookupController = processLocationLookup(conn, brLocationLookUp);
                                    RecordValidationController locationController = processLocation(conn, brLocation);
                                     

                                        // Retrieve all the errors from processing
                                        ArrayList<FileRecordError> demographicsErrorList = demographicsController.getErrorRecords();
                                    ArrayList<FileRecordError> locationLookupErrorList = locationLookupController.getErrorRecords();
                                    ArrayList<FileRecordError> locationErrorList = locationController.getErrorRecords();
                                    Collections.sort(locationErrorList, new FileRecordErrorComparator());
                                    // Determine output message

                                    if (demographicsErrorList.isEmpty() && locationLookupErrorList.isEmpty() && locationErrorList.isEmpty()) {

                                        outputList.add("success");
                                    } else {

                                        outputList.add("error");
                                    }

                                    // Set bootstrap boolean to true
                                    isValidBootstrap = true;
                                    // Prepare for output, number of records per file
                                    outputList.add("num-record-loaded:");
                                    outputList.add("" + demographicsController.getSuccessfulRows());// + " with " + demographicsController.getTotalErrors() + " errors");
                                    outputList.add("" + locationController.getSuccessfulRows());// + " with " + locationController.getTotalErrors() + " errors");
                                    outputList.add("" + locationLookupController.getSuccessfulRows());// + " with " + locationLookupController.getTotalErrors() + " errors");

                                    // Store these files in the hashmap for future use
                                    resultMap.put("status", outputList);
                                    resultMap.put("location", locationErrorList);
                                    resultMap.put("location-lookup", locationLookupErrorList);
                                    resultMap.put("demographics", demographicsErrorList);

                                    conn.close();
                                } else {
                                    // Checks which file is missing from the .zip file.
                                    outputList.add("error");
                                    String errorMessage = "";
                                    if (demographicsInputStream == null) {
                                        errorMessage = "missing demographics.csv file.";
                                        outputList.add(errorMessage);
                                    }

                                    if (locationLookupInputStream == null) {
                                        errorMessage = "missing location-lookup.csv file.";
                                        outputList.add(errorMessage);
                                    }

                                    if (locationInputStream == null) {
                                        errorMessage = "missing location.csv file.";
                                        outputList.add(errorMessage);
                                    }
                                }
                            } else {

                                outputList.add("error");
                                outputList.add("missing bootstrap-file");
                            }
                        } catch (IndexOutOfBoundsException e) {

                            outputList.add("error");
                            outputList.add("missing bootstrap-file");
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    outputList.add("error");
                    outputList.add("missing bootstrap-file");
                }
                // Set the HashMap of outputs and errors as an attribute to send for display 
                resultMap.put("status", outputList);
                if (isJsonRequest) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject jsonOutputObject = new JsonObject();
                    jsonOutputObject.addProperty("status", outputList.get(0));
                    if (isValidBootstrap) {
                        List<String> numRecordsLoaded = outputList.subList(2, outputList.size());
                        JsonArray numRecordsArray = new JsonArray();

                        JsonObject demographicsJsonObject = new JsonObject();
                        JsonObject locationJsonObject = new JsonObject();
                        JsonObject locationLookupJsonObject = new JsonObject();
                        demographicsJsonObject.addProperty("demographics.csv", Integer.parseInt(numRecordsLoaded.get(0)));
                        locationLookupJsonObject.addProperty("location-lookup.csv", Integer.parseInt(numRecordsLoaded.get(2)));
                        locationJsonObject.addProperty("location.csv", Integer.parseInt(numRecordsLoaded.get(1)));
                        numRecordsArray.add(demographicsJsonObject);
                        numRecordsArray.add(locationLookupJsonObject);
                        numRecordsArray.add(locationJsonObject);
                        jsonOutputObject.add("num-record-loaded", numRecordsArray);
                    }

                    if (outputList.get(0).contains("error")) {

                        if (!fileExist) {

                            jsonOutputObject.addProperty("message", outputList.get(1));

                        } else {

                            ArrayList<FileRecordError> joinedList = new ArrayList<FileRecordError>();
                            joinedList.addAll((ArrayList<FileRecordError>) resultMap.get("demographics"));
                            joinedList.addAll((ArrayList<FileRecordError>) resultMap.get("location-lookup"));
                            joinedList.addAll((ArrayList<FileRecordError>) resultMap.get("location"));

                            JsonArray jsonErrorList = new JsonArray();

                            for (int i = 0; i < joinedList.size(); i++) {

                                JsonObject jsonErrorObject = new JsonObject();
                                FileRecordError fileRecordobject = joinedList.get(i);
                                jsonErrorObject.addProperty("file", fileRecordobject.getFileName());
                                jsonErrorObject.addProperty("line", fileRecordobject.getRowNumber());
                                ArrayList<String> errorMessages = fileRecordobject.getErrorMessages();

                                jsonErrorObject.add("message", gson.toJsonTree(errorMessages));
                                jsonErrorList.add(jsonErrorObject);
                            }

                            jsonOutputObject.add("error", jsonErrorList);
                        }
                    }

                    out.println(gson.toJson(jsonOutputObject));

                } else {
                    request.setAttribute("bootstrapResult", resultMap);
                    RequestDispatcher rd = request.getRequestDispatcher("admin_panel.jsp");
                    rd.forward(request, response);
                }
            }
        } finally {
            out.close();

        }
    }

    /**
     * Retrieves the bootstrapFileItem of the filename specified if
     *
     * @param fileItem the diskFileItem to be looped through
     * @param fileName the fileName which is to be retrieved
     * @return the inputstream containing the wanted object
     * @throws Exception
     */
    public InputStream retrieveSpecificFile(DiskFileItem fileItem, String fileName) throws Exception {
        try {
            ZipInputStream zis = new ZipInputStream(fileItem.getInputStream());
            ZipEntry zipEntry;

            // Loop through each zip entry 
            while ((zipEntry = zis.getNextEntry()) != null) {
                // Check if the name matches what we are looking for
                if (zipEntry.getName().equals(fileName)) {
                    return zis;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;

    }

    /**
     * Processes the demographics.csv records
     *
     * @param conn the connection to process the request
     * @param scanDemographics the BufferedReader which contains the demographics.csv
     * @return the RecordValidationController containing the results
     */
    public RecordValidationController processDemographics(Connection conn, BufferedReader br) throws IOException {
        RecordValidationController demographicsController = new RecordValidationController();
        // Flush away the header
        CSVReader reader = new CSVReader(br, ',');
        reader.readNext();
        int rowCounter = 1;

        // Declare an ArrayList of StringArrays for successful rows. To be used later.
        ArrayList<String[]> successfulDemographicsRecordList = new ArrayList<String[]>();
        String[] line = null;

        // Iterate through all the lines
        while ((line = reader.readNext()) != null) {

            // Takes the line and increments the counter.          
            rowCounter++;

            // Sends for validation
            boolean isValidRecord = demographicsController.validateDemographics(conn, line, rowCounter, "demographics.csv");
            // If valid, split the line and store in the ArrayList
            if (isValidRecord) {
                successfulDemographicsRecordList.add(line);
            }
            // Every 500, process a batch query
            if (successfulDemographicsRecordList.size() % 1000 == 0 && !successfulDemographicsRecordList.isEmpty()) {
                // push to database
                ProcessRecordsManager.updateDemographics(conn, successfulDemographicsRecordList);
                successfulDemographicsRecordList.clear();
            }
        }

        // Incase any stragglers from the 1000
        ProcessRecordsManager.updateDemographics(conn, successfulDemographicsRecordList);
        successfulDemographicsRecordList.clear();
        return demographicsController;
    }

    /**
     * Processes the location-lookup.csv records
     *
     * @param conn the connection to process the request
     * @param scanLocationLookup the BufferedReader which contains the
     * location-lookup.csv
     * @return the RecordValidationController containing the results
     */
    public RecordValidationController processLocationLookup(Connection conn, BufferedReader br) throws IOException {
       // Declare an ArrayList of StringArrays for successful rows. To be used later.
        ArrayList<String[]> successfulLocationLookupRecordList = new ArrayList<String[]>();

        RecordValidationController locationLookupController = new RecordValidationController();
        // Flush away the header
        CSVReader reader = new CSVReader(br, ',');
        reader.readNext();
        int rowCounter = 1;

        String[] line = null;

        // Iterate through all the lines
        while ((line = reader.readNext()) != null) {
            rowCounter++;

            // Sends for validation
            boolean isValidRecord = locationLookupController.validateLocationLookup(line, rowCounter, "location-lookup.csv");
            // If valid, split the line and store in the ArrayList
            if (isValidRecord) {
                successfulLocationLookupRecordList.add(line);
            }
            // Every 1000, push to database
            if (successfulLocationLookupRecordList.size() % 1000 == 0 && !successfulLocationLookupRecordList.isEmpty()) {
                ProcessRecordsManager.updateLocationLookup(conn, successfulLocationLookupRecordList);
                successfulLocationLookupRecordList.clear();
            }
        }

        // Incase any stragglers from the 1000
        ProcessRecordsManager.updateLocationLookup(conn, successfulLocationLookupRecordList);
        successfulLocationLookupRecordList.clear();

        return locationLookupController;
    }

    /**
     * Processes the location.csv records
     *
     * @param conn the connection to process the request
     * @param scanLocation the BufferedReader which contains the location.csv
     * @return the RecordValidationController containing the results
     */
    public RecordValidationController processLocation(Connection conn, BufferedReader br) throws IOException {
        RecordValidationController locationController = new RecordValidationController();
        // Flush away the header
        CSVReader reader = new CSVReader(br, ',');
        reader.readNext();
        int rowCounter = 1;
        // Declare an ArrayList of StringArrays for successful rows. To be used later.
        ArrayList<String[]> successfulLocationRecordList = new ArrayList<String[]>();
        ArrayList<Integer> successfulRowRecordsList = new ArrayList<Integer>();
        String[] line = null;
        // Iterate through all the lines
        while ((line = reader.readNext()) != null) {
            rowCounter++;

            // Sends for validation
            boolean isValidRecord = locationController.validateLocation(conn, line, rowCounter, "location.csv", successfulLocationRecordList, successfulRowRecordsList, true);
            // If valid, split the line and store in the ArrayList
            if (isValidRecord) {
                successfulLocationRecordList.add(line);
                successfulRowRecordsList.add(rowCounter);
            }
            // Every 1000, process a batch query, clear the record list once it has been processed
            if (successfulLocationRecordList.size() % 50000 == 0 && !successfulLocationRecordList.isEmpty()) {
                ProcessRecordsManager.updateLocation(conn, successfulLocationRecordList, successfulRowRecordsList, true);
                successfulLocationRecordList.clear();
                successfulRowRecordsList.clear();
            }

        }

        // Incase any stragglers from the 1000
        ProcessRecordsManager.updateLocation(conn, successfulLocationRecordList, successfulRowRecordsList, true);
        successfulLocationRecordList.clear();
        successfulRowRecordsList.clear();

        return locationController;
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
