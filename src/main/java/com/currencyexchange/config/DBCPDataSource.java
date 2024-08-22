package com.currencyexchange.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBCPDataSource {
    private static final String NAME_PROPERTIES = "config.properties";
    private static final int MIN_IDLE = 5;
    private static final int MAX_IDLE = 10;
    private static final int MAX_OPEN_PREPARED_STATEMENT = 10;
    private static final BasicDataSource ds = new BasicDataSource();
    private static final Logger logger = LoggerFactory.getLogger(DBCPDataSource.class);

    private DBCPDataSource() {
    }

    static {
        ClassLoader loader = DBCPDataSource.class.getClassLoader();

        try {
            Properties config = new Properties();
            InputStream is = loader.getResourceAsStream(NAME_PROPERTIES);
            config.load(is);

            String dbDriver = config.getProperty("db_driver");
            String dbUrl = config.getProperty("db_url");
            String userName = config.getProperty("userName");
            String password = config.getProperty("password");

            if (dbUrl == null) {
                throw new FileNotFoundException("config.properties file not found in the classpath");
            }

            ds.setDriverClassName(dbDriver);
            ds.setUrl(dbUrl);
            ds.setUsername(userName);
            ds.setPassword(password);
            ds.setMinIdle(MIN_IDLE);
            ds.setMaxIdle(MAX_IDLE);
            ds.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENT);

            logger.info("DBCP DataSource initialized successfully");

        } catch (IOException e) {
            logger.error("Error initializing DBCP DataSource", e);
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        logger.info("Getting a connection from the pool");
        return ds.getConnection();
    }

    public static BasicDataSource getDataSource() {
        return ds;
    }
}
