package com.oizovita.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DB {
    private static DB instance;

    private static Connection con;
    private List bindValues;
    private HashMap<String, String> query;
    private String table;

    private DB() {

    }

    public static DB connection(String driver, String host, String port, String database, String username, String password) throws Exception {
        if (instance == null) {
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
        }


        return instance;
    }

    public Connection getConnection() {
        return con;
    }


    /**
     * Reset query
     */
    protected void reset() {
        this.query = new HashMap<String, String>();
        this.table = "";
        this.bindValues = new ArrayList();
    }

    public DB select(String table, String[] fields) {
        this.reset();
        this.table = table;
        this.query.put("base", "SELECT " + String.join(", ", fields) + " FROM " + table);
        this.query.put("type", "select");

        return this;
    }

    /**
     * @return string
     */
    public String toSQL() {
        String sql = this.query.get("base");

        sql += ";";
        return sql;
    }
}
