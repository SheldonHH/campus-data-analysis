package model;

/**
 * Checks for null and change to empty String
 * 
 */

public class Utility {
    
    /**
     * Check for null String and change it to empty String
     * @param checkString
     * 
     * @return check String
     */
    public static String nullCheck (String checkString){
        return checkString == null ? "" : checkString;
    }
    
    /**
     * Check for null Selected and change it to empty
     * @param checkString
     * @param valueToCheckAgainst
     * 
     * @return selected String
     */
    public static String selectedCheck (String checkString, String valueToCheckAgainst){
       if(checkString == null || !checkString.equals(valueToCheckAgainst)){
           return "";
       } else {
           return " selected='selected'";
       }
    }   
}