package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.config.DBCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("ID"));
                exchangeRate.setBaseCurrencyId(resultSet.getInt("BaseCurrencyId"));
                exchangeRate.setTargetCurrencyId(resultSet.getInt("TargetCurrencyId"));
                exchangeRate.setRate(resultSet.getBigDecimal("Rate"));
                exchangeRates.add(exchangeRate);
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rates", e);
        }
        return exchangeRates;
    }

    @Override
    public ExchangeRate getExchangeRateByCurrencyCode(String currencyCodePair) {
        String baseCurrencyCode = currencyCodePair.substring(0, 3);
        String targetCurrencyCode = currencyCodePair.substring(3, 6);

        int baseCurrencyId = currencyDAO.getCurrencyByCode(baseCurrencyCode).getId();
        int targetCurrencyId = currencyDAO.getCurrencyByCode(targetCurrencyCode).getId();

        String query = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";

        ExchangeRate exchangeRate = null;

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRate = new ExchangeRate();
                    exchangeRate.setId(resultSet.getInt("ID"));
                    exchangeRate.setBaseCurrencyId(resultSet.getInt("BaseCurrencyId"));
                    exchangeRate.setTargetCurrencyId(resultSet.getInt("TargetCurrencyId"));
                    exchangeRate.setRate(resultSet.getBigDecimal("Rate"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rate by currency code pair", e);
        }
        return exchangeRate;
    }

    @Override
    public void addExchangeRate(ExchangeRate exchangeRate) {
        String query = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error adding exchange rate", e);
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
        }
    }
}
