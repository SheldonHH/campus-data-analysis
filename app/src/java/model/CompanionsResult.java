package model;

/**
 * Stores the attributes of Companions Result
 * 
 */
public class CompanionsResult {
    private int rank;
    private String email;
    private String macAddress;
    private int timeSpent;
    
    /**
     *
     * @param email
     * @param macAddress
     * @param timeSpent
     */
    public CompanionsResult(String email, String macAddress, int timeSpent) {
        this.email = email;
        this.macAddress = macAddress;
        this.timeSpent = timeSpent;
    }

    /**
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return the mac address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     *
     * @return the time spent
     */
    public int getTimeSpent() {
        return timeSpent;
    }

    /**
     *
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     *
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        this.rank = rank;
    }
}
