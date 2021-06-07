package com.oizovita;

import com.oizovita.database.CreateTable;
import com.oizovita.database.DB;
import com.oizovita.database.InsertData;


public class Main {
    public static void main(String[] args) throws Exception {
        var config = Config.init();

        var db = DB.connection(config.dbDriver(), config.dbHost(), config.dbPort(), config.dbName(), config.dbUser(), config.dbPassword());

        if (args.length > 0) {
            if(args[0].equals("create_table")) {
                var ct = new CreateTable(db.getConnection());
                ct.run();
            }
        }
    }
}
