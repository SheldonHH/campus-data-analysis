<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="model.Utility"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.BasicBreakdownObject"%>
<!DOCTYPE html>
<html>
    <a name="top"></a>
    <head>
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap-datetimepicker.min.css" />
        <script type="text/javascript" src="js/moment.js"></script>
        <title>Basic Location Report Breakdown</title>
        <!--To use script and style that is standardize across functions-->
        <%@include file="/include/header_info.jsp"%>
    </head>
    <body>
        <div class="container"> 
            <!--To prevent user from accessing the page without logging in-->
            <%--<%@include file="/include/protect.jsp"%>--%>
            <!-- To display navigation bar-->
            <%@include file="/include/navbar.jsp"%>
            <!--Remind user to enable Java script in browser-->
            <%@include file="/include/noscript.jsp"%>
        </div>

        <%            // Retrieve timestamp through a request object from BasicBreakdownServlet 
            String timestamp = request.getParameter("timeRequested");
            if (timestamp == null) {
                timestamp = "";
            }

            // Retrieve order (criteria) through a request object from BasicBreakdownServlet
            String[] orderArray = request.getParameterValues("order");
            List<String> orders = new ArrayList<String>();
            if (orderArray != null) {
                orders = Arrays.asList(orderArray);
            }
        %>

        <div class="container">  
            <div class="well-small">
                <h2>Breakdown of students by  Gender / School / Year</h2><hr>
                <!--A form for user to key in floor and date and time, then it will be process in heatmap servlet-->
                <!--A form for user to key in date and criteria, then it will be process in breakdown servlet-->
                <form action="breakdown.do" method="post">
                    <div class="col-md-12">
                        <div class="col-md-6">
                            <div class="input-group date" id="datetimepicker1">
                                <p> Please enter a date and time:</p><input type="text" name="date" class="form-control" value='<%=Utility.nullCheck((String) request.getAttribute("date"))%>' data-date-format="YYYY-MM-DD HH:mm:ss" placeholder="YYYY-MM-DD HH:MM:SS" required>
                                <span class="input-group-addon"><span class="glyphicon-calendar glyphicon"></span>
                                </span>
                            </div>
                            <script type="text/javascript">
                                $(function() {
                                    $('#datetimepicker1').datetimepicker({useSeconds: true});
                                });
                            </script>
                            <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
                        </div>
                        <div class="col-md-6">
                            <p>Please select one or more criteria to search:</p>
                            <select name="order" required>
                                <option value="" default selected>Select 1st criteria</option>
                                <option value="gender" <%=Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "gender")%>>Gender</option>
                                <option value="school"<%=Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "school")%>>School</option>
                                <option value="year" <%=Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "year")%>>Year</option>
                            </select>

                            <select name="order">
                                <option value="" default selected>Select 2nd criteria (optional)</option>
                                <option value="gender" <%=Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "gender")%>>Gender</option>
                                <option value="school" <%=Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "school")%>>School</option>
                                <option value="year" <%=Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "year")%>>Year</option>
                            </select>

                            <select name="order">
                                <option value="" default selected>Select 3rd criteria (optional)</option>
                                <option value="gender" <%=Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "gender")%>>Gender</option>
                                <option value="school"<%=Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "school")%>>School</option>
                                <option value="year" <%=Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "year")%>>Year</option>
                            </select>
                        </div>
                        <input type="submit" value="Submit" class="btn btn-primary">
                    </div>
                </form>
            </div>      

            <%
                // Retrieve breakdown object, number of elements, outputList  and order of the element through a request object from BasicBreakdownServlet 
                HashMap<Integer, BasicBreakdownObject> countAndBreakdownObject = (HashMap<Integer, BasicBreakdownObject>) request.getAttribute("countAndBreakdownObject");
                Integer NoOfOrdersSelected = (Integer) request.getAttribute("NoOfElements");
                ArrayList<String> outputList = (ArrayList<String>) request.getAttribute("outputList");
                String[] order = (String[]) request.getAttribute("order");

                // print out all the output from the results obtained
                if (countAndBreakdownObject != null) {

                    Integer[] countArray = countAndBreakdownObject.keySet().toArray(new Integer[1]);
                    Integer totalUsers = countArray[0];

                    //Pull out Basic Breakdown Object 
                    if (totalUsers > 0) {
                        // Actual Breakdown Results is stored in an arrayList ( refer to last basicBreakdownObject constructor )
                        ArrayList<BasicBreakdownObject> breakdownArray = countAndBreakdownObject.get(totalUsers).getBreakdown();

                        if (breakdownArray != null && breakdownArray.size() != 0) {
                            // Number of criteria the user chose and display result accordingly
                            switch (NoOfOrdersSelected) {

                                case 1:
                                    out.println("<div class='well well-small text-center'><b>Total Users:         " + totalUsers + "</b></div>");
                                    out.println("<table class='table table-striped  table-bordered'><tr><th>" + order[0] + "</th><th> Count</th></th><th> Percentage </th></tr>");
                                    for (BasicBreakdownObject innerBbo : breakdownArray) {
                                        int count = innerBbo.getCount();
                                        if (count > 0) {
                                            out.println("<tr><td width='33%'><b>" + innerBbo.getElement() + "</b></td>");
                                            if (count != 0) {
                                                out.println("<td width='33%'>" + count + "</td>");
                                            } else {
                                                out.println("<td> - </td>");
                                            }
                                            if (count != 0) {
                                                out.println("<td width='33%'>" + Math.round((double) count / totalUsers * 100.0) + "%</td>");
                                            } else {
                                                out.println("<td> - </td>");
                                            }
                                        }
                                    }
                                    out.println("</table>");

                                    break;

                                case 2:
                                    out.println("<div class='well well-small text-center'><b>Total Users:         " + totalUsers + "</b></div>");
                                    out.println("<table class='table table-striped table table-bordered'><tr><th width='16.7%'>" + order[0].substring(0, 1).toUpperCase() + order[0].substring(1)
                                            + "</th><th width='16.7%'> Count </th></th><th width='16.7%'> Percentage</th>" + "<th width='16.7%'>"
                                            + order[1].substring(0, 1).toUpperCase() + order[1].substring(1)
                                            + "</th><th width='16.7%'> Count </th></th><th width='16.7%'> Percentage</th></tr>");
                                    for (BasicBreakdownObject middleBbo : breakdownArray) {

                                        // retrieves all outer breakdown objects 
                                        int countForOuterObject = middleBbo.getCount();
                                        if (countForOuterObject > 0) {
                                            ArrayList<BasicBreakdownObject> innerBreakdownArray = middleBbo.getBreakdown();
                                            int countForInnerObject = innerBreakdownArray.size();

                                            out.println("<tr><td width='16.7%'><b>" + middleBbo.getElement() + "</b></td>");
                                            out.println("<td width='16.7%'>" + countForOuterObject + "</td>");
                                            out.println("<td width='16.7%'>" + Math.round((double) countForOuterObject / totalUsers * 100.0) + "%</td>");
                                            out.println("<td colspan='3'><table class='table table-striped table table-bordered' width='100%'>");

                                            for (BasicBreakdownObject innerBbo : innerBreakdownArray) {
                                                int count = innerBbo.getCount();
                                                if (count != 0) {
                                                    out.println("<tr><td width='33%' ><b>" + innerBbo.getElement() + "</b></td>");
                                                    if (count != 0) {
                                                        out.println("<td width='33%'>" + count + "</td>");
                                                    } else {
                                                        out.println("<td> - </td>");
                                                    }
                                                    if (count != 0) {
                                                        out.println("<td width='33%'>" + Math.round((double) count / totalUsers * 100.0) + "%</td>");
                                                    } else {
                                                        out.println("<td> - </td>");
                                                    }
                                                    out.println("</tr>");
                                                }
                                            }
                                            out.println("</table></td></tr>");
                                        }
                                    }

                                    out.println("</table>");
                                    break;

                                case 3:
                                    out.println("<div class='well well-small text-center'><b>Total Users:         " + totalUsers + "</b></div>");
                                    out.println("<table class='table table-striped table table-bordered'><tr><th width='11.1%'>" + order[0].substring(0, 1).toUpperCase() + order[0].substring(1)
                                            + "</th><th width='11.1%'> Count </th></th><th width='11.1%'> Percentage </th>" + "<th width='11.1%'>" + order[1].substring(0, 1).toUpperCase() + order[1].substring(1)
                                            + "</th><th width='11.1%'> Count </th></th><th width='11.1%'> Percentage </th><th width='11.1%'>" + order[2].substring(0, 1).toUpperCase() + order[2].substring(1)
                                            + "</th><th width='11.1%'> Count </th></th><th width='11.1%'> Percentage </th></tr>");

                                    for (BasicBreakdownObject outerBbo : breakdownArray) {
                                        int countForOuterObject = outerBbo.getCount();

                                        if (countForOuterObject > 0) {
                                            out.println("<tr><td width='11.1%'><b>" + outerBbo.getElement() + "</b></td>");
                                            out.println("<td width='11.1%'>" + countForOuterObject + "</td>");
                                            out.println("<td width='11.1%'>" + Math.round((double) countForOuterObject / totalUsers * 100.0) + "%</td>");
                                            out.println("<td colspan='6'><table class='table table-bordered' width='100%'>");

                                            ArrayList<BasicBreakdownObject> middleBreakdownArray = outerBbo.getBreakdown();
                                            for (BasicBreakdownObject middleBbo : middleBreakdownArray) {
                                                int countForMiddleObject = middleBbo.getCount();
                                                if (countForMiddleObject > 0) {
                                                    out.println("<td width='16.7%'><b>" + middleBbo.getElement() + "</b></td>");
                                                    out.println("<td width='16.7%'>" + countForMiddleObject + "</td>");
                                                    out.println("<td width='16.7%'>" + Math.round((double) countForMiddleObject / totalUsers * 100.0) + "%</td>");
                                                    out.println("<td colspan='3'><table  class='table table-bordered' width='100%'>");

                                                    ArrayList<BasicBreakdownObject> innerBreakdownArray = middleBbo.getBreakdown();
                                                    for (BasicBreakdownObject innerBbo : innerBreakdownArray) {

                                                        int count = innerBbo.getCount();
                                                        if (count != 0) {

                                                            out.println("<td width='30%'><b>" + innerBbo.getElement() + "</b></td>");
                                                            if (count != 0) {
                                                                out.println("<td width='30%'>" + count + "</td>");
                                                            } else {
                                                                out.println("<td> - </td>");
                                                            }
                                                            if (count != 0) {
                                                                out.println("<td width='30%'>" + Math.round((double) count / totalUsers * 100.0) + "%</td>");
                                                            } else {
                                                                out.println("<td> - </td>");
                                                            }
                                                            out.println("</tr>");
                                                        }
                                                    }

                                                    out.println("</table></td></tr>");
                                                }
                                            }
                                            out.println("</table></td></tr>");
                                        }
                                    }
                                    out.println("</table>");
                                    break;
                                default:
                                    out.println("<font color='red'>Number of categories selected wasn't 1, 2 or 3. This should never happen!!!</font>");
                                    break;
                            }

                        }
                        out.println("<center><a href=\"#top\">TOP OF PAGE</a></center>");
                    } else {
                        out.println("<h3>Status: <font>There are no records for this timing.</font></h3>");
                    }
                } else if (outputList != null) {
                    for (String error : outputList) {
                        out.println("<h4><font color='red'>" + error + "</font> </h4>");
                    }
                }
            %>   
        </div>
    </body>
</html>
