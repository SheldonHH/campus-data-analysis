package model;

/**
 * Stores the attributes of Group Next Places Results
 *
 */
public class GroupNextPlacesResults {

    private int rank;
    private String semantic_place;
    private int num_groups;

    /**
     *
     * @param rank
     * @param semantic_place
     * @param num_groups
     */
    public GroupNextPlacesResults(int rank, String semantic_place, int num_groups) {
        this.rank = rank;
        this.semantic_place = semantic_place;
        this.num_groups = num_groups;
    }

    /**
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return semantic place
     */
    public String getSemantic_place() {
        return semantic_place;
    }

    /**
     * @param semantic_place semantic place to set
     */
    public void setSemantic_place(String semantic_place) {
        this.semantic_place = semantic_place;
    }

    /**
     * @return the number of groups
     */
    public int getNum_groups() {
        return num_groups;
    }

    /**
     * @param num_groups number of groups to set
     */
    public void setNum_groups(int num_groups) {
        this.num_groups = num_groups;
    }
}