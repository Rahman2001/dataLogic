DROP TABLE IF EXISTS daily_weather;
CREATE TABLE IF NOT EXISTS Daily_Weather
(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
city VARCHAR(25),
country VARCHAR(25),
date_time VARCHAR(25),
description VARCHAR(25),
temp INT,
temp_min INT,
temp_max INT,
pressure INT,
humidity INT,
wind INT,
clouds INT,
CONSTRAINT fk_key2 FOREIGN KEY(city) REFERENCES locations(city));