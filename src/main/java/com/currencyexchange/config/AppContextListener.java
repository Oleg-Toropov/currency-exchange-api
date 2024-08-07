package com.currencyexchange.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logger.info("Initializing database...");
            DatabaseInitializer.initializeDatabase();
            logger.info("Database initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        closeDatabaseConnectionPool();
    }

    private void closeDatabaseConnectionPool() {
        try {
            BasicDataSource ds = DBCPDataSource.getDataSource();
            if (ds != null) {
                ds.close();
                logger.info("Database connection pool closed.");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection pool", e);
        }
    }
}
