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
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import model.ErrorUtility;
import model.FileRecordError;
import model.FileRecordErrorComparator;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

/**
 * Validates files uploaded
 *
 */
public class UploadFileServlet extends HttpServlet {

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
        try {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            ArrayList<String> outputList = new ArrayList<String>();
            RecordValidationController demographicsValidationController = null;
            RecordValidationController locationValidationController = null;
            boolean fileExist = false;

            // Check if it is a Json Request
            // Remember to change this once functional
            // Still disabled. Unsure on how to proceed
            // Verify the content type
            boolean isValidUpload = false;

            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                outputList.add("error");
                outputList.add(ErrorUtility.MISSING_BOOTSTRAPFILE);
            } else {
                // A container to store all the files
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

                    // If this is a JSON request, check that the token provided is valid
                    if (isJsonRequest) {
                        if (parametersMap.keySet().contains("token")) {
                            DiskFileItem tokenString = parametersMap.get("token");
                            String tokenParameter = tokenString.getString();
                            if (tokenParameter.trim().isEmpty()) {
                                outputList.add("error");
                                outputList.add(ErrorUtility.BLANK_TOKEN);
                            } else {
                                tokenParameter = tokenParameter.trim();
                                try {
                                    String secretToken = "g4t7!_1234567890"; //(String) session.getAttribute("secretToken");
                                    if (secretToken != null) {
                                        String user = JWTUtility.verify(tokenParameter, secretToken);
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
                        DiskFileItem file = parametersMap.get("bootstrap-file");
                        if (!file.isFormField()) {
                            String fileName = file.getName();

                            //Retrieve the extension for checking
                            try {
                                // Retrieve the extension name
                                String extension = fileName.substring(fileName.length() - 4);
                                // Check if it is a .zip file!
                                if (extension.equals(".zip")) {

                                    InputStream demographicsInputStream = retrieveSpecificFile(file, "demographics.csv");
                                    
                                    InputStream locationInputStream = retrieveSpecificFile(file, "location.csv");
                                    

                                    // Instantiate a recordValidationController to store the errors
                                    // They are null because it has not been determined if there are location and / or demographics files
                                    if (demographicsInputStream == null && locationInputStream == null) {
                                        outputList.add("error");
                                        outputList.add(ErrorUtility.BLANK_BOOTSTRAPFILE);
                                    } else {
                                        
                                        fileExist = true;
                                        Connection conn = ConnectionManager.getConnection();
                                        // Check if the file is demographics
                                        if (demographicsInputStream != null) {
                                            BufferedReader brDemographics = new BufferedReader(new InputStreamReader(demographicsInputStream));
                                            isValidUpload = true;

                                            // Flush off the header first
                                            CSVReader reader = new CSVReader(brDemographics, ',');
                                            reader.readNext();
                                            int rowCounter = 1;
                                            demographicsValidationController = new RecordValidationController();

                                            ArrayList<String[]> recordList = new ArrayList<String[]>();
                                             String[] line = null;
                                            while ((line = reader.readNext()) != null) {

                                                // Takes the line and increments the counter.          
                                                rowCounter++;
                                                boolean isValidRecord = demographicsValidationController.validateDemographics(conn, line, rowCounter, "demographics.csv");

                                                // If the record is valid, add it into the recordList for future processing
                                                if (isValidRecord) {
                                                    recordList.add(line);
                                                }

                                                // Once hit 500, push to DB for insertion
                                                if (recordList.size() % 500 == 0 && recordList.size() > 0) {
                                                    ProcessRecordsManager.updateDemographics(conn, recordList);
                                                    recordList.clear();
                                                }
                                            }
                                            // Push the remaining entries from the 500
                                            ProcessRecordsManager.updateDemographics(conn, recordList);
                                            recordList.clear();

                                            ArrayList<FileRecordError> demographicsErrorList = demographicsValidationController.getErrorRecords();
                                            if (demographicsErrorList.isEmpty()) {
                                                outputList.add("success");
                                               
                                            } else {
                                                outputList.add("error");                                                                                           
                                                resultMap.put("demographicsErrorList", demographicsErrorList);
                                                
                                                 
                                            }
                                            outputList.add("" + demographicsValidationController.getSuccessfulRows());
                                        }
                                        // If it is not demographics.csv, check if it is location.csv
                                        if (locationInputStream != null) {
                                            BufferedReader brLocationInputStream = new BufferedReader(new InputStreamReader(locationInputStream));
                                            isValidUpload = true;
//                                            Scanner scanLocation = new Scanner(locationInputStream);
                                            locationValidationController = new RecordValidationController();
                                            // Flush off header
                                            // Flush away the header
                                            CSVReader reader = new CSVReader(brLocationInputStream, ',');
                                            reader.readNext();
                                            ArrayList<String[]> recordList = new ArrayList<String[]>();
                                            ArrayList<Integer> successfulRowRecordsList = new ArrayList<Integer>();
                                            int rowCounter = 1;

                                            String[] line = null;

                                            // Iterate through all the lines
                                            while ((line = reader.readNext()) != null) {

                                                
                                                
                                                // Takes the line and increments the counter.          
                                                rowCounter++;
                                                // Sends for validation
                                                boolean isValidRecord = locationValidationController.validateLocation(conn, line, rowCounter, "location.csv", recordList, successfulRowRecordsList, false);
                                                // If valid, split the line and store in the ArrayList
                                                if (isValidRecord) {
                                                    recordList.add(line);
                                                    successfulRowRecordsList.add(rowCounter);
                                                }

                                                // Once the ArrayList hits 50000, send to DB for insertion of data
                                                if (recordList.size() % 50000 == 0 && recordList.size() > 0) {
                                                    ProcessRecordsManager.updateLocation(conn, recordList, successfulRowRecordsList, false);
                                                    recordList.clear();
                                                    successfulRowRecordsList.clear();
                                                }
                                            }

                                            // Incase any stragglers from the 50000
                                            ProcessRecordsManager.updateLocation(conn, recordList, successfulRowRecordsList, false);
                                            recordList.clear();
                                            successfulRowRecordsList.clear();

                                            // Retrieve all the errors
                                            ArrayList<FileRecordError> locationErrorList = locationValidationController.getErrorRecords();
                                            if (locationErrorList.isEmpty()) {
                                                outputList.add("success");
                                                
                                            } else {
                                                // Clears whatever prior status it is
                                                outputList.clear();
                                                outputList.add("error");
                                                
                                                resultMap.put("locationErrorList", locationErrorList);                                        
                                                
                                            }
                                            // Determine output to print out   
                                            // If locations are successfully loaded and no demographics processed 
                                            outputList.add("" + locationValidationController.getSuccessfulRows());
                                        }
                                        conn.close();
                                    }
                                } else {
                                    // If the file is not a .csv
                                    outputList.add("error");
                                    outputList.add(ErrorUtility.MISSING_BOOTSTRAPFILE);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                outputList.add("error");
                                outputList.add(ErrorUtility.MISSING_BOOTSTRAPFILE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    outputList.add("error");
                    outputList.add(ErrorUtility.MISSING_BOOTSTRAPFILE);
                }
                resultMap.put("status", outputList);
                
            }
            // If this is a json request
            if (isJsonRequest) {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject jsonOutputObject = new JsonObject();
                jsonOutputObject.addProperty("status", outputList.get(0));
                // If the files are not found / error detected
                if (!isValidUpload) {
                    jsonOutputObject.addProperty("message", outputList.get(1));
                } else {
                    JsonArray jsonNumRecordLoadedArray = new JsonArray();
                    // If demographicsValidationController is not null, add the number of successful rows processed
                    if (demographicsValidationController != null) {
                        JsonObject jsonNumRecordLoaded = new JsonObject();
                        jsonNumRecordLoaded.addProperty("demographics", demographicsValidationController.getSuccessfulRows());
                        jsonNumRecordLoadedArray.add(jsonNumRecordLoaded);
                    }

                    // If locationValidationController is not null, add the number of successful rows processed
                    if (locationValidationController != null) {
                        JsonObject jsonNumRecordLoaded = new JsonObject();
                        jsonNumRecordLoaded.addProperty("location", locationValidationController.getSuccessfulRows());
                        jsonNumRecordLoadedArray.add(jsonNumRecordLoaded);
                    }
                    // Add the number of successful rows added in
                    jsonOutputObject.add("num-record-loaded", jsonNumRecordLoadedArray);

                    // Checks if there are any errors from the processed files
                    if ((locationValidationController != null && !locationValidationController.getErrorRecords().isEmpty())
                            || (demographicsValidationController != null && !demographicsValidationController.getErrorRecords().isEmpty())) {
                        JsonArray jsonErrorArray = new JsonArray();

                        if (demographicsValidationController != null && !demographicsValidationController.getErrorRecords().isEmpty()) {

                            ArrayList<FileRecordError> demographicsFileRecordErrors = demographicsValidationController.getErrorRecords();
                            // Sort according to row number
                            Collections.sort(demographicsFileRecordErrors, new FileRecordErrorComparator());
                            for (FileRecordError fileError : demographicsFileRecordErrors) {
                                // Create a new JsonObject to store the file name, line and error messages
                                JsonObject jsonDemographicsErrorObject = new JsonObject();
                                jsonDemographicsErrorObject.addProperty("file", fileError.getFileName());
                                jsonDemographicsErrorObject.addProperty("line", fileError.getRowNumber());
                                jsonDemographicsErrorObject.add("message", gson.toJsonTree(fileError.getErrorMessages()));
                                // Add these messages to the json error array
                                jsonErrorArray.add(jsonDemographicsErrorObject);
                            }
                        }

                        if (locationValidationController != null && !locationValidationController.getErrorRecords().isEmpty()) {
                            ArrayList<FileRecordError> locationFileRecordErrors = locationValidationController.getErrorRecords();
                            // Sort according to row numbers
                            Collections.sort(locationFileRecordErrors, new FileRecordErrorComparator());
                            for (FileRecordError fileError : locationFileRecordErrors) {
                                // Create a new JsonObject to store the file name, line and error messages
                                JsonObject jsonLocationErrorObject = new JsonObject();
                                jsonLocationErrorObject.addProperty("file", fileError.getFileName());
                                jsonLocationErrorObject.addProperty("line", fileError.getRowNumber());
                                jsonLocationErrorObject.add("message", gson.toJsonTree(fileError.getErrorMessages()));
                                // Add these messages to the json error array
                                jsonErrorArray.add(jsonLocationErrorObject);
                            }
                        }
                        jsonOutputObject.add("error", jsonErrorArray);
                    }
                }
                out.println(gson.toJson(jsonOutputObject));
            } else {
                resultMap.put("status", outputList);
                request.setAttribute("uploadResult", resultMap);
                RequestDispatcher rd = request.getRequestDispatcher("admin_panel.jsp");
                rd.forward(request, response);
            }

        } finally {
            out.close();
        }
    }

    /**
     * Retrieves the bootstrapFileItem
     *
     * @param fileItem the diskFileItem to be looped through
     * @param fileName the fileName which is to be retrieved
     * @return the Inputstream containing the wanted object
     * @throws Exception
     */
    public InputStream retrieveSpecificFile(DiskFileItem fileItem, String fileName) {
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
            ex.printStackTrace();
        }

        return null;

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
