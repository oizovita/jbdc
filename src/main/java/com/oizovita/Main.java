package com.oizovita;

import com.oizovita.database.CreateTable;
import com.oizovita.database.DB;
import com.oizovita.database.ProductSeeder;
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
                var ct = new ProductSeeder(db.getConnection());
                logger.info("Time - {} minutes", ct.run());
            }
        }

        var category = "'sport'";

        try (var stmt = db.getConnection().createStatement()) {
            var r = stmt.executeQuery("select address, amount\n" +
                    "from shops\n" +
                    "         left join product_shop ps on shops.id = ps.shop_id\n" +
                    "         left join products p on p.id = ps.product_id\n" +
                    "         left join categories c on c.id = p.category_id\n" +
                    "where c.name = " + category + " order by amount desc limit 1;");

            if (r.next()) {
                logger.info("{} - amount {} ", r.getString(1), r.getString(2));
            }

        }
    }
}
