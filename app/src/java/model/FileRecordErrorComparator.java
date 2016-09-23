package model;

import java.util.Comparator;

/**
 * Sorts file error messages according to row number
 * 
 */
public class FileRecordErrorComparator implements Comparator<FileRecordError>{
    @Override
    public int compare(FileRecordError f1, FileRecordError f2){
        return f1.getRowNumber() - f2.getRowNumber();
    }
}
