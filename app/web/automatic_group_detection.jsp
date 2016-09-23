<%@page import="model.Utility"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.GroupDetectionResults"%>
<%@page import="model.Group"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
    <a name="top"></a>
    <head>
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap-datetimepicker.min.css" />
        <script type="text/javascript" src="js/moment.js"></script>
        <title>Automatic Group Detection</title>
        <%@include file="/include/header_info.jsp"%>
    </head>
    <body>
        <div class="container"> 
            <%@include file="/include/protect.jsp"%>
            <%@include file="/include/navbar.jsp"%>
            <%@include file="/include/noscript.jsp"%>
        </div>
        <div class="container"> 
            <div class="well-small">
                <h2>Automatic Group Detection</h2><hr>

                <!--A form for user to key in date and time, then it will be process in automatic group detection servlet-->
                <form action="AutomaticGroupDetection.do" method="post">
                    <div class="input-group date" id="datetimepicker1">
                        <p>Please enter a date and time:</p><input type="text" name="date" class="form-control" value='<%=Utility.nullCheck((String) request.getAttribute("date"))%>' data-date-format="YYYY-MM-DD HH:mm:ss" placeholder="YYYY-MM-DD HH:MM:SS" required>
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
                    </br><input type="submit" value="Submit" class="btn btn-primary"> 
                </form>
            </div>   
            <%
                GroupDetectionResults groupDetectionResults = (GroupDetectionResults) request.getAttribute("groupDetectionResults");
                ArrayList<String> outputList = (ArrayList<String>) request.getAttribute("outputList");

                // print out all the output from the results obtained
                if (groupDetectionResults != null) {
                    Integer totalUsers = groupDetectionResults.getTotalUsers();
                    HashMap<String, String> userList = groupDetectionResults.getUserList();
                    ArrayList<Group> groupList = groupDetectionResults.getGroupList();
                    // if there are more than one user found
                    int groupCount = 1;
                    if (totalUsers > 0) {
            %>
            <!-- display total results summary -->
            <div class='well well-small text-center'> 
                <b>Total users found: <%=  totalUsers%></b><br/>
                <b>Total groups found: <%=  groupList.size()%></b>
            </div>

            <%
                for (Group group : groupList) {
                    ArrayList<String> groupMembers = group.getGroupMembers();
                    HashMap<String, Integer> locations = group.getLocations();
                    // display the size of group and the total time spent together in a group
            %> 
            <!-- display individual groups found -->
            <div class="well">
                <div class="col-md-6">          
                    <table class='table table-striped  table-bordered'>
                        <!-- begin table header of each group -->
                        <tr>
                            <th class='col-md-2'width="25%">Group <%=groupCount%></th>
                            <th class='col-md-2'width="40%">Mac address</th>
                            <th class='col-md-2' width="35%">Email</th></tr>
                                <% String member = groupMembers.get(0);%>
                        <tr><td rowspan="<%=groupMembers.size()%>">
                                <!-- begin table summary of each group -->
                                <table class="table table-striped table-bordered" >
                                    <tr><td class="col-md-2"><b>Group Size:</b><%=groupMembers.size()%> </td> </tr>
                                    <tr><td class="col-md-2"><b>Total time spent together:  </b><%=  group.getTotalTime()%></td></tr>
                                    <tr><td class="col-md-2">
                                            <!-- begin specific details of each row for each group -->
                                            <table class='table table-striped table-bordered'><tr><th class='col-md-1'>location_id</th><th class='col-md-1'>Time (seconds)</th></tr> <%                            //display locations and time spent for each location
                                                Iterator<String> iter = locations.keySet().iterator();
                                                while (iter.hasNext()) {
                                                    String locationID = iter.next();
                                                        %> 
                                                <tr><td class='col-md-1'><%=  locationID%></td>
                                                    <td class='col-md-1'><%=  locations.get(locationID)%></td>
                                                </tr> <%
                                                    }
                                                %> </table> <%
                                                    groupCount++;
                                                %>
                                        </td>
                                    </tr>    
                                </table>                                        
                            </td>
                            <td class='col-md-2'><%=  member%></td>
                            <td class='col-md-2'><%=  userList.get(member)%></td>
                        </tr>
                        <%                            
                            // For every group, I will print out the mac-address, email of the members found in this group
                            for (int i = 1; i < groupMembers.size(); i++) {
                                String m = groupMembers.get(i);
                        %>
                        <tr>
                            <td class='col-md-2'><%=  m%></td>
                            <td class='col-md-2'><%=  userList.get(m)%></td>
                        </tr>
                        <%
                            }
                        %> 
                    </table> 
                </div>
            </div>
            <%  }
                out.println("<center><a href=\"#top\">TOP OF PAGE</a></center>");
            } else {
                 // if there are no groups detected
            %> <h3>Status: <font>There are no detected groups for this timing.</font></h3> <%
                }
                //Check for any errors
            } else if (outputList != null) {
                for (String error : outputList) {
                %> <h4><font color='red'><%=  error%></font> </h4> <%
                        }
                    }
                %>
        </div>
    </body>
</html>