CREATE TABLE `CAP_CAPITAL_ACCOUNT`
(
    `CAPITAL_ACCOUNT_ID` int(11) NOT NULL AUTO_INCREMENT,
    `BALANCE_AMOUNT`     decimal(10, 0) DEFAULT NULL,
    `USER_ID`            int(11) DEFAULT NULL,
    PRIMARY KEY (`CAPITAL_ACCOUNT_ID`)
);

CREATE TABLE `CAP_TRADE_ORDER`
(
    `ID`                int(11) NOT NULL AUTO_INCREMENT,
    `SELF_USER_ID`      bigint(11) DEFAULT NULL,
    `OPPOSITE_USER_ID`  bigint(11) DEFAULT NULL,
    `MERCHANT_ORDER_NO` varchar(45) NOT NULL,
    `AMOUNT`            decimal(10, 0) DEFAULT NULL,
    `STATUS`            varchar(45)    DEFAULT NULL,
    `VERSION`           int(11) DEFAULT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE KEY `UX_MERCHANT_ORDER_NO` (`MERCHANT_ORDER_NO`)
);

INSERT INTO `CAP_CAPITAL_ACCOUNT` (`CAPITAL_ACCOUNT_ID`, `BALANCE_AMOUNT`, `USER_ID`)
VALUES (1, 10000, 1000);

INSERT INTO `CAP_CAPITAL_ACCOUNT` (`CAPITAL_ACCOUNT_ID`, `BALANCE_AMOUNT`, `USER_ID`)
VALUES (2, 10000, 2000);