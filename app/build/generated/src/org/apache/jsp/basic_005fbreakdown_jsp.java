package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import model.Utility;
import java.util.HashMap;
import java.util.ArrayList;
import model.BasicBreakdownObject;
import model.User;

public final class basic_005fbreakdown_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(3);
    _jspx_dependants.add("/include/header_info.jsp");
    _jspx_dependants.add("/include/navbar.jsp");
    _jspx_dependants.add("/include/noscript.jsp");
  }

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <a name=\"top\"></a>\n");
      out.write("    <head>\n");
      out.write("        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/bootstrap-datetimepicker.min.css\" />\n");
      out.write("        <script type=\"text/javascript\" src=\"js/moment.js\"></script>\n");
      out.write("        <title>Basic Location Report Breakdown</title>\n");
      out.write("        <!--To use script and style that is standardize across functions-->\n");
      out.write("        ");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("<meta name=\"viewport\" content=\"width=device-width initial-scale=1.0\">\n");
      out.write("\n");
      out.write("<!-- Optional theme -->\n");
      out.write("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap-theme.min.css\">\n");
      out.write(" \n");
      out.write("<!-- Latest compiled and minified CSS -->\n");
      out.write("<link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n");
      out.write("<link href=\"//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css\" rel=\"stylesheet\" />\n");
      out.write("<link href=\"http://fonts.googleapis.com/css?family=Abel|Open+Sans:400,600\" rel=\"stylesheet\" />\n");
      out.write("\n");
      out.write("<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>\n");
      out.write("\n");
      out.write("<script src=\"js/bootstrap-hover-dropdown.js\"></script>\n");
      out.write("\n");
      out.write("<!-- Latest compiled and minified JavaScript -->\n");
      out.write("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js\"></script>\n");
      out.write("\n");
      out.write("<style>\n");
      out.write("  \n");
      out.write("    ul.nav li.dropdown:hover > ul.dropdown-menu{\n");
      out.write("        display: block; \n");
      out.write("    }\n");
      out.write("\n");
      out.write("</style>");
      out.write("\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <div class=\"container\"> \n");
      out.write("            <!--To prevent user from accessing the page without logging in-->\n");
      out.write("            ");
      out.write("\n");
      out.write("            <!-- To display navigation bar-->\n");
      out.write("            ");
      out.write('\n');
      out.write('\n');

    User user = (User) session.getAttribute("user");

      out.write("\n");
      out.write("\n");
      out.write("<div class=\"navbar\">\n");
      out.write("    <div class=\"navbar-inner\">\n");
      out.write("        <ul class=\"nav\">\n");
      out.write("            <li><a href=\"main.jsp\"><img src=\"images/sloca.png\" height=\"38px\" width=\"48px\"></a></li>\n");
      out.write("            <li class=\"divider-vertical\"></li>\n");
      out.write("            <li><a href=\"heatmap.jsp\">Heatmap</a></li>            \n");
      out.write("            <li class=\"divider-vertical\"></li>\n");
      out.write("            <li class=\"dropdown\">\n");
      out.write("                <a href=\"#\" class=\"dropdown-toggle\"data-hover=\"dropdown\">Basic Location Report<b class=\"caret\"></b></a>\n");
      out.write("                <!--drop down bar for Basic Location Report-->\n");
      out.write("                <ul class=\"dropdown-menu\">\n");
      out.write("                    <li><a tabindex=\"-1\" href=\"basic_breakdown.jsp\">Breakdown by year and gender</a></li>\n");
      out.write("                    <li><a tabindex=\"-1\" href=\"basic_popular_place.jsp\">Top-k popular places</a></li>\n");
      out.write("                    <li><a tabindex=\"-1\" href=\"basic_companions.jsp\">Top-k companions</a></li>\n");
      out.write("                    <li><a tabindex=\"-1\" href=\"basic_next_place.jsp\">Top-k next places</a></li>\n");
      out.write("                </ul>\n");
      out.write("            </li>\n");
      out.write("            <li class=\"divider-vertical\"></li>\n");
      out.write("            <li class=\"dropdown\">\n");
      out.write("                <a href=\"#\" class=\"dropdown-toggle\"data-hover=\"dropdown\">Group Location Report<b class=\"caret\"></b></a>\n");
      out.write("                <!--drop down bar for Group Location Report-->\n");
      out.write("                <ul class=\"dropdown-menu\"> \n");
      out.write("                    <li><a tabindex=\"-1\" href=\"automatic_group_detection.jsp\">Group Detection</a></li>\n");
      out.write("                    <li><a tabindex=\"-1\" href=\"group_popular_place.jsp\">Group Top-K Popular Place</a></li> \n");
      out.write("                    <li><a tabindex=\"-1\" href=\"group_next_places.jsp\">Group Top-K Next Places</a></li> \n");
      out.write("                </ul>\n");
      out.write("            </li>\n");
      out.write("            <li class=\"divider-vertical\"></li>\n");
      out.write("        </ul>\n");
      out.write("        <!--Display the username of the logged in user-->\n");
      out.write("        <ul class=\"nav pull-right\">\n");
      out.write("            <li class=\"dropdown\">\n");
      out.write("                <a href=\"#\" class=\"dropdown-toggle\"data-hover=\"dropdown\">Welcome, <b>");
      out.print(user.getName());
      out.write("</b><b class=\"caret\"></b></a>\n");
      out.write("                <!--drop down bar for Group Location Report-->\n");
      out.write("                <ul class=\"dropdown-menu\"> \n");
      out.write("                    <li><a tabindex=\"-1\" href=\"include/logout.jsp\">Logout</a></li>\n");
      out.write("                </ul>\n");
      out.write("            </li>\n");
      out.write("        </ul>\n");
      out.write("    </div>\n");
      out.write("</div>");
      out.write("\n");
      out.write("            <!--Remind user to enable Java script in browser-->\n");
      out.write("            ");
      out.write("<noscript>\n");
      out.write("    <div class=\"row\">\n");
      out.write("        <h3>\n");
      out.write("            <font color='blue'> For a better experience on <b>SLOCA</b>, enable JavaScript in your browser</font>\n");
      out.write("        </h3>\n");
      out.write("    </div>\n");
      out.write("</noscript>");
      out.write("\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        ");
            // Retrieve timestamp through a request object from BasicBreakdownServlet 
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
        
      out.write("\n");
      out.write("\n");
      out.write("        <div class=\"container\">  \n");
      out.write("            <div class=\"well-small\">\n");
      out.write("                <h2>Breakdown of students by  Gender / School / Year</h2><hr>\n");
      out.write("                <!--A form for user to key in floor and date and time, then it will be process in heatmap servlet-->\n");
      out.write("                <!--A form for user to key in date and criteria, then it will be process in breakdown servlet-->\n");
      out.write("                <form action=\"breakdown.do\" method=\"post\">\n");
      out.write("                    <div class=\"col-md-12\">\n");
      out.write("                        <div class=\"col-md-6\">\n");
      out.write("                            <div class=\"input-group date\" id=\"datetimepicker1\">\n");
      out.write("                                <p> Please enter a date and time:</p><input type=\"text\" name=\"date\" class=\"form-control\" value='");
      out.print(Utility.nullCheck((String) request.getAttribute("date")));
      out.write("' data-date-format=\"YYYY-MM-DD HH:mm:ss\" placeholder=\"YYYY-MM-DD HH:MM:SS\" required>\n");
      out.write("                                <span class=\"input-group-addon\"><span class=\"glyphicon-calendar glyphicon\"></span>\n");
      out.write("                                </span>\n");
      out.write("                            </div>\n");
      out.write("                            <script type=\"text/javascript\">\n");
      out.write("                                $(function() {\n");
      out.write("                                    $('#datetimepicker1').datetimepicker({useSeconds: true});\n");
      out.write("                                });\n");
      out.write("                            </script>\n");
      out.write("                            <script type=\"text/javascript\" src=\"js/bootstrap-datetimepicker.js\"></script>\n");
      out.write("                        </div>\n");
      out.write("                        <div class=\"col-md-6\">\n");
      out.write("                            <p>Please select one or more criteria to search:</p>\n");
      out.write("                            <select name=\"order\" required>\n");
      out.write("                                <option value=\"\" default selected>Select 1st criteria</option>\n");
      out.write("                                <option value=\"gender\" ");
      out.print(Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "gender"));
      out.write(">Gender</option>\n");
      out.write("                                <option value=\"school\"");
      out.print(Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "school"));
      out.write(">School</option>\n");
      out.write("                                <option value=\"year\" ");
      out.print(Utility.selectedCheck((orders.size() > 0 ? orders.get(0) : ""), "year"));
      out.write(">Year</option>\n");
      out.write("                            </select>\n");
      out.write("\n");
      out.write("                            <select name=\"order\">\n");
      out.write("                                <option value=\"\" default selected>Select 2nd criteria (optional)</option>\n");
      out.write("                                <option value=\"gender\" ");
      out.print(Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "gender"));
      out.write(">Gender</option>\n");
      out.write("                                <option value=\"school\" ");
      out.print(Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "school"));
      out.write(">School</option>\n");
      out.write("                                <option value=\"year\" ");
      out.print(Utility.selectedCheck((orders.size() >= 1 ? orders.get(1) : ""), "year"));
      out.write(">Year</option>\n");
      out.write("                            </select>\n");
      out.write("\n");
      out.write("                            <select name=\"order\">\n");
      out.write("                                <option value=\"\" default selected>Select 3rd criteria (optional)</option>\n");
      out.write("                                <option value=\"gender\" ");
      out.print(Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "gender"));
      out.write(">Gender</option>\n");
      out.write("                                <option value=\"school\"");
      out.print(Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "school"));
      out.write(">School</option>\n");
      out.write("                                <option value=\"year\" ");
      out.print(Utility.selectedCheck((orders.size() > 1 ? orders.get(2) : ""), "year"));
      out.write(">Year</option>\n");
      out.write("                            </select>\n");
      out.write("                        </div>\n");
      out.write("                        <input type=\"submit\" value=\"Submit\" class=\"btn btn-primary\">\n");
      out.write("                    </div>\n");
      out.write("                </form>\n");
      out.write("            </div>      \n");
      out.write("\n");
      out.write("            ");

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
            
      out.write("   \n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
