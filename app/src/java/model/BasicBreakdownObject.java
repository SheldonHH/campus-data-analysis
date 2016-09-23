package model;

import java.util.ArrayList;

/**
 * Stores the attributes of Basic Break Down Result
 *
 */
public class BasicBreakdownObject {

    private String category;
    private String element;
    private String status;
    private int count;
    private int total;
    private ArrayList<BasicBreakdownObject> breakdown;

    /**
     * Creates a breakdown object with specified category, element and count
     *
     * @param category the category for this result, ie school, gender or year
     * @param element the name of the category element, eg. female, 2014 or sis
     * @param count the count of people for this element
     */
    public BasicBreakdownObject(String category, String element, int count) {
        this.category = category;
        this.element = element;
        this.count = count;  
    }

    /**
     * Creates a breakdown object with a specified element, count and list of
     * BreakdownObjects
     *
     * @param category the category for this result, ie school, gender or year
     * @param element the name of the category element, eg. female, 2014 or sis
     * @param count the count of people for this element
     * @param list the results for the categories on the next level of breakdown after the current category
     */
    public BasicBreakdownObject(String category, String element, int count, ArrayList<BasicBreakdownObject> list) {
        this.category = category;
        this.element = element;
        this.count = count;
        this.breakdown = list;
    }

    /**
     *Creates an breakdown object with specified status and list of BreakdownObjects
     * This constructor is returned by the BasicReportManager's retrieveBreakdownObject methods
     * @param status the status of the query, ie success or error
     * @param total the total number of people found
     * @param list all the results for the current queried breakdown
     */
    public BasicBreakdownObject(String status, int total, ArrayList<BasicBreakdownObject> list) {
        this.status = status;
        this.breakdown=list;
        this.total=total;
    }

    /**
     *
     * @param list the results for the categories on the next level of breakdown after the current category
     */
    public BasicBreakdownObject( ArrayList<BasicBreakdownObject> list) {       
        this.breakdown=list;
    }
     

    /**
     * @return the category, ie school, gender or year
     */    
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set, ie school, gender or year
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the element, which is the name of the current category element
     */
    public String getElement() {
        return element;
    }

    /**
     * @param element the element to set, which is the name of the current category element
     */
    public void setElement(String element) {
        this.element = element;
    }

    /**
     * @return the status of the current query
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status of the current query to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the count which is the number of people for this particular result
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set, which is the number of people for this particular result
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the breakdown, which is the list of results stored in this object
     */
    public ArrayList<BasicBreakdownObject> getBreakdown() {
        return breakdown;
    }

    /**
     * @param breakdown the breakdown to set, which is the list of results stored in this object
     */
    public void setBreakdown(ArrayList<BasicBreakdownObject> breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * @return the total number of people that were found in this query
     */
    public int getTotal() {
        return total;
    }
    
    /**
     * @param add the number to add to count of people found in this query
     */
    public void addCount(int add) {
        count+= add;
    }
}