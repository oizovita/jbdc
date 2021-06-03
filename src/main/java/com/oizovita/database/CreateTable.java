package com.oizovita.database;

import org.apache.ibatis.jdbc.ScriptRunner;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;

public class CreateTable {

    private static final String sqlFile  = "epicentr.sql";

    Connection connection;

    public CreateTable(Connection c) {
        this.connection = c;
    }

    public void run() {
        ScriptRunner sr = new ScriptRunner(this.connection);
        try {
            URL resource = getClass().getClassLoader().getResource(sqlFile);
            Reader reader = new BufferedReader(new FileReader(resource.getPath()));

            sr.runScript(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
