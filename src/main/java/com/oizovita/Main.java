package com.oizovita;

import com.oizovita.database.CreateTable;
import com.oizovita.database.DB;
import com.oizovita.database.seeds.ProductSeeder;
import com.oizovita.database.seeds.ShopProductSeed;
import com.oizovita.ecxeption.BuilderException;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


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
                var stopWatch = new StopWatch();
                stopWatch.start();
                var p = new ProductSeeder(db.getConnection(), db.getIds("categories"));
                for (var i = 0; i < 1000; i++){
                    var thread1 = new Thread(p);
                    thread1.start();
                }

                ShopProductSeed sp ;
                for (var i = 0; i < 1000; i++){
                    var limit = 1000;
                    sp = new ShopProductSeed(db.getConnection(), db.getIds("shops"), limit, limit * i);
                    var thread1 = new Thread(sp);
                    thread1.start();
                }

                stopWatch.stop();

                System.out.println(TimeUnit.NANOSECONDS.toSeconds(stopWatch.getTime()));

            }
        }

//        var category = "sport";
//
//        try {
//            var r = db
//                    .select("shops", new String[]{"address", "amount"})
//                    .leftJoin("product_shop ps", "shops.id", "=", "ps.shop_id")
//                    .leftJoin("products p", "p.id", "=", "ps.product_id")
//                    .leftJoin("categories c", "c.id", "=", "p.category_id")
//                    .where("c.name", category, "=")
//                    .orderBy("amount", "DESC")
//                    .limit(1)
//                    .get();
//
//            if (r.next()) {
//                logger.info("{} - amount {} ", r.getString(1), r.getString(2));
//            }
//        } catch (BuilderException exception) {
//            logger.error(exception.getMessage());
//        }
    }
}
