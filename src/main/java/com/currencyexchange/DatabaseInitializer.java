package com.currencyexchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection connection = DBCPDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/create_tables.sql"));
            String line;
            StringBuilder sqlQuery = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sqlQuery.append(line);
                if (line.endsWith(";")) {
                    statement.execute(sqlQuery.toString());
                    sqlQuery.setLength(0);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}