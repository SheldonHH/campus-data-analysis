package model;

/**
 * Stores the attributes of Popular Places Result
 * 
 */

public class PopularPlacesResult implements Comparable<PopularPlacesResult> {

    private int rank;
    private String semantic_place;
    private int count;

    /**
     * Creates an instance of PopularPlacesResult with the specified semantic
     * place and number of people
     *
     * @param semantic_place description of this location
     * @param count count of the people located here
     */
    public PopularPlacesResult(String semantic_place, int count) {
        this.semantic_place = semantic_place;
        this.count = count;
    }

    /**
     * Retrieves the semantic place
     *
     * @return the description of the semantic place
     */
    public String getSemantic_place() {
        return semantic_place;
    }

    /**
     * Retrieves the number of people at this location
     *
     * @return the number of people located in this place
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count of people at the location to number passed in
     * 
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Retrieves the ranking of this PopularPlacesResult
     *
     * @return the ranking allocated to this place
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets the rank to the value passed in
     *
     * @param rank assigns the value to the current rank of the query
     */
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public int compareTo(PopularPlacesResult popularPlacesResult){
        if(popularPlacesResult.getCount() - count == 0){
            return semantic_place.compareTo(popularPlacesResult.getSemantic_place());
        }
        return popularPlacesResult.getCount() - count;
    }
}