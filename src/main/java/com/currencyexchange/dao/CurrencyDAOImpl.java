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
import java.util.Optional;

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
                currencies.add(mapResultSetToCurrency(resultSet));
            }

        } catch (SQLException e) {
            logger.error("Error fetching currencies", e);
            throw new DatabaseUnavailableException(e);
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
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currency = mapResultSetToCurrency(resultSet);
            }

        } catch (SQLException e) {
            logger.error("Error fetching currency by id", e);
            throw new DatabaseUnavailableException(e);
        }

      return currency;
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToCurrency(resultSet));
            }

        } catch (SQLException e) {
            logger.error("Error fetching currency by code", e);
            throw new DatabaseUnavailableException(e);
        }

        return Optional.empty();
    }

    private Currency mapResultSetToCurrency(ResultSet resultSet) {
        try {
            return new Currency(resultSet.getInt("ID"), resultSet.getString("Code"),
                    resultSet.getString("FullName"), resultSet.getString("Sign"));
        } catch (SQLException e) {
            throw new DatabaseUnavailableException(e);
        }
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
    public void deleteCurrency(String code) {
        String query = "DELETE FROM Currencies WHERE Code = ?";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting currency", e);
            throw new DatabaseUnavailableException(e);
        }
    }
}
