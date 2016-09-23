package model;

import java.util.Comparator;

/**
 * Sorts the Group Detection Results in descending order according to group size and time spent
 * 
 */
public class GroupSizeTimeSpentComparator implements Comparator<Group>{
    public int compare(Group a, Group b) {
        if (a.getGroupMembers().size() == b.getGroupMembers().size()) {
            //sort result in descending order according to time spent
            return b.getTotalTime() - a.getTotalTime(); 
        } else {
            //sort result in descending order according to group size
            return b.getGroupMembers().size() - a.getGroupMembers().size(); 
        }
    }
}