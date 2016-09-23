package model;

import java.util.HashMap;

/**
 * Stores the attributes of Next Places Result
 * 
 */

public class NextPlacesResults {

    private int rank;
    private String semantic_place;
    private int count;
    private HashMap<String,Integer> results;
    
    /**
     * Creates a new NextPlacesResults object with the given rank, results and count of people
     *
     * @param rank The rank of semantic place in the results
     * @param semantic_place The semantic place for this particular result
     * @param count The count of people at this location
     */
    
    public NextPlacesResults(int rank, String semantic_place, int count) {
        this.rank = rank;
        this.semantic_place = semantic_place;
        this.count = count;
    }
    
    /**
     * Creates a new NextPlacesResults object with the given results and count of people
     * 
     * @param results multiple results stored in this
     * @param count the count of people
     */
    public NextPlacesResults(HashMap<String,Integer> results, int count){
        this.results=results;
        this.count=count;
    }

    /**
     *
     * @return results the results stored in this object
     */
    public HashMap<String,Integer> getResults(){
        return results;
    }
    
    /**
     * @return the rank the rank assigned to this location
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set for this result
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return the semantic_place the location associated with this result
     */
    public String getSemantic_place() {
        return semantic_place;
    }

    /**
     * @param semantic_place the semantic_place to set
     */
    public void setSemantic_place(String semantic_place) {
        this.semantic_place = semantic_place;
    }

    /**
     * @return the count of people associated with this result
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count of people to set for this result
     */
    public void setCount(int count) {
        this.count = count;
    }
}