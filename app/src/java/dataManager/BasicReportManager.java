package dataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import model.BasicBreakdownObject;
import model.CompanionsResult;
import model.NextPlacesResults;
import model.PopularPlacesResult;

/**
 * Retrieves data from database and generates required results for each basic
 * location report.
 *
 *
 */
public class BasicReportManager {

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    /**
     * Retrieves the results of the breakdown functionality when one order is
     * selected
     *
     * @param order the element chosen by the user
     * @param startDate the start of the 15 min query window
     * @param endDate the end of the 15 min query window
     *
     * @return breakdown result in form of HashMap<Integer,
     * BasicBreakdownObject>
     */
    public HashMap<Integer, BasicBreakdownObject> retrieveBreakdownReport(String order, Timestamp startDate, Timestamp endDate) {
        
        ArrayList<BasicBreakdownObject> breakdownArray = new ArrayList<BasicBreakdownObject>();
        
        HashMap<Integer, BasicBreakdownObject> countAndBreakdownObject = new HashMap<Integer, BasicBreakdownObject>();
        
        // This hash map stores the 
        LinkedHashMap<String, Integer> results = new LinkedHashMap<String, Integer>();

        //Query used to retrieve email and gender. Email is selected because the year and school can be retrieved from the email attribute.
        String query = "select email,gender from demographics\n"
                + "inner join location\n"
                + "where demographics.mac_address = location.mac_address\n"
                + "and timestamp\n"
                + "between ?\n"
                + "and ?\n"
                + "group by demographics.mac_address";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            rs = ps.executeQuery();

            int totalUsers = 0;

            //if order chosen is gender 
            if (order.equals("gender")) {
                // inserts gender elements into hashmap
                results.put("M", 0);
                results.put("F", 0);
                while (rs.next()) {
                     // get the gender element
                    String entry = rs.getString(2);
                     // retrieve curent count belonging to element of the current entry
                    int value = results.get(entry);
                    value++;
                    totalUsers++;
                    results.put(entry, value);
                }
            } else if (order.equals("year")) {
                // inserts gender elements into hashmap
                results.put("2010", 0);
                results.put("2011", 0);
                results.put("2012", 0);
                results.put("2013", 0);
                results.put("2014", 0);
                while (rs.next()) {
                    // get the year from the email of the current entry
                    String[] entry = rs.getString(1).split("@");
                    String emailID = entry[0];
                    String year = emailID.substring(emailID.length() - 4, emailID.length());
                    int value = results.get(year); // retrieve curent count belonging to element of the current entry
                    value++;
                    totalUsers++;
                    results.put(year, value);
                }
            } else {
                // inserts school elements into hashmap
                results.put("accountancy", totalUsers);
                results.put("business", totalUsers);
                results.put("economics", totalUsers);
                results.put("law", totalUsers);
                results.put("sis", totalUsers);
                results.put("socsc", totalUsers);
                while (rs.next()) {
                    // get the school from the email of the current entry
                    String[] entry = rs.getString(1).split("@");
                    String[] domain = entry[1].split("\\.");
                    String school = domain[0];
                    int value = results.get(school); // retrieve curent count belonging to element of the current entry
                    value++;
                    totalUsers++;
                    results.put(school, value);

                }
            }
            for (String key : results.keySet()) {
                BasicBreakdownObject outerObject = new BasicBreakdownObject(order, key, results.get(key));
                breakdownArray.add(outerObject);
            }
            countAndBreakdownObject.put(totalUsers, new BasicBreakdownObject(breakdownArray));

            return countAndBreakdownObject;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;

    }

