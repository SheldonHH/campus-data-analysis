package model;

/**
 * Stores the attributes of Heatmap Result
 * 
 */

public class HeatmapResult implements Comparable<HeatmapResult>{

    private String semanticPlace;
    private int numPeople;
    private int density;
    /**
     * Creates a heatmap object with the specified semantic place and population count
     * 
     * @param semanticPlace description of this place
     * @param numPeople count of people in this place
     */
    public HeatmapResult(String semanticPlace, int numPeople) {
        this.semanticPlace = semanticPlace;
        this.numPeople = numPeople;
        if (numPeople == 0) {
            density = 0;
        } else if (numPeople < 3) {
            density = 1;
        } else if (numPeople < 6) {
            density = 2;
        } else if (numPeople < 11) {
            density = 3;
        } else if (numPeople < 21) {
            density = 4;
        } else if (numPeople < 31) {
            density = 5;
        } else {
            density = 6;
        }
    }
    /**
     * Retrieves the semantic place
     * 
     * @return the description of the semantic place
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }
    /**
     * Retrieves the population count
     * 
     * @return the number of people located in this semantic place
     */
    public int getNumPeople() {
        return numPeople;
    }
    /**
     * Retrieves the density based on the population
     * 
     * @return the density corresponding to the number of people located 
     */
    public int getDensity() {
        return density;
    }
    
    /**
     * compareTo for use in sorting HeatmapResult
     * 
     * @param heatmapResult
     * @return 
     */
    public int compareTo(HeatmapResult heatmapResult){
        return semanticPlace.compareTo(heatmapResult.getSemanticPlace());
    }
}