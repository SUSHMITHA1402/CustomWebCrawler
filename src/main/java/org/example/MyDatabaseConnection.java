package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class MyDatabaseConnection {

    // Initialize fixed connection
    static Connection connection = null;
    public static Connection getConnection(){
        if(connection !=null){
            return connection;
        }
        String db = "searchaccio";
        String user = "root";
        String pwd = "05082021";
        // Provide the mysql credentials to get connection
        return getConnection(db,user,pwd);
    }
    // Overload the getConnection method
    private static Connection getConnection(String db, String user, String pwd){
        try{
            // Check and Extract jdbc driver library for mysql connection to our project
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Set the mysql connection url to set up the connection with mysql server
            connection = DriverManager.getConnection("jdbc:mysql://localhost/"+db+"?user="+user+"&password="+pwd);
        }
        // If driver class not there It throws an error
        catch(Exception exception){
            exception.printStackTrace();
        }
        return connection;
    }
}
