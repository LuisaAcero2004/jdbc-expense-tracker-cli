package org.expenses.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String PROP_FILE_DATABASE = "src/main/resources/database.properties";

    private static Connection connection;

    private DatabaseConnection(){}


    public static Connection getConnection() throws SQLException {
        String url = ReadProperties.getProperty(PROP_FILE_DATABASE,"url");
        String user = ReadProperties.getProperty(PROP_FILE_DATABASE,"user");
        String password = ReadProperties.getProperty(PROP_FILE_DATABASE,"password");

        if(connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(url,user,password);
        }
        return connection;
    }

}
