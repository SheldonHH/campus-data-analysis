<%
    // destroy the session and redirect user to index.jsp
    session.invalidate();
    response.sendRedirect("/index.jsp");
%>