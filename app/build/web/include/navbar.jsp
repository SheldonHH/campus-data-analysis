<%@page import="model.User"%>

<%
    User user = (User) session.getAttribute("user");
%>

<div class="navbar">
    <div class="navbar-inner">
        <ul class="nav">
            <li><a href="main.jsp"><img src="images/sloca.png" height="38px" width="48px"></a></li>
            <li class="divider-vertical"></li>
            <li><a href="heatmap.jsp">Heatmap</a></li>            
            <li class="divider-vertical"></li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle"data-hover="dropdown">Basic Location Report<b class="caret"></b></a>
                <!--drop down bar for Basic Location Report-->
                <ul class="dropdown-menu">
                    <li><a tabindex="-1" href="basic_breakdown.jsp">Breakdown by year and gender</a></li>
                    <li><a tabindex="-1" href="basic_popular_place.jsp">Top-k popular places</a></li>
                    <li><a tabindex="-1" href="basic_companions.jsp">Top-k companions</a></li>
                    <li><a tabindex="-1" href="basic_next_place.jsp">Top-k next places</a></li>
                </ul>
            </li>
            <li class="divider-vertical"></li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle"data-hover="dropdown">Group Location Report<b class="caret"></b></a>
                <!--drop down bar for Group Location Report-->
                <ul class="dropdown-menu"> 
                    <li><a tabindex="-1" href="automatic_group_detection.jsp">Group Detection</a></li>
                    <li><a tabindex="-1" href="group_popular_place.jsp">Group Top-K Popular Place</a></li> 
                    <li><a tabindex="-1" href="group_next_places.jsp">Group Top-K Next Places</a></li> 
                </ul>
            </li>
            <li class="divider-vertical"></li>
        </ul>
        <!--Display the username of the logged in user-->
        <ul class="nav pull-right">
            <li class="dropdown">
                <a href="#" class="dropdown-toggle"data-hover="dropdown">Welcome, <b class="caret"></b></a>
                <!--drop down bar for Group Location Report-->
                <ul class="dropdown-menu"> 
                    <li><a tabindex="-1" href="include/logout.jsp">Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>