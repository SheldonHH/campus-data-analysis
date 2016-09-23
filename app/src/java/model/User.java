package model;

/**
 * Stores the information of users
 * 
 */

public class User {
    private String macAddress;
    private String name;
    private String password;  
    private String email;
    private char gender;
    /**
     * Creates a user object with the specified fields
     * 
     * @param macAddress mac address corresponding to this user
     * @param name name of this user
     * @param password password of this user
     * @param email email of this user
     * @param gender gender of this user
     */
    public User(String macAddress,String name,String password,String email,char gender){
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }
    /**
     * Creates a user object with the specified fields
     * 
     * @param password of this user
     * @param email of this user
     */
    public User(String password,String email){
        this.macAddress = null;
        this.name = "admin";
        this.password = password;
        this.email = email;
        this.gender = 'm';
    }
    /**
     * Retrieves the mac address of this user
     * 
     * @return the mac address of this user
     */
    public String getMacAddress(){
        return macAddress;
    }
    /**
     * Retrieves the name of this user
     * 
     * @return the name of this user
     */
    public String getName(){
        return name;
    }
    /**
     * Retrieves the password of this user
     * 
     * @return the password of this user
     */
    public String getPassword(){
        return password;
    }
    /**
     * Retrieves the email of this user
     * 
     * @return the email of this user
     */
    public String getEmail() {
        return email;
    }    
    /**
     * Retrieves the gender of this user
     * 
     * @return the gender of this user
     */
    public char getGender() {
        return gender;
    }   
}