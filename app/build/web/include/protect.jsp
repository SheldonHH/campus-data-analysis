<%@page import="model.User"%>
<%
    // direct user that has not logged in back to index.jsp
    if (session.getAttribute("user") == null) {
        response.sendRedirect("index.jsp");
        return;
    } else {
        User user = (User) session.getAttribute("user");
        // check if user is admin, if true direct admin to admin_panel.jsp
        if(user.getEmail().equals("admin")){
            response.sendRedirect("admin_panel.jsp");
            return;
        }
    }
%>
