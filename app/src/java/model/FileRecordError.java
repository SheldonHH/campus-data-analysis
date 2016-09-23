package model;

import java.util.ArrayList;

/**
 * Stores file record error messages 
 *
 */

public class FileRecordError {
    private String fileName;
    private int rowNumber;
    private ArrayList<String> errorMessages;
    
    /**
     * Creates a FileRecordError which will store the errors, filename which the error belongs to and row number
     * 
     * @param fileName corresponding to the file which this error was detected from 
     * @param rowNumber row number corresponding to the row which this error is from
     * @param errorMessages stores the list of errors from this line
     */
    public FileRecordError(String fileName, int rowNumber, ArrayList<String> errorMessages) {
        this.fileName = fileName;
        this.rowNumber = rowNumber;
        this.errorMessages = errorMessages;
    }
    /**
     * Returns the file name of which this error belonged to
     * 
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * Returns the row number of which this error belonged to
     * 
     * @return the row number from which this error belongs to
     */
    public int getRowNumber() {
        return rowNumber;
    }
    /**
     * Returns the list of error messages
     * 
     * @return the list of error messages
     */
    public ArrayList<String> getErrorMessages() {
        return errorMessages;
    }   
}