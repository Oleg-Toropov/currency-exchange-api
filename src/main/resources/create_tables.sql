CREATE TABLE IF NOT EXISTS Currencies
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Code     TEXT NOT NULL UNIQUE,
    FullName TEXT NOT NULL,
    Sign     TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS ExchangeRates
(
    ID               INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId   INTEGER        NOT NULL,
    TargetCurrencyId INTEGER        NOT NULL,
    Rate             DECIMAL(10, 6) NOT NULL,
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies (ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies (ID)
);
