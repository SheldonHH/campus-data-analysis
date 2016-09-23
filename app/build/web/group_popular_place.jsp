<%@page import="model.PopularPlacesResult"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.Utility"%>
<!DOCTYPE html>
<html>
    <a name="top"></a>
    <head>
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap-datetimepicker.min.css" />
        <script type="text/javascript" src="js/moment.js"></script>
        <title>Group Location Report Popular Places</title>
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
                <h2>Group Location Popular Places</h2><hr>
                <!--A form for user to key in k-value and date and time, then it will be process in group popular places servlet-->
                <form action="grouppopularplaces.do" method="post">
                    <p>Please select a K-Value:</p>

                    <select name="k" required>
                        <option value="1"<%=Utility.selectedCheck((String) request.getAttribute("k"), "1")%>>1</option>
                        <option value="2"<%=Utility.selectedCheck((String) request.getAttribute("k"), "2")%>>2</option>
                        <%
                            if (request.getAttribute("k") == null) {
                        %>
                        <option value="3" selected="selected">3</option>  
                        <%
                        } else {
                        %>
                        <option value="3"<%=Utility.selectedCheck((String) request.getAttribute("k"), "3")%>>3</option>
                        <%
                            }
                        %>
                        <option value="4"<%=Utility.selectedCheck((String) request.getAttribute("k"), "4")%>>4</option>
                        <option value="5"<%=Utility.selectedCheck((String) request.getAttribute("k"), "5")%>>5</option>
                        <option value="6"<%=Utility.selectedCheck((String) request.getAttribute("k"), "6")%>>6</option>
                        <option value="7"<%=Utility.selectedCheck((String) request.getAttribute("k"), "7")%>>7</option>
                        <option value="8"<%=Utility.selectedCheck((String) request.getAttribute("k"), "8")%>>8</option>
                        <option value="9"<%=Utility.selectedCheck((String) request.getAttribute("k"), "9")%>>9</option>
                        <option value="10"<%=Utility.selectedCheck((String) request.getAttribute("k"), "10")%>>10</option>
                    </select>
                    <div class="input-group date" id="datetimepicker1">
                        <p>Please enter a date and time:</p><input type="text" name="date" class="form-control" value='<%=Utility.nullCheck((String) request.getAttribute("date"))%>' data-date-format="YYYY-MM-DD HH:mm:ss" placeholder="YYYY-MM-DD HH:MM:SS" required>
                        <span class="input-group-addon"><span class="glyphicon-calendar glyphicon"></span>
                        </span>
                    </div>
                    <!--Java Script for the date time picker-->
                    <script type="text/javascript">
                        $(function() {
                            $('#datetimepicker1').datetimepicker({useSeconds: true});
                        });
                    </script>
                    <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script></br><input type="submit" value="Submit" class="btn btn-primary"> 
                </form>
            </div>
            <%
                ArrayList<PopularPlacesResult> popularResult = null;
                ArrayList<String> outputList = (ArrayList<String>) request.getAttribute("outputList");
                // retrieve map containing the results list for different ranks
                HashMap<Integer, ArrayList<PopularPlacesResult>> newResult = (HashMap<Integer, ArrayList<PopularPlacesResult>>) request.getAttribute("groupPopularPlacesResult");
                // print out all the output from the results obtained
                if (newResult != null) {
                    if (newResult.size() > 0) {
                        out.println("<table class='table table-striped table-bordered'><tr><th width='25%'>Rank</th><th width='25%'>Count</th><th width='50%'>Semantic Place</th></tr>");
                        //get different ranks
                        Iterator iter = newResult.keySet().iterator();
                        while (iter.hasNext()) {
                            popularResult = newResult.get(iter.next());
                            PopularPlacesResult p = popularResult.get(0);
                            //merge raws for the same rank and count
                            out.println("<tr><td rowspan = \" " + popularResult.size() + "\">");
                            out.println(p.getRank() + "</td>");
                            out.println("<td rowspan = \" " + popularResult.size() + "\">" + p.getCount() + "</td>");
                            out.println("<td>" + p.getSemantic_place() + "</td>");
                            out.println("</tr>");
                            for (int i = 1; i < popularResult.size(); i++) {
                                PopularPlacesResult result = popularResult.get(i);
                                out.println("<tr>");
                                out.println("<td>" + result.getSemantic_place() + "</td>");
                                out.println("</tr>");
                            }
                        }

                        out.println("</table>");
                        //back to top button
                        out.println("<center><a href=\"#top\">TOP OF PAGE</a></center>");
                    } else {
                        out.println("<h3>Status: <font>There are no records for this timing.</font></h3>");
                    }
                    // Check for any errors
                } else if (outputList != null) {
                    for (String error : outputList) {
                        out.println("<h4><font color='red'>" + error + "</font> </h4>");
                    }
                }

            %>
        </div>
    </body>
</html>