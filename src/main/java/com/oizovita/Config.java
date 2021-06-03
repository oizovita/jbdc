package com.oizovita;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Config
 */
public class Config {
    private Map<String, String> env;
    private static Config instance;
    private Properties property;

    /**
     * Constructor
     *
     * @throws IOException
     */
    private Config() throws IOException {
        this.env = System.getenv();
        this.property = new Properties();
        InputStream in = ClassLoader.getSystemResourceAsStream("db.properties");
        this.property.load(in);
    }

    /**
     * Initialization config instance
     *
     * @return Config
     * @throws IOException
     */
    public static Config init() throws IOException {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Get db name
     *
     * @return String
     */
    public String dbName() {
        return this.property.getProperty("DB_DATABASE");
    }

    /**
     * Get db user
     *
     * @return String
     */
    public String dbUser() {
        return this.property.getProperty("DB_USERNAME");
    }

    /**
     * Get db password
     *
     * @return String
     */
    public String dbPassword() {
        return this.property.getProperty("DB_PASSWORD");
    }

    /**
     * Get db host
     *
     * @return String
     */
    public String dbHost() {
        return this.property.getProperty("DB_HOST");
    }

    /**
     * Get db port
     *
     * @return String
     */
    public String dbPort() {
        return this.property.getProperty("DB_PORT");
    }

    /**
     * Get db driver
     *
     * @return String
     */
    public String dbDriver() {
        return this.property.getProperty("DB_DRIVER");
    }
}
