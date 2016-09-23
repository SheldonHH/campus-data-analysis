package controller;

import dataManager.LocationLookupManager;
import dataManager.ProcessRecordsManager;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import model.ErrorUtility;
import model.FileRecordError;

/**
 * Validates the files to bootstrap or upload and stores error records
 *
 */
public class RecordValidationController {

    private final HashMap<String, String> schoolsMap = new HashMap<String, String>();
    private int successfulRows = 0;
    private int totalErrors = 0;
    private ArrayList<FileRecordError> errorRecords = new ArrayList<FileRecordError>();
    private HashMap<Integer, String> locationLookupMap = new HashMap<Integer, String>();
    private HashMap<String, String> demographicsMacAddressMap = new HashMap<String, String>();
    private HashMap<String, String> demographicsBatchMacAddressMap = new HashMap<String, String>();
    private HashMap<String, String> checkForRepeatLocationID = new HashMap<String, String>();
    private HashMap<String, Integer> locationMap = new HashMap<String, Integer>();

    boolean isFirstLocationCheck = true;
    boolean isFirstDemographicsCheck = true;

    /**
     * Constructor for RecordValidationController which adds the list of schools
     * into the Hashmap of schools
     */
    public RecordValidationController() {
        // Put all known schools into the hashmap for later checking. Change to context params in future
        schoolsMap.put("business", "business");
        schoolsMap.put("sis", "sis");
        schoolsMap.put("law", "law");
        schoolsMap.put("economics", "economics");
        schoolsMap.put("socsc", "socsc");
        schoolsMap.put("accountancy", "accountancy");
    }

