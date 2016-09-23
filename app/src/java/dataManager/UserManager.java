package dataManager;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Retrieves user information from database
 * 
 */

public class UserManager {
    
    /**
     * Retrieves the user of that particular username
     * 
     * @param username of the user
     * 
     * @return a user if exists, else null
     */
    public static User retrieveValidUser(String username){
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        
        try{
            conn = ConnectionManager.getConnection();
            // Select the mac_address from the db which matches after sieving out the email address portion
            psmt = conn.prepareStatement("select * from demographics where SUBSTRING_INDEX(email, '@',1) = ?;");
            psmt.setString(1, username);
            rs = psmt.executeQuery();//executeQuery to take something from database , executeUpdate to give
            
            while(rs.next()){
                String macAddress = rs.getString("mac_address");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String email = rs.getString("email");
                char gender = rs.getString("gender").charAt(0);
                return new User(macAddress,name,password,email,gender);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, psmt, rs);
        }
        return null;
    }
}