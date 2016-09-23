package model;

/**
 * Stores mac-address and email for group members
 * 
 */

public class GroupMember {
    private String mac_Address;
    private String email;
    
    /**
     * Creates an instance of GroupMember with the specified mac-address and email
     * 
     * @param mac_Address
     * @param email
     */
    public GroupMember(String mac_Address, String email) {
        this.mac_Address = mac_Address;
        this.email = email;
    }

    /**
     * Retrieves mac-address of this group member
     * 
     * @return mac address
     */
    public String getMac_Address() {
        return mac_Address;
    }

    /**
     * Sets the mac-address for this group member
     * 
     * @param mac_Address mac address to set
     */
    public void setMac_Address(String mac_Address) {
        this.mac_Address = mac_Address;
    }

    /**
     * Retrieves email of this group member
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email for this group member
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}