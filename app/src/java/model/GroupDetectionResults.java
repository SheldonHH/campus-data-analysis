package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores the attributes of Group Detection Result
 * 
 */
public class GroupDetectionResults {
    private ArrayList<Group> groupList;
    private int totalUsers;
    private HashMap<String, String> userList;
    
    /**
     * Creates an instance of GroupDetectionResults with the specified fields
     * 
     * @param groupList
     * @param userList
     * @param totalUsers
     */
    public GroupDetectionResults(ArrayList<Group> groupList, HashMap<String, String> userList, int totalUsers) {
        this.totalUsers = totalUsers;
        this.groupList = groupList;
        this.userList = userList;
    }

    /**
     * Retrieves groups of this group detection result
     * 
     * @return the groupList
     */
    public ArrayList<Group> getGroupList() {
        return groupList;
    }

    /**
     * Sets groups for this group detection result
     * 
     * @param groupList the groupList to set
     */
    public void setGroupList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }

    /**
     * Retrieves total number of users of this group detection result
     * 
     * @return the totalUsers
     */
    public int getTotalUsers() {
        return totalUsers;
    }

    /**
     * Sets total number of users for this group detection result
     * 
     * @param totalUsers the totalUsers to set
     */
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    /**
     * Retrieves user list of this group detection result
     * 
     * @return the userList
     */
    public HashMap<String, String> getUserList() {
        return userList;
    }

    /**
     * Sets user list for this group detection result
     * 
     * @param userList the userList to set
     */
    public void setUserList(HashMap<String, String> userList) {
        this.userList = userList;
    }
}