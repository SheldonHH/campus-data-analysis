package dataManager;

import java.sql.*;
import java.util.*;
import model.HeatmapResult;

/**
 * Retrieve data from database and generate Heatmap result
 *
 */
public class HeatmapManager {

    /**
     * Retrieves the number of people at a particular floor during a particular
     * time at specific places
     *
     * @param floor which you are interested in querying
     * @param startDate of the the query
     * @param endDate of the query
     *
     * @return the ArrayList of heatmap results
     */
    public ArrayList<HeatmapResult> retrieveHeatmapResult(String floor, String startDate, String endDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<HeatmapResult> heatmapResult = new ArrayList<HeatmapResult>();

        // inner to get semantic place with the mac_Add and filter by timestamp then group by semantic place to populate count of people at that specific location
        // String query to pass into the database to retrieve number of unique macAdd and semantic place
        String query = "select count(distinct mac_address), semantic_place from \n"
                + "	location_lookup loclookup left outer join\n"
                + "	(select t2.location_id, t2.mac_address from \n"
                + "		(select max(timestamp) as time, mac_address from location loc where timestamp > ? and timestamp <= ? group by mac_address) as t1 \n"
                + "		inner join (select * from location loc where timestamp > ? and timestamp <= ?) as t2 on t1.time like t2.timestamp \n"
                + "		and t1.mac_address = t2.mac_address ) as t3 \n"
                + "on t3.location_id = loclookup.location_id where semantic_place like ? group by semantic_place;";
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.setString(5, "%" + floor + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                int numPeople = rs.getInt(1);
                String semanticPlace = rs.getString(2);
                heatmapResult.add(new HeatmapResult(semanticPlace, numPeople));
            }
            Collections.sort(heatmapResult);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return heatmapResult;
    }
}
