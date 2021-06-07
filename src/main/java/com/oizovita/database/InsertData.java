package com.oizovita.database;

import com.github.javafaker.Faker;

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
    private Random rand;

    public InsertData(Connection connection) throws NoSuchAlgorithmException {
        this.connection = connection;
        this.faker = new Faker();
        this.rand = SecureRandom.getInstanceStrong();
    }

    public void insertDataForProducts() throws SQLException {
        var stmt = this.connection.createStatement();

        var queryCity = "INSERT INTO products (category_id, name, brand, price) VALUES ";
        var tmpQuery = queryCity;

        var resultSet = stmt.executeQuery("SELECT * FROM categories;");
        List<Integer> categoryIds = new ArrayList<>();
        while (resultSet.next()) {
            categoryIds.add(resultSet.getInt("id"));
        }

        resultSet = stmt.executeQuery("SELECT * FROM shops;");
        List<Integer> shopIds = new ArrayList<>();
        while (resultSet.next()) {
            shopIds.add(resultSet.getInt("id"));
        }

        for (int i = 0; i < 1000000; i++) {
            tmpQuery += String.format(" (\"%d\", \"%s\", \"%s\", \"%s\"),", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100);

            if (i % 10000 == 0) {
                System.out.println(i);
                tmpQuery += String.format(" (\"%d\", \"%s\", \"%s\", \"%s\");", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100);
                stmt.execute(tmpQuery, Statement.RETURN_GENERATED_KEYS);
                var r = stmt.getGeneratedKeys();

                var queryProductShop = "INSERT INTO product_shop (product_id, shop_id, count) VALUE ";
                var queryProductShopTmp = queryProductShop;
                while (r.next()) {
                    queryProductShopTmp += String.format(" (\"%d\", \"%d\", \"%d\")", r.getInt(1), shopIds.get(this.rand.nextInt(shopIds.size())), rand.nextInt(200));
                    stmt.addBatch(queryProductShopTmp);
                    queryProductShopTmp = queryProductShop;
                }
                stmt.executeBatch();
                tmpQuery = queryCity;
            }
        }
    }

//    public void insertDataForProducts() throws SQLException {
//        var stmt = this.connection.createStatement();
//
//
//        var resultSet = stmt.executeQuery("SELECT * FROM categories;");
//        List<Integer> categoryIds = new ArrayList<>();
//        while (resultSet.next()) {
//            categoryIds.add(resultSet.getInt("id"));
//        }
//
//        resultSet = stmt.executeQuery("SELECT * FROM shops;");
//        List<Integer> shopIds = new ArrayList<>();
//        while (resultSet.next()) {
//            shopIds.add(resultSet.getInt("id"));
//        }
//
//        var queryCity = "INSERT INTO products (category_id, name, brand, price) VALUE ";
//        var tmpQuery = queryCity;
//        for (int i = 0; i < 1000; i++) {
//            tmpQuery += String.format(" (\"%d\", \"%s\", \"%s\", \"%s\")", categoryIds.get(this.rand.nextInt(categoryIds.size())), faker.funnyName().name(), faker.app().name(), this.rand.nextFloat() * 100);
//            stmt.addBatch(tmpQuery);
//            tmpQuery = queryCity;
//            if (i % 100 == 0) {
//                System.out.println(i);
//                 stmt.executeBatch();
//            }
//        }
//    }
}
