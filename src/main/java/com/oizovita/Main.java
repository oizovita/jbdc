package com.oizovita;

import com.oizovita.database.CreateTable;
import com.oizovita.database.DB;
import com.oizovita.database.InsertData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        var config = Config.init();
        DB db;

        try {
            db = DB.connection(config.dbDriver(), config.dbHost(), config.dbPort(), config.dbName(), config.dbUser(), config.dbPassword());
        } catch (SQLException exception) {
            logger.error(exception.getMessage());
            return;
        }

        if (args.length > 0) {
            if (args[0].equals("init_database")) {
                var ct = new CreateTable(db.getConnection());
                ct.run();
            }

            if (args[0].equals("generate")) {
                var ct = new InsertData(db.getConnection());
                ct.insertDataForProducts();
            }
        }
    }
}
