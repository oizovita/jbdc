package com.oizovita.database.seeds;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class ShopProductSeed implements Runnable{

    private final Connection connection;
    private final Random rand;

    private static final Logger logger = LogManager.getLogger(ShopProductSeed.class);
    private final List<Integer> shopIds;
    private Integer offset;
    private final Integer limit;

    public ShopProductSeed(Connection connection, List<Integer> shopIds, Integer limit, Integer offset) throws NoSuchAlgorithmException {
        this.connection = connection;
        this.rand = SecureRandom.getInstanceStrong();
        this.shopIds = shopIds;
        this.offset = offset;
        this.limit = limit;
    }

    public void run() {
        System.out.println("OFFSET - " + this.offset);

        try (var stmt = this.connection.createStatement()) {
            this.connection.setAutoCommit(false);

            ResultSet r;
            var queryProductShop = "INSERT INTO product_shop (product_id, shop_id, amount) VALUE ";

            r = stmt.executeQuery("select * from products limit " + this.limit + " OFFSET " + this.offset);

            var queryProductShopTmp = new StringBuilder(queryProductShop);

            while (r.next()) {
                queryProductShopTmp.append(String.format(" (\"%d\", \"%d\", \"%d\");", r.getInt(1), shopIds.get(this.rand.nextInt(shopIds.size())), rand.nextInt(200)));
                stmt.addBatch(queryProductShopTmp.toString());
                queryProductShopTmp = new StringBuilder(queryProductShop);
            }

            stmt.executeBatch();
            this.connection.commit();
//            logger.info("End insert shop_product");
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