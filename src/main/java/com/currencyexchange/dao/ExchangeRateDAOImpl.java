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

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToExchangeRate(resultSet));
                }
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
            logger.error("Error mapping exchange rate", e);
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
        String query = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setInt(2, exchangeRate.getId());
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

    @Override
    public List<ExchangeRate> getExchangeRatesByGeneralBaseCurrency(int baseCurrencyId, int targetCurrencyId) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT " +
                "e1.TargetCurrencyId AS CurrencyId1, " +
                "e1.Rate AS Rate1, " +
                "e2.TargetCurrencyId AS CurrencyId2, " +
                "e2.Rate AS Rate2, " +
                "e1.BaseCurrencyId AS GeneralCurrencyId " +
                "FROM ExchangeRates AS e1 " +
                "JOIN ExchangeRates AS e2 " +
                "ON (e1.BaseCurrencyId = e2.BaseCurrencyId) " +
                "WHERE e1.TargetCurrencyId = ? " +
                "AND e2.TargetCurrencyId = ? " +
                "LIMIT 1";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRates = mapResultSetToExchangeRates(resultSet,
                            resultSet.getInt("GeneralCurrencyId"), resultSet.getInt("CurrencyId1"),
                            resultSet.getInt("GeneralCurrencyId"), resultSet.getInt("CurrencyId2"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rates by general base currency", e);
            throw new DatabaseUnavailableException(e);
        }

        return exchangeRates;
    }

    @Override
    public List<ExchangeRate> getExchangeRatesByGeneralTargetCurrency(int baseCurrencyId, int targetCurrencyId) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT " +
                "e1.BaseCurrencyId AS CurrencyId1, " +
                "e1.Rate AS Rate1, " +
                "e2.BaseCurrencyId AS CurrencyId2, " +
                "e2.Rate AS Rate2, " +
                "e1.TargetCurrencyId AS GeneralCurrencyId " +
                "FROM ExchangeRates AS e1 " +
                "JOIN ExchangeRates AS e2 " +
                "ON (e1.TargetCurrencyId = e2.TargetCurrencyId) " +
                "WHERE e1.BaseCurrencyId = ? " +
                "AND e2.BaseCurrencyId = ? " +
                "LIMIT 1";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRates = mapResultSetToExchangeRates(resultSet,
                            resultSet.getInt("CurrencyId1"), resultSet.getInt("GeneralCurrencyId"),
                            resultSet.getInt("CurrencyId2"), resultSet.getInt("GeneralCurrencyId"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rates by general base currency", e);
            throw new DatabaseUnavailableException(e);
        }

        return exchangeRates;
    }

    @Override
    public List<ExchangeRate> getExchangeRatesByGeneralBaseTargetCurrency(int baseCurrencyId, int targetCurrencyId) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT " +
                "e1.BaseCurrencyId AS CurrencyId1, " +
                "e1.Rate AS Rate1, " +
                "e2.TargetCurrencyId AS CurrencyId2, " +
                "e2.Rate AS Rate2, " +
                "e1.TargetCurrencyId AS GeneralCurrencyId " +
                "FROM ExchangeRates AS e1 " +
                "JOIN ExchangeRates AS e2 " +
                "ON (e1.TargetCurrencyId = e2.BaseCurrencyId) " +
                "WHERE e1.BaseCurrencyId = ? " +
                "AND e2.TargetCurrencyId = ? " +
                "LIMIT 1";

        try (Connection connection = DBCPDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRates = mapResultSetToExchangeRates(resultSet,
                            resultSet.getInt("CurrencyId1"), resultSet.getInt("GeneralCurrencyId"),
                            resultSet.getInt("GeneralCurrencyId"), resultSet.getInt("CurrencyId2"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching exchange rates by general base currency", e);
            throw new DatabaseUnavailableException(e);
        }

        return exchangeRates;
    }

    private List<ExchangeRate> mapResultSetToExchangeRates(ResultSet resultSet, int baseId1, int targetId1,
                                                           int baseId2, int targetId2) {
        try {
            ExchangeRate exchangeRate1 =
                    new ExchangeRate(1, baseId1, targetId1, resultSet.getBigDecimal("Rate1"));

            ExchangeRate exchangeRate2 =
                    new ExchangeRate(2, baseId2, targetId2, resultSet.getBigDecimal("Rate2"));

            return List.of(exchangeRate1, exchangeRate2);

        } catch (SQLException e) {
            logger.error("Error mapping exchange rate", e);
            throw new DatabaseUnavailableException(e);
        }
    }
}
