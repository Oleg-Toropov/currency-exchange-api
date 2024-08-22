package com.currencyexchange.dao;

import com.currencyexchange.config.DBCPDataSource;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateDAOImpl.class);
    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();

    @Override
    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT * FROM ExchangeRates";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                exchangeRates.add(mapResultSetToExchangeRate(resultSet));
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rates", e);
            throw new DatabaseUnavailableException(e);
        }

        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByCurrencyPairId(int baseCurrencyId, int targetCurrencyId) {
        String query = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToExchangeRate(resultSet));
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rate by currency code pair", e);
            throw new DatabaseUnavailableException(e);
        }

        return Optional.empty();
    }

    private ExchangeRate mapResultSetToExchangeRate(ResultSet resultSet) {
        try {
            return new ExchangeRate(resultSet.getInt("ID"), resultSet.getInt("BaseCurrencyId"),
                    resultSet.getInt("TargetCurrencyId"), resultSet.getBigDecimal("Rate"));
        } catch (SQLException e) {
            throw new DatabaseUnavailableException(e);
        }
    }

    @Override
    public ExchangeRate addExchangeRate(ExchangeRate exchangeRate) {
        String query = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            exchangeRate.setId(resultSet.getInt(1));

            return exchangeRate;

        } catch (SQLException e) {
            logger.error("Error adding exchange rate", e);
            throw new DatabaseUnavailableException(e);
        }
    }

    @Override
    public void updateExchangeRate(ExchangeRate exchangeRate) {
        String query = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ? WHERE ID = ?";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.setInt(4, exchangeRate.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating exchange rate", e);
            throw new DatabaseUnavailableException(e);
        }
    }

    @Override
    public void deleteExchangeRate(int id) {
        String query = "DELETE FROM ExchangeRates WHERE ID = ?";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting exchange rate", e);
            throw new DatabaseUnavailableException(e);
        }
    }
}
