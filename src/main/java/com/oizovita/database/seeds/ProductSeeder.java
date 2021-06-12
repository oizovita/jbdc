package com.oizovita.database.seeds;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class ProductSeeder implements Runnable{
    private final Connection connection;
    private final Faker faker;
    private final Random rand;

    private static final Logger logger = LogManager.getLogger(ProductSeeder.class);
    private final List<Integer> categoryIds;

    public ProductSeeder(Connection connection,  List<Integer> categoryIds) throws NoSuchAlgorithmException {
        this.connection = connection;
        this.faker = new Faker();
        this.rand = SecureRandom.getInstanceStrong();
        this.categoryIds = categoryIds;

    }

    public void run() {
        logger.info("Start insert products");
        try (var stmt = this.connection.createStatement()) {

            var queryCity = "INSERT INTO products (category_id, name, brand, price) VALUES ";
            var tmpQuery = new StringBuilder(queryCity);

            for (var i = 0; i < 1000; i++) {
                tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\"),", this.categoryIds.get(this.rand.nextInt(this.categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));
                if (i % 100 == 0) {
                    tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\");", this.categoryIds.get(this.rand.nextInt(this.categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));
                    stmt.addBatch(tmpQuery.toString());
                    stmt.executeBatch();
                    tmpQuery = new StringBuilder(queryCity);
                }
            }
            stmt.executeBatch();

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

//
//    }


}
