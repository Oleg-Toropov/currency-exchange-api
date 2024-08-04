CREATE TABLE IF NOT EXISTS Currencies
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Code     VARCHAR(3)   NOT NULL UNIQUE,
    FullName VARCHAR(100) NOT NULL,
    Sign     VARCHAR(3)   NOT NULL
);


CREATE TABLE IF NOT EXISTS ExchangeRates
(
    ID               INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId   INTEGER       NOT NULL,
    TargetCurrencyId INTEGER       NOT NULL,
    Rate             DECIMAL(6, 4) NOT NULL,
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies (ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies (ID),
    UNIQUE (BaseCurrencyId, TargetCurrencyId)
);
