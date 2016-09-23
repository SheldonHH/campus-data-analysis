<%@page import="model.User"%>
<%
    User adminUser = (User) session.getAttribute("user");
    // Check if user is logged in OR if user is not admin
    if (session.getAttribute("user") == null) {
        response.sendRedirect("index.jsp");
        return;
    } else {
        if (!adminUser.getEmail().equals("admin")) {
            response.sendRedirect("main.jsp");
            return;
        }
    }
%>
