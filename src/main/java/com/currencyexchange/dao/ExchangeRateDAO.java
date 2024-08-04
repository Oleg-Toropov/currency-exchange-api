package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.DBCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateDAO.class);

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

    public ExchangeRate getExchangeRateByCurrencyCode(String currencyCodePair) {
        String baseCurrencyCode = currencyCodePair.substring(0, 3);
        String targetCurrencyCode = currencyCodePair.substring(3, 6);

        String query = "SELECT er.ID, er.BaseCurrencyId, er.TargetCurrencyId, er.Rate " +
                "FROM ExchangeRates er " +
                "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID " +
                "WHERE bc.Code = ? AND tc.Code = ?";

        ExchangeRate exchangeRate = null;

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
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

    public ExchangeRate addExchangeRate(ExchangeRate exchangeRate) throws SQLException {
        String query = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        exchangeRate.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error adding exchange rate", e);
            throw e;
        }
        return exchangeRate;
    }

    public ExchangeRate updateExchangeRate(ExchangeRate exchangeRate) throws SQLException {
        String query = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ? WHERE ID = ?";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.setInt(4, exchangeRate.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating exchange rate failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error updating exchange rate", e);
            throw e;
        }
        return exchangeRate;
    }
}
