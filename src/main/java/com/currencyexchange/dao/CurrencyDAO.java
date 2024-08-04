package com.currencyexchange.dao;

import com.currencyexchange.model.Currency;
import com.currencyexchange.DBCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyDAO.class);

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Currency currency = new Currency();
                currency.setId(resultSet.getInt("ID"));
                currency.setCode(resultSet.getString("Code"));
                currency.setFullName(resultSet.getString("FullName"));
                currency.setSign(resultSet.getString("Sign"));
                currencies.add(currency);
            }

        } catch (SQLException e) {
            logger.error("Error fetching currencies", e);
        }
        return currencies;
    }

    public Currency getCurrencyByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        Currency currency = null;

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currency = new Currency();
                    currency.setId(resultSet.getInt("ID"));
                    currency.setCode(resultSet.getString("Code"));
                    currency.setFullName(resultSet.getString("FullName"));
                    currency.setSign(resultSet.getString("Sign"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching currency by code", e);
        }
        return currency;
    }

    public Currency addCurrency(Currency currency) throws SQLException {
        String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        currency.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error adding currency", e);
            throw e;
        }
        return currency;
    }

    public Currency updateCurrency(Currency currency) throws SQLException {
        String query = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ?";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setInt(4, currency.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating currency failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error updating currency", e);
            throw e;
        }
        return currency;
    }
}
