package com.oizovita.database;

import com.oizovita.ecxeption.BuilderException;

import java.sql.*;
import java.util.*;

public class DB {

    private static final String WHERE = "where";
    private static final String ORDER_BY = "orderBy";
    private static final String SELECT = "select";
    private static final String LIMIT = "limit";
    private static final String LEFT_JOIN = "leftJoin";

    private static DB instance;

    private static Connection con;
    private List<String> bindValues;
    private HashMap<String, List<String>> query;

    private DB() {}

    public static DB connection(String driver, String host, String port, String database, String username, String password) throws SQLException {
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

    public void reset() {
        this.query = new HashMap<>();
        this.bindValues = new ArrayList<>();
    }

    public DB select(String table, String[] fields) {
        this.reset();
        this.query.put("base", Collections.singletonList("SELECT " + String.join(", ", fields) + " FROM " + table));
        this.query.put("type", Collections.singletonList(SELECT));
        return this;
    }

    public DB where(String field, String value, String operator) throws SQLException {
        if (!this.query.get("type").contains(SELECT)) {
            throw new BuilderException("WHERE can only be added to SELECT");
        }

        var s = String.format("%s %s ?", field, operator);

        this.bindValues.add(value);

        if (!this.query.containsKey(WHERE)) {
            this.query.put(WHERE, new ArrayList<>(Arrays.asList(s)));
            return this;
        }

        this.query.get(WHERE).add(s);

        return this;
    }

    public DB limit(Integer limit) throws SQLException {
        if (!this.query.get("type").contains(SELECT)) {
            throw new BuilderException("LIMIT can only be added to SELECT");
        }

        this.query.put(LIMIT, new ArrayList<>(Collections.singletonList(String.format("LIMIT %d", limit))));

        return this;
    }

    public DB orderBy(String field, String sort) throws SQLException {
        if (!this.query.get("type").contains(SELECT)) {
            throw new BuilderException("ORDER BY can only be added to SELECT");
        }

        this.query.put(ORDER_BY, new ArrayList<>(Collections.singletonList(String.format("ORDER BY %s %s", field, sort))));

        return this;
    }

    public DB leftJoin(String table, String first, String operator, String second) throws SQLException {
        if (!this.query.get("type").contains(SELECT)) {
            throw new BuilderException("LEFT JOIN can only be added to SELECT");
        }


        var s = String.format("%s on %s %s %s", table, first, operator, second);

        if (!this.query.containsKey(LEFT_JOIN)) {
            this.query.put(LEFT_JOIN, new ArrayList<>(Collections.singletonList(s)));
            return this;
        }

        this.query.get(LEFT_JOIN).add(s);

        return this;
    }

    public String toSql() {
        var sql = new StringBuilder(this.query.get("base").get(0));

        if (this.query.containsKey(LEFT_JOIN)) {
            sql.append(String.format(" LEFT JOIN %s", String.join(" LEFT JOIN ", this.query.get(LEFT_JOIN))));
        }

        if (this.query.containsKey(WHERE)) {
            sql.append(String.format(" WHERE %s", String.join(" AND ", this.query.get(WHERE))));
        }

        if (this.query.containsKey(ORDER_BY)) {
            sql.append(String.format(" %s", String.join("", this.query.get(ORDER_BY))));
        }

        if (this.query.containsKey(LIMIT)) {
            sql.append(String.format(" %s", String.join("", this.query.get(LIMIT))));
        }

        return sql.toString();
    }


    public ResultSet get() throws SQLException {
        var stmt = con.prepareStatement(this.toSql());

        for (var i = 0; i < this.bindValues.size(); i++) {
            stmt.setString(i + 1, this.bindValues.get(i));
        }


        return stmt.executeQuery();
    }

    public List<Integer> getIds(String table) throws SQLException {
        var resultSet = con.createStatement().executeQuery("SELECT * FROM " + table);
        List<Integer> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getInt("id"));
        }

        return list;
    }
}
