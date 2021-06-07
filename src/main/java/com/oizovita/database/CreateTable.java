package com.oizovita.database;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;


public class CreateTable {

    private static final String SQL_FILE = "epicentr.sql";
    private static final Logger logger = LogManager.getLogger(CreateTable.class);

    Connection connection;

    public CreateTable(Connection c) {
        this.connection = c;
    }

    public void run() {
        var sr = new ScriptRunner(this.connection);
        try {
            URL resource = getClass().getClassLoader().getResource(SQL_FILE);
            Reader reader = new BufferedReader(new FileReader(resource.getPath()));

            sr.runScript(reader);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

}
