package model;

import java.util.Comparator;
import java.util.Map;

/**
 * Sorts Group Detection Results according to Location_id
 * 
 */
public class GroupLocationIDComparator implements Comparator<Map.Entry<String, Integer>> {

    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
        String aLocation = a.getKey();
        String bLocation = b.getKey();
        int locationResult = aLocation.compareTo(bLocation);
        if (locationResult != 0) {
            return locationResult;
        } else {
            Integer aTime = a.getValue();
            Integer bTime = b.getValue();
            return bTime.compareTo(aTime); // note that this is descending order
        }
    }
}
