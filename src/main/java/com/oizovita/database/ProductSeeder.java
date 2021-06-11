package com.oizovita.database;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ProductSeeder {
    private final Connection connection;
    private final Faker faker;
    private final Random rand;

    private static final Logger logger = LogManager.getLogger(ProductSeeder.class);

    public ProductSeeder(Connection connection) throws NoSuchAlgorithmException {
        this.connection = connection;
        this.faker = new Faker();
        this.rand = SecureRandom.getInstanceStrong();
    }

    private List<Integer> getIds(Statement stmt, String table) throws SQLException {
        var resultSet = stmt.executeQuery("SELECT * FROM " + table);
        List<Integer> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getInt("id"));
        }

        return list;
    }

    public long run() {
        var stopWatch = new StopWatch();
        stopWatch.start();
        this.insertProducts();
        this.insertShopProduct();
        stopWatch.stop();

        return TimeUnit.NANOSECONDS.toSeconds(stopWatch.getTime());
    }

    private void insertProducts() {
        logger.info("Start insert products");
        try (var stmt = this.connection.createStatement()) {
            this.connection.setAutoCommit(false);

            List<Integer> categoryIds = this.getIds(stmt, "categories");

            var queryCity = "INSERT INTO products (category_id, name, brand, price) VALUES ";
            var tmpQuery = new StringBuilder(queryCity);

            for (var i = 0; i < 100; i++) {
                tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\"),", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));
                if (i % 10 == 0) {
                    tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\");", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));
                    stmt.addBatch(tmpQuery.toString());
                    stmt.executeBatch();
                    tmpQuery = new StringBuilder(queryCity);
                }
            }
            stmt.executeBatch();
            this.connection.commit();
            logger.info("End insert products");
        } catch (SQLException ex) {
            logger.error(ex.getMessage());

            try {
                this.connection.rollback();
            } catch (SQLException e) {
                logger.error(ex.getMessage());
            }
        }
    }

    private void insertShopProduct() {
        logger.info("Start insert shop_product");
        try (var stmt = this.connection.createStatement()) {
            this.connection.setAutoCommit(false);
            List<Integer> shopIds = this.getIds(stmt, "shops");
            ResultSet r;
            var offset = 0;
            var queryProductShop = "INSERT INTO product_shop (product_id, shop_id, amount) VALUE ";
            while (true) {
                r = stmt.executeQuery("select * from products limit 100 OFFSET " + offset);

                var queryProductShopTmp = new StringBuilder(queryProductShop);

                if (!r.next()) {
                    break;
                }

                while (r.next()) {
                    queryProductShopTmp.append(String.format(" (\"%d\", \"%d\", \"%d\");", r.getInt(1), shopIds.get(this.rand.nextInt(shopIds.size())), rand.nextInt(200)));
                    stmt.addBatch(queryProductShopTmp.toString());
                    queryProductShopTmp = new StringBuilder(queryProductShop);
                }
                offset += 100;
                stmt.executeBatch();
            }
            this.connection.commit();
            logger.info("End insert shop_product");
        } catch (SQLException ex) {
            logger.error(ex.getMessage());

            try {
                this.connection.rollback();
            } catch (SQLException e) {
                logger.error(ex.getMessage());
            }
        }
    }


}
