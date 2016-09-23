<%@page import="model.User"%>
<!DOCTYPE html>
<html>
    <head>
         <title>Main Page</title>
         <!--To use script and style that is standardize across functions-->
        <%@include file="/include/header_info.jsp"%>
    </head>

    <body id="page-top" class="index">
        <div class="container">
            <!--To prevent user from accessing the page without logging in-->
            <%@include file="/include/protect.jsp"%>
            <!-- To display navigation bar-->
            <%@include file="/include/navbar.jsp"%>
            <!--Remind user to enable Java script in browser-->
            <%@include file="/include/noscript.jsp"%>
 
            <div class="well-small col-md-12 ">
                <!-- Welcome greeting-->
                <h1 class="text-center" style="color: #999999">Welcome back, <%=user.getName().split(" ")[0]%> !</h1>
             </div>
            <div class="hero-unit">
                    <!-- Tabular overview of what the app has to offer through descriptions-->
                    <div class="well-main">
                        <div class="row">
                            <div class="span3">
                                <h3> Generate Heat Map Report <span class="glyphicon glyphicon-eye-open"></span> </h3>
                                <p>
                                    View the crowd density of any floor of the SIS building on any given day and time. 

                                </p>
                            </div>
                            <div class="span3">
                                <h3>Basic Location Reports <span class="glyphicon glyphicon-user"></h3>
                                <p>
                                    View various useful statistics about the School Of Information Systems for any given day and time.
                                </p>
                            </div>
                            <div class="span3">
                                <h3>Group Location Reports <span class="glyphicon glyphicon-map-marker"></h3>
                                <p>
                                    View the location and composition of groups at a particular timing and various statistics about the group
                                    including their top most popular places and top next places groups went after visiting a particular place
                                </p>
                            </div>
                        </div>         
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>