<%@page import="model.Utility"%>
<%@page import="model.HeatmapResult"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap-datetimepicker.min.css" />
        <script type="text/javascript" src="js/moment.js"></script>
        <link rel="stylesheet" href="css/main.css">
        <script src="scripts/main.js"></script>
        <style>
            .heatmap{
                width:800px;
                height:600px;
            }
        </style>
        <title>Heatmap Page</title>
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
                <h2>Heatmap</h2><hr>
                <!--A form for user to key in floor and date and time, then it will be process in heatmap servlet-->
                <form action="heatmap.do" method="post">
                    <p>Floor:</p>
                    <select name="floor" required>
                        <option class="text-center" value="0" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "B1")%>>B1</option>
                        <option value="1" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "L1")%>>L1</option>
                        <option value="2" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "L2")%>>L2</option>
                        <option value="3" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "L3")%>>L3</option>
                        <option value="4" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "L4")%>>L4</option>
                        <option value="5" <%=Utility.selectedCheck((String) request.getAttribute("floor"), "L5")%>>L5</option>
                    </select>
                    <div class="input-group date" id="datetimepicker1">
                        <p>Date and Time:</p><input type="text" name="date" class="form-control" value='<%=Utility.nullCheck((String) request.getAttribute("date"))%>' data-date-format="YYYY-MM-DD HH:mm:ss" placeholder="YYYY-MM-DD HH:MM:SS" required>
                        <span class="input-group-addon"><span class="glyphicon-calendar glyphicon"></span>
                        </span>
                    </div>
                    <!--Java Script for the date time picker-->
                    <script type="text/javascript">
                        $(function() {
                            $('#datetimepicker1').datetimepicker({useSeconds: true});
                        });
                    </script>
                    <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
                    </br><input type="submit" value="Submit" class="btn btn-primary"> 
                </form>
            </div>

            <%
                // A list of heatmap result obtained
                ArrayList<HeatmapResult> heatmapResult = (ArrayList<HeatmapResult>) request.getAttribute("heatmapResult");
                // A list of errors obtained
                ArrayList<String> outputList = (ArrayList<String>) request.getAttribute("outputList");

                // print out all the output from the results obtained
                if (heatmapResult != null) {
                    if (heatmapResult.size() > 0) {
                        out.println("<table class='table table-striped table-bordered'><tr><th width='50%'>Semantic Place</th><th width='25%'>Population</th><th width='25%'>Density</th></tr>");
                        for (HeatmapResult result : heatmapResult) {
                            out.println("<tr><td>");
                            out.println(result.getSemanticPlace() + "</td>");
                            out.println("<td>" + result.getNumPeople() + "</td>");
                            out.println("<td>" + result.getDensity());
                            out.println("</td></tr>");
                        }

                    } else {
                        out.println("<h3>Status: <font>There are no records for this timing.</font></h3>");
                    }
                    out.println("</table>");
                    // Check for any errors
                } else if (outputList != null) {
                    for (String error : outputList) {
                        out.println("<h4><font color='red'>" + error + "</font> </h4>");
                    }
                }

            %>


            <%                if (heatmapResult != null) {
                    if (heatmapResult.size() > 0) { %>

            <div class="heatmap">
                <%
                    String floor = (String) request.getAttribute("floor");
                    if (floor.equals("B1")) {
                        out.println("<h3><font>There is no map available for basement.</font></h3>");
                    } else if (floor.equals("L1")) {
                %> <img src="images/level1.png" alt=""/> <%
                } else if (floor.equals("L2")) {
                %> <img src="images/level2.png" alt=""/> <%
                } else if (floor.equals("L3")) {
                %> <img src="images/level3.png" alt=""/> <%
                } else if (floor.equals("L4")) {
                %> <img src="images/level4.png" alt=""/> <%
                } else if (floor.equals("L5")) {
                %> <img src="images/level5.png" alt=""/> <%
                    }
                %>
            </div>
            <%
                if (!floor.equals("B1")) {
            %>
            <script src="js/heatmap.min.js"></script>
            <script>
                        window.onload = function() {
                            var heatmapContainer = document.querySelector('.heatmap');
                            // minimal heatmap instance configuration
                            var heatmapInstance = h337.create({
                                // only container is required, the rest will be defaults
                                container: document.querySelector('.heatmap')
                            });


                            var points = [];
                            var max = 6;
                            var width = 800;
                            var height = 600;

                            // common locations
                            var lobbyArr = [50, 75, 220, 275];
                            var receptionArea = [20, 100, 120, 270];
                            var waitingArea = [100, 20, 130, 342];
                            var sem1 = [60, 50, 100, 100];
                            var sem2 = [60, 50, 210, 100];
                            var sem3 = [60, 50, 280, 100];
                            var sem4 = [70, 50, 390, 100];
                            var classRoom = [120, 50, 245, 210];
                            var studyArea1 = [50, 60, 75, 295];
                            var studyArea2 = [65, 50, 365, 210];
                            var acadOffice = [250, 50, 175, 60];

                            // level four locations
                            var lvl4StudyArea1 = [34, 66, 91, 76];
                            var lvl4StudyArea2 = [45, 60, 235, 200];
                            var lvl4StudyArea3 = [33, 50, 399, 150];
                            var lvl4StudyArea4 = [86, 35, 121, 278];

                            // level five locations
                            var lvl5StudyArea1 = [75, 40, 140, 282];
                            var lvl5StudyArea2 = [25, 140, 120, 251];

                            var heatArea = function(arr, density) {

                                var boxWidth = arr[0];
                                var boxHeight = arr[1];
                                var xCoord = arr[2];
                                var yCoord = arr[3];

                                for (var i = 0; i < boxWidth; i += 25) {
                                    for (var j = 0; j < boxHeight; j += 30) {
                                        var point = {
                                            x: xCoord + i,
                                            y: yCoord + j,
                                            value: density
                                        };
                                        points.push(point);
                                    }
                                }
                            };

                <%
                    for (HeatmapResult result : heatmapResult) {
                        // JSP code here
                        String semanticPlace = result.getSemanticPlace();
                        int density = result.getDensity();

                        if (semanticPlace.contains("LOBBY") && density > 0) {
                %>  heatArea(lobbyArr, <%=density%>); <%
                    }
                    if (semanticPlace.contains("RECEPTION") && density > 0) {
                %>  heatArea(receptionArea, <%=density%>); <%
                    }
                    if (semanticPlace.contains("WAITINGAREA") && density > 0) {
                %>  heatArea(waitingArea, <%=density%>); <%
                    }
                    if (semanticPlace.contains("CLSRM") && density > 0) {
                %>  heatArea(classRoom, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("SR2-1") || semanticPlace.contains("SR3-1")) && density > 0) {
                %>  heatArea(sem1, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("SR2-2") || semanticPlace.contains("SR3-2")) && density > 0) {
                %>  heatArea(sem2, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("SR2-3") || semanticPlace.contains("SR3-3")) && density > 0) {
                %>  heatArea(sem3, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("SR2-4") || semanticPlace.contains("SR3-4")) && density > 0) {
                %>  heatArea(sem4, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("L2STUDYAREA1") || semanticPlace.contains("L3STUDYAREA1")) && density > 0) {
                %>  heatArea(studyArea1, <%=density%>); <%
                    }
                    if ((semanticPlace.contains("L2STUDYAREA2") || semanticPlace.contains("L3STUDYAREA2")) && density > 0) {
                %>  heatArea(studyArea2, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L4STUDYAREA1") && density > 0) {
                %>  heatArea(lvl4StudyArea1, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L4STUDYAREA2") && density > 0) {
                %>  heatArea(lvl4StudyArea2, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L4STUDYAREA3") && density > 0) {
                %>  heatArea(lvl4StudyArea3, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L4STUDYAREA4") && density > 0) {
                %>  heatArea(lvl4StudyArea4, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L5STUDYAREA1") && density > 0) {
                %>  heatArea(lvl5StudyArea1, <%=density%>); <%
                    }
                    if (semanticPlace.contains("L5STUDYAREA2") && density > 0) {
                %>  heatArea(lvl5StudyArea2, <%=density%>); <%
                    }
                    if (semanticPlace.contains("ACADOFFICE") && density > 0) {
                %>  heatArea(acadOffice, <%=density%>); <%
                        }
                    }
                %>
                            var data = {
                                max: max,
                                data: points
                            };

                            heatmapInstance.setData(data);

                        };
            </script>
            <%
                        }
                    }
                }
            %>
        </div>
    </div>
</body>
</html>
