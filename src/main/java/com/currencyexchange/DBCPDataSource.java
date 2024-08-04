package com.currencyexchange;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBCPDataSource {
    private static final String NAME_PROPERTIES = "config.properties";
    private static final int MIN_IDLE = 5;
    private static final int MAX_IDLE = 10;
    private static final int MAX_OPEN_PREPARED_STATEMENT = 10;
    private static final BasicDataSource ds = new BasicDataSource();
    private static final Log log = LogFactory.getLog(DBCPDataSource.class);

    static {
        ClassLoader loader = DBCPDataSource.class.getClassLoader();

        try {
            Properties config = new Properties();
            InputStream is = loader.getResourceAsStream(NAME_PROPERTIES);
            config.load(is);

            String dbName = config.getProperty("db_name");
            String dbDriver = config.getProperty("db_driver");
            String dbUrl = config.getProperty("db_url");
            String userName = config.getProperty("userName");
            String password = config.getProperty("password");

            URL res = loader.getResource(dbName);
            assert res != null;
            String path = new File(res.toURI()).getAbsolutePath();
            String dbPath = dbUrl + path;

            ds.setDriverClassName(dbDriver);
            ds.setUrl(dbPath);
            ds.setUsername(userName);
            ds.setPassword(password);
            ds.setMinIdle(MIN_IDLE);
            ds.setMaxIdle(MAX_IDLE);
            ds.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENT);

            log.info("DBCP DataSource initialized successfully");

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private DBCPDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        log.info("Getting a connection from the pool");
        return ds.getConnection();
    }
}
