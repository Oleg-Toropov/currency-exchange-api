package com.currencyexchange.dao;

import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.config.DBCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyDAOImpl.class);

    @Override
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

    @Override
    public Currency getCurrencyById(int id) {
        String query = "SELECT * FROM Currencies WHERE id = ?";
        Currency currency = null;

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                currency = mapResultSetToCurrency(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Error fetching currency by id", e);
        }
        return currency;
    }

    @Override
    public Currency getCurrencyByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        Currency currency = null;

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                currency = mapResultSetToCurrency(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Error fetching currency by code", e);
        }
        return currency;
    }

    private Currency mapResultSetToCurrency(ResultSet resultSet) throws SQLException {
        Currency currency = null;
        if (resultSet.next()) {
            currency = new Currency();
            currency.setId(resultSet.getInt("ID"));
            currency.setCode(resultSet.getString("Code"));
            currency.setFullName(resultSet.getString("FullName"));
            currency.setSign(resultSet.getString("Sign"));
        }
        return currency;
    }

    @Override
    public Currency addCurrency(Currency currency) {
        String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            currency.setId(resultSet.getInt(1));

            return currency;

        } catch (SQLException e) {
            logger.error("Error adding currency", e);
            throw new DatabaseUnavailableException(e);
        }
    }

    @Override
    public void deleteCurrency(int id) {
        String query = "DELETE FROM Currencies WHERE ID = ?";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting currency", e);
        }
    }
}
