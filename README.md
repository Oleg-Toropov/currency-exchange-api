
# Project: Currency Exchange API

## Description
This project is a REST API for managing currencies and exchange rates. It provides functionality for handling currencies, calculating currency exchange, and performing CRUD operations on exchange rates. The API is developed using Java Servlets and an SQLite database.

## Limitations and Simplifications
- **Simplified project**: 
  - The project does not account for exchange commissions.
  - There is no web interface for the project.
  - **No frameworks**: The project is implemented using Java Servlets without frameworks (e.g., Spring Boot).

## Technologies Used
- **Java** (collections, OOP)
- **Maven** for dependency management
- **JDBC, SQL** for working with the SQLite database
- **REST API** with support for JSON and HTTP methods (GET, POST, PATCH, DELETE)

## Main Features
- **CRUD operations with currencies:**
  - Get a list of currencies: `GET /currencies`
  - Add a new currency: `POST /currencies`
  - Get a currency by code: `GET /currency/{currencyCode}`
  - Delete a currency: `DELETE /currency/{currencyCode}`

- **CRUD operations with exchange rates:**
  - Get all exchange rates: `GET /exchangeRates`
  - Add a new exchange rate: `POST /exchangeRates`
  - Get a specific exchange rate by currency pair: `GET /exchangeRate/{baseCurrencyCode}{targetCurrencyCode}`
  - Update an exchange rate: `PATCH /exchangeRate/{baseCurrencyCode}{targetCurrencyCode}`
  - Delete an exchange rate: `DELETE /exchangeRate/{rateId}`

- **Currency conversion:**
  - Convert an amount from one currency to another: `GET /exchange?from={currencyCode}&to={currencyCode}&amount={value}`

## Database Structure

### Table **Currencies**
| Column   | Type     | Comment                                                   |
|----------|----------|------------------------------------------------------------|
| ID       | int      | Currency ID, auto-increment, primary key                   |
| Code     | Varchar  | Currency code                                              |
| FullName | Varchar  | Full name of the currency                                  |
| Sign     | Varchar  | Currency symbol                                            |

Example record for the Australian dollar:

| ID  | Code | FullName           | Sign |
|-----|------|--------------------|------|
| 1   | AUD  | Australian dollar   | A$   |

- **Indexes**:
  - Primary key on the `ID` field.
  - Unique index on the `Code` field to ensure currency uniqueness in the table.

### Table **ExchangeRates**
| Column          | Type          | Comment                                                    |
|-----------------|---------------|------------------------------------------------------------|
| ID              | int           | Exchange rate ID, auto-increment, primary key               |
| BaseCurrencyId  | int           | ID of the base currency                                     |
| TargetCurrencyId| int           | ID of the target currency                                   |
| Rate            | Decimal(6)    | Exchange rate from base currency to target currency         |

- **Indexes**:
  - Primary key on the `ID` field.
  - Unique index on the pair `BaseCurrencyId` and `TargetCurrencyId` to ensure the uniqueness of the currency pair.

## Data Validation
Validation classes are implemented to ensure the correctness of the input data. These classes validate data for currencies and exchange rates to prevent incorrect input and ensure data integrity.

## Errors and Response Codes
The project implements custom exceptions for the following errors:
- **200 OK**
- **201 Created**
- **400 Bad Request**
- **404 Not Found**
- **409 Conflict**
- **500 Internal Server Error**

## Scenarios for Obtaining Exchange Rates
Obtaining an exchange rate can occur in one of the following scenarios:
1. **Direct exchange rate**: The ExchangeRates table contains the pair A → B, and the rate is taken directly.
2. **Reverse exchange rate**: The table contains the pair B → A, and the rate is calculated in reverse.
3. **Via a third currency (USD)**: The table contains rates for USD → A and USD → B, and the rate is calculated via this third currency.

## Project Setup
1. Clone the repository.
2. Build the project using Maven: `mvn clean install`.
3. Run the application: `java -jar target/currency-exchange-api.jar`.
4. API testing can be performed using the Postman collection `CurrencyExchanger.postman_collection.json`.
