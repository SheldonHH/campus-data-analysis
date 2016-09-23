package dataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Updates database and retrieves data from database
 *
 */
public class ProcessRecordsManager {

    /**
     * Inserts demographics records into the database
     *
     * @param conn the connection which is used to process the request
     * @param validRecordsList the list of records to populate the database with
     */
    public static void updateDemographics(Connection conn, ArrayList<String[]> validRecordsList) {
        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement("insert into demographics values (?,?,?,?,?)");
            conn.setAutoCommit(false);
            for (String[] validRecordArray : validRecordsList) {
                psmt.setString(1, validRecordArray[0].trim());
                psmt.setString(2, validRecordArray[1].trim());
                psmt.setString(3, validRecordArray[2].trim());
                psmt.setString(4, validRecordArray[3].trim());
                psmt.setString(5, validRecordArray[4].toUpperCase().trim());
                psmt.addBatch();
            }
            psmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Inserts location records into the database
     *
     * @param conn the connection used to process the request
     * @param validRecordsList the list of records to be inserted
     * @param successfulRowRecordList the list of integers corresponding to the
     * row number
     * @param isBootstrap boolean if it is bootstrap or not
     */
    public static void updateLocation(Connection conn, ArrayList<String[]> validRecordsList, ArrayList<Integer> successfulRowRecordList, boolean isBootstrap) {

        PreparedStatement psmt = null;
        int dbRowCount = 0;
        try {
            if (!isBootstrap) {
                psmt = conn.prepareStatement("select count(*) from location;");
                ResultSet rs = psmt.executeQuery();
                while(rs.next()){
                    dbRowCount = rs.getInt(1);                    
                }
                dbRowCount++;
            }
            conn.setAutoCommit(false);
            // Use the row number count as a primary key to speed up the insertion of data
            psmt = conn.prepareStatement("insert into location values (?, ?, ?, ?)");
            for (int i = 0; i < validRecordsList.size(); i++) {
                String[] validRecordArray = validRecordsList.get(i);
                int rowCount = successfulRowRecordList.get(i);
                psmt.setInt(1, dbRowCount + rowCount);
                psmt.setString(2, validRecordArray[0].trim());
                psmt.setString(3, validRecordArray[1].trim());
                psmt.setInt(4, Integer.parseInt(validRecordArray[2].trim()));
                psmt.addBatch();
            }

            try {
                psmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConnectionManager.close(null, psmt, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts location-lookup records into the database
     *
     * @param conn the connection which is used to process the request
     * @param validRecordsList the list of records to populate the database with
     */
    public static void updateLocationLookup(Connection conn, ArrayList<String[]> validRecordsList) {

        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {

            conn.setAutoCommit(false);
            String query = "insert into location_lookup values (?,?)";
            psmt = conn.prepareStatement(query);
            for (String[] validRecordArray : validRecordsList) {
                psmt.setInt(1, Integer.parseInt(validRecordArray[0].trim()));
                psmt.setString(2, validRecordArray[1].trim());
                psmt.addBatch();
            }
            psmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(null, psmt, null);

        }
    }

    /**
     * Empties the database
     *
     * @param conn that is used to execute the emptying of database
     */
    public static void wipeDatabase(Connection conn) {
        try {
            PreparedStatement psmt = null;
            try {
                String query = "drop database if exists `app`";
                psmt = conn.prepareStatement(query);
                psmt.executeUpdate();

                psmt = conn.prepareStatement("create schema `app`");
                psmt.executeUpdate();

                psmt = conn.prepareStatement("use `app`");
                psmt.executeUpdate();

                psmt = conn.prepareStatement("create table `app`.`demographics`"
                        + "( `mac_address` char(40) not null, `name` varchar(255) not null, `password` varchar(255) not null, "
                        + "`email` varchar(255) not null, `gender` char(1) not null, "
                        + "primary key (`mac_address`))");
                psmt.executeUpdate();

                psmt = conn.prepareStatement("create  table `app`.`location_lookup` ("
                        + "  `location_id` INT NOT NULL ,"
                        + "  `semantic_place` VARCHAR(255) NOT NULL ,"
                        + "  PRIMARY KEY (`location_id`))");
                psmt.executeUpdate();

                psmt = conn.prepareStatement("create table if not exists `app`.`location` ("
                        + "`rowNumber` INT NOT NULL,"
                        + "  `timestamp` TIMESTAMP NOT NULL ,"
                        + "  `mac_address` char(40) NOT NULL ,"
                        + "  `location_id` INT NULL ,"
                        + "  PRIMARY KEY (`rowNumber`) "
                        + "   ) ");
                psmt.executeUpdate();

                psmt = conn.prepareStatement("ALTER TABLE `app`.`location` ADD INDEX (`timestamp`, `mac_address`)");
                psmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConnectionManager.close(null, psmt, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all mac addresses found in the demographics table
     *
     * @param conn the connection that is processing this request
     *
     * @return a Hashmap storing all existing mac_addresses that exist in
     * demographics table
     */
    public static HashMap<String, String> retrieveAllMacAddressFromDemographics(Connection conn) {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        HashMap<String, String> returnMap = new HashMap<String, String>();
        try {
            // Retrieve all mac addresses inside demograhpics DB
            psmt = conn.prepareStatement("select mac_address from demographics");
            rs = psmt.executeQuery();
            while (rs.next()) {
                returnMap.put(rs.getString(1), "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return returnMap;
    }

    /**
     * Checks if a location is valid
     *
     * @param arr the Array which stores the line information to validate
     * @param conn the connection used to process this request
     * @param rowNumber the row number which this line belongs to
     * @param isBootstrap
     *
     * @return the result of whether this location exists already in the
     * database
     */
    public static int checkLocation(String[] arr, Connection conn, int rowNumber, boolean isBootstrap) {
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {
            psmt = conn.prepareStatement("SELECT * FROM location WHERE `timestamp` = ? AND mac_address = ?");
            psmt.setString(1, arr[0]);
            psmt.setString(2, arr[1]);

            rs = psmt.executeQuery();
            while (rs.next()) {
                int duplicatedRow = rs.getInt(1);
                // If this is a bootstrap functionality, I would want to update the current location to my newest row
                // If this is a update functionality, I will just return this row as duplicated and not update
                if (isBootstrap) {
                    psmt = conn.prepareStatement("UPDATE location SET location_id = ?, rowNumber = ? WHERE `timestamp` = ? AND mac_address = ?");
                    psmt.setInt(1, Integer.parseInt(arr[2]));
                    psmt.setInt(2, rowNumber);
                    psmt.setString(3, arr[0]);
                    psmt.setString(4, arr[1]);
                    psmt.executeUpdate();
                }

                return duplicatedRow;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return -1;
    }
}
