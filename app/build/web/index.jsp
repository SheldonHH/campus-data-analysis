<%@page import="model.Utility"%>
<%@page import="model.User"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link type="text/css" rel="stylesheet" href="css/bootstrap.css" media="screen"/>        
        <title>Login Page</title>
    </head>
    <body>
        <%
            // Retrieve user session and if its admin, redirect to admin_panel.jsp
            if (session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                if (user.getEmail().equals("admin")) {
                    response.sendRedirect("admin");
                    return;
                } else {
                    response.sendRedirect("main.jsp");
                    return;
                }
            }

        %>          
        <div class="hero-unit-login">
            <div class="container">
                <div class="span6">
                    <div class="well">
                        <h1>Login Page</h1></br>
                        <!--Remind user to enable Java script in browser-->
                        <%@include file="/include/noscript.jsp"%>
                        <%                            
                            String error = (String) request.getAttribute("error");
                            // Check for any errors
                            if (error != null) {
                                out.println("<div class=\"alert alert-danger\" role=\"alert\">");
                                out.print(error);
                                out.print("</div>");
                            }
                        %>
                        <!--A form for user to key username and password, then it will be process in login servlet-->
                        <form action="log_in.do" method="post" class="well"/>
                            <label>Username</label><input type="text" value='<%=Utility.nullCheck((String)request.getAttribute("username"))%>' placeholder="enter username" name="username" class="span3" required></br></br>
                            <label>Password</label><input type="password" value='' placeholder="enter password" name="password" class="span3" required></br></br>
                            <button class="btn btn-primary">Login</button>
                        </form>
                    </div>    
                </div>
            </div>
        </div>
    </body>
    <script type=text/javascripts" src="/js/bootstrap.js"/>
</html>