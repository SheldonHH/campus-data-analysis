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
import java.util.Set;
import model.Group;
import model.GroupDetectionResults;
import model.GroupMember;
import model.GroupMemberEmailMacaddressComparator;
import model.GroupSizeTimeSpentComparator;

/**
 * Retrieves data from database and generate Group Location Report Result
 *
 */
public class GroupDetectionManager {

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    /**
     * Retrieves the result of next places results
     *
     * @param startDate
     * @param endDate
     *
     * @return next places results in form of GroupDetectionResults object
     */
    public GroupDetectionResults retrieveGroupDetectionResults(Timestamp startDate, Timestamp endDate) {
        int totalUser = 0;
        ArrayList<Group> groupList = new ArrayList<Group>();
        // String query to pass into the database to retrieve time spent, location, macAdd1, macAdd2, email1, email2, startTime and endTime
        String query = "SELECT newTable.TimeSpentHere, newTable.LocationTrace, RetrieveUsersAbove12Mins.M1, RetrieveUsersAbove12Mins.M2, IFNULL(d1.email, '') as M1Email, IFNULL(d2.email, '') as M2Email, newTable.GreatestStartTime, newTable.LeastEndTime  FROM (	\n"
                + "		SELECT SUM(TIMESTAMPDIFF(SECOND, GreatestStartTime, LeastEndTime)) as TimeSpentHere, GreatestStartTime, LeastEndTime, M1, M2 FROM (\n"
                + "			SELECT GREATEST(MainTimestamp1, SecondaryTimeStamp1) as GreatestStartTime, LEAST(MainTimestamp2, SecondaryTimestamp2) as LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "				SELECT MainTimestamp1, MainTimestamp2, t1.mac_address AS M1, SecondaryTimestamp1, SecondaryTimestamp2, t2.mac_address AS M2, LocationTrace FROM (\n"
                + "					#Select the minimum time difference 	\n"
                + "					(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), \n"
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
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						#retrieve row count\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
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
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t1\n"
                + "					INNER JOIN \n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as SecondaryTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS SecondaryTimestamp2, \n"
                + "						l1.mac_address, l1.location_id FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where\n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
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
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
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
                + "	HAVING TimeSpentHere >= 720\n"
                + "	ORDER BY TimeSpentHere DESC) as RetrieveUsersAbove12Mins\n"
                + "INNER JOIN \n"
                + "	(SELECT SUM(TIMESTAMPDIFF(SECOND, GreatestStartTime, LeastEndTime)) as TimeSpentHere, GreatestStartTime, LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "			SELECT GREATEST(MainTimestamp1, SecondaryTimeStamp1) as GreatestStartTime, LEAST(MainTimestamp2, SecondaryTimestamp2) as LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "				SELECT MainTimestamp1, MainTimestamp2, t1.mac_address AS M1, SecondaryTimestamp1, SecondaryTimestamp2, t2.mac_address AS M2, LocationTrace FROM (\n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as MainTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS MainTimestamp2, \n"
                + "						l1.mac_address, l1.location_id as LocationTrace FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where \n"
                + "						`timestamp` > ? AND `timestamp` <= ? \n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
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
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
                + "						GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "						ORDER BY l1.`timestamp`) as t1\n"
                + "					INNER JOIN \n"
                + "						(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "						IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "						l1.`timestamp` as SecondaryTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "						IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS SecondaryTimestamp2, \n"
                + "						l1.mac_address, l1.location_id FROM location l1 INNER JOIN location l2 \n"
                + "						INNER JOIN \n"
                + "						(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where \n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
                + "						GROUP BY mac_address) AS MaxTime\n"
                + "						ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "						INNER JOIN \n"
                + "						(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE \n"
                + "						`timestamp` > ? AND `timestamp` <= ?\n"
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
                + "						AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "						AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
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
                + "WHERE RetrieveUsersAbove12Mins.M1 = newTable.M1 AND RetrieveUsersAbove12Mins.M2 = newTable.M2\n"
                + "ORDER BY M1, M2, newTable.GreatestStartTime, newTable.LeastEndTime";
        //String query to pass into database to get the number of all the users in the building        
        String queryTotalUser = "select count(distinct mac_address) from location where `timestamp` > ? AND `timestamp` <= ?";
        
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
            // store emails for each user that is found
            HashMap<String, String> userList = new HashMap<String, String>(); 

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

                String locationID = rs.getString(2);
                Timestamp startTime = rs.getTimestamp(7);
                Timestamp endTime = rs.getTimestamp(8);
                if (locationMap.get(locationID) == null) {
                    ArrayList<Timestamp> locationTimestamps = new ArrayList<Timestamp>();
                    locationTimestamps.add(startTime);
                    locationTimestamps.add(endTime);
                    locationMap.put(locationID, locationTimestamps);
                } else {
                    ArrayList<Timestamp> currentTimestamps = locationMap.get(locationID);
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
                        locationMap.put(locationID, currentTimestamps);
                    }
                }
            }


            // the following block checks each pair and sorts them into groups
            Iterator<String> iterM1 = pairList.keySet().iterator();
            // Iterate through the outer person's companions 
            while (iterM1.hasNext()) {
                String firstUser = iterM1.next();
                HashMap<String, LinkedHashMap<String, ArrayList<Timestamp>>> innerHashMap = pairList.get(firstUser);
                Iterator<String> iterM2 = innerHashMap.keySet().iterator();
                // Iterate through the second person's companions 
                while (iterM2.hasNext()) {
                    // retrieve the second person
                    String companion = iterM2.next();
                    HashMap<String, ArrayList<Timestamp>> timestampsPerLocation = pairList.get(firstUser).get(companion);
                    Iterator<String> iteratorOfLocations = timestampsPerLocation.keySet().iterator();

                    for (Group group : groupList) {
                        // clone an arraylist of the members of this group as it is now
                        ArrayList<String> currentGroupMembers = (ArrayList<String>) group.getGroupMembers().clone();
                        // add companion as a potential member to this temp group
                        currentGroupMembers.add(companion); 
                        // check if potential group actually exists
                        if (isAGroup(currentGroupMembers, pairList)) { 
                            // Once discover a new companion, reset all the locations
                            hasValidTimeSpent(group, companion, pairList);
                        }
                    }

                    Group group = new Group(firstUser);
                    if (hasValidTimeSpent(group, companion, pairList)) {
                        // if wasn't found as part of any group, then this pair is a new 2 user group
                        groupList.add(group); 
                    }
                }
            }

            // the following block removes groups that are a subset of larger groups
            Iterator<Group> iterSubSetChecker = groupList.iterator(); // iterates through all the groups
            while (iterSubSetChecker.hasNext()) {
                ArrayList<String> currentGroupMembers = iterSubSetChecker.next().getGroupMembers();
                // gets members of the currently checked group
                boolean foundItself = false;
                for (Group checkedGroup : groupList) {
                    // iterate through all existing groups
                    ArrayList<String> checkedGroupMembers = checkedGroup.getGroupMembers();
                    // get members of group that the ouer loop group is to be compared to
                    if (checkedGroupMembers.containsAll(currentGroupMembers)) {
                        if (!foundItself && currentGroupMembers.size() == checkedGroupMembers.size()) {
                            foundItself = true;
                        } else {
                            // remove the group only if it is smaller and is a subset of the group compared against
                            iterSubSetChecker.remove();
                            break; // breaks out of for each loop if group is removed
                        }
                    }
                }
            }

            // the following block sets the common locations and timespent for all groups
            // the following code blocks sorts results as indicated by project requirements
            // this block sorts the groups according to size, then total time spent
            Collections.sort(groupList, new GroupSizeTimeSpentComparator());
            for (Group group : groupList) {
                ArrayList<String> memberList = group.getGroupMembers();
                ArrayList<GroupMember> groupMembers = new ArrayList<GroupMember>();
                for (String member : memberList) {
                    groupMembers.add(new GroupMember(member, userList.get(member)));
                }
                Collections.sort(groupMembers, new GroupMemberEmailMacaddressComparator());
                memberList = new ArrayList<String>();
                for (GroupMember member : groupMembers) {
                    memberList.add(member.getMac_Address());
                }
                group.setGroupMembers(memberList);
            }
            
            ps = conn.prepareStatement(queryTotalUser);

            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                totalUser = rs.getInt(1);
            }
            
            // collect all required data into results object
            GroupDetectionResults results = new GroupDetectionResults(groupList, userList, totalUser);
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return null;
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
        Collections.sort(groupList, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < groupList.size(); i++) {
            for (int j = 0; j < groupList.size(); j++) {
                if (i != j) {
                    // If the first person is lesser than the second person alphabetically
                    if (groupList.get(i).compareTo(groupList.get(j)) < 0) {
                        // Retrieve the j's HashMap to check if i is a companion of j

                        Object isBFirstCompanionOfAPair = outerHashMap.get(groupList.get(j));
                        //if B is not the companion of A, return false
                        if (isBFirstCompanionOfAPair == null) {
                            return false;
                        } else {

                            Object isBCompanionOfC = outerHashMap.get(groupList.get(j)).get(groupList.get(i));
                            Object isCCompanionOfB = null;
                           
                            if (outerHashMap.get(groupList.get(i)) != null) {
                                isCCompanionOfB = outerHashMap.get(groupList.get(i)).get(groupList.get(j));
                            }
                            //if C is not the companion of B, and B is not companion of C, return false
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
        ArrayList<String> currentGroupMembers = (ArrayList<String>) group.getGroupMembers().clone();
        currentGroupMembers.add(companion);

        ArrayList<LinkedHashMap<String, ArrayList<Timestamp>>> currentMembersLocationMaps = new ArrayList<LinkedHashMap<String, ArrayList<Timestamp>>>();

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

        HashMap<String, ArrayList<Timestamp>> firstMap = currentMembersLocationMaps.get(0); // one of the maps is obtained
        Iterator<String> firstMapIterator = firstMap.keySet().iterator();
        // the locationIds(keySet) of the retrieved map is obtained and stored
        ArrayList<String> commonLocations = new ArrayList<String>();
        while (firstMapIterator.hasNext()) {
            commonLocations.add(firstMapIterator.next());
        }

        // commonLocations is the eventual keySet of the final location map
        for (int i = 1; i < currentMembersLocationMaps.size(); i++) { // iterate through the REMAINING maps
            Set currentKeys = currentMembersLocationMaps.get(i).keySet(); // get the keySet of the map to be compared to
            commonLocations.retainAll(currentKeys); // remove from commonLocations any key (locationID) 
            // that does not exist in the keySet compared against
        }

        int totalTimeSpent = 0;
        Iterator<String> iterLocations = commonLocations.iterator(); // iterate through the new keySet, this should now only contain common locationIDs to all pairs
        LinkedHashMap<String, Integer> locations = new LinkedHashMap<String, Integer>();
        while (iterLocations.hasNext()) { // for each common locationID iterate...
            String locationID = iterLocations.next();
            int timeSpentAtOneLocation = Integer.MAX_VALUE;
            // Retrieve one set of timestamps and compare with the rest
            ArrayList<Timestamp> groupOverlappingTimestamps = new ArrayList<Timestamp>();
            ArrayList<Timestamp> mainTimestampLocations = currentMembersLocationMaps.get(0).get(locationID); // find the timespent for that locationID for the current location map
            // Retrieve another set of timestamps that is not the same.
            if (mainTimestampLocations != null) {
                for (int l = 0; l < mainTimestampLocations.size(); l += 2) {
                    // Do not compare the same locations
                    // Compare with each individual member
                    int minimumTimespentForThisWindow = Integer.MAX_VALUE;
                    Timestamp maxStartTime = null;
                    Timestamp minEndTime = null;

                    for (int k = 1; k < currentMembersLocationMaps.size(); k++) {
                        ArrayList<Timestamp> timestampLocationsToCompare = currentMembersLocationMaps.get(k).get(locationID);
                        maxStartTime = mainTimestampLocations.get(l);
                        minEndTime = mainTimestampLocations.get(l + 1);
                        // Retrieve 1 member's location and compare with the rest

                        for (int m = 0; m < timestampLocationsToCompare.size(); m += 2) {
                            Timestamp timestampStartToCompare = timestampLocationsToCompare.get(m);
                            Timestamp timestampEndToCompare = timestampLocationsToCompare.get(m + 1);
                            // If either timestarts and timeends are both not larger than the ones to compare with
                            if (!((timestampStartToCompare.after(maxStartTime) && timestampEndToCompare.after(minEndTime))
                                    || (maxStartTime.after(timestampStartToCompare) && minEndTime.after(timestampEndToCompare)))) {
                                // if there is an even later start time, use that
                                if (maxStartTime.before(timestampStartToCompare)) {
                                    maxStartTime = timestampStartToCompare;
                                }
                                // if there is an even earlier end time, use that
                                if (minEndTime.after(timestampEndToCompare)) {
                                    minEndTime = timestampEndToCompare;
                                }
                            }

                        }
                        int pairTimespent = (int) ((minEndTime.getTime() - maxStartTime.getTime()) / 1000);
                        //get the minimum time spent among all the pairs in the group
                        if (pairTimespent < minimumTimespentForThisWindow) {
                            minimumTimespentForThisWindow = pairTimespent;
                        }
                    }
                    //get time spent at each location
                    if (minimumTimespentForThisWindow < timeSpentAtOneLocation) {
                        timeSpentAtOneLocation = minimumTimespentForThisWindow;
                    } else {
                        timeSpentAtOneLocation += minimumTimespentForThisWindow;
                    }
                }
                locations.put(locationID, timeSpentAtOneLocation);
            }
            totalTimeSpent += timeSpentAtOneLocation;

            // insert common locationID with the common timespent
        }
        if (totalTimeSpent >= 720) {
            group.addMember(companion);
            group.setLocations(locations);
            return true;
        }// set this location map in the group

        return false;
    }
}