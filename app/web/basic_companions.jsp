<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.Utility"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.CompanionsResult"%>
<!DOCTYPE html>
<html>
    <a name="top"></a>
    <head>
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap-datetimepicker.min.css" />
        <script type="text/javascript" src="js/moment.js"></script>
        <title>Basic Location Report Companions</title>
        <!--To use script and style that is standardize across functions-->
        <%@include file="/include/header_info.jsp"%>
    </head>
    <body>
        <div class="container">
            <!--To prevent user from accessing the page without logging in-->
            <%@include file="/include/protect.jsp"%>
            <!-- To display navigation bar-->
            <%@include file="/include/navbar.jsp"%>
            <!--Remind user to enable Java script in browser-->
            <%@include file="/include/noscript.jsp"%>
        </div>
        <div class="container">  
            <div class="well-small">
                <h2>Top-k Companions</h2><hr>
                <!--A form for user to key in k-value, mac address and date and time, then it will be process in companions servlet-->
                <form action="companions.do" method="post">
                    <%String selectedChoice = (String) request.getAttribute("k");%>
                    <p>Please select a K-Value:</p>
                    <select name="k" required>
                        <!--If k has been previously specified, display that same value again-->
                        <option value="1"<%=Utility.selectedCheck(selectedChoice, "1")%>>1</option>
                        <option value="2"<%=Utility.selectedCheck(selectedChoice, "2")%>>2</option>
                        <%
                            if (request.getAttribute("k") == null) {
                        %>
                        <option value="3" selected="selected">3</option>  
                        <%
                            } else {
                        %>
                        <option value="3"<%=Utility.selectedCheck(selectedChoice, "3")%>>3</option>
                        <%
                            }
                        %>
                        <option value="4"<%=Utility.selectedCheck(selectedChoice, "4")%>>4</option>
                        <option value="5"<%=Utility.selectedCheck(selectedChoice, "5")%>>5</option>
                        <option value="6"<%=Utility.selectedCheck(selectedChoice, "6")%>>6</option>
                        <option value="7"<%=Utility.selectedCheck(selectedChoice, "7")%>>7</option>
                        <option value="8"<%=Utility.selectedCheck(selectedChoice, "8")%>>8</option>
                        <option value="9"<%=Utility.selectedCheck(selectedChoice, "9")%>>9</option>
                        <option value="10"<%=Utility.selectedCheck(selectedChoice, "10")%>>10</option>
                    </select>
                    <!--If mac-address has been specified before, display mac address again -->
                    <p>Please enter a mac address: </p><input type="text" name="mac-address" value="<%=Utility.nullCheck((String) request.getAttribute("mac-address"))%>" required>

                    <div class="input-group date" id="datetimepicker1">
                        <p> Please enter a date and time:</p><input type="text" name="date" class="form-control" value='<%=Utility.nullCheck((String) request.getAttribute("date"))%>' data-date-format="YYYY-MM-DD HH:mm:ss" placeholder="YYYY-MM-DD HH:MM:SS" required>
                        <span class="input-group-addon"><span class="glyphicon-calendar glyphicon"></span>
                        </span>
                    </div>
                    <!--Java Script for the date time picker-->
                    <script type="text/javascript">
                        $(function() {
                            $('#datetimepicker1').datetimepicker({sideBySide: true, useSeconds: true});
                        });
                    </script>
                    <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
                    </br> <input type="submit" value="Submit" class="btn btn-primary">
                </form>
            </div>   
            <%
                // Retrieve the results to be displayed
                HashMap<Integer, ArrayList<CompanionsResult>> newResult = (HashMap<Integer, ArrayList<CompanionsResult>>) request.getAttribute("result");
                // Retrieve the general output message to be displayed
                ArrayList<String> outputList = (ArrayList<String>) request.getAttribute("outputList");
                ArrayList<CompanionsResult> companionsResult = null;
                // Check for any errors
                if (outputList != null && newResult == null) {
                    for (String error : outputList) {
                        out.println("<h4><font color='red'>" + error + "</font> </h4>");
                    }
                }
                // print out all the output from the results obtained
                if (newResult != null) {
                    if (newResult.size() > 0) {
                        out.println("<table class='table table-striped table-bordered'><tr><th width='20%'>Rank</th><th width='20%'>Time (seconds) </th><th width='30%'>Mac Address</th><th width='30%'>Email Address</th></tr>");
                        Iterator iter = newResult.keySet().iterator();
                        while (iter.hasNext()) {
                            companionsResult = newResult.get(iter.next());
                            CompanionsResult c = companionsResult.get(0);
                            out.println("<tr><td rowspan = \"" + companionsResult.size() + "\">");
                            out.println(c.getRank() + "</td>");
                            out.println("<td rowspan = \"" + companionsResult.size() + "\">" + c.getTimeSpent() + "</td>");
                            out.println("<td>" + c.getMacAddress() + "</td>");
                            out.println("<td>" + c.getEmail());
                            out.println("</td></tr>");
                            for (int i = 1; i < companionsResult.size(); i++) {
                                CompanionsResult result = companionsResult.get(i);
                                out.println("<tr>");
                                out.println("<td>" + result.getMacAddress() + "</td>");
                                out.println("<td>" + result.getEmail());
                                out.println("</td></tr>");
                            }
                        }
                        out.println("</table>");
                        out.println("<center><a href=\"#top\">TOP OF PAGE</a></center>");
                    } else {
                        out.println("<h3>Status: <font>There are no records for this timing.</font></h3>");
                    }
                }
            %>
        </div>
    </body>
</html>