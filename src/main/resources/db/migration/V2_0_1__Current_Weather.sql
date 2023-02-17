DROP TABLE IF EXISTS current_weather;
CREATE TABLE IF NOT EXISTS current_weather
(id BIGINT NOT NULL PRIMARY KEY auto_increment ,
city VARCHAR(25),
country VARCHAR(25),
date_time VARCHAR(25),
temp INT,
temp_min INT,
temp_max INT,
feels_like INT,
description VARCHAR(30),
pressure INT,
humidity INT,
wind INT,
clouds INT,

CONSTRAINT fr_key FOREIGN KEY (city) REFERENCES locations(city)
ON DELETE CASCADE ON UPDATE CASCADE);
#
# INSERT INTO current_weather (city, country, date_time)
# VALUES ("Ankara", "Turkey", "2023-02-15 16:26");