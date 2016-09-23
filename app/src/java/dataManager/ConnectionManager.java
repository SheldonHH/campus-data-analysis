package dataManager;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Manages connections to database and QpenShift
 *
 */
public class ConnectionManager {

    /**
     * Closes the connection, statement and resultset objects
     *
     * @param conn the connection object to be closed
     * @param stmt the statement object to be closed
     * @param rs the resultset object to be closed
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String JDBC_DRIVER = "jdbc.driver";
    private static String JDBC_URL = "jdbc.url";
    private static String JDBC_USER = "jdbc.user";
    private static String JDBC_PASSWORD = "jdbc.password";
    private static Properties props = new Properties();

    private static String dbUser;
    private static String dbPassword;
    private static String dbURL;

    static {

        // grab environment variable
        String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        if (host != null) {
      // this is production environment
            // obtain database connection properties from environment variables
            String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
            String dbName = System.getenv("OPENSHIFT_APP_NAME");
            dbUser = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
            dbPassword = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

            dbURL = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

        } else {
      // this is development environment
            // obtain database connection properties from properties file

            try {
            // a way to retrieve the data in
                // connection.properties found
                // in WEB-INF/classes
                InputStream is = ConnectionManager.class.getResourceAsStream("/connection.properties");
                props.load(is);

                dbURL = props.getProperty(JDBC_URL);
                dbUser = props.getProperty(JDBC_USER);
                dbPassword = props.getProperty(JDBC_PASSWORD);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets a connection to the database and OpenShift
     *
     * @return the connection
     * @throws SQLException if an error occurs when connecting
     */
    public static Connection getConnection() throws SQLException {

        Connection con = null;
        try {
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return con;
    }
}