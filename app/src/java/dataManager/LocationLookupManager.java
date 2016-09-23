package dataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Retrieves location look up data from database
 * 
 */
public class LocationLookupManager {

    /**
     * Retrieves all location-lookups from the database
     *
     * @param conn the connection that is processing this request
     * 
     * @return a HashMap which stores the location lookups
     */
    public static HashMap<Integer, String> retrieveAllLocationLookupData(Connection conn) {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> returnMap = new HashMap<Integer, String>();
        try {
            // Retrieve all location_id and semantic place from DB
            psmt = conn.prepareStatement("select * from location_lookup");
            rs = psmt.executeQuery();
            while (rs.next()) {
                returnMap.put(rs.getInt(1), rs.getString(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return returnMap;
    }

    /**
     * Retrieves all semantic place from the database
     * 
     * @return the list of all semantic place in a form of String ArrayList
     */
    public static ArrayList<String> retrieveAllSemanticPlaces() {
        ArrayList<String> semanticPlaces = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Retrieve distinct semantic_place from the DB
        String query = "select distinct semantic_place from location_lookup";
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                semanticPlaces.add(rs.getString(1));
            }
            Collections.sort(semanticPlaces, String.CASE_INSENSITIVE_ORDER);
            return semanticPlaces;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

}