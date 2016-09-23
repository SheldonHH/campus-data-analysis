package dataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import model.Group;

/**
 * Retrieves data from database and generate Group Location Report Result
 *
 */
public class GroupReportManager {

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    /**
     * Retrieves the result of next places results
     *
     * @param previousWindowGroups
     * @param startDate
     * @param endDate
     *
     * @return next places results in form of GroupDetectionResults object
     */
    public HashMap<String, Integer> retrieveNextPlacesResults(ArrayList<Group> previousWindowGroups, Timestamp startDate, Timestamp endDate) {
        int totalUser = 0;

        ArrayList<Group> groupList = new ArrayList<Group>();
        // String query to pass into the database to retrieve time spent, location, macAdd1, macAdd2, email1, email2, startTime and endTime
        String query = "SELECT newTable.TimeSpentHere, loc_lookup.semantic_place, RetrieveUsersAbove12Mins.M1, RetrieveUsersAbove12Mins.M2, IFNULL(d1.email, '') as M1Email, IFNULL(d2.email, '') as M2Email, newTable.GreatestStartTime, newTable.LeastEndTime  FROM (	\n"
                + "		SELECT SUM(TIMESTAMPDIFF(SECOND, GreatestStartTime, LeastEndTime)) as TimeSpentHere, GreatestStartTime, LeastEndTime, M1, M2 FROM (\n"
                + "			SELECT GREATEST(MainTimestamp1, SecondaryTimeStamp1) as GreatestStartTime, LEAST(MainTimestamp2, SecondaryTimestamp2) as LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "				SELECT MainTimestamp1, MainTimestamp2, t1.mac_address AS M1, SecondaryTimestamp1, SecondaryTimestamp2, t2.mac_address AS M2, LocationTrace FROM (\n"
                + "					#Select the minimum time difference 	\n"
                + "					(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime) ), \n"
                + "							IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as MainTimestamp1, \n"
                + "						#timestamp 2\n"
                + "						IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "							IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), \n"
                + "							IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS MainTimestamp2, \n"
                + "						l1.mac_address, l1.location_id as LocationTrace FROM location l1 INNER JOIN location l2 \n"
                + "						#retrieve maximum timespent\n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						#retrieve row count\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS CountRows\n"
                + "						ON CountRows.mac_address = MaxTime.mac_address\n"
                + "						WHERE \n"
                + "						CASE rowCount \n"
                + "						WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "						   ELSE CASE l1.`timestamp`\n"
                + "							  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "							  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "						   END\n"
                + "						END\n"
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` < ? \n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` < ? \n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t1\n"
                + "					INNER JOIN \n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as SecondaryTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS SecondaryTimestamp2, \n"
                + "						l1.mac_address, l1.location_id FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where\n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS CountRows\n"
                + "						ON CountRows.mac_address = MaxTime.mac_address\n"
                + "						WHERE \n"
                + "						CASE rowCount \n"
                + "						WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "						   ELSE CASE l1.`timestamp`\n"
                + "							  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "							  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "						   END\n"
                + "						END\n"
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` < ? \n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` < ? \n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t2\n"
                + "					ON LocationTrace = t2.location_id\n"
                + "				AND t1.mac_address != t2.mac_address\n"
                + "				AND MainTimestamp2 > SecondaryTimeStamp1\n"
                + "				AND MainTimestamp1 < SecondaryTimeStamp2)\n"
                + "			) as t3\n"
                + "		GROUP BY M1,M2, GreatestStartTime) aS T4\n"
                + "	WHERE M1 > M2 \n"
                + "	GROUP BY M1, M2\n"
                + "	HAVING TimeSpentHere >= 300\n"
                + "	ORDER BY TimeSpentHere DESC) as RetrieveUsersAbove12Mins\n"
                + "INNER JOIN \n"
                + "	(SELECT SUM(TIMESTAMPDIFF(SECOND, GreatestStartTime, LeastEndTime)) as TimeSpentHere, GreatestStartTime, LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "			SELECT GREATEST(MainTimestamp1, SecondaryTimeStamp1) as GreatestStartTime, LEAST(MainTimestamp2, SecondaryTimestamp2) as LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "				SELECT MainTimestamp1, MainTimestamp2, t1.mac_address AS M1, SecondaryTimestamp1, SecondaryTimestamp2, t2.mac_address AS M2, LocationTrace FROM (\n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as MainTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS MainTimestamp2, \n"
                + "						l1.mac_address, l1.location_id as LocationTrace FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where \n"
                + "						`timestamp` > ? AND `timestamp` < ?  \n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS CountRows\n"
                + "						ON CountRows.mac_address = MaxTime.mac_address\n"
                + "						WHERE \n"
                + "						CASE rowCount \n"
                + "						WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "						   ELSE CASE l1.`timestamp`\n"
                + "							  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "							  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "						   END\n"
                + "						END\n"
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` < ? \n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` < ? \n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t1\n"
                + "					INNER JOIN \n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as SecondaryTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS SecondaryTimestamp2, \n"
                + "						l1.mac_address, l1.location_id FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` < ? \n"
                + "						GROUP BY mac_address) AS CountRows\n"
                + "						ON CountRows.mac_address = MaxTime.mac_address\n"
                + "						WHERE \n"
                + "						CASE rowCount \n"
                + "						WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "						   ELSE CASE l1.`timestamp`\n"
                + "							  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "							  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "						   END\n"
                + "						END\n"
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` < ? \n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` < ? \n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t2\n"
                + "					ON LocationTrace = t2.location_id\n"
                + "				AND t1.mac_address != t2.mac_address\n"
                + "				AND MainTimestamp2 > SecondaryTimeStamp1\n"
                + "				AND MainTimestamp1 < SecondaryTimeStamp2)\n"
                + "			) as t3\n"
                + "		GROUP BY M1,M2, GreatestStartTime) aS T4\n"
                + "	WHERE M1 > M2\n"
                + "	GROUP BY M1, M2, LocationTrace, GreatestStartTime\n"
                + "	ORDER BY M1,M2 DESC) as newTable\n"
                + "LEFT OUTER JOIN demographics d1\n"
                + "ON RetrieveUsersAbove12Mins.M1 = d1.mac_address \n"
                + "LEFT OUTER JOIN demographics d2 \n"
                + "ON RetrieveUsersAbove12Mins.M2 = d2.mac_address\n"
                + "INNER JOIN location_lookup loc_lookup \n"
                + "ON newTable.LocationTrace= loc_lookup.location_id\n"
                + "WHERE RetrieveUsersAbove12Mins.M1 = newTable.M1 AND RetrieveUsersAbove12Mins.M2 = newTable.M2\n"
                + "ORDER BY M1, M2, newTable.GreatestStartTime, newTable.LeastEndTime";
        try {

            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);

            ps.setTimestamp(1, endDate);
            ps.setTimestamp(2, endDate);
            ps.setTimestamp(3, endDate);
            ps.setTimestamp(4, endDate);
            ps.setTimestamp(5, startDate);
            ps.setTimestamp(6, endDate);
            ps.setTimestamp(7, startDate);
            ps.setTimestamp(8, endDate);
            ps.setTimestamp(9, startDate);
            ps.setTimestamp(10, endDate);
            ps.setTimestamp(11, startDate);
            ps.setTimestamp(12, endDate);
            ps.setTimestamp(13, endDate);
            ps.setTimestamp(14, endDate);
            ps.setTimestamp(15, endDate);
            ps.setTimestamp(16, endDate);
            ps.setTimestamp(17, startDate);
            ps.setTimestamp(18, endDate);
            ps.setTimestamp(19, startDate);
            ps.setTimestamp(20, endDate);
            ps.setTimestamp(21, startDate);
            ps.setTimestamp(22, endDate);
            ps.setTimestamp(23, startDate);
            ps.setTimestamp(24, endDate);
            ps.setTimestamp(25, endDate);
            ps.setTimestamp(26, endDate);
            ps.setTimestamp(27, endDate);
            ps.setTimestamp(28, endDate);
            ps.setTimestamp(29, startDate);
            ps.setTimestamp(30, endDate);
            ps.setTimestamp(31, startDate);
            ps.setTimestamp(32, endDate);
            ps.setTimestamp(33, startDate);
            ps.setTimestamp(34, endDate);
            ps.setTimestamp(35, startDate);
            ps.setTimestamp(36, endDate);
            ps.setTimestamp(37, endDate);
            ps.setTimestamp(38, endDate);
            ps.setTimestamp(39, endDate);
            ps.setTimestamp(40, endDate);
            ps.setTimestamp(41, startDate);
            ps.setTimestamp(42, endDate);
            ps.setTimestamp(43, startDate);
            ps.setTimestamp(44, endDate);
            ps.setTimestamp(45, startDate);
            ps.setTimestamp(46, endDate);
            ps.setTimestamp(47, startDate);
            ps.setTimestamp(48, endDate);

            rs = ps.executeQuery();
            String MacAddressM1 = null;
            String MacAddressM2 = null;
            HashMap<String, String> userList = new HashMap<String, String>(); // will store emails for each user that is found

            // this map stores all the pairs found by the query 
            // it consists of 3 layers of hashmaps
            // 1st layer key is the first user in the pair, 2nd layer key is second user, 3rd layer key is location id for that pair
            HashMap<String, HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>>> pairList = new HashMap<String, HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>>>();

            while (rs.next()) {
                // rs.getString(3) is the first mac_address from the table
                if (!rs.getString(3).equals(MacAddressM1)) { 
                    MacAddressM1 = rs.getString(3);
                    // check if M1's user already captured in userlist
                    if (userList.get(MacAddressM1) == null) { 
                        // rs.getString(5) gets email for M1
                        userList.put(MacAddressM1, rs.getString(5)); 
                    }
                }
                // rs.getString(4) is the second mac_address from the table
                MacAddressM2 = rs.getString(4); 
                // check if M2's user already captured in userlist
                if (userList.get(MacAddressM2) == null) { 
                    // rs.getString(6) gets email for M1
                    userList.put(MacAddressM2, rs.getString(6));
                }

                // this block finds out if cooresponding hashmaps exist for the current pair, 
                // and stores the location and timespent for the current pair
                HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>> innerHashMap = pairList.get(MacAddressM1);
                if (innerHashMap == null) {
                    innerHashMap = new HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>>();
                    pairList.put(MacAddressM1, innerHashMap);
                }
                LinkedHashMap<String, ArrayList<Timestamp>> locationMap = innerHashMap.get(MacAddressM2);
                if (locationMap == null) {
                    locationMap = new LinkedHashMap<String, ArrayList<Timestamp>>();
                    innerHashMap.put(MacAddressM2, locationMap);
                }

                String semantic_place = rs.getString(2);
                int timeSpent = rs.getInt(1);
                Timestamp startTime = rs.getTimestamp(7);
                Timestamp endTime = rs.getTimestamp(8);
                if (locationMap.get(semantic_place) == null) {
                    ArrayList<Timestamp> locationTimestamps = new ArrayList<Timestamp>();
                    locationTimestamps.add(startTime);
                    locationTimestamps.add(endTime);
                    locationMap.put(semantic_place, locationTimestamps);
                } else {
                    ArrayList<Timestamp> currentTimestamps = locationMap.get(semantic_place);
                    // Retrieved the last timestamp added into the arraylist
                    Timestamp latestAddedTimestamp = currentTimestamps.get(currentTimestamps.size() - 1);
                    // Check if the latest timestamp added (endTime) is the same as the new start time
                    // This is to check if they are consequtive.
                    if (latestAddedTimestamp.compareTo(startTime) == 0) {
                        // Replace the OLD end time with the new end time (depicting consequtiveness)

                        currentTimestamps.set(currentTimestamps.size() - 1, endTime);
                    } else {
                        // If the timings are not consequtive, add in new timings.
                        currentTimestamps.add(startTime);
                        currentTimestamps.add(endTime);
                        // Put the new timings back into the hashmap
                        locationMap.put(semantic_place, currentTimestamps);
                    }
                }
            }
            //END OF FIRST BLOCK WHICH ADDS ALL SQL UPDATES INTO pairList 

            totalUser = userList.size(); // gets total number of users

            // The following block checks each pair and sorts them into groups
            Iterator<String> iterM1 = pairList.keySet().iterator();

            // Iterate through the outer person's companions 
            while (iterM1.hasNext()) {

                String firstUser = iterM1.next();

                HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>> innerHashMap = pairList.get(firstUser);

                Iterator<String> iterM2 = innerHashMap.keySet().iterator();

                // Iterate through the second person's companions 
                while (iterM2.hasNext()) {

                    // Retrieve the second person
                    String companion = iterM2.next();
                    HashMap<String, ArrayList<Timestamp>> timestampsPerLocation = pairList.get(firstUser).get(companion);
                    Iterator<String> iteratorOfLocations = timestampsPerLocation.keySet().iterator();

                    for (Group group : groupList) {
                        ArrayList<String> currentGroupMembers = (ArrayList<String>) group.getGroupMembers().clone(); // clones an arraylist of the members of this group as it is now
                        currentGroupMembers.add(companion); // adds companion as a potential member to this temp group
                        if (isAGroup(currentGroupMembers, pairList)) { // checks if potential group actually exists
                            // Once i discover a new companion, I need to reset all the locations
                            hasValidTimeSpent(group, companion, pairList);
                        }
                    }
                    Group group = new Group(firstUser);

                    if (hasValidTimeSpent(group, companion, pairList)) {
                        groupList.add(group); // if wasn't found as part of any group, then this pair is a new 2 user group
                        for (Group groupy : groupList) {
                            ArrayList<String> groupMembers = groupy.getGroupMembers();
                            Collections.sort(groupMembers);
                        }
                    }
                }
            }
            for (Group group : groupList) {
                ArrayList<String> groupMembers = group.getGroupMembers();
                Collections.sort(groupMembers);
            }

            ArrayList<Group> sortedRelevantGroups = retrieveRelevantGroups(previousWindowGroups, groupList);
            HashMap<String, Integer> nextPlacesResults = new HashMap<String, Integer>();
            for (int i = 0; i < sortedRelevantGroups.size(); i++) {
                String semanticPlace = retrieveLatestFiveMinuteSemanticPlace(sortedRelevantGroups.get(i));
                if (nextPlacesResults.get(semanticPlace) == null) {
                    nextPlacesResults.put(semanticPlace, 1);
                } else {
                    Integer count = nextPlacesResults.get(semanticPlace);
                    nextPlacesResults.put(semanticPlace, ++count);
                }
            }
            return nextPlacesResults;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    // Takes in the nextWindow's groupList

    /**
     *
     * @param groupsFromPreviousWindow
     * @param currentWindowGroups
     * 
     * @return relevant groups in form of Group object 
     */
        public static ArrayList<Group> retrieveRelevantGroups(ArrayList<Group> groupsFromPreviousWindow, ArrayList<Group> currentWindowGroups) {
        ArrayList<Group> filteredGroup = new ArrayList<Group>();
        ArrayList<ArrayList<String>> tempFilteredMembers = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < groupsFromPreviousWindow.size(); i++) {
            Group currentGroup = groupsFromPreviousWindow.get(i);
            ArrayList<String> prevGroupMembers = currentGroup.getGroupMembers();
            Collections.sort(prevGroupMembers);

            for (int j = 0; j < currentWindowGroups.size(); j++) {
                Group nextWindowGroup = currentWindowGroups.get(j);
                ArrayList<String> nextGroupMembers = nextWindowGroup.getGroupMembers();
                Collections.sort(nextGroupMembers);
                if (nextGroupMembers.containsAll(prevGroupMembers)) {
                    filteredGroup.add(nextWindowGroup);
                    break;
                }
            }

        }
        return filteredGroup;
    }

