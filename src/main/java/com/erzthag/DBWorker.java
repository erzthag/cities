package com.erzthag;

import java.sql.*;

public class DBWorker {
    private static final String URL = "jdbc:mysql://localhost:3306/mysql";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    public Connection getConnection() {

            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                if (!connection.isClosed()) {
                    System.out.println("Соединение установлено");
                }

            }catch (SQLException ex) {
                System.err.println("Соединение не установлено");
                ex.printStackTrace();
            }
        return connection;
    }

    DBWorker(){
        try{
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