    /**
     * Retrieves the results of the breakdown when two elements are passed in
     *
     * @param order the element chosen by the user
     * @param element2 the element chosen by the user
     * @param startDate the start of the 15 min query window
     * @param endDate the end of the 15 min query window
     *
     * @return breakdown result in form of HashMap<Integer,
     * BasicBreakdownObject>
     */
    public HashMap<Integer, BasicBreakdownObject> retrieveBreakdownReport(String order, String element2, Timestamp startDate, Timestamp endDate) {

        ArrayList<BasicBreakdownObject> breakdownArray = new ArrayList<BasicBreakdownObject>();
        HashMap<Integer, BasicBreakdownObject> countAndBreakdownObject = new HashMap<Integer, BasicBreakdownObject>();
        LinkedHashMap<String, LinkedHashMap<String, Integer>> results = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();;

        // String query to pass into the database to retrieve email and gender
        String query = "select email, gender from demographics\n"
                + "inner join location\n"
                + "where demographics.mac_address = location.mac_address\n"
                + "and timestamp\n"
                + "between ?\n"
                + "and ?\n"
                + "group by demographics.mac_address";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            rs = ps.executeQuery();

            int totalUsers = 0;
            if (order.equals("gender")) {
                results.put("M", new LinkedHashMap<String, Integer>());
                results.put("F", new LinkedHashMap<String, Integer>());
            } else if (order.equals("year")) {
                results.put("2010", new LinkedHashMap<String, Integer>());
                results.put("2011", new LinkedHashMap<String, Integer>());
                results.put("2012", new LinkedHashMap<String, Integer>());
                results.put("2013", new LinkedHashMap<String, Integer>());
                results.put("2014", new LinkedHashMap<String, Integer>());
            } else {
                results.put("accountancy", new LinkedHashMap<String, Integer>());
                results.put("business", new LinkedHashMap<String, Integer>());
                results.put("economics", new LinkedHashMap<String, Integer>());
                results.put("law", new LinkedHashMap<String, Integer>());
                results.put("sis", new LinkedHashMap<String, Integer>());
                results.put("socsc", new LinkedHashMap<String, Integer>());
            }

            if (element2.equals("gender")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, Integer> subResults = results.get(key);
                    subResults.put("M", 0);
                    subResults.put("F", 0);
                }
            } else if (element2.equals("year")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, Integer> subResults = results.get(key);
                    subResults.put("2010", 0);
                    subResults.put("2011", 0);
                    subResults.put("2012", 0);
                    subResults.put("2013", 0);
                    subResults.put("2014", 0);
                }
            } else {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, Integer> subResults = results.get(key);
                    subResults.put("accountancy", 0);
                    subResults.put("business", 0);
                    subResults.put("economics", 0);
                    subResults.put("law", 0);
                    subResults.put("sis", 0);
                    subResults.put("socsc", 0);
                }
            }

            while (rs.next()) {
                LinkedHashMap<String, Integer> subResults = null;
                String entry = null;
                if (order.equals("gender")) {
                    entry = rs.getString(2);
                } else if (order.equals("year")) {
                    String[] email = rs.getString(1).split("@");
                    String emailID = email[0];
                    entry = emailID.substring(emailID.length() - 4, emailID.length());
                } else {
                    String[] email = rs.getString(1).split("@");
                    String[] domain = email[1].split("\\.");
                    entry = domain[0];
                }

                subResults = results.get(entry);

                String entry2 = null;
                if (element2.equals("gender")) {
                    entry2 = rs.getString(2);
                } else if (element2.equals("year")) {
                    String[] email = rs.getString(1).split("@");
                    String emailID = email[0];
                    entry2 = emailID.substring(emailID.length() - 4, emailID.length());
                } else {
                    String[] email = rs.getString(1).split("@");
                    String[] domain = email[1].split("\\.");
                    entry2 = domain[0];
                }

                int value = subResults.get(entry2);
                value++;
                totalUsers++;
                subResults.put(entry2, value);

            }
            for (String firstKey : results.keySet()) {
                LinkedHashMap<String, Integer> subResults = results.get(firstKey);
                BasicBreakdownObject outerObject = new BasicBreakdownObject(order, firstKey, 0, new ArrayList<BasicBreakdownObject>());
                for (String secondKey : subResults.keySet()) {
                    BasicBreakdownObject innerObject = new BasicBreakdownObject(element2, secondKey, subResults.get(secondKey));
                    outerObject.getBreakdown().add(innerObject);
                    outerObject.addCount(innerObject.getCount());
                }
                breakdownArray.add(outerObject);
            }

            countAndBreakdownObject.put(totalUsers, new BasicBreakdownObject(breakdownArray));

            return countAndBreakdownObject;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;

    }

    /**
     * Retrieves the results of the breakdown when three elements are passed in
     *
     * @param order the element chosen by the user
     * @param element2 the element chosen by the user
     * @param element3 the element chosen by the user
     * @param startDate the start of the 15 min query window
     * @param endDate the end of the 15 min query window
     *
     * @return breakdown result in form of HashMap<Integer,
     * BasicBreakdownObject>
     */
    public HashMap<Integer, BasicBreakdownObject> retrieveBreakdownReport(String order, String element2, String element3, Timestamp startDate, Timestamp endDate) {
        ArrayList<BasicBreakdownObject> breakdownArray = new ArrayList<BasicBreakdownObject>();
        HashMap<Integer, BasicBreakdownObject> countAndBreakdownObject = new HashMap<Integer, BasicBreakdownObject>();
        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>> results = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>>();

        // String query to pass into the database to retrieve email and gender
        String query = "select email, gender from demographics\n"
                + "inner join location\n"
                + "where demographics.mac_address = location.mac_address\n"
                + "and timestamp\n"
                + "between ?\n"
                + "and ?\n"
                + "group by demographics.mac_address";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            rs = ps.executeQuery();

            int totalUsers = 0;

            // if first element is gender
            if (order.equals("gender")) {
                results.put("M", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("F", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                // if first element is year
            } else if (order.equals("year")) {
                results.put("2010", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("2011", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("2012", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("2013", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("2014", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                // if first element is school
            } else {
                results.put("accountancy", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("business", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("economics", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("law", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("sis", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
                results.put("socsc", new LinkedHashMap<String, LinkedHashMap<String, Integer>>());
            }

            // if second element is gender
            if (element2.equals("gender")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    subResults.put("M", new LinkedHashMap<String, Integer>());
                    subResults.put("F", new LinkedHashMap<String, Integer>());
                }
                // if second element is year
            } else if (element2.equals("year")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    subResults.put("2010", new LinkedHashMap<String, Integer>());
                    subResults.put("2011", new LinkedHashMap<String, Integer>());
                    subResults.put("2012", new LinkedHashMap<String, Integer>());
                    subResults.put("2013", new LinkedHashMap<String, Integer>());
                    subResults.put("2014", new LinkedHashMap<String, Integer>());
                }
                // if second element is school
            } else {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    subResults.put("accountancy", new LinkedHashMap<String, Integer>());
                    subResults.put("business", new LinkedHashMap<String, Integer>());
                    subResults.put("economics", new LinkedHashMap<String, Integer>());
                    subResults.put("law", new LinkedHashMap<String, Integer>());
                    subResults.put("sis", new LinkedHashMap<String, Integer>());
                    subResults.put("socsc", new LinkedHashMap<String, Integer>());
                }
            }

            // if third element is gender
            if (element3.equals("gender")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    for (String key2 : subResults.keySet()) {
                        LinkedHashMap<String, Integer> subResults2 = subResults.get(key2);
                        subResults2.put("M", 0);
                        subResults2.put("F", 0);
                    }
                }
                // if third element is year
            } else if (element3.equals("year")) {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    for (String key2 : subResults.keySet()) {
                        LinkedHashMap<String, Integer> subResults2 = subResults.get(key2);
                        subResults2.put("2010", 0);
                        subResults2.put("2011", 0);
                        subResults2.put("2012", 0);
                        subResults2.put("2013", 0);
                        subResults2.put("2014", 0);
                    }
                }
                // if third element is school
            } else {
                for (String key : results.keySet()) {
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults = results.get(key);
                    for (String key2 : subResults.keySet()) {
                        LinkedHashMap<String, Integer> subResults2 = subResults.get(key2);
                        subResults2.put("accountancy", 0);
                        subResults2.put("business", 0);
                        subResults2.put("economics", 0);
                        subResults2.put("law", 0);
                        subResults2.put("sis", 0);
                        subResults2.put("socsc", 0);
                    }
                }
            }

            // Retrieve specific field of values from the result set returned by the database
            while (rs.next()) {
                LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults1 = null;
                String entry = null;
                if (order.equals("gender")) {
                    entry = rs.getString(2);
                } else if (order.equals("year")) {
                    String[] email = rs.getString(1).split("@");
                    String emailID = email[0];
                    entry = emailID.substring(emailID.length() - 4, emailID.length());
                } else {
                    String[] email = rs.getString(1).split("@");
                    String[] domain = email[1].split("\\.");
                    entry = domain[0];
                }

                subResults1 = results.get(entry);

                LinkedHashMap<String, Integer> subResults2 = null;
                String entry2 = null;
                if (element2.equals("gender")) {
                    entry2 = rs.getString(2);
                } else if (element2.equals("year")) {
                    String[] email = rs.getString(1).split("@");
                    String emailID = email[0];
                    entry2 = emailID.substring(emailID.length() - 4, emailID.length());
                } else {
                    String[] email = rs.getString(1).split("@");
                    String[] domain = email[1].split("\\.");
                    entry2 = domain[0];
                }

                subResults2 = subResults1.get(entry2);

                String entry3 = null;
                if (element3.equals("gender")) {
                    entry3 = rs.getString(2);
                } else if (element3.equals("year")) {
                    String[] email = rs.getString(1).split("@");
                    String emailID = email[0];
                    entry3 = emailID.substring(emailID.length() - 4, emailID.length());
                } else {
                    String[] email = rs.getString(1).split("@");
                    String[] domain = email[1].split("\\.");
                    entry3 = domain[0];
                }

                int value = subResults2.get(entry3);
                value++;
                totalUsers++;
                subResults2.put(entry3, value);

            }
            for (String firstKey : results.keySet()) {
                LinkedHashMap<String, LinkedHashMap<String, Integer>> subResults1 = results.get(firstKey);
                BasicBreakdownObject outerObject = new BasicBreakdownObject(order, firstKey, 0, new ArrayList<BasicBreakdownObject>());
                for (String secondKey : subResults1.keySet()) {
                    LinkedHashMap<String, Integer> subResults2 = subResults1.get(secondKey);
                    BasicBreakdownObject middleObject = new BasicBreakdownObject(element2, secondKey, 0, new ArrayList<BasicBreakdownObject>());
                    for (String thirdKey : subResults2.keySet()) {
                        BasicBreakdownObject innerObject = new BasicBreakdownObject(element3, thirdKey, subResults2.get(thirdKey));
                        middleObject.getBreakdown().add(innerObject);
                        middleObject.addCount(innerObject.getCount());
                    }
                    outerObject.getBreakdown().add(middleObject);
                    outerObject.addCount(middleObject.getCount());
                }
                breakdownArray.add(outerObject);
            }
            countAndBreakdownObject.put(totalUsers, new BasicBreakdownObject(breakdownArray));
            return countAndBreakdownObject;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * Retrieves the result of the mac address belonging to the previous window
     *
     * @param startDate of the query
     * @param endDate of the query
     * @param location of the query
     *
     * @return the mac_addresses belonging to the previous query window results
     * in form of an ArrayList
     */
    public ArrayList<String> retrievePreviousWindow(String location, Timestamp startDate, Timestamp endDate) {
        ArrayList<String> previousWindowResults = new ArrayList<String>();

        // String query to pass into the database to retrieve mac address, location id and semantic place
        String query = "select mac_address,semantic_place from (\n"
                + " select * from location\n"
                + " where timestamp between ?\n"
                + " AND ? \n"
                + " order by timestamp desc) as loc\n"
                + "inner join location_lookup \n"
                + "ON loc.location_id = location_lookup.location_id\n"
                + "group by mac_address;";

//                "select mac_address,max(timestamp), loc.location_id, semantic_place from location loc inner join location_lookup locup \n"
//                + "where locup.location_id = loc.location_id and timestamp between ?  and ? \n"
//                + "group by mac_address;";
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
//            ps.setString(3, location);
            rs = ps.executeQuery();

            while (rs.next()) {
                //
                if (rs.getString(2).equals(location)) {
                    previousWindowResults.add(rs.getString(1)); // adds each mac_address to the arrayList previousWindowResults to be returned.
                }
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return previousWindowResults;
    }

    /**
     * Retrieves the result of the next places result
     *
     * @param startDate of the query
     * @param endDate of the query
     * @param previousMacAddresses of the query
     * @return the NextPlacesResults in form of an ArrayList
     */
    public NextPlacesResults retrieveNextPlacesResult(ArrayList<String> previousMacAddresses, Timestamp startDate, Timestamp endDate) {
        //Stores the list of semantic places and the number of "visited" users found within that semantic place 
        HashMap<String, Integer> results = new HashMap<String, Integer>();
        // String query to pass into the database to retrieve timestamp and semantic place
        String query = "select timestamp, semantic_place \n"
                + " from location loc, location_lookup locup where\n"
                + "timestamp between ?  and ? and \n"
                + "mac_address=?\n"
                + " and loc.location_id=locup.location_id order by timestamp desc;";

        //returns the total number of users who "visited" another place 
        int count = 0;

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);

            // check how many counts of users visited another location
            // assumes that those that "visited" their original location are discounted   
            for (int i = 0; i < previousMacAddresses.size(); i++) {
                ps.setTimestamp(1, startDate);
                ps.setTimestamp(2, endDate);
                ps.setString(3, previousMacAddresses.get(i));
                rs = ps.executeQuery();
                //boolean found = false;

                //rs.next(); // go to first row
                while (rs.next()) {
                    Timestamp firstTimeStamp = rs.getTimestamp(1);
                    String firstLocation = rs.getString(2);
                    // check if first (latest) update is a valid 5min or more update
                    if (endDate.getTime() - firstTimeStamp.getTime() >= 300000) {
                        Integer value = results.get(firstLocation);
                        if (value == null) {
                            // add this as a new visited location
                            results.put(firstLocation, 1);
                            count++;
                            break;
                        } else {
                            value++;
                            // increase count for this location by 1
                            results.put(firstLocation, value);
                            count++;
                            break;
                        }
                    } else {
                        // this stores the possible extent of a valid location update
                        Timestamp limitTimestamp = endDate;
                        // this stores the location of the prevously checked update
                        String previousLocation = firstLocation;
                        //this stores the timestamp for the last failed update
                        Timestamp failedTimestamp = firstTimeStamp;
                        // if user has "visited" the semantic place for >=5 minutes, found = true;
                        //found = false;

                        //while (rs.next() && found==false) {
                        Timestamp currentTimestamp = rs.getTimestamp(1);
                        String currentLocation = rs.getString(2);
                        if (!currentLocation.equals(previousLocation)) {
                            limitTimestamp = failedTimestamp;
                        }
                        if (limitTimestamp.getTime() - currentTimestamp.getTime() >= 300000) {

                            Integer value = results.get(currentLocation);
                            if (value == null) {
                                // add this as a new visited location
                                results.put(currentLocation, 1);
                                count++;
                                //found=true;
                            } else {

                                value++;
                                // increase count for this location by 1
                                results.put(currentLocation, value);
                                count++;
                                //found=true;                           
                            }
                        }
                        previousLocation = currentLocation;
                        failedTimestamp = currentTimestamp;
                        //}

                    }

                }
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
        return new NextPlacesResults(results, count);
    }

    /**
     * Retrieves the popular places result
     *
     * @param startDate of the query
     * @param endDate of the query
     *
     * @return the PopularPlacesResults in form of an ArrayList
     */
    public ArrayList<PopularPlacesResult> retrievePopularPlacesResult(String startDate, String endDate) {
        ArrayList<PopularPlacesResult> popularPlacesResult = new ArrayList<PopularPlacesResult>();
        
        // String query to pass into the database to retrieve number of mac addresses and semantic place
        String query = "select count(mac_address) as population, locup.semantic_place from "
                + "location_lookup locup left outer join "
                + "(select location_id, mac_address, semantic_place, max(timestamp) from "
                + "(select location.location_id, mac_address, semantic_place, timestamp from "
                + "location inner join location_lookup on location.location_id = location_lookup.location_id "
                + "where timestamp > ? and timestamp <= ? order by timestamp desc) as t1 group by mac_address) "
                + "as maxtime on maxtime.location_id = locup.location_id group by locup.semantic_place "
                + "order by population desc, locup.semantic_place asc";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            rs = ps.executeQuery();

            while (rs.next()) {
                int count = rs.getInt(1);
                String semantic_place = rs.getString(2);
                if (count > 0) {
                    popularPlacesResult.add(new PopularPlacesResult(semantic_place, count));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return popularPlacesResult;
    }

    /**
     * Retrieves the companions places result
     *
     * @param macAddress
     * @param startDate
     * @param endDate
     *
     * @return the CompanionResult in form of an ArrayList
     */
    public ArrayList<CompanionsResult> retrieveCompanionResult(String macAddress, String startDate, String endDate) {
        ArrayList<CompanionsResult> companionsResultList = new ArrayList<CompanionsResult>();
        // String query to pass into the database to retrieve time spent, start time, end time, mac address 1, mac address 2, email and location
        String query = "SELECT SUM(TIMESTAMPDIFF(SECOND, GreatestStartTime, LeastEndTime)) as TimeSpentHere, GreatestStartTime, LeastEndTime, M1, M2, IFNULL(d1.email,''), LocationTrace FROM (\n"
                + "	SELECT GREATEST(MainTimestamp1, SecondaryTimeStamp1) as GreatestStartTime, LEAST(MainTimestamp2, SecondaryTimestamp2) as LeastEndTime, M1, M2, LocationTrace FROM (\n"
                + "		SELECT MainTimestamp1, MainTimestamp2, t1.mac_address AS M1, SecondaryTimestamp1, SecondaryTimestamp2, t2.mac_address AS M2, LocationTrace FROM (\n"
                + "				(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "				IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "				l1.`timestamp` as MainTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "				IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS MainTimestamp2, \n"
                + "				l1.mac_address, l1.location_id as LocationTrace FROM location l1 INNER JOIN location l2 \n"
                + "				INNER JOIN \n"
                + "				(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where mac_address = ? AND\n"
                + "				`timestamp` > ? AND `timestamp` <= ?\n"
                + "				GROUP BY mac_address) AS MaxTime\n"
                + "				ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "				INNER JOIN \n"
                + "				(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE mac_address = ? AND\n"
                + "				`timestamp` > ? AND `timestamp` <= ?\n"
                + "				GROUP BY mac_address) AS CountRows\n"
                + "				ON CountRows.mac_address = MaxTime.mac_address\n"
                + "				WHERE l1.mac_address = ? AND\n"
                + "				CASE rowCount \n"
                + "				WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "				   ELSE CASE l1.`timestamp`\n"
                + "					  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "					  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "				   END\n"
                + "				END\n"
                + "				AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "				AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
                + "				GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "				ORDER BY l1.`timestamp`) as t1\n"
                + "			INNER JOIN \n"
                + "				(SELECT MIN( TIMESTAMPDIFF( SECOND, l1.`timestamp`, IF(l2.`timestamp` = l1.`timestamp`, \n"
                + "				IF( TIMESTAMPADD( MINUTE, 9, FinalTime ) > ?, ?, TIMESTAMPADD( MINUTE, 9, FinalTime ) ), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) ) ) AS TimeSpentHere, \n"
                + "				l1.`timestamp` as SecondaryTimestamp1, IF(l2.`timestamp`=l1.`timestamp` AND l1.`timestamp` = FinalTime, \n"
                + "				IF(TIMESTAMPADD(MINUTE, 9, FinalTime) > ?, ?, TIMESTAMPADD(MINUTE, 9, FinalTime)), IF(TIMESTAMPDIFF(MINUTE, l1.`timestamp`, l2.`timestamp`) > 9, TIMESTAMPADD(MINUTE, 9, l1.`timestamp`),l2.`timestamp`) ) AS SecondaryTimestamp2, \n"
                + "				l1.mac_address, l1.location_id FROM location l1 INNER JOIN location l2 \n"
                + "				INNER JOIN \n"
                + "				(SELECT mac_address, MAX(`timestamp`) AS FinalTime from location where mac_address != ? AND\n"
                + "				`timestamp` > ? AND `timestamp` <= ?\n"
                + "				GROUP BY mac_address) AS MaxTime\n"
                + "				ON l1.mac_address = l2.mac_address AND MaxTime.mac_address = l2.mac_address\n"
                + "				INNER JOIN \n"
                + "				(SELECT COUNT(*) as rowCount, mac_address FROM location WHERE mac_address != ? AND\n"
                + "				`timestamp` > ? AND `timestamp` <= ?\n"
                + "				GROUP BY mac_address) AS CountRows\n"
                + "				ON CountRows.mac_address = MaxTime.mac_address\n"
                + "				WHERE l1.mac_address != ? AND\n"
                + "				CASE rowCount \n"
                + "				WHEN 1 THEN l1.`timestamp` = l2.`timestamp` \n"
                + "				   ELSE CASE l1.`timestamp`\n"
                + "					  WHEN FinalTime THEN l1.`timestamp` = l2.`timestamp`\n"
                + "					  ELSE l1.`timestamp` < l2.`timestamp`\n"
                + "				   END\n"
                + "				END\n"
                + "				AND l2.`timestamp` > ? AND l2.`timestamp` <= ?\n"
                + "				AND l1.`timestamp` > ? AND l1.`timestamp` <= ?\n"
                + "				GROUP BY l1.`timestamp`, mac_address, l1.location_id\n"
                + "				ORDER BY l1.`timestamp`) as t2\n"
                + "			ON LocationTrace = t2.location_id\n"
                + "		AND t1.mac_address != t2.mac_address\n"
                + "		AND MainTimestamp2 > SecondaryTimeStamp1\n"
                + "		AND MainTimestamp1 < SecondaryTimeStamp2)\n"
                + "	) as t3\n"
                + "	GROUP BY M1,M2, GreatestStartTime) aS T4\n"
                + "     LEFT OUTER JOIN demographics d1"
                + "     ON M2 = d1.mac_address "
                + "GROUP BY M1, M2\n"
                + "ORDER BY TimeSpentHere DESC;";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1, endDate);
            ps.setString(2, endDate);
            ps.setString(3, endDate);
            ps.setString(4, endDate);
            ps.setString(5, macAddress);
            ps.setString(6, startDate);
            ps.setString(7, endDate);
            ps.setString(8, macAddress);
            ps.setString(9, startDate);
            ps.setString(10, endDate);
            ps.setString(11, macAddress);
            ps.setString(12, startDate);
            ps.setString(13, endDate);
            ps.setString(14, startDate);
            ps.setString(15, endDate);
            ps.setString(16, endDate);
            ps.setString(17, endDate);
            ps.setString(18, endDate);
            ps.setString(19, endDate);
            ps.setString(20, macAddress);
            ps.setString(21, startDate);
            ps.setString(22, endDate);
            ps.setString(23, macAddress);
            ps.setString(24, startDate);
            ps.setString(25, endDate);
            ps.setString(26, macAddress);
            ps.setString(27, startDate);
            ps.setString(28, endDate);
            ps.setString(29, startDate);
            ps.setString(30, endDate);
            rs = ps.executeQuery();

            // Retrieves companion result
            while (rs.next()) {
                int timeSpent = rs.getInt(1);
                String mac_address = rs.getString(5);
                String emailAddress = rs.getString(6);
                if (timeSpent > 0) {
                    companionsResultList.add(new CompanionsResult(emailAddress, mac_address, timeSpent));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return companionsResultList;
    }

}
