css<%@page import="model.Utility"%>
<%@page import="model.FileRecordError"%>
<%@page import="java.util.*"%>
<%@page import="model.User"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Panel</title>
        <!--To use script and style that is standardize across functions-->
        <%@include file="/include/header_info.jsp"%>
    </head>
    <body>
        <div class="container">
            <!--To prevent non-admin from accessing the page without logging in-->
            <%@include file="/include/admin_protect.jsp"%>
            <!--Remind user to enable Java script in browser-->
            <%@include file="/include/noscript.jsp"%>
        </div>

        <div class="container"> 
            <div class="well">
                <h1>Admin Panel</h1>
                <hr>
                <!--Allow user to select a valid zip file that contain a demographics.csv, location-lookup.csv and location.csv to bootstrap-->
                <div class='page-header'>
                    <h3>Select a file to bootstrap: </h3>
                </div>

                <p>Upload a .zip file containing demographics.csv, location.csv and location-lookup.csv to bootstrap the system</p>
                <form action="bootstrap.do" method="post" enctype="multipart/form-data">                     
                    <input type="file" name="bootstrap-file"/><br>
                    <input type="submit" value="Bootstrap" />
                </form>

                <%                    String[] fileNames = new String[3];
                    fileNames[0] = "demographics.csv";
                    fileNames[1] = "location.csv";
                    fileNames[2] = "location-lookup.csv";

                    HashMap<String, Object> bootstrapResultMap = (HashMap<String, Object>) request.getAttribute("bootstrapResult");
                    if (bootstrapResultMap != null) {
                        ArrayList<String> bootstrapResultStatusList = (ArrayList<String>) bootstrapResultMap.get("status");

                        if (bootstrapResultStatusList.size() > 0) {
                %>
                <div class="well-small text-center">
                    <%
                        out.println("<u>" + " BOOTSTRAP RESULTS" + "</u><br/><br/>");
                        out.println("<u>" + bootstrapResultStatusList.get(1) + "</u><br/><br/>");
                        int maxLength = 3;
                        for (int i = 2; i < bootstrapResultStatusList.size(); i++) {
                            out.println((((i - 2) <= 2) ? fileNames[i - 2] : " ") + ": " + bootstrapResultStatusList.get(i) + "&nbsp &nbsp  ");
                        }
                    %>
                </div>
                <%
                        }

// Retrieve the results of all the various files
                        ArrayList<FileRecordError> locationList = (ArrayList<FileRecordError>) bootstrapResultMap.get("location");
                        ArrayList<FileRecordError> locationLookupList = (ArrayList<FileRecordError>) bootstrapResultMap.get("location-lookup");
                        ArrayList<FileRecordError> demographicsList = (ArrayList<FileRecordError>) bootstrapResultMap.get("demographics");

                        // print out all the output from the results obtained
                        if (locationList != null && locationLookupList != null && demographicsList != null
                                && (!locationList.isEmpty() || !locationLookupList.isEmpty() || !demographicsList.isEmpty())) {
                            out.println("<table class='table table-striped table-bordered'><tr><th>File</th><th>Line</th><th>Error Message</th></tr>");
                            if (locationList != null && !locationList.isEmpty()) {

                                FileRecordError fileRecordError = locationList.get(0);

                                for (int i = 0; i < locationList.size(); i++) {
                                    if (i == 0) {
                                        out.println("<tr><td rowspan = \" " + locationList.size() + "\">");
                                        out.println(fileRecordError.getFileName());
                                    } else {
                                        out.println("<tr>");
                                    }
                                    FileRecordError fileRecord = locationList.get(i);
                                    out.println("<td>");
                                    out.println(fileRecord.getRowNumber() + "</td><td>");
                                    out.println(fileRecord.getErrorMessages() + "</td>");
                                    out.println("</tr>");
                                }
                            }
                            if (locationLookupList != null && !locationLookupList.isEmpty()) {                              
                                
                                FileRecordError fileRecordError = locationLookupList.get(0);

                                for (int i = 0; i < locationLookupList.size(); i++) {

                                    if (i == 0) {
                                        out.println("<tr><td rowspan = \" " + locationLookupList.size() + "\">");
                                        out.println(fileRecordError.getFileName());
                                    } else {
                                        out.println("<tr>");
                                    }
                                    FileRecordError fileRecord = locationLookupList.get(i);
                                    out.println("<td>");
                                    out.println(fileRecord.getRowNumber() + "</td><td>");
                                    out.println(fileRecord.getErrorMessages() + "</td>");
                                    out.println("</tr>");
                                }
                            }
                            if (demographicsList != null && !demographicsList.isEmpty()) {

                                FileRecordError fileRecordError = demographicsList.get(0);

                                for (int i = 0; i < demographicsList.size(); i++) {
                                    if (i == 0) {
                                        out.println("<tr><td rowspan = \" " + demographicsList.size() + "\">");
                                        out.println(fileRecordError.getFileName());
                                    } else {
                                        out.println("<tr>");
                                    }
                                    FileRecordError fileRecord = demographicsList.get(i);
                                    out.println("<td>");
                                    out.println(fileRecord.getRowNumber() + "</td><td>");
                                    out.println(fileRecord.getErrorMessages() + "</td>");
                                    out.println("</tr>");
                                }
                            }
                            out.println("</table>");
                        }
                    }
                %>
                <hr>
                <!--Allow user to select a valid demographics or location csv file to upload-->
                <div class='page-header'>
                    <h3>Select a file to upload: </h3>
                </div>

                <p>Add more data to the system by uploading either demographics.csv or location.csv</p>

                <form action="uploadfile.do" method="post" enctype="multipart/form-data">                    
                    <input type="file" name="bootstrap-file"/></br>
                    <input type="submit" value="Upload File" />
                </form>

                <%
                    String[] uploadFileNames = new String[3];
                    uploadFileNames[0] = "demographics.csv";
                    uploadFileNames[1] = "location.csv";

                    HashMap<String, Object> uploadResultMap = (HashMap<String, Object>) request.getAttribute("uploadResult");
                    try {
                        if (uploadResultMap != null) {
                            ArrayList<String> uploadResultStatusList = (ArrayList<String>) uploadResultMap.get("status");
                            ArrayList<FileRecordError> demographicsErrorList = (ArrayList<FileRecordError>) uploadResultMap.get("demographicsErrorList");
                            ArrayList<FileRecordError> locationErrorList = (ArrayList<FileRecordError>) uploadResultMap.get("locationErrorList");

                            // display status result, either success or failure
                            if (uploadResultStatusList == null || uploadResultStatusList.isEmpty()) {
                                out.println("<h3>Status: <font color='red'>error</font></h3>");

                            } else if (uploadResultStatusList != null && !uploadResultStatusList.isEmpty()) {

                                if (uploadResultStatusList.size() > 0) {


                %>
                <div class="well-small text-center">
                    <%                        out.println("<u>" + " FILE UPLOAD RESULTS" + "</u><br/><br/>");
                        if (demographicsErrorList != null && !demographicsErrorList.isEmpty() && locationErrorList != null && !locationErrorList.isEmpty()) {
                            out.println("demographics.csv: -    location.csv: -");
                        } else {
                            for (int i = 1; i < uploadResultStatusList.size(); i++) {
                                if (i == 1) {
                                    try {
                                        Integer.parseInt(uploadResultStatusList.get(i));
                                        out.println("demographics.csv: ");
                                    } catch (Exception e) {
                                    }
                                }
                                if (i == 3) {
                                    try {
                                        Integer.parseInt(uploadResultStatusList.get(i));
                                        out.println("location.csv: ");
                                    } catch (Exception e) {
                                    }
                                }
                                if (i != 2) {
                                    out.println(uploadResultStatusList.get(i) + "&nbsp &nbsp  ");
                                }
                            }
                        }
                    %>
                </div>
                <%
                                }
                            }

                            if (demographicsErrorList != null || locationErrorList != null) {

                                out.println("<table class='table table-striped table-bordered'><tr><th>File</th><th>Line</th><th>Error Message</th></tr>");

                                if (demographicsErrorList != null && !demographicsErrorList.isEmpty()) {

                                    out.println("<tr><td rowspan = \" " + demographicsErrorList.size() + "\">");
                                    FileRecordError fileRecordError = demographicsErrorList.get(0);
                                    out.println(fileRecordError.getFileName() + "</td>");
                                    for (int i = 1; i < demographicsErrorList.size(); i++) {
                                        FileRecordError fileRecord = demographicsErrorList.get(i);
                                        out.println("<tr><td>");
                                        out.println(fileRecord.getRowNumber() + "</td><td>");
                                        out.println(fileRecord.getErrorMessages() + "</td>");
                                        out.println("</tr>");
                                    }

                                }
                                if (locationErrorList != null && !locationErrorList.isEmpty()) {
                                    out.println("<tr><td rowspan = \" " + locationErrorList.size() + "\">");
                                    FileRecordError fileRecordError = locationErrorList.get(0);
                                    out.println(fileRecordError.getFileName() + "</td>");
                                    for (int i = 1; i < locationErrorList.size(); i++) {
                                        FileRecordError fileRecord = locationErrorList.get(i);
                                        out.println("<tr><td>");
                                        out.println(fileRecord.getRowNumber() + "</td><td>");
                                        out.println(fileRecord.getErrorMessages() + "</td>");
                                        out.println("</tr>");
                                    }
                                }

                                out.println("</table>");
                            }

                        }
                    } catch (Exception e) {
                        out.println(e);
                        out.println("<font color='black'>Please upload a valid file!</font><br>");
                        e.printStackTrace();
                    }
                %>
                <hr>
                <a href="include/logout.jsp">
                    <button class="btn-success btn-large offset5">Logout</button>
                </a>
            </div>
        </div>     
    </body>
</html>
