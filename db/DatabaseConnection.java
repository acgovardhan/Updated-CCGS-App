package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL =  "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/ccgs?useSSL=true&verifyServerCertificate=false"; 
    private static final String USERNAME = "***********";
    private static final String PASSWORD = "***********";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("Database connection established successfully!");
        return conn;
    }
}
