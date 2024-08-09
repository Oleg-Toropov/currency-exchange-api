package com.currencyexchange.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static void initializeDatabase() {
        try (Connection connection = DBCPDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ClassLoader classLoader = DatabaseInitializer.class.getClassLoader();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("create_tables.sql"))))) {
                String sqlQuery = reader.lines().collect(Collectors.joining("\n"));
                String[] queries = sqlQuery.split(";");
                for (String query : queries) {
                    if (!query.trim().isEmpty()) {
                        logger.info("Executing query: {}", query.trim());
                        statement.execute(query.trim());
                    }
                }
            } catch (NullPointerException e) {
                logger.error("SQL file not found: create_tables.sql");
            }
        } catch (SQLException | IOException e) {
            logger.error("Error initializing database", e);
        }
    }
}