    /**
     * Returns if it is a valid group or not
     *
     * @param groupList
     * @param outerHashMap
     *
     * @return a boolean value which indicates if this group is valid
     */
    public boolean isAGroup(ArrayList<String> groupList, HashMap<String, HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>>> outerHashMap) {
        //checks if each groupList 
        Collections.sort(groupList, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < groupList.size(); i++) {
            for (int j = 0; j < groupList.size(); j++) {
                if (i != j) {
                    // If the first person is lesser than the second person alphabetically
                    if (groupList.get(i).compareTo(groupList.get(j)) < 0) {
                        // Retrieve the j's HashMap to check if i is a companion of j
                        Object isBFirstCompanionOfAPair = outerHashMap.get(groupList.get(j));
                        if (isBFirstCompanionOfAPair == null) {
                            return false;
                        } else {
                            //if isBCompanionOfC isn't null, get location object of BA 
                            Object isBCompanionOfC = outerHashMap.get(groupList.get(j)).get(groupList.get(i));
                            Object isCCompanionOfB = null;
                            if (outerHashMap.get(groupList.get(i)) != null) {
                                isCCompanionOfB = outerHashMap.get(groupList.get(i)).get(groupList.get(j));
                            }
                            if (isBCompanionOfC == null && isCCompanionOfB == null) {
                                return false;
                            }
                        }
                    } else {
                        Object isBFirstCompanionOfAPair = outerHashMap.get(groupList.get(i));
                        if (isBFirstCompanionOfAPair == null) {
                            return false;
                        } else {

                            Object isBCompanionOfC = outerHashMap.get(groupList.get(i)).get(groupList.get(j));
                            Object isCCompanionOfB = null;
                            if (outerHashMap.get(groupList.get(j)) != null) {
                                isCCompanionOfB = outerHashMap.get(groupList.get(j)).get(groupList.get(i));
                            }
                            if (isBCompanionOfC == null && isCCompanionOfB == null) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns if it is a valid time spent
     *
     * @param group
     * @param companion
     * @param outerHashMap
     *
     * @return boolean value which indicates if this is a valid time spent
     */
    public boolean hasValidTimeSpent(Group group, String companion, HashMap<String, HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>>> outerHashMap) {

        //Retrieves the current group members of the group 
        ArrayList<String> currentGroupMembers = (ArrayList<String>) group.getGroupMembers().clone();
        currentGroupMembers.add(companion);
        boolean isMemberAdded = false;
        ArrayList<LinkedHashMap<String, ArrayList<Timestamp>>> currentMembersLocationMaps = new ArrayList<LinkedHashMap<String, ArrayList<Timestamp>>>();
        LinkedHashMap<String, ArrayList<Timestamp>> finalLocations = new LinkedHashMap<String, ArrayList<Timestamp>>(); // to be the map of common locations and timespent

        // Sort all members alphabetically first
        Collections.sort(currentGroupMembers);

        // Add all locations between the pairs into a same location list
        for (int i = 0; i < currentGroupMembers.size(); i++) {
            for (int j = 0; j < currentGroupMembers.size(); j++) {
                if (i != j) {
                    // If the first person is lesser than the second person alphabetically
                    if (currentGroupMembers.get(i).compareTo(currentGroupMembers.get(j)) < 0) {
                        String firstMember = currentGroupMembers.get(i);
                        String secondMember = currentGroupMembers.get(j);
                        // Retrieve the locations that this pair has been to and add 
                        LinkedHashMap<String, ArrayList<Timestamp>> retrievePairLocation = outerHashMap.get(secondMember).get(firstMember);
                        currentMembersLocationMaps.add(retrievePairLocation);
                    } else {
                        String firstMember = currentGroupMembers.get(j);
                        String secondMember = currentGroupMembers.get(i);
                        // Retrieve the locations that this pair has been to and add 
                        LinkedHashMap<String, ArrayList<Timestamp>> retrievePairLocation = outerHashMap.get(secondMember).get(firstMember);
                        currentMembersLocationMaps.add(retrievePairLocation);
                    }
                }
            }
        }

        // Obtain the list of locations for the first pair of users -> Just the first pair's locations is enough to set as common loc list as all other pairs must at least overlap with 
        //the first pair at one location for >=5min to qualify as having spent >5min together with everyone else.        
        HashMap<String, ArrayList<Timestamp>> firstMap = currentMembersLocationMaps.get(0); // one of the maps is obtained
        Iterator<String> firstMapIterator = firstMap.keySet().iterator();
        // the locationIds(keySet) of the retrieved map is obtained and stored
        ArrayList<String> commonLocations = new ArrayList<String>();
        
        while (firstMapIterator.hasNext()) {
            commonLocations.add(firstMapIterator.next());
        }

        // commonLocations is the eventual keySet of the final location map
        // What this step does is to compare the list of locations the first pair has been against all the other pairs and only retain in commonLocations Arraylist
        // the similar locations where all pairs have been. 
        for (int i = 1; i < currentMembersLocationMaps.size(); i++) { // iterate through the REMAINING maps
            Set currentKeys = currentMembersLocationMaps.get(i).keySet(); // get the keySet of the map to be compared to
            commonLocations.retainAll(currentKeys); // remove from commonLocations any key (semantic_place) 
            // that does not exist in the keySet compared against
        }

        int totalTimeSpent = 0;
        // Iterate through the new keySet, this should now only contain common semantic_places to all pairs
        Iterator<String> iterLocations = commonLocations.iterator(); 
        
        // THIS while loop goes through each semantic_place and compares the list of timstamp updates for each pair at that particular semantic_place
        // finding any overlaps between each pair's timestamp in the process. 
        //STORES THE OVERLAPPING TIMESTAMPS ACROSS DIFFERENT SEMANTIC PLACES -> To be looped and compared against
        while (iterLocations.hasNext()) { // for each common semantic_place iterate...

            String semantic_place = iterLocations.next();
            // Tracks if a valid 5 min overlap exist between all the group members and if count is == 0 means that companion has not been added to the group yet. Else, no need to add companion to group
            int count = 0;
            int timeSpentAtOneLocation = Integer.MAX_VALUE;
            // Retrieve one set of timestamps and compare with the rest
            ArrayList<Timestamp> mainTimestampLocations = currentMembersLocationMaps.get(0).get(semantic_place); // find the timespent for that semantic_place for the current location map
            ArrayList<Timestamp> groupOverlappingTimestamps = new ArrayList<Timestamp>();

            Timestamp groupMaxStartTime = null;
            Timestamp groupMinEndTime = null;

            // Retrieve another set of timestamps that is not the same.
            if (mainTimestampLocations != null) {
                for (int l = 0; l < mainTimestampLocations.size(); l += 2) {
                    groupMaxStartTime = mainTimestampLocations.get(l);
                    groupMinEndTime = mainTimestampLocations.get(l + 1);

                    for (int k = 1; k < currentMembersLocationMaps.size(); k++) {
                        // Do not compare the same locations
                        ArrayList<Timestamp> timestampLocationsToCompare = currentMembersLocationMaps.get(k).get(semantic_place);
                        int minimumTimespentForThisWindow = Integer.MAX_VALUE;
                        // Retrieves all timestamps for a particular semantic place
                        for (int m = 0; m < timestampLocationsToCompare.size(); m += 2) {
                            Timestamp companionStartTimeToCompare = timestampLocationsToCompare.get(m);
                            Timestamp companionEndTimeToCompare = timestampLocationsToCompare.get(m + 1);
                            // If either timestarts and timeends are both not larger than the ones to compare with
                            if (!((companionStartTimeToCompare.after(groupMaxStartTime) && companionEndTimeToCompare.after(groupMinEndTime))
                                    || (groupMaxStartTime.after(companionStartTimeToCompare) && groupMinEndTime.after(companionEndTimeToCompare)))) {
                                // if there is an even later start time, use that
                                if (groupMaxStartTime.before(companionStartTimeToCompare)) {
                                    groupMaxStartTime = companionStartTimeToCompare;
                                }
                                // if there is an even earlier end time, use that
                                if (groupMinEndTime.after(companionEndTimeToCompare)) {
                                    groupMinEndTime = companionEndTimeToCompare;
                                }
                            }

                        }
                    }

                    int groupTimespent = (int) ((groupMinEndTime.getTime() - groupMaxStartTime.getTime()) / 1000);
                    if (groupTimespent >= 300) {
                        groupOverlappingTimestamps.add(groupMaxStartTime);
                        groupOverlappingTimestamps.add(groupMinEndTime);
                    }
                }
            }
            if (!groupOverlappingTimestamps.isEmpty()) {
                if (!isMemberAdded) {
                    group.addMember(companion);
                    isMemberAdded = true;
                }
                finalLocations.put(semantic_place, groupOverlappingTimestamps);
            }
        }

        if (isMemberAdded) {
            group.setNextPlacesLocations(finalLocations);
            return true;
        }
        return false;
    }

    /**
     *
     * @param group
     * 
     * @return a String of the latest semantic place that the group has been for more than or equal 5 minutes
     */
    public String retrieveLatestFiveMinuteSemanticPlace(Group group) {
        HashMap<String, ArrayList<Timestamp>> finalLocations = group.getNextPlacesLocations();
        String firstLocation = (String) finalLocations.keySet().toArray()[0];
        ArrayList<Timestamp> firstLocationTimestamps = finalLocations.get(firstLocation);
        Timestamp startTime = firstLocationTimestamps.get(firstLocationTimestamps.size() - 2);
        Timestamp endTime = firstLocationTimestamps.get(firstLocationTimestamps.size() - 1);
        String finalLocation = firstLocation;
        Iterator<Map.Entry<String, ArrayList<Timestamp>>> finalLocationsIterator = finalLocations.entrySet().iterator();
        while (finalLocationsIterator.hasNext()) {
            Map.Entry<String, ArrayList<Timestamp>> entry = finalLocationsIterator.next();
            String location = entry.getKey();
            ArrayList<Timestamp> timestampsInCurrentLocation = entry.getValue();
            Timestamp currentLocationStartTime = timestampsInCurrentLocation.get(timestampsInCurrentLocation.size() - 2);
            Timestamp currentLocationEndTime = timestampsInCurrentLocation.get(timestampsInCurrentLocation.size() - 1);
            if (currentLocationEndTime.after(endTime)) {
                startTime = currentLocationStartTime;
                endTime = currentLocationEndTime;
                finalLocation = location;
            }
        }
        return finalLocation;
    }

}
