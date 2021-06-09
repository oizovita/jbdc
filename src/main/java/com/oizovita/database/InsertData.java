package com.oizovita.database;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InsertData {
    private final Connection connection;
    private final Faker faker;
    private final Random rand;

    private static final Logger logger = LogManager.getLogger(InsertData.class);

    public InsertData(Connection connection) throws NoSuchAlgorithmException {
        this.connection = connection;
        this.faker = new Faker();
        this.rand = SecureRandom.getInstanceStrong();
    }

    private  List<Integer> getIds(Statement stmt, String table) throws SQLException {
        var resultSet = stmt.executeQuery("SELECT * FROM " + table);
        List<Integer> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getInt("id"));
        }

        return list;
    }

    public void insertDataForProducts() throws SQLException {
        try (var stmt = this.connection.createStatement()) {
            this.connection.setAutoCommit(false);

            List<Integer> categoryIds = this.getIds(stmt, "categories");
            List<Integer> shopIds = this.getIds(stmt, "shops");

            var queryCity = "INSERT INTO products (category_id, name, brand, price) VALUES ";
            var tmpQuery = new StringBuilder(queryCity);

            for (int i = 0; i < 1000; i++) {
                tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\"),", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));
                if (i % 100 == 0) {
                    tmpQuery.append(String.format(" (\"%d\", \"%s\", \"%s\", \"%s\");", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100));

                    stmt.execute(tmpQuery.toString(), Statement.RETURN_GENERATED_KEYS);
                    var r = stmt.getGeneratedKeys();

                    var queryProductShop = "INSERT INTO product_shop (product_id, shop_id, amount) VALUE ";
                    var queryProductShopTmp = new StringBuilder(queryProductShop);
                    var count = 0;
                    while (r.next()) {
                        System.out.println(count++);
                        queryProductShopTmp.append(String.format(" (\"%d\", \"%d\", \"%d\");", r.getInt(1), shopIds.get(this.rand.nextInt(shopIds.size())), rand.nextInt(200)));
                        stmt.addBatch(queryProductShopTmp.toString());
                        queryProductShopTmp = new StringBuilder(queryProductShop);
                    }

                    tmpQuery = new StringBuilder(queryCity);
                }
            }

            stmt.executeBatch();
            this.connection.commit();

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