    /**
     * Returns if the line in Demographics file is valid or not
     *
     * @param conn stores the connection to process the request
     * @param line stores entire entry to be validated
     * @param rowNum indicates the row number where this line is found
     * @param fileName indicates the file to which this line belongs to
     *
     * @return a boolean value which indicates if this line is valid
     */
    public boolean validateDemographics(Connection conn, String[] line, int rowNum, String fileName) {
        String[] arr = line;
        ArrayList<String> errorList = new ArrayList<String>();
        int arraySize = arr.length;
        // Check if there are adequate fields
        if (arraySize == 0) {
            errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            errorList.add(ErrorUtility.BSBLANK_NAME);
            errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            errorList.add(ErrorUtility.BSBLANK_EMAIL);
            errorList.add(ErrorUtility.BSBLANK_GENDER);
        } else if (arraySize == 1) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }
            errorList.add(ErrorUtility.BSBLANK_NAME);
            errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            errorList.add(ErrorUtility.BSBLANK_EMAIL);
            errorList.add(ErrorUtility.BSBLANK_GENDER);
        } else if (arraySize == 2) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }
            if (arr[1].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_NAME);
            }
            errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            errorList.add(ErrorUtility.BSBLANK_EMAIL);
            errorList.add(ErrorUtility.BSBLANK_GENDER);
        } else if (arraySize == 3) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }
            if (arr[1].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_NAME);
            }
            if (arr[2].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            }
            errorList.add(ErrorUtility.BSBLANK_EMAIL);
            errorList.add(ErrorUtility.BSBLANK_GENDER);
        } else if (arraySize == 4) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }
            if (arr[1].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_NAME);
            }
            if (arr[2].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            }
            if (arr[3].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_EMAIL);
            }
            errorList.add(ErrorUtility.BSBLANK_GENDER);
        } else {
            // .trim() to remove front and back whitespaces
            String macAddress = arr[0].trim();
            String name = arr[1].trim();
            String password = arr[2].trim();
            String email = arr[3].trim().toLowerCase();
            String gender = arr[4].trim().toUpperCase();

            if (macAddress.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }

            if (name.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_NAME);
            }

            if (password.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_PASSWORD);
            }

            if (email.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_EMAIL);
            }

            if (gender.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_GENDER);
            }

            // If there are no errors, means that there is data in each of the 5 required fields
            if (errorList.isEmpty()) {

                if (isFirstDemographicsCheck) {
                    // Call this when I want to retrieve all mac_addresses for the FIRST run
                    isFirstDemographicsCheck = false;
                    demographicsMacAddressMap = ProcessRecordsManager.retrieveAllMacAddressFromDemographics(conn);
                }

                // Check if Mac Address exists in database first! No duplicates
                if (demographicsMacAddressMap.get(macAddress) != null) {
                    errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                    // Check if Mac Adderss exists in the current batch of 1000 that is being processed
                } else if (demographicsBatchMacAddressMap.get(macAddress) != null) {
                    errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                } else {
                    //Check length of mac address
                    if (macAddress.length() != 40) {
                        errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                    } else {
                        // Check that characters are valid
                        String hexdecimals = "1234567890abcedfABCEDF";
                        for (int j = 0; j < macAddress.length(); j++) {
                            String checkChar = "" + macAddress.charAt(j);
                            if (hexdecimals.indexOf(checkChar) == -1) {
                                errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                                break;
                            }
                        }
                    }
                }

                // Check length of password 
                if (password.length() < 8) {
                    errorList.add(ErrorUtility.BSINVALID_PASSWORD);
                } else if (password.indexOf(" ") != -1) {
                    errorList.add(ErrorUtility.BSINVALID_PASSWORD);
                }

                // Check validity of email
                if (email.indexOf(" ") != -1) {
                    errorList.add(ErrorUtility.BSINVALID_EMAIL);
                } else {
                    if (email.indexOf("@") == -1) {
                        errorList.add(ErrorUtility.BSINVALID_EMAIL);
                    } else {
                        // Splitting the email into 2 parts e.g. aixin.2014@business.smu.edu.sg
                        // First value -> aixin.2014 , second value -> business.smu.edu.sg
                        String[] splitValues = email.split("@");
                        if (splitValues.length != 2) {
                            errorList.add(ErrorUtility.BSINVALID_EMAIL);
                        } else {
                            String retrieveYear = splitValues[0];

                            String retrieveFaculty = splitValues[1];
                            // Retrieve the year
                            String year = retrieveYear.substring(retrieveYear.lastIndexOf(".") + 1);
                            
                            // Retrieve the school
                            String faculty = "";
                            try {
                                // Check year
                                faculty = retrieveFaculty.substring(0, retrieveFaculty.indexOf("smu.edu.sg") - 1);
                                
                                int yearOfStudent = Integer.parseInt(year);
                                if (yearOfStudent < 2010 || yearOfStudent > 2014) {
                                    errorList.add(ErrorUtility.BSINVALID_EMAIL);
                                }
                            } catch (NumberFormatException e) {
                                errorList.add(ErrorUtility.BSINVALID_EMAIL);
                            } catch (IndexOutOfBoundsException index) {
                                
                                errorList.add(ErrorUtility.BSINVALID_EMAIL);
                            }

                            // Iterates through the front portion of the email e.g. Aixin.2013 to check 
                            // if they all contain letters / digits or '.'
                            if (!errorList.contains(ErrorUtility.BSINVALID_EMAIL)) {
                                for (int k = 0; k < retrieveYear.length(); k++) {
                                    char letter = retrieveYear.charAt(k);
                                    if (!Character.isLetterOrDigit(letter) && letter != '.') {
                                        errorList.add(ErrorUtility.BSINVALID_EMAIL);
                                        break;
                                    }
                                }
                            }
                            if (!errorList.contains(ErrorUtility.BSINVALID_EMAIL)) {
                                // Check if the email domain is .smu.edu.sg
                                if (!retrieveFaculty.endsWith("smu.edu.sg")) {
                                    errorList.add(ErrorUtility.BSINVALID_EMAIL);
                                } else if (schoolsMap.get(faculty) == null) {
                                    
                                    errorList.add(ErrorUtility.BSINVALID_EMAIL);
                                }
                                // Check if school is valid
                            }
                        }
                    }
                }
                // Check for valid gender
                if (!gender.equals("M") && !gender.equals("F")) {
                    errorList.add(ErrorUtility.BSINVALID_GENDER);
                }
            }

            if (errorList.isEmpty()) {
                demographicsBatchMacAddressMap.put(macAddress, "");
                if (demographicsBatchMacAddressMap.size() == 1000) {
                    demographicsBatchMacAddressMap.clear();
                }
            }
        }

        if (!errorList.isEmpty()) {
            errorRecords.add(new FileRecordError(fileName, rowNum, errorList));
            totalErrors++;
            return false;
        } else {

            successfulRows++;
            return true;
        }
    }

    /**
     * Returns if the line in Location file is valid or not
     *
     * @param conn stores the connection object to process the connection
     * @param line stores entire entry to be validated
     * @param rowNum indicates the row number where this line is found
     * @param fileName indicates the file to which this line belongs to
     * @param successfulLocationRecordList the current rows which are
     * successfully validated
     * @param successfulRowCountList the current row count to which the current
     * rows are validated
     * @param isBootstrap
     *
     * @return a boolean value which indicates if this line is valid
     */
    public boolean validateLocation(Connection conn, String[] line, int rowNum, String fileName, ArrayList<String[]> successfulLocationRecordList, ArrayList<Integer> successfulRowCountList, boolean isBootstrap) {
        // Checks if this is the first time a location is being validated, if so
        // pull location ID from database to validate
        if (isFirstLocationCheck) {
            isFirstLocationCheck = false;
            locationLookupMap = LocationLookupManager.retrieveAllLocationLookupData(conn);
        }

        String[] arr = line;
        int arraySize = arr.length;
        ArrayList<String> errorList = new ArrayList<String>();
        String timestamp = "";
        String macAddress = "";
        String locationID = "";
        // Check if there are valid number of entries
        if (arraySize == 0) {
            errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            errorList.add(ErrorUtility.BSBLANK_LOCATION);
            errorList.add(ErrorUtility.BSBLANK_TIMESTAMP);
        } else if (arraySize == 1) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_TIMESTAMP);
            }
            errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            errorList.add(ErrorUtility.BSBLANK_LOCATION);
        } else if (arraySize == 2) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_TIMESTAMP);
            }
            if (arr[1].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }
            errorList.add(ErrorUtility.BSBLANK_LOCATION);
        } else {
            timestamp = arr[0].trim();
            macAddress = arr[1].trim();
            locationID = arr[2].trim();

            // Check if either of the fields are empty after trim.
            if (timestamp.isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_TIMESTAMP);
            }

            if (macAddress.isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_MACADDRESS);
            }

            if (locationID.isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_LOCATION);
            }
            // If they all are not whitespaces / empty, enter if() for further validation
            if (errorList.isEmpty()) {
                try {
                    // Check for valid location
                    Integer locationNum = Integer.parseInt(locationID);
                    if (locationLookupMap.get(locationNum) == null) {
                        errorList.add(ErrorUtility.BSINVALID_LOCATION);
                    } else if (locationNum < 0) {
                        errorList.add(ErrorUtility.BSINVALID_LOCATION);
                    }
                } catch (NumberFormatException e) {
                    errorList.add(ErrorUtility.BSINVALID_LOCATION);
                }

                // Check length of mac address
                if (macAddress.length() != 40) {
                    errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                } else {
                    // Check for illegal characters
                    String hexdecimals = "1234567890abcedfABCEDF";
                    for (int j = 0; j < macAddress.length(); j++) {
                        String checkChar = "" + macAddress.charAt(j);
                        if (hexdecimals.indexOf(checkChar) == -1) {
                            errorList.add(ErrorUtility.BSINVALID_MACADDRESS);
                            break;
                        }
                    }
                }

                Date testDate = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    testDate = sdf.parse(timestamp);
                    String formatedDate = sdf.format(testDate);

                    String yearString = formatedDate.substring(0, formatedDate.indexOf("-"));
                    int year = Integer.parseInt(yearString);
                    if (year > 9999 || year < 1000) {
                        errorList.add(ErrorUtility.INVALID_DATE);
                    } else if (!sdf.format(testDate).equals(timestamp)) {
                        errorList.add(ErrorUtility.BSINVALID_TIMESTAMP);
                    }
                } catch (ParseException e) {
                    errorList.add(ErrorUtility.BSINVALID_TIMESTAMP);
                } catch (NumberFormatException e){
                    errorList.add(ErrorUtility.BSINVALID_TIMESTAMP);
                } catch (IndexOutOfBoundsException e){
                    errorList.add(ErrorUtility.BSINVALID_TIMESTAMP);
                }
            }
        }

        if (!errorList.isEmpty()) {
            totalErrors++;
            errorRecords.add(new FileRecordError(fileName, rowNum, errorList));
            return false;
        } else {
            int duplicatedRow = ProcessRecordsManager.checkLocation(arr, conn, rowNum, isBootstrap);

            if (locationMap.get(arr[0] + "," + arr[1]) != null) {
                errorList.add(ErrorUtility.BSDUPLICATE_ROW);
                // Retrieve the row count of the previous duplicated row
                int previousDuplicatedRow = locationMap.get(arr[0] + "," + arr[1]);
                // Retrieve which index is the duplicated row located in
                int duplicatedRowIndex = successfulRowCountList.indexOf(previousDuplicatedRow);
                // update the row count
                successfulRowCountList.set(duplicatedRowIndex, rowNum);
                // set the new row as the one to be bootstrapped
                successfulLocationRecordList.set(duplicatedRowIndex, line);
                totalErrors++;
                errorRecords.add(new FileRecordError(fileName, locationMap.get(arr[0] + "," + arr[1]), errorList));
                locationMap.put(arr[0] + "," + arr[1], rowNum);
                return false;
            } else if (duplicatedRow != -1) {
                errorList.add(ErrorUtility.BSDUPLICATE_ROW);
                if(isBootstrap){
                    errorRecords.add(new FileRecordError(fileName, duplicatedRow, errorList));
                } else {
                    errorRecords.add(new FileRecordError(fileName, rowNum, errorList));
                }
                totalErrors++;
                
                return false;
            } else {
                locationMap.put(arr[0] + "," + arr[1], rowNum);
                successfulRows++;
                // Remember to change this line if batch size changes
                // Clear the map once there are 1000 entries
                if (locationMap.size() == 50000) {
                    locationMap.clear();
                }
                return true;
            }
        }
    }

    /**
     * Returns if the line in LocationLookup file is valid or not
     *
     * @param line stores entire entry to be validated
     * @param rowNum indicates the row number where this line is found
     * @param fileName indicates the file to which this line belongs to
     *
     * @return a boolean value which indicates if this line is valid
     */
    public boolean validateLocationLookup(String[] line, int rowNum, String fileName) {

        String[] arr = line;
        ArrayList<String> errorList = new ArrayList<String>();
        // Update location-lookup into DB
        // Based on the location id from DB 
        // Compare to check if location ID exist
        String locationID = "";
        String semanticPlace = "";
        int arraySize = arr.length;
        if (arraySize == 0) {
            errorList.add(ErrorUtility.BSBLANK_LOCATIONID);
            errorList.add(ErrorUtility.BSBLANK_SEMANTIC_PLACE);
        } else if (arraySize == 1) {
            if (arr[0].trim().isEmpty()) {
                errorList.add(ErrorUtility.BSBLANK_LOCATIONID);
            }
            errorList.add(ErrorUtility.BSBLANK_SEMANTIC_PLACE);
        } else {
            locationID = arr[0].trim();
            semanticPlace = arr[1].trim();

            if (locationID.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_LOCATIONID);
            }

            if (semanticPlace.equals("")) {
                errorList.add(ErrorUtility.BSBLANK_SEMANTIC_PLACE);
            }

            if (errorList.isEmpty()) {
                try {
                    int locationNum = Integer.parseInt(locationID);
                    if (locationNum <= 0) {
                        errorList.add(ErrorUtility.BSINVALID_LOCATIONID);
                    }
                } catch (NumberFormatException e) {
                    errorList.add(ErrorUtility.BSINVALID_LOCATIONID);
                }
                try {
                    if (!"SMUSISL".equals(semanticPlace.substring(0, 7)) && !"SMUSISB".equals(semanticPlace.substring(0, 7))) {
                        errorList.add(ErrorUtility.BSINVALID_SEMANTIC_PLACE);
                        
                    } else if (semanticPlace.length() <= 8) {
                        errorList.add(ErrorUtility.BSINVALID_SEMANTIC_PLACE);
                    } else {
                        try {
                            int floor = Integer.parseInt(semanticPlace.substring(7,8));
                            
                            if(floor <= 0){
                                errorList.add(ErrorUtility.BSINVALID_SEMANTIC_PLACE);
                            }
                        } catch (NumberFormatException e){
                            errorList.add(ErrorUtility.BSINVALID_SEMANTIC_PLACE);
                        }
                    }

                } catch (IndexOutOfBoundsException e) {
                    errorList.add(ErrorUtility.BSINVALID_SEMANTIC_PLACE);
                }
            }
        }

        if (checkForRepeatLocationID.get(locationID) != null) {
            errorList.add(ErrorUtility.BSINVALID_LOCATIONID);
        }

        if (!errorList.isEmpty()) {
            totalErrors++;
            errorRecords.add(new FileRecordError(fileName, rowNum, errorList));
            return false;
        } else {
            checkForRepeatLocationID.put(locationID, locationID);
            successfulRows++;
            return true;
        }
    }

    /**
     * Retrieves the number of successful rows processed
     *
     * @return number of successful rows processed
     */
    public int getSuccessfulRows() {
        return successfulRows;
    }

    /**
     * Retrieves the total number of errors processed
     *
     * @return number of errors processed
     */
    public int getTotalErrors() {
        return totalErrors;
    }

    /**
     * Retrieves the list of error records
     *
     * @return the entire set of error records
     */
    public ArrayList<FileRecordError> getErrorRecords() {
        return errorRecords;
    }
}