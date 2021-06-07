package com.oizovita.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {
    private static DB instance;

    private static Connection con;

    private DB() {

    }

    public static DB connection(String driver, String host, String port, String database, String username, String password) throws Exception {
        if (instance == null) {
            try {
                con = DriverManager.getConnection(
                        String.format(
                                "jdbc:%s://%s:%s/%s?currentSchema=public&user=%s&password=%s&useSSL=true",
                                driver,
                                host,
                                port,
                                database,
                                username,
                                password
                        )
                );

                instance = new DB();
            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            }
        }

        return instance;
    }

    public Connection getConnection() {
        return con;
    }

    public ResultSet query(String query) throws SQLException {
        return con.createStatement().executeQuery(query);
    }
}
