package model;

/**
 * Stores error messages 
 * 
 */

public class ErrorUtility {

    //demographics.csv errors
    public static final String BSINVALID_MACADDRESS = "invalid mac address";
    public static final String BSINVALID_PASSWORD = "invalid password";
    public static final String BSINVALID_EMAIL = "invalid email";
    public static final String BSINVALID_GENDER = "invalid gender";
    public static final String BSBLANK_MACADDRESS = "mac address is blank";
    public static final String BSBLANK_PASSWORD = "password is blank";
    public static final String BSBLANK_EMAIL = "email is blank";
    public static final String BSBLANK_GENDER = "gender is blank";
    public static final String BSBLANK_NAME = "name is blank";
    // location-lookup.csv errors
    public static final String BSINVALID_LOCATIONID = "invalid location id";
    public static final String BSINVALID_SEMANTIC_PLACE = "invalid semantic place";
    public static final String BSBLANK_LOCATIONID = "location id is blank";
    public static final String BSBLANK_SEMANTIC_PLACE = "semantic place is blank";
    //location.csv errors
    public static final String BSINVALID_LOCATION = "invalid location";
    public static final String BSINVALID_TIMESTAMP = "invalid timestamp";
    public static final String BSBLANK_LOCATION = "location is blank";
    public static final String BSBLANK_TIMESTAMP = "timestamp is blank";
    public static final String BSDUPLICATE_ROW = "duplicate row";
    //general errors
    public static final String INVALID_DATE = "invalid date";
    public static final String INVALID_K = "invalid k";
    public static final String BLANK_DATE = "blank date";
    public static final String MISSING_DATE = "missing date";
    public static final String BLANK_K = "blank k";
    //login errors
    public static final String INVALID_LOGIN = "invalid username/password";
    public static final String BLANK_USERNAME = "blank username";

    // heatmap errors
    public static final String INVALID_FLOOR = "invalid floor";
    public static final String BLANK_FLOOR = "blank floor";
    //BLR errors
    public static final String INVALID_ORDER = "invalid order";
    public static final String INVALID_ORIGIN = "invalid origin";
    public static final String BLANK_ORDER = "blank order";
    public static final String BLANK_ORIGIN = "blank origin";
    
    // JSON errors
    public static final String MISSING_TOKEN = "missing token";
    public static final String INVALID_TOKEN = "invalid token";
    public static final String BLANK_TOKEN = "blank token";
    public static final String MISSING_USERNAME = "missing username";
    public static final String MISSING_PASSWORD = "missing password";
    public static final String MISSING_FLOOR = "missing floor";
    public static final String MISSING_ORDER = "missing order";
    public static final String MISSING_K = "missing k";
    public static final String MISSING_MACADDRESS = "missing mac address";
    public static final String MISSING_ORIGIN = "missing origin";
    public static final String BLANK_MACADDRESS = "blank mac address";
    public static final String INVALID_MACADDRESS = "invalid mac address";
    public static final String INVALID_PASSWORD = "invalid password";
    public static final String MISSING_BOOTSTRAPFILE = "missing bootstrap-file";
    public static final String BLANK_BOOTSTRAPFILE = "blank bootstrap-file";
}