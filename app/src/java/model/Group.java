package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Stores group members, locations and time spent, and next locations
 *
 */
public class Group {

    private ArrayList<String> groupMembers;
    private LinkedHashMap<String, Integer> locations;
    private LinkedHashMap<String, ArrayList<Timestamp>> nextPlacesLocations;

    /**
     * Create a group object with the specified fields
     * 
     * @param firstMember
     * @param secondMember
     * @param firstLocations
     */
    public Group(String firstMember, String secondMember, HashMap<String, Integer> firstLocations) {
        this.groupMembers = new ArrayList<String>();
        this.locations = new LinkedHashMap<String, Integer>();
        nextPlacesLocations = new LinkedHashMap<String, ArrayList<Timestamp>>();
        locations.putAll(firstLocations);
        this.groupMembers.add(firstMember);
        this.groupMembers.add(secondMember);
    }
    /**
     * Create a group object with the specified fields
     * 
     * @param firstMember
     */
    public Group(String firstMember) {
        this.groupMembers = new ArrayList<String>();
        groupMembers.add(firstMember);
        locations = new LinkedHashMap<String, Integer>();
        nextPlacesLocations = new LinkedHashMap<String, ArrayList<Timestamp>>();
    }
    
    /**
     * Create a group object with the specified fields
     * 
     * @param firstMember
     * @param secondMember
     * @param nextPlacesLocations
     */
    public Group(String firstMember, String secondMember, LinkedHashMap<String, ArrayList<Timestamp>> nextPlacesLocations) {
        this.groupMembers = new ArrayList<String>();
        this.locations = new LinkedHashMap<String, Integer>();
        this.nextPlacesLocations = nextPlacesLocations;
        this.groupMembers.add(firstMember);
        this.groupMembers.add(secondMember);
    }

    /**
     * Adds member to this group
     * 
     * @param member the groupMembers to add
     */
    public void addMember(String member) {
        groupMembers.add(member);
    }

    /**
     * Retrieves members of this group
     * 
     * @return the groupMembers
     */
    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    /**
     * Sets group members of this group
     * 
     * @param groupMembers the groupMembers to set
     */
    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     * Retrieves locations where this group stay together
     * 
     * @return the locations
     */
    public LinkedHashMap<String, Integer> getLocations() {
        return locations;
    }

    /**
     * Sets locations where this group stay together
     * 
     * @param locations the locations to set
     */
    public void setLocations(LinkedHashMap<String, Integer> locations) {
        this.locations = locations;
    }

    /**
     * Retrieves the total time this group spent together for
     * 
     * @return totalTime the total time spent
     */
    public int getTotalTime() {
        Iterator<Integer> iter = locations.values().iterator();
        int totalTime = 0;
        while (iter.hasNext()) {
            totalTime += iter.next();
        }
        return totalTime;
    }

    /**
     * Sets next locations this group visit
     * 
     * @param nextPlacesLocations
     */
    public void setNextPlacesLocations(LinkedHashMap<String, ArrayList<Timestamp>> nextPlacesLocations) {
        this.nextPlacesLocations = nextPlacesLocations;
    }

    /**
     * Retrieves next locations this group visit
     * 
     * @return the next places locations in form of LinkedHashMap<String,
     * ArrayList<Timestmp>>
     */
    public LinkedHashMap<String, ArrayList<Timestamp>> getNextPlacesLocations() {
        return nextPlacesLocations;
    }
}